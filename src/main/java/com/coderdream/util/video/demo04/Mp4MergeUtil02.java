package com.coderdream.util.video.demo04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mp4MergeUtil02 {

    /**
     * 合并指定文件夹中的所有MP4文件，每10个文件为一组合并，最后剩余的文件继续合并
     * @param inputDir MP4文件夹路径
     * @param outputDir 合并后输出文件夹路径
     */
    public static void mergeMp4Files(Path inputDir, Path outputDir) {
        // 记录方法调用的开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 获取文件夹中所有的MP4文件
            List<Path> mp4Files = Files.walk(inputDir)
                    .filter(file -> file.toString().endsWith(".mp4"))
                    .collect(Collectors.toList());

            log.info("找到{}个MP4文件进行合并", mp4Files.size());

            // 按每10个文件分为一组
            List<List<Path>> fileGroups = partitionFiles(mp4Files, 10);

            // 创建存放list.txt文件的目录
            Path listDir = Paths.get("D:/0000/EnBook002/Chapter015/video_list_file");
            if (!Files.exists(listDir)) {
                Files.createDirectories(listDir);
                log.info("目录创建成功: {}", listDir.toString());
            }

            for (List<Path> group : fileGroups) {
                // 生成合并后文件的名称，使用第一个和最后一个文件的文件名的数字部分
                String prefix = getFileNamePrefix(group);
                Path groupOutputFile = outputDir.resolve("merged_" + prefix + ".mp4");

                // 生成list.txt文件存放路径
                File inputListFile = createInputListFile(group, listDir);

                // 合并文件
                mergeFileGroup(inputListFile, groupOutputFile);
            }

            // 输出合并完成日志
            log.info("所有MP4文件合并完成");

        } catch (IOException e) {
            log.error("文件处理异常", e);
        } finally {
            // 记录方法调用的结束时间并计算耗时
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.info("合并操作耗时：{}毫秒", duration);
        }
    }

    /**
     * 将MP4文件按指定的大小分组
     * @param files 待分组的文件列表
     * @param groupSize 每组的文件数量
     * @return 按照指定大小分组后的文件列表
     */
    private static List<List<Path>> partitionFiles(List<Path> files, int groupSize) {
        List<List<Path>> result = new ArrayList<>();
        for (int i = 0; i < files.size(); i += groupSize) {
            result.add(files.subList(i, Math.min(i + groupSize, files.size())));
        }
        return result;
    }

    /**
     * 使用FFmpeg合并指定的MP4文件组
     * @param inputListFile 输入文件列表的路径
     * @param output 合并后的输出文件路径
     * @throws IOException 如果FFmpeg命令执行失败
     */
    private static void mergeFileGroup(File inputListFile, Path output) throws IOException {
        // 拼接FFmpeg命令
        String command = String.format("ffmpeg -f concat -safe 0 -i %s -c copy %s",
                inputListFile.getAbsolutePath(), output.toString());

        log.info("执行FFmpeg命令：{}", command);

        // 执行FFmpeg命令
        try {
            Process process = new ProcessBuilder(command.split(" "))
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("FFmpeg输出：{}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("FFmpeg命令执行失败，退出码：{}", exitCode);
            } else {
                log.info("MP4文件组合并成功：{}", output);
            }

        } catch (InterruptedException e) {
            log.error("FFmpeg进程被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 创建包含所有需要合并的文件路径的输入文件
     * @param fileGroup 需要合并的MP4文件组
     * @param listDir 存放list.txt的文件夹路径
     * @return 创建的list.txt文件
     * @throws IOException 如果创建文件失败
     */
    private static File createInputListFile(List<Path> fileGroup, Path listDir) throws IOException {
        File inputListFile = new File(listDir.toFile(), "input_list.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputListFile))) {
            for (Path file : fileGroup) {
                writer.write("file '" + file.toString() + "'\n");
            }
        }
        return inputListFile;
    }

    /**
     * 获取文件名的前缀（第一个和最后一个文件的文件名）
     * @param fileGroup 文件组
     * @return 文件名的前缀
     */
    private static String getFileNamePrefix(List<Path> fileGroup) {
        // 获取第一个和最后一个文件的文件名（假设文件名是三位数字）
        String firstFileName = fileGroup.get(0).getFileName().toString();
        String lastFileName = fileGroup.get(fileGroup.size() - 1).getFileName().toString();

        return firstFileName + "_" + lastFileName;
    }

    public static void main(String[] args) {
        // 测试示例
        Path inputDir = Paths.get("D:/0000/EnBook002/Chapter015/video_cht");
        Path outputDir = Paths.get("D:/0000/EnBook002/Chapter015/video_merge");

        // 确保输出目录存在
        File dir = new File(String.valueOf(outputDir));
        if (!dir.exists() && dir.mkdirs()) {
            log.info("目录创建成功: {}", dir.getAbsolutePath());
        }

        mergeMp4Files(inputDir, outputDir);
    }
}
