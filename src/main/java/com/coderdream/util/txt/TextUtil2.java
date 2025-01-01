package com.coderdream.util.txt;

import com.coderdream.entity.DialogDualEntity;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文本处理工具类
 */
@Slf4j
public class TextUtil2 {

    /**
     * 文本文件的路径
     */
    private static final String FILE_PATH = "D:\\0000\\EnBook001\\商务职场英语口语900句";

    /**
     * 文本文件的文件名
     */
    private static final String FILE_NAME = "商务职场英语口语900句V1_ch02_v1.txt";


    /**
     *  私有构造方法，防止实例化
     */
    private TextUtil2() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /**
     *  查询所有段落的第一个中文/英文冒号前的名字，并去重后存储到相同文件夹的host.txt文档中
     */
    public static void extractHosts() {
        Path inputFilePath = Paths.get(FILE_PATH, FILE_NAME);
        Path outputFilePath = Paths.get(FILE_PATH, "host.txt");

        Set<String> hosts = new LinkedHashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            String line;
            StringBuilder paragraph = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    String paragraphStr = paragraph.toString();
                    String host = extractHostFromParagraph(paragraphStr);
                    if(host != null){
                        hosts.add(host);
                    }
                    paragraph.setLength(0); // 清空段落
                    continue; // 跳过空行
                }
                paragraph.append(line).append("\n");
            }
             // 处理最后一段
            String paragraphStr = paragraph.toString();
            String host = extractHostFromParagraph(paragraphStr);
            if(host != null){
               hosts.add(host);
            }

            log.info("从文件 {} 中成功读取并提取host信息,共{}条", inputFilePath, hosts.size());
        } catch (IOException e) {
            log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
            return; // 发生异常直接返回，不再继续写入文件
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
            for (String host : hosts) {
                writer.write(host);
                writer.newLine();
            }
            log.info("host信息已成功写入到文件: {}", outputFilePath);
        } catch (IOException e) {
            log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
        }
    }

    /**
     *  从一段文本中提取host，根据第一个冒号分割,只取英文部分
     * @param paragraph
     * @return
     */
    private static String extractHostFromParagraph(String paragraph) {
       String[] lines = paragraph.trim().split("\n");
       if(lines.length>0){
         return extractHostFromLine(lines[0]);
       }
       return null;
    }


    /**
     *  从一行文本中提取host，根据第一个冒号分割
     * @param line
     * @return
     */
    private static String extractHostFromLine(String line) {
        int colonIndex =  findFirstColonIndex(line);
        if(colonIndex != -1){
            return line.substring(0,colonIndex).trim();
        }
        return null;
    }

    /**
     * 解析文本文件中的对话内容，并返回 DialogDualEntity 列表
     *
     * @return 包含对话信息的 DialogDualEntity 列表
     */
    public static List<DialogDualEntity> parseDialogs() {
        Path inputFilePath = Paths.get(FILE_PATH, FILE_NAME);
        List<DialogDualEntity> dialogs = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(inputFilePath, StandardCharsets.UTF_8)) {
            String line;
            StringBuilder paragraph = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    //处理段落
                    DialogDualEntity dialog = parseParagraph(paragraph.toString());
                    if (dialog != null) {
                       dialogs.add(dialog);
                    }
                    paragraph.setLength(0);// 清空段落
                    continue; // 跳过空行
                }
                 paragraph.append(line).append("\n");
            }
            //处理最后一段
            DialogDualEntity dialog = parseParagraph(paragraph.toString());
             if (dialog != null) {
               dialogs.add(dialog);
             }
            log.info("从文件 {} 中成功读取并解析对话信息,共{}条", inputFilePath, dialogs.size());
        } catch (IOException e) {
            log.error("读取文件 {} 发生异常：{}", inputFilePath, e.getMessage(), e);
        }
        return dialogs;
    }


    /**
     *  将对话列表写入文件
     * @param dialogs 对话列表
     */
    public static void writeDialogsToFile(List<DialogDualEntity> dialogs) {
        Path outputFilePath = Paths.get(FILE_PATH, "dialog.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
            for (DialogDualEntity dialog : dialogs) {
                writer.write(dialog.getContentAEn() != null ? dialog.getContentAEn() : "");
                writer.newLine();
                writer.write(dialog.getContentACn() != null ? dialog.getContentACn() : "");
                writer.newLine();
                writer.write(dialog.getContentBEn() != null ? dialog.getContentBEn() : "");
                writer.newLine();
                writer.write(dialog.getContentBCn() != null ? dialog.getContentBCn() : "");
                writer.newLine();
            }
            log.info("对话信息已成功写入到文件: {}", outputFilePath);
        } catch (IOException e) {
            log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
        }
    }

    /**
     *  查找字符串中第一个中文或英文冒号的索引位置
     * @param line
     * @return
     */
    private static int findFirstColonIndex(String line) {
        int chineseColonIndex = line.indexOf("：");
        int englishColonIndex = line.indexOf(":");
        if(chineseColonIndex == -1){
            return englishColonIndex;
        }
        if(englishColonIndex == -1){
            return chineseColonIndex;
        }
        return Math.min(chineseColonIndex,englishColonIndex);
    }

    /**
     * 解析段落内容为 DialogDualEntity 对象
     *
     * @param paragraph 段落文本
     * @return 解析后的 DialogDualEntity 对象，如果解析失败则返回 null
     */
     private static DialogDualEntity parseParagraph(String paragraph) {
         DialogDualEntity dialog = new DialogDualEntity();
         String[] lines = paragraph.trim().split("\n");
          if(lines.length<2){
             log.warn("段落信息行数小于2，无法解析,段落内容：{}", paragraph);
            return null;
         }
          int lineIndex=0;
       try {
            // 解析A的内容
            int colonIndexA = findFirstColonIndex(lines[lineIndex]);
            if(colonIndexA !=-1){
               dialog.setHostAEn(lines[lineIndex].substring(0,colonIndexA).trim());
               dialog.setContentAEn(lines[lineIndex].substring(colonIndexA+1).trim());
               lineIndex++;
            }

           if(lineIndex < lines.length){
               //解析B的内容
               int colonIndexB = findFirstColonIndex(lines[lineIndex]);
              if(colonIndexB!=-1){
                  dialog.setHostBEn(lines[lineIndex].substring(0,colonIndexB).trim());
                  dialog.setContentBEn(lines[lineIndex].substring(colonIndexB+1).trim());
                  lineIndex++;
                }
            }


            //解析中文翻译部分
             if (lineIndex < lines.length) {
              StringBuilder contentACnBuilder = new StringBuilder();
              StringBuilder contentBCnBuilder = new StringBuilder();
               // 解析A的中文翻译
               while(lineIndex<lines.length){
                   int colonIndexCn = findFirstColonIndex(lines[lineIndex]);
                   if(colonIndexCn!=-1){
                     String hostCn = lines[lineIndex].substring(0,colonIndexCn).trim();
                     String contentCn = lines[lineIndex].substring(colonIndexCn+1).trim();
                     if (dialog.getHostACn() == null) {
                        dialog.setHostACn(hostCn);
                       contentACnBuilder.append(contentCn);
                     }else if(dialog.getHostBCn() == null) {
                        dialog.setHostBCn(hostCn);
                        contentBCnBuilder.append(contentCn);
                     }
                   }else {
                      contentACnBuilder.append("\n");
                      contentACnBuilder.append(lines[lineIndex].trim());
                    }
                   lineIndex++;
               }


                if(contentBCnBuilder.length() >0){
                   dialog.setContentBCn(contentBCnBuilder.toString());
                }
                dialog.setContentACn(contentACnBuilder.toString());
            }
           log.debug("成功解析段落信息：{}", dialog);
            return dialog;
        } catch (Exception e) {
            log.error("解析段落信息出错: {}， 段落内容：{}", e.getMessage(),paragraph,e);
           return null;
       }
    }
}
