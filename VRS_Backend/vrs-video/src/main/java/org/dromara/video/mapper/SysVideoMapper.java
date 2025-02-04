package org.dromara.video.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.vo.VideoVO;
import org.dromara.video.domain.bo.VideoDetailBO;

import java.util.Map;

/**
 * Video Mapper Interface
 */
public interface SysVideoMapper extends BaseMapperPlus<SysVideo, SysVideo> {

    /**
     * Select video list with user and category info
     */
    Page<VideoVO> selectVideoList(Page<SysVideo> page);

    /**
     * Select video detail with video file and user info
     */
    VideoDetailBO selectVideoDetail(@Param("id") Long id);

    /**
     * 增加视频点赞数
     */
    @Update("UPDATE sys_video SET like_count = like_count + 1 WHERE id = #{videoId}")
    int incrementLikeCount(Long videoId);

    /**
     * 减少视频点赞数
     */
    @Update("UPDATE sys_video SET like_count = like_count - 1 WHERE id = #{videoId} AND like_count > 0")
    int decrementLikeCount(Long videoId);

    /**
     * 增加视频不喜欢数
     */
    @Update("UPDATE sys_video SET dislike_count = dislike_count + 1 WHERE id = #{videoId}")
    int incrementDislikeCount(Long videoId);

    /**
     * 减少视频不喜欢数
     */
    @Update("UPDATE sys_video SET dislike_count = dislike_count - 1 WHERE id = #{videoId} AND dislike_count > 0")
    int decrementDislikeCount(Long videoId);

    /**
     * 增加视频评论数
     */
    @Update("UPDATE sys_video SET comment_count = comment_count + 1 WHERE id = #{videoId}")
    int incrementCommentCount(Long videoId);

    /**
     * 减少视频评论数
     */
    @Update("UPDATE sys_video SET comment_count = comment_count - 1 WHERE id = #{videoId} AND comment_count > 0")
    int decrementCommentCount(Long videoId);

    /**
     * 获取视频播放量
     */
    Long selectViewCountById(@Param("videoId") Long videoId);

    /**
     * Batch increment view counts
     * @param viewCounts Map of video IDs to increment values
     * @return number of rows affected
     */
    int batchIncrementViewCounts(@Param("viewCounts") Map<Long, Long> viewCounts);
}
