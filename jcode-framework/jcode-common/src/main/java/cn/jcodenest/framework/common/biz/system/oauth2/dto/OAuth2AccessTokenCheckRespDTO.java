package cn.jcodenest.framework.common.biz.system.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 访问令牌校验响应 DTO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Schema(description = "RPC 服务 - OAuth2 访问令牌校验 Response DTO")
public class OAuth2AccessTokenCheckRespDTO implements Serializable {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long userId;

    @Schema(description = "用户类型, 参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer userType;

    @Schema(description = "用户信息", example = "{\"nickname\": \"芋道\"}")
    private Map<String, String> userInfo;

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tenantId;

    @Schema(description = "授权范围的数组", example = "user_info")
    private List<String> scopes;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;
}
