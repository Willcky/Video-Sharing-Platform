package org.dromara.video.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Video Upload Message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadMessage {

    /**
     * Video ID
     */
    private Long videoId;

    /**
     * Video file ID
     */
    private Long videoFileId;

    /**
     * Source directory path containing transcoded files
     */
    private String sourceDirectory;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Target directory in S3
     */
    private String targetDirectory;
} 