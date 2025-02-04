package org.dromara.interaction.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论表 对象 video_comment
 */
@Data
@TableName("video_comment")
public class VideoComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long commentId;

    /**
     * 父级评论ID
     */
    private Long pCommentId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 视频用户ID
     */
    private Long videoUserId;

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
    private Long userId;

    /**
     * 回复用户ID
     */
    private Long replyUserId;

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

    /**
     * Delete flag (0:exists 1:deleted)
     */
    private Integer delFlag;

    /**
     * 评论用户昵称
     */
    @TableField(exist = false)
    private String userName;
}
