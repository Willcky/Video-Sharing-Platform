package org.dromara.search.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.search.domain.dto.VideoSearchDTO;
import org.dromara.search.domain.vo.VideoSearchPageVO;
import org.dromara.search.service.IVideoSearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Video search controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class VideoSearchController extends BaseController {

    private final IVideoSearchService videoSearchService;

    /**
     * Search videos with various criteria
     */
    @PostMapping("/search")
    public R<VideoSearchPageVO> search(@RequestBody VideoSearchDTO searchDTO) {
        return videoSearchService.searchVideos(searchDTO);
    }
}
