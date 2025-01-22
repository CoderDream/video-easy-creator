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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本文件分割工具类
 */
@Slf4j
public class TextFileSplitter {

    /**
     *  将文本文件按章节分割成多个文本文件
     * @param inputFilePath 输入文件的绝对路径
     * @return List<String>  返回所有分割文件的绝对路径
     */
    public static List<String> splitFileByChapter(String inputFilePath) {
        Instant start = Instant.now();
        List<String> outputFiles = new ArrayList<>();
        File inputFile = new File(inputFilePath);

        if (!inputFile.exists() || !inputFile.isFile()) {
            log.error("输入文件不存在或不是文件: {}", inputFilePath);
            return outputFiles;
        }

        String chapterRegex = "^Chapter\\s+\\d+.*$";  // 匹配 "Chapter 数字 ..." 格式的行
        String partRegex = "^Part\\s+.*$";
        Pattern chapterPattern = Pattern.compile(chapterRegex);
        Pattern partPattern = Pattern.compile(partRegex);

        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line;
        int chapterCount = 0;
         String currentChapterDirName = null;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            Path parentDir = Paths.get(inputFile.getParent());


            while ((line = reader.readLine()) != null) {
                Matcher chapterMatcher = chapterPattern.matcher(line);
                Matcher partMatcher = partPattern.matcher(line);

                if (chapterMatcher.find()) {
                    if (writer != null) {
                        try {
                            writer.close();
                            log.debug("当前章节文件写入完成，文件名为:{}", currentChapterDirName);
                        } catch (IOException e) {
                            log.error("关闭当前章节输出文件异常", e);
                        }
                    }

                    chapterCount++;
                    currentChapterDirName = String.format("Chapter%03d", chapterCount);  // 创建形如 Chapter001 的文件夹名
                    Path chapterDir = parentDir.resolve(currentChapterDirName);
                    if (!Files.exists(chapterDir)) {
                        Files.createDirectory(chapterDir);
                        log.debug("创建输出文件夹,文件夹名称为：{}", chapterDir.toString());
                    }
                     String outputFileName = String.format("Chapter%03d.txt", chapterCount); // 输出文件名字如 Chapter001.txt
                    File outputFile = new File(chapterDir.toString(), outputFileName);
                    outputFiles.add(outputFile.getAbsolutePath());
                    writer = new BufferedWriter(new FileWriter(outputFile));
                     log.debug("创建新章节输出文件,文件名为:{}", outputFile.getAbsolutePath());
                    writer.write(line);
                    writer.newLine();
                } else if (!partMatcher.find() && writer != null) {
                    writer.write(line);
                    writer.newLine();
                }else{
                    log.debug("忽略Part开头的行, 忽略的行内容为:{}",line);
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                   log.debug("最后一个章节文件写入完成, 文件名为:{}",currentChapterDirName);
                } catch (IOException e) {
                     log.error("关闭最后一个章节输出文件异常",e);
                }
            }
        } catch (IOException e) {
            log.error("读取文件或写入文件异常", e);
        } finally {
            if (reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    log.error("关闭BufferedReader异常",e);
                }
            }
        }

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start,finish).toMillis();
        String duration = formatDuration(timeElapsed);
        log.info("方法splitFileByChapter调用完成，耗时:{}",duration);
        return outputFiles;
    }

    /**
     * 将毫秒时长格式化为时分秒毫秒的形式
     * @param milliseconds 毫秒时长
     * @return String 格式化后的时分秒毫秒
     */
    private static String formatDuration(long milliseconds) {
        long hours = milliseconds / (60 * 60 * 1000);
        milliseconds %= (60 * 60 * 1000);
        long minutes = milliseconds / (60 * 1000);
        milliseconds %= (60 * 1000);
        long seconds = milliseconds / 1000;
        milliseconds %= 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    public static void main(String[] args) {
        String inputFilePath = "D:\\0000\\EnBook002\\500V2.txt";
        List<String> outputFiles = TextFileSplitter.splitFileByChapter(inputFilePath);
         if (outputFiles.size()>0){
           log.info("分割成功，共创建了 {} 个文件：", outputFiles.size());
           for (String outputFile : outputFiles) {
              log.info("输出文件路径：{}", outputFile);
           }
        }
    }
}
