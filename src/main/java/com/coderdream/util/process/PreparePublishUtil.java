package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.SubtitleUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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

    /// /    String srtFileName = CdFileUtil.changeExtension(newSubtitleFileName, "srt") ;// "D:\\0000\\EnBook001\\900\\ch003\\ch003_total.srt";
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

        String mp3FileName = OperatingSystem.getVideoBaseFolder() + File.separator + bookFolderName + File.separator + bookName + "-EP-"
                + shortSubFolder + "-" + chapterName
                + File.separator + bookName + "-EP-" + shortSubFolder + "-" + chapterName
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
                OperatingSystem.getBaseFolder() + bookFolderName + File.separator + subFolder + File.separator + subFolder +
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
                CdFileUtil.getResourceRealPath() + File.separator + "youtube" + File.separator + "description_prompt.txt",
                StandardCharsets.UTF_8);
        prompt += "字幕如下：";
        prompt += FileUtil.readString(
                srtFileName,
                StandardCharsets.UTF_8);
        // 生成文本内容（阻塞式）
        GeneratedContent generatedContent;
        try {
            generatedContent = GeminiApiUtil.generateContent(prompt);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        String mdFileName = Objects.requireNonNull(
                CdFileUtil.changeExtension(srtFileName, "md"));
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

    public static void generateDescription(String srtFileName) {
        // 2. 生成描述
        log.info("----- 4.测试 generateContent 方法开始");
        String prompt = FileUtil.readString(
                CdFileUtil.getResourceRealPath() + File.separator + "youtube" + File.separator + "description_prompt.txt",
                StandardCharsets.UTF_8);
        prompt += "字幕如下：";
        prompt += FileUtil.readString(
                srtFileName,
                StandardCharsets.UTF_8);
        // 生成文本内容（阻塞式）
        GeneratedContent generatedContent = null;
        try {
            generatedContent = GeminiApiUtil.generateContent(prompt);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        File file = new File(
                Objects.requireNonNull(
                        CdFileUtil.changeExtension(srtFileName, "md")));
        try {
            FileUtils.writeStringToFile(file, generatedContent.text(), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("4. Generated content: {}", generatedContent);

        log.info("----- 4.测试 generateContent 方法结束");
    }

}
