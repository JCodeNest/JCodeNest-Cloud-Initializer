package cn.jcodenest.initializer.common.util.object;

import cn.hutool.core.bean.BeanUtil;
import cn.jcodenest.initializer.common.pojo.PageResult;
import cn.jcodenest.initializer.common.util.collection.CollectionUtils;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 工具类
 * <p>
 * 1. 默认使用 {@link cn.hutool.core.bean.BeanUtil} 作为实现类
 * 2. 针对复杂的对象转换, 可以参考 AuthConvert 实现, 通过 mapstruct + default 配合实现
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanUtils {

    /**
     * 将源对象转换为目标对象
     *
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    /**
     * 将源对象转换为目标对象, 并对目标对象进行后续处理
     *
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @param peek        目标对象后续处理
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);

        if (target != null) {
            peek.accept(target);
        }

        return target;
    }

    /**
     * 将源列表转换为目标列表
     *
     * @param source     源列表
     * @param targetType 目标对象类型
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标列表
     */
    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return Collections.emptyList();
        }

        return CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    /**
     * 将源列表转换为目标列表, 并对目标列表中的每个元素进行后续处理
     *
     * @param source     源列表
     * @param targetType 目标对象类型
     * @param peek       目标对象后续处理
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标列表
     */
    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);

        if (list != null) {
            list.forEach(peek);
        }

        return list;
    }

    /**
     * 将源分页结果转换为目标分页结果
     *
     * @param source     源分页结果
     * @param targetType 目标对象类型
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标分页结果
     */
    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }

    /**
     * 将源分页结果转换为目标分页结果, 并对目标分页结果中的每个元素进行后续处理
     *
     * @param source     源分页结果
     * @param targetType 目标对象类型
     * @param peek       目标对象后续处理
     * @param <S>        源对象类型
     * @param <T>        目标对象类型
     * @return 目标分页结果
     */
    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }

        List<T> list = toBean(source.getList(), targetType);
        if (peek != null) {
            list.forEach(peek);
        }

        return new PageResult<>(list, source.getTotal());
    }

    /**
     * 复制对象属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }

        BeanUtil.copyProperties(source, target, false);
    }
}
