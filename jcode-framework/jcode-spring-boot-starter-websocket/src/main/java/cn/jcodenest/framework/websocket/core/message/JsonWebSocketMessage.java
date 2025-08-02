package cn.jcodenest.framework.websocket.core.message;

import cn.jcodenest.framework.websocket.core.listener.WebSocketMessageListener;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * JSON 格式的 WebSocket 消息帧
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
public class JsonWebSocketMessage implements Serializable {

    /**
     * 消息类型
     * <p>
     * 目的：用于分发到对应的 {@link WebSocketMessageListener} 实现类
     */
    private String type;

    /**
     * 消息内容
     * <p>
     * 要求 JSON 对象
     */
    private String content;
}
