package cn.jcodenest.framework.websocket.core.sender;

import cn.jcodenest.framework.common.util.json.JsonUtils;

/**
 * WebSocket 消息的发送器接口
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public interface WebSocketMessageSender {

    /**
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, Long userId, String messageType, String messageContent);

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, String messageType, String messageContent);

    /**
     * 发送消息给指定 Session
     *
     * @param sessionId      Session 编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(String sessionId, String messageType, String messageContent);

    /**
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON
     */
    default void sendObject(Integer userType, Long userId, String messageType, Object messageContent) {
        send(userType, userId, messageType, JsonUtils.toJsonString(messageContent));
    }

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON
     */
    default void sendObject(Integer userType, String messageType, Object messageContent) {
        send(userType, messageType, JsonUtils.toJsonString(messageContent));
    }

    /**
     * 发送消息给指定会话
     *
     * @param sessionId      会话编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON
     */
    default void sendObject(String sessionId, String messageType, Object messageContent) {
        send(sessionId, messageType, JsonUtils.toJsonString(messageContent));
    }
}
