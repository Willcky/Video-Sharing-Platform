package org.dromara.search.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class VideoSearchDTO {

    /**
     * Search keyword for title and tags
     */
    private String keyword;

    /**
     * Sort type: 0-relevance(default), 1-latest, 2-most likes, 3-most views
     */
    private Integer sortType = 0;

    /**
     * Page number
     */
    private Integer pageNum = 1;

    /**
     * Page size
     */
    private Integer pageSize = 10;
} 