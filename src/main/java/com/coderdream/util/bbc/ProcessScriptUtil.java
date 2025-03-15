package com.coderdream.util.bbc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.CommonUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     处理原始文本，执行一下操作：
 *     1、生成对话脚本文本文件；
 *     2、生成词汇表文本文件；
 *     3、更新主持人Excel文件（host.xlsx）
 *
 * </pre>
 *
 * @author CoderDream
 */
public class ProcessScriptUtil {

  public static void main(String[] args) {
//    String folderName = "181122";
//    ProcessScriptUtil.process(folderName);

//        replace();
  }
//
//  /**
//   * @param folderName 文件夹
//   */
//  public static void process(String folderName) {
//    String fileName = folderName + "_script";
//
//
//    String scriptDialogFileName = CommonUtil.getFullPathFileName(folderName,
//      "script_dialog", ".txt");
//
//    String vocFileName = CommonUtil.getFullPathFileName(folderName, "voc",
//      ".txt");
//
//
//    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
//      ".txt");
//    List<String> stringList = CdFileUtil.readLines(srcFileName,
//      StandardCharsets.UTF_8);
//
////    Map<Integer, String> stringListWithIndex = new HashMap<>();
//    int transcriptIndex = 0;
//    int vocIndex = 0;
//    for (int i = 0; i < stringList.size(); i++) {
////      stringListWithIndex.put(i, stringList.get(i));
//      if (stringList.get(i).contains("word-for-word transcript")) {
//        transcriptIndex = i;
//      }
//
//      if (stringList.get(i).toLowerCase()
//        .contains("VOCABULARY".toLowerCase())) {
//        vocIndex = i;
//      }
//    }
//    List<String> scriptDialogContentList = new ArrayList<>();
//    List<String> scriptList; // DialogSingleEntity
//    scriptList = stringList.subList(transcriptIndex + 1, vocIndex);
//    List<DialogSingleEntity> dialogSingleEntityList = DialogParser.parseDialogSingleEntity(
//      scriptList);
//    for (DialogSingleEntity entity : dialogSingleEntityList) {
//      scriptDialogContentList.add(entity.getHostEn());
//      scriptDialogContentList.add(entity.getContentEn());
//      scriptDialogContentList.add(" ");
//    }
//    CdFileUtil.writeToFile(scriptDialogFileName, scriptDialogContentList);
//
//    List<String> vocContentList = new ArrayList<>();
//    List<String> vocList = new ArrayList<>(); // VocInfo
//    vocList = stringList.subList(vocIndex + 1, stringList.size());
//    List<VocInfo> vocInfoList = DialogParser.parseDialogVocInfo(vocList); //
//    for (VocInfo vocInfo : vocInfoList) {
//      vocContentList.add(vocInfo.getWord());
//      vocContentList.add(vocInfo.getWordExplainEn());
//      vocContentList.add(" ");
//    }
//    CdFileUtil.writeToFile(vocFileName, vocContentList);
//  }

  /**
   * @param folderName 文件夹
   */
  public static File genScriptDialogTxt(String folderName,
    String scriptDialogFileName) {
    String fileName = folderName + "_script";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = FileUtil.readLines(srcFileName,
      StandardCharsets.UTF_8);

//    Map<Integer, String> stringListWithIndex = new HashMap<>();
    int transcriptIndex = 0;
    int vocIndex = 0;
    for (int i = 0; i < stringList.size(); i++) {
//      stringListWithIndex.put(i, stringList.get(i));
      if (stringList.get(i).contains("word-for-word transcript")) {
        transcriptIndex = i;
      }

      if (stringList.get(i).toLowerCase()
        .contains("VOCABULARY".toLowerCase())) {
        vocIndex = i;
      }
    }
    List<String> scriptDialogContentList = new ArrayList<>();
    List<String> scriptList; // DialogSingleEntity
    scriptList = stringList.subList(transcriptIndex + 1, vocIndex);
    List<DialogSingleEntity> dialogSingleEntityList = DialogParser.parseDialogSingleEntity(
      scriptList);
    for (DialogSingleEntity entity : dialogSingleEntityList) {
      scriptDialogContentList.add(entity.getHostEn());
      scriptDialogContentList.add(entity.getContentEn());
      scriptDialogContentList.add(" ");
    }

    return FileUtil.writeLines(scriptDialogContentList,
      scriptDialogFileName, StandardCharsets.UTF_8);
  }

  /**
   * @param folderName 文件夹
   */
  public static File genVocTxt(String folderName, String vocFileName) {
    String fileName = folderName + "_script";

    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = FileUtil.readLines(srcFileName,
      StandardCharsets.UTF_8);

    int vocIndex = 0;
    for (int i = 0; i < stringList.size(); i++) {

      if (stringList.get(i).toLowerCase()
        .contains("VOCABULARY".toLowerCase())) {
        vocIndex = i;
      }
    }

    List<String> vocContentList = new ArrayList<>();
    List<String> vocList = new ArrayList<>(); // VocInfo
    vocList = stringList.subList(vocIndex + 1, stringList.size());
    List<VocInfo> vocInfoList = DialogParser.parseDialogVocInfo(vocList); //
    for (VocInfo vocInfo : vocInfoList) {
      vocContentList.add(vocInfo.getWord());
      vocContentList.add(vocInfo.getWordExplainEn());
      vocContentList.add(" ");
    }

    return FileUtil.writeLines(vocContentList, vocFileName,
      StandardCharsets.UTF_8);
  }

  /**
   * @param vocBeginIndex
   * @param vocEndIndex
   * @param stringList
   * @param vocList
   */
  private static void genVocList(int vocBeginIndex, int vocEndIndex,
    List<String> stringList, List<String> vocList) {
    // 设置词汇表的字符串列表
    int vocIdx = 0;// 上一次词汇的结束标志位
    String phrase;
    for (int i = vocBeginIndex; i < vocEndIndex; i++) {
      // 如果是空字符串，则说明
      String tempStr = stringList.get(i);
      if (StrUtil.isEmpty(tempStr)) {
        // 第一个
        if (vocIdx == 0) {
          phrase = stringList.get(vocBeginIndex);

          String explainEn = "";
          for (int j = vocBeginIndex + 1; j < i; j++) {
            if (StrUtil.isEmpty(explainEn)) {
              explainEn += stringList.get(j);
            } else {
              explainEn += " " + stringList.get(j);
            }
          }

          vocList.add(phrase);
          vocList.add(explainEn);
          vocList.add("");
          vocIdx = i + 1;
        } else {
          // 如果前一行不是空行就继续
          if (!StrUtil.isEmpty(stringList.get(i - 1))) {
            phrase = stringList.get(vocIdx);

            String explainEn = "";
            for (int j = vocIdx + 1; j < i; j++) {
              if (StrUtil.isEmpty(explainEn)) {
                explainEn += stringList.get(j);
              } else {
                explainEn += " " + stringList.get(j);
              }
            }

            vocList.add(phrase);
            vocList.add(explainEn);
            vocList.add("");
            vocIdx = i + 1;
          }
        }

//                System.out.println("VOC tempStr: " + tempStr);
//                if (StrUtil.isNotEmpty(tempStr) || StrUtil.isNotEmpty(prevStr)) {
//                    vocList.add(tempStr);
//                }
      }
    }
  }


}
