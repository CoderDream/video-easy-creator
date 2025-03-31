package com.coderdream.util.download;

import com.coderdream.util.cd.CdTimeUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * TS 文件批量下载合并工具类
 */
@Slf4j
public class TsFileDownloader02 {

    /**
     * 批量下载 TS 文件并合并成一个文件
     *
     * @param baseUrl      TS 文件 URL 的基础路径，例如：https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/643ba70e1397757896078503022/v.f146750_
     * @param startIndex   起始序号
     * @param endIndex     结束序号 (包含)
     * @param outputFile   合并后的输出文件路径
     * @param suffix       ts文件的后缀，如：.ts?&sign=159a7b89ad630c02ca84de2e925e8d74&t=67caec08&us=OpyQOlqRJQ
     * @return 耗时，格式为 HH:mm:ss.SSS
     */
    public static String downloadAndMergeTsFiles(String baseUrl, int startIndex, int endIndex, String outputFile, String suffix) {
        Instant start = Instant.now(); // 记录开始时间
        log.info("开始下载并合并 TS 文件，baseUrl: {}, startIndex: {}, endIndex: {}, outputFile: {}", baseUrl, startIndex, endIndex, outputFile);

        Path outputPath = Paths.get(outputFile);

        try {
            // 确保输出目录存在
            Files.createDirectories(outputPath.getParent());

            // 使用 try-with-resources 确保文件输出流被正确关闭
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                for (int i = startIndex; i <= endIndex; i++) {
                    String tsFileUrl = baseUrl + i + suffix; // 拼接完整的 TS 文件 URL
                    log.info("开始下载文件: {}", tsFileUrl);

                    try {
                        // 下载单个 TS 文件
                        byte[] tsFileData = downloadTsFile(tsFileUrl);
                        if (tsFileData != null) {
                            fos.write(tsFileData); // 将 TS 文件数据写入合并后的文件
                            log.info("文件 {} 下载并合并成功", tsFileUrl);
                        } else {
                            log.warn("文件 {} 下载失败，已跳过", tsFileUrl);
                        }
                    } catch (IOException e) {
                        log.error("下载或合并文件 {} 失败: {}", tsFileUrl, e.getMessage(), e);
                        // 可以选择抛出异常，或者继续尝试下载其他文件
                    }
                }
            } catch (IOException e) {
                log.error("创建或写入输出文件 {} 失败: {}", outputFile, e.getMessage(), e);
                return "00:00:00.000"; // 或者抛出异常
            }

            log.info("TS 文件合并完成，输出文件：{}", outputFile);
        } catch (IOException e) {
            log.error("创建输出目录失败: {}", e.getMessage(), e);
            return "00:00:00.000"; // 或者抛出异常
        }

        Instant finish = Instant.now(); // 记录结束时间
        long timeElapsed = Duration.between(start, finish).toMillis(); // 计算耗时

        String elapsedTimeStr = CdTimeUtil.formatDuration(timeElapsed);

        log.info("下载合并耗时：{}", elapsedTimeStr);
        return elapsedTimeStr;
    }


    /**
     * 下载单个 TS 文件
     *
     * @param tsFileUrl TS 文件 URL
     * @return TS 文件数据，如果下载失败则返回 null
     * @throws IOException 下载过程中发生异常
     */
    private static byte[] downloadTsFile(String tsFileUrl) throws IOException {
        try {
            URL url = new URL(tsFileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000); // 设置连接超时时间为 5 秒
            connection.setReadTimeout(10000); // 设置读取超时时间为 10 秒

            // 使用 try-with-resources 确保输入流被正确关闭
            try (InputStream inputStream = connection.getInputStream()) {
                return IOUtils.toByteArray(inputStream); // 使用 Apache Commons IO 方便地读取输入流到 byte 数组
            }
        } catch (IOException e) {
            log.error("下载文件 {} 失败: {}", tsFileUrl, e.getMessage());
            throw e; // 继续抛出异常，让上层处理
        }
    }


    public static void main(String[] args) {
        String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/643ba70e1397757896078503022/v.f146750_";
        int startIndex = 0;
        int endIndex = 2;
        String outputFile = "D:\\Download\\小鹅通视频下载器\\"+ "output_merged.ts";
        String suffix = ".ts?&sign=159a7b89ad630c02ca84de2e925e8d74&t=67caec08&us=OpyQOlqRJQ";

        String elapsedTime = downloadAndMergeTsFiles(baseUrl, startIndex, endIndex, outputFile, suffix);
        System.out.println("耗时: " + elapsedTime);
    }
}
