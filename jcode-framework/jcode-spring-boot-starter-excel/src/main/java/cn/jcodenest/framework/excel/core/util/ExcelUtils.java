package cn.jcodenest.framework.excel.core.util;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.converters.longconverter.LongStringConverter;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import cn.jcodenest.framework.common.util.http.HttpUtils;
import cn.jcodenest.framework.excel.core.handler.SelectSheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Excel 工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/8/2
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
public class ExcelUtils {

    /**
     * 将列表以 Excel 响应给前端
     *
     * @param response  响应
     * @param filename  文件名
     * @param sheetName Excel sheet 名
     * @param head      Excel head 头
     * @param data      数据列表哦
     * @param <T>       泛型，保证 head 和 data 类型的一致性
     * @throws IOException 写入失败的情况
     */
    public static <T> void write(HttpServletResponse response, String filename, String sheetName, Class<T> head, List<T> data) throws IOException {
        // 输出 Excel
        FastExcelFactory.write(response.getOutputStream(), head)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                // 基于 column 长度自动适配, 最大 255 宽度
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                // 基于固定 sheet 实现下拉框
                .registerWriteHandler(new SelectSheetWriteHandler(head))
                // 避免 Long 类型丢失精度
                .registerConverter(new LongStringConverter())
                .sheet(sheetName).doWrite(data);

        // 设置 header 和 contentType, 写在最后的原因是避免报错时响应 contentType 已经被修改了
        response.addHeader("Content-Disposition", "attachment;filename=" + HttpUtils.encodeUtf8(filename));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    }

    /**
     * 读取 Excel 文件
     *
     * @param file 文件
     * @param head Excel head 头
     * @param <T>  泛型，保证 head 和 data 类型的一致性
     * @return 列表
     * @throws IOException 读取失败的情况
     */
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return FastExcelFactory.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }
}
