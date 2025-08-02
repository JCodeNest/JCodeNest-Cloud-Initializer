package cn.jcodenest.framework.websocket.config.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * WebSocket 配置项
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Validated
@ConfigurationProperties("jcode.websocket")
public class WebSocketProperties {

    /**
     * WebSocket 的连接路径
     */
    @NotEmpty(message = "WebSocket 的连接路径不能为空")
    private String path = "/ws";

    /**
     * 消息发送器的类型
     * <p>
     * 可选值：local、redis、rocketmq、kafka、rabbitmq
     */
    @NotNull(message = "WebSocket 的消息发送者不能为空")
    private String senderType = "local";
}
