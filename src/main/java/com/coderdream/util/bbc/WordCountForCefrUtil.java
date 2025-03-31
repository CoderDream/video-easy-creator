package com.coderdream.util.bbc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.AbbrevComplete;
import com.coderdream.entity.CefrWordEntity;
import com.coderdream.entity.WordInfo;
import com.coderdream.enums.CefrEnum;
import com.coderdream.util.cd.CdExcelUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.excel.MakeExcel;
import com.coderdream.util.nlp.CoreNlpUtils;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.sqlite.SQLiteUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @author CoderDream
 */
@Slf4j
public class WordCountForCefrUtil {

  public static void main(String[] args) {
//    m1();
    String folderName = "20250321"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0008_DailyNews"
        + File.separator
        + folderName;
    WordCountForCefrUtil.genVocTable(folderPath, folderName);
  }

  /**
   * @param folderPath 文件夹路径
   * @param folderName 文件夹名
   */
  public static void genVocTable(String folderPath, String folderName) {
    long startTime = System.currentTimeMillis();
    String txtFilePath =
      folderPath + File.separator + folderName + ".txt";
    if(CdFileUtil.isFileEmpty(txtFilePath)) {
      log.error("文件不存在：{}", txtFilePath);
      txtFilePath = CdFileUtil.addPostfixToFileName(txtFilePath,
        "_script_pure");
    }
    List<WordInfo> wordInfoList = process(txtFilePath);
    String resourcesFolderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "dict";
    String templateFileName =
      resourcesFolderPath + File.separator + "CEFR_VOC.xlsx";

    String excelFilePath = CdFileUtil.changeExtension(txtFilePath, "xlsx");
    excelFilePath = CdFileUtil.addPostfixToFileName(excelFilePath,
      "_完整词汇表");
    log.error("excelFilePath: {}", excelFilePath);

    writeToFile(wordInfoList, templateFileName, excelFilePath);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    log.info("分析的文件为：{}，共有{} 单词，统计耗时：{} ", txtFilePath,
      countWords(txtFilePath), CdTimeUtil.formatDuration(duration));
  }

  public static long countWords(String filePath) {
    long wordCount = 0;
    Pattern pattern = Pattern.compile("\\s+"); // 正则表达式，匹配一个或多个空白字符

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // 移除行首尾的空白字符，并按空白字符分割成单词数组
        String[] words = pattern.split(line.trim());
        wordCount += words.length;
      }
    } catch (IOException e) {
      log.error("Error reading file: {}", e.getMessage(), e);
      return -1; // 或者抛出异常，取决于你的需求
    }

    return wordCount;
  }

  private static void writeToFile(List<WordInfo> wordInfoList,
    String templateFileName, String excelFileName) {
    String sheetName1 = "B1_C2";
    String sheetName2 = "A2";
    String sheetName3 = "A1";
    String sheetName4 = "Others";

    List<WordInfo> wordInfoList1 = new ArrayList<>();
    List<WordInfo> wordInfoList2 = new ArrayList<>();
    List<WordInfo> wordInfoList3 = new ArrayList<>();
    List<WordInfo> wordInfoList4 = new ArrayList<>();
    for (WordInfo wordInfo : wordInfoList) {
      String levelStr = wordInfo.getLevelStr();
      CefrEnum cefrEnum = CefrEnum.fromString(
        levelStr); // 将 String 转换为 CefrEnum 枚举
      if (cefrEnum != null) {
        switch (cefrEnum) {
          case A1:
            wordInfoList3.add(wordInfo);
            break;
          case A2:
            wordInfoList2.add(wordInfo);
            break;
          case B1:
          case B2:
          case C1:
          case C2:
            wordInfoList1.add(wordInfo);
            break;
          case UNKNOWN:
            wordInfoList4.add(wordInfo);
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

  public static List<WordInfo> process(String filePath) {
    List<WordInfo> wordInfoList = new ArrayList<>();
    // 单词集合
    Set<String> stringSet = new LinkedHashSet<>();
    // 单词列表
//    List<String> rawWordList = new ArrayList<>();
    Map<String, Integer> stringIntegerMap = new TreeMap<>();

    List<String> stringList = FileUtil.readLines(filePath,
      StandardCharsets.UTF_8);
//
//    List<String> stringList = WordExtractor.extractWords(filePath);

    // 2.遍历每一行，以空格作为分隔符 省写形式，如:I'm、I've、I'll等等
    Map<String, String> abbrevCompleteMap = genAbbrevCompleteMap();

    // 第1步：分割单词
    String wordTemp;
    for (String str : stringList) {
      if (StrUtil.isNotEmpty(str)) {
        String[] arr = str.split(" ");
        if (arr.length > 1) {
          for (String s : arr) {
            wordTemp = s;
            processSingleWord(abbrevCompleteMap, stringSet,
              wordTemp);
          }
        } else {
          wordTemp = str;
          processSingleWord(abbrevCompleteMap, stringSet,
            wordTemp);
        }
      }
    }
    // 单独处理'
    // 单引号
    List<String> filterSingleQuoteWordList = new ArrayList<>();
    for (String word : stringSet) {
      if (word.contains("'")) {
        String[] split = word.split("'");
        for (String wordTemp2 : split) {
          // 如果是单个字母且不等于I
          if (wordTemp2.length() == 1 && !wordTemp2.equals("I")
            && !wordTemp2.equals("a")) {
            log.info("又一个单个字符：{}", wordTemp2);
          } else {
            filterSingleQuoteWordList.add(
              wordTemp2);
          }
        }
      } else {
        // 如果是单个字母且不等于I
        if (word.length() == 1 && !word.equals("I") && !word.equals("a")) {
          log.info("单个字符：{}", word);
        } else {
          filterSingleQuoteWordList.add(
            word);//   stringSet.add(str.toLowerCase());
        }
      }
    }

    // 空格
    List<String> filteredSpaceWordList = new ArrayList<>();
    for (String word : filterSingleQuoteWordList) {
      if (word.contains(" ")) {
        String[] split = word.split(" ");
        for (String wordTemp2 : split) {
          // 如果是单个字母且不等于I
          if (wordTemp2.length() == 1 && !wordTemp2.equals("I")
            && !wordTemp2.equals("a")) {
            log.info("又一个单个字符：{}", wordTemp2);
          } else {
            if (StrUtil.isNotEmpty(wordTemp2)) {
              filteredSpaceWordList.add(
                wordTemp2);
            } else {
              log.info("单个字符：{}", word);
            }
          }
        }
      } else {
        // 如果是单个字母且不等于I
        if (word.length() == 1 && !word.equals("I") && !word.equals("a")) {
          log.info("单个字符：{}", word);
        } else {
          if (StrUtil.isNotEmpty(word)) {
            filteredSpaceWordList.add(
              word);//   stringSet.add(str.toLowerCase());
          }
        }
      }
    }

    // 获取单词原型映射键值对
    Map<String, String> lemmaMap = new HashMap<>();
    //按每100个一组分割
    List<List<String>> parts = ListUtil.partition(filteredSpaceWordList, 100);
    for (List<String> list : parts) {
      // 先从数据库中查询单词原型数据，如果查到就直接用，如果没有查到再去调用CoreNlpUtils.getLemmaList()方法获取单词原型列表，并存入数据库
      Map<String, String> wordLemmaListDB = SQLiteUtil.findWordLemmaList(list);
      List<String> listOne =null;

      if(wordLemmaListDB != null && !wordLemmaListDB.isEmpty()) {
        lemmaMap.putAll(wordLemmaListDB);
        listOne = new ArrayList<>(wordLemmaListDB.values());
      }
      // 从list中过滤掉listOne中的单词
      List<String> listTwo = new ArrayList<>(list);
//      assert listOne != null;
      if(CollectionUtil.isNotEmpty(listOne)) {
        listTwo.removeAll(listOne);
      }

      if(CollectionUtil.isNotEmpty(listTwo)) {
        Map<String, String> lemmaListOne = CoreNlpUtils.getLemmaList(listTwo);
        String s = SQLiteUtil.importWordLemmaDataFromMap(lemmaListOne);
        log.info("导入原型数据：{}", s);
        lemmaMap.putAll(Objects.requireNonNull(lemmaListOne));
      }
    }

    // 处理原型并计算数量
    for (String word : filteredSpaceWordList) {
      addToMap(lemmaMap, stringIntegerMap, word);
    }

//    System.out.println("###");

    Map<CefrWordEntity, Integer> c00WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c01WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c02WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c03WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c04WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c05WordMap = new LinkedHashMap<>();
    Map<CefrWordEntity, Integer> c06WordMap = new LinkedHashMap<>();

    Map<String, String> cefrMap = new TreeMap<>();

    Map<String, CefrWordEntity> wordListMap = new HashMap<>();

    // 假设你有一个包含 300 个单词的 List<String>
    List<String> wordsToFind = new ArrayList<>(stringIntegerMap.keySet());

    // 调用 findWordsInSummaryTable 方法 TODO
    List<CefrWordEntity> foundWords = SQLiteUtil.findCefrWordList(
      wordsToFind);

    // 处理查询结果
    if (foundWords != null) {
      for (CefrWordEntity word : foundWords) {
        wordListMap.put(word.getHeadword(), word);
        cefrMap.put(word.getHeadword(), word.getCefr());
      }
    }

    String cefr;
    CefrWordEntity cefrWordEntityTemp;
    for (Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
      String word = entry.getKey();
      cefr = cefrMap.get(word);
      CefrEnum cefrEnum = CefrEnum.fromString(cefr);
      Integer count = entry.getValue();
      if (cefrEnum != null) {
        switch (cefrEnum) {
          case A1:
            c01WordMap.put(wordListMap.get(word), count);
            break;
          case A2:
            c02WordMap.put(wordListMap.get(word), count);
            break;
          case B1:
            c03WordMap.put(wordListMap.get(word), count);
            break;
          case B2:
            c04WordMap.put(wordListMap.get(word), count);
            break;
          case C1:
            c05WordMap.put(wordListMap.get(word), count);
            break;
          case C2:
            c06WordMap.put(wordListMap.get(word), count);
          case UNKNOWN:
            c00WordMap.put(wordListMap.get(word), count);
            break;
          default:
            c00WordMap.put(wordListMap.get(word), count);
            break;
        }
      } else {
        cefrWordEntityTemp = new CefrWordEntity();
        cefrWordEntityTemp.setHeadword(word);
        cefrWordEntityTemp.setLevel(99);
        c00WordMap.put(cefrWordEntityTemp, count);
      }
      System.out.println(word + "：" + count);
    }

    for (Entry<CefrWordEntity, Integer> entry : c01WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);

      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 高中词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c02WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 四级词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c03WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 六级词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c04WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 考研词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c05WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 雅思词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c06WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

//    System.out.println("#### 未知词汇：");
    for (Entry<CefrWordEntity, Integer> entry : c00WordMap.entrySet()) {
      CefrWordEntity cefrWordEntity = entry.getKey();
      Integer count = entry.getValue();
//      System.out.println(cefrWordEntity + "：" + count);
      fillWordInfo(wordInfoList, cefrWordEntity, count);
    }

    // 过滤掉相同的对象
    wordInfoList = filterDuplicateWords(wordInfoList);

    return wordInfoList;
  }

  public static List<WordInfo> filterDuplicateWords(
    List<WordInfo> wordInfoList) {
    return new ArrayList<>(wordInfoList.stream()
      .collect(Collectors.toMap(
        WordInfo::getWord, // Key: word
        wordInfo -> wordInfo,    // Value: WordInfo object
        (existing, replacement) -> existing
        // Merge function: keep the existing one
        , LinkedHashMap::new //Use LinkedHashMap to preserve the order
      ))
      .values());
  }

//  /**
//   * 处理单个单词 过滤数字，处理省略形式 TODO
//   */
//  private static void processSingleWord(Map<String, String> abbrevCompleteMap,
//    Set<String> stringSet, List<String> wordList, String wordTemp) {
//    wordTemp = removeChar(wordTemp);
//    if (!NumberUtil.isNumber(wordTemp)) {  //判断是否为数字
//      if (abbrevCompleteMap.containsKey(wordTemp.toLowerCase())) {
//        String completeStr = abbrevCompleteMap.get(wordTemp.toLowerCase());
//        String[] arr = completeStr.split(" ");
//        for (String str : arr) {
//          if ("day-to-day".equals(str)) {
//            System.out.println("ERROR: day-to-day ####");
//          }
//
//          stringSet.add(str.toLowerCase());
//          wordList.add(str.toLowerCase());
//        }
//      } else {
//        stringSet.add(wordTemp.toLowerCase());
//        wordList.add(wordTemp.toLowerCase());
//      }
//    } else {
//      System.out.println("Number: /t" + wordTemp);
//    }
//  }

  /**
   * 处理单个单词 过滤数字，处理省略形式 TODO
   */
  private static void processSingleWord(Map<String, String> abbrevCompleteMap,
    Set<String> stringSet, String wordTemp) {
    wordTemp = removeChar(wordTemp);
    if (!NumberUtil.isNumber(wordTemp)) {  //判断是否为数字
      if (abbrevCompleteMap.containsKey(wordTemp.toLowerCase())) {
        String completeStr = abbrevCompleteMap.get(wordTemp.toLowerCase());

        String[] arr = completeStr.split(" ");
        for (String str : arr) {
//          if ("day-to-day".equals(str)) {
//            System.out.println("ERROR: day-to-day ####");
//          }
          if (str.contains("'")) {
            String[] split = str.split("'");
            stringSet.addAll(List.of(split));
          } else {
            stringSet.add(str);//   stringSet.add(str.toLowerCase());
          }
        }
      } else {
        stringSet.add(wordTemp);// stringSet.add(wordTemp.toLowerCase());
      }
    } else {
      System.out.println("Number: /t" + wordTemp);
    }
  }

  /**
   * 设置等级信息及出现次数
   *
   * @param wordInfoList   单词信息列表
   * @param cefrWordEntity 单词实体
   * @param count          出现次数
   */
  private static void fillWordInfo(List<WordInfo> wordInfoList,
    CefrWordEntity cefrWordEntity, Integer count) {
    WordInfo wordInfo = new WordInfo();
    BeanUtils.copyProperties(cefrWordEntity, wordInfo);
    wordInfo.setWord(cefrWordEntity.getHeadword());
    wordInfo.setUk(cefrWordEntity.getAmericanPhonetic());
    wordInfo.setPos(cefrWordEntity.getPos());
    wordInfo.setComment(cefrWordEntity.getChineseDefinition());
    if (cefrWordEntity.getCefr() != null) {

      wordInfo.setLevelStr(cefrWordEntity.getCefr());
    } else {
      CefrEnum cefrEnum = CefrEnum.fromLevel(cefrWordEntity.getLevel());
      wordInfo.setLevelStr(cefrEnum.name());
    }
    wordInfo.setTimes(count);
    wordInfoList.add(wordInfo);
  }

  private static void addToMap(Map<String, String> lemmaMap,
    Map<String, Integer> stringIntegerMap, String wordTemp) {
    Integer count;
    if (StrUtil.isEmpty(wordTemp)) {
      return;
    }
    // 获取单词原型
    String lemma = lemmaMap.get(wordTemp);
    // 找到了原型
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
    }
    // 未找到原型，直接使用单词本身
    else {
      log.error("CANNOT FIND lemma: {}", wordTemp);
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
   * @param stringInfo 待处理的字符串
   * @return 处理后的字符串
   */
  public static String removeChar(String stringInfo) {
    Pattern p = Pattern.compile("[.,\"\\?!:;()]");//增加对应的标点
    Matcher m = p.matcher(stringInfo);
    String first = m.replaceAll(""); //把英文标点符号替换成空，即去掉英文标点符号
    first = first.replace("…", "");//去掉…
    first = first.replace("%", "");//去掉%
    first = first.replace("(", " ");//去掉%
    first = first.replace(")", " ");//去掉%
    first = first.replace("“", " ");//去掉%“poison”
    first = first.replace("”", " ");//去掉%
    first = first.replace(";", " ");//去掉%
    first = first.replace("—", " ");//去掉%
    first = first.replace("' ", " ");//去掉%
    first = first.replace("‘", "'");//【‘】替换成【'】
    first = first.replace("’", "'");//【’】替换成【'】 '
//    first = first.replace("'", " ");//【’】替换成【'】 '
    first = first.replace("  ", " ");//【’】替换成【'】 '

//    first = first.replaceAll("…", "");//去掉…
//    first = first.replaceAll("%", "");//去掉%
//    first = first.replace("(", " ");//去掉%
//    first = first.replace(")", " ");//去掉%
//    first = first.replace("“", " ");//去掉%“poison”
//    first = first.replace("”", " ");//去掉%
//    first = first.replace(";", " ");//去掉%
//    first = first.replaceAll("—", " ");//去掉%
//    first = first.replaceAll("' ", " ");//去掉%
//    first = first.replaceAll("‘", "'");//【‘】替换成【'】
//    first = first.replaceAll("’", "'");//【’】替换成【'】 '
//    first = first.replaceAll("'", " ");//【’】替换成【'】 '
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

    // 移除开始的'
    if (first.startsWith("'")) {
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

    // 移除最后的'
    if (first.endsWith("'")) {
      first = first.substring(0, first.length() - 1);
    }

    return first;
  }

  /**
   * 生成缩写和完整形式的映射关系
   *
   * @return 映射关系
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

    result.put("can't", "cannot");
    result.put("won't", "will not");
    result.put("shan't", "shall not");
    result.put("n't", "not");
    result.put("'re", "are");
    result.put("'s", "is");
    result.put("'d", "would");
    result.put("'ll", "will");
    result.put("'m", "am");
    result.put("'ve", "have");
    result.put("he's", "he is");
    result.put("she's", "she is");
    result.put("it's", "it is"); // 注意 its 的所有格
    result.put("they're", "they are");
    result.put("we're", "we are");
    result.put("i've", "I have"); // 注意大小写
    result.put("you've", "you have");
    result.put("he'd", "he would"); // 或者 he had，需要上下文
    result.put("she'd", "she would");
    result.put("i'd", "I would");
    result.put("I'd", "I would");
    result.put("you'd", "you would");
    result.put("You'd", "You would");
    result.put("we'd", "we would");
    result.put("they'd", "they would");
    result.put("there's", "there is");
    result.put("here's", "here is");
    result.put("who's", "who is"); // 注意 whose 所有格
    result.put("what's", "what is");
    result.put("where's", "where is");
    result.put("let's", "let us");
    result.put("Let's", "Let us");
    result.put("don't", "do not");//it'll
    result.put("Don't", "Do not");//it'll
    result.put("it'll", "it will");//it'll
    result.put("It'll", "It will");//it'll

    return result;
  }
}
