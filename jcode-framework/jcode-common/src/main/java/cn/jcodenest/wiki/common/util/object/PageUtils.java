package cn.jcodenest.wiki.common.util.object;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.jcodenest.wiki.common.pojo.PageParam;
import cn.jcodenest.wiki.common.pojo.SortablePageParam;
import cn.jcodenest.wiki.common.pojo.SortingField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static java.util.Collections.singletonList;

/**
 * 分页工具类
 * {@link PageParam}
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

    /**
     * 排序类型
     */
    private static final Object[] ORDER_TYPES = new String[]{
            SortingField.ORDER_ASC,
            SortingField.ORDER_DESC
    };

    /**
     * 获取分页查询的起始位置
     *
     * @param pageParam 分页参数
     * @return 起始位置
     */
    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

    /**
     * 构建排序字段（默认倒序）
     *
     * @param func 排序字段的 Lambda 表达式
     * @param <T>  排序字段所属的类型
     * @return 排序字段
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func) {
        return buildSortingField(func, SortingField.ORDER_DESC);
    }

    /**
     * 构建排序字段
     *
     * @param func  排序字段的 Lambda 表达式
     * @param order 排序类型 {@link SortingField#ORDER_ASC} {@link SortingField#ORDER_DESC}
     * @param <T>   排序字段所属的类型
     * @return 排序字段
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func, String order) {
        Assert.isTrue(ArrayUtil.contains(ORDER_TYPES, order), String.format("字段的排序类型只能是 %s/%s", ORDER_TYPES));

        String fieldName = LambdaUtil.getFieldName(func);
        return new SortingField(fieldName, order);
    }

    /**
     * 构建默认的排序字段
     * 如果排序字段为空, 则设置排序字段, 否则忽略
     *
     * @param sortablePageParam 排序分页查询参数
     * @param func              排序字段的 Lambda 表达式
     * @param <T>               排序字段所属的类型
     */
    public static <T> void buildDefaultSortingField(SortablePageParam sortablePageParam, Func1<T, ?> func) {
        if (sortablePageParam != null && CollUtil.isEmpty(sortablePageParam.getSortingFields())) {
            sortablePageParam.setSortingFields(singletonList(buildSortingField(func)));
        }
    }
}
