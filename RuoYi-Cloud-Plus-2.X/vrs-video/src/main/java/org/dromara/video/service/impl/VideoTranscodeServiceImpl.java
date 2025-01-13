package org.dromara.video.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.ffmpeg.domain.VideoTransResult;
import org.dromara.common.ffmpeg.utils.FFmpegUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.config.RedisChannelConfig;
import org.dromara.video.config.RedisStreamConfig;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.entity.SysVideoFile;
import org.dromara.video.domain.enums.VideoStatus;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.domain.message.VideoUploadMessage;
import org.dromara.video.mapper.SysVideoFileMapper;
import org.dromara.video.mapper.SysVideoMapper;
import org.dromara.video.service.IVideoTranscodeService;
import org.dromara.video.service.IVideoUploadService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.stream.StreamRecords;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

/**
 * Video Transcode Service Implementation
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoTranscodeServiceImpl implements IVideoTranscodeService {

    private final SysVideoMapper videoMapper;
    private final SysVideoFileMapper videoFileMapper;
    private final FFmpegUtils fFmpegUtils;
    private final IVideoUploadService uploadService;
    private final StringRedisTemplate redisTemplate;
    private static final String TRANSCODE_LOCK_KEY = "video:transcode:lock:";
    private static final String TRANSCODE_QUEUE_KEY = "video:transcode:queue";
    private static final long LOCK_TIMEOUT = 30; // 30 seconds lock timeout

    @Override
    public void sendToTranscode(VideoTranscodeMessage message) {
        try {
            // Add message to Redis List instead of publishing
            String messageJson = JSON.toJSONString(message);
            redisTemplate.opsForList().rightPush(TRANSCODE_QUEUE_KEY, messageJson);
            log.info("Added video to transcode queue: {}", messageJson);
        } catch (Exception e) {
            log.error("Error sending video to transcode queue: ", e);
            throw new ServiceException("发送转码任务失败");
        }
    }

    @Override
    public void handleTranscodeMessage() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Try to get message from queue with timeout
                String messageJson = redisTemplate.opsForList().leftPop(TRANSCODE_QUEUE_KEY, 5, TimeUnit.SECONDS);
                if (messageJson == null) {
                    Thread.sleep(1000);
                    continue;
                }

                VideoTranscodeMessage message = JSON.parseObject(messageJson, VideoTranscodeMessage.class);
                String lockKey = TRANSCODE_LOCK_KEY + message.getVideoFileId();

                // Try to acquire lock with expiration
                Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TIMEOUT, TimeUnit.SECONDS);
                if (acquired != null && acquired) {
                    try {
                        processTranscodeMessage(message);
                    } finally {
                        // Release lock after processing
                        redisTemplate.delete(lockKey);
                    }
                } else {
                    log.info("Skip processing, another consumer is handling video: {}", message.getVideoFileId());
                }

            } catch (Exception e) {
                log.error("Error in transcode message handler: ", e);
            }
        }
    }

    private void processTranscodeMessage(VideoTranscodeMessage message) {
        try {
            log.info("Processing transcode message: {}", JSON.toJSONString(message));

            // Check video file status first
            SysVideoFile videoFile = videoFileMapper.selectById(message.getVideoFileId());
            if (videoFile == null) {
                log.error("Video file not found: {}", message.getVideoFileId());
                return;
            }

            // Only proceed if status is PENDING_TRANSCODE
            if (videoFile.getStatus() != VideoStatus.PENDING_TRANSCODE.getCode()) {
                log.info("Skip transcoding for video file {} as its status is not PENDING_TRANSCODE (current status: {})",
                    message.getVideoFileId(), videoFile.getStatus());
                return;
            }

            // Update video status to transcoding
            SysVideo video = new SysVideo();
            video.setId(message.getVideoId());
            video.setStatus(VideoStatus.TRANSCODING.getCode());
            videoMapper.updateById(video);

            // Update video file status
            videoFile = new SysVideoFile();
            videoFile.setId(message.getVideoFileId());
            videoFile.setStatus(VideoStatus.TRANSCODING.getCode());
            videoFileMapper.updateById(videoFile);

            // Perform transcoding
            VideoTransResult videoTransResult = fFmpegUtils.convertToHls(message.getSourceFilePath(), message.getVideoFileId());

            // Update status to PENDING_UPLOAD and send upload message
            updateStatusAndSendUpload(message, videoTransResult);

        } catch (Exception e) {
            log.error("Error handling transcode message: ", e);
            // Update status to transcode failed
            updateStatusToFailed(message.getVideoId(), message.getVideoFileId());
        }
    }

    private void updateStatusAndSendUpload(VideoTranscodeMessage transcodeMessage, VideoTransResult videoTransResult) {
        try {
            String outputDir = videoTransResult.getOutputDir();
            String resolution = StringUtils.join(videoTransResult.getResolutions(), ",");
            long duration = videoTransResult.getDuration();

            // Update status to PENDING_UPLOAD
            SysVideo video = new SysVideo();
            video.setId(transcodeMessage.getVideoId());
            video.setStatus(VideoStatus.PENDING_UPLOAD.getCode());
            video.setDuration(duration);
            videoMapper.updateById(video);

            SysVideoFile videoFile = new SysVideoFile();
            videoFile.setId(transcodeMessage.getVideoFileId());
            videoFile.setStatus(VideoStatus.PENDING_UPLOAD.getCode());
            videoFile.setFileType("hls");
            videoFile.setFilePath(outputDir);
            videoFile.setResolution(resolution);
            videoFile.setDuration(duration);
            videoFileMapper.updateById(videoFile);

            // Send transcode complete event to Redis Stream
            Map<String, String> event = new HashMap<>();
            event.put("eventType", RedisStreamConfig.EventType.TRANSCODE_COMPLETE);
            event.put("videoId", transcodeMessage.getVideoId().toString());
            event.put("videoFileId", transcodeMessage.getVideoFileId().toString());
            event.put("outputDir", outputDir);
            event.put("resolution", resolution);
            event.put("duration", String.valueOf(duration));
            event.put("userId", transcodeMessage.getUserId().toString());
            event.put("targetDirectory", "videos/" + transcodeMessage.getVideoFileId());
            event.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // Add event to stream
            redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                    .in(RedisStreamConfig.VIDEO_PROCESS_STREAM)
                    .ofMap(event)
            );

        } catch (Exception e) {
            log.error("Error updating status and sending upload message: ", e);
            updateStatusToFailed(transcodeMessage.getVideoId(), transcodeMessage.getVideoFileId());
        }
    }

    private void updateStatusToFailed(Long videoId, Long videoFileId) {
        try {
            SysVideo video = new SysVideo();
            video.setId(videoId);
            video.setStatus(VideoStatus.TRANSCODE_FAILED.getCode());
            videoMapper.updateById(video);

            SysVideoFile videoFile = new SysVideoFile();
            videoFile.setId(videoFileId);
            videoFile.setStatus(VideoStatus.TRANSCODE_FAILED.getCode());
            videoFileMapper.updateById(videoFile);
        } catch (Exception e) {
            log.error("Error updating failed status: ", e);
        }
    }
}
