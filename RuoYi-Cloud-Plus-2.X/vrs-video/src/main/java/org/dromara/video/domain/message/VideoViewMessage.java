package org.dromara.video.domain.message;

import lombok.Data;

/**
 * 视频播放量消息
 */
@Data
public class VideoViewMessage {

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 增加的播放量
     */
    private Long increment;

    public VideoViewMessage(Long videoId, Long increment) {
        this.videoId = videoId;
        this.increment = increment;
    }

    public VideoViewMessage(String messageId, Long videoId, Long increment) {
        this.messageId = messageId;
        this.videoId = videoId;
        this.increment = increment;
    }
} 