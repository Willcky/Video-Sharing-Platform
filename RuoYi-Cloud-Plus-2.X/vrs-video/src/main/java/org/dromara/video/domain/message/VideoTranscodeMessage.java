package org.dromara.video.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Video Transcode Message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTranscodeMessage {

    /**
     * Video ID
     */
    private Long videoId;

    /**
     * Video file ID
     */
    private Long videoFileId;

    /**
     * Original file path
     */
    private String sourceFilePath;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Original file name
     */
    private String fileName;
}
