package com.coderdream.util.subtitle;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.SubtitleEntity;
import com.coderdream.util.audio.FfmpegUtil2;
import com.coderdream.util.bbc.StringSplitter4;
import com.coderdream.util.bbc.TranslateUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.daily.DailyUtil;
import com.coderdream.util.sentence.demo03.SentenceMerger;
import com.coderdream.util.sentence.demo03.StanfordNLPSentenceSplitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenSubtitleUtil {

  public static void process(String filePath, int maxLength) {

    String inputPathMp3 = CdFileUtil.changeExtension(filePath, "mp3");
    if (CdFileUtil.isFileEmpty(inputPathMp3)) {
      // 获取 mp4 文件路径信息
      String inputPathMp4 = CdFileUtil.changeExtension(filePath, "mp4");
      // 提取音频文件
      FfmpegUtil2.extractAudioFromMp4(inputPathMp4, inputPathMp3);
    }
    //
    filePath = CdFileUtil.changeExtension(filePath, "txt");
    if (CdFileUtil.isFileEmpty(filePath)) {
      log.info("filePath 文件不存在，请先生成： {}", filePath);
      return;
    }

    String sentenceFilePath = CdFileUtil.addPostfixToFileName(filePath,
      "_sentence");
    if (CdFileUtil.isFileEmpty(sentenceFilePath)) {
      genRawSrtFile(filePath, sentenceFilePath, maxLength);
    }

    // 生成英文SRT文件
    String srcFileNameEn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEn = CdFileUtil.addPostfixToFileName(srcFileNameEn,
      "." + CdConstants.SUBTITLE_EN);
    if (CdFileUtil.isFileEmpty(srcFileNameEn)) {
      log.info("srcFileNameEng 文件不存在, {}", srcFileNameEn);
      SubtitleUtil.genSrtByExecuteCommand(inputPathMp3, sentenceFilePath,
        srcFileNameEn, "eng");
    }
    //  生成中文SRT文件
    String srcFileNameZhCn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameZhCn = CdFileUtil.addPostfixToFileName(srcFileNameZhCn,
      "." + CdConstants.SUBTITLE_ZH_CN);
    String srcFileNameZhTw = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameZhTw = CdFileUtil.addPostfixToFileName(srcFileNameZhTw,
      "." + CdConstants.SUBTITLE_ZH_TW);
    int retryTime = 0;
    while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
      srcFileNameZhCn)) && retryTime < 10) {
      TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
        srcFileNameZhTw);
      retryTime++;
      log.info("重试次数: {}", retryTime);
    }
    if (!CdFileUtil.isFileEmpty(srcFileNameZhCn)) {
      log.info("srcFileNameChn 文件已创建: {}", srcFileNameZhCn);
    } else {
      log.warn("重试 10 次后，文件仍为空: {}", srcFileNameZhCn);
    }

    //  生成中文SRT文件  public static final String SUBTITLE_EN_ZH_CN = "en-zh-CN";
    //  public static final String SUBTITLE_EN_ZH_TW = "en-zh-TW";
    String srcFileNameEnZhCn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEnZhCn = CdFileUtil.addPostfixToFileName(srcFileNameEnZhCn,
      "." + CdConstants.SUBTITLE_EN_ZH_CN);
    String srcFileNameEnZhTw = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEnZhTw = CdFileUtil.addPostfixToFileName(srcFileNameEnZhTw,
      "." + CdConstants.SUBTITLE_EN_ZH_TW);
    if (CdFileUtil.isFileEmpty(srcFileNameEnZhCn) || CdFileUtil.isFileEmpty(
      srcFileNameEnZhTw)) {
      SubtitleUtil.mergeSubtitleFile(srcFileNameEn, srcFileNameZhCn,
        srcFileNameEnZhCn);
      SubtitleUtil.mergeSubtitleFile(srcFileNameEn, srcFileNameZhCn,
        srcFileNameEnZhTw);
    } else {
      log.info("srtFilePath 文件已存在: {} {}", srcFileNameEnZhCn,
        srcFileNameEnZhTw);
    }

    // 生成Markdown文件
    String mdFileName = Objects.requireNonNull(
      CdFileUtil.changeExtension(filePath, "md"));
    String chnMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_chn");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_cht");

    if (CdFileUtil.isFileEmpty(chnMdFileName) || CdFileUtil.isFileEmpty(
      chtMdFileName)) {
      DailyUtil.generateDescription(srcFileNameEnZhCn, chnMdFileName,
        chtMdFileName);
    } else {
      log.info("描述文件已存在: {}， {}", chnMdFileName, chtMdFileName);
    }
  }

  public static void processSrtAndGenDescription(String filePath) {
    // 生成英文SRT文件
    String srcFileNameEn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEn = CdFileUtil.addPostfixToFileName(srcFileNameEn,
      "." + CdConstants.SUBTITLE_EN);

    // 过滤内容文件
    GenSubtitleUtil.filterContentFile(srcFileNameEn, srcFileNameEn);

    //  生成中文SRT文件，通过谷歌服务翻译
    String srcFileNameZhCn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameZhCn = CdFileUtil.addPostfixToFileName(srcFileNameZhCn,
      "." + CdConstants.SUBTITLE_ZH_CN);
    String srcFileNameZhTw = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameZhTw = CdFileUtil.addPostfixToFileName(srcFileNameZhTw,
      "." + CdConstants.SUBTITLE_ZH_TW);

    //  通过微软服务翻译
    int retryTime = 0;
    while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
      srcFileNameZhCn)) && retryTime < 10) {
      if (retryTime > 0) {
        log.info(CdConstants.TRANSLATE_PLATFORM_MSTTS + " 重试次数: {}",
          retryTime);
      }
      TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
        srcFileNameZhTw,
        CdConstants.TRANSLATE_PLATFORM_MSTTS);
      retryTime++;
      ThreadUtil.sleep(3000L);
    }
    if (!CdFileUtil.isFileEmpty(srcFileNameZhCn) && !CdFileUtil.isFileEmpty(
      srcFileNameZhTw)) {
      log.info("chnSrcFileName 文件已创建: {} {}", srcFileNameZhCn,
        srcFileNameZhTw);
    } else {
      log.warn("重试 10 次后，文件仍为空: {} {}", srcFileNameZhCn,
        srcFileNameZhTw);
      return;
    }

    retryTime = 0;
    while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
      srcFileNameZhCn)) && retryTime < 10) {
      if (retryTime > 0) {
        log.info(CdConstants.TRANSLATE_PLATFORM_GEMINI + " 重试次数: {}",
          retryTime);
      }
      TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
        srcFileNameZhTw,
        CdConstants.TRANSLATE_PLATFORM_GEMINI);
      retryTime++;
    }
    if (!CdFileUtil.isFileEmpty(srcFileNameZhCn)) {
      log.info("srcFileNameZhCn 文件已创建: {}", srcFileNameZhCn);
      log.info(CdConstants.TRANSLATE_PLATFORM_GEMINI + " 重试次数: {}",
        retryTime);
    } else {
      log.warn("重试 10 次后，文件仍为空: {}", srcFileNameZhCn);
    }

    //  生成中文SRT文件
//    String srtFilePath = CdFileUtil.changeExtension(srcFilePath, "srt");
    //  生成中文SRT文件  public static final String SUBTITLE_EN_ZH_CN = "en-zh-CN";
    //  public static final String SUBTITLE_EN_ZH_TW = "en-zh-TW";
    String srcFileNameEnZhCn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEnZhCn = CdFileUtil.addPostfixToFileName(srcFileNameEnZhCn,
      "." + CdConstants.SUBTITLE_EN_ZH_CN);
    String srcFileNameEnZhTw = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEnZhTw = CdFileUtil.addPostfixToFileName(srcFileNameEnZhTw,
      "." + CdConstants.SUBTITLE_EN_ZH_TW);
    if (CdFileUtil.isFileEmpty(srcFileNameEnZhCn) || CdFileUtil.isFileEmpty(
      srcFileNameEnZhTw)) {
      SubtitleUtil.mergeSubtitleFile(srcFileNameEn, srcFileNameZhCn,
        srcFileNameEnZhCn);
      SubtitleUtil.mergeSubtitleFile(srcFileNameEn, srcFileNameZhCn,
        srcFileNameEnZhTw);
    } else {
      log.info("srtFilePath 文件已存在: {} {}", srcFileNameEnZhCn,
        srcFileNameEnZhTw);
    }

    // 看看翻译质量 TODO


    // 生成Markdown文件
    String mdFileName = Objects.requireNonNull(
      CdFileUtil.changeExtension(filePath, "md"));
    String chnMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_chn");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_cht");

    if (CdFileUtil.isFileEmpty(chnMdFileName) || CdFileUtil.isFileEmpty(
      chtMdFileName)) {
      DailyUtil.generateDescription(srcFileNameEnZhCn, chnMdFileName,
        chtMdFileName);
    } else {
      log.info("描述文件已存在: {}， {}", chnMdFileName, chtMdFileName);
    }
  }

  /**
   * 生成原始的SRT文件
   *
   * @param filePath         文件路径
   * @param sentenceFilePath 句子文件路径
   * @param maxLength        最大长度
   */
  public static void genRawSrtFile(String filePath, String sentenceFilePath,
    int maxLength) {
    // step 1: read file，生成句子
    List<String> stringLists = CdFileUtil.readFileContent(filePath);

    List<String> sentenceList;
    assert stringLists != null;
    StringBuilder stringBuilder = new StringBuilder();
    for (String string : stringLists) {
      log.info(string);
      // 预处理
      // 1.先去掉双引号 ” “ (Laughs.)  (Laughter.)
      string = string.replace("\"", "");
      string = string.replace("”", "");
      string = string.replace("“", "");
      string = string.replace("(Laughs.)", "");
      string = string.replace("(Laughter.)", "");
      stringBuilder.append(string).append(" ");
    }

    // 1.1 使用JDK自带的方法
//    sentenceList= SentenceSplitterWithJDK.splitIntoSentences(stringBuilder.toString());

    // 1.2 使用StanfordCoreNLP
    sentenceList = StanfordNLPSentenceSplitter.splitIntoSentences(
      stringBuilder.toString());

    // 1.3 处理长句子 SentenceSplitterWithChar.processLongSentence(text, ";", ":")
//    sentenceList = sentenceList.stream()
//      .map(sentence -> SentenceSplitterWithChar03.processLongSentence(sentence,
//        ";", ":"))
//      .flatMap(List::stream)
//      .toList();

    // 分割句子
    sentenceList = StringSplitter4.splitStringList(sentenceList, maxLength);

    // 合并句子
    sentenceList = SentenceMerger.mergeSentences(sentenceList, maxLength);

    // 移除空行
    sentenceList = sentenceList.stream().filter(s -> !s.isEmpty()).toList();

    CdFileUtil.writeToFile(sentenceFilePath, sentenceList);
  }

  /**
   * 生成原始的SRT文件
   *
   * @param filePath 文件路径
   */
  public static void processRawSrtFile(String filePath, String newFilePath) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<String> newSrtStringList = new ArrayList<>();
    List<SubtitleEntity> enSubtitleEntityList = new ArrayList<>();
    List<String> contentList = FileUtil.readLines(filePath, "UTF-8");
    List<List<String>> lists = CdStringUtil.splitByEmptyLine(contentList);
    for (List<String> list : lists) {
      if (CollectionUtil.isNotEmpty(list)) {
        int size = list.size();
        String index = "";
        String timeStr = "";
        //第一字幕内容
        String subtitle = "";

        //第二字幕内容
        String subtitleSecond = "";
        switch (size) {
          case 1:
          case 2:
            log.warn("字幕行数不对（不到2行）: {}", size);
            break;
          case 3:
            index = list.get(0);
            timeStr = list.get(1);
            subtitle = list.get(2);
            break;
          case 4:
            index = list.get(0);
            timeStr = list.get(1);
            subtitle = list.get(2);
            subtitleSecond = list.get(3);
            break;
          default:
            log.warn("字幕行数不对（超过4行）: {}", size);
            break;
        }

        if (StrUtil.isNotBlank(index) && StrUtil.isNotBlank(timeStr)
          && StrUtil.isNotBlank(subtitle)) {
          SubtitleEntity subtitleEntity = new SubtitleEntity();
          subtitleEntity.setSubIndex(Integer.valueOf(index));
          subtitleEntity.setTimeStr(timeStr);
          subtitleEntity.setSubtitle(subtitle);
          newSrtStringList.add(index);
          newSrtStringList.add(timeStr);
          if (StrUtil.isNotBlank(subtitleSecond)) {
            subtitleEntity.setSecondSubtitle(subtitleSecond);
            String mergeContent = subtitle + " " + subtitleSecond;
            newSrtStringList.add(mergeContent);
          } else {
            newSrtStringList.add(subtitle);
          }
          newSrtStringList.add("");
          enSubtitleEntityList.add(subtitleEntity);
        }
      }
    }

    if (CollectionUtil.isNotEmpty(newSrtStringList)) {
      // 写入文件
      CdFileUtil.writeToFile(newFilePath, newSrtStringList);
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时
      log.info("写入完成，文件路径: {}，共计耗时：{}", newFilePath,
        CdTimeUtil.formatDuration(elapsedTime));
    } else {
      System.out.println("newList is empty!");
    }
  }


  /**
   * 生成原始的SRT文件
   *
   * @param filePath 文件路径
   */
  public static void filterContentFile(String filePath, String newFilePath) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<String> newSrtStringList = new ArrayList<>();
    List<String> contentList = FileUtil.readLines(filePath, "UTF-8");
    List<List<String>> lists = CdStringUtil.splitByEmptyLine(contentList);
    int count = 0; // 记录过滤次数 如果过滤一次，就少一次
    for (List<String> list : lists) {
      if (CollectionUtil.isNotEmpty(list)) {
        int size = list.size();
        String index = "";
        String timeStr = "";
        //第一字幕内容
        String subtitle = "";

        //第二字幕内容
        String subtitleSecond = "";
        switch (size) {
          case 1:
          case 2:
            log.warn("字幕行数不对（不到2行）: {}", size);
            break;
          case 3:
            index = list.get(0);
            timeStr = list.get(1);
            subtitle = list.get(2);
            break;
          case 4:
            index = list.get(0);
            timeStr = list.get(1);
            subtitle = list.get(2);
            subtitleSecond = list.get(3);
            break;
          default:
            log.warn("字幕行数不对（超过4行）: {}", size);
            break;
        }

        // 合并字幕内容
        String mergeContent;
        if (StrUtil.isNotBlank(subtitleSecond)) {
          mergeContent = subtitle + " " + subtitleSecond;
        } else {
          mergeContent = subtitle;
        }

        // 如果内容有效，则添加到字符串列表中
        if (StrUtil.isNotBlank(index) && StrUtil.isNotBlank(timeStr)
          && StrUtil.isNotBlank(mergeContent) && validContent(mergeContent)) {
          int indexInt = Integer.parseInt(index);
          indexInt = indexInt - count;
          newSrtStringList.add(indexInt + "");
          newSrtStringList.add(timeStr);
          if (StrUtil.isNotBlank(subtitleSecond)) {
            newSrtStringList.add(GenSubtitleUtil.filterContent(mergeContent));
          } else {
            newSrtStringList.add(GenSubtitleUtil.filterContent(subtitle));
          }
          newSrtStringList.add("");
        } else {
          count++;
          log.warn("无效内容，过滤一次: {}", mergeContent);
        }
      }
    }

    if (CollectionUtil.isNotEmpty(newSrtStringList)) {
      // 写入文件
      CdFileUtil.writeToFile(newFilePath, newSrtStringList);
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时
      log.info("写入完成，文件路径: {}，共计耗时：{}", newFilePath,
        CdTimeUtil.formatDuration(elapsedTime));
    } else {
      log.error("newSrtStringList is empty!");
    }
  }

  public static String filterContent(String content) {
    if (StrUtil.isBlank(content)) {
      return "";
    }
    // 以【i 】，替换为大写【I 】
    if (content.startsWith("i ")) {
      log.warn("替换为大写: {}", content);
      content = "I " + content.substring(2);
    }

    // 以【i 】，替换为大写【I 】
    if (content.startsWith("i'm ")) {
      log.warn("替换为大写: {}", content);
      content = "I'm " + content.substring(2);
    }

    // 以【i 】，替换为大写【I 】
    if (content.contains(" i ")) {
      log.warn("替换为大写: {}", content);
      content = content.replace(" i ", " I ");
    }

    // 以【i 】，替换为大写【I 】
    if (content.contains(" i'm ")) {
      log.warn("替换为大写: {}", content);
      content = content.replace(" i ", " I'm ");
    }

    // 以【i 】，替换为大写【I 】
    if (content.contains(" i'll ")) {
      log.warn("替换为大写: {}", content);
      content = content.replace(" i ", " I'll ");
    }

    return content;
  }

  public static boolean validContent(String content) {
    if (StrUtil.isBlank(content)) {
      return false;
    }

    // 如果只是包含【♪】，则认为是无效内容
    if (content.trim().equals("♪")) {
      return false;
    }

    return true;
  }

}
