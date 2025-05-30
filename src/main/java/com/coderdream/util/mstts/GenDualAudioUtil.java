package com.coderdream.util.mstts;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.resource.ResourcesSourcePathUtil;
import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.util.string.SceneMatcherUtil;
import com.coderdream.vo.SentenceVO;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 语音合成工具类，使用微软 Azure 认知服务 Text to Speech API。
 */
@Slf4j
public class GenDualAudioUtil {

  // 最大重试次数，作为常量
  private static final int MAX_RETRIES = 16;
  // 重试日志文件路径
  private static final String RETRY_LOG_PATH = "retry_log.txt";
  // 失败日志文件路径
  private static final String FAILURE_LOG_PATH = "failure_log.txt";


  /**
   * 将文本列表转换为音频文件。
   *
   * @param textList  待转换的文本列表
   * @param voiceName 语音名称，如 zh-CN-XiaochenNeural 或 en-US-JennyNeural
   * @param pitch     音调，如 medium 或 default
   * @param volume    音量，如 medium 或 default
   * @param rate      语速，如 medium 或 default
   * @param fileName  输出音频文件名（包含完整路径）
   * @param audioType 音频类型，如 wav 或 mp3
   * @param lang      语言代码，如 zh-cn 或 en-us
   */
  public static void content2Audio(List<String> textList, String voiceName,
    String pitch, String volume, String rate, String fileName,
    String audioType, String lang) {
    long startTime = System.currentTimeMillis();
    int retryCount = 0; // 重试计数器
    boolean success = false; // 是否成功标记

    // 循环尝试，直到成功或达到最大重试次数
    while (retryCount < MAX_RETRIES && !success) {
      try {
        String speechKey = null; // 语音密钥
        SpeechConfig speechConfig = null; // 语音配置

        // 根据语言选择语音密钥和区域
        switch (lang.toLowerCase()) {
          case "zh-cn" -> {
            speechKey = CdConstants.SPEECH_KEY_EASTASIA; // 中文语音密钥
            speechConfig = SpeechConfig.fromSubscription(speechKey,
              CdConstants.SPEECH_REGION_EASTASIA); // 东亚区域
          }
          case "en-us" -> {
            speechKey = CdConstants.SPEECH_KEY_EAST_US; // 英文语音密钥
            speechConfig = SpeechConfig.fromSubscription(speechKey,
              CdConstants.SPEECH_REGION_EASTUS); // 美国东部区域
          }
          default -> throw new IllegalArgumentException(
            "不支持的语言代码: " + lang); // 不支持的语言抛出异常
        }

        // 根据音频类型设置输出格式
        switch (audioType.toLowerCase()) {
          case "wav":
            speechConfig.setSpeechSynthesisOutputFormat(
              SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm); // 设置为 WAV 格式
            break;
          case "mp3":
            speechConfig.setSpeechSynthesisOutputFormat(
              SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3); // 设置为 MP3 格式
            break;
          default:
            throw new IllegalArgumentException(
              "不支持的音频格式: " + audioType); // 不支持的音频格式抛出异常
        }

        // 创建语音合成器
        try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(
          speechConfig, null)) {
          // 生成 SSML 格式的文本，包含语音设置
          String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch,
            volume, rate);
          // 执行语音合成
          SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);

          // 判断合成结果
          if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
            // 如果合成成功，获取音频流并保存到文件
            AudioDataStream stream = AudioDataStream.fromResult(result);
            stream.saveToWavFile(fileName);// TODO 耗时
            long endTime = System.currentTimeMillis();
            long durationMillis = endTime - startTime;
            log.info("音频文件生成成功: {}，耗时：{}", fileName,
              CdTimeUtil.formatDuration(durationMillis));
            success = true; // 标记为成功
          } else {
//              StringBuilder content = new StringBuilder();
//              for (String text : textList) {
//                content.append(text).append("\n");
//              }
            // 如果合成失败，记录错误信息，增加重试计数器
            log.error("语音合成失败: {}，内容为：{}", result.getReason(), ssml);
            retryCount++;
            logRetryInfo(fileName, retryCount);  // 记录重试日志
          }
        }
      } catch (Exception e) {
        // 捕获异常，记录错误信息，增加重试计数器
        log.error("语音合成出错: {}", e.getMessage(), e);
        retryCount++;
        logRetryInfo(fileName, retryCount);  // 记录重试日志
      }

      // 如果失败次数超过最大重试次数，记录失败信息
      if (!success && retryCount >= MAX_RETRIES) {
        logFailureInfo(fileName); // 记录失败日志
      }

      // 如果没有成功，则休眠一段时间后重试
      if (!success) {
        try {
          Thread.sleep(1000); // 重试前的延迟（可以调整）
        } catch (InterruptedException interruptedException) {
          log.error("线程休眠被中断：{}", interruptedException.getMessage());
        }
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
      content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
        "medium", "medium", "medium", cnFile, audioType, "zh-cn");
      content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
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
//     SceneMatcherUtil.addCommaBeforeSpace("testString");
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      // 特殊处理，在 Scene XXX 后添加逗号和句号，以便在音频中停顿更明显
      sentenceVO.setChinese(
        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getChinese()));
      sentenceVO.setEnglish(
        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getEnglish()));

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
        content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
          "medium", "medium", "medium", cnFileName, audioType, "zh-cn");
      } else {
        log.warn("中文音频文件已存在，跳过生成: {}", cnFileName);
      }

      if (CdFileUtil.isFileEmpty(enFileName)) {
        content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "default", enFileName, audioType, "en-us");
      } else {
        log.warn("英文音频文件已存在，跳过生成: {}", enFileName);
      }
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
  public static void genDialogToAudioThreeTypes(String folderName,
    String subFolder,
    String fileName, String audioType) {
    String fullFileName =
      folderName + File.separator + subFolder + File.separator + fileName
        + ".txt";
    log.info("开始解析脚本文件: {}", fullFileName);

    // 设置输出目录
    String outputDirCn =
      folderName + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_CN; // 中文音频输出目录
    String outputDirEnNormal =
      folderName + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN; // 英文音频输出目录
    String outputDirEnSlow =
      folderName + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator
        + CdConstants.LANG_EN_SLOW; // 英文音频输出目录
    // 创建目录
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("中文音频目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEnNormal = new File(outputDirEnNormal);
    if (!dirEnNormal.exists() && dirEnNormal.mkdirs()) {
      log.info("英文音频目录创建成功: {}", dirEnNormal.getAbsolutePath());
    }

    File dirEnSlow = new File(outputDirEnSlow);
    if (!dirEnSlow.exists() && dirEnSlow.mkdirs()) {
      log.info("英文音频目录创建成功: {}", dirEnSlow.getAbsolutePath());
    }

    // 解析文本文件
    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFileV2(
      fullFileName);
    int number = 0;
//     SceneMatcherUtil.addCommaBeforeSpace("testString");
    // 遍历句子列表，生成音频
    for (SentenceVO sentenceVO : sentenceVOs) {
      // 特殊处理，在 Scene XXX 后添加逗号和句号，以便在音频中停顿更明显
      sentenceVO.setChinese(
        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getChinese()));
      sentenceVO.setEnglish(
        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getEnglish()));

      number++;
      // 构建中文音频文件名
      String cnFileName =
        outputDirCn + File.separator + subFolder + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_cn." + audioType;
      // 构建英文音频文件名
      String enNormalFileName =
        outputDirEnNormal + File.separator + subFolder + "_"
          + MessageFormat.format(
          "{0,number,000}", number) + "_en." + audioType;
      // 构建英文音频文件名
      String enSlowFileName =
        outputDirEnSlow + File.separator + subFolder + "_"
          + MessageFormat.format(
          "{0,number,000}", number) + "_en." + audioType;

      // 调用 content2Audio 生成中文和英文音频
      if (CdFileUtil.isFileEmpty(cnFileName)) {
        content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
          "medium", "medium", "medium", cnFileName, audioType, "zh-cn");
      } else {
        log.warn("中文音频文件已存在，跳过生成: {}", cnFileName);
      }

      if (CdFileUtil.isFileEmpty(enNormalFileName)) {
        content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "default", enNormalFileName, audioType,
          "en-us");
      } else {
        log.warn("常速英文音频文件已存在，跳过生成: {}", enNormalFileName);
      }

      if (CdFileUtil.isFileEmpty(enSlowFileName)) {
        content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
          "default", "default", "slow", enSlowFileName, audioType, "en-us");
      } else {
        log.warn("慢速英文音频文件已存在，跳过生成: {}", enSlowFileName);
      }
    }
  }

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   */
  public static void genHeadAudio() {
    String headContentFileName = "head_content.txt";
    String audioType = "wav";
    genHeadAudio(headContentFileName, audioType);
  }

  /**
   * 将对话文本文件转换为音频文件（中英文）。
   *
   * @param audioType 音频类型，如 wav 或 mp3
   */
  public static void genHeadAudio(String headContentFileName,
    String audioType) {
    String resourcesPath = ResourcesSourcePathUtil.getResourcesSourceAbsolutePath();
    List<String> contentList = CdFileUtil.readFileContent(
      resourcesPath + File.separator + headContentFileName);
    if (CollectionUtil.isEmpty(contentList)) {
      log.error("{} 文件内容为空", headContentFileName);
      return;
    }

    for (String content : contentList) {
      String[] split = content.split("\\|");
      if (split.length == 3) {
        // 随机整数0~4
        String bookName = split[0].trim();
        String enContent = split[1].trim();
        String cnContent = split[2].trim();

        String folderPath =
          OperatingSystem.getFolderPath(bookName) + File.separator + "head";
        File dirCn = new File(folderPath);
        if (!dirCn.exists() && dirCn.mkdirs()) {
          log.info("Head音频目录创建成功: {}", dirCn.getAbsolutePath());
        }

        // 构建中文音频文件名
        String cnFileName =
          folderPath + File.separator + bookName + "_cn." + audioType;
        // 构建英文音频文件名
        String enFileName =
          folderPath + File.separator + bookName + "_en." + audioType;

        // 调用 content2Audio 生成中文和英文音频
        if (CdFileUtil.isFileEmpty(cnFileName)) {
          content2Audio(List.of(cnContent),
            "zh-CN-XiaochenNeural",
            "medium", "medium", "medium", cnFileName, audioType, "zh-cn");
        } else {
          log.warn("中文音频文件已存在，跳过生成: {}", cnFileName);
        }

        if (CdFileUtil.isFileEmpty(enFileName)) {
          content2Audio(List.of(enContent), "en-US-JennyNeural",
            "default", "default", "default", enFileName, audioType, "en-us");
        } else {
          log.warn("英文音频文件已存在，跳过生成: {}", enFileName);
        }
      }
    }

//    String fullFileName =
//      folderName + subFolder + File.separator + fileName
//        + ".txt";
//    log.info("开始解析脚本文件: {}", fullFileName);
//
//    // 设置输出目录
//    String outputDirCn =
//      folderName + subFolder + File.separator
//        + CdConstants.AUDIO_FOLDER + File.separator
//        + CdConstants.LANG_CN; // 中文音频输出目录
//    String outputDirEn =
//      folderName + subFolder + File.separator
//        + CdConstants.AUDIO_FOLDER + File.separator
//        + CdConstants.LANG_EN; // 英文音频输出目录
//    // 创建目录
//    File dirCn = new File(outputDirCn);
//    if (!dirCn.exists() && dirCn.mkdirs()) {
//      log.info("中文音频目录创建成功: {}", dirCn.getAbsolutePath());
//    }
//    File dirEn = new File(outputDirEn);
//    if (!dirEn.exists() && dirEn.mkdirs()) {
//      log.info("英文音频目录创建成功: {}", dirEn.getAbsolutePath());
//    }
//
//    // 解析文本文件
//    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
//      fullFileName);
//    int number = 0;
////     SceneMatcherUtil.addCommaBeforeSpace("testString");
//    // 遍历句子列表，生成音频
//    for (SentenceVO sentenceVO : sentenceVOs) {
//      // 特殊处理，在 Scene XXX 后添加逗号和句号，以便在音频中停顿更明显
//      sentenceVO.setChinese(
//        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getChinese()));
//      sentenceVO.setEnglish(
//        SceneMatcherUtil.addCommaBeforeSpace(sentenceVO.getEnglish()));
//
//      number++;
//
//    }
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
    int number = 0;
    // 遍历句子列表，生成音频
    for (String sentence : sentences) {
      number++;
      // 构建中文音频文件名
      String cnFile =
        outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", number) + "_cn." + audioType;

      // 调用 content2Audio 生成中文和英文音频
      content2Audio(List.of(sentence), "zh-CN-XiaochenNeural",
        "medium", "medium", "medium", cnFile, audioType, "zh-cn");
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

      content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
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

  /**
   * 记录重试日志。
   *
   * @param fileName   文件名（包含完整路径）
   * @param retryCount 重试次数
   */
  private static void logRetryInfo(String fileName, int retryCount) {
    try {
      String logMessage = String.format("文件：%s，重试次数：%d", fileName,
        retryCount);
      // 获取视频文件路径，并将日志写入文件
      // 写入
      File file = new File(fileName);
      writeLogToFile(file.getParent() + File.separator + RETRY_LOG_PATH,
        logMessage);
    } catch (IOException e) {
      log.error("记录重试日志时出错", e);
    }
  }

  /**
   * 记录失败日志。
   *
   * @param fileName 文件名（包含完整路径）
   */
  private static void logFailureInfo(String fileName) {
    try {
      String logMessage = String.format("文件：%s，创建音频失败", fileName);

      // TODO
      File file = new File(fileName);
      // 获取视频文件路径，并将日志写入文件
      writeLogToFile(
        file.getParent() + FAILURE_LOG_PATH,
        logMessage);
    } catch (IOException e) {
      log.error("记录失败日志时出错", e);
    }
  }

  /**
   * 将日志信息写入文件。
   *
   * @param logFilePath 日志文件路径
   * @param message     日志信息
   * @throws IOException 写入文件时可能抛出的 I/O 异常
   */
  private static void writeLogToFile(String logFilePath, String message)
    throws IOException {
    File logFile = new File(logFilePath);
    // 如果日志文件不存在，创建父目录和文件
    if (!logFile.exists()) {
      boolean mkdir = logFile.getParentFile().mkdirs();// 创建日志文件的父目录
      log.info("创建日志文件父目录: {}，路径：{}", mkdir, logFilePath);
      boolean newFile = logFile.createNewFile();// 创建文件
      log.info("创建日志文件: {}", newFile);
    }

    // 使用 BufferedWriter 写入日志信息
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(logFile, true))) {
      writer.write(message);
      writer.newLine();
    }
  }
}
