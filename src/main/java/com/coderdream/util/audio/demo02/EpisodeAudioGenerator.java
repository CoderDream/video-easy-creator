package com.coderdream.util.audio.demo02;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.mstts.GenDualAudioUtil;
import com.coderdream.util.process.GenAudioUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class EpisodeAudioGenerator {

  //    private static final String BASE_OUTPUT_PATH = "output/";
  private static final String AUDIO_TYPE = "wav";
  private static final String[] STAGES = {"warmup", "intermediate", "challenge",
    "review"};
  private static final int[] STAGE_SIZES = {20, 30, 20, 10}; // 每段句数

  public static void generateEpisodeAudio(String folderPath, String episodeName,
    String inputFilePath) {
    long startTime = System.currentTimeMillis();
    // 1. 读取并解析输入文件
    List<SentenceVO> sentences = SentenceParser.parseSentencesFromFile(
      inputFilePath);
    if (sentences.size() != 80) {
      log.error("Expected 80 sentences, but got: {}", sentences.size());
      return;
    }

    // 2. 创建输出目录
    String episodeDir =
      folderPath + File.separator + episodeName + File.separator + "audio" + File.separator;
    createDirectory(episodeDir + "cn", "Chinese audio");
    createDirectory(episodeDir + "en", "English audio");

    // 3. 分段生成音频
    int sentenceIndex = 0;
    for (int i = 0; i < STAGES.length; i++) {
      String stage = STAGES[i];
      int stageSize = STAGE_SIZES[i];
      List<SentenceVO> stageSentences = sentences.subList(sentenceIndex,
        sentenceIndex + stageSize);
      generateStageAudio(episodeName, stage, stageSentences, episodeDir);
      sentenceIndex += stageSize;
    }

//    log.info("Episode {} audio generation completed.", episodeName);
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("{} 文件夹音频创建成功，耗时: {}", episodeName,
      CdTimeUtil.formatDuration(durationMillis));
  }

  private static void generateStageAudio(String episodeName, String stage,
    List<SentenceVO> sentences, String episodeDir) {
    String rate = getRateForStage(stage); // 根据阶段调整语速
    int number = getStartNumber(stage);   // 获取起始编号

    for (SentenceVO sentence : sentences) {
      String cnFile = String.format("%scn" + File.separator + "%s_%03d_cn.%s",
        episodeDir,
        episodeName, number, AUDIO_TYPE);
      String enFile = String.format("%sen" + File.separator + "%s_%03d_en.%s",
        episodeDir,
        episodeName, number, AUDIO_TYPE);

      // 生成中文音频
      if (!new File(cnFile).exists()) {
        GenDualAudioUtil.content2Audio(
          List.of(sentence.getChinese()), "zh-CN-XiaochenNeural",
          "medium", "medium", rate, cnFile, AUDIO_TYPE, "zh-cn");
      } else {
        log.warn("Skipping existing file: {}", cnFile);
      }

      // 生成英文音频
      if (!new File(enFile).exists()) {
        GenDualAudioUtil.content2Audio(
          List.of(sentence.getEnglish()), "en-US-JennyNeural",
          "default", "default", rate, enFile, AUDIO_TYPE, "en-us");
      } else {
        log.warn("Skipping existing file: {}", enFile);
      }

      number++;
    }
  }

  private static String getRateForStage(String stage) {
    return switch (stage) {
      case "warmup" -> "slow";       // 热身：慢速
      case "challenge" -> "fast";    // 挑战：稍快
      default -> "medium";           // 进阶和复习：中等
    };
  }

  private static int getStartNumber(String stage) {
    return switch (stage) {
      case "warmup" -> 1;
      case "intermediate" -> 21;
      case "challenge" -> 51;
      case "review" -> 71;
      default -> throw new IllegalArgumentException("Unknown stage: " + stage);
    };
  }

  private static void createDirectory(String path, String type) {
    File dir = new File(path);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("{} directory created: {}", type, dir.getAbsolutePath());
    }
  }

  public static void main(String[] args) {
    String bookName = "EnBook006";
    String folderPath = OperatingSystem.getFolderPath(bookName);
//    String subFolder = "Chapter003";

    List<String> subFolders = new ArrayList<>();
    int end = 11; // 假定总共50章 51
    for (int i = 4; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      String inputFilePath =
        folderPath + File.separator + subFolder + File.separator + subFolder + ".txt";
      generateEpisodeAudio(folderPath, subFolder, inputFilePath);
    }
  }
}
