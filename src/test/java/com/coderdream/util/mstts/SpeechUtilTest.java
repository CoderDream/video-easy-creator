package com.coderdream.util.mstts;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class SpeechUtilTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

//  @Test
//  void content2mp3() {
//    SpeechUtil2.content2mp3();
//  }
//
//  @Test
//  void testContent2mp3() {
//    //
//    List<String> textList = List.of("你好", "欢迎使用微软的文本转语音服务！");
//    String fileName = "test.mp3";
//    SpeechUtil2.content2mp3(textList, fileName);
//  }
//
//  /**
//   * 批量生成mp3
//   */
//  @Test
//  void testContent2mp301() {
//    //
//    List<String> textList = List.of("你好", "欢迎使用微软的文本转语音服务！");
//    String fileName = "test.mp3";
//    SpeechUtil.content2mp3(textList, fileName);
//  }

  @Test
  void testContent2mp31() {
  }

  @Test
  void testContent2mp32() {
  }

//  @Test
//  void testGenDialog2Mp3_01() {
//    String folderName = "src/main/resources";
//    String fileName = "CampingInvitation";
//    SpeechUtil.genDialog2Mp3(folderName, fileName);
//  }

  @Test
  void testGenDialog2Audio_01() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "src/main/resources";
    String fileName = "CampingInvitation";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio(folderName, fileName, audioType);
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void testGenDialog2Audio_02() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "src/main/resources";
    String fileName = "CampingInvitation_02";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio(folderName, fileName, audioType);
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void testGenDialog2Audio_03() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "src/main/resources";
    String fileName = "CampingInvitation_cht";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio(folderName, fileName, audioType);
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void testGenDialog2Audio_04() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "src/main/resources";
    String fileName = "CampingInvitation_cht";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio(folderName, fileName, audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  // TODO: 2024-05-08 23:39:18 测试生成wav文件

  @Test
  void testContent2Audio_01() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "D:\\0000\\EnBook001\\900\\ch01\\";
    String fileName = "dialog_single_with_phonetics";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio900(folderName, "ch01", fileName, audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void testContent2Audio_02() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "D:\\0000\\EnBook001\\900\\ch002\\";
    String fileName = "ch002_total_phonetics";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio900(folderName, "ch002", fileName, audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void genDialog2CnAudio_03() {
    long startTime = System.currentTimeMillis(); // 开始时间
    String folderName = "D:\\0000\\EnBook001\\900\\ch01\\";
    String fileName = "dialog_single_cn";
    String audioType = "wav";
    SpeechUtil.genDialog2CnAudio(folderName, fileName, audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("中文音频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  @Test
  void content2Audio() {
    // D:\0000\EnBook011\Chapter001

    String bookName = "EnBook011";
    String bookPath =
      OperatingSystem.getBaseFolder() + File.separator + bookName;
//      String folderName = ;
    String audioType = "wav";
    String chapterName = "Chapter001";
    String chapterPathName = bookPath + File.separator + chapterName;
//    String audioName = "en-US-AndrewMultilingualNeural";
    String audioName = "en-US-JennyNeural";
    String lang = "en-us";
    Integer groupSize = 1;
    SpeechUtil07.genDialog2Audio(chapterPathName, chapterName, audioName, audioType, lang,
      groupSize);
  }

//  @Test
//  void testContent2wav2_01() {
//     List<String> textList = List.of("你好，这是一个语音合成测试！");
//    String fileName = "output.wav";
//
//    // 调用生成 WAV 文件
//    SpeechUtil.content2wav(textList, "zh-CN-XiaoxiaoNeural", "medium", "medium", "medium", fileName);
//  }

  @Test
  void genDialog2CnAudio_01() {

//    // 构建中文音频文件名
//    String cnFile =
//      outputDirCn + File.separator + fileName + "_" + MessageFormat.format(
//        "{0,number,000}", number) + "_cn." + audioType;
//    // 构建英文音频文件名
//    String enFile =
//      outputDirEn + File.separator + fileName + "_" + MessageFormat.format(
//        "{0,number,000}", number) + "_en." + audioType;
//
//    // 调用 content2Audio 生成中文和英文音频
//    content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural",
//      "medium", "medium", "medium", cnFile, audioType, "zh-cn");
//    content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural",
//      "default", "default", "default", enFile, audioType, "en-us");
  }

}
