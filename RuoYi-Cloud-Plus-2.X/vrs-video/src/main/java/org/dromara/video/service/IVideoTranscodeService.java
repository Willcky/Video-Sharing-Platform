package org.dromara.video.service;

import org.dromara.video.domain.message.VideoTranscodeMessage;

/**
 * Video transcode service interface
 */
public interface IVideoTranscodeService {

    /**
     * Send video to transcode queue
     */
    void sendToTranscode(VideoTranscodeMessage message);

    /**
     * Handle transcode messages from queue
     * This method runs in a loop and should be called by a single consumer
     */
    void handleTranscodeMessage();
} 