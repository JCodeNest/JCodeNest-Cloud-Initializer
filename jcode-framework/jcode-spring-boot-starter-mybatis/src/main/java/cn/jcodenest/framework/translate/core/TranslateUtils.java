package cn.jcodenest.framework.translate.core;

import cn.hutool.core.collection.CollUtil;
import com.fhs.core.trans.vo.VO;
import com.fhs.trans.service.impl.TransService;

import java.util.List;

/**
 * VO 数据翻译工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/1
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class TranslateUtils {

    /**
     * 翻译服务
     */
    private static TransService transService;

    /**
     * 初始化翻译服务
     *
     * @param transService 翻译服务
     */
    public static void init(TransService transService) {
        TranslateUtils.transService = transService;
    }

    /**
     * 数据翻译
     * <p>
     * 使用场景：无法使用 @TransMethodResult 注解的场景，只能通过手动触发翻译
     *
     * @param data 数据
     * @return 翻译结果
     */
    public static <T extends VO> List<T> translate(List<T> data) {
        if (CollUtil.isNotEmpty((data))) {
            transService.transBatch(data);
        }
        return data;
    }
}
