package cn.jcodenest.framework.test.core.ut;

import cn.hutool.extra.spring.SpringUtil;
import cn.jcodenest.framework.redis.config.JCodeRedisAutoConfiguration;
import cn.jcodenest.framework.test.config.RedisTestConfiguration;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 依赖内存 Redis 的单元测试
 * 相比 {@link BaseDbUnitTest} 来说，从内存 DB 改成了内存 Redis
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BaseRedisUnitTest.Application.class)
@ActiveProfiles("unit-test") // 设置使用 application-unit-test 配置文件
public class BaseRedisUnitTest {

    @Import({
            // Redis 配置类
            RedisTestConfiguration.class,       // Redis 测试配置类，用于启动 RedisServer
            RedisAutoConfiguration.class,       // Spring Redis 自动配置类
            JCodeRedisAutoConfiguration.class,  // 自己的 Redis 配置类
            RedissonAutoConfiguration.class,    // Redisson 自动配置类

            // 其它配置类
            SpringUtil.class
    })
    public static class Application {
    }
}
