package org.dromara.video.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisStreamConfig {

    /**
     * Stream key for video processing events
     */
    public static final String VIDEO_PROCESS_STREAM = "video:process:stream";

    /**
     * Consumer group name
     */
    public static final String CONSUMER_GROUP = "video-process-group";

    /**
     * Consumer name prefix
     */
    public static final String CONSUMER_PREFIX = "consumer-";

    /**
     * Event types
     */
    public static class EventType {
        public static final String TRANSCODE_COMPLETE = "TRANSCODE_COMPLETE";
        public static final String UPLOAD_COMPLETE = "UPLOAD_COMPLETE";
    }
} 