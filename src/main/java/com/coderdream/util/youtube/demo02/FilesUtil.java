package com.coderdream.util.youtube.demo02;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FilesUtil {

    /**
     * 安全地删除文件，如果文件存在。
     *
     * @param filePath 要删除的文件路径
     * @throws IOException 如果删除文件时发生错误
     */
    public static void deleteIfExists(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.deleteIfExists(path);
                log.info("文件 {} 删除成功.", filePath);
            } catch (IOException e) {
                log.error("删除文件 {} 失败: {}", filePath, e.getMessage(), e);
                throw e;
            }
        } else {
            log.info("文件 {} 不存在，无需删除.", filePath);
        }
    }
}
