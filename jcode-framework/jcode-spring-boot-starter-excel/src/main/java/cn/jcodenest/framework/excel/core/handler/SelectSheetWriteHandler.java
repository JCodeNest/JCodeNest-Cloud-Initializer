package cn.jcodenest.framework.excel.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.write.handler.SheetWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteWorkbookHolder;
import cn.jcodenest.framework.common.core.KeyValue;
import cn.jcodenest.framework.common.util.collection.CollectionUtils;
import cn.jcodenest.framework.dict.core.util.DictFrameworkUtils;
import cn.jcodenest.framework.excel.core.annotations.ExcelColumnSelect;
import cn.jcodenest.framework.excel.core.function.ExcelColumnSelectFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于固定 sheet 实现下拉框
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@Slf4j
public class SelectSheetWriteHandler implements SheetWriteHandler {

    /**
     * 数据起始行从 0 开始
     * <p>
     * 约定：本项目第一行有标题所以从 1 开始, 如果您的 Excel 有多行标题请自行更改
     */
    public static final int FIRST_ROW = 1;

    /**
     * 下拉列需要创建下拉框的行数，默认两千行如需更多请自行调整
     */
    public static final int LAST_ROW = 2000;

    /**
     * 字典 sheet 页的名称
     */
    private static final String DICT_SHEET_NAME = "字典sheet";

    /**
     * key: 列
     * value: 下拉数据源
     */
    private final Map<Integer, List<String>> selectMap = new HashMap<>();

    /**
     * 构造函数，用于解析下拉数据
     *
     * @param head Excel 表头
     */
    public SelectSheetWriteHandler(Class<?> head) {
        // 解析下拉数据
        int colIndex = 0;
        boolean ignoreUnannotated = head.isAnnotationPresent(ExcelIgnoreUnannotated.class);

        for (Field field : head.getDeclaredFields()) {
            // 检查是否需要跳过字段
            if (isStaticFinalOrTransient(field) ||
                    (ignoreUnannotated && !field.isAnnotationPresent(ExcelProperty.class)) ||
                    field.isAnnotationPresent(ExcelIgnore.class)) {
                colIndex++;
                continue;
            }

            // 核心：处理有 ExcelColumnSelect 注解的字段
            if (field.isAnnotationPresent(ExcelColumnSelect.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                if (excelProperty != null && excelProperty.index() != -1) {
                    colIndex = excelProperty.index();
                }
                getSelectDataList(colIndex, field);
            }
            colIndex++;
        }
    }

    /**
     * 判断字段是否是静态的、最终的、transient 的
     * 原因：FastExcel 默认是忽略 static final 或 transient 的字段，所以需要判断
     *
     * @param field 字段
     * @return 是否是静态的、最终的、transient 的
     */
    private boolean isStaticFinalOrTransient(Field field) {
        return (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()))
                || Modifier.isTransient(field.getModifiers());
    }


    /**
     * 获得下拉数据，并添加到 {@link #selectMap} 中
     *
     * @param colIndex 列索引
     * @param field    字段
     */
    private void getSelectDataList(int colIndex, Field field) {
        ExcelColumnSelect columnSelect = field.getAnnotation(ExcelColumnSelect.class);
        String dictType = columnSelect.dictType();
        String functionName = columnSelect.functionName();
        Assert.isTrue(ObjectUtil.isNotEmpty(dictType) || ObjectUtil.isNotEmpty(functionName),
                "Field({}) 的 @ExcelColumnSelect 注解，dictType 和 functionName 不能同时为空", field.getName());

        // 情况一：使用 dictType 获得下拉数据
        if (StrUtil.isNotEmpty(dictType)) {
            selectMap.put(colIndex, DictFrameworkUtils.getDictDataLabelList(dictType));
            return;
        }

        // 情况二：使用 functionName 获得下拉数据
        Map<String, ExcelColumnSelectFunction> functionMap = SpringUtil.getApplicationContext().getBeansOfType(ExcelColumnSelectFunction.class);
        ExcelColumnSelectFunction function = CollUtil.findOne(functionMap.values(), item -> item.getName().equals(functionName));
        Assert.notNull(function, "未找到对应的 function({})", functionName);
        selectMap.put(colIndex, function.getOptions());
    }

    /**
     * 创建下拉框
     *
     * @param writeWorkbookHolder 工作簿信息
     * @param writeSheetHolder    工作表信息
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (CollUtil.isEmpty(selectMap)) {
            return;
        }

        // 获取相应操作对象
        // 需要设置下拉框的 sheet 页的数据验证助手
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
        // 获得工作簿
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        List<KeyValue<Integer, List<String>>> keyValues = CollectionUtils.convertList(selectMap.entrySet(), entry -> new KeyValue<>(entry.getKey(), entry.getValue()));
        // 升序不然创建下拉会报错
        keyValues.sort(Comparator.comparing(item -> item.getValue().size()));

        // 创建数据字典的 sheet 页
        Sheet dictSheet = workbook.createSheet(DICT_SHEET_NAME);
        for (KeyValue<Integer, List<String>> keyValue : keyValues) {
            int rowLength = keyValue.getValue().size();
            // 设置字典 sheet 页的值，每一列一个字典项
            for (int i = 0; i < rowLength; i++) {
                Row row = dictSheet.getRow(i);
                if (row == null) {
                    row = dictSheet.createRow(i);
                }
                row.createCell(keyValue.getKey()).setCellValue(keyValue.getValue().get(i));
            }
            // 设置单元格下拉选择
            setColumnSelect(writeSheetHolder, workbook, helper, keyValue);
        }
    }

    /**
     * 设置单元格下拉选择
     *
     * @param writeSheetHolder 工作表信息
     * @param workbook         工作簿信息
     * @param helper           数据验证助手
     * @param keyValue         列索引和下拉数据
     */
    private static void setColumnSelect(WriteSheetHolder writeSheetHolder, Workbook workbook, DataValidationHelper helper,
                                        KeyValue<Integer, List<String>> keyValue) {
        // 创建可被其他单元格引用的名称
        Name name = workbook.createName();
        String excelColumn = ExcelUtil.indexToColName(keyValue.getKey());

        // 下拉框数据来源 eg:字典sheet!$B1:$B2
        String refers = DICT_SHEET_NAME + "!$" + excelColumn + "$1:$" + excelColumn + "$" + keyValue.getValue().size();
        // 设置名称的名字
        name.setNameName("dict" + keyValue.getKey());
        // 设置公式
        name.setRefersToFormula(refers);

        // 设置约束
        // 设置引用约束
        DataValidationConstraint constraint = helper.createFormulaListConstraint("dict" + keyValue.getKey());

        // 设置下拉单元格的首行、末行、首列、末列
        CellRangeAddressList rangeAddressList = new CellRangeAddressList(FIRST_ROW, LAST_ROW, keyValue.getKey(), keyValue.getKey());
        DataValidation validation = helper.createValidation(constraint, rangeAddressList);
        if (validation instanceof HSSFDataValidation) {
            validation.setSuppressDropDownArrow(false);
        } else {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        }

        // 阻止输入非下拉框的值
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("提示", "此值不存在于下拉选择中！");

        // 添加下拉框约束
        writeSheetHolder.getSheet().addValidationData(validation);
    }
}
