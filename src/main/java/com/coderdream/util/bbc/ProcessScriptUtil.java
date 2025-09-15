package com.coderdream.util.bbc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.gemini.CallGeminiApiUtil;
import com.coderdream.util.grok.GrokApiUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

/**
 * <pre>
 *     处理原始文本，执行一下操作：
 *     1、生成对话脚本文本文件；
 *     2、生成词汇表文本文件；
 *     3、更新主持人Excel文件（host.xlsx）
 *
 * </pre>
 *
 * @author CoderDream
 */
@Slf4j
public class ProcessScriptUtil {

  public static void main(String[] args) {
//    String folderName = "181122";
//    ProcessScriptUtil.process(folderName);

//        replace();
  }
//
//  /**
//   * @param folderName 文件夹
//   */
//  public static void process(String folderName) {
//    String fileName = folderName + "_script";
//
//
//    String scriptDialogFileName = CommonUtil.getFullPathFileName(folderName,
//      "script_dialog", ".txt");
//
//    String vocFileName = CommonUtil.getFullPathFileName(folderName, "voc",
//      ".txt");
//
//
//    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
//      ".txt");
//    List<String> stringList = CdFileUtil.readLines(srcFileName,
//      StandardCharsets.UTF_8);
//
////    Map<Integer, String> stringListWithIndex = new HashMap<>();
//    int transcriptIndex = 0;
//    int vocIndex = 0;
//    for (int i = 0; i < stringList.size(); i++) {
////      stringListWithIndex.put(i, stringList.get(i));
//      if (stringList.get(i).contains("word-for-word transcript")) {
//        transcriptIndex = i;
//      }
//
//      if (stringList.get(i).toLowerCase()
//        .contains("VOCABULARY".toLowerCase())) {
//        vocIndex = i;
//      }
//    }
//    List<String> scriptDialogContentList = new ArrayList<>();
//    List<String> scriptList; // DialogSingleEntity
//    scriptList = stringList.subList(transcriptIndex + 1, vocIndex);
//    List<DialogSingleEntity> dialogSingleEntityList = DialogParser.parseDialogSingleEntity(
//      scriptList);
//    for (DialogSingleEntity entity : dialogSingleEntityList) {
//      scriptDialogContentList.add(entity.getHostEn());
//      scriptDialogContentList.add(entity.getContentEn());
//      scriptDialogContentList.add(" ");
//    }
//    CdFileUtil.writeToFile(scriptDialogFileName, scriptDialogContentList);
//
//    List<String> vocContentList = new ArrayList<>();
//    List<String> vocList = new ArrayList<>(); // VocInfo
//    vocList = stringList.subList(vocIndex + 1, stringList.size());
//    List<VocInfo> vocInfoList = DialogParser.parseDialogVocInfo(vocList); //
//    for (VocInfo vocInfo : vocInfoList) {
//      vocContentList.add(vocInfo.getWord());
//      vocContentList.add(vocInfo.getWordExplainEn());
//      vocContentList.add(" ");
//    }
//    CdFileUtil.writeToFile(vocFileName, vocContentList);
//  }

  /**
   * @param srcFileName 文件夹
   */
  public static File processScriptTxt(String srcFileName) {

    List<String> scriptDialogContentList = new ArrayList<>();
    List<String> stringList = FileUtil.readLines(srcFileName,
      StandardCharsets.UTF_8);
    for (String line : stringList) {
      line = line.replace(" – ", ", ");
      scriptDialogContentList.add(line);
    }

    return FileUtil.writeLines(scriptDialogContentList,
      srcFileName, StandardCharsets.UTF_8);
  }

  /**
   * @param folderName 文件夹
   */
  public static File genScriptDialogTxt(String folderName,
    String scriptDialogFileName) {
    String fileName = folderName + "_script";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = FileUtil.readLines(srcFileName,
      StandardCharsets.UTF_8);

//    Map<Integer, String> stringListWithIndex = new HashMap<>();
    int transcriptIndex = 0;
    int vocIndex = 0;
    for (int i = 0; i < stringList.size(); i++) {
//      stringListWithIndex.put(i, stringList.get(i));
      if (stringList.get(i).contains("word-for-word transcript")) {
        transcriptIndex = i;
      } else {
        log.error("找不到word-for-word transcript");
      }

      if (stringList.get(i).toLowerCase()
        .contains("VOCABULARY".toLowerCase())) {
        vocIndex = i;
        log.error("找到了 VOCABULARY， 在第 {} 行", i);
      } else {
        log.error("找不到VOCABULARY");
      }
    }
    List<String> scriptDialogContentList = new ArrayList<>();
    if(CollectionUtil.isEmpty(stringList) || stringList.size() < 2){
      log.error("stringList 文件内容为空");
    }

    List<String> scriptList = stringList.subList(transcriptIndex + 1, vocIndex);
    List<DialogSingleEntity> dialogSingleEntityList = DialogParser.parseDialogSingleEntity(
      scriptList);
    for (DialogSingleEntity entity : dialogSingleEntityList) {
      scriptDialogContentList.add(entity.getHostEn());
      scriptDialogContentList.add(entity.getContentEn());
      scriptDialogContentList.add(" ");
    }

    return FileUtil.writeLines(scriptDialogContentList,
      scriptDialogFileName, StandardCharsets.UTF_8);
  }

  /**
   * @param folderName 文件夹
   */
  public static File genVocTxt(String folderName, String vocFileName) {
    String fileName = folderName + "_script";

    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = FileUtil.readLines(srcFileName,
      StandardCharsets.UTF_8);

    int vocIndex = 0;
    for (int i = 0; i < stringList.size(); i++) {

      if (stringList.get(i).toLowerCase()
        .contains("VOCABULARY".toLowerCase())) {
        vocIndex = i;
      }
    }

    List<String> vocContentList = new ArrayList<>();
    List<String> vocList = new ArrayList<>(); // VocInfo
    vocList = stringList.subList(vocIndex + 1, stringList.size());
    List<VocInfo> vocInfoList = DialogParser.parseDialogVocInfo(vocList); //
    for (VocInfo vocInfo : vocInfoList) {
      vocContentList.add(vocInfo.getWord());
      vocContentList.add(vocInfo.getWordExplainEn());
      vocContentList.add(" ");
    }

    return FileUtil.writeLines(vocContentList, vocFileName,
      StandardCharsets.UTF_8);
  }

  /**
   * @param vocBeginIndex
   * @param vocEndIndex
   * @param stringList
   * @param vocList
   */
  private static void genVocList(int vocBeginIndex, int vocEndIndex,
    List<String> stringList, List<String> vocList) {
    // 设置词汇表的字符串列表
    int vocIdx = 0;// 上一次词汇的结束标志位
    String phrase;
    for (int i = vocBeginIndex; i < vocEndIndex; i++) {
      // 如果是空字符串，则说明
      String tempStr = stringList.get(i);
      if (StrUtil.isEmpty(tempStr)) {
        // 第一个
        if (vocIdx == 0) {
          phrase = stringList.get(vocBeginIndex);

          String explainEn = "";
          for (int j = vocBeginIndex + 1; j < i; j++) {
            if (StrUtil.isEmpty(explainEn)) {
              explainEn += stringList.get(j);
            } else {
              explainEn += " " + stringList.get(j);
            }
          }

          vocList.add(phrase);
          vocList.add(explainEn);
          vocList.add("");
          vocIdx = i + 1;
        } else {
          // 如果前一行不是空行就继续
          if (!StrUtil.isEmpty(stringList.get(i - 1))) {
            phrase = stringList.get(vocIdx);

            String explainEn = "";
            for (int j = vocIdx + 1; j < i; j++) {
              if (StrUtil.isEmpty(explainEn)) {
                explainEn += stringList.get(j);
              } else {
                explainEn += " " + stringList.get(j);
              }
            }

            vocList.add(phrase);
            vocList.add(explainEn);
            vocList.add("");
            vocIdx = i + 1;
          }
        }

//                System.out.println("VOC tempStr: " + tempStr);
//                if (StrUtil.isNotEmpty(tempStr) || StrUtil.isNotEmpty(prevStr)) {
//                    vocList.add(tempStr);
//                }
      }
    }
  }

  public static String SUBTITLE_HEAD = "翻译下面的英文字幕，内容有的是一句话，有的是半句话，但是你直接翻译就行，返回格式为一句英文一句中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";
  public static String DIALOG_HEAD = "翻译下面的英文字幕，一行一行翻译，每行内容有的是一句话，有的是几句话，不要拆开，不要分行，你直接翻译就行，返回格式为一行英文一行中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";


  public static void translateByGemini(String type, String fileName,
    String geminiFileName, Integer groupSize) {
    String head = "翻译下面的英文字幕，内容有的是一句话，有的是半句话，但是你直接翻译就行，返回格式为一句英文一句中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";
    switch (type) {
      case "subtitle":
        head = SUBTITLE_HEAD;
        break;
      case "dialog":
        head = DIALOG_HEAD;
        break;
      default:
        head = SUBTITLE_HEAD;
        log.info("不支持的类型：{}", type);
    }

    List<String> stringList = FileUtil.readLines(fileName,
      StandardCharsets.UTF_8);
    List<String> totalStringList = new ArrayList<>();
    List<List<String>> stringLists = ListUtils.partition(stringList, groupSize);

    int i = 0;
    for (List<String> subStringList : stringLists) {
      i++;
      String index = java.lang.String.format("%03d", i);

      String geminiFileNameTemp = CdFileUtil.addPostfixToFileName(
        geminiFileName, "_" + index);
      if (CdFileUtil.isFileEmpty(geminiFileNameTemp)) {
        String str = java.lang.String.join("\n", subStringList);
        String prompt = head + str;
        int MAX_RETRIES = 10;
        int attempt;
        for (attempt = 0; attempt < MAX_RETRIES; attempt++) {
          String modelName = getGeminiModelName(
            RandomUtil.randomInt(1, 5));
          log.error("尝试次数：{}，模型：{}，prompt：{}", attempt, modelName,
            prompt);
          String text = CallGeminiApiUtil.callApi(prompt, modelName);
          List<String> sList = StrUtil.split(text, "\n");
          if (CollectionUtil.isNotEmpty(sList)
            && sList.size() / 2 == subStringList.size()) {
            totalStringList.addAll(sList);
            FileUtil.writeLines(sList, geminiFileNameTemp,
              StandardCharsets.UTF_8);
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              log.error("InterruptedException", e);
            }
            break;
          } else {
            log.info("尝试次数：{}，重试中...，大小不一致，输出：{}，输入：{}",
              attempt, sList.size() / 2, subStringList.size());
            try {
              Thread.sleep(3000);
            } catch (InterruptedException e) {
              log.error("InterruptedException", e);
            }
          }
        }
      } else {
        log.info("文件已存在，跳过：{}", geminiFileNameTemp);
        List<String> stringListTemp = FileUtil.readLines(geminiFileNameTemp,
          StandardCharsets.UTF_8);
        totalStringList.addAll(stringListTemp);
      }
    }

    // 写入最终文件
    if (isTotalTranslated(geminiFileName,
      stringLists.size())) { // && !CdFileUtil.isFileEmpty(geminiFileNameTempLast)
      log.info("写入最终文件：{}", geminiFileName);
      FileUtil.writeLines(totalStringList, geminiFileName,
        StandardCharsets.UTF_8);
    } else {
      log.info("未翻译完成，不写入最终文件：{}", geminiFileName);
    }
  }

  public static void translateByGemini(String fileName, String geminiFileName) {
    int groupSize = 20;
    String type = "subtitle";
    translateByGemini(type, fileName, geminiFileName, groupSize);
  }

  public static boolean isTotalTranslated(String geminiFileName, Integer size) {
    for (int i = 0; i < size; i++) {
      int count = i + 1;
      String lastIndex = java.lang.String.format("%03d", count);
      String geminiFileNameTempLast = CdFileUtil.addPostfixToFileName(
        geminiFileName, "_" + lastIndex);
      if (CdFileUtil.isFileEmpty(geminiFileNameTempLast)) {
        return false;
      }
    }
    return true;
  }

  /**
   * gemini-2.0-flash-lite Gemini 2.0 Flash Thinking Experimental
   * gemini-2.0-flash-thinking-exp-01-21
   *
   * @return Map<Integer, String>
   */
  public static Map<Integer, String> geminiModelNameMap() {

    // "gemini-2.0-flash"; // 选择合适的模型 "gemini-2.5-pro-exp-03-25";
    Map<Integer, String> geminiModelNameMap = new HashMap<>();
    geminiModelNameMap.put(1, "gemini-2.0-flash");
    geminiModelNameMap.put(2, "gemini-2.5-pro-exp-03-25");
    geminiModelNameMap.put(3, "gemini-2.0-flash-lite");
    geminiModelNameMap.put(4, "gemini-2.0-flash-thinking-exp-01-21");
    return geminiModelNameMap;
  }

  public static String getGeminiModelName(Integer index) {

    // "gemini-2.0-flash"; // 选择合适的模型 "gemini-2.5-pro-exp-03-25";
    Map<Integer, String> geminiModelNameMap = geminiModelNameMap();

    return geminiModelNameMap.get(index);
  }

  public static void translateByGrok(String type, String fileName,
    String grokFileName,
    Integer groupSize) {
    String systemPrompt = "你是一位专业的字幕翻译专家，请严格按照下面的格式进行回答：";
//    String head = "翻译下面的英文字幕，内容有的是一句话，有的是半句话，但是你直接翻译就行，返回格式为一句英文一句中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";
    String head = "翻译下面的英文字幕，内容有的是一句话，有的是半句话，但是你直接翻译就行，返回格式为一句英文一句中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";
    switch (type) {
      case "subtitle":
        head = SUBTITLE_HEAD;
        break;
      case "dialog":
        head = DIALOG_HEAD;
        break;
      default:
        head = SUBTITLE_HEAD;
        log.info("不支持的类型：{}", type);
    }
//    String userPrompt="";
    List<String> stringList = FileUtil.readLines(fileName,
      StandardCharsets.UTF_8);
    List<String> totalStringList = new ArrayList<>();
    List<List<String>> stringLists = ListUtils.partition(stringList, groupSize);

    int i = 0;
    for (List<String> subStringList : stringLists) {
      i++;
      String index = java.lang.String.format("%03d", i);

      String geminiFileNameTemp = CdFileUtil.addPostfixToFileName(
        grokFileName, "_" + index);
      if (CdFileUtil.isFileEmpty(geminiFileNameTemp)) {
        String str = java.lang.String.join("\n", subStringList);
        String prompt = head + str;
        int MAX_RETRIES = 10;
        int attempt;
        for (attempt = 0; attempt < MAX_RETRIES; attempt++) {
          String text = GrokApiUtil.callGrokApi(systemPrompt, prompt);
          List<String> sList = StrUtil.split(text, "\n");
          if (CollectionUtil.isNotEmpty(sList)
            && sList.size() / 2 == subStringList.size()) {
            totalStringList.addAll(sList);
            FileUtil.writeLines(sList, geminiFileNameTemp,
              StandardCharsets.UTF_8);
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              log.error("InterruptedException", e);
            }
            break;
          } else {
            log.info("尝试次数：{}，重试中...，大小不一致，输出：{}，输入：{}",
              attempt, sList.size() / 2, subStringList.size());
            try {
              Thread.sleep(3000);
            } catch (InterruptedException e) {
              log.error("InterruptedException", e);
            }
          }
        }
      } else {
        log.info("文件已存在，跳过：{}", geminiFileNameTemp);
        List<String> stringListTemp = FileUtil.readLines(geminiFileNameTemp,
          StandardCharsets.UTF_8);
        totalStringList.addAll(stringListTemp);
      }
    }

    // 写入最终文件
    if (isTotalTranslated(grokFileName,
      stringLists.size())) { // && !CdFileUtil.isFileEmpty(geminiFileNameTempLast)
      log.info("写入最终文件：{}", grokFileName);
      FileUtil.writeLines(totalStringList, grokFileName,
        StandardCharsets.UTF_8);
    } else {
      log.info("未翻译完成，不写入最终文件：{}", grokFileName);
    }
  }

//  public static void translateDialogByGrok(String fileName, String grokFileName,
//    Integer groupSize) {
//    String systemPrompt = "你是一位专业的字幕翻译专家，请严格按照下面的格式进行回答：";
//    String head = "翻译下面的英文字幕，一行一行翻译，每行内容有的是一句话，有的是几句话，不要拆开，不要分行，你直接翻译就行，返回格式为一行英文一行中文，我给你n行就返回2*n行，只需要返回内容，不要其他的：\n";
////    String userPrompt="";
//    List<String> stringList = FileUtil.readLines(fileName,
//      StandardCharsets.UTF_8);
//
//    // 只取偶数行，去掉奇数行的内容
//    stringList = getEvenIndexedLines(stringList);
//
//    List<String> totalStringList = new ArrayList<>();
//    List<List<String>> stringLists = ListUtils.partition(stringList, groupSize);
//
//    int i = 0;
//    for (List<String> subStringList : stringLists) {
//      i++;
//      String index = java.lang.String.format("%03d", i);
//      String geminiFileNameTemp = CdFileUtil.addPostfixToFileName(
//        grokFileName, "_" + index);
//      if (CdFileUtil.isFileEmpty(geminiFileNameTemp)) {
//        String str = java.lang.String.join("\n", subStringList);
//        String prompt = head + str;
//        int MAX_RETRIES = 10;
//        int attempt;
//        for (attempt = 0; attempt < MAX_RETRIES; attempt++) {
//          String text = GrokApiUtil.callGrokApi(systemPrompt, prompt);
//          List<String> sList = StrUtil.split(text, "\n");
//          // 过滤掉空串
//          sList = filterNonBlank(sList);
//          if (CollectionUtil.isNotEmpty(sList)
//            && sList.size() / 2 == subStringList.size()) {
//            totalStringList.addAll(sList);
//            FileUtil.writeLines(sList, geminiFileNameTemp,
//              StandardCharsets.UTF_8);
//            try {
//              Thread.sleep(1000);
//            } catch (InterruptedException e) {
//              log.error("InterruptedException", e);
//            }
//            break;
//          } else {
//            log.info("尝试次数：{}，重试中...，大小不一致，输出：{}，输入：{}",
//              attempt, sList.size() / 2, subStringList.size());
//            try {
//              Thread.sleep(3000);
//            } catch (InterruptedException e) {
//              log.error("InterruptedException", e);
//            }
//          }
//        }
//      } else {
//        log.info("文件已存在，跳过：{}", geminiFileNameTemp);
//        List<String> stringListTemp = FileUtil.readLines(geminiFileNameTemp,
//          StandardCharsets.UTF_8);
//        totalStringList.addAll(stringListTemp);
//      }
//    }
//
//    // 写入最终文件
//    if (isTotalTranslated(grokFileName,
//      stringLists.size())) { // && !CdFileUtil.isFileEmpty(geminiFileNameTempLast)
//      log.info("写入最终文件：{}", grokFileName);
//      FileUtil.writeLines(totalStringList, grokFileName,
//        StandardCharsets.UTF_8);
//    } else {
//      log.info("未翻译完成，不写入最终文件：{}", grokFileName);
//    }
//  }

  public static void translateByGrok(String fileName, String grokFileName) {
    int groupSize = 20;
    String type = "subtitle";
    translateByGrok(type, fileName, grokFileName, groupSize);
  }

  public static List<String> getEvenIndexedLines(List<String> list) {
    return IntStream.range(0, list.size())
      .filter(i -> i % 2 == 1) // 过滤偶数索引
      .mapToObj(list::get)     // 映射到对应元素
      .collect(Collectors.toList()); // 收集到新List
  }

  /**
   * 过滤List<String>中的空串（包括null、空字符串和只包含空格的字符串）
   *
   * @param list 输入列表
   * @return 过滤后的列表
   */
  public static List<String> filterNonBlank(List<String> list) {
    return list.stream()
      .filter(str -> str != null && !str.trim().isEmpty()) // 保留非空串
      .collect(Collectors.toList());
  }
}
