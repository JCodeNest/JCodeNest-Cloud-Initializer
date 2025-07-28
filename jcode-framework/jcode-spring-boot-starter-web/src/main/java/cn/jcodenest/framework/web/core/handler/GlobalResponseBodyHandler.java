package cn.jcodenest.framework.web.core.handler;

import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 全局响应结果处理器 - ResponseBody
 *
 * <p>
 * 不同于在网上看到的很多文章, 会选择自动将 Controller 返回结果包上 {@link CommonResult}
 * 在 onemall 中是 Controller 在返回时主动自己包上 {@link CommonResult}. 原因是
 * GlobalResponseBodyHandler 本质上是 AOP, 它不应该改变 Controller 返回的数据结构.
 * 目前 GlobalResponseBodyHandler 的主要作用是记录 Controller 的返回结果.
 * 便于 {@link cn.jcodenest.framework.apilog.core.filter.ApiAccessLogFilter} 记录访问日志.
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@ControllerAdvice
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {

    /**
     * 是否支持该方法
     *
     * @param returnType    返回值类型
     * @param converterType 转换器类型
     * @return true 支持 ｜ false 不支持
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public boolean supports(MethodParameter returnType, Class converterType) {
        if (returnType.getMethod() == null) {
            return false;
        }

        Method method = returnType.getMethod();
        if (method == null) {
            return false;
        }

        return method.getReturnType() == CommonResult.class;
    }

    /**
     * 在响应之前处理
     *
     * @param body                  响应体
     * @param returnType            返回值类型
     * @param selectedContentType   选中的内容类型
     * @param selectedConverterType 选中的转换器类型
     * @param request               请求
     * @param response              响应
     * @return 处理后的响应体
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 设置 Controller 结果到 ServletRequest Attribute
        WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(), (CommonResult<?>) body);
        return body;
    }
}
