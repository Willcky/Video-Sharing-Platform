package org.dromara.interaction.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户行为请求DTO
 */
@Data
public class UserActionDTO {

    /**
     * 视频ID
     */
    @NotNull(message = "视频ID不能为空")
    private Long videoId;

    /**
     * 评论ID（点赞/讨厌评论时必填）
     */
    private Long commentId;

    /**
     * 行为类型
     */
    @NotNull(message = "行为类型不能为空")
    private Integer actionType;
}
