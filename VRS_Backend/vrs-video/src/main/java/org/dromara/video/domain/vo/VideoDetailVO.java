package org.dromara.video.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Video detail view object
 */
@Data
public class VideoDetailVO {

    /**
     * Video ID
     */
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
     * User name
     */
    private String username;

    /**
     * User Id
     */
    private Long userId;
    /**
     * Video tags
     */
    private List<String> tags;

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
     * Duration in seconds
     */
    private Long duration;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * M3U8 URL for playback
     */
    private String playbackUrl;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Available resolutions
     */
    private List<String> availableResolutions;
}
