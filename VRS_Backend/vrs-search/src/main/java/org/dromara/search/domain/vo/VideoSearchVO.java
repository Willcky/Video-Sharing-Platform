package org.dromara.search.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class VideoSearchVO {
    /**
     * Video ID
     */
    private Long id;

    /**
     * Video title
     */
    private String title;

    /**
     * Video tags
     */
    private String tags;

    /**
     * Category ID
     */
    private Long categoryId;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * User name
     */
    private String userName;

    /**
     * View count
     */
    private Long viewCount;

    /**
     * Like count
     */
    private Long likeCount;

    /**
     * Creation time
     */
    private Date createTime;
} 