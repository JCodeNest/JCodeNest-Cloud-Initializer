package cn.jcodenest.framework.websocket.core.session;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.tenant.core.content.TenantContextHolder;
import cn.jcodenest.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认的 {@link WebSocketSessionManager} 实现类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class WebSocketSessionManagerImpl implements WebSocketSessionManager {

    /**
     * id 与 WebSocketSession 映射
     * <p>
     * key：Session 编号
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    /**
     * user 与 WebSocketSession 映射
     * <p>
     * key1：用户类型
     * key2：用户编号
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> userSessions = new ConcurrentHashMap<>();

    /**
     * 添加 Session
     *
     * @param session Session
     */
    @Override
    public void addSession(WebSocketSession session) {
        // 添加到 idSessions 中
        idSessions.put(session.getId(), session);
        // 添加到 userSessions 中
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return;
        }

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            userSessionsMap = new ConcurrentHashMap<>();
            if (userSessions.putIfAbsent(user.getUserType(), userSessionsMap) != null) {
                userSessionsMap = userSessions.get(user.getUserType());
            }
        }

        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            if (userSessionsMap.putIfAbsent(user.getId(), sessions) != null) {
                sessions = userSessionsMap.get(user.getId());
            }
        }

        sessions.add(session);
    }

    /**
     * 移除 Session
     *
     * @param session Session
     */
    @Override
    public void removeSession(WebSocketSession session) {
        // 移除从 idSessions 中
        idSessions.remove(session.getId());
        // 移除从 idSessions 中
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return;
        }

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(user.getUserType());
        if (userSessionsMap == null) {
            return;
        }

        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(user.getId());
        sessions.removeIf(session0 -> session0.getId().equals(session.getId()));
        if (CollUtil.isEmpty(sessions)) {
            userSessionsMap.remove(user.getId(), sessions);
        }
    }

    /**
     * 获得指定编号的 Session
     *
     * @param id Session 编号
     * @return Session
     */
    @Override
    public WebSocketSession getSession(String id) {
        return idSessions.get(id);
    }

    /**
     * 获得指定用户类型的 Session 列表
     *
     * @param userType 用户类型
     * @return Session 列表
     */
    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }

        // 避免扩容
        LinkedList<WebSocketSession> result = new LinkedList<>();
        Long contextTenantId = TenantContextHolder.getTenantId();

        for (List<WebSocketSession> sessions : userSessionsMap.values()) {
            // 跳过空会话列表或租户不匹配的会话
            if (CollUtil.isEmpty(sessions) || (contextTenantId != null && !contextTenantId.equals(WebSocketFrameworkUtils.getTenantId(sessions.get(0))))) {
                continue;
            }
            result.addAll(sessions);
        }
        return result;
    }

    /**
     * 获得指定用户编号的 Session 列表
     *
     * @param userType 用户类型
     * @param userId   用户编号
     * @return Session 列表
     */
    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Long userId) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = userSessions.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId);
        return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }
}
