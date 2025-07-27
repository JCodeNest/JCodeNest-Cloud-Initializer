package cn.jcodenest.wiki.common.util.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.jcodenest.wiki.common.core.KeyValue;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Map 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtils {

    /**
     * 从哈希表表中获得 keys 对应的所有 value 数组
     *
     * @param multimap 哈希表
     * @param keys     keys
     * @return value 数组
     */
    public static <K, V> List<V> getList(Multimap<K, V> multimap, Collection<K> keys) {
        List<V> result = new ArrayList<>();

        keys.forEach(k -> {
            Collection<V> values = multimap.get(k);
            if (CollUtil.isEmpty(values)) {
                return;
            }

            result.addAll(values);
        });

        return result;
    }

    /**
     * 从哈希表查找到 key 对应的 value, 然后进一步处理
     * 注意: 如果查找到的 value 为 null 时, 不进行处理
     *
     * @param map      哈希表
     * @param key      key
     * @param consumer 进一步处理的逻辑
     */
    public static <K, V> void findAndThen(Map<K, V> map, K key, Consumer<V> consumer) {
        if (ObjectUtil.isNull(key) || CollUtil.isEmpty(map)) {
            return;
        }

        V value = map.get(key);
        if (value == null) {
            return;
        }

        consumer.accept(value);
    }

    /**
     * 将 {@link KeyValue} 数组转换成 Map
     *
     * @param keyValues {@link KeyValue} 数组
     * @return Map
     */
    public static <K, V> Map<K, V> convertMap(List<KeyValue<K, V>> keyValues) {
        Map<K, V> map = Maps.newLinkedHashMapWithExpectedSize(keyValues.size());
        keyValues.forEach(keyValue -> map.put(keyValue.getKey(), keyValue.getValue()));
        return map;
    }
}
