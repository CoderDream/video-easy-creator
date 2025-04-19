package com.coderdream.util.mstts;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.mstts.demo04.SSMLGenerator;
import com.coderdream.util.proxy.OperatingSystem;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsttsAudioUtil {

  public static void genAudioFileWithIndex(String subscriptionKey,
    String region, String index) {
    String indexedAudioFileName = OperatingSystem.getBaseFolder() + File.separator + "mstts"
      + File.separator + "file" + index + ".wav";
    String indexedSsmlFileName = CdFileUtil.getResourceRealPath() + File.separator + "ssml"
      + File.separator + "ssml" + index + ".xml";

    genAudio(subscriptionKey, region, indexedAudioFileName,
      indexedSsmlFileName);
  }

  private static void genAudio(String subscriptionKey, String region,
    String indexedAudioFileName, String indexedSsmlFileName) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Riff16Khz16BitMonoPcm;
    genAudio(subscriptionKey, region, indexedAudioFileName, indexedSsmlFileName, value);
  }
  private static void genAudio(String subscriptionKey, String region,
    String indexedAudioFileName, String indexedSsmlFileName, SpeechSynthesisOutputFormat value) {
    int retryTimes = 5;
    genAudio(subscriptionKey, region, indexedAudioFileName, indexedSsmlFileName, value, retryTimes);
  }

  private static final long RETRY_DELAY_MS = 2000; // 重试间隔，例如 2000 毫秒

  /**
   * 生成音频文件，包含重试逻辑。
   * 如果文件已存在且有效，则跳过。
   * 如果生成失败，会根据 retryTimes 进行重试。
   *
   * @param subscriptionKey      Azure Speech Key
   * @param region               Azure Speech Region
   * @param indexedAudioFileName 目标音频文件路径
   * @param indexedSsmlFileName  输入的 SSML 文件路径
   * @param value                输出音频格式
   * @param retryTimes           初始重试次数（例如传入 3，表示最多尝试 4 次：首次尝试 + 3 次重试）
   */
  private static void genAudio(String subscriptionKey, String region,
    String indexedAudioFileName, String indexedSsmlFileName,
    SpeechSynthesisOutputFormat value, int retryTimes) { // 使用 int

    long startTime = System.currentTimeMillis();
    boolean generatedSuccessfully = false;

    // 1. 初始状态检查: 如果文件已存在且有效，则直接成功
    //    使用 getFileSize 替代 isFileEmpty，更明确检查有效性
    if (!CdFileUtil.isFileEmpty(indexedAudioFileName)) {
      log.info("音频文件已存在且有效，跳过生成：{}", indexedAudioFileName);
      generatedSuccessfully = true;
    } else {
      log.info("音频文件不存在或无效，开始尝试生成：{}", indexedAudioFileName);
    }

    // 2. 重试循环 (仅在初始检查失败时进入)
    while (!generatedSuccessfully && retryTimes >= 0) {
      int currentAttempt = retryTimes; // 用于日志记录
      log.info("开始生成音频尝试，剩余重试次数: {}", currentAttempt);
      long attemptStartTime = System.currentTimeMillis();
      boolean attemptSuccess = false; // 本次尝试是否成功

      try {
        // 每次尝试都重新配置，确保状态干净 (或者考虑缓存 SpeechConfig/Synthesizer 以优化)
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(subscriptionKey, region);
        speechConfig.setSpeechSynthesisOutputFormat(value);

        // 注意: SpeechSynthesizer 不是 AutoCloseable，需要确保其资源被正确管理
        // 如果长时间运行或大量调用，可能需要更好的 Synthesizer 生命周期管理
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null);
        try {
          String ssml = xmlToString(indexedSsmlFileName); // 假设 xmlToString 是安全的

          try (SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(
            ssml)) { // 确保 result 被关闭
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
              // 使用 try-with-resources 确保 stream 被关闭
              try (AudioDataStream stream = AudioDataStream.fromResult(
                result)) {
                stream.saveToWavFile(indexedAudioFileName);
                // 3. 成功检查：保存后立即检查文件大小
                if (!CdFileUtil.isFileEmpty(indexedAudioFileName)) {
                  log.info("尝试成功: 音频文件生成于 {}", indexedAudioFileName);
                  attemptSuccess = true;
                  generatedSuccessfully = true; // 标记整体成功
                } else {
                  // 文件大小仍为0，视为本次尝试失败
                  log.warn("尝试失败: saveToWavFile 调用后文件大小仍为 0: {}",
                    indexedAudioFileName);
                }
              } // stream 在此自动关闭
            } else if (result.getReason() == ResultReason.Canceled) {
              SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(
                result);
              log.error("尝试失败: 语音合成被取消，原因: {}, 详情: {}",
                cancellation.getReason(), cancellation.getErrorDetails());
            } else {
              log.error("尝试失败: 语音合成失败，原因: {}", result.getReason());
            }
          }
          // 关闭 SpeechSynthesisResult
        } finally {
          // 关闭 Synthesizer?
          // 微软的示例代码通常不显式关闭 Synthesizer，它可能依赖 GC 或特定作用域管理。
          // 如果遇到资源泄露问题，可能需要研究其关闭方式。
          // speechSynthesizer.close(); // 如果有 close 方法的话
        }

      } catch (Exception e) {
        // 捕获任何在尝试过程中发生的未预料异常
        log.error("尝试异常: 生成音频时发生错误 (剩余重试 {} 次): {}", currentAttempt, e.getMessage(), e);
      }

      // 记录本次尝试耗时
      log.info("本次生成尝试耗时: {}", CdTimeUtil.formatDuration(System.currentTimeMillis() - attemptStartTime));

      // 4. 失败处理与延迟
      if (!generatedSuccessfully) { // 如果本次尝试未成功 (包括文件大小为0的情况)
        if (retryTimes > 0) {
          retryTimes--; // 消耗一次重试机会
          log.warn("生成尝试失败，将在 {} ms 后进行下一次重试 (剩余 {} 次)", RETRY_DELAY_MS, retryTimes);
          try {
            Thread.sleep(RETRY_DELAY_MS);
          } catch (InterruptedException ie) {
            log.warn("重试等待被中断，停止重试流程。");
            Thread.currentThread().interrupt();
            retryTimes = -1; // 强制退出循环
          }
        } else {
          // retryTimes 已经是 0 或更少，没有更多重试机会了
          log.error("所有重试次数已用尽，最终生成失败: {}", indexedAudioFileName);
          retryTimes = -1; // 确保循环退出
        }
      }
      // 如果 generatedSuccessfully 为 true，循环将在下次检查时自然退出

    } // 结束 while 循环

    // 7. 最终日志记录
    long elapsedTime = System.currentTimeMillis() - startTime;
    String duration = CdTimeUtil.formatDuration(elapsedTime);
    if (generatedSuccessfully) {
      log.info("音频生成过程完成 (成功)，总耗时：{}；文件名称：{}", duration, indexedAudioFileName);
    } else {
      log.error("音频生成过程完成 (失败)，总耗时：{}；文件名称：{}", duration, indexedAudioFileName);
    }
  }




//  private static void genAudio(String subscriptionKey, String region,
//    String indexedAudioFileName, String indexedSsmlFileName, SpeechSynthesisOutputFormat value, Integer retryTimes) {
//    long startTime = System.currentTimeMillis();
//    if (CdFileUtil.isFileEmpty(indexedAudioFileName)) {
//      SpeechConfig speechConfig = SpeechConfig.fromSubscription(subscriptionKey,
//        region);
//      speechConfig.setSpeechSynthesisOutputFormat(value);
//      SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
//        null);
//      String ssml = xmlToString(indexedSsmlFileName);
//      SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
//      if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
//        AudioDataStream stream = AudioDataStream.fromResult(result);
//        stream.saveToWavFile(indexedAudioFileName);
//        System.out.println(
//          "音频文件生成成功: " + new File(indexedAudioFileName).getAbsolutePath());
//        stream.close();
//      } else if (result.getReason() == ResultReason.Canceled) {
//        SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(
//          result);
//        log.error("语音合成被取消:{}", cancellation.getReason());
//        log.error("错误详情: {}", cancellation.getErrorDetails());
//      } else {
//        log.error("语音合成失败: {}", result.getReason().toString());
//      }
//      result.close();
//
//    } else {
//      log.info("文件已存在，不重复生成：{}", indexedAudioFileName);
//    }
//
//    long elapsedTime = System.currentTimeMillis() - startTime;
//    String duration = CdTimeUtil.formatDuration(elapsedTime);
//    log.info("文本转语音文件生成完成，耗时：{}；文件名称：{}", duration,
//      indexedAudioFileName);
//  }

  public static void genAudioFileWithIndexFileName(String subscriptionKey,
    String region, String audioFileName, String indexFileName) {
    genAudio(subscriptionKey, region, audioFileName, indexFileName);
  }

  public static void genAudioFile(String subscriptionKey,String region,
    String index, SpeechSynthesisOutputFormat value) {

    String fileName = OperatingSystem.getBaseFolder() + File.separator + "mstts"
      + File.separator + "file" + index + ".wav";
    String indexedSsmlFileName = CdFileUtil.getResourceRealPath() + File.separator + "ssml"
      + File.separator + "ssml" + index + ".xml";

    genAudio(subscriptionKey, region, fileName, indexedSsmlFileName, value);
  }

  /**
   * content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
   * "medium", "medium", "medium", cnFile, audioType, "zh-cn");
   * content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
   * "default", "default", "default", enFile, audioType, "en-us");
   *
   * @param textFileName  文本文件
   * @param audioFileName 音频文件
   * @param lang          语言
   */
  public static void genAudioFileWithIndexSsmlFileName(String textFileName,
    String audioFileName, String lang, String indexedSsmlFileName) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    String voiceName = "";
    String subscriptionKey = "";
    String region = switch (lang) {
      case CdConstants.LANG_EN -> {
        voiceName = CdConstants.DEFAULT_VOICE_NAME_EN;
        subscriptionKey = CdConstants.SPEECH_KEY_EAST_US;
        yield CdConstants.SPEECH_REGION_EASTUS;
      }
      case CdConstants.LANG_CN -> {
        voiceName = CdConstants.DEFAULT_VOICE_NAME_CN;
        subscriptionKey = CdConstants.SPEECH_KEY_EASTASIA;
        yield CdConstants.SPEECH_REGION_EASTASIA;
      }
      default -> "";
    };

    List<String> textList = CdFileUtil.readFileContent(textFileName);
    String pitch = "";
    String volume = "";
    String rate = "";
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName, indexedSsmlFileName,
      voiceName, pitch, volume, rate);
  }

  /**
   * content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
   * "medium", "medium", "medium", cnFile, audioType, "zh-cn");
   * content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
   * "default", "default", "default", enFile, audioType, "en-us");
   *
   * @param textList      文本列表
   * @param audioFileName 音频文件
   * @param lang          语言
   */
  public static void genAudioFileWithIndexSsmlFileName(List<String> textList,
    String audioFileName,
    String lang, String indexedSsmlFileName) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    String voiceName = "";
    String subscriptionKey = "";
    String region = switch (lang) {
      case CdConstants.LANG_EN -> {
        voiceName = CdConstants.DEFAULT_VOICE_NAME_EN;
        subscriptionKey = CdConstants.SPEECH_KEY_EAST_US;
        yield CdConstants.SPEECH_REGION_EASTUS;
      }
      case CdConstants.LANG_CN -> {
        voiceName = CdConstants.DEFAULT_VOICE_NAME_CN;
        subscriptionKey = CdConstants.SPEECH_KEY_EASTASIA;
        yield CdConstants.SPEECH_REGION_EASTASIA;
      }
      default -> "";
    };

    String pitch = "";
    String volume = "";
    String rate = "";
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName, indexedSsmlFileName, voiceName, pitch, volume, rate);
  }

  public static void genAudioFileWithIndexSsmlFileName(String textFileName,
    String audioFileName, String indexedSsmlFileName,
    String voiceName, String lang) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    String subscriptionKey = "";
    String region = switch (lang) {
      case "en" -> {
        subscriptionKey = CdConstants.SPEECH_KEY_EAST_US;
        yield CdConstants.SPEECH_REGION_EASTUS;
      }
      case "zh" -> {
        subscriptionKey = CdConstants.SPEECH_KEY_EASTASIA;
        yield CdConstants.SPEECH_REGION_EASTASIA;
      }
      default -> "";
    };

    List<String> textList = CdFileUtil.readFileContent(textFileName);
    String pitch = "";
    String volume = "";
    String rate = "";
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName,indexedSsmlFileName,
      voiceName, pitch, volume, rate);
  }

  public static void genAudioFileWithIndexSsmlFileName(String subscriptionKey,
    String region,
    String textFileName, String audioFileName, String indexedSsmlFileName,
    String voiceName) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    List<String> textList = CdFileUtil.readFileContent(textFileName);
    String pitch = "";
    String volume = "";
    String rate = "";
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName,
      voiceName, indexedSsmlFileName, pitch, volume, rate);
  }

  public static void genAudioFileWithIndexSsmlFileName(String subscriptionKey,
    String region,
    List<String> textList, String audioFileName, String indexedSsmlFileName,
    String voiceName) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    String pitch = "";
    String volume = "";
    String rate = "";
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName, indexedSsmlFileName,
      voiceName, pitch, volume, rate);
  }

  public static void genAudioFileWithIndexSsmlFileName(String subscriptionKey,
    String region,
    List<String> textList, String audioFileName, String indexedSsmlFileName,
    String voiceName, String pitch,
    String volume, String rate) {
    SpeechSynthesisOutputFormat value = SpeechSynthesisOutputFormat.Audio16Khz16Bit32KbpsMonoOpus;
    genAudioFileWithIndexSsmlFileName(subscriptionKey, region, value, textList,
      audioFileName, indexedSsmlFileName,
      voiceName, pitch, volume, rate);
  }

  public static void genAudioFileWithIndexSsmlFileName(String subscriptionKey,
    String region, SpeechSynthesisOutputFormat value, List<String> textList,
    String audioFileName, String indexedSsmlFileName, String voiceName,
    String pitch, String volume, String rate) {
    SpeechConfig speechConfig = SpeechConfig.fromSubscription(subscriptionKey,
      region);
    speechConfig.setSpeechSynthesisOutputFormat(value);
      voiceName =
        StrUtil.isBlankIfStr(voiceName) ? "en-US-JennyNeural" : voiceName;
      pitch = StrUtil.isBlankIfStr(pitch) ? "+0Hz" : pitch;    // 使用具体值
      volume = StrUtil.isBlankIfStr(volume) ? "100" : volume;
      rate = StrUtil.isBlankIfStr(rate) ? "1.0" : rate;
//      String ssmlFileNameTemp =
//        CdFileUtil.getResourceRealPath() + File.separator + "ssml"
//          + File.separator + "ssml_temp.xml";
      String ssmlStr = generateSSML(textList, voiceName, pitch, volume, rate);
      FileUtil.writeUtf8String(ssmlStr, indexedSsmlFileName);
//      FileUtil.writeStringToFile(ssmlFileNameTemp, ssmlStr);
      String ssml = xmlToString(indexedSsmlFileName);
      log.debug("生成的 SSML: {}", ssml); // 调试用

      genAudio(subscriptionKey, region, audioFileName, indexedSsmlFileName);
  }

  public static String xmlToString(String filePath) {
    File file = new File(filePath);
    StringBuilder fileContents = new StringBuilder((int) file.length());

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        fileContents.append(scanner.nextLine())
          .append(System.lineSeparator());
      }
      return fileContents.toString().trim();
    } catch (FileNotFoundException ex) {
      return "File not found.";
    }
  }

  // 生成 SSML (Speech Synthesis Markup Language)
//  public static String generateSSML(List<String> textList, String voiceName,
//    String pitch, String volume, String rate) {
//    StringBuilder ssmlBuilder = new StringBuilder();
//    ssmlBuilder.append("<speak version='1.0' xml:lang='en-US'>");
//    ssmlBuilder.append("<voice name='").append(voiceName).append("'>");
//    ssmlBuilder.append("<prosody pitch='").append(pitch).append("' ")
//      .append("volume='").append(volume).append("' ")
//      .append("rate='").append(rate).append("'>");
//
//    for (String text : textList) {
//      String escapedText = text.replace("&", "&amp;")
//        .replace("<", "&lt;")
//        .replace(">", "&gt;")
//        .replace("\"", "&quot;")
//        .replace("'", "&apos;");
//      // 生成 phoneme，处理多音字
//      escapedText = SSMLGenerator.generatePhoneme(escapedText);
//      ssmlBuilder.append(escapedText).append(" ");
//    }
//
//    ssmlBuilder.append("</prosody>");
//    ssmlBuilder.append("</voice>");
//    ssmlBuilder.append("</speak>");
//    return ssmlBuilder.toString();
//  }


  /**
   * 生成 SSML (Speech Synthesis Markup Language) 优化点:
   * 1. 所有属性值使用双引号。
   * 2. <speak> 标签包含 xmlns 属性。
   * 3. 每个文本片段 (List<String> 中的 String) 都被单独的 <prosody> 标签包裹。
   *
   * @param textList  文本列表，每个元素是一段需要单独设置语速/音调/音量的文本
   * @param voiceName 语音名称 (例如 "zh-CN-XiaochenNeural")
   * @param pitch     音高 (例如 "+0Hz", "medium")
   * @param volume    音量 (例如 "100", "loud")
   * @param rate      语速 (例如 "1.0", "medium")
   * @return 生成的 SSML 字符串
   */
  public static String generateSSML(List<String> textList, String voiceName,
    String pitch, String volume, String rate) {
    StringBuilder ssmlBuilder = new StringBuilder();

    // 1. 使用双引号
    // 2. 添加 xmlns 属性
    ssmlBuilder.append(
      "<speak version=\"1.0\" xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2001/10/synthesis\">\n");

    // 使用双引号
    ssmlBuilder.append("<voice name=\"").append(voiceName).append("\">\n");

    // 3. 为列表中的每个 String 单独包裹 <prosody>
    for (String text : textList) {
      // 先对原始文本进行 XML 转义
//      String escapedText = text.replace("&", "&")
//        .replace("<", "<")
//        .replace(">", ">")
//        .replace("\"", "&quot;")
//        // 单引号转为 ' 或保持原样，取决于目标平台，' 更标准
//        .replace("'", "'");

      String escapedText = text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;");

      // 如果需要，在此处调用 phoneme 生成器
      // 注意：确保 SSMLGenerator.generatePhoneme 返回的内容适合放在<prosody>标签内部
      // 如果它返回包含 <speak> 或 <voice> 的完整 SSML，则不能这样嵌套
      escapedText = SSMLGenerator.generatePhoneme(escapedText);

      // 为当前文本片段添加 <prosody> 标签，使用双引号
      ssmlBuilder.append("<prosody pitch=\"").append(pitch).append("\" ")
        .append("volume=\"").append(volume).append("\" ")
        .append("rate=\"").append(rate).append("\">\n");

      // 添加处理后的文本
      ssmlBuilder.append(escapedText);
      // 添加处理后的文本
      ssmlBuilder.append("\n");

      // 关闭当前文本片段的 <prosody> 标签
      ssmlBuilder.append("</prosody>\n");

      // 可选：在每个 prosody 块后添加一个空格，以模拟自然的停顿或分隔
      // 如果不需要，可以移除下面这行
      ssmlBuilder.append(" ");
    }

    // 关闭 <voice> 和 <speak> 标签
    ssmlBuilder.append("</voice>\n");
    ssmlBuilder.append("</speak>\n");

    // 返回最终的 SSML 字符串，去除可能因最后一个空格产生的尾随空格
    return ssmlBuilder.toString().trim();
  }
}


