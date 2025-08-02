package cn.jcodenest.framework.tracer.config;

import cn.jcodenest.framework.common.constants.WebFilterOrderConstants;
import cn.jcodenest.framework.tracer.config.properties.TracerProperties;
import cn.jcodenest.framework.tracer.core.aop.BizTraceAspect;
import cn.jcodenest.framework.tracer.core.filter.TraceFilter;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Tracer 配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnClass(name = {
        "org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer",
        "io.opentracing.Tracer"
})
@EnableConfigurationProperties(TracerProperties.class)
@ConditionalOnProperty(prefix = "jcode.tracer", value = "enable", matchIfMissing = true)
public class JCodeTracerAutoConfiguration {

    /**
     * 创建 TracerProperties Bean
     */
    @Bean
    public TracerProperties bizTracerProperties() {
        return new TracerProperties();
    }

    /**
     * 创建 BizTraceAspect Bean
     */
    @Bean
    public BizTraceAspect bizTracingAop() {
        return new BizTraceAspect(tracer());
    }

    /**
     * 创建 Tracer Bean
     */
    @Bean
    public Tracer tracer() {
        // 创建 SkywalkingTracer 对象
        SkywalkingTracer tracer = new SkywalkingTracer();
        // 设置为 GlobalTracer 的追踪器
        GlobalTracer.registerIfAbsent(tracer);
        return tracer;
    }

    /**
     * 创建 TraceFilter 过滤器，响应 header 设置 traceId
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter());
        registrationBean.setOrder(WebFilterOrderConstants.TRACE_FILTER);
        return registrationBean;
    }
}
