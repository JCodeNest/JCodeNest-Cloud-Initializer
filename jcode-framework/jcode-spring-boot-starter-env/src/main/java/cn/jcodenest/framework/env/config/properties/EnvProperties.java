package cn.jcodenest.framework.env.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境属性配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "jcode.env")
public class EnvProperties {

    /**
     * 环境标签的键
     */
    public static final String TAG_KEY = "jcode.env.tag";

    /**
     * 环境标签
     */
    private String tag;
}
