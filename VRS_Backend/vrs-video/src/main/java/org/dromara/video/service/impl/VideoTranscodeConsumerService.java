package org.dromara.video.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.video.service.IVideoTranscodeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Video transcode consumer service
 * Runs in background and processes transcode messages from queue
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoTranscodeConsumerService implements CommandLineRunner {

    private final IVideoTranscodeService transcodeService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void run(String... args) {
        log.info("Starting video transcode consumer...");
        executorService.submit(() -> {
            try {
                transcodeService.handleTranscodeMessage();
            } catch (Exception e) {
                log.error("Error in transcode consumer: ", e);
            }
        });
    }
} 