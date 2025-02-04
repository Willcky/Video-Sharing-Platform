package org.dromara.interaction.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.interaction.domain.dto.UserActionDTO;
import org.dromara.interaction.domain.vo.UserActionVo;
import org.dromara.interaction.service.IUserActionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为接口
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/action")
public class UserActionController extends BaseController {

    private final IUserActionService userActionService;

    /**
     * 执行用户行为
     * 支持：点赞视频、讨厌视频、点赞评论、讨厌评论、收藏视频、关注UP主
     */
    @PostMapping("/do")
    @SaCheckLogin
    public R<Void> doAction(@Valid @RequestBody UserActionDTO actionDTO) {
        try {
            userActionService.doAction(actionDTO);
            return R.ok();
        } catch (Exception e) {
            log.error("执行用户行为失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 获取用户对视频的操作列表
     */
    @GetMapping("/list/{videoId}")
    @SaCheckLogin
    public R<List<UserActionVo>> getUserActions(@PathVariable("videoId") Long videoId) {
        try {
            List<UserActionVo> actions = userActionService.getUserActions(videoId);
            return R.ok(actions);
        } catch (Exception e) {
            log.error("获取用户操作列表失败", e);
            return R.fail(e.getMessage());
        }
    }
}
