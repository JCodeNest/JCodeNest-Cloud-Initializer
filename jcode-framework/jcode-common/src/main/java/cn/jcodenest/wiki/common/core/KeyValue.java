package cn.jcodenest.wiki.common.core;

import java.io.Serializable;

/**
 * Key-Value 键值对
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class KeyValue<K, V> implements Serializable {

    /**
     * 键
     */
    private K key;

    /**
     * 值
     */
    private V value;
}
