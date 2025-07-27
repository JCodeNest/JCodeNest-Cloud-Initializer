package cn.jcodenest.framework.common.biz.system.tenant;

import cn.jcodenest.framework.common.constants.RpcConstants;
import cn.jcodenest.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 多租户 API
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Tag(name = "RPC 服务 - 多租户")
@FeignClient(name = RpcConstants.SYSTEM_NAME)
public interface TenantCommonApi {

    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/tenant";

    @GetMapping(PREFIX + "/id-list")
    @Operation(summary = "获得所有租户编号")
    CommonResult<List<Long>> getTenantIdList();

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验租户是否合法")
    @Parameter(name = "id", description = "租户编号", required = true, example = "1024")
    CommonResult<Boolean> validTenant(@RequestParam("id") Long id);
}
