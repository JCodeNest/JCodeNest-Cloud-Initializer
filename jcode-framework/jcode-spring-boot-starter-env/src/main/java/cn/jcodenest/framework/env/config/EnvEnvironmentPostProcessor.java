package cn.jcodenest.framework.env.config;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.util.collection.SetUtils;
import cn.jcodenest.framework.env.core.util.EnvUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

import static cn.jcodenest.framework.env.core.util.EnvUtils.HOST_NAME_VALUE;

/**
 * 多环境的 {@link EnvEnvironmentPostProcessor} 实现类
 * 将 jcode.env.tag 设置到 nacos 等组件对应的 tag 配置项，当且仅当它们不存在时
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class EnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 需要设置 tag 的配置项
     */
    private static final Set<String> TARGET_TAG_KEYS = SetUtils.asSet(
            // Nacos 注册中心
            "spring.cloud.nacos.discovery.metadata.tag"
            // ... 待补充
    );

    /**
     * 处理 Spring 环境配置
     *
     * @param environment Spring 的环境配置对象
     * @param application Spring 应用对象
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 设置 ${HOST_NAME} 兜底的环境变量
        String hostNameKey = StrUtil.subBetween(HOST_NAME_VALUE, "{", "}");
        if (!environment.containsProperty(hostNameKey)) {
            environment.getSystemProperties().put(hostNameKey, EnvUtils.getHostName());
        }

        // 如果没有 jcode.env.tag 配置项，则不进行配置项的修改
        String tag = EnvUtils.getTag(environment);
        if (StrUtil.isEmpty(tag)) {
            return;
        }

        // 需要修改的配置项
        for (String targetTagKey : TARGET_TAG_KEYS) {
            String targetTagValue = environment.getProperty(targetTagKey);
            if (StrUtil.isNotEmpty(targetTagValue)) {
                continue;
            }
            environment.getSystemProperties().put(targetTagKey, tag);
        }
    }
}
