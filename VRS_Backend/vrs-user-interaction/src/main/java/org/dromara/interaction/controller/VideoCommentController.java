package org.dromara.interaction.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.interaction.domain.dto.VideoCommentCreateDTO;
import org.dromara.interaction.domain.entity.VideoComment;
import org.dromara.interaction.domain.vo.VideoCommentVo;
import org.dromara.interaction.service.IVideoCommentService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频评论接口
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class VideoCommentController extends BaseController {

    private final IVideoCommentService videoCommentService;

    /**
     * 创建评论
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckLogin
    public R<VideoComment> createComment(
        @Valid VideoCommentCreateDTO createDTO
    ) {
        try {
            VideoComment comment = videoCommentService.createComment(createDTO, createDTO.getImage());
            return R.ok(comment);
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取视频的一级评论列表（分页，固定每页10条）
     */
    @GetMapping("/list/{videoId}")
    public TableDataInfo<VideoCommentVo> getFirstLevelComments(
        @PathVariable("videoId") String videoId,
        @NotNull @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum
    ) {
        return videoCommentService.getFirstLevelComments(videoId, pageNum);
    }

    /**
     * 获取评论的回复列表（分页，固定每页10条）
     */
    @GetMapping("/replies/{commentId}")
    public TableDataInfo<VideoCommentVo> getReplyComments(
        @PathVariable("commentId") Long commentId,
        @NotNull @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum
    ) {
        return videoCommentService.getReplyComments(commentId, pageNum);
    }
}
