package cn.jcodenest.framework.websocket.core.sender.rocketmq;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * {@link RocketMQWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
// 重点：添加 @RocketMQMessageListener 注解声明消费的 topic
@RocketMQMessageListener(
        topic = "${jcode.websocket.sender-rocketmq.topic}",
        consumerGroup = "${jcode.websocket.sender-rocketmq.consumer-group}",
        // 设置为广播模式，保证每个实例都能收到消息
        messageModel = MessageModel.BROADCASTING
)
@RequiredArgsConstructor
public class RocketMQWebSocketMessageConsumer implements RocketMQListener<RocketMQWebSocketMessage> {

    private final RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender;

    /**
     * 监听消息
     *
     * @param message 消息
     */
    @Override
    public void onMessage(RocketMQWebSocketMessage message) {
        rocketMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }
}
