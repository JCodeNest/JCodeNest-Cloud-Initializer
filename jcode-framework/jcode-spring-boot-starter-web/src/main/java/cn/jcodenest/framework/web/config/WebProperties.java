package cn.jcodenest.framework.web.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 自定义 Web 属性配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "jcode.web")
public class WebProperties {

    @NotNull(message = "APP API 不能为空")
    private Api appApi = new Api("/app-api", "**.controller.app.**");

    @NotNull(message = "Admin API 不能为空")
    private Api adminApi = new Api("/admin-api", "**.controller.admin.**");

    @NotNull(message = "Admin UI 不能为空")
    private Ui adminUi;

    @Data
    @Valid
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Api {

        @NotEmpty(message = "API 前缀不能为空")
        private String prefix;

        @NotEmpty(message = "Controller 所在包不能为空")
        private String controller;
    }

    @Data
    @Valid
    public static class Ui {

        /**
         * 访问地址
         */
        private String url;
    }
}
