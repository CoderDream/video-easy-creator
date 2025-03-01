package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.SubtitleEntity;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.SubtitleUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;

@Slf4j
public class PreparePublishUtil {

  /**
   * 商务英语900句
   */

//  public static final String B_E_900_CHAPTER_NAME = "900_cht_name.txt";

//  public static void process(String folderPath, String subFolder) {
//    // 1. 生成字幕
//
////    String path = "D:/0000/Book02/";
////    String pureName = "Boo02_v2";
////    String mp3FileName = path + pureName + ".mp3";
////    String subtitleFileName = path + pureName + "_srt.txt";
//
//    String lang = "cmn";
//
//    Map<String, String> chapterNameMap = new HashMap<>();
//    List<String> stringList = FileUtil.readLines(B_E_900_CHAPTER_NAME,
//      StandardCharsets.UTF_8);
//    for (String line : stringList) {
//      String[] split = line.split(" ");
//      chapterNameMap.put(split[1], split[2]);
//    }
//    String shortSubFolder = subFolder.substring(3);
//    String chapterName = chapterNameMap.get(shortSubFolder);
//
//    String mp3FileName =
//      "D:\\0000\\商務英語-EP-" + shortSubFolder + "-" + chapterName
//        + "\\商務英語-EP-" + shortSubFolder + "-" + chapterName + ".MP3";
//    File mp3File = new File(mp3FileName);
//    if (!mp3File.exists() || mp3File.length() == 0) {
//      log.info("mp3文件不存在, {}", mp3FileName);
//      return;
//    }
//    String srtFileName = CdFileUtil.changeExtension(mp3FileName, "srt");
//    // D:\0000\EnBook001\900\ch003\ch003_total.txt
//    String subtitleFileName =
//      folderPath + subFolder + File.separator + subFolder +
//        "_total.txt";
//    File totalFile = new File(subtitleFileName);
//    if (!totalFile.exists() || totalFile.length() == 0) {
//      log.info("subtitleFileName文件不存在, {}", subtitleFileName);
//      return;
//    }
//
//    List<String> textList = new ArrayList<>(List.of(
//      "Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.",
//      "英文加中文配音，每次半小時，增强你的英文听力。"));
//    List<String> srtList = FileUtil.readLines(subtitleFileName,
//      StandardCharsets.UTF_8);
//    for (String srtLine : srtList) {
//      textList.add(ZhConverterUtil.toTraditional(srtLine));
//    }
////    textList.addAll(srtList);
//    String newSubtitleFileName = CdFileUtil.addPostfixToFileName(
//      subtitleFileName,
//      "_cht");// "D:\\0000\\EnBook001\\900\\ch003\\ch003_total_new.txt";
//    CdFileUtil.writeToFile(newSubtitleFileName, textList);
//

  /// /    String srtFileName = CdFileUtil.changeExtension(newSubtitleFileName,
  /// "srt") ;// "D:\\0000\\EnBook001\\900\\ch003\\ch003_total.srt";
//    lang = "eng";
//
//    File srtFile = new File(srtFileName);
//    if (!srtFile.exists() || srtFile.length() == 0) {
//      log.info("srt文件不存在, {}", srtFileName);
//      SubtitleUtil.genSrtByExecuteCommand(mp3FileName, newSubtitleFileName,
//        srtFileName, lang);
//    }
//
//    // 2. 生成描述
//
//    log.info("----- 4.测试 generateContent 方法开始");
//    String prompt = FileUtil.readString(
//      CdFileUtil.getResourceRealPath() + "\\youtube\\description_prompt.txt",
//      StandardCharsets.UTF_8);
//    ;
//    prompt += "字幕如下：";
//    prompt += FileUtil.readString(
//      srtFileName,
//      StandardCharsets.UTF_8);
//    // 生成文本内容（阻塞式）
//    GeneratedContent generatedContent = null;
//    try {
//      generatedContent = GeminiApiUtil.generateContent(prompt);
//    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//      throw new RuntimeException(e);
//    }
//
//    String scriptFileName = "";
//    try {
//      FileUtils.writeStringToFile(
//        new File(
//          Objects.requireNonNull(
//            CdFileUtil.changeExtension(srtFileName, "md"))),
//        generatedContent.text(), "UTF-8");
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    log.info("4. Generated content: {}", generatedContent);
//
//    log.info("----- 4.测试 generateContent 方法结束");
//
//  }
  public static void process(String folderPath, String subFolder) {
    // TODO
    //Prox

    // 1. 生成字幕
    String lang = "cmn";

//        Map<String, String> chapterNameMap = new HashMap<>();
//        List<String> stringList = FileUtil.readLines(chapterFileName,
//                StandardCharsets.UTF_8);
//        for (String line : stringList) {
//            String[] split = line.split(" ");
//            chapterNameMap.put(split[1], split[2]);
//        }
////    String shortSubFolder = subFolder.substring(8);
//        String chapterName = chapterNameMap.get(shortSubFolder);
    String mp4FileName =
      folderPath + subFolder + File.separator + "video" + File.separator
        + subFolder
        + ".mp4";
    String mp3FileName = CdFileUtil.changeExtension(mp4FileName, "mp3");

    // "/Users/coderdream/Documents/EnBook002/一輩子夠用的英語口語大全集-EP-10-情緒 "

    if (CdFileUtil.isFileEmpty(mp3FileName)) {
      log.info("mp3文件不存在，先生成： {}", mp3FileName);
      CommandUtil.extractAudioFromMp4(mp4FileName, mp3FileName);
    } else {
      log.info("mp3文件存在, {}", mp3FileName);
    }

    String subtitleFolderPath =
      folderPath + subFolder + File.separator + "subtitle" + File.separator;
    if (!new File(subtitleFolderPath).exists()) {
      log.info("subtitle文件夹不存在，先创建： {}", subtitleFolderPath);
      try {
        FileUtils.forceMkdir(new File(subtitleFolderPath));
      } catch (IOException e) {
        log.error("创建文件夹失败：{}", subtitleFolderPath);
      }
    }

    String totalFileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(totalFileNameTotal)) {
      log.info("文件不存在或为空，已生成新文件: {}",
        totalFileNameTotal);
      return;
    }

    String subtitleRawFileName =
      subtitleFolderPath + subFolder + "_subtitle_raw.txt";
    if (CdFileUtil.isFileEmpty(subtitleRawFileName)) {
      List<String> textList = new ArrayList<>(List.of(
        "Enhance your English listening with 30-minute sessions of English audio, ",
        "paired with Chinese dubbing.",
        ZhConverterUtil.toTraditional("英文加中文配音，每次半小時，"),
        // 英文加中文配音，每次半小時，增强你的英文听力。 小時
        ZhConverterUtil.toTraditional("增强你的英文听力。")));
      makeSrcRawFile(totalFileNameTotal, subtitleRawFileName, textList);
    } else {
      log.info("文件已存在，不再生成: {}", subtitleRawFileName);
    }

//        String srtFileName = CdFileUtil.changeExtension(mp3FileName, "srt");
//        // D:\0000\EnBook001\900\ch003\ch003_total.txt
//        String subtitleFileName =
//                OperatingSystem.getBaseFolder() + bookFolderName + File.separator + subFolder + File.separator + subFolder +
//                        "_total.txt";
//        File totalFile = new File(subtitleFileName);
//        if (!totalFile.exists() || totalFile.length() == 0) {
//            log.info("subtitleFileName文件不存在, {}", subtitleFileName);
//            return;
//        }
//
//        List<String> textList = new ArrayList<>(List.of(
//                "Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.",
//                "英文加中文配音，每次半小時，增强你的英文听力。"));
//        List<String> srtList = FileUtil.readLines(subtitleFileName,
//                StandardCharsets.UTF_8);
//        for (String srtLine : srtList) {
//            textList.add(ZhConverterUtil.toTraditional(srtLine));
//        }
////    textList.addAll(srtList);
//        String newSubtitleFileName = CdFileUtil.addPostfixToFileName(
//                subtitleFileName,
//                "_cht");// "D:\\0000\\EnBook001\\900\\ch003\\ch003_total_new.txt";
//        CdFileUtil.writeToFile(newSubtitleFileName, textList);
//
    lang = "eng";
    String srtFileName =
      folderPath + subFolder + File.separator + "subtitle" + File.separator
        + subFolder
        + ".srt";
    if (CdFileUtil.isFileEmpty(srtFileName)) {
      log.info("srt文件不存在, {}", srtFileName);
      SubtitleUtil.genSrtByExecuteCommand(mp3FileName, subtitleRawFileName,
        srtFileName, lang);
    }

    String mdFileName = Objects.requireNonNull(
      CdFileUtil.changeExtension(srtFileName, "md"));
    String chnMdFileName = CdFileUtil.addPostfixToFileName(mdFileName,
      "_zh_CN");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(mdFileName,
      "_zh_TW");
    // 2. 生成描述
    if (CdFileUtil.isFileEmpty(chnMdFileName) || CdFileUtil.isFileEmpty(
      chtMdFileName)) {

      log.info("文件已存在，不再生成: {}", mdFileName);

      log.info("----- 4.测试 generateContent 方法开始");
      String prompt = FileUtil.readString(
        CdFileUtil.getResourceRealPath() + File.separator + "youtube"
          + File.separator + "description_prompt.txt",
        StandardCharsets.UTF_8);
      prompt += "字幕如下：";
      prompt += FileUtil.readString(
        srtFileName,
        StandardCharsets.UTF_8);
      // 生成文本内容（阻塞式）
      GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);

      CdFileUtil.writeToFile(chtMdFileName, Collections.singletonList(
        ZhConverterUtil.toTraditional(generatedContent.text())));

      CdFileUtil.writeToFile(chnMdFileName, Collections.singletonList(
        ZhConverterUtil.toSimple(generatedContent.text())));

      log.info("4. Generated content: {}", generatedContent);
    }

    log.info("----- 4.测试 generateContent 方法结束");
  }

  public static void makeSrcRawFile(String totalFileName, String srtFileName,
    List<String> textList) {
    List<String> responseList = FileUtil.readLines(totalFileName,
      StandardCharsets.UTF_8);
    List<String> srtList = new ArrayList<>(textList);
    // 解析字符串为字幕对象列表
    List<SubtitleEntity> subtitleEntityList = CdFileUtil.genSubtitleEntityList(
      responseList, CdConstants.TRANSLATE_PLATFORM_GEMINI);
    for (SubtitleEntity subtitleEntity : subtitleEntityList) {
      String subtitle = subtitleEntity.getSubtitle();
      String secondSubtitle = subtitleEntity.getSecondSubtitle();
      srtList.add(subtitle);
      srtList.add(subtitle);
      srtList.add(subtitle);
      srtList.add(secondSubtitle);
      srtList.add(subtitle);
    }

    if (CdFileUtil.isFileEmpty(srtFileName)) {
      CdFileUtil.writeToFile(srtFileName, srtList);
    }
  }

  public static void process(String folderPath, String subFolder,
    String shortSubFolder, String bookFolderName,
    String bookName, String chapterFileName) {
    // TODO
    //Prox

    // 1. 生成字幕
    String lang = "cmn";

    Map<String, String> chapterNameMap = new HashMap<>();
    List<String> stringList = FileUtil.readLines(chapterFileName,
      StandardCharsets.UTF_8);
    for (String line : stringList) {
      String[] split = line.split(" ");
      chapterNameMap.put(split[1], split[2]);
    }
//    String shortSubFolder = subFolder.substring(8);
    String chapterName = chapterNameMap.get(shortSubFolder);

    String mp3FileName =
      OperatingSystem.getVideoBaseFolder() + File.separator + bookFolderName
        + File.separator + bookName + "-EP-"
        + shortSubFolder + "-" + chapterName
        + File.separator + bookName + "-EP-" + shortSubFolder + "-"
        + chapterName
        + ".MP3";

    // "/Users/coderdream/Documents/EnBook002/一輩子夠用的英語口語大全集-EP-10-情緒 "

    File mp3File = new File(mp3FileName);
    if (!mp3File.exists() || mp3File.length() == 0) {
      log.info("mp3文件不存在, {}", mp3FileName);
      return;
    }
    String srtFileName = CdFileUtil.changeExtension(mp3FileName, "srt");
    // D:\0000\EnBook001\900\ch003\ch003_total.txt
    String subtitleFileName =
      OperatingSystem.getBaseFolder() + bookFolderName + File.separator
        + subFolder + File.separator + subFolder +
        "_total.txt";
    File totalFile = new File(subtitleFileName);
    if (!totalFile.exists() || totalFile.length() == 0) {
      log.info("subtitleFileName文件不存在, {}", subtitleFileName);
      return;
    }

    List<String> textList = new ArrayList<>(List.of(
      "Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.",
      "英文加中文配音，每次半小時，增强你的英文听力。"));
    List<String> srtList = FileUtil.readLines(subtitleFileName,
      StandardCharsets.UTF_8);
    for (String srtLine : srtList) {
      textList.add(ZhConverterUtil.toTraditional(srtLine));
    }
//    textList.addAll(srtList);
    String newSubtitleFileName = CdFileUtil.addPostfixToFileName(
      subtitleFileName,
      "_cht");// "D:\\0000\\EnBook001\\900\\ch003\\ch003_total_new.txt";
    CdFileUtil.writeToFile(newSubtitleFileName, textList);

    lang = "eng";

    File srtFile = new File(srtFileName);
    if (!srtFile.exists() || srtFile.length() == 0) {
      log.info("srt文件不存在, {}", srtFileName);
      SubtitleUtil.genSrtByExecuteCommand(mp3FileName, newSubtitleFileName,
        srtFileName, lang);
    }

    // 2. 生成描述
    log.info("----- 4.测试 generateContent 方法开始");
    String prompt = FileUtil.readString(
      CdFileUtil.getResourceRealPath() + File.separator + "youtube"
        + File.separator + "description_prompt.txt",
      StandardCharsets.UTF_8);
    prompt += "字幕如下：";
    prompt += FileUtil.readString(
      srtFileName,
      StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);

    String mdFileName = CdFileUtil.changeExtension(srtFileName, "md");
    String chnMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_chn");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_cht");
    try {
      FileUtils.writeStringToFile(
        new File(chtMdFileName),
        generatedContent.text(), "UTF-8");
      FileUtils.writeStringToFile(
        new File(chnMdFileName),
        ZhConverterUtil.toSimple(generatedContent.text()), "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("4. Generated content: {}", generatedContent);

    log.info("----- 4.测试 generateContent 方法结束");
  }
}
