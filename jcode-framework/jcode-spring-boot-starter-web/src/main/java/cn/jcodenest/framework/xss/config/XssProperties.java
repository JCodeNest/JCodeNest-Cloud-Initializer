package cn.jcodenest.framework.xss.config;

import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * XSS 属性配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/29
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Valid
@ConfigurationProperties(prefix = "jcode.xss")
public class XssProperties {

    /**
     * 是否开启, 默认为 true
     */
    private boolean enable = true;

    /**
     * 需要排除的 URL, 默认为空
     */
    private List<String> excludeUrls = Collections.emptyList();
}
