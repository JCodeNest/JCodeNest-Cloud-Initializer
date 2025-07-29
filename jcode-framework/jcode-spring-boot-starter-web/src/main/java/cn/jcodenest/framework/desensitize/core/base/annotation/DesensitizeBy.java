package cn.jcodenest.framework.desensitize.core.base.annotation;

import cn.jcodenest.framework.desensitize.core.base.handler.DesensitizationHandler;
import cn.jcodenest.framework.desensitize.core.base.serializer.StringDesensitizeSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 顶级脱敏注解, 自定义脱敏注解需要使用此注解
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside // 此注解是其他所有 jackson 注解的元注解, 打上了此注解的注解表明是 jackson 注解的一部分
@JsonSerialize(using = StringDesensitizeSerializer.class) // 使用自定义的序列化器
public @interface DesensitizeBy {

    /**
     * 脱敏处理器
     */
    @SuppressWarnings("rawtypes")
    Class<? extends DesensitizationHandler> handler();
}
