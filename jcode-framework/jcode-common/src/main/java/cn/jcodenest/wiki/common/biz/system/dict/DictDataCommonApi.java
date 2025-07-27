package cn.jcodenest.wiki.common.biz.system.dict;

import cn.jcodenest.wiki.common.biz.system.dict.dto.DictDataRespDTO;
import cn.jcodenest.wiki.common.constants.RpcConstants;
import cn.jcodenest.wiki.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 字典数据 API
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@FeignClient(name = RpcConstants.SYSTEM_NAME, primary = false)
@Tag(name = "RPC 服务 - 字典数据")
public interface DictDataCommonApi {

    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/dict-data";

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得指定字典类型的字典数据列表")
    @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true)
    CommonResult<List<DictDataRespDTO>> getDictDataList(@RequestParam("dictType") String dictType);
}
