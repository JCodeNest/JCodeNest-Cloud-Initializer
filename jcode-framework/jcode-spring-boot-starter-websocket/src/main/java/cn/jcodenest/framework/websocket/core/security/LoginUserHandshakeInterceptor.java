package cn.jcodenest.framework.websocket.core.security;

import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.filter.TokenAuthenticationFilter;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import cn.jcodenest.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 登录用户的 {@link HandshakeInterceptor} 实现类
 *
 * <p>
 * 流程如下：
 * 1. 前端连接 websocket 时，会通过拼接 ?token={token} 到 ws:// 连接后，这样它可以被 {@link TokenAuthenticationFilter} 所认证通过
 * 2. {@link LoginUserHandshakeInterceptor} 负责把 {@link LoginUser} 添加到 {@link WebSocketSession} 中
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 拦截 WebSocket 的握手请求
     *
     * @param request    ServerHttpRequest
     * @param response   ServerHttpResponse
     * @param wsHandler  WebSocketHandler
     * @param attributes Map
     * @return boolean
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null) {
            WebSocketFrameworkUtils.setLoginUser(loginUser, attributes);
        }
        return true;
    }

    /**
     * 握手之后
     *
     * @param request   ServerHttpRequest
     * @param response  ServerHttpResponse
     * @param wsHandler WebSocketHandler
     * @param exception Exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }
}
