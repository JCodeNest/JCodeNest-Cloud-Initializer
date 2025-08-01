package cn.jcodenest.framework.common.biz.infra.logger;

import cn.jcodenest.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import cn.jcodenest.framework.common.constants.RpcConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * API 异常日志通用 API
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Tag(name = "RPC 服务 - API 异常日志")
@FeignClient(name = RpcConstants.INFRA_NAME)
public interface ApiErrorLogCommonApi {

    String PREFIX = RpcConstants.INFRA_PREFIX + "/api-error-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建 API 异常日志")
    CommonResult<Boolean> createApiErrorLog(@Valid @RequestBody ApiErrorLogCreateReqDTO createDTO);

    /**
     * 【异步】创建 API 异常日志
     *
     * @param createDTO 异常日志 DTO
     */
    @Async
    default void createApiErrorLogAsync(ApiErrorLogCreateReqDTO createDTO) {
        createApiErrorLog(createDTO).checkError();
    }
}
