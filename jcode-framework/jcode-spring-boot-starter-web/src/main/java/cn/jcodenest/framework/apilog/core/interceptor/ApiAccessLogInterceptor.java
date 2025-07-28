package cn.jcodenest.framework.apilog.core.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.common.util.spring.SpringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * API 访问日志拦截器
 *
 * <p>目的: 在非 prod 环境时打印 request 和 response 两条日志到日志文件（控制台）中</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class ApiAccessLogInterceptor implements HandlerInterceptor {

    /**
     * HandlerMethod 属性名
     */
    public static final String ATTRIBUTE_HANDLER_METHOD = "HANDLER_METHOD";

    /**
     * StopWatch 属性名
     */
    private static final String ATTRIBUTE_STOP_WATCH = "ApiAccessLogInterceptor.StopWatch";

    /**
     * 在请求之前处理
     *
     * @param request  请求
     * @param response 响应
     * @param handler  处理器
     * @return 是否继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录 HandlerMethod, 提供给 ApiAccessLogFilter 使用
        HandlerMethod handlerMethod = handler instanceof HandlerMethod handlerMethod1 ? handlerMethod1 : null;
        if (handlerMethod != null) {
            request.setAttribute(ATTRIBUTE_HANDLER_METHOD, handlerMethod);
        }

        // 打印 request 日志
        if (!SpringUtils.isProd()) {
            // 获取请求参数
            Map<String, String> queryString = ServletUtils.getParamMap(request);
            // 获取请求体
            String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtils.getBody(request) : null;

            if (CollUtil.isEmpty(queryString) && StrUtil.isEmpty(requestBody)) {
                log.info("[preHandle][开始请求 URL({}) 无参数]", request.getRequestURI());
            } else {
                log.info("[preHandle][开始请求 URL({}) 参数({})]", request.getRequestURI(), StrUtil.blankToDefault(requestBody, queryString.toString()));
            }

            // 开始计时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            request.setAttribute(ATTRIBUTE_STOP_WATCH, stopWatch);

            // 打印 Controller 路径
            printHandlerMethodPosition(handlerMethod);
        }

        return true;
    }

    /**
     * 在请求之后处理
     *
     * @param request  请求
     * @param response 响应
     * @param handler  处理器
     * @param ex       异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 打印 response 日志
        if (!SpringUtils.isProd()) {
            // 结束计时
            StopWatch stopWatch = (StopWatch) request.getAttribute(ATTRIBUTE_STOP_WATCH);
            stopWatch.stop();

            log.info("[afterCompletion][完成请求 URL({}) 耗时({} ms)]", request.getRequestURI(), stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * 打印 Controller 方法路径
     */
    private void printHandlerMethodPosition(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return;
        }

        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        try {
            // 获取 method 的 lineNumber
            List<String> clazzContents = FileUtil.readUtf8Lines(
                    ResourceUtil.getResource(null, clazz)
                            .getPath()
                            .replace("/target/classes/", "/src/main/java/")
                            + clazz.getSimpleName() + ".java"
            );

            Optional<Integer> lineNumber = IntStream.range(0, clazzContents.size())
                    // 简单匹配, 不考虑方法重名
                    .filter(i -> clazzContents.get(i).contains(" " + method.getName() + "("))
                    // 行号从 1 开始
                    .mapToObj(i -> i + 1)
                    .findFirst();

            if (lineNumber.isEmpty()) {
                return;
            }

            // 打印结果
            log.info("\tController 方法路径: {}({}.java:{})", clazz.getName(), clazz.getSimpleName(), lineNumber.get());
        } catch (Exception ignore) {
            // 忽略异常。原因：仅仅打印，非重要逻辑
        }
    }
}
