package com.coderdream.util.mdict;


import com.coderdream.entity.DictionaryEntry;
import com.coderdream.util.CdDateTimeUtils;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.mdict.demo05.JsoupParser;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典解析工具类
 */
@Slf4j
public class Mdict4jUtil {


  public static void main(String[] args) throws Exception {
    //        String word = "a realistic possibility";
    //        word = "math";
    //        getWordDetail(word, "cambridge");
    //        System.out.println("===============================");
    //        getWordDetail(word, "oaldpe");
    //        System.out.println("===============================");
    //        getWordDetail(word, "maldpe");
    //        System.out.println("===============================");
    //        getWordDetail(word, "c8");
    //        System.out.println("===============================");
    //        getWordDetail(word, "");
    // hangry

//        List<String> strings = FileUtil.readLines("D:\\Download\\20000words.txt", StandardCharsets.UTF_8);
//
//        int i = 0;
//        int index = 10000;
//        index = 5;
//        for (String word : strings) {
//            String[] split = word.split("\t");
//            if (split.length > 1) {
//                word = split[1].trim();
//                log.info("{}", word);
//                Mdict4jDemo.getWordDetail(word, "collins");
//                if (i >= index) {
//                    break;
//                }
//                i++;
//            }
//        }

    System.out.println("===============================");
    List<String> list = Arrays.asList("hone", "demographic");
//
//        list = Arrays.asList("appliance");
    for (String word : list) {
//            System.out.println(    Mdict4jUtil.getWordDetail(word, "oald"));;
      System.out.println(Mdict4jUtil.genDictionaryEntry(word));
//            Mdict4jUtil.getDictInfo(word);

      // DictionaryEntry genDictionaryEntry(String word)
    }
  }

  // a realistic possibility

  /**
   * 根据单词和字典类型获取 HtmlContentBean。
   *
   * @param word     查询的单词
   * @param dictType 字典类型（如 OALD, Collins 等）
   * @return HtmlContentBean 包含 HTML 内容的对象
   */
  public static HtmlContentBean getHtmlContentBean(String word, String dictType) {
    // 获取字典文件路径
    String mdxFile = getMdxFilePath(dictType);
//    log.info("加载字典文件，路径：{}", mdxFile);

    try {
      // 加载字典文件
      MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);

      // 读取字典条目，解析 HTML 内容
//      return Optional.ofNullable(dictionary.readArticles(word))  // 首先获取字典条目
//        .filter(entries -> entries != null && !entries.isEmpty())  // 确保字典条目不为null且非空
//        .flatMap(entries -> entries.stream()  // 将条目转换为流
//          .map(Entry::getValue)  // 提取 HTML 内容
//          .map(html -> parseHtmlByDictType(html, dictType))  // 按字典类型解析
//          .findFirst())  // 获取第一个结果（若无结果返回空）
//        .orElseGet(() -> {
//          log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
//          return new HtmlContentBean();  // 返回空对象
//        });
//      return Optional.ofNullable(dictionary.readArticles(word))  // 获取字典条目
//        .filter(entries -> entries != null && !entries.isEmpty())  // 确保字典条目不为空
//        .flatMap(entries -> entries.stream()  // 将条目转换为流
//          .map(Entry::getValue)  // 提取 HTML 内容
//          .map(html -> parseHtmlByDictType(html, dictType))  // 按字典类型解析
//          .findFirst())  // 获取第一个结果（若无结果返回空）
//        .orElseGet(() -> {
//          log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
//          return new HtmlContentBean();  // 返回空对象
//        });

      return Optional.ofNullable(dictionary.readArticles(word))  // 获取字典条目
        .filter(entries -> entries != null && !entries.isEmpty())  // 确保字典条目不为空
        .map(entries -> entries.get(0))  // 直接获取第一个元素
        .map(Entry::getValue)  // 提取 HTML 内容
        .map(html -> parseHtmlByDictType(html, dictType))  // 按字典类型解析
        .orElseGet(() -> {
          log.warn("未找到单词 {} 在字典 {} 中的内容", word, dictType);
          return new HtmlContentBean();  // 返回空对象
        });


    } catch (MDException e) {
      // 捕获异常，记录错误日志
      log.error("加载字典文件异常，路径：{}", mdxFile, e);
    }

    // 异常或其他情况时，返回一个空对象
    return new HtmlContentBean();
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
    return switch (dictType) {
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
    // 获取字典文件路径
    String mdxFile = getMdxFilePath(dictType);

    MDictDictionary dictionary = null;
    List<Entry<String, String>> list = null;
    try {
      dictionary = MDictDictionary.loadDictionary(mdxFile);
      list = dictionary.readArticles(word);
    } catch (MDException e) {
      log.error("加载字典文件异常，字典文件路径，{}", mdxFile, e);
    }

    HtmlContentBean bean = new HtmlContentBean();

    for (Entry<String, String> entry : list) {
      String html = entry.getValue();
      // 考虑解析不同的字典文件，解析不同的html
      bean = DictHtmlParserUtil.parseOaldHtml(html);
    }

    return bean;
  }

  /**
   * 获取字典文件路径
   *
   * @param dictType 字典类型
   * @return 字典文件路径
   */
  public static String getMdxFilePath(String dictType) {

    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "dict"
        + File.separatorChar;

    // 剑桥在线英汉双解词典完美版 400MB
    // 74MB
    // 28MB
    // 28MB
    //
    //
    // C:\Users\CoderDream\Downloads\ABDM\牛津高阶英汉双解词典（第10版）V3.mdx
    //
    return switch (dictType) {
      case "cambridge" -> folderPath + "cdepe.mdx"; // 剑桥在线英汉双解词典完美版 400MB
      case "oaldpe" -> folderPath + "oaldpe.mdx"; // 74MB
      case "maldpe" -> folderPath + "maldpe.mdx"; // 28MB
      case "c8" -> folderPath + "牛津高阶8简体.mdx"; // 28MB
      //
      case "collins" ->
        folderPath + "柯林斯COBUILD高阶英汉双解学习词典.mdx";   //
      case "oald" ->
        "D:\\Download\\oald.mdx"; // C:\Users\CoderDream\Downloads\ABDM\牛津高阶英汉双解词典（第10版）V3.mdx
      case "Oxford10" ->
        "DC:\\Users\\CoderDream\\Downloads\\ABDM\\牛津高阶英汉双解词典（第10版）V3.mdx"; //
      default -> "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
    };
  }

  public static List<DictionaryEntry> genDictionaryEntryList(
    List<String> wordList) {
    List<DictionaryEntry> list = new ArrayList<>();
    DictionaryEntry entry = null;
    for (String word : wordList) {
      entry = genDictionaryEntry(word);
      list.add(entry);
    }

    return list;
  }

  public static List<DictionaryEntry> genDictionaryEntryList(
    List<String> wordList, Integer startCocaRank) {
    long startTime = System.currentTimeMillis();
    log.info("genDictionaryEntryList: {}", wordList.size());
    List<DictionaryEntry> list = new ArrayList<>();
    DictionaryEntry entry = null;
    for (String word : wordList) {
      entry = genDictionaryEntry(word);
      if (entry != null) {
        entry.setCocaRank(++startCocaRank);
        entry.setCreatedAt(new Date());
        list.add(entry);
      } else {
        log.error("{} not found", word);
      }
    }

    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println("Elapsed time: " + elapsedTime + " ms");
    log.info("Elapsed time: {} ms", elapsedTime);
    log.error("genDictionaryEntryList 本次记录条数{}, 耗时{}。", wordList.size(),
      CdDateTimeUtils.genMessage(elapsedTime));

    return list;
  }

  /**
   * 生成字典词条对象。
   *
   * @param word 单词
   * @return 字典词条对象
   */
  public static DictionaryEntry genDictionaryEntry(String word) {
    HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word,
      "oald");
    String html = htmlContentBean.getRawHtml();
    DictionaryEntry entry = JsoupParser.parseHtml(html);
    return entry;
  }

  public static void getDictInfo(String word) throws Exception {
    System.out.println("Hello Mdict4j!");

    Path dictionaryPath = Paths.get("foo.mdx");
    String mdxFile = "D:\\Download\\oald.mdx";
//        mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
    MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);

//        if (dictionary.isMDX()) {
//            System.out.println("loaded file is .mdx");
//        }
    if (StandardCharsets.UTF_8.equals(dictionary.getEncoding())) {
      System.out.println("MDX file encoding is UTF-8");
    }
    if (dictionary.isHeaderEncrypted()) {
      System.out.println("MDX file is encrypted.");
    }
    if (dictionary.isIndexEncrypted()) {
      System.out.println("MDX index part is encrypted.");
    }
    System.out.println(dictionary.getMdxVersion());
    System.out.println(dictionary.getFormat());
//        System.out.printf("MDX version: %d, format: %s", dictionary.getMdxVersion(), dictionary.getFormat());
    System.out.println(dictionary.getCreationDate());
    System.out.println(dictionary.getTitle());
    System.out.println(dictionary.getDescription());

    List<Entry<String, String>> list = dictionary.readArticles(word);
    System.out.println("list1:" + list.size());
    HtmlContentBean bean = new HtmlContentBean();

    for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

      System.out.println(entry.getKey());
      String html = entry.getValue();
//            System.out.println(html);
      bean = DictHtmlParserUtil.parseOaldHtml(html);
      System.out.println("bean1:" + bean);
    }

    list = dictionary.readArticlesPredictive(word);
    System.out.println("list2:" + list.size());
    for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

      System.out.println(entry.getKey());
      String html = entry.getValue();
//            System.out.println(html);
      bean = DictHtmlParserUtil.parseOaldHtml(html);
      System.out.println("bean2:" + bean);
    }

//        return bean;

  }

}
