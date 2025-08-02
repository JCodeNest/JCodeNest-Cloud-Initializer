package cn.jcodenest.framework.lock4j.core.constants;

/**
 * Lock4j Redis Key 常量
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface Lock4jRedisKeyConstants {

    /**
     * 分布式锁
     * <p>
     * KEY      格式：lock4j:%s // 参数来自 DefaultLockKeyBuilder 类
     * VALUE 数据格式：HASH      // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
     * 过期时间：不固定
     */
    String LOCK4J = "lock4j:%s";
}
