package org.dromara.interaction.domain.vo;

import lombok.Data;

/**
 * 用户操作信息VO
 */
@Data
public class UserActionVo {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 用户ID
     */
    private Long userId;
}
