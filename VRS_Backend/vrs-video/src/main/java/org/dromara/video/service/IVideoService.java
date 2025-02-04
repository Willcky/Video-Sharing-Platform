package org.dromara.video.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.video.domain.dto.VideoUploadDTO;
import org.dromara.video.domain.vo.VideoDetailVO;
import org.dromara.video.domain.vo.VideoVO;

/**
 * Video service interface
 */
public interface IVideoService {

    /**
     * Upload video
     */
    void uploadVideo(VideoUploadDTO uploadDTO, Long userId);

    /**
     * Get video list with pagination
     */
    TableDataInfo<VideoVO> queryPageList(PageQuery pageQuery);

    /**
     * Get video detail by ID
     */
    VideoDetailVO getVideoDetail(Long id);
} 