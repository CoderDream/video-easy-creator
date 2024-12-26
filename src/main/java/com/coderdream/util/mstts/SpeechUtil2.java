package com.coderdream.util.mstts;

import cn.hutool.core.util.StrUtil;
import com.coderdream.util.CdConstants;
import com.coderdream.util.chatgpt.TextParserUtil;
import com.coderdream.vo.SentenceVO;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpeechUtil2 {


//  public static void content2mp3() {
//    String speechKey = CdConstants.SPEECH_KEY;
//    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
//      CdConstants.SPEECH_REGION_EASTASIA);
//    // set the output format
//    speechConfig.setSpeechSynthesisOutputFormat(
//      SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
//    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
//      null);
//    String xmlFileName = "src/main/resources/ssml.xml";
//    String ssml = xmlToString(xmlFileName);
//    SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
//    AudioDataStream stream = AudioDataStream.fromResult(result);
//    stream.saveToWavFile("test.mp3");
//  }

  /**
   * @param filePath 文件路径
   * @return 文件内容
   */
  private static String xmlToString(String filePath) {
    File file = new File(filePath);
    StringBuilder fileContents = new StringBuilder((int) file.length());

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        fileContents.append(scanner.nextLine()).append(System.lineSeparator());
      }
      log.info("文件内容：{}", fileContents);
      return fileContents.toString().trim();
    } catch (FileNotFoundException ex) {
      log.error("File not found: {}", filePath);
      return "File not found.";
    }
  }
//
//  public static void content2mp3(String ssml, String fileName) {
//    String speechKey = CdConstants.SPEECH_KEY;
//    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
//      CdConstants.SPEECH_REGION_EASTASIA);
//    // set the output format
//    speechConfig.setSpeechSynthesisOutputFormat(
//      SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
//    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
//      null);
//    SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
//    AudioDataStream stream = AudioDataStream.fromResult(result);
//    stream.saveToWavFile(fileName);
//  }
//
//  public static void content2mp3(List<String> textList, String fileName) {
//    content2mp3(textList, "zh-CN-XiaochenNeural", "medium", "medium",
//      "medium", fileName);
//  }
//
//  public static void content2mp3(List<String> textList, String voiceName,
//    String pitch, String volume, String rate, String fileName) {
//    String speechKey = CdConstants.SPEECH_KEY;
//    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
//      CdConstants.SPEECH_REGION_EASTASIA);
//    // set the output format
//    speechConfig.setSpeechSynthesisOutputFormat(
//      SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
//    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
//      null);
//    if (StrUtil.isBlank(voiceName)) {
//      voiceName = "zh-CN-XiaochenNeural";
//    }
//    if (StrUtil.isBlank(pitch)) {
//      pitch = "medium";
//    }
//    if (StrUtil.isBlank(volume)) {
//      volume = "medium";
//    }
//    if (StrUtil.isBlank(rate)) {
//      rate = "medium";
//    }
//
//    String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch, volume,
//      rate);
//    SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
//    AudioDataStream stream = AudioDataStream.fromResult(result);
//    stream.saveToWavFile(fileName);
//  }

  public static void content2wav(List<String> textList, String voiceName,
    String pitch, String volume, String rate, String fileName) {

    // 获取语音密钥和区域配置
    String speechKey = CdConstants.SPEECH_KEY;
    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
      CdConstants.SPEECH_REGION_EASTASIA);

    // 设置输出格式为 WAV
    speechConfig.setSpeechSynthesisOutputFormat(
      SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

    // 初始化语音合成器
    try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(
      speechConfig, null)) {

      // 设置默认参数
      voiceName =
        StrUtil.isNotBlank(voiceName) ? voiceName : "zh-CN-XiaochenNeural";
      pitch = StrUtil.isNotBlank(pitch) ? pitch : "medium";
      volume = StrUtil.isNotBlank(volume) ? volume : "medium";
      rate = StrUtil.isNotBlank(rate) ? rate : "medium";

      // 生成 SSML 内容
      String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch,
        volume, rate);

      // 合成语音
      SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
      if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
        // 保存为 WAV 文件
        AudioDataStream stream = AudioDataStream.fromResult(result);
        stream.saveToWavFile(fileName);
        System.out.println("音频文件生成成功: " + fileName);
      } else {
        System.err.println("content2wav 语音合成失败: " + result.getReason());
      }
    } catch (Exception e) {
      System.err.println("语音合成出错: " + e.getMessage());
      log.error("语音合成出错: {}", e.getMessage(), e);
      //e.printStackTrace();
    }
  }

  /**
   * 将文本内容转换为音频文件，支持 WAV 和 MP3 格式。
   * @param textList  文本内容列表
   * @param voiceName 语音名称
   * @param pitch 语音音高
   * @param volume  语音音量
   * @param rate      语音语速
   * @param fileName    音频文件名
   * @param audioType 音频格式（WAV 或 MP3）
   * @param lang    语音语言代码（例如：zh-CN 表示中文）
   */
  public static void content2Audio(List<String> textList, String voiceName,
    String pitch, String volume, String rate, String fileName,
    String audioType, String lang) {
    // 获取语音密钥和区域配置
    String speechKey = null;
    SpeechConfig speechConfig = null;

    switch (lang.toLowerCase()) {
      case "zh-cn" -> {
        speechKey = CdConstants.SPEECH_KEY;
        speechConfig = SpeechConfig.fromSubscription(speechKey,
          CdConstants.SPEECH_REGION_EASTASIA);
      }
      case "en-us" -> {
        speechKey = CdConstants.SPEECH_KEY_EAST_US;
        speechConfig = SpeechConfig.fromSubscription(speechKey,
          CdConstants.SPEECH_REGION_EASTUS);
      }
      default ->
        throw new IllegalArgumentException("不支持的语言代码: " + lang);
    }

    // 获取语音密钥和区域配置
//    String speechKey = CdConstants.SPEECH_KEY;
//    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
//      CdConstants.SPEECH_REGION_EASTASIA);

    switch (audioType.toLowerCase()) {
      case "wav":
        // 设置输出格式为 WAV
        speechConfig.setSpeechSynthesisOutputFormat(
          SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);
        break;
      case "mp3":
        // 设置输出格式为 MP3
        speechConfig.setSpeechSynthesisOutputFormat(
          SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
        break;
      default:
        throw new IllegalArgumentException("不支持的音频格式: " + audioType);

    }
//    // 设置输出格式为 WAV
//    speechConfig.setSpeechSynthesisOutputFormat(
//      SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

    // 初始化语音合成器
    try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(
      speechConfig, null)) {

      // 设置默认参数
//      voiceName =
//        StrUtil.isNotBlank(voiceName) ? voiceName : "zh-CN-XiaochenNeural";
//      pitch = StrUtil.isNotBlank(pitch) ? pitch : "medium";
//      volume = StrUtil.isNotBlank(volume) ? volume : "medium";
//      rate = StrUtil.isNotBlank(rate) ? rate : "medium";

      // 生成 SSML 内容
      String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch,
        volume, rate);

      // 合成语音
      SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
      if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
        // 保存为 WAV 文件
        AudioDataStream stream = AudioDataStream.fromResult(result);
        stream.saveToWavFile(fileName);
        System.out.println("音频文件生成成功: " + fileName);
      } else {
        System.err.println("content2Audio 语音合成失败: " + result.getReason());
      }
    } catch (Exception e) {
      System.err.println("语音合成出错: " + e.getMessage());
      log.error("语音合成出错: {}", e.getMessage(), e);
      //e.printStackTrace();
    }
  }


  /**
   * 生成对话音频
   *
   * @param folderName 文件夹名
   * @param fileName   文件名
   */
//  public static void genDialog2Mp3(String folderName, String fileName) {
//    String fullFileName = folderName + File.separator + fileName + ".txt";
//    // 示例数据
////    String filePath = "src/main/resources/CampingInvitation.txt";
//    log.info("开始解析文件: {}", fullFileName);
//
//    //
//    String outputDirCn =
//      folderName + File.separator + "mp3" + File.separator + "cn";
//    String outputDirEn =
//      folderName + File.separator + "mp3" + File.separator + "en";
//    // 确保输出目录存在
//    File dirCn = new File(outputDirCn);
//    if (!dirCn.exists() && dirCn.mkdirs()) {
//      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
//    }
//    File dirEn = new File(outputDirEn);
//    if (!dirEn.exists() && dirEn.mkdirs()) {
//      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
//    }
//
//    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(
//      fullFileName);
//    int number = 0;
//    for (SentenceVO sentenceVO : sentenceVOs) {
//      number++;
//      log.info("英文: {}, 发音: {}, 中文: {}", sentenceVO.getEnglish(),
//        sentenceVO.getPhonetics(), sentenceVO.getChinese());
//      content2mp3(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
//        "medium", "medium",
//        "medium",
//        outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
//          "{0,number,000}",
//          number) + "_cn.mp3");
//      content2mp3(List.of(sentenceVO.getEnglish()), "zh-CN-XiaochenNeural",
//        "medium", "medium",
//        "medium",
//        outputDirEn + File.separator + fileName + "_" + MessageFormat.format(
//          "{0,number,000}",
//          number) + "_en.mp3");
//    }
//  }

  /**
   * 生成对话音频
   * <p>注意：此方法会生成两个文件夹，一个用于存放中文音频，一个用于存放英文音频。音频文件名会按照序号进行命名，例如
   * "CampingInvitation_001_cn.mp3"。
   *
   * @param folderName 文件夹名
   * @param fileName   文件名
   * @param audioType  音频类型，例如 "mp3" 或 "wav"
   */
  public static void genDialog2Audio(String folderName, String fileName,
    String audioType) {
    String fullFileName = folderName + File.separator + fileName + ".txt";
    // 示例数据
//    String filePath = "src/main/resources/CampingInvitation.txt";
    log.info("开始解析文件: {}", fullFileName);

    //
    String outputDirCn =
      folderName + File.separator + audioType + File.separator + "cn";
    String outputDirEn =
      folderName + File.separator + audioType + File.separator + "en";
    // 确保输出目录存在
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
    }

    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(
      fullFileName);
    int number = 0;
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
//      log.info("英文: {}, 发音: {}, 中文: {}", sentenceVO.getEnglish(),
//        sentenceVO.getPhonetics(), sentenceVO.getChinese());
      content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
        "medium", "medium",
        "medium",
        outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}",
          number) + "_cn." + audioType, audioType, "zh-cn");
      // en-US-AmberNeural en-US-BrandonNeural en-US-JennyNeural
      content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
        "default", "default",
        "default",
        outputDirEn + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}",
          number) + "_en." + audioType, audioType, "en-us");
    }
  }

}
