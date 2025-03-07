package com.coderdream.util.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * TS 文件批量下载合并工具类 (使用 FFmpeg)
 */
@Slf4j
public class TsFileDownloader03 {

    /**
     * 批量下载 TS 文件, 保存到临时目录, 并使用 FFmpeg 合并成一个文件
     *
     * @param baseUrl    TS 文件 URL 的基础路径，例如：https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/643ba70e1397757896078503022/v.f146750_
     * @param startIndex 起始序号
     * @param endIndex   结束序号 (包含)
     * @param tempDir    临时文件保存目录
     * @param outputFile 合并后的输出文件路径
     * @param suffix     ts文件的后缀，如：.ts?&sign=159a7b89ad630c02ca84de2e925e8d74&t=67caec08&us=OpyQOlqRJQ
     * @return 耗时，格式为 HH:mm:ss.SSS
     */
    public static String downloadAndMergeTsFilesWithFFmpeg(String baseUrl, int startIndex, int endIndex, String tempDir, String outputFile, String suffix) {
        Instant start = Instant.now(); // 记录开始时间
        log.info("开始下载 TS 文件并使用 FFmpeg 合并，baseUrl: {}, startIndex: {}, endIndex: {}, tempDir: {}, outputFile: {}", baseUrl, startIndex, endIndex, tempDir, outputFile);

        Path tempDirPath = Paths.get(tempDir);
        Path outputFilePath = Paths.get(outputFile);

        try {
            // 确保临时目录和输出目录存在
            Files.createDirectories(tempDirPath);
            Files.createDirectories(outputFilePath.getParent());

            // 下载 TS 文件到临时目录
            List<Path> downloadedFiles = new ArrayList<>();
            for (int i = startIndex; i <= endIndex; i++) {
                String tsFileUrl = baseUrl + i + suffix; // 拼接完整的 TS 文件 URL
                Path tempFile = tempDirPath.resolve(String.format("temp_%05d.ts", i)); // 临时文件名
                log.info("检查文件是否存在: {}", tempFile);

                if (Files.exists(tempFile)) {
                    log.info("文件 {} 已经存在，跳过下载", tempFile);
                    downloadedFiles.add(tempFile);
                } else {
                    log.info("开始下载文件: {} 到 {}", tsFileUrl, tempFile);
                    try {
                        downloadTsFile(tsFileUrl, tempFile); // 下载 TS 文件到指定路径
                        downloadedFiles.add(tempFile);
                        log.info("文件 {} 下载成功", tsFileUrl);
                    } catch (IOException e) {
                        log.error("下载文件 {} 失败: {}", tsFileUrl, e.getMessage(), e);
                        // 可以选择抛出异常，或者继续尝试下载其他文件
                    }
                }
            }

            // 使用 FFmpeg 合并 TS 文件
            boolean mergeSuccess = mergeTsFilesWithFFmpeg(downloadedFiles, outputFile, outputFilePath.getParent()); // 将 listfile 放到相同的文件夹下
            if (!mergeSuccess) {
                log.error("使用 FFmpeg 合并 TS 文件失败");
                return "00:00:00.000";
            }
            log.info("TS 文件合并完成，输出文件：{}", outputFile);

        } catch (IOException e) {
            log.error("创建目录失败: {}", e.getMessage(), e);
            return "00:00:00.000"; // 或者抛出异常
        } finally {
            // 清理临时文件 (即使合并失败也要清理)
            // deleteTempFiles(tempDirPath);  // 注释掉，不再删除临时文件
            log.info("临时文件保留在: {}", tempDirPath);
        }

        Instant finish = Instant.now(); // 记录结束时间
        long timeElapsed = Duration.between(start, finish).toMillis(); // 计算耗时

        long milliseconds = timeElapsed % 1000;
        long seconds = (timeElapsed / 1000) % 60;
        long minutes = (timeElapsed / (1000 * 60)) % 60;
        long hours = (timeElapsed / (1000 * 60 * 60)) % 24;

        String elapsedTimeStr = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);

        log.info("下载合并耗时：{}", elapsedTimeStr);
        return elapsedTimeStr;
    }

    /**
     * 下载单个 TS 文件到指定路径
     *
     * @param tsFileUrl TS 文件 URL
     * @param tempFile  保存的临时文件路径
     * @throws IOException 下载过程中发生异常
     */
    private static void downloadTsFile(String tsFileUrl, Path tempFile) throws IOException {
        try {
            URL url = new URL(tsFileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000); // 设置连接超时时间为 5 秒
            connection.setReadTimeout(10000); // 设置读取超时时间为 10 秒

            // 使用 try-with-resources 确保输入流和输出流被正确关闭
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
                IOUtils.copy(inputStream, fileOutputStream); // 使用 Apache Commons IO 方便地复制输入流到文件
            }
        } catch (IOException e) {
            log.error("下载文件 {} 失败: {}", tsFileUrl, e.getMessage());
            Files.deleteIfExists(tempFile); // 下载失败删除临时文件
            throw e; // 继续抛出异常，让上层处理
        }
    }

    /**
     * 使用 FFmpeg 合并 TS 文件
     *
     * @param tsFiles     要合并的 TS 文件列表
     * @param outputFile  输出文件路径
     * @param outputDir   输出文件所在的目录，用于存放 listfile
     * @return true if merge successful, false otherwise
     */
    private static boolean mergeTsFilesWithFFmpeg(List<Path> tsFiles, String outputFile, Path outputDir) {
        // 创建 FFmpeg 命令
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y"); // 覆盖输出文件 (如果存在)
        command.add("-f");  // 强制指定输入格式
        command.add("concat"); // 使用 concat demuxer
        command.add("-safe");
        command.add("0"); // 允许访问任何文件
        command.add("-i");
        // Create input concat file
        File concatFile = createConcatFile(tsFiles, outputDir);
        command.add(concatFile.getAbsolutePath());
        command.add("-c");
        command.add("copy"); // 直接复制，不重新编码
        command.add(outputFile);

        log.info("执行 FFmpeg 命令: {}", String.join(" ", command));  // 打印完整的命令

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 将错误流合并到标准输出流

        Process process = null;
        try {
            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("FFmpeg Output: {}", line); // 打印 FFmpeg 的输出信息到日志
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("FFmpeg 合并 TS 文件成功，输出文件：{}", outputFile);
                //concatFile.delete(); // 删除concat file // 这里不能删除，需要保留
                log.info("concat file 保留在：{}", concatFile.getAbsolutePath());

                return true;
            } else {
                log.error("FFmpeg 进程执行失败，退出代码: {}", exitCode);
                // 尝试读取错误流
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        log.error("FFmpeg Error: {}", errorLine); // 打印 FFmpeg 的错误信息
                    }
                }
                //concatFile.delete(); // 删除concat file  // 这里不能删除，需要保留
                log.info("concat file 保留在：{}", concatFile.getAbsolutePath());

                return false;
            }
        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令时发生异常: ", e);
            //concatFile.delete(); // 删除concat file  // 这里不能删除，需要保留
            log.info("concat file 保留在：{}", concatFile.getAbsolutePath());
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 创建FFmpeg的concat文件
     *
     * @param tsFiles   要合并的 TS 文件列表
     * @param outputDir 输出文件所在的目录，用于存放 listfile
     * @return
     */
    private static File createConcatFile(List<Path> tsFiles, Path outputDir) {
        File concatFile = null;
        try {
            concatFile = File.createTempFile("concat", ".txt", outputDir.toFile()); // 创建在输出目录下

            try (FileWriter fw = new FileWriter(concatFile);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                for (Path tsFile : tsFiles) {
                    bw.write("file '" + tsFile.toAbsolutePath().toString() + "'");
                    bw.newLine();
                }

            } catch (IOException e) {
                log.error("创建concat文件失败", e);
            }

        } catch (IOException e) {
            log.error("创建concat文件失败", e);
        }
        return concatFile;
    }

    /**
     * 删除临时文件
     *
     * @param tempDir 临时目录路径
     */
    private static void deleteTempFiles(Path tempDir) {
        if (tempDir == null || !Files.exists(tempDir)) {
            return;
        }

        try {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.toString().length() - a.toString().length()) // 先删除文件，再删除目录
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            log.debug("删除临时文件: {}", path);
                        } catch (IOException e) {
                            log.error("删除临时文件 {} 失败: {}", path, e.getMessage());
                        }
                    });
            log.info("临时文件清理完成");
        } catch (IOException e) {
            log.error("遍历临时目录失败: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/643ba70e1397757896078503022/v.f146750_";
        int startIndex = 0;
        int endIndex = 1358;
        String tempDir = "D:\\Download\\小鹅通视频下载器\\temp"; // 临时文件目录
        String outputFile = "D:\\Download\\小鹅通视频下载器\\output_merged.ts";  // 修改后的路径
        String suffix = ".ts?&sign=159a7b89ad630c02ca84de2e925e8d74&t=67caec08&us=OpyQOlqRJQ";

        String elapsedTime = downloadAndMergeTsFilesWithFFmpeg(baseUrl, startIndex, endIndex, tempDir, outputFile, suffix);
        System.out.println("耗时: " + elapsedTime);
    }
}
