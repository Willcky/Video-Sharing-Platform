package org.dromara.video.service;

import org.dromara.video.domain.message.VideoTranscodeMessage;

/**
 * Video Transcode Service Interface
 */
public interface IVideoTranscodeService {

    /**
     * Send video to transcoding queue
     *
     * @param message Transcode message
     */
    void sendToTranscode(VideoTranscodeMessage message);

    /**
     * Handle transcoding message
     *
     * @param message Transcode message
     */
    void handleTranscodeMessage(VideoTranscodeMessage message);
} 