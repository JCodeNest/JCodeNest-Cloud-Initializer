package cn.jcodenest.framework.excel.core.convert;

import cn.hutool.core.convert.Convert;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import cn.jcodenest.framework.excel.core.annotations.DictFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel 数据字典转换器
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class DictConvert implements Converter<Object> {

    /**
     * 获取支持的 Java 类
     *
     * @return {@link Class}
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    /**
     * 获取支持的 Excel 类
     *
     * @return {@link Class}
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    /**
     * 将 Excel 转换成 Java
     *
     * @param readCellData       读取到的单元格数据
     * @param contentProperty    内容属性
     * @param globalConfiguration 全局配置
     * @return 转换后的数据
     */
    @Override
    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 使用字典解析
        String type = getType(contentProperty);
        String label = readCellData.getStringValue();
        String value = DictFrameworkUtils.parseDictDataValue(type, label);
        if (value == null) {
            log.error("[convertToJavaData][type({}) 解析不掉 label({})]", type, label);
            return null;
        }

        // 将 String 的 value 转换成对应的属性
        Class<?> fieldClazz = contentProperty.getField().getType();
        return Convert.convert(fieldClazz, value);
    }

    /**
     * 将 Java 转换成 Excel
     *
     * @param object              Java 对象
     * @param contentProperty     内容属性
     * @param globalConfiguration 全局配置
     * @return 填充后的单元格数据
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 空时，返回空
        if (object == null) {
            return new WriteCellData<>("");
        }

        // 使用字典格式化
        String type = getType(contentProperty);
        String value = String.valueOf(object);
        String label = DictFrameworkUtils.parseDictDataLabel(type, value);
        if (label == null) {
            log.error("[convertToExcelData][type({}) 转换不了 label({})]", type, value);
            return new WriteCellData<>("");
        }

        // 生成 Excel 小表格
        return new WriteCellData<>(label);
    }

    /**
     * 获得字典类型
     *
     * @param contentProperty 内容属性
     * @return 字典类型
     */
    private static String getType(ExcelContentProperty contentProperty) {
        return contentProperty.getField().getAnnotation(DictFormat.class).value();
    }
}
