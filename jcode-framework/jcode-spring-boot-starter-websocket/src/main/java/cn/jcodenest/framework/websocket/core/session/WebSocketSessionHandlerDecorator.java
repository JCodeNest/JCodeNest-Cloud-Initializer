package cn.jcodenest.framework.websocket.core.session;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

/**
 * {@link WebSocketHandler} 的装饰类，实现了以下功能：
 * 1. {@link WebSocketSession} 连接或关闭时，使用 {@link #sessionManager} 进行管理
 * 2. 封装 {@link WebSocketSession} 支持并发操作
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {

    /**
     * 发送时间的限制，单位：毫秒
     */
    private static final Integer SEND_TIME_LIMIT = 1000 * 5;

    /**
     * 发送消息缓冲上线，单位：bytes
     */
    private static final Integer BUFFER_SIZE_LIMIT = 1024 * 100;

    /**
     * 会话管理器
     */
    private final WebSocketSessionManager sessionManager;

    /**
     * 构造方法
     *
     * @param delegate       WebSocketHandler
     * @param sessionManager 会话管理器
     */
    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate, WebSocketSessionManager sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }

    /**
     * 在链接创建成功后调用
     *
     * @param session 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 实现 session 支持并发，可参考 https://blog.csdn.net/abu935009066/article/details/131218149
        session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        // 添加到 WebSocketSessionManager 中
        sessionManager.addSession(session);
    }

    /**
     * 在断开连接后调用
     *
     * @param session     WebSocketSession
     * @param closeStatus 关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionManager.removeSession(session);
    }
}
