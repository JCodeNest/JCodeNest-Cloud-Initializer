package cn.jcodenest.framework.tracer.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Metrics 配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnClass({MeterRegistryCustomizer.class})
// 允许使用 jcode.metrics.enable=false 禁用 Metrics
@ConditionalOnProperty(prefix = "jcode.metrics", value = "enable", matchIfMissing = true)
public class JCodeMetricsAutoConfiguration {

    /**
     * 配置 Metrics 的公共标签
     *
     * @param applicationName 应用名称
     * @return MeterRegistryCustomizer Bean
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${spring.application.name}") String applicationName) {
        return registry -> registry.config().commonTags("application", applicationName);
    }
}
