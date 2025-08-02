package cn.jcodenest.framework.websocket.core.sender.rocketmq;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * RocketMQ 广播 WebSocket 的消息
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Accessors(chain = true)
public class RocketMQWebSocketMessage {

    /**
     * Session 编号
     */
    private String sessionId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String messageContent;
}
