package org.dromara.video.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.domain.message.VideoViewMessage;
import org.dromara.video.service.impl.VideoViewCountServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 视频播放量消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoViewCountConsumer {

    private final VideoViewCountServiceImpl videoViewCountService;

    /**
     * Redis消息去重key前缀
     */
    private static final String MESSAGE_DEDUP_KEY = "video:view:msg:";

    @KafkaListener(topics = "video-view-count", groupId = "video-view-count-group")
    public void consumeViewCount(VideoViewMessage message, Acknowledgment ack) {
        try {
            String messageKey = MESSAGE_DEDUP_KEY + message.getMessageId();
            // 使用setObjectIfAbsent设置消息去重标记，有效期24小时
            Boolean setSuccess = RedisUtils.setObjectIfAbsent(messageKey, "1", Duration.ofHours(24));
            
            if (Boolean.TRUE.equals(setSuccess)) {
                // 只有第一次处理时才更新计数
                videoViewCountService.consumeViewCount(message, ack);
                log.debug("处理视频播放量消息：messageId={}", message.getMessageId());
            } else {
                log.debug("消息已处理，跳过：messageId={}", message.getMessageId());
                // 消息已处理过，直接确认
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("处理视频播放量消息失败", e);
            // 不确认消息，让Kafka进行重试
        }
    }
} 