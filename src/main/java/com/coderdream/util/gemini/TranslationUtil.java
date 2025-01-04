package com.coderdream.util.gemini;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.CdConstants;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.callapi.HttpUtil;
import com.coderdream.util.txt.ParagraphUtil;
import com.coderdream.util.txt.TextSingleUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TranslationUtil {

  public static String translate(String text) {
    return GeminiApiClient.generateContent(text);
  }



  /**
   * 处理词汇信息，将其翻译后写入文件。
   *
   * @param vocInfoList 包含词汇信息的列表
   * @param fileName    文件名，用于存储翻译结果
   */
  public static void processVoc(List<VocInfo> vocInfoList, String fileName) {
    StringBuilder text = new StringBuilder(
      CdConstants.VOC_CN_PREFIX);  // 使用 StringBuilder 拼接字符串，避免多次创建字符串对象

    // 遍历 vocInfoList，拼接文本
    for (VocInfo vocInfo : vocInfoList) {
      text.append(vocInfo.getWord()).append("\n ");
      text.append(vocInfo.getWordExplainEn()).append("\n");
    }

    // 调用翻译方法并记录日志
    log.info("开始翻译文本内容，包含 {} 个词汇", vocInfoList.size());

    String result = GeminiApiClient.generateContent(text.toString());  // 翻译文本

    // 记录翻译后的结果日志
    log.info("翻译完成，开始写入文件: {}", fileName);

    // 将翻译结果写入文件
    boolean writeSuccess = writeToFile(fileName, result);

    if (writeSuccess) {
      log.info("文件写入成功: {}", fileName);
    } else {
      log.error("文件写入失败: {}", fileName);
    }
  }

  /**
   * 将翻译结果写入文件
   */
  public static boolean writeToFile(String newFileName, String result) {
    // 使用正则表达式直接处理换行符
    String temp = result.replaceAll("\n{2,}", "\n");  // 将连续的两个或更多的换行符替换为一个换行符
    temp = temp.replace("\n", "__");  // 替换单个换行符为 "__"

    // 使用 Arrays.asList 来避免不必要的 List.of
    List<String> list = Arrays.asList(temp.split("__"));

    // 写入文件
    boolean writeToFileResult = CdFileUtil.writeToFile(newFileName, list);
    log.info("写入文件结果: {}", writeToFileResult);
    return writeToFileResult;
  }

  // 增加英文音标 phonetics
  public static void genPhonetics(String fileName) {

    String text = "解析下面的文本，整个英文句子的音标作为一个整体，插入到英文句子和中文句子之间，从而使原本的两句话变成三句话。这样，原本的12句话会变成18句话。文本如下：";
    List<String> vocInfoList = CdFileUtil.readFileContent(fileName);
    String content = String.join("\n", vocInfoList);
    text += content;
    String translate = GeminiApiClient.generateContent(text);
    log.info("translate: {}", translate);
    File file = new File(fileName);
    String filePath = file.getParent();
    Path outputFilePath = Paths.get(filePath,
      "dialog_single_with_phonetics.txt");
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      for (String line : translate.split("\n")) {
        if (StrUtil.isNotEmpty(line)) {
          // 以斜线\ 分割字符串，然后逐个写入文件 斜线 \\\\ 反斜线  /
          String[] split = line.split("/");
          if (CollectionUtil.isNotEmpty(Arrays.asList(split))
            && split.length == 3) {
            writer.write(split[0]);
            writer.newLine();
            writer.write("/" + split[1] + "/");
            writer.newLine();
            writer.write(split[2]);
            writer.newLine();
          }
        }
      }
      log.info("对话信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }
  }

  /**
   * 生成文章描述
   *
   * @param fileName 文件名
   */
  public static void genDescription(String fileName) {

    String text = "解析下面的文本，帮我写文章，用来发快手、小红书和公众号，要根据不同的平台特性生成不同风格的文章，快手的文章字数在500~600之间，小红书不超过800字，公众号不超过200字；另外，帮我每个平台取3个疑问句的标题，标题中间不要有任何标点符号、表情符号且不超过20个字，快手加入一些表情符号。文本如下：";
    List<String> vocInfoList = CdFileUtil.readFileContent(fileName);
    String content = String.join("\n", vocInfoList);
    text += content;
    String translate = GeminiApiClient.generateContent(text);
    log.info("translate: {}", translate);
    File file = new File(fileName);
    String filePath = file.getParent();
    Path outputFilePath = Paths.get(filePath,
      "description.md");
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
      StandardCharsets.UTF_8)) {
      for (String line : translate.split("\n")) {
        if (StrUtil.isNotEmpty(line)) {
          // 以斜线\ 分割字符串，然后逐个写入文件 斜线 \\\\ 反斜线  /
//          String[] split = line.split("/");
//          if (CollectionUtil.isNotEmpty(Arrays.asList(split))
//            && split.length == 3) {
//            writer.write(split[0]);
//            writer.newLine();
//            writer.write("/" + split[1] + "/");
//            writer.newLine();
          writer.write(line);
          writer.newLine();
//          }
        }
      }
      log.info("对话信息已成功写入到文件: {}", outputFilePath);
    } catch (IOException e) {
      log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
    }

  }

}
