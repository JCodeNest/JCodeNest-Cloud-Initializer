package cn.jcodenest.framework.websocket.core.sender.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;

/**
 * {@link RabbitMQWebSocketMessage} 广播消息的消费者，真正把消息发送出去
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        // 在 Queue 的名字上使用 UUID 生成其后缀。这样启动的 Consumer 的 Queue 不同，以达到广播消费的目的
                        name = "${jcode.websocket.sender-rabbitmq.queue}" + "-" + "#{T(java.util.UUID).randomUUID()}",
                        // Consumer 关闭时，该队列就可以被自动删除了
                        autoDelete = "true"
                ),
                exchange = @Exchange(
                        name = "${jcode.websocket.sender-rabbitmq.exchange}",
                        type = ExchangeTypes.TOPIC,
                        declare = "false"
                )
        )
)
@RequiredArgsConstructor
public class RabbitMQWebSocketMessageConsumer {

    private final RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender;

    /**
     * 监听消息
     *
     * @param message 消息
     */
    @RabbitHandler
    public void onMessage(RabbitMQWebSocketMessage message) {
        rabbitMQWebSocketMessageSender.send(message.getSessionId(),
                message.getUserType(), message.getUserId(),
                message.getMessageType(), message.getMessageContent());
    }
}
