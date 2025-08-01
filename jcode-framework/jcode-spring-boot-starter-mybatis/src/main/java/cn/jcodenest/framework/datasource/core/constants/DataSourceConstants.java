package cn.jcodenest.framework.datasource.core.constants;

/**
 * 多数据源配置常量
 *
 * <p>通过在方法上使用 {@link com.baomidou.dynamic.datasource.annotation.DS} 注解, 设置使用的数据源</p>
 * <p>注意：默认是 {@link #MASTER} 数据源</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DataSourceConstants {

    /**
     * 主库, 推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Master} 注解
     */
    public final String MASTER = "master";

    /**
     * 从库, 推荐使用 {@link com.baomidou.dynamic.datasource.annotation.Slave} 注解
     */
    public final  String SLAVE = "slave";
}
