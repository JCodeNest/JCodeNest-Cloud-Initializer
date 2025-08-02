package cn.jcodenest.framework.ip.core;

import cn.jcodenest.framework.ip.core.enums.AreaTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 区域节点，包括国家、省份、城市、地区等信息
 *
 * <p>数据可见 resources/area.csv 文件</p>
 *
 * <p>
 *     @ToString(exclude = {"parent"}) 解释：
 *    【优化】idea 在 debug 时 toString 方法报错 StackOverflowError、指定 jackson 默认序列化时双向引用的前向、后向出口避免死循环报错：
 *     1. idea 在 debug 调试时报错：“Method threw 'java.lang.StackOverflowError' exception. Cannot evaluate com.cyb.common.ip.core.entity.Area.toString()”
 *     2. Area 存在自引用，jackson 默认序列化时报错：“Infinite recursion (StackOverflowError)”
 * </p>
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"parent"})
public class Area {

    /**
     * 编号 - 全球，即根目录
     */
    public static final Integer ID_GLOBAL = 0;

    /**
     * 编号 - 中国
     */
    public static final Integer ID_CHINA = 1;

    /**
     * 编号
     */
    private Integer id;

    /**
     * 名字
     */
    private String name;

    /**
     * 类型
     * <p>
     * 枚举 {@link AreaTypeEnum}
     */
    private Integer type;

    /**
     * 父节点
     */
    @JsonManagedReference
    private Area parent;

    /**
     * 子节点
     */
    @JsonBackReference
    private List<Area> children;
}
