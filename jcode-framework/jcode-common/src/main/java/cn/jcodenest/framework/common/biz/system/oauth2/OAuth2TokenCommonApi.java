package cn.jcodenest.framework.common.biz.system.oauth2;

import cn.jcodenest.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.jcodenest.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.jcodenest.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.jcodenest.framework.common.constants.RpcConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth2 令牌 API
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@FeignClient(name = RpcConstants.SYSTEM_NAME)
@Tag(name = "RPC 服务 - OAuth2.0 令牌")
public interface OAuth2TokenCommonApi {

    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/oauth2/token";

    /**
     * 校验 Token 的 URL 地址, 主要是提供给 Gateway 使用
     */
    @SuppressWarnings("HttpUrlsUsage")
    String URL_CHECK = "http://" + RpcConstants.SYSTEM_NAME + PREFIX + "/check";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建访问令牌")
    CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(@Valid @RequestBody OAuth2AccessTokenCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/check")
    @Operation(summary = "校验访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(@RequestParam("accessToken") String accessToken);

    @DeleteMapping(PREFIX + "/remove")
    @Operation(summary = "移除访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(@RequestParam("accessToken") String accessToken);

    @PutMapping(PREFIX + "/refresh")
    @Operation(summary = "刷新访问令牌")
    @Parameters({
            @Parameter(name = "refreshToken", description = "刷新令牌", required = true, example = "haha"),
            @Parameter(name = "clientId", description = "客户端编号", required = true, example = "jcodenest")
    })
    CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(@RequestParam("refreshToken") String refreshToken, @RequestParam("clientId") String clientId);
}
