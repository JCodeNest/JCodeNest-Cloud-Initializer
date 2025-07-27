package com.fhs.trans.service;

import com.fhs.core.trans.vo.VO;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动翻译接口, 只有实现了这个接口的才能自动翻译
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface AutoTransable<V extends VO> {

    /**
     * 根据 ids 查询数据列表
     * <p>
     * 改方法已过期啦，请使用 selectByIds
     *
     * @param ids 编号数组
     * @return 数据列表
     */
    @Deprecated
    default List<V> findByIds(List<? extends Object> ids) {
        return new ArrayList<>();
    }

    /**
     * 根据 ids 查询
     *
     * @param ids 编号数组
     * @return 数据列表
     */
    default List<V> selectByIds(List<? extends Object> ids) {
        return this.findByIds(ids);
    }

    /**
     * 获取 db 中所有的数据
     *
     * @return db 中所有的数据
     */
    default List<V> select() {
        return new ArrayList<>();
    }

    /**
     * 根据 id 获取 vo
     *
     * @param primaryValue id
     * @return vo
     */
    V selectById(Object primaryValue);
}
