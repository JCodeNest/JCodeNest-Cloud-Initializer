package cn.jcodenest.wiki.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    @Schema(description = "数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long total;

    /**
     * 空构造方法, 避免反序列化问题
     */
    public PageResult() {
    }

    /**
     * 构造方法
     *
     * @param list  数据
     * @param total 总量
     */
    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    /**
     * 构造方法
     *
     * @param total 总量
     */
    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    /**
     * 创建空分页
     *
     * @return 空分页
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    /**
     * 创建空分页
     *
     * @param total 总量
     * @return 空分页
     */
    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }
}
