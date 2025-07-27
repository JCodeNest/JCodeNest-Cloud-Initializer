package cn.jcodenest.initializer.common.exception;

import cn.jcodenest.initializer.common.exception.enums.GlobalErrorCodeConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义服务器异常
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerException extends RuntimeException {

    /**
     * 全局错误码
     *
     * @see GlobalErrorCodeConstants
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法, 避免反序列化问题
     */
    public ServerException() {
    }

    /**
     * 通过错误码创建异常
     *
     * @param errorCode 错误码
     */
    public ServerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 通过错误码和错误提示创建异常
     *
     * @param code    错误码
     * @param message 错误提示
     */
    public ServerException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     * @return this
     */
    public ServerException setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ServerException setMessage(String message) {
        this.message = message;
        return this;
    }
}
