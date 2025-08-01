package cn.jcodenest.framework.common.biz.system.oauth2.dto;

import cn.jcodenest.framework.common.enums.UserTypeEnum;
import cn.jcodenest.framework.common.validation.annotation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OAuth2 访问令牌创建请求 DTO
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Schema(description = "RPC 服务 - OAuth2 访问令牌创建 Request DTO")
public class OAuth2AccessTokenCreateReqDTO implements Serializable {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "用户类型, 参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户类型不能为空")
    @InEnum(value = UserTypeEnum.class, message = "用户类型必须是 {value}")
    private Integer userType;

    @Schema(description = "客户端编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudaoyuanma")
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @Schema(description = "授权范围的数组", example = "user_info")
    private List<String> scopes;
}
