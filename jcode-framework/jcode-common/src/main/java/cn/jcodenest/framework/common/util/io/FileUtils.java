package cn.jcodenest.framework.common.util.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;

/**
 * 文件工具类
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/27
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(String data) {
        File file = createTempFile();
        FileUtil.writeUtf8String(data, file);
        return file;
    }

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(byte[] data) {
        File file = createTempFile();
        FileUtil.writeBytes(data, file);
        return file;
    }

    /**
     * 创建临时文件, 无内容
     * 该文件会在 JVM 退出时进行删除
     *
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile() {
        File file = File.createTempFile(IdUtil.simpleUUID(), null);
        // 标记 JVM 退出时自动删除
        file.deleteOnExit();
        return file;
    }
}
