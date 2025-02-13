package com.coderdream.util.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.Book002ChapterInfoEntity;
import com.coderdream.entity.Book002ContentEntity;
import com.coderdream.entity.Book002SceneEntity;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.SentencePair;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.sentence.DialogSingleEntityUtil;
import com.coderdream.util.sentence.SentencePairFilter;
import com.coderdream.util.txt.SceneExtractor;
import com.coderdream.util.txt.filter.TextFileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class BeforeGenerateUtil {


  public static void processGenRawTxt(String folderPath, String subFolder) {
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
    String txtPath = targetFolderPath + subFolder + ".txt";
    if (CdFileUtil.isFileEmpty(rawTxtPath)) {
      String elapsedTime = TextFileUtil.filterTextFile(sourcePath, rawTxtPath);
      String elapsedTime2 = TextFileUtil.filterTextFile(sourcePath, txtPath);
      log.info("耗时：{}：{}", elapsedTime, elapsedTime2);
    }
  }

  /**
   * 生成对话文本
   * @param folderPath  文件夹路径
   * @param subFolder 子文件夹名称
   * @param replaceFlag 是否替换
   */
  public static void processGenDialogTxt(String folderPath, String subFolder, boolean replaceFlag) {
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
    String basicPath =
      folderPath + subFolder + File.separator + subFolder + "_basic.txt";
    String dialogPath =
      folderPath + subFolder + File.separator + subFolder + "_dialog.txt";
    String totalPath =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(dialogPath) || CdFileUtil.isFileEmpty(basicPath)
      || CdFileUtil.isFileEmpty(totalPath) || replaceFlag) {

      Map<Integer, DialogSingleEntity> dialogSingleEntityListSceneMap = getSceneMap(
        folderPath);
      if (dialogSingleEntityListSceneMap == null) {
        return;
      }

      String IDSP = "\u3000";
      Book002ChapterInfoEntity chapterInfoEntity = ChapterStringParser.genChapterInfoEntity(
        sourcePath);

      List<String> basicStringList = new ArrayList<>();
      List<String> dialogStringList = new ArrayList<>();
      List<String> totalStringList = new ArrayList<>();
      assert chapterInfoEntity != null;
      List<Book002SceneEntity> sceneEntityList = chapterInfoEntity.getSceneEntityList();
      for (Book002SceneEntity sceneEntity : sceneEntityList) {

        String sceneTitle = sceneEntity.getSceneTitle();
        // 替换为普通空格
        sceneTitle = sceneTitle.replace(IDSP, " ");
        int firstSpaceIndex = sceneTitle.indexOf(" ");
        int secondSpaceIndex =
          (firstSpaceIndex != -1) ? sceneTitle.indexOf(" ", firstSpaceIndex + 1)
            : -1;
        // 将第一个空格后的内容替换为普通空格，并获取场景编号，并获取对话文本 Scene相关信息放入 basic 对话文本列表中
        if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
          String sceneNumber = StrUtil.sub(sceneTitle, firstSpaceIndex + 1,
            secondSpaceIndex);
          DialogSingleEntity dialogSingleEntity = dialogSingleEntityListSceneMap.get(
            Integer.valueOf(sceneNumber));
          if (dialogSingleEntity != null) {
            basicStringList.add(dialogSingleEntity.getHostEn().trim() + " "
              + dialogSingleEntity.getContentEn().trim());
            basicStringList.add(ZhConverterUtil.toTraditional(
              dialogSingleEntity.getHostCn().trim() + " "
                + dialogSingleEntity.getContentCn().trim()));
          }
        }

        List<Book002ContentEntity> contentEntityList = sceneEntity.getContentEntityList();
        for (Book002ContentEntity contentEntity : contentEntityList) {
          // 将第一句
          SentencePair sentencePair = contentEntity.getSentencePair();
          if(StrUtil.isBlank(sentencePair.getEnglishSentence())
            || StrUtil.isBlank(sentencePair.getChineseSentence())) {
            log.warn("sentencePair 对话文本为空，忽略：{}", sentencePair);
          }

          basicStringList.add(sentencePair.getEnglishSentence());
          basicStringList.add(
            ZhConverterUtil.toTraditional(sentencePair.getChineseSentence()));

          List<SentencePair> sameSentencePairList = contentEntity.getSameSentencePairList();
          for (SentencePair sameSentencePair : sameSentencePairList) {
            if(StrUtil.isBlank(sameSentencePair.getEnglishSentence())
              || StrUtil.isBlank(sameSentencePair.getChineseSentence())) {
              log.warn("sameSentencePair 对话文本为空，忽略：{}", sameSentencePair);
            }
            dialogStringList.add(sameSentencePair.getEnglishSentence());
            dialogStringList.add(ZhConverterUtil.toTraditional(
              sameSentencePair.getChineseSentence()));
          }
        }
      }
      totalStringList.addAll(basicStringList);
      totalStringList.addAll(dialogStringList);

      File elapsedTime1 = FileUtil.writeLines(totalStringList, basicPath,
        StandardCharsets.UTF_8);
      File elapsedTime2 = FileUtil.writeLines(basicStringList, totalPath,
        StandardCharsets.UTF_8);
      File elapsedTime3 = FileUtil.writeLines(dialogStringList, dialogPath,
        StandardCharsets.UTF_8);

    }
  }

  public static void genAiFile(String folderPath, String subFolder) {

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

    // 生成带音标的文件
    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }

  }

  public static void process(String folderPath, String subFolder) {
    // 1. 生成章节文本
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch004"; ❸

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

    // 生成带音标的文件
    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }

    // 2. 生成
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal, aiFileName);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("带音标文件已存在: {}", phoneticsFileName);
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
    List<String> scenseChtStringList = FileUtil.readLines(sceneCht,
      StandardCharsets.UTF_8);
    List<String> scenseEnStringList = FileUtil.readLines(sceneEn,
      StandardCharsets.UTF_8);

    Map<Integer, DialogSingleEntity> dialogSingleEntityListSceneMap = new LinkedHashMap<>();
    if (scenseChtStringList.size() != scenseEnStringList.size()) {
      log.info("scene_cht.txt 与 scene_en.txt 文件行数不一致！");
      return;
    }
    DialogSingleEntity dialogSingleEntity;
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
        log.info("scene_cht.txt 与 scene_en.txt 文件格式不正确！{}  :  {}",
          cht,
          en);
        return;
      }
    }

    List<SentencePair> sentencePairList = new ArrayList<>();
    SentencePair sentencePair;
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
          String englishSentence;
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
          //  String patternStr = "同类表达";
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
    String englishSentence;
    String chineseSentence;
    for (SentencePair sentencePair2 : filteredList1) {
      chineseSentence = sentencePair2.getChineseSentence();
      if (StrUtil.isBlank(chineseSentence)) {
        log.info("@@修改后 sentencePair1 {}", sentencePair2);
      }

      log.info("修改后 sentencePair2 {}", sentencePair2);

      englishSentence = sentencePair2.getEnglishSentence();
      if (StrUtil.isBlank(englishSentence)
        && englishSentence.length() > CdConstants.SINGLE_SCRIPT_LENGTH) {
        List<String> englishSentenceList = Arrays.asList(
          englishSentence.split("/."));
        List<String> chineseSentenceList = Arrays.asList(
          chineseSentence.split("。"));
        if (CollectionUtil.isNotEmpty(englishSentenceList)
          && CollectionUtil.isNotEmpty(chineseSentenceList)) {
          if (englishSentenceList.size() != chineseSentenceList.size()) {
            log.info("######### 英文和中文句子数量不一致！");
            return;
          } else {
            log.info("######### 英文和中文句子数量相同致！");
            for (int i = 0; i < englishSentenceList.size(); i++) {
              String englishSentenceItem = englishSentenceList.get(i);
              String chineseSentenceItem = chineseSentenceList.get(i);

              totalList.add(englishSentenceItem);
              totalList.add(chineseSentenceItem);
            }
          }
        }
      } else {
        totalList.add(sentencePair2.getEnglishSentence());
        totalList.add(ZhConverterUtil.toTraditional(chineseSentence));
      }
    }

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

    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }

    // 2. 生成
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal, aiFileName);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("带音标文件已存在: {}", phoneticsFileName);
    }

  }

  public static void processBook0201(String folderPath, String subFolder) {
    String fileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {
      return;
    }

    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }

    // 2. 生成
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal, aiFileName);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("带音标文件已存在: {}", phoneticsFileName);
    }
  }

  public static void processBook002_AI(String folderPath, String subFolder) {
    String fileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {
      return;
    }

    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }
  }


  public static void processBook002Phonetics(String folderPath, String subFolder) {
    String fileNameTotal =
      folderPath + subFolder + File.separator + subFolder + "_total.txt";
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {
      return;
    }

    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");

    // 2. 生成英文+音标的文件
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal, aiFileName);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("带音标文件已存在: {}", phoneticsFileName);
    }
  }

  private static boolean genTotal(String fileNameTotal,
    List<SentencePair> filteredList1) {
    if (CdFileUtil.isFileEmpty(fileNameTotal)) {

      List<String> totalList = new ArrayList<>();
      String englishSentence;
      String chineseSentence;
      for (SentencePair sentencePair2 : filteredList1) {
        chineseSentence = sentencePair2.getChineseSentence();
        if (StrUtil.isBlank(chineseSentence)) {
          log.info("@@修改后 sentencePair1 {}", sentencePair2);
        }

        log.info("修改后 sentencePair2 {}", sentencePair2);

        englishSentence = sentencePair2.getEnglishSentence();
        if (StrUtil.isBlank(englishSentence)
          && englishSentence.length() > CdConstants.SINGLE_SCRIPT_LENGTH) {
          List<String> englishSentenceList = Arrays.asList(
            englishSentence.split("/."));
          List<String> chineseSentenceList = Arrays.asList(
            chineseSentence.split("。"));
          if (CollectionUtil.isNotEmpty(englishSentenceList)
            && CollectionUtil.isNotEmpty(chineseSentenceList)) {
            if (englishSentenceList.size() != chineseSentenceList.size()) {
              log.info("######### 英文和中文句子数量不一致！");
              return true;
            } else {
              log.info("######### 英文和中文句子数量相同致！");
              for (int i = 0; i < englishSentenceList.size(); i++) {
                String englishSentenceItem = englishSentenceList.get(i);
                String chineseSentenceItem = chineseSentenceList.get(i);

                totalList.add(englishSentenceItem);
                totalList.add(chineseSentenceItem);
              }
            }
          }
        } else {
          totalList.add(sentencePair2.getEnglishSentence());
          totalList.add(ZhConverterUtil.toTraditional(chineseSentence));
        }
      }

      File fileTotal = FileUtil.writeLines(totalList, fileNameTotal,
        StandardCharsets.UTF_8);
      log.info("文件不存在或为空，已生成新文件: {}",
        fileTotal.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", fileNameTotal);
    }
    return false;
  }

  /**
   * 只保留 Scene开头和序号开头
   *
   * @param folderPath
   * @param subFolder
   */
  public static void processBook0202(String folderPath, String subFolder) {
    // 1. 生成章节文本
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch004"; ❸
    String sourcePath =
      folderPath + subFolder + File.separator + subFolder + ".txt";

//    String targetFolderPath =
//      folderPath + subFolder + File.separator + "input" + File.separator;
//    // 1.1. 创建目标文件夹
//    File targetFolder = new File(targetFolderPath);
//    if (!targetFolder.exists()) {
//      FileUtil.mkdir(targetFolderPath);
//    }

    Map<Integer, DialogSingleEntity> dialogSingleEntityListSceneMap = getSceneMap(
      folderPath);
    if (dialogSingleEntityListSceneMap == null) {
      return;
    }

    List<SentencePair> sentencePairList = new ArrayList<>();
    SentencePair sentencePair;
    List<String> stringList = FileUtil.readLines(sourcePath,
      StandardCharsets.UTF_8);
    List<SentencePair> sceneDialogSingleEntityList = null;
    List<SentencePair> sequenceDialogSingleEntityList = null;
    List<SentencePair> dualDialogSingleEntityList = null;
    List<String> sequenceLineList = new ArrayList<>();
    for (String line : stringList) {
      sceneDialogSingleEntityList = new ArrayList<>();
      sequenceDialogSingleEntityList = new ArrayList<>();
      dualDialogSingleEntityList = new ArrayList<>();
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
          // 场景对象添加到列表中
          sceneDialogSingleEntityList.add(sentencePair);
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
          String englishSentence;
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
          sequenceDialogSingleEntityList.add(sentencePairTemp);
        } else {

          //处理 同类表达 跳过
          //  String patternStr = "同类表达";
//          replaceAndGenObject(line, "同类表达", sentencePairList);
//          replaceAndGenObject(line, "这样回答", sentencePairList);
//          replaceAndGenObject(line, "对话 A:", sentencePairList);
//          replaceAndGenObject(line, "B:", sentencePairList);
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
    String englishSentence;
    String chineseSentence;
    for (SentencePair sentencePair2 : filteredList1) {
      chineseSentence = sentencePair2.getChineseSentence();
      if (StrUtil.isBlank(chineseSentence)) {
        log.info("@@修改后 sentencePair1 {}", sentencePair2);
      }

      log.info("修改后 sentencePair2 {}", sentencePair2);

      englishSentence = sentencePair2.getEnglishSentence();
      if (StrUtil.isBlank(englishSentence)
        && englishSentence.length() > CdConstants.SINGLE_SCRIPT_LENGTH) {
        List<String> englishSentenceList = Arrays.asList(
          englishSentence.split("/."));
        List<String> chineseSentenceList = Arrays.asList(
          chineseSentence.split("。"));
        if (CollectionUtil.isNotEmpty(englishSentenceList)
          && CollectionUtil.isNotEmpty(chineseSentenceList)) {
          if (englishSentenceList.size() != chineseSentenceList.size()) {
            log.info("######### 英文和中文句子数量不一致！");
            return;
          } else {
            log.info("######### 英文和中文句子数量相同致！");
            for (int i = 0; i < englishSentenceList.size(); i++) {
              String englishSentenceItem = englishSentenceList.get(i);
              String chineseSentenceItem = chineseSentenceList.get(i);

              totalList.add(englishSentenceItem);
              totalList.add(chineseSentenceItem);
            }
          }
        }
      } else {
        totalList.add(sentencePair2.getEnglishSentence());
        totalList.add(ZhConverterUtil.toTraditional(chineseSentence));
      }
    }

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

    // 生成带音标的文件
    String aiFileName = CdFileUtil.addPostfixToFileName(fileNameTotal, "_ai");
    if (CdFileUtil.isFileEmpty(aiFileName)) {
      File file = TranslationUtil.genAiFile(fileNameTotal);
      assert file != null;
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("AI文件已存在: {}", aiFileName);
    }

    // 2. 生成
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal, aiFileName);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("带音标文件已存在: {}", phoneticsFileName);
    }

  }

  private static @Nullable Map<Integer, DialogSingleEntity> getSceneMap(
    String folderPath) {
    Map<Integer, DialogSingleEntity> dialogSingleEntityListSceneMap = new LinkedHashMap<>();
    String sceneCht = folderPath + File.separator + "scene_cht.txt";
    String sceneEn = folderPath + File.separator + "scene_en.txt";
    List<String> scenseChtStringList = FileUtil.readLines(sceneCht,
      StandardCharsets.UTF_8);
    List<String> scenseEnStringList = FileUtil.readLines(sceneEn,
      StandardCharsets.UTF_8);

    if (scenseChtStringList.size() != scenseEnStringList.size()) {
      log.info("scene_cht.txt 与 scene_en.txt 文件行数不一致！");
      return null;
    }
    DialogSingleEntity dialogSingleEntity;
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
        log.info("scene_cht.txt 与 scene_en.txt 文件格式不正确！{}  :  {}",
          cht,
          en);
        return null;
      }
    }
    return dialogSingleEntityListSceneMap;
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

  public static void processBook002_01(String folderPath, String subFolder) {

    Book002ChapterInfoEntity book002ChapterInfoEntity = new Book002ChapterInfoEntity();
    String sourcePath =
      folderPath + subFolder + File.separator + subFolder + "_temp.txt";
    List<String> stringList = FileUtil.readLines(sourcePath,
      StandardCharsets.UTF_8);
    List<SentencePair> sentencePairs = new ArrayList<>();
    for (String line : stringList) {
      replaceAndGenObject(line, "CHAPTER ", sentencePairs);
      replaceAndGenObject(line, "SCENE ", sentencePairs);
    }

  }
}
