package com.coderdream.util.bbc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.SubtitleEntity;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.BbcConstants;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.gemini.GeminiApiClient;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author CoderDream
 */
@Slf4j
public class TranslateUtil {

  public static void main(String[] args) {

    String folderName = "181213";
    TranslateUtil.process(folderName);
//        TranslateUtil.mergeScriptContent(folderName);

//        TranslateUtil.translateEngSrc(folderName);

//        String str = "大括号外面的内容（Michael Collins）的内容（Michael Collins） ";
//        str = removeEnContent(str);
//        System.out.println(str);
//
//    List<String> NUMBER_LIST = Arrays.asList("201015");
//    TranslateUtil.translateTitleWithScriptFile(NUMBER_LIST, "script");

//        TranslateUtil.mergeScriptContentWx(folderName);

    //translateAllTitle();

//        process(folderName);
  }

  /**
   * @param str
   * @return
   */
  @NotNull
  private static String removeEnContent(String str) {
    do {
      int startIndex = str.lastIndexOf("（");
      int endIndex = str.lastIndexOf("）");
      if (startIndex != -1 && endIndex != -1) {
        try {
          if (startIndex >= 0 && endIndex > 0) {
            str = str.replaceAll(str.substring(startIndex, endIndex + 1), "");
          } else {
            System.out.println(
              "#####x##### ERROR: startIndex is " + startIndex
                + "; endIndex is " + endIndex + "; str "
                + str);
          }
        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("### str " + str);
        }
      }
    } while (str.contains("（") && str.contains("）"));
    return str;
  }

  public static List<String> translateTitle(List<String> folderNameList,
    String fileName) {
//        if (fileName == null) {
//            fileName = "script_raw";
//        }
    // 220303_script.txt
//        if (fileName.endsWith("_script.txt")) {
//            // TODO
//        }

    // 6 Minute English
    // word-for-word transcript
    List<String> titleList = new ArrayList<>();
    for (String folderName : folderNameList) {
      fileName = folderName + "_script"; // TODO 指定
      String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
        ".txt");
      List<String> stringList = CdFileUtil.readFileContent(srcFileName);

      String title;
      int size = stringList.size();
      if (CollectionUtil.isNotEmpty(stringList)) {
        //    titleList.add(stringList.get(0));
        titleList.add(getTitleString(stringList));
      }
    }

    String textTitleList = titleList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListTitleCn = TranslatorTextUtil.translatorText(
      textTitleList);
    String[] arr = new String[0];
    for (int i = 0; i < stringListTitleCn.size(); i++) {
      String temp = stringListTitleCn.get(i);
      arr = temp.split("\r\n");
    }

    List<String> titleCnList = Arrays.asList(arr);
    List<String> newList = new ArrayList<>();
    String titleTranslate;
    for (int i = 0; i < arr.length; i++) {
      titleTranslate =
        folderNameList.get(i).substring(2) + "\t" + titleList.get(i) + "\t"
          + arr[i];
      System.out.println(titleTranslate);
      newList.add(titleTranslate);
    }

//        String srcFileNameCn = BbcConstants.ROOT_FOLDER_NAME + File.separator + "title.txt";
    // 写中文翻译文本
//         CdFileUtil.writeToFile(srcFileNameCn, newList);
    return titleCnList;
  }

  public static List<String> translateTitleWithScriptFile(
    List<String> folderNameList, String fileName) {
//        if (fileName == null) {
//            fileName = "script_raw";
//        }
    // 220303_script.txt
//        if (fileName.endsWith("_script.txt")) {
//            // TODO
//        }

    // 6 Minute English
    // word-for-word transcript
    List<String> titleList = new ArrayList<>();
    for (String folderName : folderNameList) {
      fileName = folderName + "_script"; // TODO 指定
      String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
        ".txt");
      List<String> stringList = CdFileUtil.readFileContent(srcFileName);

      String title;
      int size = stringList.size();
      if (CollectionUtil.isNotEmpty(stringList)) {
        //    titleList.add(stringList.get(0));
        titleList.add(getTitleString(stringList));
      }
    }

    String textTitleList = titleList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListTitleCn = TranslatorTextUtil.translatorText(
      textTitleList);
    String[] arr = new String[0];
    for (int i = 0; i < stringListTitleCn.size(); i++) {
      String temp = stringListTitleCn.get(i);
      arr = temp.split("\r\n");
    }

    List<String> titleCnList = Arrays.asList(arr);
    List<String> newList = new ArrayList<>();
    String titleTranslate;
    for (int i = 0; i < arr.length; i++) {
      titleTranslate =
        folderNameList.get(i).substring(2) + "\t" + titleList.get(i) + "\t"
          + arr[i];
      System.out.println(titleTranslate);
      newList.add(titleTranslate);
    }

//        String srcFileNameCn = BbcConstants.ROOT_FOLDER_NAME + File.separator + "title.txt";
    // 写中文翻译文本
//         CdFileUtil.writeToFile(srcFileNameCn, newList);
    return titleCnList;
  }

  private static String getTitleString(List<String> stringList) {
    int startIndex = 0;
    int endIndex = 0;

    for (int i = 0; i < stringList.size(); i++) {
      if (stringList.get(i).contains("6 Minute English")) {
        startIndex = i;
      }
      if (stringList.get(i).contains("word-for-word transcript")) {
        endIndex = i;
        break;
      }
    }
    StringBuilder result = new StringBuilder();

    for (int i = startIndex + 1; i < endIndex; i++) {
      result.append(stringList.get(i));
    }

    return result.toString();
  }

  /**
   * 手工粘贴文本
   *
   * @param folderNameList
   * @param fileName
   * @return
   */
  public static List<String> translateTitleManual(List<String> folderNameList,
    String fileName) {
    if (fileName == null) {
      fileName = "script_raw";
    }
    List<String> titleList = new ArrayList<>();
    for (String folderName : folderNameList) {
      String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
        ".txt");
      List<String> stringList = CdFileUtil.readFileContent(srcFileName);

      String title;
      int size = stringList.size();

      for (int i = 0; i < size; i++) {
        String tempStr = stringList.get(i);
//                System.out.println("tempStr: " + tempStr);
        if ("INTERMEDIATE LEVEL".equals(tempStr)) {
          title = stringList.get(i + 1);
          titleList.add(title);
          break;
        }
      }
    }

    String textTitleList = titleList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListTitleCn = TranslatorTextUtil.translatorText(
      textTitleList);
    String[] arr = new String[0];
    for (int i = 0; i < stringListTitleCn.size(); i++) {
      String temp = stringListTitleCn.get(i);
      arr = temp.split("\r\n");
    }

    List<String> titleCnList = Arrays.asList(arr);
    List<String> newList = new ArrayList<>();
    String titleTranslate;
    for (int i = 0; i < arr.length; i++) {
      titleTranslate =
        folderNameList.get(i).substring(2) + "\t" + titleList.get(i) + "\t"
          + arr[i];
      System.out.println(titleTranslate);
      newList.add(titleTranslate);
    }

    String srcFileNameCn =
      BbcConstants.ROOT_FOLDER_NAME + File.separator + "title.txt";
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameCn, newList);
    return titleCnList;
  }

  /**
   * 翻译脚本
   *
   * @param folderName
   */
  public static void process(String folderName) {
    String fileName = "script_dialog";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = CdFileUtil.readFileContent(srcFileName);

    assert stringList != null;
    List<String> newList = translate(folderName, stringList);

    String srcFileNameCn = CommonUtil.getFullPathFileName(folderName, fileName,
      "_cn.txt");
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameCn, newList);
  }

  /**
   * 翻译脚本
   *
   * @param folderName    文件夹名称
   * @param srcFileNameCn 翻译后的文件名
   */
  public static File genScriptDialogCn(String folderName,
    String srcFileNameCn) {
    String fileName = "script_dialog";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> stringList = CdFileUtil.readFileContent(srcFileName);

    List<String> newList = translate(folderName, stringList);

    // 写中文翻译文本
    return FileUtil.writeLines(newList, srcFileNameCn,
      StandardCharsets.UTF_8);
  }

  /**
   * 翻译脚本
   *
   * @param folderName
   */
  public static void process(String folderName, String fileName) {
//        String fileName = "script_dialog";
    String srcFileName = folderName + fileName + "_dialog.txt";
    List<String> stringList = CdFileUtil.readFileContent(srcFileName);

    List<String> newList = translate(folderName, stringList);

    String srcFileNameCn = folderName + fileName + "_dialog.cn.txt";
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameCn, newList);
  }

  private static @NotNull List<String> translate(String folderName,
    List<String> stringList) {
    String text = stringList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    System.out.println("text:  " + text);
    List<String> stringListCn = TranslatorTextUtil.translatorText(text);

    DialogSingleEntity dialogSingleEntity;
    List<DialogSingleEntity> dialogSingleEntityList = new ArrayList<>();
    List<String> newList = new ArrayList<>();
    for (int i = 0; i < stringListCn.size(); i++) {
      String temp = stringListCn.get(i);
      String[] arr = temp.split("\r\n");
      if (arr.length % 2 == 0) {
        for (int k = 0; k < arr.length; k++) {
          dialogSingleEntity = new DialogSingleEntity();
          dialogSingleEntity.setHostCn(
            replaceHostCn(arr[k].replaceAll(" ", "")));
          dialogSingleEntity.setContentCn(replaceContentCn(arr[k + 1]));
          dialogSingleEntityList.add(dialogSingleEntity);
          newList.add(dialogSingleEntity.getHostCn());
          newList.add(dialogSingleEntity.getContentCn());
          newList.add("");
          k++;
        }
      }
    }

    return newList;
  }

  /**
   * 优化句子
   */
  private static String replaceContentCn(String content) {
    String result = content;
//    result = result.replaceAll("抢", "罗伯");
    result = result.replaceAll(" 6 Minute English ", "六分钟英语");
    result = result.replaceAll("医 管 局", "哈哈");
    result = result.replaceAll("乔吉", "乔治");
    result = result.replaceAll("成语", "谚语");
    result = result.replaceAll(" Phil", "菲尔");
    result = result.replaceAll(" Beth", "贝丝");
    result = result.replaceAll("Beth，", "贝丝，");
    result = result.replaceAll("Sean，", "肖恩，");
    result = result.replaceAll("Rob", "罗伯");
    result = result.replaceAll("伟大！", "太好了！");
    result = result.replaceAll("右。", "好的。"); // Right应该翻译成好的，而不是右
    result = result.replaceAll("山 姆", "山姆");
    result = result.replaceAll("6分钟", "六分钟");
    result = result.replaceAll(" 6 分钟", "六分钟");
    result = result.replaceAll(";", "；");
    result = result.replaceAll("“拯救大象”（Save the Elephants）", "“拯救大象”");
    result = result.replaceAll("六分钟又到了",
      "六分钟时间又到了"); // 英国广播公司（BBC）
    result = result.replaceAll("英国广播公司（BBC）",
      "英国广播公司"); // 英国广播公司（BBC）
    result = result.replaceAll("——", " —— ");// ——

    return result;
  }

  /**
   * 优化句子
   */
  private static String replaceHostCn(String content) {
    String result = content;
    result = result.replaceAll("抢", "罗伯");

    return result;
  }


  /**
   * 优化句子
   *
   * @param folderName
   * @param arr
   * @param j
   */
  private static void upgradeTranslate(String folderName, String[] arr, int j,
    List<String> subtitleList) {
    if ("抢".equals(arr[j])) {
      arr[j] = "罗伯";
    }
    if ("山 姆".equals(arr[j])) {
      arr[j] = "山姆";
    }
    if ("井".equals(arr[j])) {
      arr[j] = "嗯";
    }
    if ("右".equals(arr[j])) {
      arr[j] = "是的";
    }

    if ("右！".equals(arr[j])) {
      arr[j] = "好的！";
    }

    // 医 管 局！
    // （Michael Collins） 去掉大括号及大括号内的内容
    arr[j] = removeEnContent(arr[j]);
    arr[j] = arr[j].replaceAll(" 6 Minute English ", "六分钟英语");
    arr[j] = arr[j].replaceAll("医 管 局", "哈哈");
    arr[j] = arr[j].replaceAll("乔吉", "乔治");
    arr[j] = arr[j].replaceAll("成语", "谚语");
    arr[j] = arr[j].replaceAll("Rob", "罗伯");
    arr[j] = arr[j].replaceAll("伟大！", "太好了！");
    arr[j] = arr[j].replaceAll("右。", "好的。"); // Right应该翻译成好的，而不是右
    arr[j] = arr[j].replaceAll("山 姆", "山姆");
    arr[j] = arr[j].replaceAll("6分钟", "六分钟");
    arr[j] = arr[j].replaceAll(" 6 分钟", "六分钟");
    arr[j] = arr[j].replaceAll(";", "；");
    arr[j] = arr[j].replaceAll("“拯救大象”（Save the Elephants）", "“拯救大象”");
    arr[j] = arr[j].replaceAll("六分钟又到了",
      "六分钟时间又到了"); // 英国广播公司（BBC）
    arr[j] = arr[j].replaceAll("英国广播公司（BBC）",
      "英国广播公司"); // 英国广播公司（BBC）

    arr[j] = arr[j].replaceAll("——", " —— ");// ——
    if (-1 != arr[j].lastIndexOf("程序") && -1 != subtitleList.get(j)
      .lastIndexOf("programme")) {
      arr[j] = arr[j].replaceAll("程序", "节目");
    }
    if (-1 != arr[j].lastIndexOf("课程") && -1 != subtitleList.get(j)
      .lastIndexOf("programme")) {
      arr[j] = arr[j].replaceAll("课程", "节目");
    }

    // 维克托
    arr[j] = arr[j].replaceAll("Victor;", "维克多");

    arr[j] = arr[j].trim();//去掉前后空格
    if ("231026".equals(folderName)) {
      arr[j] = arr[j].replaceAll("一百个", "一百岁");
    }

    if ("231109".equals(folderName)) {
      if (arr[j].equals("幸运")) {
        arr[j] = arr[j].replaceAll("幸运", "幸运的是");
      }
    }

    // 针对230413的AI翻译优化
    if ("230413".equals(folderName)) {
      arr[j] = arr[j].replaceAll("垃圾场", "情绪低落");
      arr[j] = arr[j].replaceAll("垃圾堆", "情绪低落");
      arr[j] = arr[j].replaceAll("最尖锐的", "极度");
      arr[j] = arr[j].replaceAll(" Covid", "冠状病毒");
      arr[j] = arr[j].replaceAll("Covid", "冠状病毒");
      arr[j] = arr[j].replaceAll("英国广播公司（BBC）", "BBC");//英国广播公司（BBC）
      arr[j] = arr[j].replaceAll("《纪录片》（The Documentary）",
        "《纪录片》");//《纪录片》（The Documentary）
      arr[j] = arr[j].replaceAll("海伦·罗素（Helen Russell）",
        "海伦·罗素");// 海伦·罗素（Helen Russell）
      arr[j] = arr[j].replaceAll("托马斯·迪克森（Thomas Dixon）",
        "托马斯·迪克森");// 托马斯·迪克森（Thomas Dixon）
    }
    // 针对230302的AI翻译优化
    if ("230302".equals(folderName)) {
      arr[j] = arr[j].replaceAll("Rob", "'dunk'");
      arr[j] = arr[j].replaceAll("扣篮", "'dunk'");
      arr[j] = arr[j].replaceAll("英国广播公司（BBC）", "BBC");//英国广播公司（BBC）
      arr[j] = arr[j].replaceAll("迈克尔·罗森（Michael Rosen）", "迈克尔·罗森");
      arr[j] = arr[j].replaceAll("朱莉·塞迪维（Julie Sedivy）", "朱莉·塞迪维");
      arr[j] = arr[j].replaceAll("计划", "节目");
    }

    //
    // 针对230330的AI翻译优化
    if ("230330".equals(folderName)) {
      arr[j] = arr[j].replaceAll("历克斯·米尔克（Alex Mielke）", "历克斯·米尔克");
    }
  }

  /**
   * 优化句子
   *
   * @param arr
   * @param j
   */
  private static void upgradeTranslate(String[] arr, int j,
    List<String> subtitleList) {
    if ("抢".equals(arr[j])) {
      arr[j] = "罗伯";
    }
    if ("山 姆".equals(arr[j])) {
      arr[j] = "山姆";
    }
    if ("井".equals(arr[j])) {
      arr[j] = "嗯";
    }
    if ("右".equals(arr[j])) {
      arr[j] = "是的";
    }

    if ("右！".equals(arr[j])) {
      arr[j] = "好的！";
    }

    // 医 管 局！
    // （Michael Collins） 去掉大括号及大括号内的内容
    arr[j] = removeEnContent(arr[j]);
    arr[j] = arr[j].replaceAll(" 6 Minute English ", "六分钟英语");
    arr[j] = arr[j].replaceAll("医 管 局", "哈哈");
    arr[j] = arr[j].replaceAll("乔吉", "乔治");
    arr[j] = arr[j].replaceAll("成语", "谚语");
    arr[j] = arr[j].replaceAll("Rob", "罗伯");
    arr[j] = arr[j].replaceAll("伟大！", "太好了！");
    arr[j] = arr[j].replaceAll("右。", "好的。"); // Right应该翻译成好的，而不是右
    arr[j] = arr[j].replaceAll("山 姆", "山姆");
    arr[j] = arr[j].replaceAll("6分钟", "六分钟");
    arr[j] = arr[j].replaceAll(" 6 分钟", "六分钟");
    arr[j] = arr[j].replaceAll(";", "；");
    arr[j] = arr[j].replaceAll("“拯救大象”（Save the Elephants）", "“拯救大象”");
    arr[j] = arr[j].replaceAll("六分钟又到了",
      "六分钟时间又到了"); // 英国广播公司（BBC）
    arr[j] = arr[j].replaceAll("英国广播公司（BBC）",
      "英国广播公司"); // 英国广播公司（BBC）

    arr[j] = arr[j].replaceAll("——", " —— ");// ——
    if (-1 != arr[j].lastIndexOf("程序") && -1 != subtitleList.get(j)
      .lastIndexOf("programme")) {
      arr[j] = arr[j].replaceAll("程序", "节目");
    }
    if (-1 != arr[j].lastIndexOf("课程") && -1 != subtitleList.get(j)
      .lastIndexOf("programme")) {
      arr[j] = arr[j].replaceAll("课程", "节目");
    }

    // 维克托
    arr[j] = arr[j].replaceAll("Victor;", "维克多");

    arr[j] = arr[j].trim();//去掉前后空格
    arr[j] = arr[j].replaceAll("一百个", "一百岁");

    if (arr[j].equals("幸运")) {
      arr[j] = arr[j].replaceAll("幸运", "幸运的是");
    }

    // 针对230413的AI翻译优化
    arr[j] = arr[j].replaceAll("垃圾场", "情绪低落");
    arr[j] = arr[j].replaceAll("垃圾堆", "情绪低落");
    arr[j] = arr[j].replaceAll("最尖锐的", "极度");
    arr[j] = arr[j].replaceAll(" Covid", "冠状病毒");
    arr[j] = arr[j].replaceAll("Covid", "冠状病毒");
    arr[j] = arr[j].replaceAll("英国广播公司（BBC）", "BBC");//英国广播公司（BBC）
    arr[j] = arr[j].replaceAll("《纪录片》（The Documentary）",
      "《纪录片》");//《纪录片》（The Documentary）
    arr[j] = arr[j].replaceAll("海伦·罗素（Helen Russell）",
      "海伦·罗素");// 海伦·罗素（Helen Russell）
    arr[j] = arr[j].replaceAll("托马斯·迪克森（Thomas Dixon）",
      "托马斯·迪克森");// 托马斯·迪克森（Thomas Dixon）
    // 针对230302的AI翻译优化
    arr[j] = arr[j].replaceAll("Rob", "'dunk'");
    arr[j] = arr[j].replaceAll("扣篮", "'dunk'");
    arr[j] = arr[j].replaceAll("英国广播公司（BBC）", "BBC");//英国广播公司（BBC）
    arr[j] = arr[j].replaceAll("迈克尔·罗森（Michael Rosen）", "迈克尔·罗森");
    arr[j] = arr[j].replaceAll("朱莉·塞迪维（Julie Sedivy）", "朱莉·塞迪维");
    arr[j] = arr[j].replaceAll("计划", "节目");

    //
    // 针对230330的AI翻译优化
    arr[j] = arr[j].replaceAll("历克斯·米尔克（Alex Mielke）", "历克斯·米尔克");
  }

  public static void translateEngSrc(String folderName) {
    String fileName = "eng";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".srt");
    // readSrcFileContent

    List<SubtitleEntity> SubtitleEntityList = CdFileUtil.readSrcFileContent(
      srcFileName);

    List<String> subtitleList = SubtitleEntityList.stream()
      .map(SubtitleEntity::getSubtitle)
      .collect(Collectors.toList());

    String text = subtitleList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListCn = TranslatorTextUtil.translatorText(text);

    List<String> newList = new ArrayList<>();
    List<String> newListEnCn = new ArrayList<>();
    List<String> lrcListEnCn = new ArrayList<>();
    SubtitleEntity SubtitleEntity;
    String timeStr;
    String lrc;
    for (int i = 0; i < stringListCn.size(); i++) {
      String temp = stringListCn.get(i);
      String[] arr = temp.split("\r\n");
      // 检查大小
      if (arr.length != SubtitleEntityList.size()) {
        System.out.println("###");
        break;
      }
      for (int j = 0; j < arr.length; j++) {
        // 优化翻译
        upgradeTranslate(folderName, arr, j, subtitleList);

        System.out.println(arr[j]);
        SubtitleEntity = SubtitleEntityList.get(j);
        newList.add(SubtitleEntity.getSubIndex() + "");
        newList.add(SubtitleEntity.getTimeStr());
        newList.add(arr[j]);
        newList.add("");

        newListEnCn.add(SubtitleEntity.getSubIndex() + "");
        newListEnCn.add(SubtitleEntity.getTimeStr());
        newListEnCn.add(subtitleList.get(j) + "\r" + arr[j]);
        newListEnCn.add("");
        timeStr = SubtitleEntity.getTimeStr();
        timeStr = timeStr.substring(3, 11);
        timeStr = timeStr.replaceAll(",", ".");
        lrc = "[" + timeStr + "]" + subtitleList.get(j) + "|" + arr[j];
        lrcListEnCn.add(lrc);
      }
    }

    String srcFileNameCn = CommonUtil.getFullPathFileName(folderName, "chn",
      ".srt");
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameCn, newList);

//        // 双语字幕
//        String srcFileNameEnCn = CommonUtil.getFullPathFileName(folderName, "audio5", ".srt");
//        // 写双语歌词文本
//         CdFileUtil.writeToFile(srcFileNameEnCn, newListEnCn);
//
//        // 双语歌词
//        String lrcFileNameEnCn = CommonUtil.getFullPathFileName(folderName, "audio5", ".lrc");
//        // 写双语歌词文本
//         CdFileUtil.writeToFile(lrcFileNameEnCn, lrcListEnCn);
  }

  public static void translateEngSrc(String folderName, String fileName) {
    //String fileName = "eng";
//        String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName, ".srt");
    String srcFileName = folderName + fileName + ".eng" + ".srt";
    // readSrcFileContent

    List<SubtitleEntity> SubtitleEntityList = CdFileUtil.readSrcFileContent(
      srcFileName);

    List<String> subtitleList = SubtitleEntityList.stream()
      .map(SubtitleEntity::getSubtitle)
      .collect(Collectors.toList());

    String text = subtitleList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListCn = TranslatorTextUtil.translatorText(text);

    List<String> newList = new ArrayList<>();
    List<String> newListEnCn = new ArrayList<>();
    List<String> lrcListEnCn = new ArrayList<>();
    SubtitleEntity SubtitleEntity;
    String timeStr;
    String lrc;
    for (int i = 0; i < stringListCn.size(); i++) {
      String temp = stringListCn.get(i);
      String[] arr = temp.split("\r\n");
      // 检查大小
      if (arr.length != SubtitleEntityList.size()) {
        System.out.println("###");
        break;
      }
      for (int j = 0; j < arr.length; j++) {
        // 优化翻译
        upgradeTranslate(folderName, arr, j, subtitleList);

        System.out.println(arr[j]);
        SubtitleEntity = SubtitleEntityList.get(j);
        newList.add(SubtitleEntity.getSubIndex() + "");
        newList.add(SubtitleEntity.getTimeStr());
        newList.add(arr[j]);
        newList.add("");

        newListEnCn.add(SubtitleEntity.getSubIndex() + "");
        newListEnCn.add(SubtitleEntity.getTimeStr());
        newListEnCn.add(subtitleList.get(j) + "\r" + arr[j]);
        newListEnCn.add("");
        timeStr = SubtitleEntity.getTimeStr();
        timeStr = timeStr.substring(3, 11);
        timeStr = timeStr.replaceAll(",", ".");
        lrc = "[" + timeStr + "]" + subtitleList.get(j) + "|" + arr[j];
        lrcListEnCn.add(lrc);
      }
    }

    String srcFileNameCn = folderName + fileName + ".chn" + ".srt";
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameCn, newList);

  }

  public static void translateSrc(String srcFileNameEng,
    String srcFileNameChn) {
    //String fileName = "eng";
//        String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName, ".srt");
//    String srcFileName = folderName + fileName + ".eng" + ".srt";
    // readSrcFileContent

    List<SubtitleEntity> SubtitleEntityList = CdFileUtil.readSrcFileContent(
      srcFileNameEng);

    List<String> subtitleList = SubtitleEntityList.stream()
      .map(SubtitleEntity::getSubtitle)
      .collect(Collectors.toList());

//    String text = subtitleList.stream().map(String::valueOf)
//      .collect(Collectors.joining("\r\n"));
//    List<String> stringListCn = TranslatorTextUtil.translatorText(text);
    List<String> stringListCn = new ArrayList<>();
    // 按200行分割字符串数组
    List<List<String>> stringListEnGroup = ListUtil.split(subtitleList, 200);
    log.info("stringListEnGroup size: {}", stringListEnGroup.size());
    for (List<String> stringListEn : stringListEnGroup) {
      String text = stringListEn.stream().map(String::valueOf)
        .collect(Collectors.joining("\r\n"));
      stringListCn.addAll(TranslatorTextUtil.translatorText(text));
      ThreadUtil.sleep(2000);
      log.info("TranslatorTextUtil.translatorText(text) size: {}",
        stringListCn.size());
    }

    List<String> newList = new ArrayList<>();
    List<String> newListEnCn = new ArrayList<>();
    List<String> lrcListEnCn = new ArrayList<>();
    SubtitleEntity SubtitleEntity;
    String timeStr;
    String lrc;
    for (int i = 0; i < stringListCn.size(); i++) {
      String temp = stringListCn.get(i);
      String[] arr = temp.split("\r\n");
      // 检查大小
      if (arr.length != SubtitleEntityList.size()) {
        System.out.println("###");
        break;
      }
      for (int j = 0; j < arr.length; j++) {
        // 优化翻译
        upgradeTranslate(arr, j, subtitleList);

        System.out.println(arr[j]);
        SubtitleEntity = SubtitleEntityList.get(j);
        newList.add(SubtitleEntity.getSubIndex() + "");
        newList.add(SubtitleEntity.getTimeStr());
        newList.add(arr[j]);
        newList.add("");

        newListEnCn.add(SubtitleEntity.getSubIndex() + "");
        newListEnCn.add(SubtitleEntity.getTimeStr());
        newListEnCn.add(subtitleList.get(j) + "\r" + arr[j]);
        newListEnCn.add("");
        timeStr = SubtitleEntity.getTimeStr();
        timeStr = timeStr.substring(3, 11);
        timeStr = timeStr.replaceAll(",", ".");
        lrc = "[" + timeStr + "]" + subtitleList.get(j) + "|" + arr[j];
        lrcListEnCn.add(lrc);
      }
    }

//    String srcFileNameCn = folderName + fileName + ".chn" + ".srt";
    if (CollectionUtil.isNotEmpty(newList)) {
      // 写中文翻译文本
      CdFileUtil.writeToFile(srcFileNameChn, newList);

    } else {
      System.out.println("newList is empty!");
    }
  }

  public static void translateSrcWithGemini(String srcFileNameEng,
    String srcFileNameChn) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<SubtitleEntity> responseList = new ArrayList<>();
    List<SubtitleEntity> enSubtitleEntityList = CdFileUtil.readSrcFileContent(
      srcFileNameEng);

    List<String> subtitleList = enSubtitleEntityList.stream()
      .map(SubtitleEntity::getSubtitle)
      .toList();
    List<List<String>> stringListEnGroup = ListUtil.split(subtitleList, 200);
    for (List<String> stringListEn : stringListEnGroup) {
      StringBuilder text = new StringBuilder(CdConstants.SRC_TRANSLATE_PREFIX);
      text.append(stringListEn.stream().map(String::valueOf)
        .collect(Collectors.joining("\r\n")));
      text.append(" ");  // 添加空格分隔不同的文本块，避免一次性发送过多内容导致请求失败
      responseList.addAll(retryGetResponseList(stringListEn, text));
    }

    SubtitleEntity enSubtitleEntity;
    List<String> chnSrtStringList = new ArrayList<>();
    if (CollectionUtil.isNotEmpty(responseList)
      && responseList.size() == enSubtitleEntityList.size()) {
      log.info("相等 size: {}",
        responseList.size());
      for (int i = 0; i < enSubtitleEntityList.size(); i++) {
        enSubtitleEntity = enSubtitleEntityList.get(i);
        chnSrtStringList.add(enSubtitleEntity.getSubIndex() + "");
        chnSrtStringList.add(enSubtitleEntity.getTimeStr());
        chnSrtStringList.add(responseList.get(i).getSubtitleSecond());
        chnSrtStringList.add("");
      }
    } else {
      log.error("返回结果: {}, 期待结果: {}",
        responseList.size(), enSubtitleEntityList.size());
    }

    if (CollectionUtil.isNotEmpty(chnSrtStringList)) {
      // 写中文翻译文本
      CdFileUtil.writeToFile(srcFileNameChn, chnSrtStringList);
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时
      log.info("写入完成，文件路径: {}，共计耗时：{}", srcFileNameChn,
        CdTimeUtil.formatDuration(elapsedTime));
    } else {
      System.out.println("newList is empty!");
    }
  }

  private static @NotNull List<SubtitleEntity> retryGetResponseList(
    List<String> stringListEn,
    StringBuilder text) {
    int retryTimes = 10;
    List<SubtitleEntity> responseList = getResponseList(stringListEn, text);
    if (CollectionUtil.isEmpty(responseList)) {
      for (int i = 0; i < retryTimes; i++) {
        log.info("开始重试第{}次", i + 1);
        responseList = getResponseList(stringListEn, text);
        if (CollectionUtil.isNotEmpty(responseList)) {
          log.info("重试成功，第{}次", i + 1);
          break;
        }
      }
    }
    return responseList;
  }

  private static @NotNull List<SubtitleEntity> getResponseList(
    List<String> stringListEn, StringBuilder text) {
    String response = GeminiApiClient.generateContent(text.toString());
    // 将两个回车换行替换成一个
    response = response.replaceAll("\n\n", "\n");
    // 解析成字符串数组，并以行为单位拆分字符串数组
    List<String> responseList = new ArrayList<>(
      Arrays.asList(response.split("\n")));
    List<SubtitleEntity> subtitleEntityList = CdFileUtil.getSubtitleEntityListNoIndexAndTimeStr(
      responseList);
    if (stringListEn.size() != subtitleEntityList.size()) {
      log.error("返回结果不一致，期待值：{}，实际值：{}, \r\n，返回内容为：{}",
        stringListEn.size(),
        subtitleEntityList.size(), response);
    } else {
      log.error("返回结果一致，期待值：{}，实际值：{}, \r\n，返回内容为：{}",
        stringListEn.size(),
        subtitleEntityList.size(), response);
      ThreadUtil.sleep(50);
    }
    return subtitleEntityList;
  }

  /**
   * 生成对话脚本合集
   *
   * @param folderName
   */
  public static void mergeScriptContent(String folderName) {
    String fileName = "script_dialog";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<DialogSingleEntity> dialogSingleEntityListEn = CdFileUtil.genDialogSingleEntityList(
      srcFileName);

    String srcFileNameCn = CommonUtil.getFullPathFileName(folderName, fileName,
      "_cn.txt");

    List<String> newList = mergeContent(srcFileNameCn,
      dialogSingleEntityListEn);

    String srcFileNameMerge = CommonUtil.getFullPathFileName(folderName,
      folderName, "_中英双语对话脚本.txt");
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameMerge, newList);
  }

  /**
   * 生成对话脚本合集
   *
   */
//  public static File mergeScriptContent(String srcFileName,String srcFileNameCn, String srcFileNameMerge) {
//
//    List<DialogSingleEntity> dialogSingleEntityListEn = CdFileUtil.genDialogSingleEntityList(
//      srcFileName);
//
//
//    List<String> newList = mergeContent(srcFileNameCn,      dialogSingleEntityListEn);
//
//    // 写中文翻译文本
//    CdFileUtil.writeToFile(srcFileNameMerge, newList);
//
//    return new File(srcFileNameMerge);
//  }

  /**
   * 生成对话脚本合集
   */
  public static File mergeScriptContent(String scriptDialogFileName,
    String scriptDialogCnFileName, String scriptDialogMergeFileName) {

    List<DialogSingleEntity> dialogSingleEntityListEn = CdFileUtil.genDialogSingleEntityList(
      scriptDialogFileName);

    List<String> newList = mergeContent(scriptDialogCnFileName,
      dialogSingleEntityListEn);

    // 写中文翻译文本
    CdFileUtil.writeToFile(scriptDialogMergeFileName, newList);
    return new File(scriptDialogMergeFileName);
  }

  private static @NotNull List<String> mergeContent(String srcFileNameCn,
    List<DialogSingleEntity> dialogSingleEntityListEn) {
    List<DialogSingleEntity> DialogSingleEntityListCn = CdFileUtil.genDialogSingleEntityList(
      srcFileNameCn);
    DialogSingleEntity dialogSingleEntityEn;
    DialogSingleEntity dialogSingleEntityCn;
    String scriptEn;
    String scriptCn;
    List<String> newList = new ArrayList<>();
    if (CollectionUtil.isNotEmpty(dialogSingleEntityListEn)
      && CollectionUtil.isNotEmpty(DialogSingleEntityListCn)
      && dialogSingleEntityListEn.size() == DialogSingleEntityListCn.size()) {
      for (int i = 0; i < dialogSingleEntityListEn.size(); i++) {
        dialogSingleEntityEn = dialogSingleEntityListEn.get(i);
        scriptEn = dialogSingleEntityEn.getContentEn();
        scriptEn = scriptEn.replaceAll(
          "Hello. This is 6 Minute English from BBC Learning English. ", "");
        dialogSingleEntityCn = DialogSingleEntityListCn.get(i);
        scriptCn = dialogSingleEntityCn.getContentEn();
        scriptCn = scriptCn.replaceAll("你好。这是来自BBC学习英语的六分钟英语。",
          "");
        newList.add(dialogSingleEntityEn.getHostEn() + "("
          + dialogSingleEntityCn.getHostEn() + ")");
        newList.add(scriptEn + "\r\n" + scriptCn);
        newList.add("");
      }
    } else {
      if (CollectionUtil.isEmpty(dialogSingleEntityListEn)) {
        System.out.println("dialogSingleEntityListEn 为空。");
      } else if (CollectionUtil.isEmpty(DialogSingleEntityListCn)) {
        System.out.println("DialogSingleEntityListCn 为空。");
      } else {
        System.out.println(
          "两个脚本格式不对，实体大小分别为：" + dialogSingleEntityListEn.size()
            + "\t:\t"
            + DialogSingleEntityListCn.size()); //
      }
    }
    return newList;
  }

  /**
   * 生成对话脚本合集
   *
   * @param folderName
   */
  public static void mergeScriptContentWx(String folderName) {
    String fileName = "script_dialog_wx2";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");

    List<String> stringList = CdFileUtil.readFileContent(srcFileName);

    String text = stringList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringListCn = TranslatorTextUtil.translatorText(text);

    List<String> newList = new ArrayList<>();
    List<String> newListEnCn = new ArrayList<>();
    List<String> lrcListEnCn = new ArrayList<>();
    SubtitleEntity SubtitleEntity;
    String timeStr;
    String lrc;

    for (int i = 0; i < stringListCn.size(); i++) {
      String temp = stringListCn.get(i);
      String[] arr = temp.split("\r\n");

      for (int j = 0; j < arr.length; j++) {

        upgradeTranslate(folderName, arr, j, stringList);

        System.out.println(arr[j]);
//                SubtitleEntity = SubtitleEntityList.get(j);
//                newList.add(SubtitleEntity.getSubIndex() + "");
//                newList.add(SubtitleEntity.getTimeStr());
//                newList.add(arr[j]);
//                newList.add("");

//                newListEnCn.add(SubtitleEntity.getSubIndex() + "");
//                newListEnCn.add(SubtitleEntity.getTimeStr());
        newListEnCn.add(stringList.get(j) + "\r" + arr[j]);
        newListEnCn.add("");

      }
    }

    String srcFileNameMerge = CommonUtil.getFullPathFileName(folderName,
      fileName, "_merge.txt");
    // 写中文翻译文本
    CdFileUtil.writeToFile(srcFileNameMerge, newListEnCn);
  }

  /**
   * 生成对话脚本合集
   */
//    public static void translateAllTitle() {
//        String fileName = BbcConstants.ROOT_FOLDER_NAME + "all.txt";
//        List<TopicEntity> topicEntityList =  CdFileUtil.genTopicEntityList(fileName);
//
////        List<String> stringList =  CdFileUtil.readFileContent(srcFileName);
//        List<String> textTopicStringList = topicEntityList.stream().map(TopicEntity::getTopic)
//            .collect(Collectors.toList());
//        List<String> stringTopicCn = translateStringList(textTopicStringList);
//
//        List<String> textDescriptionStringList = topicEntityList.stream().map(TopicEntity::getDescription)
//            .collect(Collectors.toList());
//        List<String> stringDescriptionCn = translateStringList(textDescriptionStringList);
//
//        List<String> newListEnCn = new ArrayList<>();
//        String titleStr;
//
//        if (CollectionUtil.isNotEmpty(stringTopicCn) && CollectionUtil.isNotEmpty(stringDescriptionCn)
//            && topicEntityList.size() == stringDescriptionCn.size()
//            && stringTopicCn.size() == stringDescriptionCn.size()) {
//
//            for (int i = 0; i < topicEntityList.size(); i++) {
//                titleStr = topicEntityList.get(i).getEpisode()
//                    + "\t" + topicEntityList.get(i).getTopic() + "|" + stringTopicCn.get(i)
//                    + "\t" + topicEntityList.get(i).getDescription() + "|" + stringDescriptionCn.get(i);
//                newListEnCn.add(titleStr);
//            }
//
//        }
//
//        String fileNameCn = BbcConstants.ROOT_FOLDER_NAME + "all_title_cn.txt";
//        // 写中文翻译文本
//         CdFileUtil.writeToFile(fileNameCn, newListEnCn);
//    }

  /**
   * 翻译字符串数组
   *
   * @param textTopicStringList
   * @return
   */
  public static List<String> translateStringList(
    List<String> textTopicStringList) {

    List<String> result = new ArrayList<>();
    String textTopic = textTopicStringList.stream().map(String::valueOf)
      .collect(Collectors.joining("\r\n"));
    List<String> stringList = TranslatorTextUtil.translatorText(textTopic);

    for (int i = 0; i < stringList.size(); i++) {
      String temp = stringList.get(i);
      String[] arr = temp.split("\r\n");
      result = Arrays.asList(arr);
    }

    return result;
  }

}
