package org.dromara.video.api.provider.impl;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.video.api.provider.VideoStatisticsService;
import org.dromara.video.mapper.SysVideoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 视频统计服务实现
 */
@RequiredArgsConstructor
@Service
@DubboService
public class VideoStatisticsServiceImpl implements VideoStatisticsService {

    private final SysVideoMapper videoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLikeCount(Long videoId) {
        return videoMapper.incrementLikeCount(videoId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decrementLikeCount(Long videoId) {
        return videoMapper.decrementLikeCount(videoId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementDislikeCount(Long videoId) {
        return videoMapper.incrementDislikeCount(videoId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decrementDislikeCount(Long videoId) {
        return videoMapper.decrementDislikeCount(videoId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementCommentCount(Long videoId) {
        return videoMapper.incrementCommentCount(videoId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean decrementCommentCount(Long videoId) {
        return videoMapper.decrementCommentCount(videoId) > 0;
    }
}
