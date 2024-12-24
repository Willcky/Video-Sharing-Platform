package org.dromara.video.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.config.RedisChannelConfig;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.entity.SysVideoFile;
import org.dromara.video.domain.enums.VideoStatus;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.mapper.SysVideoFileMapper;
import org.dromara.video.mapper.SysVideoMapper;
import org.dromara.video.service.IVideoTranscodeService;
import org.springframework.stereotype.Service;

/**
 * Video Transcode Service Implementation
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoTranscodeServiceImpl implements IVideoTranscodeService {

    private final RedisUtils redisUtils;
    private final SysVideoMapper videoMapper;
    private final SysVideoFileMapper videoFileMapper;

    @Override
    public void sendToTranscode(VideoTranscodeMessage message) {
        try {
            String jsonMessage = JSON.toJSONString(message);
            redisUtils.publish(RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL, jsonMessage);
            log.info("Sent video to transcode queue: {}", jsonMessage);
        } catch (Exception e) {
            log.error("Error sending video to transcode queue: ", e);
            throw new ServiceException("发送转码任务失败");
        }
    }

    @Override
    public void handleTranscodeMessage(VideoTranscodeMessage message) {
        try {
            log.info("Received transcode message: {}", JSON.toJSONString(message));

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

            // TODO: Implement actual transcoding logic
            // This would typically involve:
            // 1. Reading the source file
            // 2. Transcoding to different resolutions
            // 3. Saving transcoded files
            // 4. Updating status on completion

        } catch (Exception e) {
            log.error("Error handling transcode message: ", e);
            // Update status to transcode failed
            updateStatusToFailed(message.getVideoId(), message.getVideoFileId());
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
