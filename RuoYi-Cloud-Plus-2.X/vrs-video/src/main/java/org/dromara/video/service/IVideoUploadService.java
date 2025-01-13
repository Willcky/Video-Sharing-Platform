package org.dromara.video.service;

import org.dromara.video.domain.message.VideoUploadMessage;

/**
 * Video Upload Service Interface
 */
public interface IVideoUploadService {

    void handleUploadMessage();
}
