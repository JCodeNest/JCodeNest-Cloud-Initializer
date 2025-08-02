package cn.jcodenest.framework.tenant.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.jcodenest.framework.common.biz.system.tenant.TenantCommonApi;
import cn.jcodenest.framework.common.constants.WebFilterOrderConstants;
import cn.jcodenest.framework.mybatis.core.util.MyBatisUtils;
import cn.jcodenest.framework.redis.config.properties.JCodeCacheProperties;
import cn.jcodenest.framework.security.core.service.SecurityFrameworkService;
import cn.jcodenest.framework.tenant.config.properties.TenantProperties;
import cn.jcodenest.framework.tenant.core.aop.TenantIgnore;
import cn.jcodenest.framework.tenant.core.aop.TenantIgnoreAspect;
import cn.jcodenest.framework.tenant.core.db.TenantDatabaseInterceptor;
import cn.jcodenest.framework.tenant.core.job.TenantJobAspect;
import cn.jcodenest.framework.tenant.core.mq.rabbitmq.TenantRabbitMQInitializer;
import cn.jcodenest.framework.tenant.core.mq.redis.TenantRedisMessageInterceptor;
import cn.jcodenest.framework.tenant.core.mq.rocketmq.TenantRocketMQInitializer;
import cn.jcodenest.framework.tenant.core.redis.TenantRedisCacheManager;
import cn.jcodenest.framework.tenant.core.security.TenantSecurityWebFilter;
import cn.jcodenest.framework.tenant.core.service.TenantFrameworkService;
import cn.jcodenest.framework.tenant.core.service.impl.TenantFrameworkServiceImpl;
import cn.jcodenest.framework.tenant.core.web.TenantContextWebFilter;
import cn.jcodenest.framework.tenant.core.web.TenantVisitContextInterceptor;
import cn.jcodenest.framework.web.config.WebProperties;
import cn.jcodenest.framework.web.core.handler.GlobalExceptionHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Map;
import java.util.Objects;

import static cn.jcodenest.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 多租户自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
// 允许使用 jcode.tenant.enable=false 禁用多租户
@ConditionalOnProperty(prefix = "jcode.tenant", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(TenantProperties.class)
public class JCodeTenantAutoConfiguration {

    /**
     * 应用上下文
     */
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 多租户的 API Bean
     *
     * @param tenantApi 多租户 RPC API
     * @return TenantFrameworkService Bean
     */
    @Bean
    public TenantFrameworkService tenantFrameworkService(TenantCommonApi tenantApi) {
        try {
            TenantCommonApi tenantApiImpl = SpringUtil.getBean("tenantApiImpl", TenantCommonApi.class);
            if (tenantApiImpl != null) {
                tenantApi = tenantApiImpl;
            }
        } catch (Exception ignored) {
            // ignored
        }
        return new TenantFrameworkServiceImpl(tenantApi);
    }

    // ========== AOP ==========

    /**
     * 忽略多租户的 Aspect Bean
     *
     * @return TenantIgnoreAspect Bean
     */
    @Bean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    // ========== DB ==========

    /**
     * 多租户 MyBatis 的拦截器
     *
     * @param properties  多租户配置
     * @param interceptor 多租户拦截器
     * @return TenantLineInnerInterceptor Bean
     */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties properties, MybatisPlusInterceptor interceptor) {
        TenantLineInnerInterceptor inner = new TenantLineInnerInterceptor(new TenantDatabaseInterceptor(properties));
        // 添加到 interceptor 中
        // 需要加在首个，主要是为了在分页插件前面，这个是 MyBatis Plus 的规定
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    // ========== WEB ==========

    /**
     * 创建 TenantContextWebFilter Bean
     *
     * @param tenantProperties 多租户配置
     * @return TenantContextWebFilter Bean
     */
    @Bean
    public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter(TenantProperties tenantProperties) {
        FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantContextWebFilter());
        registrationBean.setOrder(WebFilterOrderConstants.TENANT_CONTEXT_FILTER);
        addIgnoreUrls(tenantProperties);
        return registrationBean;
    }

    /**
     * 如果 Controller 接口上，有 {@link TenantIgnore} 注解，那么添加到忽略的 URL 中
     *
     * @param tenantProperties 租户配置
     */
    private void addIgnoreUrls(TenantProperties tenantProperties) {
        // 获得接口对应的 HandlerMethod 集合
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();

        // 获得有 @TenantIgnore 注解的接口
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(TenantIgnore.class)) {
                continue;
            }

            // 添加到忽略的 URL 中
            if (entry.getKey().getPatternsCondition() != null) {
                tenantProperties.getIgnoreUrls().addAll(entry.getKey().getPatternsCondition().getPatterns());
            }
            if (entry.getKey().getPathPatternsCondition() != null) {
                tenantProperties.getIgnoreUrls().addAll(convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
            }
        }
    }

    /**
     * 创建 TenantSecurityWebFilter Bean
     *
     * @param tenantProperties         多租户配置
     * @param securityFrameworkService 权限框架服务
     * @return TenantSecurityWebFilter Bean
     */
    @Bean
    public TenantVisitContextInterceptor tenantVisitContextInterceptor(TenantProperties tenantProperties, SecurityFrameworkService securityFrameworkService) {
        return new TenantVisitContextInterceptor(tenantProperties, securityFrameworkService);
    }

    /**
     * 创建 TenantVisitContextInterceptor Bean
     *
     * @param tenantProperties              多租户配置
     * @param tenantVisitContextInterceptor 多租户访问拦截器
     * @return WebMvcConfigurer Bean
     */
    @Bean
    public WebMvcConfigurer tenantWebMvcConfigurer(TenantProperties tenantProperties, TenantVisitContextInterceptor tenantVisitContextInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(tenantVisitContextInterceptor)
                        .excludePathPatterns(tenantProperties.getIgnoreVisitUrls().toArray(new String[0]));
            }
        };
    }

    // ========== Security ==========

    /**
     * 创建 TenantSecurityWebFilter Bean
     *
     * @param tenantProperties       多租户配置
     * @param webProperties          Spring Boot Web 配置
     * @param globalExceptionHandler 全局异常处理器
     * @param tenantFrameworkService 多租户服务
     * @return TenantSecurityWebFilter Bean
     */
    @Bean
    public FilterRegistrationBean<TenantSecurityWebFilter> tenantSecurityWebFilter(TenantProperties tenantProperties,
                                                                                   WebProperties webProperties,
                                                                                   GlobalExceptionHandler globalExceptionHandler,
                                                                                   TenantFrameworkService tenantFrameworkService) {
        FilterRegistrationBean<TenantSecurityWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantSecurityWebFilter(tenantProperties, webProperties, globalExceptionHandler, tenantFrameworkService));
        registrationBean.setOrder(WebFilterOrderConstants.TENANT_SECURITY_FILTER);
        return registrationBean;
    }

    // ========== Job ==========

    /**
     * 创建 TenantJobAspect Bean
     *
     * @param tenantFrameworkService 多租户 Service Bean
     * @return TenantJobAspect Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.xxl.job.core.handler.annotation.XxlJob")
    public TenantJobAspect tenantJobAspect(TenantFrameworkService tenantFrameworkService) {
        return new TenantJobAspect(tenantFrameworkService);
    }

    // ========== MQ ==========

    /**
     * 多租户 Redis 消息队列的配置类
     * <p>
     * 问题：为什么要单独一个配置类呢？
     * 回答：如果直接把 TenantRedisMessageInterceptor Bean 的初始化放外面，会报 RedisMessageInterceptor 类不存在的错误
     */
    @Configuration
    @ConditionalOnClass(name = "cn.jcodenest.framework.mq.redis.core.RedisMQTemplate")
    public static class TenantRedisMQAutoConfiguration {
        @Bean
        public TenantRedisMessageInterceptor tenantRedisMessageInterceptor() {
            return new TenantRedisMessageInterceptor();
        }
    }

    /**
     * 创建 TenantRabbitMQInitializer Bean
     *
     * @return TenantRabbitMQInitializer Bean
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.core.RabbitTemplate")
    public TenantRabbitMQInitializer tenantRabbitMQInitializer() {
        return new TenantRabbitMQInitializer();
    }

    /**
     * 创建 TenantRocketMQInitializer Bean
     *
     * @return TenantRocketMQInitializer Bean
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
    public TenantRocketMQInitializer tenantRocketMQInitializer() {
        return new TenantRocketMQInitializer();
    }

    // ========== Redis ==========

    /**
     * 创建 RedisCacheManager Bean
     *
     * @param redisTemplate Redis 缓存配置
     * @param redisCacheConfiguration Redis 缓存配置
     * @param jCodeCacheProperties 缓存属性
     * @param tenantProperties 多租户属性
     * @return RedisCacheManager Bean
     */
    @Bean
    @Primary // 引入租户时 tenantRedisCacheManager 为主 Bean
    public RedisCacheManager tenantRedisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                                     RedisCacheConfiguration redisCacheConfiguration,
                                                     JCodeCacheProperties jCodeCacheProperties,
                                                     TenantProperties tenantProperties) {
        // 创建 RedisCacheWriter 对象
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(jCodeCacheProperties.getRedisScanBatchSize()));

        // 创建 TenantRedisCacheManager 对象
        return new TenantRedisCacheManager(cacheWriter, redisCacheConfiguration, tenantProperties.getIgnoreCaches());
    }
}
