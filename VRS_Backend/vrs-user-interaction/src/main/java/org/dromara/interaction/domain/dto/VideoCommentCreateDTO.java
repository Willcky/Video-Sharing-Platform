package org.dromara.interaction.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 评论创建请求DTO
 */
@Data
public class VideoCommentCreateDTO {

    /**
     * 父级评论ID（回复评论时必填）
     */
    private Long pCommentId;

    /**
     * 视频ID
     */
    @NotNull(message = "视频ID不能为空")
    private Long videoId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500个字符")
    private String content;

    /**
     * 回复用户ID（回复评论时必填）
     */
    private Long replyUserId;

    private MultipartFile image;
}
