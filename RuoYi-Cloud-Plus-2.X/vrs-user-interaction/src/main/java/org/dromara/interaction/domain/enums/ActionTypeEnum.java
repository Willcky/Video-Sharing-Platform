package org.dromara.interaction.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户行为类型枚举
 */
@Getter
@AllArgsConstructor
public enum ActionTypeEnum {

    LIKE_VIDEO(1, "点赞视频"),
    DISLIKE_VIDEO(2, "讨厌视频"),
    LIKE_COMMENT(3, "点赞评论"),
    DISLIKE_COMMENT(4, "讨厌评论"),
    FAVORITE_VIDEO(5, "收藏视频"),
    SUBSCRIBE_USER(6, "关注UP主"),
    CANCEL_FAVORITE(7, "取消收藏"),
    CANCEL_SUBSCRIBE(8, "取消关注");

    private final Integer code;
    private final String info;

    /**
     * 获取对立的行为类型
     * 例如：点赞和讨厌是对立的，收藏和取消收藏是对立的
     */
    public static Integer getOppositeAction(Integer actionType) {
        if (LIKE_VIDEO.getCode().equals(actionType)) {
            return DISLIKE_VIDEO.getCode();
        } else if (DISLIKE_VIDEO.getCode().equals(actionType)) {
            return LIKE_VIDEO.getCode();
        } else if (LIKE_COMMENT.getCode().equals(actionType)) {
            return DISLIKE_COMMENT.getCode();
        } else if (DISLIKE_COMMENT.getCode().equals(actionType)) {
            return LIKE_COMMENT.getCode();
        } else if (FAVORITE_VIDEO.getCode().equals(actionType)) {
            return CANCEL_FAVORITE.getCode();
        } else if (CANCEL_FAVORITE.getCode().equals(actionType)) {
            return FAVORITE_VIDEO.getCode();
        } else if (SUBSCRIBE_USER.getCode().equals(actionType)) {
            return CANCEL_SUBSCRIBE.getCode();
        } else if (CANCEL_SUBSCRIBE.getCode().equals(actionType)) {
            return SUBSCRIBE_USER.getCode();
        }
        return null;
    }

    public static boolean isMutuallyExclusive(Integer actionType) {
        return LIKE_VIDEO.getCode().equals(actionType) ||
            DISLIKE_VIDEO.getCode().equals(actionType) ||
            LIKE_COMMENT.getCode().equals(actionType) ||
            DISLIKE_COMMENT.getCode().equals(actionType);
    }
}
