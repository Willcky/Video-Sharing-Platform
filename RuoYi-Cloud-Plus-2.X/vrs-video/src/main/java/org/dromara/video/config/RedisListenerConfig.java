package org.dromara.video.config;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.video.domain.message.VideoTranscodeMessage;
import org.dromara.video.service.IVideoTranscodeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis Message Listener Configuration
 */
@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final IVideoTranscodeService transcodeService;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Add video transcode channel listener
        container.addMessageListener(new VideoTranscodeListener(transcodeService),
            new ChannelTopic(RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL));

        return container;
    }

    /**
     * Video Transcode Message Listener
     */
    @Slf4j
    @RequiredArgsConstructor
    static class VideoTranscodeListener implements MessageListener {

        private final IVideoTranscodeService transcodeService;

        @Override
        public void onMessage(Message message, byte[] pattern) {
            String channel = new String(pattern);
            if (!RedisChannelConfig.VIDEO_TRANSCODE_CHANNEL.equals(channel)) {
                return;
            }

            try {
                String messageBody = new String(message.getBody());
                VideoTranscodeMessage transcodeMessage = JSON.parseObject(messageBody, VideoTranscodeMessage.class);
                transcodeService.handleTranscodeMessage(transcodeMessage);
            } catch (Exception e) {
                log.error("Error processing transcode message: ", e);
            }
        }
    }
}
