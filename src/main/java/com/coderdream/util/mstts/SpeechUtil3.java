package com.coderdream.util.mstts;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.vo.SentenceVO;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpeechUtil3 {

  /**
   * 将文本内容转换为音频文件，支持 WAV 和 MP3 格式。
   *
   * @param textList  文本内容列表
   * @param voiceName 语音名称
   * @param pitch     语音音高
   * @param volume    语音音量
   * @param rate      语音语速
   * @param fileName  音频文件名
   * @param audioType 音频格式（WAV 或 MP3）
   * @param lang      语音语言代码（例如：zh-CN 表示中文）
   */
  public static void content2Audio(List<String> textList, String voiceName,
    String pitch, String volume, String rate, String fileName,
    String audioType, String lang) {
    // 获取语音密钥和区域配置
    String speechKey = null;
    SpeechConfig speechConfig = null;

    switch (lang.toLowerCase()) {
      case "zh-cn" -> {
        speechKey = CdConstants.SPEECH_KEY_EASTASIA;
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

    // 初始化语音合成器
    try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(
      speechConfig, null)) {

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
   * /** 生成对话音频
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

    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
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
