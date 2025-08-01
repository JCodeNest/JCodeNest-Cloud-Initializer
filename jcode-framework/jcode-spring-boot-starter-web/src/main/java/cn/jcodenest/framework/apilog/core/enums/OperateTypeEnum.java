package cn.jcodenest.framework.apilog.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志操作类型枚举
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 查询
     */
    GET(1),

    /**
     * 新增
     */
    CREATE(2),

    /**
     * 修改
     */
    UPDATE(3),

    /**
     * 删除
     */
    DELETE(4),

    /**
     * 导出
     */
    EXPORT(5),

    /**
     * 导入
     */
    IMPORT(6),

    /**
     * 其它 (在无法归类时, 可以选择使用其它, 因为还有操作名可以进一步标识)
     */
    OTHER(0);

    /**
     * 类型
     */
    private final Integer type;
}
