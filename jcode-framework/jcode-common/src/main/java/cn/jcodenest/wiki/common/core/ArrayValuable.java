package cn.jcodenest.wiki.common.core;

/**
 * 可以生成 T 泛型数组的接口
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface ArrayValuable<T> {

    /**
     * @return 数组
     */
    T[] array();
}
