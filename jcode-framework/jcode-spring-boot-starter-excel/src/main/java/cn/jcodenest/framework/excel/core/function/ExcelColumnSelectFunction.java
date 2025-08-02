package cn.jcodenest.framework.excel.core.function;

import java.util.List;

/**
 * Excel 列下拉数据源获取接口
 *
 * <p>
 * 问题：为什么不直接解析字典还设计个接口？
 * 回答：考虑到有的下拉数据不是从字典中获取的所有需要做一个兼容。
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface ExcelColumnSelectFunction {

    /**
     * 获得方法名称
     *
     * @return 方法名称
     */
    String getName();

    /**
     * 获得列下拉数据源
     *
     * @return 下拉数据源
     */
    List<String> getOptions();
}
