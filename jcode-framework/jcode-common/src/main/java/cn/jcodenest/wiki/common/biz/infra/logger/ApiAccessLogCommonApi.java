package cn.jcodenest.wiki.common.biz.infra.logger;

import cn.jcodenest.wiki.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import cn.jcodenest.wiki.common.constants.RpcConstants;
import cn.jcodenest.wiki.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * API 访问日志通用 API
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Tag(name = "RPC 服务 - API 访问日志")
@FeignClient(name = RpcConstants.INFRA_NAME)
public interface ApiAccessLogCommonApi {

    String PREFIX = RpcConstants.INFRA_PREFIX + "/api-access-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建 API 访问日志")
    CommonResult<Boolean> createApiAccessLog(@Valid @RequestBody ApiAccessLogCreateReqDTO createDTO);

    /**
     * 【异步】创建 API 访问日志
     *
     * @param createDTO 访问日志 DTO
     */
    @Async
    default void createApiAccessLogAsync(ApiAccessLogCreateReqDTO createDTO) {
        createApiAccessLog(createDTO).checkError();
    }
}
