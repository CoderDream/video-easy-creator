package com.coderdream.util.txt;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本文件处理工具类，用于替换指定格式的数字标记和合并行
 */
@Slf4j
public class TextFileProcessor2 {

    /**
     * 处理文本文件，替换数字标记并合并行
     *
     * @param tempFilePath 临时文件路径
     * @param outputFilePath 输出文件路径
     * @return 返回一个字符串，表示处理耗时
     */
    public static String processTextFile(String tempFilePath, String outputFilePath) {
        Instant startTime = Instant.now();
        log.info("开始处理文件：{}，输出到：{}", tempFilePath, outputFilePath);

        Path tempPath = Paths.get(tempFilePath);
        Path outputPath = Paths.get(outputFilePath);


        // 用于匹配 "【^数字^】" 的正则表达式
        Pattern numberPattern = Pattern.compile("【\\^\\d+\\^】");

        List<String> processedLines = new ArrayList<>();

         try (BufferedReader reader = new BufferedReader(new FileReader(tempPath.toFile()))) {
             String line;
            while ((line = reader.readLine()) != null) {
                // 1. 替换数字标记
                Matcher matcher = numberPattern.matcher(line);
                String replacedLine = matcher.replaceAll("");
                processedLines.add(replacedLine);
            }

             //2. 合并行
            List<String> mergedLines = mergeLines(processedLines);
             //3. 输出到目标文件
             try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
                 for (String mergedLine : mergedLines) {
                     writer.write(mergedLine);
                     writer.newLine();
                 }
                 log.info("文件处理完成，输出到：{}", outputPath);
             }catch (IOException e){
                 log.error("写入文件失败：{}", outputFilePath, e);
                 return "写入文件失败";
             }
        }catch (IOException e){
            log.error("读取文件失败：{}", tempFilePath, e);
            return "读取文件失败";
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long millis = duration.toMillis() % 1000;
        String time = String.format("%02d时:%02d分:%02d秒:%03d毫秒", hours, minutes % 60, seconds % 60,millis);
        log.info("文件处理耗时：{}", time); // 记录耗时
        return time;
    }


    /**
     * 合并文本行，处理以冒号开头且末尾非标点符号的行
     *
     * @param lines 文本行列表
     * @return 合并后的文本行列表
     */
    private static List<String> mergeLines(List<String> lines) {
        List<String> mergedLines = new ArrayList<>();
        Pattern colonPattern = Pattern.compile("^[:：].*"); // 匹配以冒号开头的行
        Pattern endPunctuationPattern = Pattern.compile(".*[。？！.?!]$"); // 匹配以中英文句号、问号、感叹号结尾的行

        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            Matcher colonMatcher = colonPattern.matcher(currentLine);
            if (colonMatcher.matches()) {
                Matcher endMatcher = endPunctuationPattern.matcher(currentLine);
                if (!endMatcher.matches()) {
                    //需要合并
                    StringBuilder mergedLine = new StringBuilder(currentLine);
                    int nextIndex = i+1;
                    if (nextIndex < lines.size()){
                       mergedLine.append(" ").append(lines.get(nextIndex));
                       nextIndex++;
                       if(nextIndex < lines.size()){
                           mergedLine.append(" ").append(lines.get(nextIndex));
                       }
                        mergedLines.add(mergedLine.toString());
                        i = nextIndex;

                    }else{
                        mergedLines.add(currentLine);
                        i++;
                    }

                     // 合并行后，跳过已合并的行
                }else{
                   mergedLines.add(currentLine);
                }
            }else{
                mergedLines.add(currentLine);
            }
        }

        return mergedLines;
    }

     public static void main(String[] args) {
        String tempFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_temp.txt";
        String outputFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
        String time = TextFileProcessor2.processTextFile(tempFilePath,outputFilePath);
        System.out.println("耗时:"+time);
    }

}
