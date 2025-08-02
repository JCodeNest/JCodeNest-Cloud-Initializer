package cn.jcodenest.framework.excel.core.annotations;

import java.lang.annotation.*;

/**
 * 字典格式化注解，实现将字典数据的值格式化成字典数据的标签
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DictFormat {

    /**
     * 例如说 SysDictTypeConstants、InfDictTypeConstants
     *
     * @return 字典类型
     */
    String value();
}
