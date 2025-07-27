package cn.jcodenest.initializer.common.exception;

import lombok.Data;

/**
 * 错误码
 *
 * <p>全局错误码: [0, 999] , {@link GlobalErrorCodeConstants}</p>
 * <p>业务异常错误码: [1 000 000 000, +∞) , {@link ServiceErrorCodeRange}</p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    /**
     * 通过错误码和错误提示创建异常
     *
     * @param code 错误码
     * @param message 错误提示
     */
    public ErrorCode(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }
}
