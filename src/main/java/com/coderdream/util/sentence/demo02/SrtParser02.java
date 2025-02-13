package com.coderdream.util.sentence.demo02;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于解析 SRT 文件并提取句子列表的工具类。
 */
public class SrtParser02 {

    private static final Logger log = LoggerFactory.getLogger(SrtParser02.class);

    /**
     * 从给定的 SRT 文件路径中读取内容，并将其解析为句子列表。
     *
     * @param filePath SRT 文件的路径
     * @return 解析后的句子列表
     * @throws IOException 如果读取文件时发生错误
     */
    public static List<String> parseSrtFile(String filePath) throws IOException {
        Instant start = Instant.now();
        List<String> sentences = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            StringBuilder currentSpeaker = new StringBuilder();
            StringBuilder currentSentence = new StringBuilder();
            String line;
            boolean speakerDetected = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 忽略空行和数字行（时间戳）
                if (line.isEmpty() || line.matches("\\d+")) {
                    continue;
                }
                // 忽略时间线  00:00:19,189 --> 00:00:24,699
                if(line.matches("\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}")){
                    continue;
                }

                // 检查是否是新的发言者（例如 "Ms. Leavitt:"）
                Matcher speakerMatcher = Pattern.compile("^[A-Za-z]+\\. [A-Za-z]+:$").matcher(line);
                if (speakerMatcher.matches()) {
                    // 如果之前已经有句子，先添加到列表
                    if (currentSentence.length() > 0) {
                         //如果有上一句，先结算上一句
                        sentences.add(currentSentence.toString().trim());
                        currentSentence = new StringBuilder();
                    }
                     //如果上一句话是主持人, 合并到当前行
                     if(speakerDetected){
                         currentSentence.append(line).append(" ");
                         speakerDetected = false; //重置
                     }else {
                         currentSpeaker.setLength(0); // 清空之前的发言者
                         currentSpeaker.append(line).append(" ");
                         speakerDetected = true;
                     }
                    continue;
                }


                // 如果检测到发言者，将当前行添加到 currentSentence，并在行尾加空格
                if (speakerDetected) {
                    currentSentence.append(currentSpeaker.toString()).append(line).append(" ");
                    speakerDetected = false; // 处理完第一句后，重置标志
                } else {
                    //常规句子
                    currentSentence.append(line).append(" ");
                }
                //先把句子按句号、问号拆分，解决长句
                List<String> splitSentences = splitSentence(currentSentence.toString());
                if(splitSentences.size()>1){
                    //如果确实拆分了
                    for(int i=0;i<splitSentences.size()-1;i++){
                        sentences.add(splitSentences.get(i));
                    }
                    currentSentence.setLength(0);// 清空之前的,保留最后一个
                    currentSentence.append(splitSentences.get(splitSentences.size()-1));
                }

            }

            // 添加最后一个句子（如果有）
            if (currentSentence.length() > 0) {
                sentences.add(currentSentence.toString().trim());
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            throw e; // 重新抛出异常，让调用者处理
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("SRT file parsed in: {}h {}m {}s {}ms",
                duration.toHoursPart(), duration.toMinutesPart(),
                duration.toSecondsPart(), duration.toMillisPart());

        return sentences;
    }

     /**
      * 考虑了英文的各种标点符号，将文本分割成句子。
      * @param text
      * @return
      */
     private static List<String> splitSentence(String text) {
         List<String> sentences = new ArrayList<>();
         // 使用正则表达式分割句子，考虑各种标点符号和缩写
         //Pattern pattern = Pattern.compile("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|!)\\s");
         //Pattern pattern = Pattern.compile("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|!|\n)\\s");
         Pattern pattern = Pattern.compile("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?|!|:')\\s+");


         Matcher matcher = pattern.matcher(text);

         int start = 0;
         while (matcher.find()) {
             String sentence = text.substring(start, matcher.end()).trim();
             if (!sentence.isEmpty()) {
                 sentences.add(sentence);
             }
             start = matcher.end();
         }

         // 添加最后一个句子（如果有）
         if (start < text.length()) {
             String lastSentence = text.substring(start).trim();
             if (!lastSentence.isEmpty()) {
                 sentences.add(lastSentence);
             }
         }

         return sentences;
     }


    public static void main(String[] args) throws IOException {
        String filePath = "D:\\0000\\0003_PressBriefings\\250128\\25012802.srt";
        List<String> sentences = SrtParser02.parseSrtFile(filePath);

        // 打印解析后的句子列表
        for (int i = 0; i < sentences.size(); i++) {
            System.out.println("Sentence " + (i + 1) + ": " + sentences.get(i));
        }
    }
}
