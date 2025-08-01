package cn.jcodenest.framework.common.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Cache 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheUtils {

    /**
     * 构建异步刷新的 LoadingCache 对象
     *
     * <p>
     * 注意：如果你的缓存和 ThreadLocal 有关系, 
     *      要么自己处理 ThreadLocal 的传递
     *      要么使用 {@link #buildCache(Duration, CacheLoader)} 方法
     * </p>
     *
     * <p>
     * 简单理解：
     *  1. 和 “人” 相关的, 使用 {@link #buildCache(Duration, CacheLoader)} 方法
     *  2. 和 “全局”、“系统” 相关的, 使用当前缓存方法
     * </p>
     *
     * @param duration 过期时间
     * @param loader   CacheLoader 对象
     * @return LoadingCache 对象
     */
    public static <K, V> LoadingCache<K, V> buildAsyncReloadingCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder()
                // 只阻塞当前数据加载线程, 其他线程返回旧值
                .refreshAfterWrite(duration)
                // 通过 asyncReloading 实现全异步加载, 包括 refreshAfterWrite 被阻塞的加载线程
                .build(CacheLoader.asyncReloading(loader, Executors.newCachedThreadPool()));
    }

    /**
     * 构建同步刷新的 LoadingCache 对象
     *
     * @param duration 过期时间
     * @param loader   CacheLoader 对象
     * @return LoadingCache 对象
     */
    public static <K, V> LoadingCache<K, V> buildCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder().refreshAfterWrite(duration).build(loader);
    }
}
