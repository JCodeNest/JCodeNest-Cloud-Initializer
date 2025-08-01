package cn.jcodenest.framework.common.biz.system.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

/**
 * 部门数据权限响应 DTO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Accessors(chain=true)
@Schema(description = "RPC 服务 - 部门的数据权限 Response DTO")
public class DeptDataPermissionRespDTO {

    @Schema(description = "是否可查看全部数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean all;

    @Schema(description = "是否可查看自己的数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean self;

    @Schema(description = "可查看的部门编号数组", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 3]")
    private Set<Long> deptIds;

    public DeptDataPermissionRespDTO() {
        this.all = false;
        this.self = false;
        this.deptIds = new HashSet<>();
    }
}
