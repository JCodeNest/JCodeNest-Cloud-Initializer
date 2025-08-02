package cn.jcodenest.framework.websocket.core.sender.rocketmq;

import cn.jcodenest.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.WebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * 基于 RocketMQ 的 {@link WebSocketMessageSender} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class RocketMQWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * RocketMQ 模板
     */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * RocketMQ 的 Topic
     */
    private final String topic;

    /**
     * 构造器
     *
     * @param sessionManager   会话管理器
     * @param rocketMQTemplate RocketMQ 模板
     * @param topic            RocketMQ 的 Topic
     */
    public RocketMQWebSocketMessageSender(WebSocketSessionManager sessionManager, RocketMQTemplate rocketMQTemplate, String topic) {
        super(sessionManager);
        this.rocketMQTemplate = rocketMQTemplate;
        this.topic = topic;
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
        sendRocketMQMessage(null, userId, userType, messageType, messageContent);
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
        sendRocketMQMessage(null, null, userType, messageType, messageContent);
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
        sendRocketMQMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 RocketMQ 广播消息
     *
     * @param sessionId      Session 编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRocketMQMessage(String sessionId, Long userId, Integer userType, String messageType, String messageContent) {
        RocketMQWebSocketMessage mqMessage = new RocketMQWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        rocketMQTemplate.syncSend(topic, mqMessage);
    }
}
