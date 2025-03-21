package com.coderdream.util.ppt;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.callapi.HttpUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @author CoderDream
 */
@Slf4j
public class DictUtils {

  public static void main(String[] args) {
//        String word = "sustainable";
//        List<String> wordList = Arrays.asList("genre", "upbeat", "jaunty", "gasp", "gobsmacked", "high five");
//        List<String> wordList = Arrays.asList("upbeat", "jaunty", "gasp", "gobsmacked", "high five");
//    List<String> wordList = Arrays.asList("spoil");
//    List<VocInfo> vocInfoList = DictUtils.queryWords(wordList);
//    for (VocInfo vocInfo : vocInfoList) {
//      System.out.println(vocInfo);
//    }

//        String folderName = "230428";
//        DictUtils.processVoc(folderName);

//        List<VocInfo> vocInfoList = DictUtils.getVocInfoList(folderName);
//        for (VocInfo vocInfo : vocInfoList) {
//            System.out.println(vocInfo);
//        }

//        String wordCn = "气候变化否定者：一种观点，认为全球气候变化是不存在的或者人类活动对气候变化的影响被夸大了。";
//        String wordExplainCn = "气候变化否定者：一种观点，认为全球气候变化是不存在的或者人类活动对气候变化的影响被夸大了。； · Climate deniers often argue that the scientific consensus on climate change is based on flawed data.； 气候变化否定者经常辩称，关于气候变化的科学共识是基于错误的数据的。";
//        //
//        int colonIndex = wordCn.indexOf("：");
//
//        int firstIndexOfIndex = wordExplainCn.indexOf("； · ");
//        int lastIndexOfIndex = wordExplainCn.lastIndexOf("； ");
//        wordCn = wordExplainCn.substring(0, colonIndex);
////        System.out.println(wordCn);
//
//        String sampleSentenceEn = wordExplainCn.substring(firstIndexOfIndex + 3, lastIndexOfIndex);
//        String sampleSentenceCn = wordExplainCn.substring(lastIndexOfIndex + 3);
//        wordExplainCn = wordExplainCn.substring(colonIndex + 1, firstIndexOfIndex);
//
//        System.out.println(wordCn);
//        System.out.println(wordExplainCn);
//        System.out.println(sampleSentenceEn.trim());
//        System.out.println(sampleSentenceCn);

//        String b = "网络自夸（在网络环境中对自己的情况有所隐瞒，通常精心编造一个优质的网络身份，目的是为了给他人留下深刻印象，尤其是为了吸引某人与其发展恋爱关系）";
//        b = "绒毛般的，覆有绒毛的；（食物等）松软的，透气的；轻软状的；<非正式>空洞的，不严肃的";
//        System.out.println(shortStr(b));

    String folderName = "D:\\14_LearnEnglish\\u11_frankenstein\\u11_frankenstein_episode1\\";
    String fileName = "u11_frankenstein_episode1_voc";
    DictUtils.processVoc(folderName, fileName);
  }

  /**
   * 根据英文词汇生成中英文词汇
   *
   * @param folderName 文件夹名称
   * @return List<VocInfo>
   */
  public static List<VocInfo> getVocInfoList(String folderName) {
    String fileName = "voc_cn";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        scriptList.add(line);
      }
      bufferedReader.close();
    } catch (Exception e) {
      log.error("生成中英文词汇出错", e);
      //e.printStackTrace();
    }

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList == null || scriptList.size() != 36) {
      System.out.println("E 判断是否为6个词汇，size：" + scriptList.size());
    } else {
      size = scriptList.size();
    }

    VocInfo vocInfo;
    for (int i = 0; i < size; i++) {
      if ((i + 1) % 6 == 0) {
        vocInfo = new VocInfo();
        vocInfo.setWord(scriptList.get(i - 5));
        vocInfo.setWordExplainEn(scriptList.get(i - 4));
        vocInfo.setWordCn(scriptList.get(i - 3));
        vocInfo.setWordExplainCn(scriptList.get(i - 2));
        vocInfo.setSampleSentenceEn(scriptList.get(i - 1));
        vocInfo.setSampleSentenceCn(scriptList.get(i));
        vocInfoList.add(vocInfo);
      }
    }
    return vocInfoList;
  }

  /**
   * 根据英文词汇生成中英文词汇
   *
   * @param folderName 文件夹名称
   * @return List<VocInfo>
   */
  public static List<VocInfo> getVocInfoList(String folderName,
    String fileName) {
    //  String fileName = "voc_cn"; //CommonUtil.getFullPathFileName(folderName, fileName,      ".txt");
    String filePath = folderName + fileName      + "_voc.txt";
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        scriptList.add(line);
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList == null || scriptList.size() != 36) {
      System.out.println("F 判断是否为6个词汇，size：" + scriptList.size());
    } else {
      size = scriptList.size();
    }

    VocInfo vocInfo;
    for (int i = 0; i < size; i++) {
      if ((i + 1) % 6 == 0) {
        vocInfo = new VocInfo();
        vocInfo.setWord(scriptList.get(i - 5));
        vocInfo.setWordExplainEn(scriptList.get(i - 4));
        vocInfo.setWordCn(scriptList.get(i - 3));
        vocInfo.setWordExplainCn(scriptList.get(i - 2));
        vocInfo.setSampleSentenceEn(scriptList.get(i - 1));
        vocInfo.setSampleSentenceCn(scriptList.get(i));
        vocInfoList.add(vocInfo);
      }
    }
    return vocInfoList;
  }

  /**
   * 根据英文词汇生成中英文词汇
   */
  public static List<VocInfo> writeVocCnExcel(String folderName) {
    String fileName = "voc_cn";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        scriptList.add(line);
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 写文件
    String newFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      "_cn_excel.txt");

    List<String> scriptListNew = new ArrayList<>();
    // 添加第一个空格
    scriptListNew.add("");

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList == null || scriptList.size() != 36) {
      System.out.println("G 判断是否为6个词汇，size：" + scriptList.size());
    } else {
      size = scriptList.size();
      String string;
      for (int i = 0; i < size; i++) {
        string = scriptList.get(i);
        scriptListNew.add(string);
        if ((i + 1) % 12 == 0) {
          scriptListNew.add("");
        }
      }
    }

    if (CollectionUtil.isNotEmpty(scriptListNew)) {
      CdFileUtil.writeToFile(newFileName, scriptListNew);
    } else {
      System.out.println("###### 空");
    }

    return vocInfoList;
  }

  /**
   * 根据英文词汇生成中英文词汇
   */
  public static List<VocInfo> writeVocCnExcel(String folderName,
    String fileName) {
//        String fileName = "voc_cn";
    String filePath = folderName + fileName + "_voc_cn.txt";
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        scriptList.add(line);
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 写文件
    String newFileName = folderName + fileName + "_cn_excel.txt";

    List<String> scriptListNew = new ArrayList<>();
    // 添加第一个空格
    scriptListNew.add("");

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList == null || scriptList.size() != 36) {
      System.out.println("H 判断是否为6个词汇，size：" + scriptList.size());
    } else {
      size = scriptList.size();
      String string;
      for (int i = 0; i < size; i++) {
        string = scriptList.get(i);
        scriptListNew.add(string);
        if ((i + 1) % 12 == 0) {
          scriptListNew.add("");
        }
      }
    }

    if (CollectionUtil.isNotEmpty(scriptListNew)) {
      CdFileUtil.writeToFile(newFileName, scriptListNew);
    } else {
      System.out.println("###### 空");
    }

    return vocInfoList;
  }

  /**
   * @param folderName
   */
  public static void processVoc(String folderName) {
    String fileName = "voc";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        line = specialUnicode(line);
        if (StrUtil.isNotEmpty(line)) {
          scriptList.add(line);
        }
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList.size() != 12) {
      System.out.println("A 判断是否为6个词汇，当前为：" + scriptList.size());
      return;
    } else {
      size = scriptList.size();
    }

    VocInfo vocInfo;
    String word;
    int startIndex;
    int endIndex;
    String temp;
    for (int i = 0; i < size; i++) {
      if ((i + 1) % 2 == 0) {
        vocInfo = new VocInfo();
        word = scriptList.get(i - 1);
        startIndex = word.lastIndexOf("(");
        endIndex = word.lastIndexOf(")");
        if (startIndex != -1 && endIndex != -1) {
          // 存在括号，则删除括号
          temp = word.substring(startIndex, endIndex + 1);
          System.out.println(word + "\t|\t" + temp);
          word = word.replaceAll(temp, "");
          word = word.replaceAll("\\(", "");
          word = word.replaceAll("\\)", "");
        }
        vocInfo.setWord(word.trim());
        vocInfo.setWordExplainEn(scriptList.get(i));
        vocInfoList.add(vocInfo);
      }
    }

    DictUtils.queryVocInfoList(vocInfoList);

    // 写文件
    String newFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      "_cn.txt");

    List<String> scriptListNew = new ArrayList<>();
    String wordCnBrief = "";
    for (VocInfo vocInfo1 : vocInfoList) {
      scriptListNew.add(vocInfo1.getWord());
      scriptListNew.add(vocInfo1.getWordExplainEn());
      wordCnBrief = vocInfo1.getWordCn() != null ? vocInfo1.getWordCn() : "";

      wordCnBrief = shortStr(wordCnBrief);

      scriptListNew.add(wordCnBrief);
      scriptListNew.add(
        vocInfo1.getWordExplainCn() != null ? vocInfo1.getWordExplainCn() : "");
      scriptListNew.add(
        vocInfo1.getSampleSentenceEn() != null ? vocInfo1.getSampleSentenceEn()
          : "");
      scriptListNew.add(
        vocInfo1.getSampleSentenceCn() != null ? vocInfo1.getSampleSentenceCn()
          : "");
//            scriptListNew.add("");
    }

    if (CollectionUtil.isNotEmpty(scriptListNew)) {
      CdFileUtil.writeToFile(newFileName, scriptListNew);
    } else {
      System.out.println("###### 空");
    }
  }

  /**
   * @param folderName 文件夹路径
   * @param fileName   文件名称
   */
  public static void processVoc(String folderName, String fileName) {
//        String fileName = "voc";
    String filePath = folderName + fileName + ".txt";
    List<String> scriptList = new ArrayList<>();

    List<VocInfo> vocInfoList = new ArrayList<>();

    try {
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath));
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        line = specialUnicode(line);
        if (StrUtil.isNotEmpty(line)) {
          scriptList.add(line);
        }
      }
      bufferedReader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 判断是否为6个词汇
    int size = 0;
    if (scriptList.size() != 12) {
      System.out.println("B 判断是否为6个词汇，当前为：" + scriptList.size());
      return;
    } else {
      size = scriptList.size();
    }

    VocInfo vocInfo;
    String word;
    int startIndex;
    int endIndex;
    String temp;
    for (int i = 0; i < size; i++) {
      if ((i + 1) % 2 == 0) {
        vocInfo = new VocInfo();
        word = scriptList.get(i - 1);
        startIndex = word.lastIndexOf("(");
        endIndex = word.lastIndexOf(")");
        if (startIndex != -1 && endIndex != -1) {
          // 存在括号，则删除括号
          temp = word.substring(startIndex, endIndex + 1);
          System.out.println(word + "\t|\t" + temp);
          word = word.replaceAll(temp, "");
          word = word.replaceAll("\\(", "");
          word = word.replaceAll("\\)", "");
        }
        vocInfo.setWord(word.trim());
        vocInfo.setWordExplainEn(scriptList.get(i));
        vocInfoList.add(vocInfo);
      }
    }

    DictUtils.queryVocInfoList(vocInfoList);

    // 写文件
    String newFileName = folderName + fileName
      + "_cn.txt";// CommonUtil.getFullPathFileName(folderName, fileName,      "_cn.txt");

    List<String> scriptListNew = new ArrayList<>();
    String wordCnBrief = "";
    for (VocInfo vocInfo1 : vocInfoList) {
      scriptListNew.add(vocInfo1.getWord());
      scriptListNew.add(vocInfo1.getWordExplainEn());
      wordCnBrief = vocInfo1.getWordCn() != null ? vocInfo1.getWordCn() : "";

      wordCnBrief = shortStr(wordCnBrief);

      scriptListNew.add(wordCnBrief);
      scriptListNew.add(
        vocInfo1.getWordExplainCn() != null ? vocInfo1.getWordExplainCn() : "");
      scriptListNew.add(
        vocInfo1.getSampleSentenceEn() != null ? vocInfo1.getSampleSentenceEn()
          : "");
      scriptListNew.add(
        vocInfo1.getSampleSentenceCn() != null ? vocInfo1.getSampleSentenceCn()
          : "");
//            scriptListNew.add("");
    }

    if (CollectionUtil.isNotEmpty(scriptListNew)) {
      CdFileUtil.writeToFile(newFileName, scriptListNew);
    } else {
      System.out.println("###### 空");
    }
  }

  /**
   * 去除 字符串收尾的 特殊的Unicode [ "\uFEFF" ] csv 文件可能会带有该编码
   *
   * @param str
   * @return
   */
  public static String specialUnicode(String str) {
    if (str.startsWith("\uFEFF")) {
      str = str.replace("\uFEFF", "");
    } else if (str.endsWith("\uFEFF")) {
      str = str.replace("\uFEFF", "");
    }
    return str;
  }

  public static String shortStr(String wordCnBrief) {
    /// 【正则】如何去掉花括号{}大括号，大阔号和里面的值；去掉尖括号里的值
    // https://blog.csdn.net/river_continent/article/details/78214766

    int beginIndex = 0;
    int endIndex = 0;

//        StrUtil.isEmpty();

//        wordCnBrief.

//        String str="{A}123{aaa}456{bbb}";
//        大阔号和里面的值=str.replaceAll("\\{[^}]*\\}","");
    String reg = "（[^}]*）";
    wordCnBrief = wordCnBrief.replaceAll(reg, "");
    System.out.println(wordCnBrief);

    reg = "<[^}]*>";
    wordCnBrief = wordCnBrief.replaceAll(reg, "");
    System.out.println(wordCnBrief);

//        beginIndex = wordCnBrief.indexOf("（");
//        endIndex = wordCnBrief.indexOf("）");
//
//        String temp = "";
//
//        if (beginIndex != -1 && endIndex != -1) {
//            temp = wordCnBrief.substring(beginIndex, endIndex);
//            wordCnBrief = wordCnBrief.replaceAll(temp, "");
//        }
//
//        wordCnBrief = wordCnBrief.substring(beginIndex, endIndex);

    if (wordCnBrief.length() > 13 && wordCnBrief.indexOf("；") != -1
      && wordCnBrief.indexOf("；") < 12) {
      wordCnBrief = wordCnBrief.substring(0, wordCnBrief.indexOf("；"));
    }

    return wordCnBrief;
  }

  /**
   * @param vocInfoList
   */
  public static void queryVocInfoList(List<VocInfo> vocInfoList) {
    List<VocInfo> result = new ArrayList<>();
    for (VocInfo vocInfo : vocInfoList) {
      VocInfo vocInfo1 = queryWord(vocInfo.getWord());
      BeanUtils.copyProperties(vocInfo1, vocInfo, "wordExplainEn");
      result.add(vocInfo);
    }
  }

  public static List<VocInfo> queryWords(List<String> wordList) {
    List<VocInfo> vocInfoList = new ArrayList<>();
    for (String word : wordList) {
      vocInfoList.add(queryWord(word));
    }

    return vocInfoList;
  }

  /**
   * 通过网络查找
   *
   * @param word 待查询的单词
   * @return VocInfo
   */
  public static VocInfo queryWord(String word) {
    VocInfo vocInfo = new VocInfo();
    Map<String, Object> param = new LinkedHashMap<>();
    param.put("q", word);
    param.put("le", "en");
    param.put("t", "2");
    param.put("client", "web");
    param.put("sign", "cb7b9683228573db5f84d8fb13e748ae");
    param.put("keyfrom", "webdict");
//        param.put("client", "");
    Map<String, String> head = new LinkedHashMap<>();
    String url = "https://dict.youdao.com/jsonapi_s?doctype=json&jsonversion=4";
//        Class<T> t = Object.class;
    Integer retryTimes = 3;
    JSONObject jsonObject = HttpUtil.postWithForm(param, head, url,
      JSONObject.class, retryTimes);

    vocInfo.setWord(word);

//    System.out.println(jsonObject.toStringPretty());

    // 填充中文意思字段和句子字段
    fillExplain(vocInfo, jsonObject);
    boolean wordCnEmpty = vocInfo.getWordCn() == null;
    boolean wordExplainCnEmpty = vocInfo.getWordExplainCn() == null;
    boolean sampleSentenceEnEmpty = vocInfo.getSampleSentenceEn() == null;
    boolean sampleSentenceCnEmpty = vocInfo.getSampleSentenceCn() == null;

    if (wordCnEmpty && wordExplainCnEmpty && sampleSentenceEnEmpty
      && sampleSentenceCnEmpty) {
      System.out.println(vocInfo.getWord() + "翻译和例句都为空");
    } else if (wordCnEmpty && wordExplainCnEmpty) {
      System.out.println(vocInfo.getWord() + "翻译为空");
    } else if (sampleSentenceEnEmpty && sampleSentenceCnEmpty) {
      System.out.println(vocInfo.getWord() + "例句为空");
      // TODO 填充 例句字段


    } else {
      System.out.println(vocInfo.getWord() + "词汇信息正常");
    }

    return vocInfo;
  }

  /**
   * 填充
   *
   * @param vocInfo
   * @param jsonObject
   */
  public static void fillExplain(VocInfo vocInfo, JSONObject jsonObject) {
    // TODO
    String expression1 = "[ec].[exam_type]";
    Object byPath1 = jsonObject.getByPath(expression1);
    String usphone = "[ec].[word].[usphone]";
    Object usphoneObject = jsonObject.getByPath(usphone);
    String ukphone = "[ec].[word].[ukphone]";
    Object ukphoneObject = jsonObject.getByPath(ukphone);
    String expression3 = "[ec].[word].[trs].[pos]";
    Object byPath3 = jsonObject.getByPath(expression3);
    String expression4 = "[ec].[word].[trs].[tran]";
    Object byPath4 = jsonObject.getByPath(expression4);

    String wordCn = "";
    String wordExplainCn = "";
    if (ukphoneObject != null) {
      wordExplainCn += "英/" + ukphoneObject + "/";
    }
    if (usphoneObject != null) {
      wordExplainCn += "美/" + usphoneObject + "/";
    }

    String pos;
    String tran;

    if (byPath3 != null && byPath3 instanceof ArrayList && byPath4 != null
      && byPath4 instanceof ArrayList) {
      List posList = (ArrayList) byPath3;
      List tranList = (ArrayList) byPath4;
      int size = posList.size();
      if (posList.size() == tranList.size()) {
        for (int i = 0; i < size; i++) {
          pos = posList.get(i) != null ? posList.get(i).toString() : "";
          tran = tranList.get(i) != null ? tranList.get(i).toString() : "";
          if (i == 0) {
            wordCn = tran; // 设置中文
          }
          // 设置解释
          if (i == size - 1) {
            wordExplainCn += pos + "" + tran;
          } else {
            wordExplainCn += pos + "" + tran + "； ";
          }
        }
      }
    }
    vocInfo.setWordCn(wordCn);

    vocInfo.setWordExplainCn(wordExplainCn);

    String expression5 = "[expand_ec].[word].[transList].[content].[sents].[sentSpeech].[0].[0].[0]";
    Object byPath5 = jsonObject.getByPath(expression5);
    String expression6 = "[expand_ec].[word].[transList].[content].[sents].[sentTrans].[0].[0].[0]";
    Object byPath6 = jsonObject.getByPath(expression6);

//        System.out.println(byPath1);
//        System.out.println(byPath2);
//        System.out.println(byPath3);
//        System.out.println(byPath4);
    String sampleSentenceEn = "";
    if (byPath5 != null) {
      sampleSentenceEn = byPath5.toString();
      vocInfo.setSampleSentenceEn(sampleSentenceEn);
    }
    if (byPath6 != null) {
      String sampleSentenceCn = byPath6.toString();
      vocInfo.setSampleSentenceCn(sampleSentenceCn);
    }

    // 如果包含冒号：

    // 没有找到例句，找柯林斯例句
    if (StrUtil.isEmpty(sampleSentenceEn)) {
      String expressionCollinsEn = "[collins].[collins_entries].[entries].[entry].[tran_entry].[exam_sents].[sent].[eng_sent].[0].[0].[0].[0]";
      Object objectCollinsEn = jsonObject.getByPath(expressionCollinsEn);
      String expressionCollinsCn = "[collins].[collins_entries].[entries].[entry].[tran_entry].[exam_sents].[sent].[chn_sent].[0].[0].[0].[0]";
      Object objectCollinsCn = jsonObject.getByPath(expressionCollinsCn);

      if (objectCollinsEn != null) {
        sampleSentenceEn = objectCollinsEn.toString();
        vocInfo.setSampleSentenceEn(sampleSentenceEn);
      }
      if (objectCollinsCn != null) {
        String sampleSentenceCn = objectCollinsCn.toString();
        vocInfo.setSampleSentenceCn(sampleSentenceCn);
      }
    }

    // 如果既有冒号，又有点号，则拆分
    if (wordExplainCn.contains(".") && wordExplainCn.contains("：")) {
      //
      int colonIndex = wordCn.indexOf("：");

      int firstIndexOfIndex = wordExplainCn.indexOf("； · ");
      int lastIndexOfIndex = wordExplainCn.lastIndexOf("； ");
      wordCn = wordExplainCn.substring(0, colonIndex);
//        System.out.println(wordCn);

      sampleSentenceEn = wordExplainCn.substring(firstIndexOfIndex + 3,
        lastIndexOfIndex);
      String sampleSentenceCn = wordExplainCn.substring(lastIndexOfIndex + 3);
      wordExplainCn = wordExplainCn.substring(colonIndex + 1,
        firstIndexOfIndex);

      System.out.println(wordCn);
      System.out.println(wordExplainCn);
      System.out.println(sampleSentenceEn.trim());
      System.out.println(sampleSentenceCn);

      vocInfo.setWordCn(wordCn);
      vocInfo.setWordExplainCn(wordExplainCn);
      vocInfo.setSampleSentenceEn(sampleSentenceEn);
      vocInfo.setSampleSentenceCn(sampleSentenceCn);
    }

    // 没有找到例句，找柯林斯例句和牛津
    if (StrUtil.isEmpty(sampleSentenceEn)) {
      //
//            String expression7 = "[blng_sents_part][sentence-pair]";
//            Object byPath7 = jsonObject.getByPath(expression7);
//            System.out.println(byPath7.getClass().toString());
//
//            //  cn.hutool.json.JSONArray
//            if(byPath7 instanceof JSONArray) {
//                JSONArray jsonArray = (JSONArray) byPath7;
//
//

//            }

      System.out.println("####");

//            if (objectCollinsEn != null) {
//                sampleSentenceEn = objectCollinsEn.toString();
//                vocInfo.setSampleSentenceEn(sampleSentenceEn);
//            }
//            if (objectCollinsCn != null) {
//                String sampleSentenceCn = objectCollinsCn.toString();
//                vocInfo.setSampleSentenceCn(sampleSentenceCn);
//            }
    }

//        JSONObject.get 2 sentSpeech sentTrans
  }

}
