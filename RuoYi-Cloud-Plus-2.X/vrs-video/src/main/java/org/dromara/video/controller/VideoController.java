package org.dromara.video.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.video.domain.dto.VideoUploadDTO;
import org.dromara.video.service.IVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Video Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class VideoController {

    private final IVideoService videoService;

    /**
     * Upload video
     */
    @SaCheckLogin
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> upload(@Valid VideoUploadDTO uploadDTO) {
        Long userId = LoginHelper.getUserId();
        videoService.uploadVideo(uploadDTO, userId);
        return R.ok();
    }
}
