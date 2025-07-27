package cn.jcodenest.wiki.common.util.exception;

import cn.jcodenest.wiki.common.exception.ErrorCode;
import cn.jcodenest.wiki.common.exception.ServiceException;
import cn.jcodenest.wiki.common.exception.enums.GlobalErrorCodeConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常工具类 {@link ServiceException}
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class ServiceExceptionUtil {

    // ========== 集成 ServiceException ==========

    public static ServiceException exception(ErrorCode errorCode) {
        return exception0(errorCode.getCode(), errorCode.getMsg());
    }

    public static ServiceException exception(ErrorCode errorCode, Object... params) {
        return exception0(errorCode.getCode(), errorCode.getMsg(), params);
    }

    public static ServiceException exception0(Integer code, String messagePattern, Object... params) {
        String message = doFormat(code, messagePattern, params);
        return new ServiceException(code, message);
    }

    public static ServiceException invalidParamException(String messagePattern, Object... params) {
        return exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), messagePattern, params);
    }

    // ========== 格式化方法 ==========

    /**
     * 将错误编号对应的消息使用 params 进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    @VisibleForTesting
    public static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder buff = new StringBuilder(messagePattern.length() + 50);

        int i = 0;
        int j;
        int l;

        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    buff.append(messagePattern.substring(i));
                    return buff.toString();
                }
            } else {
                buff.append(messagePattern, i, j);
                buff.append(params[l]);
                i = j + 2;
            }
        }

        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
        }

        buff.append(messagePattern.substring(i));
        return buff.toString();
    }
}
