package org.dromara.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Video Storage Configuration
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "video.storage")
public class VideoStorageConfig {

    /**
     * Local storage base path
     */
    private String localPath;

    /**
     * Temporary file path
     */
    private String tempPath;

    /**
     * Video file path
     */
    private String videoPath;

    /**
     * Cover file path
     */
    private String coverPath;
}
