package cn.jcodenest.framework.websocket.core.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.tenant.core.util.TenantUtils;
import cn.jcodenest.framework.websocket.core.listener.WebSocketMessageListener;
import cn.jcodenest.framework.websocket.core.message.JsonWebSocketMessage;
import cn.jcodenest.framework.websocket.core.util.WebSocketFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * JSON 格式 {@link WebSocketHandler} 实现类
 * 基于 {@link JsonWebSocketMessage#getType()} 消息类型，调度到对应的 {@link WebSocketMessageListener} 监听器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class JsonWebSocketMessageHandler extends TextWebSocketHandler {

    /**
     * type 与 WebSocketMessageListener 的映射
     */
    private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

    /**
     * 添加监听器
     *
     * @param listenersList 监听器列表
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public JsonWebSocketMessageHandler(List<? extends WebSocketMessageListener> listenersList) {
        listenersList.forEach((Consumer<WebSocketMessageListener>) listener -> listeners.put(listener.getType(), listener));
    }

    /**
     * 处理消息
     *
     * @param session WebSocketSession
     * @param message 消息
     * @throws Exception 异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 空消息跳过
        if (message.getPayloadLength() == 0) {
            return;
        }

        // ping 心跳消息，直接返回 pong 消息
        if (message.getPayloadLength() == 4 && Objects.equals(message.getPayload(), "ping")) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }

        // 解析消息
        try {
            JsonWebSocketMessage jsonMessage = JsonUtils.parseObject(message.getPayload(), JsonWebSocketMessage.class);
            if (jsonMessage == null) {
                log.error("[handleTextMessage][session({}) message({}) 解析为空]", session.getId(), message.getPayload());
                return;
            }
            if (StrUtil.isEmpty(jsonMessage.getType())) {
                log.error("[handleTextMessage][session({}) message({}) 类型为空]", session.getId(), message.getPayload());
                return;
            }

            // 获得对应的 WebSocketMessageListener
            WebSocketMessageListener<Object> messageListener = listeners.get(jsonMessage.getType());
            if (messageListener == null) {
                log.error("[handleTextMessage][session({}) message({}) 监听器为空]", session.getId(), message.getPayload());
                return;
            }

            // 处理消息
            Type type = TypeUtil.getTypeArgument(messageListener.getClass(), 0);
            Object messageObj = JsonUtils.parseObject(jsonMessage.getContent(), type);
            Long tenantId = WebSocketFrameworkUtils.getTenantId(session);
            TenantUtils.execute(tenantId, () -> messageListener.onMessage(session, messageObj));
        } catch (Throwable ex) {
            log.error("[handleTextMessage][session({}) message({}) 处理异常]", session.getId(), message.getPayload());
        }
    }
}
