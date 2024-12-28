package org.dromara.video.config;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.service.IVideoTranscodeService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Redis Message Listener Configuration
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final IVideoTranscodeService transcodeService;

    @PostConstruct
    public void init() {
        log.info("Initializing Redis message listener for channel: {}", RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL);
        
        // Subscribe to video transcode channel using Redisson
        RedisUtils.subscribe(RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL, VideoTranscodeMessage.class, message -> {
            try {
                log.info("Received transcode message on channel {}: {}", 
                    RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL, 
                    JSON.toJSONString(message));
                transcodeService.handleTranscodeMessage(message);
            } catch (Exception e) {
                log.error("Error processing transcode message: ", e);
            }
        });
        
        log.info("Successfully subscribed to Redis channel: {}", RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL);
    }
}
