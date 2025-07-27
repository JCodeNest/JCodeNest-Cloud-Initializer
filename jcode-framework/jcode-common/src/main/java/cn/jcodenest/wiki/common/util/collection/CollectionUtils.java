package cn.jcodenest.wiki.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.wiki.common.pojo.PageResult;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.convert.Convert.toCollection;
import static java.util.Arrays.asList;

/**
 * Collection 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

    /**
     * 判断 source 是否在 targets 中
     *
     * @param source  源
     * @param targets 目标
     * @return true 在 ｜ false 不在
     */
    public static boolean containsAny(Object source, Object... targets) {
        return asList(targets).contains(source);
    }

    /**
     * 判断集合是否为空
     *
     * @param collections 集合
     * @return true 为空 ｜ false 不为空
     */
    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtil::isEmpty);
    }

    /**
     * 检查集合中是否有任何元素匹配给定的条件
     *
     * @param from      源集合
     * @param predicate 匹配条件
     * @param <T>       集合元素类型
     * @return true 如果有任何元素匹配条件, 否则返回 false
     */
    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return from.stream().anyMatch(predicate);
    }

    /**
     * 根据给定条件过滤集合, 返回匹配条件的元素列表
     *
     * @param from      源集合
     * @param predicate 过滤条件
     * @param <T>       集合元素类型
     * @return 过滤后的元素列表, 如果源集合为空则返回空列表
     */
    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return from.stream().filter(predicate).toList();
    }

    /**
     * 根据指定的键映射函数对集合进行去重, 保留第一个出现的元素
     *
     * @param from      源集合
     * @param keyMapper 键映射函数, 用于提取去重的依据
     * @param <T>       集合元素类型
     * @param <R>       键类型
     * @return 去重后的元素列表, 如果源集合为空则返回空列表
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    /**
     * 根据指定的键映射函数对集合进行去重, 使用合并函数处理重复元素
     *
     * @param from      源集合
     * @param keyMapper 键映射函数, 用于提取去重的依据
     * @param cover     合并函数, 用于处理重复键的元素
     * @param <T>       集合元素类型
     * @param <R>       键类型
     * @return 去重后的元素列表, 如果源集合为空则返回空列表
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    /**
     * 将数组转换为列表, 对每个元素应用转换函数
     *
     * @param from 源数组
     * @param func 转换函数
     * @param <T>  数组元素类型
     * @param <U>  转换后的元素类型
     * @return 转换后的列表, 如果源数组为空则返回空列表
     */
    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return convertList(Arrays.asList(from), func);
    }

    /**
     * 将集合转换为列表, 对每个元素应用转换函数, 过滤掉null值
     *
     * @param from 源集合
     * @param func 转换函数
     * @param <T>  源集合元素类型
     * @param <U>  转换后的元素类型
     * @return 转换后的列表, 如果源集合为空则返回空列表
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return from.stream().
                map(func)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 将集合转换为列表, 先应用过滤条件, 再对每个元素应用转换函数, 过滤掉null值
     *
     * @param from   源集合
     * @param func   转换函数
     * @param filter 过滤条件
     * @param <T>    源集合元素类型
     * @param <U>    转换后的元素类型
     * @return 转换后的列表, 如果源集合为空则返回空列表
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return from.stream()
                .filter(filter)
                .map(func)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 转换分页结果, 对分页数据中的每个元素应用转换函数
     *
     * @param from 源分页结果
     * @param func 转换函数
     * @param <T>  源分页数据元素类型
     * @param <U>  转换后的元素类型
     * @return 转换后的分页结果, 保持总数不变
     */
    public static <T, U> PageResult<U> convertPage(PageResult<T> from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return new PageResult<>(from.getTotal());
        }

        return new PageResult<>(convertList(from.getList(), func), from.getTotal());
    }

    /**
     * 使用flatMap将集合转换为列表, 每个元素可以映射为多个元素
     *
     * @param from 源集合
     * @param func flatMap转换函数, 将单个元素转换为Stream
     * @param <T>  源集合元素类型
     * @param <U>  转换后的元素类型
     * @return 扁平化后的列表, 如果源集合为空则返回空列表
     */
    public static <T, U> List<U> convertListByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return from.stream()
                .filter(Objects::nonNull)
                .flatMap(func)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 使用复合flatMap将集合转换为列表, 先映射再扁平化
     *
     * @param from   源集合
     * @param mapper 第一步映射函数
     * @param func   第二步flatMap转换函数
     * @param <T>    源集合元素类型
     * @param <U>    中间映射类型
     * @param <R>    最终元素类型
     * @return 扁平化后的列表, 如果源集合为空则返回空列表
     */
    public static <T, U, R> List<R> convertListByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }

        return from.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(func)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 合并Map中所有值列表为单个列表
     *
     * @param map 包含列表值的Map
     * @param <K> Map的键类型
     * @param <V> 列表元素类型
     * @return 合并后的列表
     */
    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        return map.values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    /**
     * 将集合转换为Set, 去除重复元素
     *
     * @param from 源集合
     * @param <T>  集合元素类型
     * @return 转换后的Set
     */
    public static <T> Set<T> convertSet(Collection<T> from) {
        return convertSet(from, v -> v);
    }

    /**
     * 将集合转换为Set, 对每个元素应用转换函数, 过滤掉null值
     *
     * @param from 源集合
     * @param func 转换函数
     * @param <T>  源集合元素类型
     * @param <U>  转换后的元素类型
     * @return 转换后的Set, 如果源集合为空则返回空Set
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }

        return from.stream()
                .map(func)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 将集合转换为Set, 先应用过滤条件, 再对每个元素应用转换函数, 过滤掉null值
     *
     * @param from   源集合
     * @param func   转换函数
     * @param filter 过滤条件
     * @param <T>    源集合元素类型
     * @param <U>    转换后的元素类型
     * @return 转换后的Set, 如果源集合为空则返回空Set
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }

        return from.stream()
                .filter(filter)
                .map(func)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 根据过滤条件将集合转换为Map
     *
     * @param from    源集合
     * @param filter  过滤条件
     * @param keyFunc 键映射函数
     * @param <T>     集合元素类型
     * @param <K>     Map键类型
     * @return 转换后的Map, 如果源集合为空则返回空Map
     */
    public static <T, K> Map<K, T> convertMapByFilter(Collection<T> from, Predicate<T> filter, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return from.stream()
                .filter(filter)
                .collect(Collectors.toMap(keyFunc, v -> v));
    }

    /**
     * 使用flatMap将集合转换为Set, 每个元素可以映射为多个元素
     *
     * @param from 源集合
     * @param func flatMap转换函数, 将单个元素转换为Stream
     * @param <T>  源集合元素类型
     * @param <U>  转换后的元素类型
     * @return 扁平化后的Set, 如果源集合为空则返回空Set
     */
    public static <T, U> Set<U> convertSetByFlatMap(Collection<T> from, Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }

        return from.stream()
                .filter(Objects::nonNull)
                .flatMap(func)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 使用复合flatMap将集合转换为Set, 先映射再扁平化
     *
     * @param from   源集合
     * @param mapper 第一步映射函数
     * @param func   第二步flatMap转换函数
     * @param <T>    源集合元素类型
     * @param <U>    中间映射类型
     * @param <R>    最终元素类型
     * @return 扁平化后的Set, 如果源集合为空则返回空Set
     */
    public static <T, U, R> Set<R> convertSetByFlatMap(Collection<T> from, Function<? super T, ? extends U> mapper, Function<U, ? extends Stream<? extends R>> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }

        return from.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(func)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 将集合转换为Map, 使用指定的键映射函数, 值为原始元素
     *
     * @param from    源集合
     * @param keyFunc 键映射函数
     * @param <T>     集合元素类型
     * @param <K>     Map键类型
     * @return 转换后的Map, 如果源集合为空则返回空Map
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return convertMap(from, keyFunc, Function.identity());
    }

    /**
     * 将集合转换为Map, 使用指定的键映射函数和Map供应商
     *
     * @param from     源集合
     * @param keyFunc  键映射函数
     * @param supplier Map供应商, 用于创建特定类型的Map
     * @param <T>      集合元素类型
     * @param <K>      Map键类型
     * @return 转换后的Map, 如果源集合为空则返回供应商提供的空Map
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return supplier.get();
        }

        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    /**
     * 将集合转换为Map, 使用指定的键映射函数和值映射函数
     *
     * @param from      源集合
     * @param keyFunc   键映射函数
     * @param valueFunc 值映射函数
     * @param <T>       集合元素类型
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @return 转换后的Map, 如果源集合为空则返回空Map
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    /**
     * 将集合转换为Map, 使用指定的键映射函数、值映射函数和合并函数处理重复键
     *
     * @param from          源集合
     * @param keyFunc       键映射函数
     * @param valueFunc     值映射函数
     * @param mergeFunction 合并函数, 用于处理重复键的值
     * @param <T>           集合元素类型
     * @param <K>           Map键类型
     * @param <V>           Map值类型
     * @return 转换后的Map, 如果源集合为空则返回空Map
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    /**
     * 将集合转换为Map, 使用指定的键映射函数、值映射函数和Map供应商
     *
     * @param from      源集合
     * @param keyFunc   键映射函数
     * @param valueFunc 值映射函数
     * @param supplier  Map供应商, 用于创建特定类型的Map
     * @param <T>       集合元素类型
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @return 转换后的Map, 如果源集合为空则返回供应商提供的空Map
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return supplier.get();
        }

        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }

    /**
     * 将集合转换为Map, 使用完整的参数配置
     *
     * @param from          源集合
     * @param keyFunc       键映射函数
     * @param valueFunc     值映射函数
     * @param mergeFunction 合并函数, 用于处理重复键的值
     * @param supplier      Map供应商, 用于创建特定类型的Map
     * @param <T>           集合元素类型
     * @param <K>           Map键类型
     * @param <V>           Map值类型
     * @return 转换后的Map, 如果源集合为空则返回空HashMap
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    /**
     * 将集合转换为多值Map, 每个键对应一个元素列表
     *
     * @param from    源集合
     * @param keyFunc 键映射函数
     * @param <T>     集合元素类型
     * @param <K>     Map键类型
     * @return 转换后的多值Map, 如果源集合为空则返回空Map
     */
    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }

    /**
     * 将集合转换为多值Map, 每个键对应一个转换后的元素列表
     *
     * @param from      源集合
     * @param keyFunc   键映射函数
     * @param valueFunc 值映射函数
     * @param <T>       集合元素类型
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @return 转换后的多值Map, 如果源集合为空则返回空Map
     */
    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    /**
     * 将集合转换为多值Map, 每个键对应一个转换后的元素Set（去重）
     *
     * @param from      源集合
     * @param keyFunc   键映射函数
     * @param valueFunc 值映射函数
     * @param <T>       集合元素类型
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @return 转换后的多值Map, 值为Set类型, 如果源集合为空则返回空Map
     */
    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }

    /**
     * 将集合转换为不可变Map
     *
     * @param from    源集合
     * @param keyFunc 键映射函数
     * @param <T>     集合元素类型
     * @param <K>     Map键类型
     * @return 转换后的不可变Map, 如果源集合为空则返回空Map
     */
    public static <T, K> Map<K, T> convertImmutableMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return Collections.emptyMap();
        }

        ImmutableMap.Builder<K, T> builder = ImmutableMap.builder();
        from.forEach(item -> builder.put(keyFunc.apply(item), item));
        return builder.build();
    }

    /**
     * 对比老、新两个列表, 找出新增、修改、删除的数据
     *
     * @param oldList  老列表
     * @param newList  新列表
     * @param sameFunc 对比函数, 返回 true 表示相同, 返回 false 表示不同 (注意 same 是通过每个元素的 “标识”, 判断它们是不是同一个数据)
     * @return [新增列表、修改列表、删除列表]
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList, BiPredicate<T, T> sameFunc) {
        List<T> createList = new LinkedList<>(newList);
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        for (T oldObj : oldList) {
            T foundObj = createList.stream()
                    .filter(newObj -> sameFunc.test(oldObj, newObj))
                    .findFirst()
                    .orElse(null);

            if (foundObj != null) {
                createList.remove(foundObj);
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return asList(createList, updateList, deleteList);
    }

    /**
     * 检查两个集合是否有任何交集元素
     *
     * @param source     源集合
     * @param candidates 候选集合
     * @return true 如果两个集合有交集, 否则返回 false
     */
    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    /**
     * 获取列表的第一个元素
     *
     * @param from 源列表
     * @param <T>  列表元素类型
     * @return 第一个元素, 如果列表为空则返回 null
     */
    public static <T> T getFirst(List<T> from) {
        return !CollUtil.isEmpty(from) ? from.get(0) : null;
    }

    /**
     * 查找集合中第一个匹配条件的元素
     *
     * @param from      源集合
     * @param predicate 匹配条件
     * @param <T>       集合元素类型
     * @return 第一个匹配的元素, 如果没有匹配的元素则返回 null
     */
    public static <T> T findFirst(Collection<T> from, Predicate<T> predicate) {
        return findFirst(from, predicate, Function.identity());
    }

    /**
     * 查找集合中第一个匹配条件的元素, 并对其应用转换函数
     *
     * @param from      源集合
     * @param predicate 匹配条件
     * @param func      转换函数
     * @param <T>       集合元素类型
     * @param <U>       转换后的类型
     * @return 转换后的第一个匹配元素, 如果没有匹配的元素则返回 null
     */
    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }

        return from.stream()
                .filter(predicate)
                .findFirst()
                .map(func)
                .orElse(null);
    }

    /**
     * 获取集合中指定属性的最大值
     *
     * @param from      源集合
     * @param valueFunc 值提取函数
     * @param <T>       集合元素类型
     * @param <V>       值类型, 必须实现Comparable接口
     * @return 最大值, 如果集合为空则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }

        return from.stream()
                .max(Comparator.comparing(valueFunc))
                .map(valueFunc)
                .orElse(null);
    }

    /**
     * 获取列表中指定属性的最小值
     *
     * @param from      源列表
     * @param valueFunc 值提取函数
     * @param <T>       列表元素类型
     * @param <V>       值类型, 必须实现Comparable接口
     * @return 最小值, 如果列表为空则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }

        return from.stream()
                .min(Comparator.comparing(valueFunc))
                .map(valueFunc)
                .orElse(null);
    }

    /**
     * 获取列表中具有最小值的对象
     *
     * @param from      源列表
     * @param valueFunc 值提取函数
     * @param <T>       列表元素类型
     * @param <V>       值类型, 必须实现Comparable接口
     * @return 具有最小值的对象, 如果列表为空则返回 null
     */
    public static <T, V extends Comparable<? super V>> T getMinObject(List<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }

        return from.stream()
                .min(Comparator.comparing(valueFunc))
                .orElse(null);
    }

    /**
     * 计算集合中指定属性的累计值
     *
     * @param from        源集合
     * @param valueFunc   值提取函数
     * @param accumulator 累加器函数
     * @param <T>         集合元素类型
     * @param <V>         值类型, 必须实现Comparable接口
     * @return 累计值, 如果集合为空或没有有效值则返回 null
     */
    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        return getSumValue(from, valueFunc, accumulator, null);
    }

    /**
     * 计算集合中指定属性的累计值, 支持默认值
     *
     * @param from         源集合
     * @param valueFunc    值提取函数
     * @param accumulator  累加器函数
     * @param defaultValue 默认值, 当集合为空或没有有效值时返回
     * @param <T>          集合元素类型
     * @param <V>          值类型, 必须实现Comparable接口
     * @return 累计值, 如果集合为空或没有有效值则返回默认值
     */
    public static <T, V extends Comparable<? super V>> V getSumValue(Collection<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator, V defaultValue) {
        if (CollUtil.isEmpty(from)) {
            return defaultValue;
        }

        return from.stream()
                .map(valueFunc)
                .filter(Objects::nonNull)
                .reduce(accumulator)
                .orElse(defaultValue);
    }

    /**
     * 当元素不为null时, 将其添加到集合中
     *
     * @param coll 目标集合
     * @param item 要添加的元素
     * @param <T>  元素类型
     */
    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }

        coll.add(item);
    }

    /**
     * 创建包含单个元素的集合, 如果元素为null则返回空集合
     *
     * @param obj 要包含的元素
     * @param <T> 元素类型
     * @return 包含单个元素的集合, 如果元素为null则返回空集合
     */
    public static <T> Collection<T> singleton(T obj) {
        return obj == null ? Collections.emptyList() : Collections.singleton(obj);
    }

    /**
     * 将嵌套的列表扁平化为单个列表
     *
     * @param list 嵌套的列表
     * @param <T>  元素类型
     * @return 扁平化后的列表, 过滤掉null的子列表
     */
    public static <T> List<T> newArrayList(List<List<T>> list) {
        return list.stream().filter(Objects::nonNull).flatMap(Collection::stream).toList();
    }

    /**
     * 转换为 LinkedHashSet
     *
     * @param <T>         元素类型
     * @param elementType 集合中元素类型
     * @param value       被转换的值
     * @return {@link Set}
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toLinkedHashSet(Class<T> elementType, Object value) {
        return (Set<T>) toCollection(LinkedHashSet.class, elementType, value);
    }
}
