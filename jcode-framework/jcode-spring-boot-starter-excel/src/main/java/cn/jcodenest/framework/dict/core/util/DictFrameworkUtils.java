package cn.jcodenest.framework.dict.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.jcodenest.framework.common.biz.system.dict.DictDataCommonApi;
import cn.jcodenest.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.jcodenest.framework.common.util.cache.CacheUtils;
import cn.jcodenest.framework.common.util.collection.CollectionUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * 字典工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class DictFrameworkUtils {

    /**
     * 字典数据 API
     */
    private static DictDataCommonApi dictDataApi;

    /**
     * 针对 dictType 的字段数据缓存
     */
    private static final LoadingCache<String, List<DictDataRespDTO>> GET_DICT_DATA_CACHE = CacheUtils.buildAsyncReloadingCache(
            // 过期时间 1 分钟
            Duration.ofMinutes(1L),
            new CacheLoader<>() {

                /**
                 * 获取字典数据
                 *
                 * @param dictType 字典类型
                 * @return 字典数据
                 */
                @Override
                public List<DictDataRespDTO> load(String dictType) {
                    return dictDataApi.getDictDataList(dictType).getCheckedData();
                }
            });

    /**
     * 初始化字典数据
     *
     * @param dictDataApi 字典数据 API
     */
    public static void init(DictDataCommonApi dictDataApi) {
        DictFrameworkUtils.dictDataApi = dictDataApi;
        log.info("[init][初始化 DictFrameworkUtils 成功]");
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        GET_DICT_DATA_CACHE.invalidateAll();
    }

    /**
     * 解析字典数据标签
     *
     * @param dictType 字典类型
     * @param value 字典值
     * @return 字典标签
     */
    @SneakyThrows
    public static String parseDictDataLabel(String dictType, Integer value) {
        if (value == null) {
            return null;
        }
        return parseDictDataLabel(dictType, String.valueOf(value));
    }

    /**
     * 解析字典数据标签
     *
     * @param dictType 字典类型
     * @param value 字典值
     * @return 字典标签
     */
    @SneakyThrows
    public static String parseDictDataLabel(String dictType, String value) {
        List<DictDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictDataRespDTO dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getValue(), value));
        return dictData != null ? dictData.getLabel() : null;
    }

    /**
     * 获得字典数据标签数组
     *
     * @param dictType 字典类型
     * @return 字典标签数组
     */
    @SneakyThrows
    public static List<String> getDictDataLabelList(String dictType) {
        List<DictDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return CollectionUtils.convertList(dictDatas, DictDataRespDTO::getLabel);
    }

    /**
     * 解析字典数据值
     *
     * @param dictType 字典类型
     * @param label 字典标签
     * @return 字典值
     */
    @SneakyThrows
    public static String parseDictDataValue(String dictType, String label) {
        List<DictDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        DictDataRespDTO dictData = CollUtil.findOne(dictDatas, data -> Objects.equals(data.getLabel(), label));
        return dictData != null ? dictData.getValue() : null;
    }

    /**
     * 获得字典数据值数组
     *
     * @param dictType 字典类型
     * @return 字典值数组
     */
    @SneakyThrows
    public static List<String> getDictDataValueList(String dictType) {
        List<DictDataRespDTO> dictDatas = GET_DICT_DATA_CACHE.get(dictType);
        return CollectionUtils.convertList(dictDatas, DictDataRespDTO::getValue);
    }
}
