package cn.jcodenest.framework.idempotent.core.keyresolver.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.framework.idempotent.core.annotation.Idempotent;
import cn.jcodenest.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 基于 Spring EL 表达式幂等 Key 解析器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class ExpressionIdempotentKeyResolver implements IdempotentKeyResolver {

    /**
     * 参数名发现器
     */
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Spring EL 表达式解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 解析一个 Key
     *
     * @param joinPoint  AOP 切面
     * @param idempotent 幂等注解
     * @return Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        // 获取被拦截方法参数名列表
        Method method = getMethod(joinPoint);
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(method);

        // 准备 Spring EL 表达式解析的上下文
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (ArrayUtil.isNotEmpty(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }
        }

        // 解析参数
        Expression expression = expressionParser.parseExpression(idempotent.keyArg());
        return expression.getValue(evaluationContext, String.class);
    }

    /**
     * 从 JoinPoint 中获取方法
     *
     * @param point 切面点
     * @return 方法
     */
    private static Method getMethod(JoinPoint point) {
        // 处理声明在类上的情况
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (!method.getDeclaringClass().isInterface()) {
            return method;
        }

        // 处理声明在接口上的情况
        try {
            return point.getTarget().getClass().getDeclaredMethod(point.getSignature().getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
