package cn.jcodenest.framework.websocket.core.sender.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * {@link KafkaWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RequiredArgsConstructor
public class KafkaWebSocketMessageConsumer {

    private final KafkaWebSocketMessageSender kafkaWebSocketMessageSender;

    @RabbitHandler
    @KafkaListener(
            topics = "${yudao.websocket.sender-kafka.topic}",
            // 在 Group 上使用 UUID 生成其后缀。这样启动的 Consumer 的 Group 不同，以达到广播消费的目的
            groupId = "${yudao.websocket.sender-kafka.consumer-group}" + "-" + "#{T(java.util.UUID).randomUUID()}")
    public void onMessage(KafkaWebSocketMessage message) {
        kafkaWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }
}
