package cn.jcodenest.framework.websocket.config;

import cn.jcodenest.framework.mq.redis.config.JCodeRedisMQConsumerAutoConfiguration;
import cn.jcodenest.framework.mq.redis.core.RedisMQTemplate;
import cn.jcodenest.framework.websocket.config.properties.WebSocketProperties;
import cn.jcodenest.framework.websocket.core.handler.JsonWebSocketMessageHandler;
import cn.jcodenest.framework.websocket.core.listener.WebSocketMessageListener;
import cn.jcodenest.framework.websocket.core.security.LoginUserHandshakeInterceptor;
import cn.jcodenest.framework.websocket.core.security.WebSocketAuthorizeRequestsCustomizer;
import cn.jcodenest.framework.websocket.core.sender.kafka.KafkaWebSocketMessageConsumer;
import cn.jcodenest.framework.websocket.core.sender.kafka.KafkaWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.local.LocalWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageConsumer;
import cn.jcodenest.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.redis.RedisWebSocketMessageConsumer;
import cn.jcodenest.framework.websocket.core.sender.redis.RedisWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageConsumer;
import cn.jcodenest.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageSender;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionHandlerDecorator;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManager;
import cn.jcodenest.framework.websocket.core.session.WebSocketSessionManagerImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

/**
 * WebSocket 自动配置类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
// 在 JCodeRedisMQConsumerAutoConfiguration 的原因是需要保证 RedisWebSocketMessageConsumer 先创建，才能创建 RedisMessageListenerContainer
@AutoConfiguration(before = JCodeRedisMQConsumerAutoConfiguration.class)
// 开启 websocket
@EnableWebSocket
// 允许使用 jcode.websocket.enable=false 禁用 websocket
@ConditionalOnProperty(prefix = "jcode.websocket", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(WebSocketProperties.class)
public class JCodeWebSocketAutoConfiguration {

    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] handshakeInterceptors,
                                                   WebSocketHandler webSocketHandler,
                                                   WebSocketProperties webSocketProperties) {
        return registry -> registry
                // 添加 WebSocketHandler
                .addHandler(webSocketHandler, webSocketProperties.getPath())
                .addInterceptors(handshakeInterceptors)
                // 允许跨域，否则前端连接会直接断开
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new LoginUserHandshakeInterceptor();
    }

    @Bean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager,
                                             List<? extends WebSocketMessageListener<?>> messageListeners) {
        // 创建 JsonWebSocketMessageHandler 对象，处理消息
        JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(messageListeners);
        // 创建 WebSocketSessionHandlerDecorator 对象，处理连接
        return new WebSocketSessionHandlerDecorator(messageHandler, sessionManager);
    }

    @Bean
    public WebSocketSessionManager webSocketSessionManager() {
        return new WebSocketSessionManagerImpl();
    }

    @Bean
    public WebSocketAuthorizeRequestsCustomizer webSocketAuthorizeRequestsCustomizer(
            WebSocketProperties webSocketProperties) {
        return new WebSocketAuthorizeRequestsCustomizer(webSocketProperties);
    }

    // ==================== Sender 相关 ====================

    @Configuration
    @ConditionalOnProperty(prefix = "jcode.websocket", name = "sender-type", havingValue = "local")
    public class LocalWebSocketMessageSenderConfiguration {
        @Bean
        public LocalWebSocketMessageSender localWebSocketMessageSender(
                WebSocketSessionManager sessionManager) {
            return new LocalWebSocketMessageSender(sessionManager);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "jcode.websocket", name = "sender-type", havingValue = "redis")
    public class RedisWebSocketMessageSenderConfiguration {
        @Bean
        public RedisWebSocketMessageSender redisWebSocketMessageSender(
                WebSocketSessionManager sessionManager, RedisMQTemplate redisMQTemplate) {
            return new RedisWebSocketMessageSender(sessionManager, redisMQTemplate);
        }

        @Bean
        public RedisWebSocketMessageConsumer redisWebSocketMessageConsumer(
                RedisWebSocketMessageSender redisWebSocketMessageSender) {
            return new RedisWebSocketMessageConsumer(redisWebSocketMessageSender);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "jcode.websocket", name = "sender-type", havingValue = "rocketmq")
    public class RocketMQWebSocketMessageSenderConfiguration {
        @Bean
        public RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender(
                WebSocketSessionManager sessionManager, RocketMQTemplate rocketMQTemplate, @Value("${yudao.websocket.sender-rocketmq.topic}") String topic) {
            return new RocketMQWebSocketMessageSender(sessionManager, rocketMQTemplate, topic);
        }

        @Bean
        public RocketMQWebSocketMessageConsumer rocketMQWebSocketMessageConsumer(
                RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender) {
            return new RocketMQWebSocketMessageConsumer(rocketMQWebSocketMessageSender);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "jcode.websocket", name = "sender-type", havingValue = "rabbitmq")
    public class RabbitMQWebSocketMessageSenderConfiguration {

        @Bean
        public RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender(
                WebSocketSessionManager sessionManager, RabbitTemplate rabbitTemplate,
                TopicExchange websocketTopicExchange) {
            return new RabbitMQWebSocketMessageSender(sessionManager, rabbitTemplate, websocketTopicExchange);
        }

        @Bean
        public RabbitMQWebSocketMessageConsumer rabbitMQWebSocketMessageConsumer(
                RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender) {
            return new RabbitMQWebSocketMessageConsumer(rabbitMQWebSocketMessageSender);
        }

        /**
         * 创建 Topic Exchange
         */
        @Bean
        public TopicExchange websocketTopicExchange(@Value("${jcode.websocket.sender-rabbitmq.exchange}") String exchange) {
            return new TopicExchange(
                    exchange,
                    // durable: 是否持久化
                    true,
                    // exclusive: 是否排它
                    false
            );
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "yudao.websocket", name = "sender-type", havingValue = "kafka")
    public class KafkaWebSocketMessageSenderConfiguration {

        @Bean
        public KafkaWebSocketMessageSender kafkaWebSocketMessageSender(
                WebSocketSessionManager sessionManager, KafkaTemplate<Object, Object> kafkaTemplate,
                @Value("${yudao.websocket.sender-kafka.topic}") String topic) {
            return new KafkaWebSocketMessageSender(sessionManager, kafkaTemplate, topic);
        }

        @Bean
        public KafkaWebSocketMessageConsumer kafkaWebSocketMessageConsumer(
                KafkaWebSocketMessageSender kafkaWebSocketMessageSender) {
            return new KafkaWebSocketMessageConsumer(kafkaWebSocketMessageSender);
        }

    }
}
