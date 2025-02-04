package org.dromara.search.service;

import org.dromara.common.core.domain.R;
import org.dromara.search.domain.dto.VideoSearchDTO;
import org.dromara.search.domain.vo.VideoSearchPageVO;

public interface IVideoSearchService {

    /**
     * Search videos with various criteria
     *
     * @param searchDTO search parameters
     * @return search results with pagination
     */
    R<VideoSearchPageVO> searchVideos(VideoSearchDTO searchDTO);
} 