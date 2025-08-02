package cn.jcodenest.framework.tenant.core.aop;

import cn.jcodenest.framework.common.util.spring.SpringExpressionUtils;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import cn.jcodenest.framework.tenant.core.util.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 忽略多租户的 Aspect，基于 {@link TenantIgnore} 注解实现，用于一些全局的逻辑
 * 例如一个定时任务，读取所有数据，进行处理。又例如，读取所有数据，进行缓存。
 * 整体逻辑的实现，和 {@link TenantUtils#executeIgnore(Runnable)} 需要保持一致
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@Aspect
public class TenantIgnoreAspect {

    /**
     * 忽略多租户
     *
     * @param joinPoint 切点
     * @param tenantIgnore 忽略多租户注解
     * @return 忽略多租户结果
     * @throws Throwable 错误
     */
    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
        Boolean oldIgnore = TenantContextHolder.isIgnore();
        try {
            // 计算条件，满足的情况下，才进行忽略
            Object enable = SpringExpressionUtils.parseExpression(tenantIgnore.enable());
            if (Boolean.TRUE.equals(enable)) {
                TenantContextHolder.setIgnore(true);
            }

            // 执行逻辑
            return joinPoint.proceed();
        } finally {
            TenantContextHolder.setIgnore(oldIgnore);
        }
    }
}
