package cn.jcodenest.framework.datapermission.core.aop;

import cn.jcodenest.framework.datapermission.core.annotation.DataPermission;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * {@link DataPermission} 注解的 Advisor 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class DataPermissionAnnotationAdvisor extends AbstractPointcutAdvisor {

    /**
     * 拦截器
     */
    private final Advice advice;

    /**
     * 切点
     */
    private final Pointcut pointcut;

    /**
     * 构造函数
     */
    public DataPermissionAnnotationAdvisor() {
        this.advice = new DataPermissionAnnotationInterceptor();
        this.pointcut = this.buildPointcut();
    }

    /**
     * 构建切点
     *
     * @return 切点
     */
    protected Pointcut buildPointcut() {
        Pointcut classPointcut = new AnnotationMatchingPointcut(DataPermission.class, true);
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, DataPermission.class, true);
        return new ComposablePointcut(classPointcut).union(methodPointcut);
    }
}
