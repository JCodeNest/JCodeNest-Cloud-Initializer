package cn.jcodenest.framework.mq.redis.core.job;

import cn.jcodenest.framework.mq.redis.core.RedisMQTemplate;
import cn.jcodenest.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * JOB：Redis Stream 消息清理任务，用于定期清理已消费的消息，防止内存占用过大
 *
 * @author JCodeNest
 * @version 1.0.0
 * @see <a href="https://www.cnblogs.com/nanxiang/p/16179519.html">记一次 redis stream 数据类型内存不释放问题</a>
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@AllArgsConstructor
public class RedisStreamMessageCleanupJob {

    /**
     * 锁的 key，用于防止并发执行
     */
    private static final String LOCK_KEY = "redis:stream:message-cleanup:lock";

    /**
     * 保留的消息数量，默认保留最近 10000 条消息
     */
    private static final long MAX_COUNT = 10000;

    /**
     * 监听器列表
     */
    private final List<AbstractRedisStreamMessageListener<?>> listeners;

    /**
     * Redis MQ 模板
     */
    private final RedisMQTemplate redisTemplate;

    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 每小时执行一次清理任务
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanup() {
        // 获取锁
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                // 执行清理逻辑
                execute();
            } catch (Exception ex) {
                log.error("[cleanup][执行异常]", ex);
            } finally {
                // 解锁
                lock.unlock();
            }
        }
    }

    /**
     * 执行清理逻辑
     */
    private void execute() {
        StreamOperations<String, Object, Object> ops = redisTemplate.getRedisTemplate().opsForStream();
        listeners.forEach(listener -> {
            try {
                // 使用 XTRIM 命令清理消息，只保留最近的 MAX_LEN 条消息
                Long trimCount = ops.trim(listener.getStreamKey(), MAX_COUNT, true);
                if (trimCount != null && trimCount > 0) {
                    log.info("[execute][Stream({}) 清理消息数量({})]", listener.getStreamKey(), trimCount);
                }
            } catch (Exception ex) {
                log.error("[execute][Stream({}) 清理异常]", listener.getStreamKey(), ex);
            }
        });
    }
}
