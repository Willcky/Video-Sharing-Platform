package org.dromara.common.ffmpeg.config;

import lombok.Data;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FFmpeg Configuration
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "ffmpeg")
public class FFmpegConfig {

    /**
     * FFmpeg executable path
     */
    private String ffmpegPath;

    /**
     * FFprobe executable path
     */
    private String ffprobePath;

    /**
     * Output directory for transcoded files
     */
    private String outputPath;

    /**
     * Temporary directory for processing
     */
    private String tempPath;

    /**
     * HLS segment duration in seconds
     */
    private int hlsTime;

    /**
     * HLS playlist size
     */
    private int hlsListSize;

    /**
     * Video bitrates for different qualities (in kbps)
     */
    private int[] videoBitrates;

    /**
     * Video resolutions (height in pixels)
     */
    private int[] videoResolutions;

    @PostConstruct
    public void init() {
        validatePaths();
        createDirectories();
    }

    /**
     * Validate FFmpeg paths and settings
     */
    private void validatePaths() {
        // Check FFmpeg executable
        File ffmpeg = new File(ffmpegPath);
        if (!ffmpeg.exists() || !ffmpeg.canExecute()) {
            throw new ServiceException("FFmpeg executable not found or not executable: " + ffmpegPath);
        }

        // Check FFprobe executable
        File ffprobe = new File(ffprobePath);
        if (!ffprobe.exists() || !ffprobe.canExecute()) {
            throw new ServiceException("FFprobe executable not found or not executable: " + ffprobePath);
        }

        // Validate HLS settings
        if (hlsTime <= 0) {
            throw new ServiceException("Invalid HLS segment duration: " + hlsTime);
        }

        if (hlsListSize < 0) {
            throw new ServiceException("Invalid HLS playlist size: " + hlsListSize);
        }

        // Validate video settings
        if (videoBitrates == null || videoBitrates.length == 0) {
            throw new ServiceException("Video bitrates not configured");
        }

        if (videoResolutions == null || videoResolutions.length == 0) {
            throw new ServiceException("Video resolutions not configured");
        }

        if (videoBitrates.length != videoResolutions.length) {
            throw new ServiceException("Number of bitrates does not match number of resolutions");
        }
    }

    /**
     * Create necessary directories
     */
    private void createDirectories() {
        try {
            // Create output directory
            Path outputDir = Paths.get(outputPath);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Create temp directory
            Path tempDir = Paths.get(tempPath);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
        } catch (Exception e) {
            throw new ServiceException("Failed to create FFmpeg directories: " + e.getMessage());
        }
    }
} 