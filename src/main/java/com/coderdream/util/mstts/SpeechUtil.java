package com.coderdream.util.mstts;

import com.coderdream.util.CdConstants;
import com.coderdream.util.chatgpt.TextParserUtil;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceVO;
import com.microsoft.cognitiveservices.speech.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class SpeechUtil {

  // 最大重试次数，作为常量
  private static final int MAX_RETRIES = 10;
  private static final String RETRY_LOG_PATH = "retry_log.txt";  // 重试日志文件路径
  private static final String FAILURE_LOG_PATH = "failure_log.txt";  // 失败日志文件路径

  private static final ExecutorService executorService = new ThreadPoolExecutor(
    16,  // 核心线程数
    64, // 最大线程数
    60L, TimeUnit.SECONDS, // 空闲线程存活时间
    new LinkedBlockingQueue<>(), // 阻塞队列
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
  );

  public static void content2Audio(List<String> textList, String voiceName,
                                    String pitch, String volume, String rate, String fileName,
                                    String audioType, String lang) {
    executorService.submit(() -> {
      int retryCount = 0;
      boolean success = false;

      while (retryCount < MAX_RETRIES && !success) {
        try {
          String speechKey = null;
          SpeechConfig speechConfig = null;

          switch (lang.toLowerCase()) {
            case "zh-cn" -> {
              speechKey = CdConstants.SPEECH_KEY;
              speechConfig = SpeechConfig.fromSubscription(speechKey, CdConstants.SPEECH_REGION_EASTASIA);
            }
            case "en-us" -> {
              speechKey = CdConstants.SPEECH_KEY_EAST_US;
              speechConfig = SpeechConfig.fromSubscription(speechKey, CdConstants.SPEECH_REGION_EASTUS);
            }
            default -> throw new IllegalArgumentException("不支持的语言代码: " + lang);
          }

          switch (audioType.toLowerCase()) {
            case "wav":
              speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);
              break;
            case "mp3":
              speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
              break;
            default:
              throw new IllegalArgumentException("不支持的音频格式: " + audioType);
          }

          try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null)) {
            String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch, volume, rate);
            SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
              AudioDataStream stream = AudioDataStream.fromResult(result);
              stream.saveToWavFile(fileName);
              log.info("音频文件生成成功: {}", fileName);
              success = true;
            } else {
              log.error("语音合成失败: {}", result.getReason());
              retryCount++;
              logRetryInfo(fileName, retryCount);  // 记录重试日志
            }
          }
        } catch (Exception e) {
          log.error("语音合成出错: {}", e.getMessage(), e);
          retryCount++;
          logRetryInfo(fileName, retryCount);  // 记录重试日志
        }

        // 如果失败次数超过最大重试次数，记录失败信息
        if (!success && retryCount >= MAX_RETRIES) {
          logFailureInfo(fileName);
        }

        if (!success) {
          try {
            Thread.sleep(1000); // 重试前的延迟（可以调整）
          } catch (InterruptedException interruptedException) {
            log.error("线程休眠被中断：{}", interruptedException.getMessage());
          }
        }
      }
    });
  }

  public static void genDialog2Audio(String folderName, String fileName,
                                      String audioType) {
    String fullFileName = folderName + File.separator + fileName + ".txt";
    log.info("开始解析文件: {}", fullFileName);

    String outputDirCn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator + CdConstants.LANG_CN;
    String outputDirEn =
      folderName + File.separator + fileName + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator + CdConstants.LANG_EN;
    File dirCn = new File(outputDirCn);
    if (!dirCn.exists() && dirCn.mkdirs()) {
      log.info("目录创建成功: {}", dirCn.getAbsolutePath());
    }
    File dirEn = new File(outputDirEn);
    if (!dirEn.exists() && dirEn.mkdirs()) {
      log.info("目录创建成功: {}", dirEn.getAbsolutePath());
    }

    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(fullFileName);
    int number = 0;
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      String cnFile =
        outputDirCn + File.separator + fileName + "_" + MessageFormat.format("{0,number,000}", number) + "_cn." + audioType;
      String enFile =
        outputDirEn + File.separator + fileName + "_" + MessageFormat.format("{0,number,000}", number) + "_en." + audioType;

      content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
        "medium", "medium", "medium", cnFile, audioType, "zh-cn");
      content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
        "default", "default", "default", enFile, audioType, "en-us");
    }
  }

  public static void genDialog2Audio(String fileName, String audioType) {
    genDialog2Audio(CdConstants.RESOURCES_BASE_PATH, fileName, audioType);
  }

  public static void shutdownExecutor() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  // 记录重试日志
  private static void logRetryInfo(String fileName, int retryCount) {
    try {
      String logMessage = String.format("文件：%s，重试次数：%d", fileName, retryCount);


      writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName) + RETRY_LOG_PATH, logMessage);
    } catch (IOException e) {
      log.error("记录重试日志时出错", e);
    }
  }

  // 记录失败日志
  private static void logFailureInfo(String fileName) {
    try {
      String logMessage = String.format("文件：%s，创建音频失败", fileName);
      writeLogToFile(BatchCreateVideoCommonUtil.getVideoPath(fileName) + FAILURE_LOG_PATH, logMessage);
    } catch (IOException e) {
      log.error("记录失败日志时出错", e);
    }
  }

  // 将日志信息写入文件
  private static void writeLogToFile(String logFilePath, String message) throws IOException {
    File logFile = new File(logFilePath);
    if (!logFile.exists()) {
      logFile.getParentFile().mkdirs();  // 创建日志文件的父目录
      logFile.createNewFile();  // 创建文件
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
      writer.write(message);
      writer.newLine();
    }
  }
}
