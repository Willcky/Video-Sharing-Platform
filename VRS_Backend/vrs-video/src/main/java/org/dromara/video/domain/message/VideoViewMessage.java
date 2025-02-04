package org.dromara.video.domain.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 视频播放量消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
