package com.coderdream.util.bbc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.util.BbcConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.sentence.StanfordSentenceSplitter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 字幕初剪
 *
 * @author CoderDream
 */
@Slf4j
public class GenSrtUtil {


  public static void main(String[] args) {

//        ep230907();
    String folderName = "181115";

    List<String> stringList = GenSrtUtil.processScriptDialog(folderName);
    System.out.println("##########################");
    for (int i = 0; i < stringList.size(); i++) {
      System.out.println(
        "#" + i + "\t:length: " + stringList.get(i).length() + "\t\t: "
          + stringList.get(i));
    }
  }


  /**
   * 缩写形式列表
   *
   * @return
   */
//    public static Map<String, String> genAbbrevCompleteMap() {
//        String filePath = BbcConstants.ROOT_FOLDER_NAME + FOLDER_NAME + "abbreviation.xlsx";
//
//        Map<String, String> stringMap = CdExcelUtil.genAbbrevCompleteMap(filePath);
////        System.out.println(hostList.stream().map(String::valueOf).collect(Collectors.joining(",")));
//
//        return stringMap;
//    }
  public static List<String> getScripts(String folderName, String fileName) {
//        Set<String> hostSet = new TreeSet<>();
//        hostSet.addAll(getHost());
//    if (StrUtil.isEmpty(folderName)) {
//      folderName = FOLDER_NAME;
//    }

    if (StrUtil.isEmpty(fileName)) {
      fileName = "script.txt";
    }

    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt"); //
    List<String> scriptList = new ArrayList<>();
    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (StrUtil.isNotEmpty(line)) {
//                    System.out.println("###:  " + line);
          scriptList.add(line);
//                    scriptList.addAll(BreakUpToSentence.splitSentence(line));
        }
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return scriptList;
  }

  public static List<String> getScripts(String filePath) {
//        Set<String> hostSet = new TreeSet<>();
//        hostSet.addAll(getHost());
//        String filePath = ROOT_FOLDER_NAME + FOLDER_NAME + "script.txt";
    List<String> scriptList = new ArrayList<>();
    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (StrUtil.isNotEmpty(line)) {
//                    System.out.println("###:  " + line);
          scriptList.add(line);
//                    scriptList.addAll(BreakUpToSentence.splitSentence(line));
        }
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return scriptList;
  }


  private static void processCommaScript(String str, List<String> scriptList) {
    String tempStr;
    // 根据逗号分割
    String[] commaArr = str.split(",");
    for (int j = 0; j < commaArr.length; j++) {
      tempStr = commaArr[j];
      if (j != commaArr.length - 1) {
        scriptList.add(tempStr + ",");
      } else {
        scriptList.add(tempStr);
      }
    }
  }

  /**
   * @param folderName
   * @return
   */
  public static List<String> processScriptDialog(String folderName) {
    String fileName = "script_dialog";
    // 写文件
    String newFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      "_new.txt");
    if (!CdFileUtil.isFileEmpty(newFileName)) {
      log.info("初剪脚本文件已经存在： {} ", newFileName);
      return new ArrayList<>();
    }

//        Set<String> hostSet = new TreeSet<>();
//        hostSet.addAll(getHost());
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> scriptList = new ArrayList<>();

    if (folderName.startsWith("22") || folderName.startsWith("21")
      || folderName.startsWith("20") || folderName.startsWith("19")
      || folderName.startsWith("18")) {
      scriptList.add("This is a download from bbc learning English.");
      scriptList.add("To find out more, visit our website.");
    }

    scriptList.add("6 Minute English from bbc learningenglish.com");
    try {
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
      List<DialogSingleEntity> stringList2 = CdFileUtil.genDialogSingleEntityList(
        filePath);

      assert stringList2 != null;
      List<String> enSentences = null;
      List<String> splitSentence = null;
      for (DialogSingleEntity line : stringList2) {
        String contentEn = line.getContentEn();
        // Welcome to 6 Minute English, I'm Neil.
        contentEn = contentEn.replace("Welcome to 6 Minute English, I",
          "Welcome to 6 Minute English. I");
        // Hello and welcome to 6 Minute English, I
        contentEn = contentEn.replace(
          "Hello and welcome to 6 Minute English, I",
          "Hello and welcome to 6 Minute English. I");
        contentEn = contentEn.replace("a: ", "a) ");
        contentEn = contentEn.replace("b: ", "b) ");
        contentEn = contentEn.replace("c: ", "c) ");

        //  - 替换为句号
        contentEn = contentEn.replace(" -", ".");
        enSentences = StanfordSentenceSplitter.splitSentences(
          contentEn);
        splitSentence = new ArrayList<>();
        for (String sentence1 : enSentences) {
          // 分号和减号分割
          splitSentence.addAll(SentenceSplitter.splitSentence(sentence1));
        }

        for (String sentence2 : splitSentence) {
          // 自定义分割
          List<String> strings22 = StringSplitter4.splitString(sentence2);
          log.info("{} strings22: {} ", sentence2, strings22);
          scriptList.addAll(strings22);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 6 Minute English
    //from bbc learningenglish.com
    scriptList.add("6 Minute English from bbc learningenglish.com");

    for (String tempStr : scriptList) {
      if (tempStr.length() > BbcConstants.MAX_SINGLE_LINE_LENGTH) {
        System.out.println(tempStr.length() + ":" + tempStr);
      }
    }

    CdFileUtil.writeToFile(newFileName, scriptList);

    return scriptList;
  }


  /**
   * @return
   */
  public static File genScriptDialogNewV2(String newFileName, String fileName) {

    List<String> scriptList = new ArrayList<>();

    try {
      List<String> stringList2 = FileUtil.readLines(fileName,
        StandardCharsets.UTF_8);

      assert stringList2 != null;
      List<String> enSentences = null;
      List<String> splitSentence = null;
      for (String contentEn : stringList2) {
        // Welcome to 6 Minute English, I'm Neil.

        //  - 替换为句号
        contentEn = contentEn.replace(" -", ".");
        enSentences = StanfordSentenceSplitter.splitSentences(
          contentEn);
        splitSentence = new ArrayList<>();
        for (String sentence1 : enSentences) {
          // 分号和减号分割
          splitSentence.addAll(SentenceSplitter.splitSentence(sentence1));
        }

        for (String sentence2 : splitSentence) {
          // 自定义分割
          List<String> strings22 = StringSplitter4.splitString(sentence2);
          log.info("{} strings22: {} ", sentence2, strings22);
          scriptList.addAll(strings22);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 6 Minute English
    //from bbc learningenglish.com
    scriptList.add("6 Minute English from bbc learningenglish.com");

    for (String tempStr : scriptList) {
      if (tempStr.length() > BbcConstants.MAX_SINGLE_LINE_LENGTH) {
        System.out.println(tempStr.length() + ":" + tempStr);
      }
    }

    CdFileUtil.writeToFile(newFileName, scriptList);

    return new File(newFileName);
  }

  /**
   * @param folderName 如：2201
   * @return 如：2201_script_dialog.txt
   */
  public static File genScriptDialogNew(String folderName, String newFileName) {
    String fileName = "script_dialog";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> scriptList = new ArrayList<>();

    if (folderName.startsWith("22") || folderName.startsWith("21")
      || folderName.startsWith("20") || folderName.startsWith("19")
      || folderName.startsWith("18") || folderName.startsWith("17")
      || folderName.startsWith("16") || folderName.startsWith("15")
      || folderName.startsWith("14")) {
      scriptList.add("This is a download from bbc learning English.");
      scriptList.add("To find out more, visit our website.");
    }

    // this is a download from BBC Learning English
    //to find out more visit our website
    //6 Minute English from bbclearningenglish.com
    //hello and welcome to 6 Minute English I'm Neil

    scriptList.add("6 Minute English from bbc learningenglish.com");
    try {
      List<DialogSingleEntity> stringList2 = CdFileUtil.genDialogSingleEntityList(
        filePath);

      assert stringList2 != null;
      List<String> enSentences = null;
      List<String> splitSentence = null;
      for (DialogSingleEntity line : stringList2) {
        String contentEn = line.getContentEn();
        // Welcome to 6 Minute English, I'm Neil.
        contentEn = contentEn.replace("Welcome to 6 Minute English, I",
          "Welcome to 6 Minute English. I");
        // Hello and welcome to 6 Minute English, I
        contentEn = contentEn.replace(
          "Hello and welcome to 6 Minute English, I",
          "Hello and welcome to 6 Minute English. I");
        contentEn = contentEn.replace("a: ", "a) ");
        contentEn = contentEn.replace("b: ", "b) ");
        contentEn = contentEn.replace("c: ", "c) ");

        //  - 替换为句号
        contentEn = contentEn.replace(" -", ".");
        enSentences = StanfordSentenceSplitter.splitSentences(
          contentEn);
        splitSentence = new ArrayList<>();
        for (String sentence1 : enSentences) {
          // 分号和减号分割
          splitSentence.addAll(SentenceSplitter.splitSentence(sentence1));
        }

        for (String sentence2 : splitSentence) {
          // 自定义分割
          List<String> strings22 = StringSplitter4.splitString(sentence2);
          log.info("{} strings22: {} ", sentence2, strings22);
          scriptList.addAll(strings22);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 6 Minute English
    //from bbc learningenglish.com
    scriptList.add("6 Minute English from bbc learningenglish.com");

    for (String tempStr : scriptList) {
      if (tempStr.length() > BbcConstants.MAX_SINGLE_LINE_LENGTH) {
        System.out.println(tempStr.length() + ":" + tempStr);
      }
    }

    CdFileUtil.writeToFile(newFileName, scriptList);

    return new File(newFileName);
  }

  public static Integer SENTENCE_LENGTH = 65;

  public List<String> processSentence(List<String> sentenceList) {

    List<String> scriptList = new ArrayList<>();
    for (String sentence : sentenceList) {
      processSentence(scriptList, sentence);
    }

    return scriptList;
  }

  /**
   * 处理单行
   *
   * @param scriptList
   * @param splitStr
   */
  private static void processSentence(List<String> scriptList,
    String splitStr) {
    // 如果单行字符超过某个阈值，则需要分割
    if (splitStr.length() > BbcConstants.MAX_SINGLE_LINE_LENGTH) {
//            List<String> splitSentence = BreakUpToSentence.splitSentence(splitStr);
      String[] strings = splitStr.split(",");
      if (strings != null && strings.length > 0) {
        int len = strings.length;

        // 分段处理批量短句，
        List<String> subList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
          // 去掉左边空格
          strings[i] = StringUtils.stripStart(strings[i], null);
          if (i == len - 1) {
            processSubSentence(subList, strings[i]);
          } else {
            processSubSentence(subList, strings[i] + ", ");
          }
        }

        // 如果前后两句够短就拼接
        // for () TODO

        List<String> mergeSubSentence = mergeSubSentence(subList);

        scriptList.addAll(mergeSubSentence);
      } else {
        processSubSentence(scriptList, splitStr);
      }
    }
    // 否则直接处理
    else {
      scriptList.add(splitStr);
    }
  }

  private static void processSubSentence(List<String> scriptList,
    String splitStr) {
    // 如果单行字符超过某个阈值，则需要分割
    if (splitStr.length() > BbcConstants.MAX_SINGLE_LINE_LENGTH) {
//            List<String> splitSentence = BreakUpToSentence.splitSentence(splitStr);
      String[] strings = splitStr.split(" ");
      List<String> splitSentence = Arrays.asList(strings);
      if (CollectionUtil.isNotEmpty(splitSentence)) {
        scriptList.addAll(processString(splitSentence));
      } else {
        System.out.println(
          "##### splitSentence ERROR: " + splitStr + " length: "
            + splitStr.length());
      }
    }
    // 否则直接处理
    else {
      scriptList.add(splitStr);
    }
  }

  public static void checkSentenceLength(String folderName, String fileName) {
    String newFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      "_new.txt");
    List<String> scriptList = CdFileUtil.readFileContent(newFileName);
    for (String tempStr : scriptList) {
      if (tempStr.length() > 60) {
        System.out.println(tempStr.length() + ":" + tempStr);
      } else if (tempStr.length() < 2) {
        System.out.println(tempStr.length() + ":" + tempStr);
      }
    }
  }


  public static void processInteger(List<Integer> integerList) {
    int MAX = 55;
    // 如果只有一个元素，直接返回
    if (integerList.size() == 1) {
      System.out.println(integerList.get(0));
    } else {
      int sum;
      // 先将第一个元素保存到临时变量
      int temp = integerList.get(0);
      for (int i = 1; i < integerList.size(); i++) {
        // 累加循环得到的变量
        sum = temp + integerList.get(i);
        // 如果没有超过阈值，则继续
        if (sum < MAX) {
          // 修改临时变量
          temp = sum;
        } else {
          // 已经超过阈值，则输出临时变量（累加结束）
          System.out.println(temp);
          // 将当前元素赋值给临时变量
          temp = integerList.get(i);
        }
        // 如果没有元素了，则直接输出最后得到的结果
        if (i == integerList.size() - 1) {
          System.out.println(temp);
        }
      }
    }
  }

  /**
   * 输出合并后的字符串列表，字符串长度不大于某个阈值
   *
   * @param stringList 输入字符串列表
   * @return 输入字符串列表
   */
  public static List<String> processString(List<String> stringList) {
    List<String> result = new ArrayList<>();
    String tempString;
    // 如果只有一个元素，直接返回
    if (stringList.size() == 1) {
      System.out.println(stringList.get(0));
      tempString = stringList.get(0);
      result.add(tempString);
    } else {
      int sum;
      String sumStr;
      // 先将第一个元素保存到临时变量
      tempString = stringList.get(0);
      int temp = tempString.length();
      for (int i = 1; i < stringList.size(); i++) {
        // 累加循环得到的变量
        sum = temp + stringList.get(i).length();
        sumStr = tempString + " " + stringList.get(i);
        sum += 1;
        // 如果没有超过阈值，则继续  + 5
        if (sum <= BbcConstants.MAX_SINGLE_LINE_LENGTH + 5) {
          // 修改临时变量
          temp = sum;
          tempString = sumStr;
        } else {
          // 5. Apache Commons
          //
          //为此，我们首先添加的 lang3 依赖性：
          //
          //<dependency>
          //    <groupId>org.apache.commons</groupId>
          //    <artifactId>commons-lang3</artifactId>
          //    <version>3.11</version>
          //</dependency>
          //根据文档，我们使用null来去除空格：
//                    String ltrim = StringUtils.stripStart(src, null);
//                    String rtrim = StringUtils.stripEnd(src, null);
          // 已经超过阈值，则输出临时变量（累加结束）
//                    result.add(tempString.tr()); // TODO 去空格
          result.add(StringUtils.stripStart(tempString, null)); // TODO 去空格
          // 将当前元素赋值给临时变量
          temp = stringList.get(i).length();
          tempString = stringList.get(i);
        }
        // 如果没有元素了，则直接输出最后得到的结果
        if (i == stringList.size() - 1) {
          result.add(tempString.trim());
        }
      }
    }
    return result;
  }

  /**
   * 输出合并后的字符串列表，字符串长度不大于某个阈值
   *
   * @param stringList 输入字符串列表
   * @return 输入字符串列表
   */
  public static List<String> mergeSubSentence(List<String> stringList) {
    List<String> result = new ArrayList<>();
    String tempString;
    // 如果只有一个元素，直接返回
    if (stringList.size() == 1) {
      System.out.println(stringList.get(0));
      tempString = stringList.get(0);
      result.add(tempString);
    } else {
      int sum;
      String sumStr;
      // 先将第一个元素保存到临时变量
      tempString = stringList.get(0);
      int temp = tempString.length();
      for (int i = 1; i < stringList.size(); i++) {
        // 累加循环得到的变量
        sum = temp + stringList.get(i).length();
        sumStr = tempString + " " + stringList.get(i);
        sum += 1;
        // 如果没有超过阈值，则继续  + 5
        if (sum <= BbcConstants.MAX_SINGLE_LINE_LENGTH) {
          // 修改临时变量
          temp = sum;
          tempString = sumStr;
        } else {
          // 5. Apache Commons
          //
          //为此，我们首先添加的 lang3 依赖性：
          //
          //<dependency>
          //    <groupId>org.apache.commons</groupId>
          //    <artifactId>commons-lang3</artifactId>
          //    <version>3.11</version>
          //</dependency>
          //根据文档，我们使用null来去除空格：
//                    String ltrim = StringUtils.stripStart(src, null);
//                    String rtrim = StringUtils.stripEnd(src, null);
          // 已经超过阈值，则输出临时变量（累加结束）
//                    result.add(tempString.tr()); // TODO 去空格
          result.add(StringUtils.stripStart(tempString, null)); // TODO 去空格
          // 将当前元素赋值给临时变量
          temp = stringList.get(i).length();
          tempString = stringList.get(i);
        }
        // 如果没有元素了，则直接输出最后得到的结果
        if (i == stringList.size() - 1) {
          result.add(tempString.trim());
        }
      }
    }
    return result;
  }
}
