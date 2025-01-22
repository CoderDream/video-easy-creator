package com.coderdream.util.txt;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本文件分割工具类
 */
@Slf4j
public class TextSplitter {

    /**
     * 将文本文件按章节分割成多个小文件，并忽略第一个文件
     *
     * @param filePath 原始文本文件路径
     * @return 返回一个字符串，表示分割耗时
     */
    public static String splitTextByChapter(String filePath) {
        Instant startTime = Instant.now(); // 记录开始时间
        log.info("开始分割文件：{}", filePath);
        Path sourcePath = Paths.get(filePath);

        // 从文件路径中提取基础目录，用于创建输出目录
        Path outputBaseDir = sourcePath.getParent();

        if (!Files.exists(outputBaseDir)) {
            try {
                Files.createDirectories(outputBaseDir);
            } catch (IOException e) {
                log.error("创建输出目录失败：{}", outputBaseDir, e);
                return "创建输出目录失败";
            }
        }


        List<String> lines = new ArrayList<>();
        boolean firstChapterFound = false; // 标记是否找到第一个章节
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int chapterCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Chapter")) {
                     // 忽略第一个 "Chapter" 前的内容
                    if (firstChapterFound){
                        chapterCount++;
                        writeChapterToFile(outputBaseDir,chapterCount,lines);
                    }else{
                       firstChapterFound= true;
                    }
                    lines.clear();
                    lines.add(line);
                }else if (firstChapterFound){
                   lines.add(line);
                }

            }
             // 处理最后一个章节的数据
            if (!lines.isEmpty() && firstChapterFound){
                chapterCount++;
                writeChapterToFile(outputBaseDir,chapterCount,lines);
            }
            log.info("文件分割完成，总共分割 {} 个章节", chapterCount);
        } catch (IOException e) {
            log.error("读取文件或写入文件失败", e);
            return "读取文件或写入文件失败";
        }


        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long millis = duration.toMillis() % 1000;
         String time = String.format("%02d时:%02d分:%02d秒:%03d毫秒", hours, minutes % 60, seconds % 60,millis);
        log.info("文件分割耗时：{}", time); // 记录耗时
        return time;
    }

    /**
     * 将一个章节的内容写入到文件中
     * @param outputBaseDir 输出的根路径
     * @param chapterCount 当前的章节序号
     * @param lines 当前章节的内容
     * @throws IOException 文件读写异常
     */
    private static void writeChapterToFile(Path outputBaseDir,int chapterCount, List<String> lines) throws IOException {

        String chapterDirName = "ch" + String.format("%03d",chapterCount) ;
        Path outputChapterDir = Paths.get(outputBaseDir.toString(), chapterDirName);
        if (!Files.exists(outputChapterDir)) {
            Files.createDirectories(outputChapterDir);
        }
         String outputFileName = chapterDirName + "_temp.txt";
         Path outputPath = Paths.get(outputChapterDir.toString(), outputFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            log.info("写入文件: {} 完成",outputPath.toString());
        } catch (IOException e) {
            log.error("写入章节文件失败：{}", outputPath, e);
            throw e; // 抛出异常，让外部方法处理
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\0000\\EnBook001\\900\\商务职场英语口语900句.txt";
        String time = TextSplitter.splitTextByChapter(filePath);
        System.out.println("耗时:"+ time);
    }
}
