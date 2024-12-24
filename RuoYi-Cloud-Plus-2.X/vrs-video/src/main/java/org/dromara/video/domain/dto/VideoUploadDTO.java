package org.dromara.video.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Video upload request DTO
 */
@Data
public class VideoUploadDTO {

    /**
     * Video file
     */
    @NotNull(message = "视频文件不能为空")
    private MultipartFile videoFile;

    /**
     * Thumbnail file
     */
    @NotNull(message = "缩略图不能为空")
    private MultipartFile thumbnailFile;

    /**
     * Video title
     */
    @NotBlank(message = "视频标题不能为空")
    @Size(max = 255, message = "视频标题长度不能超过255个字符")
    private String title;

    /**
     * Video description
     */
    private String description;

    /**
     * Category ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * Video tags (max 10)
     */
    @Size(min = 1, max = 10, message = "标签数量至少一个，最多十个")
    private List<String> tags;
}
