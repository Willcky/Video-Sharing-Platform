package org.dromara.video.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.video.domain.entity.SysVideo;
import org.dromara.video.domain.message.VideoViewMessage;
import org.dromara.video.mapper.SysVideoMapper;
import org.dromara.video.service.IVideoViewCountService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 视频播放量统计服务实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoViewCountServiceImpl implements IVideoViewCountService {

    private final SysVideoMapper videoMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedissonClient redissonClient;

    /**
     * Redis中视频播放量的key前缀
     */
    private static final String VIDEO_VIEW_COUNT_KEY = "video:view:count:";

    /**
     * Redis锁的key前缀
     */
    private static final String VIDEO_VIEW_LOCK_KEY = "video:view:lock:";

    /**
     * Kafka主题
     */
    private static final String KAFKA_TOPIC = "video-view-count";

    /**
     * 内存中的播放量增量 - 使用synchronized保证线程安全
     */
    private static final Map<Long, Long> VIEW_COUNT_MAP = new HashMap<>();

    /**
     * 用于同步的对象锁
     */
    private final Object mapLock = new Object();

    @Override
    public void increaseViewCount(Long videoId) {
        try {
            // 先尝试从Redis获取播放量
            String redisKey = VIDEO_VIEW_COUNT_KEY + videoId;
            Object viewCountObj = RedisUtils.getCacheObject(redisKey);
            Long viewCount = null;
            if (viewCountObj != null) {
                if (viewCountObj instanceof Integer) {
                    viewCount = ((Integer) viewCountObj).longValue();
                } else if (viewCountObj instanceof Long) {
                    viewCount = (Long) viewCountObj;
                }
            }

            if (viewCount == null) {
                // 如果Redis中没有，需要从数据库加载
                String lockKey = VIDEO_VIEW_LOCK_KEY + videoId;
                RLock rLock = redissonClient.getLock(lockKey);

                try {
                    // 尝试获取锁，最多等待3秒，10秒后自动释放
                    if (rLock.tryLock(3, 10, TimeUnit.SECONDS)) {
                        try {
                            // 再次检查Redis中是否已有值（双重检查）
                            viewCount = RedisUtils.getCacheObject(redisKey);
                            if (viewCount == null) {
                                // 从数据库加载播放量
                                viewCount = videoMapper.selectViewCountById(videoId);
                                if (viewCount == null) {
                                    throw new ServiceException("视频不存在");
                                }
                                // 设置到Redis中
                                RedisUtils.setCacheObject(redisKey, viewCount);
                            }
                        } finally {
                            // 释放锁
                            if (rLock.isHeldByCurrentThread()) {
                                rLock.unlock();
                            }
                        }
                    } else {
                        throw new ServiceException("获取锁超时");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ServiceException("获取锁被中断");
                }
            }

            // 增加Redis中的播放量
            RedisUtils.incrAtomicValue(redisKey);

            // 生成唯一消息ID
            String messageId = IdUtil.fastSimpleUUID();
            // 发送消息到Kafka
            VideoViewMessage message = new VideoViewMessage(messageId, videoId, 1L);
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KAFKA_TOPIC, messageId, message);

            // 添加回调处理
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("发送视频播放量消息成功：{}", messageId);
                } else {
                    log.error("发送视频播放量消息失败：{}", messageId, ex);
                }
            });

        } catch (Exception e) {
            log.error("增加视频播放量失败", e);
            throw new ServiceException("增加视频播放量失败");
        }
    }

    /**
     * 消费Kafka消息，更新内存中的播放量
     * 使用synchronized保证线程安全
     */
    public void consumeViewCount(VideoViewMessage message, Acknowledgment ack) {
        try {
            synchronized (mapLock) {
                // 直接更新计数
                VIEW_COUNT_MAP.merge(message.getVideoId(), message.getIncrement(), Long::sum);
                log.debug("更新视频播放量：videoId={}, increment={}", message.getVideoId(), message.getIncrement());
            }
            // 消息处理成功后进行确认
            if (ack != null) {
                ack.acknowledge();
                log.debug("确认消费消息：messageId={}", message.getMessageId());
            }
        } catch (Exception e) {
            log.error("处理视频播放量消息失败：messageId={}", message.getMessageId(), e);
            throw e; // 抛出异常，让Kafka进行重试
        }
    }

    /**
     * 定时任务：每10秒将内存中的播放量写入数据库
     * 使用synchronized保证线程安全
     */
    @Scheduled(fixedRate = 10000)
    public void syncViewCount2Db() {
        Map<Long, Long> currentCounts;
        synchronized (mapLock) {
            if (VIEW_COUNT_MAP.isEmpty()) {
                return;
            }
            // 在同步块内复制和清空map
            currentCounts = new HashMap<>(VIEW_COUNT_MAP);
            VIEW_COUNT_MAP.clear();
        }

        try {
            // 使用批量更新SQL
            boolean success = videoMapper.batchIncrementViewCounts(currentCounts) > 0;
            if (success) {
                log.debug("成功更新{}个视频的播放量", currentCounts.size());
            } else {
                log.warn("批量更新视频播放量失败");
                // 更新失败，将计数加回map中
                synchronized (mapLock) {
                    currentCounts.forEach((videoId, increment) ->
                        VIEW_COUNT_MAP.merge(videoId, increment, Long::sum));
                }
            }
        } catch (Exception e) {
            log.error("批量更新视频播放量失败", e);
            // 如果批量更新失败，将所有计数加回map中
            synchronized (mapLock) {
                currentCounts.forEach((videoId, increment) ->
                    VIEW_COUNT_MAP.merge(videoId, increment, Long::sum));
            }
        }
    }
}
