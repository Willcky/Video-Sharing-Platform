package org.dromara.interaction.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论信息视图对象
 */
@Data
public class VideoCommentVo {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 父级评论ID
     */
    private Long pCommentId;

    /**
     * 视频ID
     */
    private String videoId;

    /**
     * 视频用户ID
     */
    private String videoUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 图片路径
     */
    private String imgPath;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 回复用户ID
     */
    private String replyUserId;

    /**
     * 回复用户名称
     */
    private String replyUserName;

    /**
     * 发布时间
     */
    private LocalDateTime postTime;

    /**
     * 差评数量
     */
    private Long hateCount;

    /**
     * 评论数量
     */
    private Long commentCount;

    /**
     * 喜欢数量
     */
    private Long likeCount;
} 