package org.dromara.interaction.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.interaction.domain.entity.VideoComment;
import org.dromara.interaction.domain.vo.VideoCommentVo;

/**
 * 评论 数据层
 */
public interface VideoCommentMapper extends BaseMapperPlus<VideoComment, VideoComment> {

    /**
     * 获取一级评论列表
     */
    Page<VideoCommentVo> selectFirstLevelComments(@Param("page") Page<VideoCommentVo> page, @Param("videoId") Long videoId);

    /**
     * 获取回复评论列表
     */
    Page<VideoCommentVo> selectReplyComments(@Param("page") Page<VideoCommentVo> page, @Param("commentId") Long commentId);

    /**
     * 增加评论的回复数量
     */
    int incrementCommentCount(@Param("commentId") Long commentId);

    /**
     * 增加评论的差评数量
     */
    int incrementHateCount(@Param("commentId") Long commentId);

    /**
     * 减少评论的差评数量
     */
    int decrementHateCount(@Param("commentId") Long commentId);

    /**
     * 增加评论的点赞数量
     */
    int incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少评论的点赞数量
     */
    int decrementLikeCount(@Param("commentId") Long commentId);
} 