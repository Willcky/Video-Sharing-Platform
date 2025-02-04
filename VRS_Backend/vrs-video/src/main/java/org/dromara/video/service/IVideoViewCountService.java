package org.dromara.video.service;

/**
 * 视频播放量统计服务
 */
public interface IVideoViewCountService {

    /**
     * 增加视频播放量
     *
     * @param videoId 视频ID
     */
    void increaseViewCount(Long videoId);

}
