package cn.jcodenest.framework.idempotent.core.aop;

import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.framework.common.util.collection.CollectionUtils;
import cn.jcodenest.framework.idempotent.core.annotation.Idempotent;
import cn.jcodenest.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import cn.jcodenest.framework.idempotent.core.redis.IdempotentRedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 拦截声明了 {@link Idempotent} 注解的方法，实现幂等操作
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
public class IdempotentAspect {

    /**
     * IdempotentKeyResolver 集合
     */
    private final Map<Class<? extends IdempotentKeyResolver>, IdempotentKeyResolver> keyResolvers;

    /**
     * 幂等 Redis DAO
     */
    private final IdempotentRedisDAO idempotentRedisDAO;

    /**
     * 构造函数
     *
     * @param keyResolvers IdempotentKeyResolver 集合
     * @param idempotentRedisDAO 幂等 Redis DAO
     */
    public IdempotentAspect(List<IdempotentKeyResolver> keyResolvers, IdempotentRedisDAO idempotentRedisDAO) {
        this.keyResolvers = CollectionUtils.convertMap(keyResolvers, IdempotentKeyResolver::getClass);
        this.idempotentRedisDAO = idempotentRedisDAO;
    }

    /**
     * 切面方法
     *
     * @param joinPoint   AOP 切面
     * @param idempotent  幂等注解
     */
    @Around(value = "@annotation(idempotent)")
    public Object aroundPointCut(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 获取 IdempotentKeyResolver
        IdempotentKeyResolver keyResolver = keyResolvers.get(idempotent.keyResolver());
        Assert.notNull(keyResolver, "找不到对应的 IdempotentKeyResolver");

        // 解析 Key
        String key = keyResolver.resolver(joinPoint, idempotent);

        // 锁定 Key，锁定失败抛出异常
        boolean success = idempotentRedisDAO.setIfAbsent(key, idempotent.timeout(), idempotent.timeUnit());
        if (!success) {
            log.info("[aroundPointCut][方法({}) 参数({}) 存在重复请求]", joinPoint.getSignature().toString(), joinPoint.getArgs());
            throw new ServiceException(GlobalErrorCodeConstants.REPEATED_REQUESTS.getCode(), idempotent.message());
        }

        // 执行逻辑
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 异常时删除 Key
            // 参考美团 GTIS 思路：https://tech.meituan.com/2016/09/29/distributed-system-mutually-exclusive-idempotence-cerberus-gtis.html
            if (idempotent.deleteKeyWhenException()) {
                idempotentRedisDAO.delete(key);
            }
            throw throwable;
        }
    }
}
