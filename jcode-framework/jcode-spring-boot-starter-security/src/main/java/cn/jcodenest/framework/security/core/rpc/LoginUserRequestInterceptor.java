package cn.jcodenest.framework.security.core.rpc;

import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.security.core.LoginUser;
import cn.jcodenest.framework.security.core.util.SecurityFrameworkUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LoginUser 的 RequestInterceptor 实现类
 *
 * <p>Feign 请求时将 {@link LoginUser} 设置到 header 中继续透传给被调用的服务</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class LoginUserRequestInterceptor implements RequestInterceptor {

    /**
     * 重写 apply 方法
     *
     * @param requestTemplate 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        LoginUser user = SecurityFrameworkUtils.getLoginUser();
        if (user == null) {
            return;
        }

        try {
            String userStr = JsonUtils.toJsonString(user);
            // 编码, 避免中文乱码
            userStr = URLEncoder.encode(userStr, StandardCharsets.UTF_8);
            // 设置到 header 中
            requestTemplate.header(SecurityFrameworkUtils.LOGIN_USER_HEADER, userStr);
        } catch (Exception ex) {
            log.error("[apply][序列化 LoginUser({}) 发生异常]", user, ex);
            throw ex;
        }
    }
}
