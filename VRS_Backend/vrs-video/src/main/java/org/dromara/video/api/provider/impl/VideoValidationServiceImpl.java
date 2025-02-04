package org.dromara.video.api.provider.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.video.api.provider.VideoValidationService;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.enums.VideoStatus;
import org.dromara.video.mapper.SysVideoMapper;
import org.springframework.stereotype.Service;

/**
 * 视频验证服务实现
 */
@RequiredArgsConstructor
@Service
@DubboService
public class VideoValidationServiceImpl implements VideoValidationService {

    private final SysVideoMapper videoMapper;

    @Override
    public boolean isVideoValid(Long videoId) {
        LambdaQueryWrapper<SysVideo> wrapper = new LambdaQueryWrapper<SysVideo>()
            .eq(SysVideo::getId, videoId)
            .eq(SysVideo::getDelFlag, 0)  // 未删除
            .eq(SysVideo::getStatus, VideoStatus.PUBLISHED);   // 已发布状态

        return videoMapper.exists(wrapper);
    }


}
