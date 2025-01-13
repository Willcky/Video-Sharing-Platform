package org.dromara.video.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.video.config.RedisStreamConfig;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.entity.SysVideoFile;
import org.dromara.video.domain.enums.VideoStatus;
import org.dromara.video.mapper.SysVideoFileMapper;
import org.dromara.video.mapper.SysVideoMapper;
import org.dromara.video.service.IVideoUploadService;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Collections;

import static org.dromara.video.config.RedisStreamConfig.CONSUMER_PREFIX;

/**
 * Video Upload Service Implementation
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoUploadServiceImpl implements IVideoUploadService {

    private final SysVideoMapper videoMapper;
    private final SysVideoFileMapper videoFileMapper;
    private final StringRedisTemplate redisTemplate;
    private ExecutorService executorService;
    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::handleUploadMessage);
        log.info("Started Redis Stream consumer for video uploads");
    }

    @PreDestroy
    public void destroy() {
        running = false;
        if (executorService != null) {
            executorService.shutdown();
        }
        log.info("Stopped Redis Stream consumer for video uploads");
    }

    @Override
    public void handleUploadMessage() {
        String consumerId = CONSUMER_PREFIX + UUID.randomUUID();

        try {
            // Create stream if not exists by adding a dummy message that we'll delete
            try {
                redisTemplate.opsForStream().add(
                    RedisStreamConfig.VIDEO_PROCESS_STREAM,
                    Collections.singletonMap("init", "init")
                );
            } catch (Exception e) {
                log.debug("Stream might already exist: {}", e.getMessage());
            }

            // Create consumer group
            try {
                redisTemplate.opsForStream().createGroup(
                    RedisStreamConfig.VIDEO_PROCESS_STREAM,
                    RedisStreamConfig.CONSUMER_GROUP
                );
            } catch (Exception e) {
                log.debug("Consumer group might already exist: {}", e.getMessage());
            }

            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    List<MapRecord<String, Object, Object>> records = redisTemplate
                        .opsForStream()
                        .read(
                            Consumer.from(RedisStreamConfig.CONSUMER_GROUP, consumerId),
                            StreamReadOptions.empty().block(Duration.ofSeconds(0)),
                            StreamOffset.create(RedisStreamConfig.VIDEO_PROCESS_STREAM, ReadOffset.lastConsumed())
                        );

                    if (records == null || records.isEmpty()) {
                        continue;
                    }

                    for (MapRecord<String, Object, Object> record : records) {
                        Map<Object, Object> value = record.getValue();
                        try {
                            if (RedisStreamConfig.EventType.TRANSCODE_COMPLETE.equals(value.get("eventType"))) {
                                Long videoId = Long.valueOf((String) value.get("videoId"));
                                Long videoFileId = Long.valueOf((String) value.get("videoFileId"));
                                String outputDir = (String) value.get("outputDir");
                                String targetDirectory = (String) value.get("targetDirectory");

                                processUpload(videoId, videoFileId, outputDir, targetDirectory);
                            }

                            redisTemplate.opsForStream().acknowledge(
                                RedisStreamConfig.VIDEO_PROCESS_STREAM,
                                RedisStreamConfig.CONSUMER_GROUP,
                                record.getId()
                            );

                        } catch (Exception e) {
                            log.error("Error processing upload message for record {}: ", record.getId(), e);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error in upload message handler: ", e);
                    Thread.sleep(1000); // Add a small delay on error to prevent tight loop
                }
            }
        } catch (Exception e) {
            log.error("Fatal error in message handler: ", e);
        }
    }

    /**
     * Actual logic to upload the transcoded output to OSS
     */
    private void processUpload(Long videoId, Long videoFileId, String sourceDirectory, String targetDirectory) {
        try {
            // Check the video file from DB
            SysVideoFile videoFile = videoFileMapper.selectById(videoFileId);
            if (videoFile == null) {
                log.error("Video file not found: {}", videoFileId);
                return;
            }

            // Only proceed if status is PENDING_UPLOAD
            if (!Objects.equals(videoFile.getStatus(), VideoStatus.PENDING_UPLOAD.getCode())) {
                log.info("Skip uploading for video file {}. Current status: {}",
                    videoFileId, videoFile.getStatus());
                return;
            }

            // Check the video
            SysVideo video = videoMapper.selectById(videoId);
            if (video == null) {
                log.error("Video not found: {}", videoId);
                return;
            }

            // Update DB status to "uploading"
            updateStatusToUploading(videoId, videoFileId);

            // Get an OssClient for the upload
            OssClient ossClient = OssFactory.instance();

            // Upload the directory to the object storage
            log.info("Starting upload of directory {} to target path {}", sourceDirectory, targetDirectory);
            String videoS3Url = ossClient.uploadDirectoryTransferAndWait(sourceDirectory, targetDirectory);
            log.info("Upload complete. Video S3 URL: {}", videoS3Url);

            // Upload the thumbnail
            String thumbnailPath = video.getThumbnailUrl();
            String suffix = StringUtils.substring(thumbnailPath, thumbnailPath.lastIndexOf(".") + 1);
            String objectName = "covers/" + StringUtils.substring(thumbnailPath, thumbnailPath.lastIndexOf(File.separator) + 1);

            UploadResult uploadResult = ossClient.upload(Path.of(thumbnailPath), objectName, null, suffix);
            log.info("Thumbnail upload complete. S3 URL: {}", uploadResult.getUrl());

            // Update to success in DB
            updateStatusToSuccess(videoId, videoFileId, videoS3Url, uploadResult.getUrl());

            // Clean up local transcoded files
            FileUtils.del(sourceDirectory);

        } catch (Exception e) {
            log.error("Error during processUpload: ", e);
            // Mark DB status as failed
            updateStatusToFailed(videoId, videoFileId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void updateStatusToUploading(Long videoId, Long videoFileId) {
        SysVideo video = new SysVideo();
        video.setId(videoId);
        video.setStatus(VideoStatus.UPLOADING.getCode());
        videoMapper.updateById(video);

        SysVideoFile videoFile = new SysVideoFile();
        videoFile.setId(videoFileId);
        videoFile.setStatus(VideoStatus.UPLOADING.getCode());
        videoFileMapper.updateById(videoFile);
    }

    @Transactional(rollbackFor = Exception.class)
    void updateStatusToSuccess(Long videoId, Long videoFileId, String s3Url, String thumbnailUrl) {
        SysVideo video = new SysVideo();
        video.setId(videoId);
        video.setStatus(VideoStatus.PUBLISHED.getCode());
        video.setThumbnailUrl(thumbnailUrl);
        videoMapper.updateById(video);

        SysVideoFile videoFile = new SysVideoFile();
        videoFile.setId(videoFileId);
        videoFile.setFilePath(s3Url);
        videoFile.setStatus(VideoStatus.PUBLISHED.getCode());
        videoFileMapper.updateById(videoFile);
    }

    @Transactional(rollbackFor = Exception.class)
    void updateStatusToFailed(Long videoId, Long videoFileId) {
        SysVideo video = new SysVideo();
        video.setId(videoId);
        video.setStatus(VideoStatus.UPLOAD_FAILED.getCode());
        videoMapper.updateById(video);

        SysVideoFile videoFile = new SysVideoFile();
        videoFile.setId(videoFileId);
        videoFile.setStatus(VideoStatus.UPLOAD_FAILED.getCode());
        videoFileMapper.updateById(videoFile);
    }
}
