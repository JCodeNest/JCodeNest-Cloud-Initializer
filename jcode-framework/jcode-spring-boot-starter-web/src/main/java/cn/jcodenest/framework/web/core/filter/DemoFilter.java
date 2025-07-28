package cn.jcodenest.framework.web.core.filter;

import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants.DEMO_DENY;

/**
 * 演示过滤器, 禁止用户发起写操作, 避免影响测试数据
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class DemoFilter extends OncePerRequestFilter {

    /**
     * 过滤器逻辑
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException 服务端异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 直接返回 DEMO_DENY 的结果, 即请求不继续
        ServletUtils.writeJSON(response, CommonResult.error(DEMO_DENY));
    }

    /**
     * 是否过滤
     *
     * <p>
     * 1. 写操作时, 不进行过滤率
     * 2. 非登录用户时, 不进行过滤
     * </p>
     *
     * @param request 请求
     * @return true-过滤, false-不过滤
     * @throws ServletException 服务端异常
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        return !StrUtil.equalsAnyIgnoreCase(method, "POST", "PUT", "DELETE")
                || WebFrameworkUtils.getLoginUserId(request) == null;
    }
}
