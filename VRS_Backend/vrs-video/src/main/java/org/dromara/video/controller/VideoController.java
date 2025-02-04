package org.dromara.video.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.video.domain.dto.VideoUploadDTO;
import org.dromara.video.domain.vo.VideoDetailVO;
import org.dromara.video.domain.vo.VideoVO;
import org.dromara.video.service.IVideoService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Video Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {

    private final IVideoService videoService;

    /**
     * Upload video
     */
    @SaCheckLogin
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> upload(@Valid VideoUploadDTO uploadDTO) {
        try {
            Long userId = LoginHelper.getUserId();
            videoService.uploadVideo(uploadDTO, userId);
            return R.ok();
        } catch (Exception e) {
            return R.fail("上传视频失败，请重试");
        }
    }

    /**
     * Get video list with pagination
     */
    @GetMapping("/list")
    public TableDataInfo<VideoVO> list(PageQuery pageQuery) {
        return videoService.queryPageList(pageQuery);
    }

    /**
     * Get video detail by ID
     */
    @GetMapping("/{id}")
    public R<VideoDetailVO> getInfo(@PathVariable Long id) {
        return R.ok(videoService.getVideoDetail(id));
    }
}
