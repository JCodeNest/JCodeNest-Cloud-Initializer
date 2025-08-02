package cn.jcodenest.framework.websocket.core.sender.redis;

import cn.jcodenest.framework.mq.redis.core.RedisMQTemplate;
import cn.jcodenest.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.WebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Redis 的 {@link WebSocketMessageSender} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class RedisWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * Redis 模版
     */
    private final RedisMQTemplate redisMQTemplate;

    /**
     * 构造器
     *
     * @param sessionManager 会话管理器
     * @param redisMQTemplate Redis 模版
     */
    public RedisWebSocketMessageSender(WebSocketSessionManager sessionManager, RedisMQTemplate redisMQTemplate) {
        super(sessionManager);
        this.redisMQTemplate = redisMQTemplate;
    }

    /**
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRedisMessage(null, userId, userType, messageType, messageContent);
    }

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRedisMessage(null, null, userType, messageType, messageContent);
    }

    /**
     * 发送消息给指定 Session
     *
     * @param sessionId      Session 编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRedisMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Redis 广播消息
     *
     * @param sessionId      Session 编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRedisMessage(String sessionId, Long userId, Integer userType, String messageType, String messageContent) {
        RedisWebSocketMessage mqMessage = new RedisWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        redisMQTemplate.send(mqMessage);
    }
}
