package cn.jcodenest.framework.security.core.content;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

/**
 * 基于 TransmittableThreadLocal 实现的 Security Context 持有者策略
 *
 * <p>避免 @Async 等异步执行时, 原生 ThreadLocal 的丢失问题</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TransmittableThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    /**
     * 使用 TransmittableThreadLocal 作为上下文
     */
    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 清理上下文
     */
    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 获取上下文
     *
     * @return Security 上下文
     */
    @Override
    public SecurityContext getContext() {
        SecurityContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }

        return ctx;
    }

    /**
     * 设置上下文
     *
     * @param context Security 上下文
     */
    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 构造一个空的 Security 上下文
     *
     * @return 空的 Security 上下文
     */
    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}
