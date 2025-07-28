package cn.jcodenest.framework.web.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.jcodenest.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import cn.jcodenest.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import cn.jcodenest.framework.common.exception.ServiceException;
import cn.jcodenest.framework.common.pojo.CommonResult;
import cn.jcodenest.framework.common.util.collection.SetUtils;
import cn.jcodenest.framework.common.util.exception.ServiceExceptionUtil;
import cn.jcodenest.framework.common.util.json.JsonUtils;
import cn.jcodenest.framework.common.util.monitor.TracerUtils;
import cn.jcodenest.framework.common.util.servlet.ServletUtils;
import cn.jcodenest.framework.web.core.util.WebFrameworkUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.jcodenest.framework.common.exception.enums.GlobalErrorCodeConstants.*;

/**
 * 全局异常处理器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    /**
     * 忽略的 ServiceException 错误提示, 避免打印过多 logger
     */
    public static final Set<String> IGNORE_ERROR_MESSAGES = SetUtils.asSet("无效的刷新令牌");

    /**
     * 应用名
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final String applicationName;

    private final ApiErrorLogCommonApi apiErrorLogApi;

    /**
     * 处理所有异常
     *
     * <p>
     * 主要是提供给 Filter 使用, 因为 Filter 不走 SpringMVC 的流程, 但是我们又需要兜底处理异常,
     * 所以这里提供一个全量的异常处理过程保持逻辑统一
     * </p>
     *
     * @param request 请求
     * @param ex      异常
     * @return {@link CommonResult}
     */
    public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
        if (ex instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            return missingServletRequestParameterExceptionHandler(missingServletRequestParameterException);
        }
        if (ex instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            return methodArgumentTypeMismatchExceptionHandler(methodArgumentTypeMismatchException);
        }
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            return methodArgumentNotValidExceptionExceptionHandler(methodArgumentNotValidException);
        }
        if (ex instanceof BindException bindException) {
            return bindExceptionHandler(bindException);
        }
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationExceptionHandler(constraintViolationException);
        }
        if (ex instanceof ValidationException validationException) {
            return validationException(validationException);
        }
        if (ex instanceof NoHandlerFoundException noHandlerFoundException) {
            return noHandlerFoundExceptionHandler(noHandlerFoundException);
        }
        if (ex instanceof NoResourceFoundException noResourceFoundException) {
            return noResourceFoundExceptionHandler(request, noResourceFoundException);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
            return httpRequestMethodNotSupportedExceptionHandler(httpRequestMethodNotSupportedException);
        }
        if (ex instanceof ServiceException serviceException) {
            return serviceExceptionHandler(serviceException);
        }
        if (ex instanceof AccessDeniedException accessDeniedException) {
            return accessDeniedExceptionHandler(request, accessDeniedException);
        }

        return defaultExceptionHandler(request, ex);
    }

    /**
     * 处理 SpringMVC 请求参数缺失
     * 示例：接口上设置了 @RequestParam("xx"x) 参数, 实际客户端请求时并未传递
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 请求参数校验错误
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);

        String errorMessage = null;
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            // 组合校验, 借鉴 https://t.zsxq.com/3HVTx
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            if (CollUtil.isNotEmpty(allErrors)) {
                errorMessage = allErrors.get(0).getDefaultMessage();
            }
        } else {
            errorMessage = fieldError.getDefaultMessage();
        }

        if (StrUtil.isEmpty(errorMessage)) {
            return CommonResult.error(BAD_REQUEST);
        }

        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", errorMessage));
    }

    /**
     * 处理 SpringMVC 请求参数绑定错误, 本质还是通过 Validator 校验
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        assert fieldError != null;
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<?> methodArgumentTypeInvalidFormatExceptionHandler(HttpMessageNotReadableException ex) {
        log.warn("[methodArgumentTypeInvalidFormatExceptionHandler]", ex);
        Throwable exCause = ex.getCause();
        if (exCause instanceof InvalidFormatException invalidFormatException) {
            return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", invalidFormatException.getValue()));
        } else {
            return defaultExceptionHandler(ServletUtils.getRequest(), ex);
        }
    }

    /**
     * 处理 SpringMVC 请求参数校验错误
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }

    /**
     * 处理 Dubbo 请求参数校验错误
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = ValidationException.class)
    public CommonResult<?> validationException(ValidationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        // 无法拼接明细的错误信息, 因为 Dubbo Consumer 抛出 ValidationException 异常时是直接的字符串信息, 且人类不可读
        return CommonResult.error(BAD_REQUEST);
    }

    /**
     * 处理 SpringMVC 未找到页面异常
     *
     * <p>
     * 需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found = true
     * 2. spring.mvc.static-path-pattern = /static/**
     * </p>
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.warn("[noHandlerFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getRequestURL()));
    }

    /**
     * 处理 SpringMVC 未找到页面异常
     *
     * <p>
     * 需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found = true
     * 2. spring.mvc.static-path-pattern = /static/**
     * </p>
     *
     * @param req 请求
     * @param ex  异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(NoResourceFoundException.class)
    private CommonResult<?> noResourceFoundExceptionHandler(HttpServletRequest req, NoResourceFoundException ex) {
        log.warn("[noResourceFoundExceptionHandler]", ex);
        return CommonResult.error(NOT_FOUND.getCode(), String.format("请求地址不存在:%s", ex.getResourcePath()));
    }

    /**
     * 处理 SpringMVC 请求方法不正确异常
     * 示例: 接口上设置了 @PostMapping, 但是使用 @GetMapping 请求
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return CommonResult.error(METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringSecurity 权限不足异常
     * 来源: 使用 @PreAuthorize 注解, AOP 处理权限拦截
     *
     * @param req 请求
     * @param ex  异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]", WebFrameworkUtils.getLoginUserId(req), req.getRequestURL(), ex);
        return CommonResult.error(FORBIDDEN);
    }

    /**
     * 处理业务异常 ServiceException
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        // 不包含的时候才进行打印, 避免 ex 堆栈过多, 即使打印也只打印第一层 StackTraceElement 并且使用 warn 在控制台输出
        if (!IGNORE_ERROR_MESSAGES.contains(ex.getMessage())) {
            try {
                StackTraceElement[] stackTraces = ex.getStackTrace();
                for (StackTraceElement stackTrace : stackTraces) {
                    if (ObjUtil.notEqual(stackTrace.getClassName(), ServiceExceptionUtil.class.getName())) {
                        log.warn("[serviceExceptionHandler]\n\t{}", stackTrace);
                        break;
                    }
                }
            } catch (Exception ignored) {
                // 忽略日志, 避免影响主流程
            }
        }

        return CommonResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理未知异常(系统异常, 最后兜底)
     *
     * @param req 请求
     * @param ex  异常
     * @return {@link CommonResult}
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
        // 情况一：处理表不存在的异常
        CommonResult<?> tableNotExistsResult = handleTableNotExists(ex);
        if (tableNotExistsResult != null) {
            return tableNotExistsResult;
        }

        // 情况二：处理默认异常
        log.error("[defaultExceptionHandler]", ex);

        // 插入异常日志
        createExceptionLog(req, ex);

        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

    /**
     * 创建异常日志
     *
     * @param req 请求
     * @param e   异常
     */
    private void createExceptionLog(HttpServletRequest req, Throwable e) {
        ApiErrorLogCreateReqDTO errorLog = new ApiErrorLogCreateReqDTO();

        try {
            // 初始化 errorLog
            buildExceptionLog(errorLog, req, e);
            // 执行插入 errorLog
            apiErrorLogApi.createApiErrorLogAsync(errorLog);
        } catch (Throwable th) {
            log.error("[createExceptionLog][url({}) log({}) 发生异常]", req.getRequestURI(), JsonUtils.toJsonString(errorLog), th);
        }
    }

    /**
     * 构建异常日志
     *
     * @param errorLog 异常日志
     * @param request  请求
     * @param e        异常
     */
    private void buildExceptionLog(ApiErrorLogCreateReqDTO errorLog, HttpServletRequest request, Throwable e) {
        // 处理用户信息
        errorLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        errorLog.setUserType(WebFrameworkUtils.getLoginUserType(request));

        // 设置异常字段
        errorLog.setExceptionName(e.getClass().getName());
        errorLog.setExceptionMessage(ExceptionUtil.getMessage(e));
        errorLog.setExceptionRootCauseMessage(ExceptionUtil.getRootCauseMessage(e));
        errorLog.setExceptionStackTrace(ExceptionUtil.stacktraceToString(e));
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        Assert.notEmpty(stackTraceElements, "异常 stackTraceElements 不能为空");
        StackTraceElement stackTraceElement = stackTraceElements[0];
        errorLog.setExceptionClassName(stackTraceElement.getClassName());
        errorLog.setExceptionFileName(stackTraceElement.getFileName());
        errorLog.setExceptionMethodName(stackTraceElement.getMethodName());
        errorLog.setExceptionLineNumber(stackTraceElement.getLineNumber());

        // 设置其它字段
        errorLog.setTraceId(TracerUtils.getTraceId());
        errorLog.setApplicationName(applicationName);
        errorLog.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                .put("query", JakartaServletUtil.getParamMap(request))
                .put("body", JakartaServletUtil.getBody(request)).build();
        errorLog.setRequestParams(JsonUtils.toJsonString(requestParams));
        errorLog.setRequestMethod(request.getMethod());
        errorLog.setUserAgent(ServletUtils.getUserAgent(request));
        errorLog.setUserIp(JakartaServletUtil.getClientIP(request));
        errorLog.setExceptionTime(LocalDateTime.now());
    }

    /**
     * 处理表不存在的异常
     *
     * @param ex 异常
     * @return {@link CommonResult}
     */
    private CommonResult<?> handleTableNotExists(Throwable ex) {
        String message = ExceptionUtil.getRootCauseMessage(ex);
        if (!message.contains("doesn't exist")) {
            return null;
        }

        // 1. 数据报表
        if (message.contains("report_")) {
            log.error("[报表模块 jcode-module-report - 表结构未导入][参考 https://www.jcodenest.cn/report/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[报表模块 jcode-module-report - 表结构未导入][参考 https://www.jcodenest.cn/report/ 开启]"
            );
        }

        // 2. 工作流
        if (message.contains("bpm_")) {
            log.error("[工作流模块 jcode-module-bpm - 表结构未导入][参考 https://www.jcodenest.cn/bpm/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[工作流模块 jcode-module-bpm - 表结构未导入][参考 https://www.jcodenest.cn/bpm/ 开启]"
            );
        }

        // 3. 微信公众号
        if (message.contains("mp_")) {
            log.error("[微信公众号 jcode-module-mp - 表结构未导入][参考 https://www.jcodenest.cn/mp/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[微信公众号 jcode-module-mp - 表结构未导入][参考 https://www.jcodenest.cn/mp/build/ 开启]"
            );
        }

        // 4. 商城系统
        if (StrUtil.containsAny(message, "product_", "promotion_", "trade_")) {
            log.error("[商城系统 jcode-module-mall - 已禁用][参考 https://www.jcodenest.cn/mall/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[商城系统 jcode-module-mall - 已禁用][参考 https://www.jcodenest.cn/mall/build/ 开启]"
            );
        }

        // 5. ERP 系统
        if (message.contains("erp_")) {
            log.error("[ERP 系统 jcode-module-erp - 表结构未导入][参考 https://www.jcodenest.cn/erp/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[ERP 系统 jcode-module-erp - 表结构未导入][参考 https://www.jcodenest.cn/erp/build/ 开启]"
            );
        }

        // 6. CRM 系统
        if (message.contains("crm_")) {
            log.error("[CRM 系统 jcode-module-crm - 表结构未导入][参考 https://www.jcodenest.cn/crm/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[CRM 系统 jcode-module-crm - 表结构未导入][参考 https://www.jcodenest.cn/crm/build/ 开启]"
            );
        }

        // 7. 支付平台
        if (message.contains("pay_")) {
            log.error("[支付模块 jcode-module-pay - 表结构未导入][参考 https://www.jcodenest.cn/pay/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[支付模块 jcode-module-pay - 表结构未导入][参考 https://www.jcodenest.cn/pay/build/ 开启]"
            );
        }

        // 8. AI 大模型
        if (message.contains("ai_")) {
            log.error("[AI 大模型 jcode-module-ai - 表结构未导入][参考 https://www.jcodenest.cn/ai/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[AI 大模型 jcode-module-ai - 表结构未导入][参考 https://www.jcodenest.cn/ai/build/ 开启]"
            );
        }

        // 9. IOT 物联网
        if (message.contains("iot_")) {
            log.error("[IoT 物联网 jcode-module-iot - 表结构未导入][参考 https://www.jcodenest.cn/iot/build/ 开启]");
            return CommonResult.error(
                    NOT_IMPLEMENTED.getCode(),
                    "[IoT 物联网 jcode-module-iot - 表结构未导入][参考 https://www.jcodenest.cn/iot/build/ 开启]"
            );
        }

        return null;
    }
}
