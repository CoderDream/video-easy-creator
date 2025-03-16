package com.coderdream.util.mdict;

import com.coderdream.entity.DictionaryEntry;
import com.coderdream.util.cd.CdDateTimeUtils;
import com.coderdream.util.mdict.demo05.JsoupParser;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;

/**
 * 字典解析工具类
 */
@Slf4j
public class Mdict4jUtil {

  public static void main(String[] args) throws Exception {
    log.info("===============================");
    List<String> list = Arrays.asList(
//      "commonly"
//      ,
//      "trolley",
//      "incinerator",
//      "tinge",
//      "wipe",
//      "clamor",
//      "linen",
//      "startle",
//      "capitulate",
//      "attractive"
      "chemistry"
    );

    List<String> dictTypes = Arrays.asList("cambridge",
      "oaldpe",
      "maldpe",
      "c8",
      "collins",
      "oald");
    String dictName = dictTypes.get(4);
    log.info("字典类型：{}", dictName);

    for (String word : list) {
      log.info("{}", Mdict4jUtil.genDictionaryEntry(word, dictName));
    }
  }

  /**
   * 根据单词和字典类型获取 HtmlContentBean。
   *
   * @param word     查询的单词
   * @param dictType 字典类型（如 OALD, Collins 等）
   * @return HtmlContentBean 包含 HTML 内容的对象
   */
  public static HtmlContentBean getHtmlContentBean(String word,
    String dictType) {
    Instant start = Instant.now();
    String mdxFile = getMdxFilePath(dictType);
    log.info("加载字典文件，路径：{}", mdxFile);

    try {
      MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);
      HtmlContentBean result = Optional.of(dictionary.readArticles(word))
        .filter(entries -> !entries.isEmpty())
        .map(entries -> entries.get(0))
        .map(Entry::getValue)
        .map(html -> parseHtmlByDictType(html, dictType))
        .orElseGet(() -> {
          log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
          return new HtmlContentBean();
        });
      Instant finish = Instant.now();
      long timeElapsed = Duration.between(start, finish).toMillis();
      log.info("查询单词 {} 在字典 {} 中的 HtmlContentBean 耗时：{}， 结果： {}",
        word, dictType, CdDateTimeUtils.genMessage(timeElapsed), result);
      return result;
    } catch (MDException e) {
      log.error("加载字典文件异常，路径：{}", mdxFile, e);
      return new HtmlContentBean();
    }
  }


  /**
   * 根据单词和字典类型获取 HtmlContentBean。
   *
   * @param word     查询的单词
   * @param dictType 字典类型（如 OALD, Collins 等）
   * @return HtmlContentBean 包含 HTML 内容的对象
   */
  /**
   * 根据单词和字典类型获取 HTML 字符串。
   *
   * @param word     查询的单词
   * @param dictType 字典类型（如 OALD, Collins 等）
   * @return HTML 字符串
   */
  /**
   * 根据单词和字典类型获取 HTML 字符串。
   *
   * @param word     查询的单词
   * @param dictType 字典类型（如 OALD, Collins 等）
   * @return HTML 字符串
   */
  public static String getHtmlString(String word, String dictType) {
    Instant start = Instant.now();
    String mdxFile = getMdxFilePath(dictType);
    log.info("加载字典文件，路径：{}", mdxFile);
    String result = "";
    try {
      MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);
      result = Optional.of(dictionary.readArticles(word))
        .filter(entries -> !entries.isEmpty())
        .map(entries -> entries.get(0))
        .map(Entry::getValue)
        .orElseGet(() -> {
          log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
          return "";
        });

    } catch (MDException e) {
      log.error("加载字典文件异常，路径：{}", mdxFile, e);
      return "";
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
//    log.info("查询单词 {} 在字典 {} 中的 HTML 耗时：{}， 结果： {}",
//      word, dictType, CdDateTimeUtils.genMessage(timeElapsed), result);
    log.info("查询单词 {} 在字典 {} 中的 HTML 耗时：{}",
      word, dictType, CdDateTimeUtils.genMessage(timeElapsed));
    return result;
  }

  /**
   * 根据字典类型解析 HTML 内容。
   *
   * @param html     需要解析的 HTML 内容
   * @param dictType 字典类型
   * @return HtmlContentBean 解析后的结果对象
   */
  private static HtmlContentBean parseHtmlByDictType(String html,
    String dictType) {
    log.info("解析字典类型：{}, html {}", dictType, html);
    return switch (dictType) {
      case "oaldpe" -> DictHtmlParserUtil.parseDefaultHtml(html);
      case "Oald" -> DictHtmlParserUtil.parseOaldHtml(html); // 解析 OALD 字典的 HTML
      case "Collins" ->
        DictHtmlParserUtil.parseCollinsHtml(html); // 解析 Collins 字典的 HTML
      case "Oxford" ->
        DictHtmlParserUtil.parseOxfordHtml(html); // 解析 Oxford 字典的 HTML
      default -> {
        log.warn("字典类型 {} 未找到专用解析器，使用默认解析器", dictType);
        yield DictHtmlParserUtil.parseDefaultHtml(html);
      }
    };
  }

  /**
   * 获取单词的详细信息
   *
   * @param word     单词
   * @param dictType 字典类型
   * @return HtmlContentBean  单词的详细信息
   */
  public static HtmlContentBean getHtmlContentBeanBackupUglyVersion(String word,
    String dictType) {
    Instant start = Instant.now();
    String mdxFile = getMdxFilePath(dictType);
    log.info("加载字典文件，路径：{}", mdxFile);
    HtmlContentBean bean = new HtmlContentBean();
    try {
      MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);
      List<Entry<String, String>> list = dictionary.readArticles(word);
      if (list != null) {
        for (Entry<String, String> entry : list) {
          String html = entry.getValue();
          // 考虑解析不同的字典文件，解析不同的html
          bean = DictHtmlParserUtil.parseOaldHtml(html);
        }
      } else {
        log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
      }
    } catch (MDException e) {
      log.error("加载字典文件异常，字典文件路径，{}", mdxFile, e);
    }

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info(
      "查询单词 {} 在字典 {} 中的 getHtmlContentBeanBackupUglyVersion 耗时：{}， 结果： {}",
      word, dictType, CdDateTimeUtils.genMessage(timeElapsed), bean);
    return bean;
  }

  /**
   * 获取字典文件路径
   *
   * @param dictType 字典类型
   * @return 字典文件路径
   */
  public static String getMdxFilePath(String dictType) {
    String folderPath = "D:\\java_output\\dict"
      + File.separatorChar;

    return switch (dictType) {
      case "cambridge" -> folderPath + File.separator + "cdepe.mdx";
      case "oaldpe" -> folderPath + File.separator + "oaldpe.mdx";
      case "maldpe" -> folderPath + File.separator + "maldpe.mdx";
      case "c8" -> folderPath + File.separator + "牛津高阶8简体.mdx";
      case "collins" -> folderPath + File.separator + "柯林斯COBUILD高阶英汉双解学习词典.mdx";
      case "oald" -> folderPath + File.separator + "oald.mdx";
      case "Oxford10" -> folderPath + File.separator + "牛津高阶英汉双解词典（第10版）V3.mdx";
      default -> folderPath + File.separator + "柯林斯COBUILD高阶英汉双解学习词典.mdx";
    };
  }

  /**
   * 生成字典词条列表
   *
   * @param wordList 单词列表
   * @return 字典词条列表
   */
  public static List<DictionaryEntry> genDictionaryEntryList(
    List<String> wordList, String dictType) {
    Instant start = Instant.now();
    List<DictionaryEntry> list = new ArrayList<>();
    for (String word : wordList) {
      DictionaryEntry entry = genDictionaryEntry(word, dictType);
      list.add(entry);
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("生成字典词条列表 耗时：{}，共 {} 个词条",
      CdDateTimeUtils.genMessage(timeElapsed), wordList.size());
    return list;
  }

  /**
   * 生成字典词条列表，带起始排名。
   *
   * @param wordList      单词列表
   * @param startCocaRank 起始排名
   * @return 字典词条列表
   */
  public static List<DictionaryEntry> genDictionaryEntryList(
    List<String> wordList, Integer startCocaRank, String dictType) {
    Instant start = Instant.now();
    log.info("genDictionaryEntryList: {}", wordList.size());
    List<DictionaryEntry> list = new ArrayList<>();
    for (String word : wordList) {
      DictionaryEntry entry = genDictionaryEntry(word, dictType);
      if (entry != null) {
        entry.setCocaRank(++startCocaRank);
        entry.setCreatedAt(new Date());
        list.add(entry);
      } else {
        log.error("{} not found", word);
      }
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("genDictionaryEntryList 本次记录条数{}, 耗时{}。", wordList.size(),
      CdDateTimeUtils.genMessage(timeElapsed));

    return list;
  }

  /**
   * 生成字典词条对象。
   *
   * @param word 单词
   * @return 字典词条对象
   */
  public static DictionaryEntry genDictionaryEntry(String word,
    String dictType) {
    Instant start = Instant.now();
//    HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word,
//      "oald");

    HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word,
      dictType);
    String html = htmlContentBean.getRawHtml();
    DictionaryEntry entry = JsoupParser.parseHtml(html);

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("生成字典词条 {}，耗时: {} ", word,
      CdDateTimeUtils.genMessage(timeElapsed));
    return entry;
  }

  /**
   * 获取字典信息，用于调试
   *
   * @param word 单词
   */
  public static void getDictInfo(String word) {
    Instant start = Instant.now();
    String mdxFile = "D:\\Download\\oald.mdx";
    try {
      MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);
      if (StandardCharsets.UTF_8.equals(dictionary.getEncoding())) {
        log.info("MDX file encoding is UTF-8");
      }
      if (dictionary.isHeaderEncrypted()) {
        log.info("MDX file is encrypted.");
      }
      if (dictionary.isIndexEncrypted()) {
        log.info("MDX index part is encrypted.");
      }
      log.info("MdxVersion: {}", dictionary.getMdxVersion());
      log.info("Format: {}", dictionary.getFormat());
      log.info("CreationDate: {}", dictionary.getCreationDate());
      log.info("Title: {}", dictionary.getTitle());
      log.info("Description: {}", dictionary.getDescription());

      List<Entry<String, String>> list = dictionary.readArticles(word);
      log.info("list1 size: {}", list.size());
      HtmlContentBean bean = new HtmlContentBean();
      if (list != null) {
        for (Entry<String, String> entry : list) {
          log.info("entry key: {}", entry.getKey());
          String html = entry.getValue();
          bean = DictHtmlParserUtil.parseOaldHtml(html);
          log.info("bean1: {}", bean);
        }
      }

      list = dictionary.readArticlesPredictive(word);
      log.info("list2 size: {}", list.size());
      if (list != null) {
        for (Entry<String, String> entry : list) {
          log.info("entry key: {}", entry.getKey());
          String html = entry.getValue();
          bean = DictHtmlParserUtil.parseOaldHtml(html);
          log.info("bean2: {}", bean);
        }
      }


    } catch (MDException e) {
      log.error("加载字典文件异常，字典文件路径，{}", mdxFile, e);
    }

    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("获取字典信息，单词: {}, 耗时: {}", word,
      CdDateTimeUtils.genMessage(timeElapsed));
  }
}
