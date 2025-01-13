package org.dromara.video.api.provider;

/**
 * 视频验证服务接口
 */
public interface VideoValidationService {

    /**
     * 检查视频是否存在且有效
     *
     * @param videoId 视频ID
     * @return 视频是否有效
     */
    boolean isVideoValid(Long videoId);

}
