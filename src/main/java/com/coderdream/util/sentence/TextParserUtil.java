package com.coderdream.util.sentence;

import cn.hutool.core.io.FileUtil;
import com.coderdream.vo.SentenceVO;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextParserUtil {

  /**
   * 将包含英文、音标和中文的字符串解析为 SentenceVO 对象列表。
   *
   * @param jsonFileName 待解析的文件名称
   * @return SentenceVO 对象列表
   */
  public static List<SentenceVO> parseTextToSentenceVOsReplace(
    String jsonFileName) {

    //  创建一个空的 SentenceVO 列表来存储结果
    List<SentenceVO> result = new ArrayList<>();

    // 读取文件内容，按行分割为字符串列表
    List<String> inputList = FileUtil.readLines(jsonFileName,
      StandardCharsets.UTF_8);
    // 如果第一行含有/，则需处理
    if (inputList.get(0).contains("/")) {
      // 过滤掉空行和不符合格式的行
      inputList = inputList.stream()
        .filter(
          line -> line != null && !line.trim().isEmpty() && line.contains("/"))
        .toList();

      for (String line : inputList) {
        if (line == null || line.trim().isEmpty()) {
          continue; // 跳过空行或null
        }

        String[] parts = line.split("/", -1); // 使用 -1 参数，保留末尾的空字符串
        if (parts.length == 3) {
          //去除左右的空格，并确保三个字符串都存在值
          String english = parts[0].trim();
          if (english.isEmpty()) {
            log.warn("数据不规范: {}", line);
            return result; // 其中一个字符串为空，直接返回结果
          }
          String phonetics = "/" + parts[1].trim() + "/";  // 保留中间的斜杠，以便后续处理
          String chinese = parts[2].trim();
          // 移除空格并转换成繁体
          chinese = chinese.replaceAll(" ", "");
          chinese = ZhConverterUtil.toTraditional(chinese);

          result.add(new SentenceVO(english, phonetics, chinese));
        }
      }

    } else {
      // 移除list中的空行
      inputList = inputList.stream().filter(s -> !s.trim().isEmpty()).toList();
      if (inputList.size() % 3 != 0) {
        log.error("数据不规范，不是3的倍数: {}", inputList.size());
      }

      for (int i = 0; i < inputList.size(); i++) {
        //去除左右的空格，并确保三个字符串都存在值
        String english = inputList.get(i).trim();
        if (english.isEmpty()) {
          log.warn("Skipping line with empty English part: {}", i);
          return null; // 其中一个字符串为空，直接返回结果
        }
        String phonetics = inputList.get(i + 1).trim();
        String chinese = inputList.get(i + 2).trim();
        // 移除空格并转换成繁体
        chinese = chinese.replaceAll(" ", "");
        chinese = ZhConverterUtil.toTraditional(chinese);

        result.add(new SentenceVO(english, phonetics, chinese));
        i += 2; // 跳过下一个英文和音标行
      }

    }

    if (inputList.isEmpty()) {
      return result; // 处理空列表
    }

    return result;
  }


  /**
   * 从文件中读取文本内容并解析为 SentenceVO 对象列表。
   *
   * @param filePath 文件路径
   * @return SentenceVO 对象列表
   */
  public static List<SentenceVO> parseFileToSentenceVOsThreeLine(
    String filePath) {
    StringBuilder contentBuilder = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        contentBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      System.err.println("读取文件失败: " + e.getMessage());
      return new ArrayList<>(); // 或者抛出异常
    }
    return TextParserUtil.parseTextToSentenceVOsReplace(
      contentBuilder.toString());
  }

  /**
   * 从文件中读取文本内容并解析为 SentenceVO 对象列表。
   *
   * @param filePath 文件路径
   * @return SentenceVO 对象列表
   */
  public static List<SentenceVO> parseFileToSentenceVOsTwoLine(
    String filePath) {
    StringBuilder contentBuilder = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        contentBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      System.err.println("读取文件失败: " + e.getMessage());
      return new ArrayList<>(); // 或者抛出异常
    }
    return TextParserUtil.parseTextToSentenceVOsReplace(
      contentBuilder.toString());
  }

//  public static void main(String[] args) {
//    String filePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics_raw.txt";
//    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOsThreeLine(filePath);
//
//    if (sentenceVOs != null && !sentenceVOs.isEmpty()) {
//      for (SentenceVO sentenceVO : sentenceVOs) {
//        System.out.println("English: " + sentenceVO.getEnglish());
//        System.out.println("Phonetics: " + sentenceVO.getPhonetics());
//        System.out.println("Chinese: " + sentenceVO.getChinese());
//        System.out.println("-------------------");
//      }
//      System.out.println("Total sentences parsed: " + sentenceVOs.size());
//    } else {
//      System.out.println("No sentences were parsed.");
//    }
//  }
}
