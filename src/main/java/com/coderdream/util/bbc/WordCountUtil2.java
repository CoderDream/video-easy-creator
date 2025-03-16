package com.coderdream.util.bbc;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.AbbrevComplete;
import com.coderdream.entity.WordEntity;
import com.coderdream.entity.WordInfo;
import com.coderdream.enums.VocLevelEnum;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdExcelUtil;
import com.coderdream.util.excel.MakeExcel;
import com.coderdream.util.nlp.CoreNlpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;

/**
 * @author CoderDream 词汇统计工具类
 */
@Slf4j
public class WordCountUtil2 {

  public static void main(String[] args) {
    m1();
//        m2();
  }

  public static void m2() {
    String folderName = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\sarah-jenkins's-diary";
    folderName = "D:\\Download\\History\\b1-listening\\";
    String fileName = "b1-listening-06-introduction-lecture";
    String fileType = "txt";
//        fileName = "fileName";

    folderName = "D:\\Download\\[zmk.pw]怦然心动 Flipped.2010.1080p.BluRay.REMUX.AVC.DTS-HD.MA.5.1\\";
    fileName = "怦然心动 Flipped.2010.1080p.BluRay.REMUX.AVC.DTS-HD.MA.5.1.eng";
    fileType = "srt";

    genVocTable(folderName, fileName, fileType);
  }

  public static void m1() {
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "bbc"
        + File.separatorChar;
    List<String> NUMBER_LIST = FileUtil.readLines(folderPath + File.separator + "todo.txt",
      "UTF-8");

    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      WordCountUtil.genVocTable(folderName);
    }
  }

//  /**
//   * 根据文件夹名称生成词汇表
//   *
//   * @param folderName 文件夹名称
//   * @return 生成的词汇表文件
//   */
//  public static File genVocTable(String folderName) {
//    Instant start = Instant.now();
//    log.info("开始生成词汇表，文件夹名称：{}", folderName);
//
//    String fileName = "script_dialog";
//    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
//      ".txt");
//    List<WordInfo> wordInfoList = process(filePath, "txt");
//    String folderPath =
//      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
//        + File.separatorChar + "dict";
//    String templateFileName = folderPath + File.separator + "词汇.xlsx";
//
//    // 方案1 一下子全部放到内存里面 并填充
//    String excelFileName = CommonUtil.getFullPathFileName(folderName,
//      folderName, "_完整词汇表.xlsx");
//
//    writeToFile(wordInfoList, templateFileName, excelFileName);
//
//    Instant end = Instant.now();
//    Duration duration = Duration.between(start, end);
//    long milliseconds = duration.toMillis();
//    long seconds = milliseconds / 1000;
//    long minutes = seconds / 60;
//    long hours = minutes / 60;
//    log.info("词汇表生成完成，文件夹名称：{}, 耗时：{}时{}分{}秒{}毫秒",
//      folderName,
//      hours,
//      minutes % 60,
//      seconds % 60,
//      milliseconds % 1000);
//    return new File(excelFileName);
//  }

  /**
   * 根据文件路径生成词汇表
   *
   * @param folderName 文件夹路径
   * @param fileName   文件名称
   * @param fileType   文件类型
   */
  public static void genVocTable(String folderName, String fileName,
    String fileType) {
    Instant start = Instant.now();
    log.info("开始生成词汇表，文件夹名称：{}, 文件名称：{}, 文件类型：{}",
      folderName, fileName, fileType);
    String filePath = folderName + File.separatorChar + fileName
      + "."
      + fileType;
    List<WordInfo> wordInfoList = process(filePath, fileType);
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String templateFileName = folderPath + File.separator + "词汇.xlsx";

    String excelFileName = folderName + File.separatorChar + fileName
      + "_完整词汇表.xlsx";
    writeToFile(wordInfoList, templateFileName, excelFileName);
    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    long milliseconds = duration.toMillis();
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    log.info(
      "词汇表生成完成，文件夹名称：{}, 文件名称：{}, 文件类型：{}，耗时：{}时{}分{}秒{}毫秒",
      folderName, fileName, fileType,
      hours,
      minutes % 60,
      seconds % 60,
      milliseconds % 1000);
  }

  /**
   * 将词汇信息写入 Excel 文件
   *
   * @param wordInfoList     词汇信息列表
   * @param templateFileName 模板文件名
   * @param excelFileName    输出文件名
   */
  private static void writeToFile(List<WordInfo> wordInfoList,
    String templateFileName, String excelFileName) {
    Instant start = Instant.now();
    log.info("开始写入词汇到文件，输出文件路径：{}, 模板文件：{}", excelFileName,
      templateFileName);
    String sheetName1 = "四六级及以上";
    String sheetName2 = "高中";
    String sheetName3 = "初中";
    String sheetName4 = "其他";

    List<WordInfo> wordInfoList1 = new ArrayList<>();
    List<WordInfo> wordInfoList2 = new ArrayList<>();
    List<WordInfo> wordInfoList3 = new ArrayList<>();
    List<WordInfo> wordInfoList4 = new ArrayList<>();
    for (WordInfo wordInfo : wordInfoList) {
      String levelStr = wordInfo.getLevelStr();
      if (levelStr != null) {
        switch (levelStr) {
          case "C01":
            wordInfoList3.add(wordInfo);
            break;
          case "C02":
            wordInfoList2.add(wordInfo);
            break;
          case "C03":
          case "C04":
          case "C05":
          case "C06":
            wordInfoList1.add(wordInfo);
            break;
          default:
            String word = wordInfo.getWord();
            // 过滤单个字符
            if (StrUtil.isNotEmpty(word) && word.length() > 1) {
              wordInfoList4.add(wordInfo);
            }
            break;
        }
      }
    }

    MakeExcel.listFill(templateFileName, excelFileName, sheetName1,
      wordInfoList1, sheetName2, wordInfoList2,
      sheetName3, wordInfoList3, sheetName4, wordInfoList4);

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    long milliseconds = duration.toMillis();
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    log.info(
      "词汇写入文件完成，输出文件路径：{}， 模板文件：{}, 耗时：{}时{}分{}秒{}毫秒",
      excelFileName, templateFileName,
      hours,
      minutes % 60,
      seconds % 60,
      milliseconds % 1000);
  }

  /**
   * 处理文件，提取单词信息
   *
   * @param filePath 文件路径
   * @param fileType 文件类型
   * @return 单词信息列表
   */
  public static List<WordInfo> process(String filePath, String fileType) {
    Instant start = Instant.now();
    log.info("开始处理文件，文件路径：{}，文件类型：{}", filePath, fileType);

    List<WordInfo> wordInfoList = new ArrayList<>();
    Set<String> stringSet = new LinkedHashSet<>();  // 单词集合
    List<String> rawWordList = new ArrayList<>();  // 单词列表
    Map<String, Integer> stringIntegerMap = new TreeMap<>(); // 单词及其出现次数
    Map<String, String> abbrevCompleteMap = genAbbrevCompleteMap(); // 获取缩写完整形式的映射
    // 定义匹配中文字符、数字和中文标点的正则表达式
    Pattern chinesePattern = Pattern.compile(
      "[\u4e00-\u9fa50-9\\p{Punct}。？！，、；：“”‘’（）《》【】…—·]+");
    //定义匹配英文字符和英文标点的正则表达式
    Pattern englishPattern = Pattern.compile("[a-zA-Z0-9\\p{Punct}\\s]+");

    List<String> stringList = new ArrayList<>();
    // 使用 try-with-resources 读取文件
    if ("txt".equals(fileType)) {
      try (BufferedReader reader = new BufferedReader(
        new FileReader(filePath, StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (StrUtil.isNotEmpty(line)) {
            String[] arr = line.split(" ");
            if (arr.length > 1) {
              for (String s : arr) {
                processSingleWord(abbrevCompleteMap, stringSet, rawWordList,
                  s);
              }
            } else {
              processSingleWord(abbrevCompleteMap, stringSet, rawWordList,
                line);
            }
          }
        }

      } catch (IOException e) {
        log.error("读取文件时发生异常: {}", e.getMessage(), e);
        return new ArrayList<>(); // 如果发生异常，返回一个空的列表
      }

    } else {
      // TODO 处理其他文件类型
      log.warn("暂不支持 {} 类型的文件", fileType);
      return new ArrayList<>();

    }

    List<String> rawWordSet = new ArrayList<>(stringSet);  // 将 Set 转换为 List
    Map<String, String> lemmaMap = new HashMap<>(); // 获取单词原型映射键值对

    //按每100个一组分割
    List<List<String>> parts = ListUtil.partition(rawWordSet, 100);
    parts.forEach(list -> {
      lemmaMap.putAll(Objects.requireNonNull(CoreNlpUtils.getLemmaList(list)));
    });

    // 处理原型并计算数量
    for (String word : rawWordList) {
      addToMap(lemmaMap, stringIntegerMap, word);
    }

    log.debug("单词计数结果：{}", stringIntegerMap);
    Map<WordEntity, Integer> c00WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c01WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c02WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c03WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c04WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c05WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c06WordMap = new LinkedHashMap<>();

    Map<String, String> levelMap = new TreeMap<>();
    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil2.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil2.getC03WordList();
    Map<String, WordEntity> c04WordList = WordCountUtil2.getC04WordList();
    Map<String, WordEntity> c05WordList = WordCountUtil2.getC05WordList();
    Map<String, WordEntity> c06WordList = WordCountUtil2.getC06WordList();

    addToLevelMap(levelMap, c06WordList);
    addToLevelMap(levelMap, c05WordList);
    addToLevelMap(levelMap, c04WordList);
    addToLevelMap(levelMap, c03WordList);
    addToLevelMap(levelMap, c02WordList);
    addToLevelMap(levelMap, c01WordList);

    List<String> otherList = new ArrayList<>();
    String levelTemp;
    WordEntity wordEntityTemp;
    for (Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
      String word = entry.getKey();

      Integer count = entry.getValue();
      levelTemp = levelMap.get(word);
      if (levelTemp != null) {
        switch (levelTemp) {
          case "C01":
            c01WordMap.put(c01WordList.get(word), count);
            break;
          case "C02":
            c02WordMap.put(c02WordList.get(word), count);
            break;
          case "C03":
            c03WordMap.put(c03WordList.get(word), count);
            break;
          case "C04":
            c04WordMap.put(c04WordList.get(word), count);
            break;
          case "C05":
            c05WordMap.put(c05WordList.get(word), count);
            break;
          case "C06":
            c06WordMap.put(c06WordList.get(word), count);
            break;
        }
      } else {
        wordEntityTemp = new WordEntity();
        wordEntityTemp.setWord(word);
        wordEntityTemp.setLevel("C99");
        c00WordMap.put(wordEntityTemp, count);
        otherList.add(word);
      }
      log.debug("单词：{}， 出现次数：{}", word, count);
    }

    log.debug("初中词汇：{}", c01WordMap);
    for (Entry<WordEntity, Integer> entry : c01WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }

    log.debug("高中词汇：{}", c02WordMap);
    for (Entry<WordEntity, Integer> entry : c02WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }

    log.debug("四级词汇：{}", c03WordMap);
    for (Entry<WordEntity, Integer> entry : c03WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }

    log.debug("六级词汇：{}", c04WordMap);
    for (Entry<WordEntity, Integer> entry : c04WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }

    log.debug("考研词汇：{}", c05WordMap);
    for (Entry<WordEntity, Integer> entry : c05WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }
    log.debug("雅思词汇：{}", c06WordMap);
    for (Entry<WordEntity, Integer> entry : c06WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }

    log.debug("未知词汇：{}", c00WordMap);
    for (Entry<WordEntity, Integer> entry : c00WordMap.entrySet()) {
      fillWordInfo(wordInfoList, entry.getKey(), entry.getValue());
    }
    Instant end = Instant.now();  // 记录方法结束时间
    Duration duration = Duration.between(start, end); // 计算方法执行耗时
    long milliseconds = duration.toMillis();
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    log.info("文件处理完成，耗时：{}时{}分{}秒{}毫秒",
      hours,
      minutes % 60,
      seconds % 60,
      milliseconds % 1000);
    return wordInfoList;
  }

  /**
   * 处理单个单词 过滤数字，处理省略形式
   *
   * @param abbrevCompleteMap 缩写完整形式的映射
   * @param stringSet         单词集合
   * @param wordList          单词列表
   * @param wordTemp          单词
   */
  private static void processSingleWord(
    Map<String, String> abbrevCompleteMap,
    Set<String> stringSet, List<String> wordList, String wordTemp) {

    wordTemp = removeChar(wordTemp); // 移除标点符号
    log.debug("处理单个单词：{}， 移除标点符号后：{}", wordTemp, wordTemp);
    if (!NumberUtil.isNumber(wordTemp)) {  // 判断是否为数字
      if (abbrevCompleteMap.containsKey(wordTemp.toLowerCase())) {
        String completeStr = abbrevCompleteMap.get(wordTemp.toLowerCase());
        String[] arr = completeStr.split(" ");
        for (String str : arr) {
          if ("day-to-day".equals(str)) {
            log.warn("ERROR: 遇到 'day-to-day' ####");
          }
          stringSet.add(str.toLowerCase());
          wordList.add(str.toLowerCase());
        }
      } else {
        stringSet.add(wordTemp.toLowerCase());
        wordList.add(wordTemp.toLowerCase());
      }
    } else {
      log.debug("数字：{}", wordTemp);
    }

  }


  /**
   * 设置等级信息及出现次数
   *
   * @param wordInfoList 词汇信息列表
   * @param wordEntity   词汇实体
   * @param count        出现次数
   */
  private static void fillWordInfo(List<WordInfo> wordInfoList,
    WordEntity wordEntity, Integer count) {
    WordInfo wordInfo;
    wordInfo = new WordInfo();
    BeanUtils.copyProperties(wordEntity, wordInfo);
    VocLevelEnum vocLevelEnum = VocLevelEnum.init(wordEntity.getLevel());
    if (vocLevelEnum != null) {
      wordInfo.setLevelStr(vocLevelEnum.getName());
    } else {
      log.error("ERROR LEVEL: {}", wordEntity.getLevel());
    }
    wordInfo.setTimes(count);
    wordInfoList.add(wordInfo);
  }

  /**
   * 添加词汇等级到 Map 中
   *
   * @param levelMap 等级 Map
   * @param wordList 词汇列表
   */
  private static void addToLevelMap(Map<String, String> levelMap,
    Map<String, WordEntity> wordList) {
    for (Entry<String, WordEntity> entry : wordList.entrySet()) {
      String word = entry.getKey();
      WordEntity wordEntity = entry.getValue();
      log.debug("添加词汇等级信息，词汇：{}， 等级：{}", word, wordEntity);
      levelMap.put(word, wordEntity.getLevel());
    }
  }


  /**
   * 将单词加入到 Map 中，统计单词出现次数
   *
   * @param lemmaMap         单词原型 Map
   * @param stringIntegerMap 单词计数 Map
   * @param wordTemp         单词
   */
  private static void addToMap(Map<String, String> lemmaMap,
    Map<String, Integer> stringIntegerMap, String wordTemp) {
    if (StrUtil.isEmpty(wordTemp)) {
      return;
    }
    log.debug("开始处理单词：{}", wordTemp);
    Integer count;
    // 获取单词原型
    String lemma = lemmaMap.get(wordTemp);
    if (lemma != null) {
      count = stringIntegerMap.get(lemma);
      if (count != null && count > 0) {
        count += 1;
      } else {
        count = 1;
      }
      log.debug("单词原型：{}， 出现次数：{}", lemma, count);
      stringIntegerMap.put(lemma, count);
    } else {
      log.warn("找不到单词原型：{}", wordTemp);
      count = stringIntegerMap.get(wordTemp);
      if (count != null && count > 0) {
        count += 1;
      } else {
        count = 1;
      }
      log.debug("单词：{}， 出现次数：{}", wordTemp, count);
      stringIntegerMap.put(wordTemp, count);
    }
  }


  /**
   * 移除标点符号，百分比符号
   *
   * @param stringInfo 待处理的字符串
   * @return 处理后的字符串
   */
  public static String removeChar(String stringInfo) {
    log.debug("待处理的字符串：{}", stringInfo);
    if (StrUtil.isBlank(stringInfo)) {
      log.debug("输入字符串为空，直接返回。");
      return stringInfo;
    }
    final Pattern p = Pattern.compile("[.,\"\\?!:)]");//增加对应的标点

    Matcher m = p.matcher(stringInfo);
    String first = m.replaceAll("");
    first = first.replaceAll("…", "");
    first = first.replaceAll("%", "");
    first = first.replaceAll("‘", "'");
    first = first.replaceAll("’", "'");

    if (first.startsWith("”") && first.endsWith("”")) {
      if (first.length() == 1) {
        log.debug("移除字符串的首尾 ”， 字符串为空，返回空字符串");
        return "";
      } else {
        first = first.substring(1, first.length() - 1);
      }

    }

    // 移除开始的--
    if (first.startsWith("--")) {
      first = first.substring(2);
    }

    // 移除开始的-
    if (first.startsWith("-")) {
      first = first.substring(1);
    }

    // 移除最后的--
    if (first.endsWith("--")) {
      first = first.substring(0, first.length() - 2);
    }

    // 移除最后的-
    if (first.endsWith("-")) {
      first = first.substring(0, first.length() - 1);
    }
    log.debug("处理后的字符串：{}", first);
    return first;
  }


  public static Map<String, WordEntity> getC01WordList() {
    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C01_初中词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("初中词汇：{}", wordEntity);
      result.put(wordEntity.getWord(), wordEntity);
    }

    return result;
  }

  /**
   * C01_初中词汇正序版.xlsx C02_高中英语词汇正序版.xlsx C03_四级词汇正序版.xlsx C04_六级词汇正序版.xlsx
   * C05_2013考研词汇正序版.xlsx
   *
   * @return
   */
  public static Map<String, WordEntity> getC02WordList() {

    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath =
      folderPath + File.separator + "C02_高中英语词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("高中词汇：{}", wordEntity);
      if (!c01WordList.keySet().contains(wordEntity.getWord())) {
        result.put(wordEntity.getWord(), wordEntity);
      }
    }

    return result;
  }

  /**
   * C01_初中词汇正序版.xlsx C02_高中英语词汇正序版.xlsx C03_四级词汇正序版.xlsx C04_六级词汇正序版.xlsx
   * C05_2013考研词汇正序版.xlsx
   *
   * @return
   */
  public static Map<String, WordEntity> getC03WordList() {

    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil2.getC02WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C03_四级词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("四级词汇：{}", wordEntity);
      if (!c01WordList.keySet().contains(wordEntity.getWord())
        && !c02WordList.keySet()
        .contains(wordEntity.getWord())) {
        result.put(wordEntity.getWord(), wordEntity);
      }
    }

    return result;
  }

  /**
   * C01_初中词汇正序版.xlsx C02_高中英语词汇正序版.xlsx
   *
   * @return
   */
  public static Map<String, WordEntity> getC04WordList() {

    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil2.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil2.getC03WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C04_六级词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("六级词汇：{}", wordEntity);
      if (!c01WordList.keySet().contains(wordEntity.getWord())
        && !c02WordList.keySet()
        .contains(wordEntity.getWord()) && !c03WordList.keySet()
        .contains(wordEntity.getWord())) {
        result.put(wordEntity.getWord(), wordEntity);
      }
    }

    return result;
  }

  /**
   * C01_初中词汇正序版.xlsx C02_高中英语词汇正序版.xlsx C03_四级词汇正序版.xlsx
   *
   * @return
   */
  public static Map<String, WordEntity> getC05WordList() {

    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil2.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil2.getC03WordList();
    Map<String, WordEntity> c04WordList = WordCountUtil2.getC04WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath =
      folderPath + File.separator + "C05_2013考研词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("考研词汇：{}", wordEntity);
      if (!c01WordList.keySet().contains(wordEntity.getWord())
        && !c02WordList.keySet()
        .contains(wordEntity.getWord()) && !c03WordList.keySet()
        .contains(wordEntity.getWord()) && !c04WordList.keySet()
        .contains(wordEntity.getWord())) {
        result.put(wordEntity.getWord(), wordEntity);
      }
    }

    return result;
  }

  /**
   * C01_初中词汇正序版.xlsx C02_高中英语词汇正序版.xlsx C03_四级词汇正序版.xlsx
   *
   * @return
   */
  public static Map<String, WordEntity> getC06WordList() {

    Map<String, WordEntity> c01WordList = WordCountUtil2.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil2.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil2.getC03WordList();
    Map<String, WordEntity> c04WordList = WordCountUtil2.getC04WordList();
    Map<String, WordEntity> c05WordList = WordCountUtil2.getC05WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C06_雅思词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
      log.debug("雅思词汇：{}", wordEntity);
      if (!c01WordList.keySet().contains(wordEntity.getWord())
        && !c02WordList.keySet()
        .contains(wordEntity.getWord()) && !c03WordList.keySet()
        .contains(wordEntity.getWord()) && !c04WordList.keySet()
        .contains(wordEntity.getWord()) && !c05WordList.keySet()
        .contains(wordEntity.getWord())) {
        result.put(wordEntity.getWord(), wordEntity);
      }
    }

    return result;
  }

  /**
   * @return
   */
  public static Map<String, String> genAbbrevCompleteMap() {
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "abbreviation.xlsx";
    List<AbbrevComplete> abbrevCompleteList = CdExcelUtil.genAbbrevCompleteList(
      filePath);
    Map<String, String> result = new LinkedHashMap<>();
    for (AbbrevComplete abbrevComplete : abbrevCompleteList) {
      String abbrev = abbrevComplete.getAbbrev();
      if ("I'm".equals(abbrev) || "I've".equals(abbrev) || "I'd".equals(abbrev)
        || "I'll".equals(abbrev)
        || "I’m".equals(abbrev) || "I’ve".equals(abbrev) || "I’d".equals(abbrev)
        || "I’ll".equals(abbrev)) {
        result.put(abbrevComplete.getAbbrev(), abbrevComplete.getComplete());
      } else {
        result.put(abbrevComplete.getAbbrev().toLowerCase(),
          abbrevComplete.getComplete().toLowerCase());
      }
    }

    return result;
  }
}
