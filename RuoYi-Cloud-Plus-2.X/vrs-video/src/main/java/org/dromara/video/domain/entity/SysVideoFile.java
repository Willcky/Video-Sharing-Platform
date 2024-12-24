package org.dromara.video.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Video File Entity
 */
@Data
@TableName("sys_video_file")
public class SysVideoFile {

    /**
     * File ID
     */
    @TableId
    private Long id;

    /**
     * Video ID
     */
    private Long videoId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * File name
     */
    private String fileName;

    /**
     * File URL
     */
    private String filePath;

    /**
     * File size in bytes
     */
    private Long fileSize;

    /**
     * File type (mp4/webm etc)
     */
    private String fileType;

    /**
     * Resolution (1080p/720p etc)
     */
    private String resolution;

    /**
     * Bitrate in Kbps
     */
    private Integer bitrate;

    /**
     * Duration in seconds
     */
    private Integer duration;

    /**
     * Storage type (0:local 1:AWS)
     */
    private Integer storageType;

    /**
     * Status (0:published 1:reviewing 2:offline 3:transcoding 4:transcode_failed 5:uploaded 6:pending_transcode)
     */
    private Integer status;

    /**
     * Create time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;
} 