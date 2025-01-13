package org.dromara.interaction.service;

import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.interaction.domain.dto.VideoCommentCreateDTO;
import org.dromara.interaction.domain.entity.VideoComment;
import org.dromara.interaction.domain.vo.VideoCommentVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频评论服务接口
 */
public interface IVideoCommentService {

    /**
     * 创建评论
     */
    VideoComment createComment(VideoCommentCreateDTO createDTO, MultipartFile image);

    /**
     * 获取视频的一级评论列表（分页，固定每页10条）
     */
    TableDataInfo<VideoCommentVo> getFirstLevelComments(String videoId, Integer pageNum);

    /**
     * 获取评论的回复列表（分页，固定每页10条）
     */
    TableDataInfo<VideoCommentVo> getReplyComments(Long commentId, Integer pageNum);

}
