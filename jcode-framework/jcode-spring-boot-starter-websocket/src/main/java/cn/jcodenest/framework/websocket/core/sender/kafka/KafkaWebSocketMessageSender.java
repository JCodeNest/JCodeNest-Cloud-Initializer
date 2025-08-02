package cn.jcodenest.framework.websocket.core.sender.kafka;

import cn.jcodenest.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.WebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;

/**
 * 基于 Kafka 的 {@link WebSocketMessageSender} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class KafkaWebSocketMessageSender extends AbstractWebSocketMessageSender {

    /**
     * KafkaTemplate
     */
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    /**
     * Kafka 的 Topic
     */
    private final String topic;

    /**
     * 构造器
     *
     * @param sessionManager 会话管理器
     * @param kafkaTemplate  KafkaTemplate
     * @param topic          Kafka 的 Topic
     */
    public KafkaWebSocketMessageSender(WebSocketSessionManager sessionManager, KafkaTemplate<Object, Object> kafkaTemplate, String topic) {
        super(sessionManager);
        this.kafkaTemplate = kafkaTemplate;
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
        sendKafkaMessage(null, userId, userType, messageType, messageContent);
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
        sendKafkaMessage(null, null, userType, messageType, messageContent);
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
        sendKafkaMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Kafka 广播消息
     *
     * @param sessionId      Session 编号
     * @param userId         用户编号
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    private void sendKafkaMessage(String sessionId, Long userId, Integer userType, String messageType, String messageContent) {
        KafkaWebSocketMessage mqMessage = new KafkaWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);

        try {
            kafkaTemplate.send(topic, mqMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("[sendKafkaMessage][发送消息({}) 到 Kafka 失败]", mqMessage, e);
        }
    }
}
