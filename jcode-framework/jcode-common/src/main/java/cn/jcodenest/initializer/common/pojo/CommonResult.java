package cn.jcodenest.initializer.common.pojo;

import cn.hutool.core.lang.Assert;
import cn.jcodenest.initializer.common.exception.ErrorCode;
import cn.jcodenest.initializer.common.exception.ServiceException;
import cn.jcodenest.initializer.common.exception.enums.GlobalErrorCodeConstants;
import cn.jcodenest.initializer.common.util.exception.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回对象
 *
 * @param <T> 响应数据泛型
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     * @see ErrorCode#getCode()
     */
    private Integer code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 用户友好的错误提示
     *
     * @see ErrorCode#getMsg()
     */
    private String msg;

    /**
     * 成功
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功结果
     */
    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = "";
        return result;
    }

    /**
     * 成功
     *
     * @param data 响应数据
     * @param msg  提示信息
     * @param <T>  响应数据类型
     * @return 成功结果
     */
    public static <T> CommonResult<T> success(T data, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = msg;
        return result;
    }

    /**
     * 失败
     *
     * @param code    错误码
     * @param message 错误提示
     * @param <T>     响应数据类型
     * @return 失败结果
     */
    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.notEquals(GlobalErrorCodeConstants.SUCCESS.getCode(), code, "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param params    参数
     * @param <T>       响应数据类型
     * @return 失败结果
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode, Object... params) {
        Assert.notEquals(GlobalErrorCodeConstants.SUCCESS.getCode(), errorCode.getCode(), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = errorCode.getCode();
        result.msg = ServiceExceptionUtil.doFormat(errorCode.getCode(), errorCode.getMsg(), params);
        return result;
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param <T>       响应数据类型
     * @return 失败结果
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    /**
     * 将传入的 result 对象转换成另外一个泛型结果的对象
     * 使用场景：A 方法返回的 CommonResult 对象, 不满足调用其的 B 方法的返回, 则需要进行转换
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    /**
     * 是否成功
     *
     * @param code 错误码
     * @return true 成功 ｜ false 失败
     */
    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getCode());
    }

    /**
     * 是否成功
     *
     * @return true 成功 ｜ false 失败
     */
    @JsonIgnore // 避免 Jackson 序列化
    public boolean isSuccess() {
        return isSuccess(code);
    }

    /**
     * 是否失败
     *
     * @return true 成功 ｜ false 失败
     */
    @JsonIgnore // 避免 Jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 判断是否有异常, 如果有则抛出 {@link ServiceException} 异常
     */
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }

        // 业务异常
        throw new ServiceException(code, msg);
    }

    /**
     * 判断是否有异常, 如果有则抛出 {@link ServiceException} 异常
     * 如果没有则返回 {@link #data} 数据
     */
    @JsonIgnore // 避免 Jackson 序列化
    public T getCheckedData() {
        checkError();
        return data;
    }
}
