package org.dromara.video.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Video Entity
 */
@Data
@TableName("sys_video")
public class SysVideo {

    /**
     * Video ID
     */
    @TableId
    private Long id;

    /**
     * Video title
     */
    private String title;

    /**
     * Video description
     */
    private String description;

    /**
     * Category ID
     */
    private Long categoryId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Video tags (comma-separated)
     */
    private String tags;

    /**
     * View count
     */
    private Long viewCount;

    /**
     * Like count
     */
    private Long likeCount;

    /**
     * Dislike count
     */
    private Long dislikeCount;

    /**
     * Comment count
     */
    private Long commentCount;

    /**
     * Share count
     */
    private Long shareCount;

    /**
     * Duration in seconds
     */
    private Long duration;

    /**
     * Status (0:published 1:reviewing 2:offline 3:transcoding 4:transcode_failed 5:uploaded 6:pending_transcode)
     */
    private Integer status;

    /**
     * Delete flag (0:exists 1:deleted)
     */
    private Integer delFlag;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;

    /**
     * Remark
     */
    private String remark;
}
