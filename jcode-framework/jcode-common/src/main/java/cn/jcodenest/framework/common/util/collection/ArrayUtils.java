package cn.jcodenest.framework.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.jcodenest.framework.common.util.collection.CollectionUtils.convertList;

/**
 * Array 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArrayUtils {

    /**
     * 将 object 和 newElements 合并成一个数组
     *
     * @param object      对象
     * @param newElements 数组
     * @param <T>         泛型
     * @return 结果数组
     */
    @SafeVarargs
    public static <T> Consumer<T>[] append(Consumer<T> object, Consumer<T>... newElements) {
        if (object == null) {
            return newElements;
        }
        
        Consumer<T>[] result = ArrayUtil.newArray(Consumer.class, 1 + newElements.length);
        result[0] = object;
        System.arraycopy(newElements, 0, result, 1, newElements.length);
        return result;
    }

    /**
     * 将 from 转成数组
     *
     * @param from     被转换的集合
     * @param mapper   转换器
     * @return 结果数组
     * @param <T>      被转换的集合元素类型
     * @param <V>      转换后的集合元素类型
     */
    public static <T, V> V[] toArray(Collection<T> from, Function<T, V> mapper) {
        return toArray(convertList(from, mapper));
    }

    /**
     * 将 from 转成数组
     *
     * @param from     被转换的集合
     * @return 结果数组
     * @param <T>      被转换的集合元素类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> from) {
        if (CollUtil.isEmpty(from)) {
            return (T[]) (new Object[0]);
        }

        return ArrayUtil.toArray(from, (Class<T>) IterUtil.getElementType(from.iterator()));
    }

    /**
     * 获取数组中指定位置的元素
     *
     * @param array 数组
     * @param index 位置
     * @param <T>   泛型
     * @return 元素
     */
    public static <T> T get(T[] array, int index) {
        if (null == array || index >= array.length) {
            return null;
        }

        return array[index];
    }
}
