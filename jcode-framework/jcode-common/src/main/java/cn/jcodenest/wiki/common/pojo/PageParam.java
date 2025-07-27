package cn.jcodenest.wiki.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页参数
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Schema(description = "分页参数")
public class PageParam implements Serializable {

    /**
     * 默认页码
     */
    private static final Integer PAGE_NO = 1;

    /**
     * 默认每页条数
     */
    private static final Integer PAGE_SIZE = 10;

    /**
     * 不分页
     * 使用场景：导出接口可以设置 {@link #pageSize} 为 -1 不分页, 查询所有数据
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "页码, 从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "每页条数, 最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    private Integer pageSize = PAGE_SIZE;
}
