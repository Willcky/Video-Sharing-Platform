package org.dromara.search.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VideoSearchPageVO {
    /**
     * Total number of records
     */
    private Long total;

    /**
     * Current page number
     */
    private Integer pageNum;

    /**
     * Page size
     */
    private Integer pageSize;

    /**
     * Video search results
     */
    private List<VideoSearchVO> records;

    public VideoSearchPageVO(Long total, Integer pageNum, Integer pageSize, List<VideoSearchVO> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }
} 