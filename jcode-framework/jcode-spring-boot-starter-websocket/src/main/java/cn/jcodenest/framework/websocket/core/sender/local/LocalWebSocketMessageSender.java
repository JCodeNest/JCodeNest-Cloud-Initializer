package cn.jcodenest.framework.websocket.core.sender.local;

import cn.jcodenest.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.WebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类，注意仅仅适合单机场景！
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * 构造器
     *
     * @param sessionManager 会话管理器
     */
    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }
}
