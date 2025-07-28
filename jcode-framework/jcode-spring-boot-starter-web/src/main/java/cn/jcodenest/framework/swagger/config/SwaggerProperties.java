package cn.jcodenest.framework.swagger.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger 属性配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@ConfigurationProperties(prefix = "jcode.swagger")
public class SwaggerProperties {

    /**
     * 标题
     */
    @NotEmpty(message = "标题不能为空")
    private String title;

    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description;

    /**
     * 作者
     */
    @NotEmpty(message = "作者不能为空")
    private String author;

    /**
     * 版本
     */
    @NotEmpty(message = "版本不能为空")
    private String version;

    /**
     * url
     */
    @NotEmpty(message = "扫描的 package 不能为空")
    private String url;

    /**
     * email
     */
    @NotEmpty(message = "扫描的 email 不能为空")
    private String email;

    /**
     * license
     */
    @NotEmpty(message = "扫描的 license 不能为空")
    private String license;

    /**
     * license-url
     */
    @NotEmpty(message = "扫描的 license-url 不能为空")
    private String licenseUrl;
}
