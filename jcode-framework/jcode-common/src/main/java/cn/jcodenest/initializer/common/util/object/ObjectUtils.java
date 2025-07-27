package cn.jcodenest.initializer.common.util.object;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Object 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

    /**
     * 复制对象, 并忽略 Id 编号
     *
     * @param object   被复制对象
     * @param consumer 消费者, 可以二次编辑被复制对象
     * @return 复制后的对象
     */
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
        T result = ObjectUtil.clone(object);

        // 忽略 id 编号
        Field field = ReflectUtil.getField(object.getClass(), "id");
        if (field != null) {
            ReflectUtil.setFieldValue(result, field, null);
        }

        // 二次编辑
        if (result != null) {
            consumer.accept(result);
        }

        return result;
    }

    /**
     * 获取最大值
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 最大值
     */
    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        }

        if (obj2 == null) {
            return obj1;
        }

        return obj1.compareTo(obj2) > 0 ? obj1 : obj2;
    }

    /**
     * 获取第一个非空对象
     *
     * @param array 对象数组
     * @return 第一个非空对象
     */
    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for (T item : array) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断对象是否等于数组中的任意一个
     *
     * @param obj   对象
     * @param array 数组
     * @return true-等于 ｜ false-不等于
     */
    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }
}
