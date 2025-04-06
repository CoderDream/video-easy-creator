package com.coderdream.util.mstts;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.util.sentence.StanfordSentenceSplitter;
import com.coderdream.vo.SentenceVO;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 语音合成工具类，使用微软 Azure 认知服务 Text to Speech API。
 */
@Slf4j
public class SpeechUtil07 {

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   *
   * @param chapterPath 文件所在文件夹路径
   * @param chapterName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genDialog2Audio(String chapterPath,  String chapterName,
    String audioName, String audioType, String lang, Integer groupSize) {
    String fullFileName = chapterPath + File.separator + chapterName + ".txt";
    log.info("开始解析文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      chapterPath + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEn =
      chapterPath + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
    }

    List<String> textList = FileUtil.readLines(fullFileName,
      StandardCharsets.UTF_8);
    textList.removeIf(String::isBlank);
    List<String> sentenceStringList = StanfordSentenceSplitter.splitSentences(
      String.join("\n", textList));

    List<List<String>> groupedTextList = ListUtil.partition(sentenceStringList,
      groupSize);

    int number = 0;
    // 遍历句子列表，生成音频
    for (List<String> subTextList : groupedTextList) {
      number++;
      String file = switch (lang) {
        case "zh-cn" ->
          // 构建英文音频文件名
          outputDirCn + File.separator + chapterName + "_"
          + MessageFormat.format(
          "{0,number,000}", number) + "_cn." + audioType;
        case "en-us" ->
          // 构建英文音频文件名
          outputDirEn + File.separator + chapterName + "_"
            + MessageFormat.format("{0,number,000}", number) + "_en."
            + audioType;
        default -> "";
        // 构建中文音频文件名
      };
      // 调用 content2Audio 生成中文和英文音频
      SpeechSingleUtil.content2Audio(subTextList, audioName,
        "medium", "medium", "medium", file, audioType, lang);
    }
  }

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   *
   * @param folderName 文件所在文件夹路径
   * @param fileName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genDialog2Audio(String folderName, String fileName,
    String audioType) {
    String fullFileName = folderName + File.separator + fileName + ".txt";
    log.info("开始解析文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
    }

    // 解析文本文件
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullFileName);
    int number = 0;
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      // 构建中文音频文件名
      String cnFile =
        outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_cn." + audioType;
      // 构建英文音频文件名
      String enFile =
        outputDirEn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_en." + audioType;

      // 调用 content2Audio 生成中文和英文音频
      SpeechPool.content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
        "medium", "medium", "medium", cnFile, audioType, "zh-cn");
      SpeechPool.content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
        "default", "default", "default", enFile, audioType, "en-us");
    }
  }

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   *
   * @param folderName 根文件夹
   * @param subFolder  子文件夹
   * @param fileName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genDialog2Audio900(String folderName, String subFolder,
    String fileName, String audioType) {
    String fullFileName =
      folderName + subFolder + File.separator + fileName
        + ".txt";
    log.info("开始解析脚本文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEn =
      folderName + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("中文音频目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("英文音频目录创建成功: {}", dirEn.getAbsolutePath());
    }

    // 解析文本文件
    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
      fullFileName);
    int number = 0;
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      // 构建中文音频文件名
      String cnFileName =
        outputDirCn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_cn." + audioType;
      // 构建英文音频文件名
      String enFileName =
        outputDirEn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_en." + audioType;

      // 调用 content2Audio 生成中文和英文音频
      if (CdFileUtil.isFileEmpty(cnFileName)) {
        SpeechPool.content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
          "medium", "medium", "medium", cnFileName, audioType, "zh-cn");
      } else {
        log.info("中文音频文件已存在，跳过生成: {}", cnFileName);
      }

      if (CdFileUtil.isFileEmpty(enFileName)) {
        SpeechPool.content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "default", enFileName, audioType, "en-us");
      } else {
        log.info("英文音频文件已存在，跳过生成: {}", enFileName);
      }
    }
  }


  /**
   * 将对话文本文件转换为音频文件：中文常速、英文常速、英文慢速
   *
   * @param folderName 根文件夹
   * @param subFolder  子文件夹
   * @param fileName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genDialog2AudioThreeTypes(String folderName,
    String subFolder,
    String fileName, String audioType) {
    String fullFileName =
      folderName + subFolder + File.separator + fileName
        + ".txt";
    log.info("开始解析脚本文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEn =
      folderName + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    String outputDirEnSlow =
      folderName + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN_SLOW; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("中文常速音频目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("英文常速音频目录创建成功: {}", dirEn.getAbsolutePath());
    }

    File dirEnSlow = new File(outputDirEnSlow);
    if (!dirEnSlow.exists() && dirEnSlow.mkdirs()) {
      log.info("英文慢速音频目录创建成功: {}", dirEnSlow.getAbsolutePath());
    }

    // 解析文本文件
    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
      fullFileName);
    int number = 0;
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      // 构建中文音频文件名
      String cnFileName =
        outputDirCn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_" + CdConstants.LANG_CN + "."
          + audioType;
      // 构建英文音频文件名
      String enFileName =
        outputDirEn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_" + CdConstants.LANG_EN + "."
          + audioType;
      // 构建英文音频文件名
      String enSlowFileName =
        outputDirEn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_" + CdConstants.LANG_EN_SLOW + "."
          + audioType;

      // 调用 content2Audio 生成中文和英文音频
      if (CdFileUtil.isFileEmpty(cnFileName)) {
        SpeechPool.content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
          "medium", "medium", "medium", cnFileName, audioType, "zh-cn");
      } else {
        log.info("中文常速音频文件已存在，跳过生成: {}", cnFileName);
      }

      if (CdFileUtil.isFileEmpty(enFileName)) {
        SpeechPool.content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "default", enFileName, audioType, "en-us");
      } else {
        log.info("英文常速音频文件已存在，跳过生成: {}", enFileName);
      }

      if (CdFileUtil.isFileEmpty(enSlowFileName)) {
        SpeechPool.content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "-20%", enSlowFileName, audioType, "en-us");
      } else {
        log.info("英文慢速音频文件已存在，跳过生成: {}", enSlowFileName);
      }
    }
  }

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   *
   * @param folderName 文件所在文件夹路径
   * @param fileName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genDialog2CnAudio(String folderName, String fileName,
    String audioType) {
    String fullFileName = folderName + File.separator + fileName + ".txt";
    log.info("开始解析文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }

    // 解析文本文件
    List<String> sentences = CdFileUtil.readFileContent(fullFileName);
    if(CollectionUtil.isNotEmpty(sentences)) {
      int number = 0;
      // 遍历句子列表，生成音频
      for (String sentence : sentences) {
        number++;
        // 构建中文音频文件名
        String cnFile =
          outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
            "{0,number,000}", number) + "_cn." + audioType;

        // 调用 content2Audio 生成中文和英文音频
        SpeechPool.content2Audio(List.of(sentence), "zh-CN-XiaochenNeural",
          "medium", "medium", "medium", cnFile, audioType, "zh-cn");
      }
    }
  }

  /**
   * 生成英文音频文件。
   *
   * @param folderName 文件所在文件夹路径
   * @param fileName   文件名（不包含扩展名）
   * @param audioType  音频类型，如 wav 或 mp3
   */
  public static void genEnAudio(String folderName, String fileName,
    String audioType) {
    String fullFileName = folderName + File.separator + fileName + ".txt";
    log.info("开始解析文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
    }

    // 解析文本文件
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullFileName);
    int number = 0;
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      // 构建英文音频文件名
      String enFile =
        outputDirEn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_en." + audioType;

      SpeechPool.content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
        "default", "default", "default", enFile, audioType, "en-us");
    }
  }

  /**
   * 将对话文本文件转换为音频文件（中英文），使用默认的资源路径。
   *
   * @param fileName  文件名（不包含扩展名）
   * @param audioType 音频类型，如 wav 或 mp3
   */
  public static void genDialog2Audio(String fileName, String audioType) {
    genDialog2Audio(CdConstants.RESOURCES_BASE_PATH, fileName, audioType);
  }

//  /**
//   * 将日志信息写入文件。
//   *
//   * @param logFilePath 日志文件路径
//   * @param message     日志信息
//   * @throws IOException 写入文件时可能抛出的 I/O 异常
//   */
//  private static void writeLogToFile(String logFilePath, String message)
//    throws IOException {
//    File logFile = new File(logFilePath);
//    // 如果日志文件不存在，创建父目录和文件
//    if (!logFile.exists()) {
//      boolean mkdir = logFile.getParentFile().mkdirs();// 创建日志文件的父目录
//      log.info("创建日志文件父目录: {}，路径：{}", mkdir, logFilePath);
//      boolean newFile = logFile.createNewFile();// 创建文件
//      log.info("创建日志文件: {}", newFile);
//    }
//
//    // 使用 BufferedWriter 写入日志信息
//    try (BufferedWriter writer = new BufferedWriter(
//      new FileWriter(logFile, true))) {
//      writer.write(message);
//      writer.newLine();
//    }
//  }
}
