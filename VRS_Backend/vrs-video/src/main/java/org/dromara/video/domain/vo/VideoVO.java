package org.dromara.video.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Video list view object
 */
@Data
public class VideoVO {

    /**
     * Video ID
     */
    private Long id;

    /**
     * Video title
     */
    private String title;

    /**
     * Category ID
     */
    private Long categoryId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * User name
     */
    private String username;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * View count
     */
    private Long viewCount;

    /**
     * Duration in seconds
     */
    private Long duration;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;
}
