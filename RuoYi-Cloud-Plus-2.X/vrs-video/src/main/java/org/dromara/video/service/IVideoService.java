package org.dromara.video.service;

import org.dromara.video.domain.dto.VideoUploadDTO;

/**
 * Video Service Interface
 */
public interface IVideoService {

    /**
     * Upload video
     *
     * @param uploadDTO Video upload request
     * @param userId User ID
     */
    void uploadVideo(VideoUploadDTO uploadDTO, Long userId);
} 