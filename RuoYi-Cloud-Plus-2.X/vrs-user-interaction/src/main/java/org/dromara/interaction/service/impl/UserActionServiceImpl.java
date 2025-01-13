package org.dromara.interaction.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.interaction.domain.dto.UserActionDTO;
import org.dromara.interaction.domain.entity.UserAction;
import org.dromara.interaction.domain.entity.VideoComment;
import org.dromara.interaction.domain.enums.ActionTypeEnum;
import org.dromara.interaction.domain.enums.DelFlagEnum;
import org.dromara.interaction.domain.vo.UserActionVo;
import org.dromara.interaction.mapper.UserActionMapper;
import org.dromara.interaction.mapper.VideoCommentMapper;
import org.dromara.interaction.service.IUserActionService;
import org.dromara.video.api.provider.VideoStatisticsService;
import org.dromara.video.api.provider.VideoValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户行为服务实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserActionServiceImpl implements IUserActionService {

    private final UserActionMapper baseMapper;
    private final VideoCommentMapper commentMapper;

    @DubboReference
    private VideoValidationService videoValidationService;

    @DubboReference
    private VideoStatisticsService videoStatisticsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAction(UserActionDTO actionDTO) {
        // 验证视频是否存在且有效
        if (!videoValidationService.isVideoValid(actionDTO.getVideoId())) {
            throw new ServiceException("视频不存在或已被删除");
        }

        // 验证评论相关操作
        if (isCommentAction(actionDTO.getActionType())) {
            if (actionDTO.getCommentId() == null) {
                throw new ServiceException("评论ID不能为空");
            }
            VideoComment comment = commentMapper.selectById(actionDTO.getCommentId());
            if (comment == null || DelFlagEnum.DELETED.getCode().equals(comment.getDelFlag())) {
                throw new ServiceException("评论不存在或已被删除");
            }
            // 验证评论是否属于该视频
            if (!actionDTO.getVideoId().equals(comment.getVideoId())) {
                throw new ServiceException("评论不属于该视频");
            }
        }

        Long userId = StpUtil.getLoginIdAsLong();

        // 检查是否已经执行过相同的操作
        LambdaQueryWrapper<UserAction> sameActionWrapper = new LambdaQueryWrapper<UserAction>()
            .eq(UserAction::getUserId, userId)
            .eq(UserAction::getVideoId, actionDTO.getVideoId())
            .eq(UserAction::getCommentId, actionDTO.getCommentId() != null ? actionDTO.getCommentId() : 0L)
            .eq(UserAction::getActionType, actionDTO.getActionType());

        UserAction existingSameAction = baseMapper.selectOne(sameActionWrapper);
        if (existingSameAction != null) {
            // 如果已经执行过相同的操作，则取消该操作
            baseMapper.deleteById(existingSameAction.getActionId());
            decrementActionCount(existingSameAction);
            return;
        }

        // 只有互斥的行为（点赞/讨厌）才需要处理对立操作
        if (ActionTypeEnum.isMutuallyExclusive(actionDTO.getActionType())) {
            Integer oppositeActionType = ActionTypeEnum.getOppositeAction(actionDTO.getActionType());
            // 查找是否存在对立操作
            LambdaQueryWrapper<UserAction> wrapper = new LambdaQueryWrapper<UserAction>()
                .eq(UserAction::getUserId, userId)
                .eq(UserAction::getVideoId, actionDTO.getVideoId())
                .eq(UserAction::getCommentId, actionDTO.getCommentId() != null ? actionDTO.getCommentId() : 0L)
                .eq(UserAction::getActionType, oppositeActionType);

            UserAction oppositeAction = baseMapper.selectOne(wrapper);
            if (oppositeAction != null) {
                // 删除对立操作
                baseMapper.deleteById(oppositeAction.getActionId());
                // 减少对应的计数
                decrementActionCount(oppositeAction);
            }
        }

        // 创建用户行为记录
        UserAction action = new UserAction();
        action.setUserId(userId);
        action.setVideoId(actionDTO.getVideoId());
        action.setCommentId(actionDTO.getCommentId() != null ? actionDTO.getCommentId() : 0L);
        action.setActionType(actionDTO.getActionType());
        action.setTime(LocalDateTime.now());

        try {
            // 尝试插入，如果已存在会抛出唯一索引冲突异常
            baseMapper.insert(action);
            // 增加对应的计数
            incrementActionCount(action);
        } catch (Exception e) {
            log.error("用户行为记录插入失败", e);
            throw new ServiceException("用户行为记录插入失败");
        }
    }

    /**
     * 增加行为对应的计数
     */
    private void incrementActionCount(UserAction action) {
        if (ActionTypeEnum.LIKE_VIDEO.getCode().equals(action.getActionType())) {
            if (!videoStatisticsService.incrementLikeCount(action.getVideoId())) {
                throw new ServiceException("更新视频点赞数失败");
            }
        } else if (ActionTypeEnum.DISLIKE_VIDEO.getCode().equals(action.getActionType())) {
            if (!videoStatisticsService.incrementDislikeCount(action.getVideoId())) {
                throw new ServiceException("更新视频不喜欢数失败");
            }
        } else if (ActionTypeEnum.LIKE_COMMENT.getCode().equals(action.getActionType())) {
            if (commentMapper.incrementLikeCount(action.getCommentId()) == 0) {
                throw new ServiceException("更新评论点赞数失败");
            }
        } else if (ActionTypeEnum.DISLIKE_COMMENT.getCode().equals(action.getActionType())) {
            if (commentMapper.incrementHateCount(action.getCommentId()) == 0) {
                throw new ServiceException("更新评论不喜欢数失败");
            }
        }
    }

    /**
     * 减少行为对应的计数
     */
    private void decrementActionCount(UserAction action) {
        if (ActionTypeEnum.LIKE_VIDEO.getCode().equals(action.getActionType())) {
            videoStatisticsService.decrementLikeCount(action.getVideoId());
        } else if (ActionTypeEnum.DISLIKE_VIDEO.getCode().equals(action.getActionType())) {
            videoStatisticsService.decrementDislikeCount(action.getVideoId());
        } else if (ActionTypeEnum.LIKE_COMMENT.getCode().equals(action.getActionType())) {
            commentMapper.decrementLikeCount(action.getCommentId());
        } else if (ActionTypeEnum.DISLIKE_COMMENT.getCode().equals(action.getActionType())) {
            commentMapper.decrementHateCount(action.getCommentId());
        }
    }

    /**
     * 判断是否是评论相关的操作
     */
    private boolean isCommentAction(Integer actionType) {
        return ActionTypeEnum.LIKE_COMMENT.getCode().equals(actionType) ||
               ActionTypeEnum.DISLIKE_COMMENT.getCode().equals(actionType);
    }

    @Override
    public List<UserActionVo> getUserActions(Long videoId) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            return baseMapper.selectUserActions(userId, videoId);
        } catch (Exception e) {
            log.error("获取用户操作列表失败", e);
            throw new ServiceException("获取用户操作列表失败");
        }
    }
}
