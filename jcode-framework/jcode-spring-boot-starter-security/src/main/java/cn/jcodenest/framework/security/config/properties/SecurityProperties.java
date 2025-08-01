package cn.jcodenest.framework.security.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * Security 属性类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Valid
@ConfigurationProperties(prefix = "jcode.security")
public class SecurityProperties {

    /**
     * HTTP 请求时访问令牌的请求 Header
     */
    @NotEmpty(message = "Token Header 不能为空")
    private String tokenHeader = "Authorization";

    /**
     * HTTP 请求时访问令牌的请求参数
     * <p>
     * 初始目的: 解决 WebSocket 无法通过 header 传参, 只能通过 token 参数拼接
     */
    @NotEmpty(message = "Token Parameter 不能为空")
    private String tokenParameter = "token";

    /**
     * mock 模式的开关
     */
    @NotNull(message = "mock 模式的开关不能为空")
    private Boolean mockEnable = false;

    /**
     * mock 模式的密钥, 一定要配置密钥保证安全性
     *
     * <p>这里设置了一个默认值, 因为实际上只有 mockEnable 为 true 时才需要配置</p>
     */
    @NotEmpty(message = "mock 模式的密钥不能为空")
    private String mockSecret = "test";

    /**
     * 免登录的 URL 列表
     */
    private List<String> permitAllUrls = Collections.emptyList();

    /**
     * PasswordEncoder 加密复杂度, 越高开销越大
     */
    private Integer passwordEncoderLength = 4;
}
