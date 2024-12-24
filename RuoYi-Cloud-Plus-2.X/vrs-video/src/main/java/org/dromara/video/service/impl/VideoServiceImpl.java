package org.dromara.video.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.video.config.VideoStorageConfig;
import org.dromara.video.domain.dto.VideoUploadDTO;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.entity.SysVideoFile;
import org.dromara.video.domain.enums.VideoStatus;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.mapper.SysVideoFileMapper;
import org.dromara.video.mapper.SysVideoMapper;
import org.dromara.video.service.IVideoService;
import org.dromara.video.service.IVideoTranscodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Video Service Implementation
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements IVideoService {

    private final SysVideoMapper videoMapper;
    private final SysVideoFileMapper videoFileMapper;
    private final VideoStorageConfig storageConfig;
    private final IVideoTranscodeService transcodeService;

    private static final String[] ALLOWED_VIDEO_TYPES = {".mp4", ".webm", ".avi", ".mov"};
    private static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png"};
    private static final long MAX_VIDEO_SIZE = 30L * 1024 * 1024; // 30MB
    private static final long MAX_THUMBNAIL_SIZE = 2L * 1024 * 1024; // 2MB

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadVideo(VideoUploadDTO uploadDTO, Long userId) {
        MultipartFile videoFile = uploadDTO.getVideoFile();
        MultipartFile thumbnailFile = uploadDTO.getThumbnailFile();

        // Validate files
        validateVideoFile(videoFile);
        validateThumbnailFile(thumbnailFile);

        // Save video metadata
        SysVideo video = new SysVideo();
        video.setId(IdUtil.getSnowflakeNextId());
        video.setTitle(uploadDTO.getTitle());
        video.setDescription(uploadDTO.getDescription());
        video.setCategoryId(uploadDTO.getCategoryId());
        video.setUserId(userId);
        video.setTags(StringUtils.join(uploadDTO.getTags(), ","));
        video.setStatus(VideoStatus.PENDING_TRANSCODE.getCode());
        video.setCreateTime(LocalDateTime.now());
        video.setUpdateTime(LocalDateTime.now());

        try {
            // Save thumbnail
            String thumbnailPath = saveThumbnail(thumbnailFile, video.getId());
            video.setThumbnailUrl(thumbnailPath);
            videoMapper.insert(video);

            // Save video file and get the entity
            SysVideoFile videoFileResult = saveVideoFile(videoFile, video, userId);

            // Send to transcoding queue
            VideoTranscodeMessage message = VideoTranscodeMessage.builder()
                .videoId(video.getId())
                .videoFileId(videoFileResult.getId())
                .sourceFilePath(videoFileResult.getFilePath())
                .userId(userId)
                .fileName(videoFileResult.getFileName())
                .build();

            transcodeService.sendToTranscode(message);

        } catch (IOException e) {
            log.error("Error uploading video: ", e);
            throw new ServiceException("视频上传失败");
        }
    }

    /**
     * Save thumbnail file and return its relative path
     */
    private String saveThumbnail(MultipartFile thumbnailFile, Long videoId) throws IOException {
        String extension = getFileExtension(thumbnailFile.getOriginalFilename());
        String fileName = videoId + "_thumb." + extension;
        String relativePath = generateRelativePath(fileName);
        String absolutePath = storageConfig.getCoverPath() + File.separator + relativePath;

        // Create directories if they don't exist
        FileUtil.mkdir(new File(absolutePath).getParentFile());

        // Save the thumbnail
        thumbnailFile.transferTo(new File(absolutePath));

        return relativePath;
    }

    /**
     * Save video file and its metadata
     */
    private SysVideoFile saveVideoFile(MultipartFile file, SysVideo video, Long userId) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = video.getId() + "." + extension;
        String relativePath = generateRelativePath(fileName);
        String absolutePath = storageConfig.getVideoPath() + File.separator + relativePath;

        // Create directories if they don't exist
        FileUtil.mkdir(new File(absolutePath).getParentFile());

        // Save the file
        file.transferTo(new File(absolutePath));

        // Save file metadata
        SysVideoFile videoFile = new SysVideoFile();
        videoFile.setId(IdUtil.getSnowflakeNextId());
        videoFile.setVideoId(video.getId());
        videoFile.setUserId(userId);
        videoFile.setFileName(file.getOriginalFilename());
        videoFile.setFilePath(relativePath);
        videoFile.setFileSize(file.getSize());
        videoFile.setFileType(extension);
        videoFile.setStorageType(0); // Local storage
        videoFile.setStatus(VideoStatus.PENDING_TRANSCODE.getCode());
        videoFile.setCreateTime(LocalDateTime.now());
        videoFile.setUpdateTime(LocalDateTime.now());
        videoFileMapper.insert(videoFile);

        return videoFile;
    }

    /**
     * Generate relative storage path using date-based directory structure
     */
    private String generateRelativePath(String fileName) {
        return fileName + IdUtil.getSnowflakeNextId();
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("请选择要上传的视频文件");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isValidFileExtension(extension, ALLOWED_VIDEO_TYPES)) {
            throw new ServiceException("不支持的视频格式，请上传" + Arrays.toString(ALLOWED_VIDEO_TYPES) + "格式的视频");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new ServiceException("视频文件大小不能超过30MB");
        }
    }

    private void validateThumbnailFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("请选择要上传的缩略图");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!isValidFileExtension(extension, ALLOWED_IMAGE_TYPES)) {
            throw new ServiceException("不支持的图片格式，请上传" + Arrays.toString(ALLOWED_IMAGE_TYPES) + "格式的图片");
        }

        if (file.getSize() > MAX_THUMBNAIL_SIZE) {
            throw new ServiceException("缩略图大小不能超过2MB");
        }
    }

    private String getFileExtension(String filename) {
        return StrUtil.isNotEmpty(filename) ?
            StrUtil.subAfter(filename, ".", true) : "";
    }

    private boolean isValidFileExtension(String extension, String[] allowedTypes) {
        if (StrUtil.isEmpty(extension)) {
            return false;
        }
        String ext = "." + extension.toLowerCase();
        return Arrays.asList(allowedTypes).contains(ext);
    }
}
