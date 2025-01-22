package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.SentencePair;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.sentence.DialogSingleEntityUtil;
import com.coderdream.util.sentence.SentencePairFilter;
import com.coderdream.util.txt.SceneExtractor;
import com.coderdream.util.txt.SentenceSplitter;
import com.coderdream.util.txt.filter.TextFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeforeGenerateUtil {


  public static void process(String folderPath, String subFolder) {
    // 1. 生成章节文本
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch004"; ❸
    String sourcePath =
      folderPath + subFolder + File.separator + subFolder + "_temp.txt";

    String targetFolderPath =
      folderPath + subFolder + File.separator + "input" + File.separator;
    // 1.1. 创建目标文件夹
    File targetFolder = new File(targetFolderPath);
    if (!targetFolder.exists()) {
      FileUtil.mkdir(targetFolderPath);
    }
    // 生成初剪文本
    String rawTxtPath = targetFolderPath + subFolder + "_raw.txt";
    if (CdFileUtil.isFileEmpty(rawTxtPath)) {
      String elapsedTime = TextFileUtil.filterTextFile(sourcePath, rawTxtPath);
      log.info("耗时：{}", elapsedTime);
    }

    // 分割并校验文本
    boolean b = DialogSingleEntityUtil.genPart1AndPart2File(folderPath,
      subFolder);
    if (!b) {
      log.info("分割文本文件失败！");
      return;
    }

    String fileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {
      File fileTotal = DialogSingleEntityUtil.genTotalFile(folderPath,
        subFolder);
      log.info("文件不存在或为空，已生成新文件: {}",
        fileTotal.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", fileNameTotal);
    }
  }

  public static void processBook02(String folderPath, String subFolder) {
    // 1. 生成章节文本
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch004"; ❸
    String sourcePath =
      folderPath + subFolder + File.separator + subFolder + ".txt";

    String targetFolderPath =
      folderPath + subFolder + File.separator + "input" + File.separator;
    // 1.1. 创建目标文件夹
    File targetFolder = new File(targetFolderPath);
    if (!targetFolder.exists()) {
      FileUtil.mkdir(targetFolderPath);
    }
    String sceneCht = folderPath + File.separator + "scene_cht.txt";
    String sceneEn = folderPath + File.separator + "scene_en.txt";
    // scense_cht.txt
    // scense_en.txt

    List<String> scenseChtStringList = FileUtil.readLines(sceneCht,
      StandardCharsets.UTF_8);

    List<String> scenseEnStringList = FileUtil.readLines(sceneEn,
      StandardCharsets.UTF_8);

    Map<Integer, DialogSingleEntity> dialogSingleEntityListSceneMap = new LinkedHashMap<>();
    if (scenseChtStringList.size() != scenseEnStringList.size()) {
      log.info("scene_cht.txt 与 scene_en.txt 文件行数不一致！");
      return;
    }
    DialogSingleEntity dialogSingleEntity = new DialogSingleEntity();
    for (int i = 0; i < scenseChtStringList.size(); i++) {
      String cht = scenseChtStringList.get(i);
      String[] splitCht = cht.split("\t");
      String en = scenseEnStringList.get(i);
      String[] splitEn = en.split("\t");
      if (splitCht.length == 3 && splitEn.length == 3) {
        dialogSingleEntity = new DialogSingleEntity();
        dialogSingleEntity.setHostEn(splitEn[0] + " " + splitEn[1]);
        dialogSingleEntity.setContentEn(splitEn[2]);
        dialogSingleEntity.setHostCn(splitCht[0] + " " + splitCht[1]);
        dialogSingleEntity.setContentCn(splitCht[2]);
        dialogSingleEntityListSceneMap.put(Integer.parseInt(splitEn[1]),
          dialogSingleEntity);
      } else {
        log.info("scene_cht.txt 与 scene_en.txt 文件格式不正确！{}  :  {}", cht,
          en);
        return;
      }
    }

    List<SentencePair> sentencePairList = new ArrayList<>();
    SentencePair sentencePair = null;

    List<String> stringList = FileUtil.readLines(sourcePath,
      StandardCharsets.UTF_8);
    for (String line : stringList) {
      sentencePair = new SentencePair();
      // 过滤掉以Chapter+空格开头的行
      if (line.startsWith("Chapter ")) {
        continue;
      }

      if (line.startsWith("Scene ")) {
        Integer sceneIndex = SceneExtractor.extractSceneNumberFromString(
          line);
        DialogSingleEntity dialogSingleEntityTemp = dialogSingleEntityListSceneMap.get(
          sceneIndex);
        if (dialogSingleEntityTemp != null) {

          sentencePair.setEnglishSentence(
            dialogSingleEntityTemp.getHostEn() + " "
              + dialogSingleEntityTemp.getContentEn());
          sentencePair.setChineseSentence(
            dialogSingleEntityTemp.getHostCn() + " "
              + dialogSingleEntityTemp.getContentCn());

          sentencePairList.add(sentencePair);
        } else {
          log.info("{} 文件格式不正确！{}", sourcePath, line);
          return;
        }
      } else {
//          log.info("{} 文件格式不正确！{}",sourcePath, string);
//          return;
        // 定义匹配序号的正则表达式 ❶、❷、...、❿
        Pattern sequencePattern = Pattern.compile("^[❶-❿]\\s*");

        // 移除序号
        if (sequencePattern.matcher(line).find()) {
          String lineWithoutSequence = sequencePattern.matcher(line)
            .replaceAll("");

          int lastChineseCharIndex = CdStringUtil.findLastChineseCharOrPunctuationIndexWithoutPunct(
            lineWithoutSequence);
          String chineseSentence = "";
          String englishSentence = "";
          //如果 lastChineseCharIndex 为 -1,则说明没有中文
          if (lastChineseCharIndex != -1) {
            chineseSentence = lineWithoutSequence.substring(0,
              lastChineseCharIndex + 1).trim(); // 需要加1,
            englishSentence = lineWithoutSequence.substring(
              lastChineseCharIndex + 1).trim();
          } else {
            englishSentence = lineWithoutSequence.trim(); // 没有中文，全部是英文
          }

          SentencePair sentencePairTemp = new SentencePair(englishSentence,
            chineseSentence);
          sentencePairList.add(sentencePairTemp);
        } else {

          //处理 同类表达
          String patternStr = "同类表达";

          replaceAndGenObject(line, "同类表达", sentencePairList);
          replaceAndGenObject(line, "这样回答", sentencePairList);
          replaceAndGenObject(line, "对话 A:", sentencePairList);
          replaceAndGenObject(line, "B:", sentencePairList);
        }
      }

    }

    List<SentencePair> filteredList1 = SentencePairFilter.filterDuplicateSentencePairs(
      sentencePairList);

    int size = filteredList1.size();
    if (size > 0) {
      SentencePair previousPair = null;
      for (int i = 0; i < size; i++) {
        if (i > 1) {
          previousPair = filteredList1.get(i - 1);
        }

        SentencePair sentencePair1 = filteredList1.get(i);
        String chineseSentence = sentencePair1.getChineseSentence();
        if (StrUtil.isBlank(chineseSentence)) {
          log.info("sentencePair1 {}", sentencePair1);
          assert previousPair != null;
          sentencePair1.setChineseSentence(previousPair.getChineseSentence());
        }
      }
    }

    List<String> totalList = new ArrayList<>();
    for (SentencePair sentencePair2 : filteredList1) {
      String chineseSentence = sentencePair2.getChineseSentence();
      if (StrUtil.isBlank(chineseSentence)) {
        log.info("@@修改后 sentencePair1 {}", sentencePair2);
      }

      log.info("修改后 sentencePair2 {}", sentencePair2);
      totalList.add(sentencePair2.getEnglishSentence());
      totalList.add(
        ZhConverterUtil.toTraditional(sentencePair2.getChineseSentence()));
    }

    // 生成初剪文本
//      String rawTxtPath = targetFolderPath + subFolder + "_raw.txt";
//      if (CdFileUtil.isFileEmpty(rawTxtPath)) {
//        String elapsedTime = TextFileUtil.filterTextFile(sourcePath, rawTxtPath);
//        log.info("耗时：{}", elapsedTime);
//      }
//
//      // 分割并校验文本
//      boolean b = DialogSingleEntityUtil.genPart1AndPart2File(folderPath,
//        subFolder);
//      if (!b) {
//        log.info("分割文本文件失败！");
//        return;
//      }
//
    String fileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {
      File fileTotal = FileUtil.writeLines(totalList, fileNameTotal,
        StandardCharsets.UTF_8);
      log.info("文件不存在或为空，已生成新文件: {}",
        fileTotal.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", fileNameTotal);
    }

//    String targetPath = targetFolderPath + subFolder + ".txt";
//    if (CdFileUtil.isFileEmpty(targetPath)) {
//      String elapsedTime = TextFileUtil.filterTextFile(sourcePath, targetPath);
//      log.info("耗时：{}", elapsedTime);
//    }

    // 2. 生成描述

  }

  private static void replaceAndGenObject(String line, String patternStr,
    List<SentencePair> sentencePairList) {
    if (line.startsWith(patternStr)) {
      line = line.replace(patternStr, "");

      int lastChineseCharIndex = CdStringUtil.findFirstChineseCharOrPunctuationIndexWithoutPunct(
        line);
      String chineseSentence = "";
      String englishSentence = "";
      //如果 lastChineseCharIndex 为 -1,则说明没有中文
      if (lastChineseCharIndex != -1) {
        englishSentence = line.substring(0,
          lastChineseCharIndex).trim(); // 需要加1,
        chineseSentence = line.substring(
          lastChineseCharIndex).trim();
      } else {
        englishSentence = line.trim(); // 没有中文，全部是英文
      }

      SentencePair sentencePairTemp = new SentencePair(englishSentence,
        chineseSentence);
      sentencePairList.add(sentencePairTemp);
    }
  }
}
