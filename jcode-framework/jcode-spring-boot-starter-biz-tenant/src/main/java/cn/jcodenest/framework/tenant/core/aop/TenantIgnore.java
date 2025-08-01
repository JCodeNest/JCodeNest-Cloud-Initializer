package cn.jcodenest.framework.tenant.core.aop;

import cn.jcodenest.framework.tenant.config.properties.TenantProperties;

import java.lang.annotation.*;

/**
 * 忽略租户，标记指定方法不进行租户的自动过滤
 *
 * <p>
 * 注意，只有 DB 的场景会过滤，其它场景暂时不过滤：
 * 1. Redis 场景：因为是基于 Key 实现多租户的能力，所以忽略没有意义，不像 DB 是一个 column 实现的
 * 2. MQ 场景：有点难以抉择，目前可以通过 Consumer 手动在消费的方法上，添加 @TenantIgnore 进行忽略
 * </p>
 *
 * <p>
 * 特殊：
 * 1. 如果添加到 Controller 类上，则该 URL 自动添加到 {@link TenantProperties#getIgnoreUrls()} 中
 * 2. 如果添加到 DO 实体类上，则它对应的表名 “相当于” 自动添加到 {@link TenantProperties#getIgnoreTables()} 中
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TenantIgnore {

    /**
     * 是否开启忽略租户，默认为 true 开启
     * <p>
     * 支持 Spring EL 表达式，如果返回 true 则满足条件，进行租户的忽略
     */
    String enable() default "true";
}
