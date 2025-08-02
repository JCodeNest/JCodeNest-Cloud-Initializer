package cn.jcodenest.framework.lock4j.core.strategy;

import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.baomidou.lock.LockFailureStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 自定义获取所失败策略, 抛出 {@link ServiceException} 异常
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    /**
     * 获取锁失败
     *
     * @param key       锁的 key
     * @param method    方法
     * @param arguments 方法参数
     */
    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        log.debug("[onLockFailure][线程:{} 获取锁失败，key:{} 获取失败:{} ]", Thread.currentThread().getName(), key, arguments);
        throw new ServiceException(GlobalErrorCodeConstants.LOCKED);
    }
}
