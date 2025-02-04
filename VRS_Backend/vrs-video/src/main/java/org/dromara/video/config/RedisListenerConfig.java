package org.dromara.video.config;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.domain.message.VideoUploadMessage;
import org.dromara.video.service.IVideoTranscodeService;
import org.dromara.video.service.IVideoUploadService;
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

    private final IVideoUploadService uploadService;

    @PostConstruct
    public void init() {

        // Subscribe to video transcode channel using Redisson
//        RedisUtils.subscribe(RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL, VideoTranscodeMessage.class, message -> {
//            try {
//                log.info("Received transcode message on channel {}: {}",
//                    RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL,
//                    JSON.toJSONString(message));
//                transcodeService.handleTranscodeMessage(message);
//            } catch (Exception e) {
//                log.error("Error processing transcode message: ", e);
//            }
//        });

//        log.info("Successfully subscribed to Redis channel: {}", RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL);

//        RedisUtils.subscribe(RedisChannelConfig.VIDEO_UPLOAD_CHANNEL, VideoUploadMessage.class, message -> {
//            try {
//                log.info("Received upload message on channel {}: {}",
//                    RedisChannelConfig.VIDEO_UPLOAD_CHANNEL,
//                    JSON.toJSONString(message));
//                uploadService.handleUploadMessage(message);
//            } catch (Exception e) {
//                log.error("Error processing upload message: ", e);
//            }
//        });

//        log.info("Successfully subscribed to Redis channel: {}", RedisChannelConfig.VIDEO_UPLOAD_CHANNEL);
    }


}
