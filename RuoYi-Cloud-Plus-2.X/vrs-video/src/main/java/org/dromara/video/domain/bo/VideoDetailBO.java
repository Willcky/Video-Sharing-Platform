package org.dromara.video.domain.bo;

import lombok.Data;
import org.dromara.video.domain.vo.VideoDetailVO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Video detail business object
 */
@Data
public class VideoDetailBO {

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
     * Available resolutions as comma-separated string
     */
    private String availableResolutions;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Convert to VideoDetailVO with resolution splitting
     */
    public VideoDetailVO toVO() {
        VideoDetailVO vo = new VideoDetailVO();
        vo.setId(this.id);
        vo.setTitle(this.title);
        vo.setDescription(this.description);
        vo.setUsername(this.username);
        vo.setViewCount(this.viewCount);
        vo.setLikeCount(this.likeCount);
        vo.setDislikeCount(this.dislikeCount);
        vo.setCommentCount(this.commentCount);
        vo.setDuration(this.duration);
        vo.setCreateTime(this.createTime);
        vo.setPlaybackUrl(this.playbackUrl);
        vo.setThumbnailUrl(this.thumbnailUrl);

        // Split resolutions if not empty
        if (this.availableResolutions != null && !this.availableResolutions.isEmpty()) {
            vo.setAvailableResolutions(Arrays.asList(this.availableResolutions.split(",")));
        }
        if (this.tags != null && !this.tags.isEmpty()) {
            vo.setTags(Arrays.asList(this.tags.split(",")));
        }
        return vo;
    }
}
