package cn.jcodenest.framework.websocket.core.sender.rabbitmq;

import cn.jcodenest.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.WebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 基于 RabbitMQ 的 {@link WebSocketMessageSender} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class RabbitMQWebSocketMessageSender extends AbstractWebSocketMessageSender {

 /**
  * RabbitMQ 模版
  */
    private final RabbitTemplate rabbitTemplate;

    /**
     * RabbitMQ 交换机
     */
    private final TopicExchange topicExchange;

    /**
     * 构造器
     *
     * @param sessionManager 会话管理器
     * @param rabbitTemplate RabbitMQ 模版
     * @param topicExchange  RabbitMQ 交换机
     */
    public RabbitMQWebSocketMessageSender(WebSocketSessionManager sessionManager, RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        super(sessionManager);
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
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
        sendRabbitMQMessage(null, userId, userType, messageType, messageContent);
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
        sendRabbitMQMessage(null, null, userType, messageType, messageContent);
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
        sendRabbitMQMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 RabbitMQ 广播消息
     *
     * @param sessionId      Session 编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendRabbitMQMessage(String sessionId, Long userId, Integer userType, String messageType, String messageContent) {
        RabbitMQWebSocketMessage mqMessage = new RabbitMQWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        rabbitTemplate.convertAndSend(topicExchange.getName(), null, mqMessage);
    }
}
