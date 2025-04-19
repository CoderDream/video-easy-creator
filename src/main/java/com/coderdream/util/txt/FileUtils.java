package com.coderdream.util.txt;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*; // 使用 nio 包，更现代和推荐
import java.util.stream.Stream;

/**
 * 文件操作工具类
 */
public class FileUtils {

    // 私有构造函数，防止外部实例化工具类
    private FileUtils() {
    }

    /**
     * 在指定基础路径下的所有直接子文件夹中创建或覆盖一个名为 temp.txt 的文件，
     * 并写入指定内容。
     *
     * @param basePathString 目标基础目录的路径字符串 (例如 "D:\\14_LearnEnglish\\6MinuteEnglish\\2025")
     * @param fileName 要创建的文件名 (例如 "temp.txt")
     * @param content 要写入文件的文本内容 (例如 "hello world!")
     */
    public static void addTextFileToSubfolders(String basePathString, String fileName, String content) {
        // 1. 将字符串路径转换为 Path 对象
        Path basePath = Paths.get(basePathString);

        // 2. 检查基础路径是否存在且是一个目录
        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            System.err.println("错误：指定的基础路径不存在或不是一个有效的目录: " + basePathString);
            return; // 路径无效，直接返回
        }

        System.out.println("开始处理目录: " + basePath);

        // 3. 使用 try-with-resources 确保 DirectoryStream 被正确关闭
        //    Files.list() 返回一个 Stream<Path>，代表目录中的条目
        try (Stream<Path> stream = Files.list(basePath)) {
            stream
                // 4. 筛选出 basePath 下的直接子目录
                .filter(Files::isDirectory) // 方法引用，等同于 path -> Files.isDirectory(path)
                // 5. 对每个子目录执行操作
                .forEach(subDirectory -> {
                    // 构造目标文件的完整路径
                    Path targetFilePath = subDirectory.resolve(fileName); // 例如 D:\...\2025\Subfolder1\temp.txt

                    try {
                        // 6. 写入文件内容
                        // Files.writeString 提供了一种简洁的方式写入文本文件
                        // StandardOpenOption.CREATE: 如果文件不存在则创建
                        // StandardOpenOption.TRUNCATE_EXISTING: 如果文件已存在，则清空内容再写入（覆盖）
                        // StandardOpenOption.WRITE: 以写入模式打开文件
                        Files.writeString(targetFilePath, content + System.lineSeparator(), StandardCharsets.UTF_8,
                                          StandardOpenOption.CREATE,
                                          StandardOpenOption.TRUNCATE_EXISTING,
                                          StandardOpenOption.WRITE);

                        System.out.println("  成功在子目录 '" + subDirectory.getFileName() + "' 中创建/更新文件: " + targetFilePath.getFileName());

                    } catch (IOException e) {
                        // 处理在特定子目录创建/写入文件时可能发生的 IO 异常
                        System.err.println("  错误：无法在子目录 '" + subDirectory.getFileName() + "' 中创建/写入文件 " + targetFilePath.getFileName() + " - 原因: " + e.getMessage());
                    }
                });

        } catch (IOException e) {
            // 处理列出基础目录内容时可能发生的 IO 异常
            System.err.println("错误：无法列出基础目录 '" + basePathString + "' 的内容 - 原因: " + e.getMessage());
        }

        System.out.println("处理完成。");
    }

    /**
     * 主方法，用于演示和执行工具类方法
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        // --- 配置区 ---
        // 注意：在Java字符串中，反斜杠 \ 需要转义为 \\
        String targetBaseDirectory = "D:\\14_LearnEnglish\\6MinuteEnglish\\2025";
        String targetFileName = "temp.txt";
        String fileContent = "hello world!";
        // -------------

        // 调用工具方法执行操作
        addTextFileToSubfolders(targetBaseDirectory, targetFileName, fileContent);
    }
}
