package cn.jcodenest.framework.datapermission.core.aop;

import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import lombok.Getter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link DataPermission} 注解的拦截器
 * 1. 在执行方法前，将 @DataPermission 注解入栈
 * 2. 在执行方法后，将 @DataPermission 注解出栈
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@DataPermission // 该注解，用于 {@link DATA_PERMISSION_NULL} 的空对象
public class DataPermissionAnnotationInterceptor implements MethodInterceptor {

    /**
     * DataPermission 空对象，用于方法无 {@link DataPermission} 注解时，使用 DATA_PERMISSION_NULL 进行占位
     */
    static final DataPermission DATA_PERMISSION_NULL = DataPermissionAnnotationInterceptor.class.getAnnotation(DataPermission.class);

    /**
     * 缓存 DataPermission 注解
     */
    @Getter
    private final Map<MethodClassKey, DataPermission> dataPermissionCache = new ConcurrentHashMap<>();

    /**
     * 拦截方法
     *
     * @param methodInvocation 方法调用
     * @return 方法执行结果
     * @throws Throwable 抛出异常
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 入栈
        DataPermission dataPermission = this.findAnnotation(methodInvocation);
        if (dataPermission != null) {
            DataPermissionContextHolder.add(dataPermission);
        }

        try {
            // 执行逻辑
            return methodInvocation.proceed();
        } finally {
            // 出栈
            if (dataPermission != null) {
                DataPermissionContextHolder.remove();
            }
        }
    }

    /**
     * 获取 DataPermission 注解
     *
     * @param methodInvocation 方法调用
     * @return DataPermission 注解
     */
    private DataPermission findAnnotation(MethodInvocation methodInvocation) {
        // 从缓存中获取
        Method method = methodInvocation.getMethod();
        Object targetObject = methodInvocation.getThis();
        Class<?> clazz = targetObject != null ? targetObject.getClass() : method.getDeclaringClass();
        MethodClassKey methodClassKey = new MethodClassKey(method, clazz);
        DataPermission dataPermission = dataPermissionCache.get(methodClassKey);
        if (dataPermission != null) {
            return dataPermission != DATA_PERMISSION_NULL ? dataPermission : null;
        }

        // 从方法中获取
        dataPermission = AnnotationUtils.findAnnotation(method, DataPermission.class);

        // 从类上获取
        if (dataPermission == null) {
            dataPermission = AnnotationUtils.findAnnotation(clazz, DataPermission.class);
        }

        // 添加到缓存中
        dataPermissionCache.put(methodClassKey, dataPermission != null ? dataPermission : DATA_PERMISSION_NULL);
        return dataPermission;
    }
}
