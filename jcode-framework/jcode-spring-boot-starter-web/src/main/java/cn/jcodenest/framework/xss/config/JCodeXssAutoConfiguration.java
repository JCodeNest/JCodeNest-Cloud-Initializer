package cn.jcodenest.framework.xss.config;

import cn.jcodenest.framework.common.constants.WebFilterOrderConstants;
import cn.jcodenest.framework.xss.core.clean.JsoupXssCleaner;
import cn.jcodenest.framework.xss.core.clean.XssCleaner;
import cn.jcodenest.framework.xss.core.filter.XssFilter;
import cn.jcodenest.framework.xss.core.json.XssStringJsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static cn.jcodenest.framework.web.config.JCodeWebAutoConfiguration.createFilterBean;

/**
 * XSS 自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class)
@ConditionalOnProperty(prefix = "jcode.xss", name = "enable", havingValue = "true", matchIfMissing = true)
public class JCodeXssAutoConfiguration implements WebMvcConfigurer {

    /**
     * Xss 清理器
     *
     * @return XssCleaner
     */
    @Bean
    @ConditionalOnMissingBean(XssCleaner.class)
    public XssCleaner xssCleaner() {
        return new JsoupXssCleaner();
    }

    /**
     * 注册 Jackson 的序列化器, 用于处理 json 类型参数的 xss 过滤
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean(name = "xssJacksonCustomizer")
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnProperty(value = "jcode.xss.enable", havingValue = "true")
    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        // 在反序列化时进行 xss 过滤, 可以替换使用 XssStringJsonSerializer 在序列化时进行处理
        return builder -> builder.deserializerByType(String.class, new XssStringJsonDeserializer(properties, pathMatcher, xssCleaner));
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题
     */
    @Bean
    @ConditionalOnBean(XssCleaner.class)
    public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher, XssCleaner xssCleaner) {
        return createFilterBean(new XssFilter(properties, pathMatcher, xssCleaner), WebFilterOrderConstants.XSS_FILTER);
    }
}
