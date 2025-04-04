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
import com.coderdream.util.sqlite.SQLiteUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;

/**
 * @author CoderDream
 */
public class WordCountUtil {

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
    //        String folderName = "240111";
//        WordCountUtil.genVocTable(folderName);
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

//        System.out.println(WordCountUtil.removeChar("-abc"));
//        System.out.println(WordCountUtil.removeChar("--abc"));
//        System.out.println(WordCountUtil.removeChar("abc-"));
//        System.out.println(WordCountUtil.removeChar("abc--"));
//        System.out.println(WordCountUtil.removeChar("-abc--"));
//        System.out.println(WordCountUtil.removeChar("--abc--"));

//        String folderName = "E:\\02_\\电影\\Oppenheimer.2023.IMAX.2160p.BluRay.x265.10bit.DTS-HD.MA.5.1-WiKi\\";
//        String fileName = "en_test.txt";
//        WordCountUtil.genVocTableForScript(folderName, fileName);

    // TODO
  }

  public static File genVocTable(String folderName) {
    String fileName = "script_dialog";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    List<WordInfo> wordInfoList = process(filePath, "txt");
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String templateFileName = folderPath + File.separator + "词汇.xlsx";

    // 方案1 一下子全部放到内存里面 并填充
    String excelFileName = CommonUtil.getFullPathFileName(folderName,
      folderName, "_完整词汇表.xlsx");
//        String sheetName = "词汇表";

//        MakeExcel.listFill(templateFileName, excelFileName, sheetName, wordInfoList);

    writeToFile(wordInfoList, templateFileName, excelFileName);
    return new File(excelFileName);
  }

  public static void genVocTable(String folderName, String fileName,
    String fileType) {
//        String fileName = "script_dialog";
    String filePath = folderName + File.separatorChar + fileName
      + "."
      + fileType; //   CommonUtil.getFullPathFileName(folderName, fileName, ".txt");
    List<WordInfo> wordInfoList = process(filePath, fileType);
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String templateFileName = folderPath + File.separator + "词汇.xlsx";

    // 方案1 一下子全部放到内存里面 并填充
    String excelFileName = folderName + File.separatorChar + fileName
      + "_完整词汇表.xlsx";//CommonUtil.getFullPathFileName(folderName, folderName, "_完整词汇表.xlsx");
//        String sheetName = "词汇表";

//        MakeExcel.listFill(templateFileName, excelFileName, sheetName, wordInfoList);

    writeToFile(wordInfoList, templateFileName, excelFileName);
  }

  private static void writeToFile(List<WordInfo> wordInfoList,
    String templateFileName, String excelFileName) {
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
//      levelStr = getLastCAndFollowing(levelStr); TODO
      VocLevelEnum vocLevelEnum = VocLevelEnum.match(levelStr);
      String level = vocLevelEnum != null ? vocLevelEnum.getLabel() : "";
      if (level != null) {
        switch (level) {
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
  }

  /**
   * 获取最后一个C及其后面的字符串
   *
   * @param inputString 输入字符串
   * @return 最后一个C及其后面的字符串
   */
  public static String getLastCAndFollowing(String inputString) {
    assert inputString != null;
//    return Optional.of(inputString)
//      .map(String::toUpperCase)
//      .map(s -> s.lastIndexOf("C"))
//      .filter(index -> index != -1)
//      .map(inputString::substring)
//      .orElse("");

    return inputString.substring(0, 3);
  }

//  /**
//   * @param folderName
//   * @param fileName
//   */
//  public static void genVocTableForScript(String folderName, String fileName,
//    String fileType) {
//    // 方案1 一下子全部放到内存里面 并填充
//    String excelFileName =
//      folderName + fileName.substring(0, fileName.lastIndexOf("."))
//        + "_完整词汇表.xlsx";
//    String filePath = folderName + fileName;
//
//    List<WordInfo> wordInfoList = process(filePath, fileType);
//    String folderPath =
//      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
//        + File.separatorChar + "dict";
//    String templateFileName = folderPath + File.separator + "词汇.xlsx";
//
//    writeToFile(wordInfoList, templateFileName, excelFileName);
//  }

  public static List<WordInfo> process(String filePath, String fileType) {
    List<WordInfo> wordInfoList = new ArrayList<>();
    // 单词集合
    Set<String> stringSet = new LinkedHashSet<>();
    // 单词列表
    List<String> rawWordList = new ArrayList<>();
    Map<String, Integer> stringIntegerMap = new TreeMap<>();

    Set<String> pointSet = new HashSet<>();
    pointSet.add(".");
    List<String> stringList;
    if ("txt".equals(fileType)) {
      // 1.按行读取文本，每行是一个字符串
      stringList = FileUtil.readLines(filePath,
        StandardCharsets.UTF_8);// TxtUtil.readTxtFileToList(filePath);
    } else {
      stringList = null;// TODO SrtUtils.genContentList(filePath);
    }
    // 2.遍历每一行，以空格作为分隔符
    Map<String, String> abbrevCompleteMap = genAbbrevCompleteMap();

    // 第1步：分割单词
    String wordTemp;
    for (String str : stringList) {
      if (StrUtil.isNotEmpty(str)) {
        String[] arr = str.split(" ");
        if (arr.length > 1) {
          for (String s : arr) {
            wordTemp = s;
            processSingleWord(abbrevCompleteMap, stringSet, rawWordList,
              wordTemp);
          }
        } else {
          wordTemp = str;
          processSingleWord(abbrevCompleteMap, stringSet, rawWordList,
            wordTemp);
        }
      }
//            System.out.println();
    }

    List<String> rawWordSet = new ArrayList<>(stringSet);

    // 获取单词原型映射键值对
    Map<String, String> lemmaMap = new HashMap<>();
    //按每100个一组分割
    List<List<String>> parts = ListUtil.partition(rawWordSet, 100);
    parts.forEach(list -> {
      lemmaMap.putAll(Objects.requireNonNull(CoreNlpUtils.getLemmaList(list)));
    });

    // 处理原型并计算数量
    for (String word : rawWordList) {
      addToMap(lemmaMap, stringIntegerMap, word);
    }

    System.out.println("###");

    Map<WordEntity, Integer> c00WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c01WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c02WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c03WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c04WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c05WordMap = new LinkedHashMap<>();
    Map<WordEntity, Integer> c06WordMap = new LinkedHashMap<>();

    // 初始化levelMap
    Map<String, String> levelMap = new TreeMap<>();
    // String tableName = "C01_初中词汇正序版";
//    Map<String, WordEntity> c01WordList = WordCountUtil.getWordList("C01_初中词汇正序版");
//    Map<String, WordEntity> c02WordList = WordCountUtil.getWordList("C02_高中英语词汇正序版");// WordCountUtil.getC02WordList();
//    Map<String, WordEntity> c03WordList = WordCountUtil.getWordList("C03_四级词汇正序版");//WordCountUtil.getC03WordList();
//    Map<String, WordEntity> c04WordList = WordCountUtil.getWordList("C04_六级词汇正序版");//WordCountUtil.getC04WordList();
//    Map<String, WordEntity> c05WordList = WordCountUtil.getWordList("C05_2013考研词汇正序版");//WordCountUtil.getC05WordList();
//    Map<String, WordEntity> c06WordList = WordCountUtil.getWordList("C06_雅思词汇正序版");//WordCountUtil.getC06WordList();
//
//    addToLevelMap(levelMap, c06WordList);
//    addToLevelMap(levelMap, c05WordList);
//    addToLevelMap(levelMap, c04WordList);
//    addToLevelMap(levelMap, c03WordList);
//    addToLevelMap(levelMap, c02WordList);
//    addToLevelMap(levelMap, c01WordList);
    Map<String, WordEntity> wordListMap = new HashMap<>();

    // 假设你有一个包含 300 个单词的 List<String>
    List<String> wordsToFind = new ArrayList<>(stringIntegerMap.keySet());

    // 调用 findWordsInSummaryTable 方法
    List<WordEntity> foundWords = SQLiteUtil.findWordsInSummaryTable(
      wordsToFind);

    // 处理查询结果
    if (foundWords != null) {
      for (WordEntity word : foundWords) {
        wordListMap.put(word.getWord(), word);
        String levelStr = getLastCAndFollowing(word.getLevel());
        levelMap.put(word.getWord(), levelStr);
      }
    }

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
            c01WordMap.put(wordListMap.get(word), count);
            break;
          case "C02":
            c02WordMap.put(wordListMap.get(word), count);
            break;
          case "C03":
            c03WordMap.put(wordListMap.get(word), count);
            break;
          case "C04":
            c04WordMap.put(wordListMap.get(word), count);
            break;
          case "C05":
            c05WordMap.put(wordListMap.get(word), count);
            break;
          case "C06":
            c06WordMap.put(wordListMap.get(word), count);
            break;
//                    default:
//                        c00WordMap.put(c01WordList.get(word), count);
//                        break;
        }
      } else {
        wordEntityTemp = new WordEntity();
        wordEntityTemp.setWord(word);
        wordEntityTemp.setLevel("C99");
        c00WordMap.put(wordEntityTemp, count);
        otherList.add(word);
      }
      System.out.println(word + "：" + count);
    }

    System.out.println("#####");

    System.out.println("#### 初中词汇：");
    for (Entry<WordEntity, Integer> entry : c01WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);

      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 高中词汇：");
    for (Entry<WordEntity, Integer> entry : c02WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 四级词汇：");
    for (Entry<WordEntity, Integer> entry : c03WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 六级词汇：");
    for (Entry<WordEntity, Integer> entry : c04WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 考研词汇：");
    for (Entry<WordEntity, Integer> entry : c05WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 雅思词汇：");
    for (Entry<WordEntity, Integer> entry : c06WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    System.out.println("#### 未知词汇：");
    for (Entry<WordEntity, Integer> entry : c00WordMap.entrySet()) {
      WordEntity wordEntity = entry.getKey();
      Integer count = entry.getValue();
      System.out.println(wordEntity + "：" + count);
      fillWordInfo(wordInfoList, wordEntity, count);
    }

    return wordInfoList;
  }

  /**
   * 处理单个单词 过滤数字，处理省略形式 TODO
   */
  private static void processSingleWord(
    Map<String, String> abbrevCompleteMap,
    Set<String> stringSet, List<String> wordList, String wordTemp) {
    wordTemp = removeChar(wordTemp);
    if (!NumberUtil.isNumber(wordTemp)) {  //判断是否为数字
      if (abbrevCompleteMap.containsKey(wordTemp.toLowerCase())) {
        String completeStr = abbrevCompleteMap.get(wordTemp.toLowerCase());
        String[] arr = completeStr.split(" ");
        for (String str : arr) {
          if ("day-to-day".equals(str)) {
            System.out.println("ERROR: day-to-day ####");
          }

          stringSet.add(str.toLowerCase());
          wordList.add(str.toLowerCase());
        }
      } else {
        stringSet.add(wordTemp.toLowerCase());
        wordList.add(wordTemp.toLowerCase());
      }
    } else {
      System.out.println("Number: /t" + wordTemp);
    }
  }

  /**
   * 设置等级信息及出现次数
   *
   * @param wordInfoList
   * @param wordEntity
   * @param count
   */
  private static void fillWordInfo(List<WordInfo> wordInfoList,
    WordEntity wordEntity, Integer count) {
    WordInfo wordInfo;
    wordInfo = new WordInfo();
    BeanUtils.copyProperties(wordEntity, wordInfo);
    VocLevelEnum vocLevelEnum = VocLevelEnum.init(
      getLastCAndFollowing(wordEntity.getLevel()));
    if (vocLevelEnum != null) {
      wordInfo.setLevelStr(vocLevelEnum.getName());
    } else {
      System.out.println("ERROR LEVEL: " + wordEntity.getLevel());
    }
    wordInfo.setTimes(count);
    wordInfoList.add(wordInfo);
  }

  /**
   * @param levelMap
   * @param wordList
   */
  private static void addToLevelMap(Map<String, String> levelMap,
    Map<String, WordEntity> wordList) {
    for (Entry<String, WordEntity> entry : wordList.entrySet()) {
      String word = entry.getKey();
      WordEntity wordEntity = entry.getValue();
//      System.out.println(word + "：" + wordEntity);
      levelMap.put(word, wordEntity.getLevel());
    }
  }

  private static void addToMap(Map<String, String> lemmaMap,
    Map<String, Integer> stringIntegerMap, String wordTemp) {
    Integer count;
    if (StrUtil.isEmpty(wordTemp)) {
      return;
    }
    // 获取单词原型
    String lemma = lemmaMap.get(wordTemp);
    if (lemma != null) {
      count = stringIntegerMap.get(lemma);
      if (count != null && count > 0) {
        count += 1;
      } else {
        count = 1;
      }
      lemma = lemma.trim();
      if (StrUtil.isNotBlank(lemma)) {
        stringIntegerMap.put(lemma, count);
      }
    } else {
//      System.out.println("CANNOT FIND lemma: " + wordTemp);
      count = stringIntegerMap.get(wordTemp);
      if (count != null && count > 0) {
        count += 1;
      } else {
        count = 1;
      }
      wordTemp = wordTemp.trim();
      if (StrUtil.isNotBlank(wordTemp)) {
        stringIntegerMap.put(wordTemp, count);
      }
    }
  }

  /**
   * 移除标点符号，百分比符号
   *
   * @param stringInfo
   * @return
   */
  public static String removeChar(String stringInfo) {
//        String stringInfo = "{infoNum='10' EdwardBlog='http://hi.baidu.com/Edwardworld' topicLength='20' titleShow='yes' EdwardMotto='I am a man,I am a true man!' /}";

//        System.out.println("待处理的字符串：" + stringInfo);
//        Pattern p = Pattern.compile("[.,\"\\?!:')-]");//增加对应的标点
    Pattern p = Pattern.compile("[.,\"\\?!:)]");//增加对应的标点
    Matcher m = p.matcher(stringInfo);
    String first = m.replaceAll(""); //把英文标点符号替换成空，即去掉英文标点符号
    first = first.replaceAll("…", "");//去掉…
    first = first.replaceAll("%", "");//去掉%
    first = first.replaceAll("‘", "'");//【‘】替换成【'】
    first = first.replaceAll("’", "'");//【’】替换成【'】
//        // 移除单引号
//        if (first.startsWith("'") && first.endsWith("'")) {
//            first = first.substring(1, first.length() - 1); // TODO
//        }
    if (StrUtil.isBlank(first)) {
      return first;
    }

    // “heathens”
    if (first.startsWith("”") && first.endsWith("”")) {
      if (first.length() == 1) {
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

//        first= first.replaceAll("…", "");//去掉…
//        System.out.println("去掉英文标点符号后的字符串：" + first);

//        p = Pattern.compile(" {2,}");//去除多余空格
//        m = p.matcher(first);
//
//        String second = m.replaceAll(" ");
//        System.out.println("去掉多余空格后的字符串" + second);//second为最终输出的字符串

    return first;
  }

  public static Map<String, WordEntity> getWordList(String tableName) {
    Map<String, WordEntity> result = new LinkedHashMap<>();
//    String folderPath =
//      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
//        + File.separatorChar + "dict";
//    String filePath = folderPath + File.separator + "C01_初中词汇正序版.xlsx";

//    String tableName = "C01_初中词汇正序版";
//    List<WordEntity> allWords =
//    assert allWords != null;
//    for (WordEntity word : allWords) {
//      log.info(word.toString());
//    }

    List<WordEntity> wordEntityList = SQLiteUtil.getAllWords(tableName);
    assert wordEntityList != null;
    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
      result.put(wordEntity.getWord(), wordEntity);
    }

    return result;
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
//      System.out.println(wordEntity);
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

    Map<String, WordEntity> c01WordList = WordCountUtil.getC01WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath =
      folderPath + File.separator + "C02_高中英语词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
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

    Map<String, WordEntity> c01WordList = WordCountUtil.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil.getC02WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C03_四级词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
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

    Map<String, WordEntity> c01WordList = WordCountUtil.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil.getC03WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C04_六级词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
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

    Map<String, WordEntity> c01WordList = WordCountUtil.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil.getC03WordList();
    Map<String, WordEntity> c04WordList = WordCountUtil.getC04WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath =
      folderPath + File.separator + "C05_2013考研词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
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

    Map<String, WordEntity> c01WordList = WordCountUtil.getC01WordList();
    Map<String, WordEntity> c02WordList = WordCountUtil.getC02WordList();
    Map<String, WordEntity> c03WordList = WordCountUtil.getC03WordList();
    Map<String, WordEntity> c04WordList = WordCountUtil.getC04WordList();
    Map<String, WordEntity> c05WordList = WordCountUtil.getC05WordList();

    Map<String, WordEntity> result = new LinkedHashMap<>();
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String filePath = folderPath + File.separator + "C06_雅思词汇正序版.xlsx";
    List<WordEntity> wordEntityList = CdExcelUtil.genWordEntityList(filePath,
      "Sheet1");

    for (WordEntity wordEntity : wordEntityList) {
//      System.out.println(wordEntity);
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
