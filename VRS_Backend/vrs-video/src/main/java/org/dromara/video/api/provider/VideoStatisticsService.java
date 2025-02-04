package org.dromara.video.api.provider;

/**
 * 视频统计服务接口
 */
public interface VideoStatisticsService {

    /**
     * 增加视频点赞数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long videoId);

    /**
     * 减少视频点赞数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean decrementLikeCount(Long videoId);

    /**
     * 增加视频不喜欢数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean incrementDislikeCount(Long videoId);

    /**
     * 减少视频不喜欢数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean decrementDislikeCount(Long videoId);

    /**
     * 增加视频评论数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean incrementCommentCount(Long videoId);

    /**
     * 减少视频评论数
     *
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean decrementCommentCount(Long videoId);
}
