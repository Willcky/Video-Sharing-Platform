package org.dromara.interaction.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.interaction.domain.dto.VideoCommentCreateDTO;
import org.dromara.interaction.domain.entity.VideoComment;
import org.dromara.interaction.domain.vo.VideoCommentVo;
import org.dromara.interaction.domain.enums.DelFlagEnum;
import org.dromara.interaction.mapper.VideoCommentMapper;
import org.dromara.interaction.service.IVideoCommentService;
import org.dromara.interaction.utils.ImageValidationUtil;
import org.dromara.video.api.provider.VideoStatisticsService;
import org.dromara.video.api.provider.VideoValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 视频评论服务实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoCommentServiceImpl implements IVideoCommentService {

    private final VideoCommentMapper baseMapper;

    @DubboReference
    private VideoValidationService videoValidationService;

    @DubboReference
    private VideoStatisticsService videoStatisticsService;

    /**
     * 固定的分页大小
     */
    private static final int PAGE_SIZE = 10;

    /**
     * 评论图片存储路径前缀
     */
    private static final String COMMENT_IMAGE_PATH = "comment/image/";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoComment createComment(VideoCommentCreateDTO createDTO, MultipartFile image) {
        try {
            // 验证回复评论的情况
            if(createDTO.getPCommentId() != null || createDTO.getReplyUserId() != null) {
                if (createDTO.getPCommentId() == null || createDTO.getReplyUserId() == null) {
                    throw new ServiceException("回复评论时，父评论ID和回复用户ID不能为空");
                }
            }
            // 验证视频是否存在且有效
            if (!videoValidationService.isVideoValid(createDTO.getVideoId())) {
                throw new ServiceException("视频不存在或已被删除");
            }

            VideoComment comment = new VideoComment();
            comment.setVideoId(createDTO.getVideoId());
            comment.setContent(createDTO.getContent());
            comment.setUserId(StpUtil.getLoginIdAsLong());
            comment.setUserName((String) StpUtil.getExtra("userName"));
            comment.setPostTime(LocalDateTime.now());
            comment.setHateCount(0L);
            comment.setCommentCount(0L);
            comment.setLikeCount(0L);
            comment.setDelFlag(DelFlagEnum.VALID.getCode());

            // 处理回复评论的情况
            if (createDTO.getPCommentId() != null) {
                // 检查父评论是否存在
                VideoComment parentComment = baseMapper.selectById(createDTO.getPCommentId());
                if (parentComment == null || DelFlagEnum.DELETED.getCode().equals(parentComment.getDelFlag())
                    || !Objects.equals(parentComment.getUserId(), createDTO.getReplyUserId())) {
                    throw new ServiceException("回复的评论不存在");
                }
                comment.setPCommentId(createDTO.getPCommentId());
                comment.setReplyUserId(createDTO.getReplyUserId());

                // 增加父评论的回复数量
                if (baseMapper.incrementCommentCount(createDTO.getPCommentId()) == 0) {
                    throw new ServiceException("更新父评论回复数量失败");
                }
            }

            // 处理图片上传
            if (image != null && !image.isEmpty()) {
                ImageValidationUtil.validateImage(image);
                String imgPath = saveImage(image);
                comment.setImgPath(imgPath);
            }
            videoStatisticsService.incrementCommentCount(createDTO.getVideoId());
            // 插入评论
            if (baseMapper.insert(comment) == 0) {
                throw new ServiceException("创建评论失败");
            }

            return comment;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建评论失败", e);
            throw new ServiceException("创建评论失败: " + e.getMessage());
        }
    }

    @Override
    public TableDataInfo<VideoCommentVo> getFirstLevelComments(String videoId, Integer pageNum) {
        try {
            Page<VideoCommentVo> page = new Page<>(pageNum, PAGE_SIZE);
            page = baseMapper.selectFirstLevelComments(page, Long.valueOf(videoId));
            return new TableDataInfo<>(page.getRecords(), page.getTotal());
        } catch (Exception e) {
            log.error("获取一级评论列表失败", e);
            throw new ServiceException("获取评论列表失败");
        }
    }

    @Override
    public TableDataInfo<VideoCommentVo> getReplyComments(Long commentId, Integer pageNum) {
        try {
            Page<VideoCommentVo> page = new Page<>(pageNum, PAGE_SIZE);
            page = baseMapper.selectReplyComments(page, commentId);
            return new TableDataInfo<>(page.getRecords(), page.getTotal());
        } catch (Exception e) {
            log.error("获取回复评论列表失败", e);
            throw new ServiceException("获取回复列表失败");
        }
    }


    /**
     * 保存图片到OSS
     *
     * @param image 图片文件
     * @return 图片访问路径
     */
    private String saveImage(MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);

            // 使用雪花算法生成唯一文件名
            String fileName = IdUtil.getSnowflakeNextIdStr() + "." + extension;

            // 完整的存储路径
            String key = COMMENT_IMAGE_PATH + fileName;

            // 获取OSS客户端并上传
            OssClient ossClient = OssFactory.instance();
            UploadResult uploadResult = ossClient.upload(
                image.getInputStream(),
                key,
                image.getSize(),
                image.getContentType()
            );

            return uploadResult.getUrl();
        } catch (IOException e) {
            log.error("保存评论图片失败", e);
            throw new ServiceException("图片保存失败");
        }
    }
}
