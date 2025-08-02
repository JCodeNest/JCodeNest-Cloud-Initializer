package cn.jcodenest.framework.tenant.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.tenant.core.service.TenantFrameworkService;
import cn.jcodenest.framework.tenant.core.util.TenantUtils;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多租户 JobHandler AOP，任务执行时会按照租户逐个执行 Job 的逻辑
 *
 * <p>
 * 注意，需要保证 JobHandler 的幂等性。因为 Job 因为某个租户执行失败重试时，之前执行成功的租户也会再次执行。
 * </p>
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
@RequiredArgsConstructor
public class TenantJobAspect {

    private final TenantFrameworkService tenantFrameworkService;

    /**
     * 拦截有 {@link TenantJob} 注解的方法
     *
     * @param joinPoint 切点
     * @param tenantJob 注解
     */
    @Around("@annotation(tenantJob)")
    public void around(ProceedingJoinPoint joinPoint, TenantJob tenantJob) {
        // 获得租户列表
        List<Long> tenantIds = tenantFrameworkService.getTenantIds();
        if (CollUtil.isEmpty(tenantIds)) {
            return;
        }

        // 逐个租户执行 Job
        Map<Long, String> results = new ConcurrentHashMap<>();
        // 标记，是否存在失败的情况
        AtomicBoolean success = new AtomicBoolean(true);
        // XXL-Job 上下文
        XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();

        tenantIds.parallelStream().forEach(tenantId -> {
            // TODO：先通过 parallel 实现并行
            //  1）多个租户是一条执行日志
            //  2）异常的情况
            TenantUtils.execute(tenantId, () -> {
                try {
                    XxlJobContext.setXxlJobContext(xxlJobContext);

                    // 执行 Job
                    Object result = joinPoint.proceed();
                    results.put(tenantId, StrUtil.toStringOrEmpty(result));
                } catch (Throwable e) {
                    results.put(tenantId, ExceptionUtil.getRootCauseMessage(e));
                    success.set(false);

                    // 打印异常
                    XxlJobHelper.log(StrUtil.format("[多租户({}) 执行任务({})，发生异常：{}]",
                            tenantId, joinPoint.getSignature(), ExceptionUtils.getStackTrace(e)));
                }
            });
        });

        // 记录执行结果
        if (success.get()) {
            XxlJobHelper.handleSuccess(JsonUtils.toJsonString(results));
        } else {
            XxlJobHelper.handleFail(JsonUtils.toJsonString(results));
        }
    }
}
