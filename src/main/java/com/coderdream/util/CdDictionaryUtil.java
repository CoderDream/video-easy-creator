package com.coderdream.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.coderdream.entity.DictionaryEntity;
import com.coderdream.entity.MultiLanguageContent;
import com.coderdream.entity.WordDetail;
import com.coderdream.entity.WordPronunciation;
import com.coderdream.util.mdict.HtmlContentBean;
import com.coderdream.util.mdict.Mdict4jUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class CdDictionaryUtil {

  /**
   * 获取单词详情
   *
   * @param word 单词
   * @return wordDetail
   */
  public static WordDetail getWordDetail(String word) {
    return getWordDetail(word, CdConstants.OALDPE);
  }

  /**
   * 获取单词详情
   *
   * @param word 单词
   * @return wordDetail
   */
  public static WordDetail getWordDetail(String word, String source) {
    // 获取单词详情
    DictionaryEntity dictionary = CdDictionaryUtil.getDictionaryEntity(
      word, source);
    assert dictionary != null;
    // 获取单词详情html字符串
    String htmlStr = dictionary.getReserved05();
    // 获取WordDetail实体
    WordDetail wordDetail = CdDictionaryUtil.getWordDetailFromHtmlStr(
      htmlStr);
    wordDetail.setWord(word); // 设置单词
    return wordDetail;
  }

  public static List<DictionaryEntity> getDictionaryEntityList(
    List<String> list, String source) {
    List<DictionaryEntity> dictionaries = null;
    if (CollectionUtil.isNotEmpty(list)) {
      dictionaries = new ArrayList<>();
      // 创建新的字典对象
      DictionaryEntity newDictionary;
      for (String word : list) {
        System.out.println(word);
        //            log.info("s:{}", word);
        try {
          // 创建新的字典对象
          newDictionary = getDictionaryEntity(word, source);
          log.info("newDictionary:{}", newDictionary);
          dictionaries.add(newDictionary);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
    return dictionaries;
  }

  /**
   * 获取单词详情
   *
   * @param word  单词
   * @param source  来源
   * @return  dictionaryEntity
   */
  public static DictionaryEntity getDictionaryEntityBackupUglyVersion(
    String word, String source) {
    // 创建新的字典对象
    DictionaryEntity newDictionary = new DictionaryEntity();
    System.out.println(word);
    log.info("s:{}", word);
    try {
      // HtmlContentBean htmlContentBean = Mdict4jUtil.getWordDetail(word, "collins"); // Oxford10
      HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(
        word, source); // Oxford10
      newDictionary.setSource(source);
      newDictionary.setWord(word);
      newDictionary.setReserved01(
        htmlContentBean.getRatingText());
      newDictionary.setReserved02(
        ObjectUtil.isNull(htmlContentBean.getCaptions()) ? ""
          : htmlContentBean.getCaptions().toString());
      newDictionary.setReserved03(
        ObjectUtil.isNull(htmlContentBean.getSentences()) ? ""
          : htmlContentBean.getSentences().toString());
      newDictionary.setReserved04(
        ObjectUtil.isNull(htmlContentBean.getTranslations()) ? ""
          : htmlContentBean.getTranslations().toString());
      newDictionary.setReserved05(
        ObjectUtil.isNull(htmlContentBean.getRawHtml()) ? ""
          : htmlContentBean.getRawHtml());

    } catch (Exception e) {
      log.error("获取单词详情失败:{}", word, e);
      throw new RuntimeException(e);
    }
    return newDictionary;
  }

  /**
   * 获取单词的详细信息，并将其封装到 DictionaryEntity 对象中。
   * 如果发生异常，返回包含基本信息（word 和 source）的空对象。
   *
   * @param word   查询的单词
   * @param source 数据来源（例如 "Oxford"、"Collins"）
   * @return DictionaryEntity 包含单词详细信息的对象
   */
  public static DictionaryEntity getDictionaryEntity(String word, String source) {
    // 记录日志，方便调试
//    log.info("开始获取单词详情：word={}, source={}", word, source);

    try {
      // 调用工具类获取单词的详细信息（HtmlContentBean）
      HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word, source);

      // 返回一个已填充的 DictionaryEntity 对象，使用链式调用进行初始化
      return new DictionaryEntity()
        .setSource(source) // 设置数据来源
        .setWord(word) // 设置单词
        // 设置评分文本，允许为 null
        .setReserved01(htmlContentBean.getRatingText())
        // 设置标题信息，如果为 null，则设置为空字符串
        .setReserved02(Optional.ofNullable(htmlContentBean.getCaptions())
          .map(Object::toString).orElse(""))
        // 设置示例句子，如果为 null，则设置为空字符串
        .setReserved03(Optional.ofNullable(htmlContentBean.getSentences())
          .map(Object::toString).orElse(""))
        // 设置翻译信息，如果为 null，则设置为空字符串
        .setReserved04(Optional.ofNullable(htmlContentBean.getTranslations())
          .map(Object::toString).orElse(""))
        // 设置原始 HTML 内容，如果为 null，则设置为空字符串
        .setReserved05(Optional.ofNullable(htmlContentBean.getRawHtml())
          .map(Object::toString).orElse(""));

    } catch (Exception e) {
      // 捕获异常并记录错误日志
      log.error("获取单词详情失败：word={}, source={}, 异常信息={}", word, source, e.getMessage(), e);
    }

    // 如果发生异常，返回一个包含基本信息的空对象，避免外部调用报错
    return new DictionaryEntity()
      .setWord(word)
      .setSource(source);
  }

//
//  public static DictionaryEntity getDictionaryEntity(
//    String word, String source) {
//    // 创建新的字典对象
//    DictionaryEntity newDictionary = new DictionaryEntity();
////    System.out.println(word);
////    log.info("s:{}", word);
//    try {
//      // HtmlContentBean htmlContentBean = Mdict4jUtil.getWordDetail(word, "collins"); // Oxford10
//      HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(
//        word, source); // Oxford10
//      if (ObjectUtil.isNull(htmlContentBean)) {
//        return null;
//      }
//      newDictionary.setSource(source);
//      newDictionary.setWord(word);
//      newDictionary.setReserved01(
//        htmlContentBean.getRatingText());
//      newDictionary.setReserved02(
//        ObjectUtil.isNull(htmlContentBean.getCaptions()) ? ""
//          : htmlContentBean.getCaptions().toString());
//      newDictionary.setReserved03(
//        ObjectUtil.isNull(htmlContentBean.getSentences()) ? ""
//          : htmlContentBean.getSentences().toString());
//      newDictionary.setReserved04(
//        ObjectUtil.isNull(htmlContentBean.getTranslations()) ? ""
//          : htmlContentBean.getTranslations().toString());
//      newDictionary.setReserved05(
//        ObjectUtil.isNull(htmlContentBean.getRawHtml()) ? ""
//          : htmlContentBean.getRawHtml());
//
//    } catch (Exception e) {
//      log.error("获取单词详情失败:{}", word, e);
//      throw new RuntimeException(e);
//    }
//    return newDictionary;
//  }


  /**
   * 从 HTML 字符串中解析单词详情
   *
   * @param htmlStr HTML 字符串
   * @return WordDetail 对象，包含词性、音标、定义、例句等信息
   */
  public static WordDetail getWordDetailFromHtmlStr(String htmlStr) {

    // 创建 WordDetail 实例
    WordDetail wordDetail = new WordDetail();
    // 如果输入的 HTML 字符串为空，则返回 null
    if (isNullOrEmpty(htmlStr)) {
      log.warn("输入的 HTML 字符串为空，无法解析单词详情。");
      return wordDetail;
    }

    // 解析 HTML 字符串
    Document document = Jsoup.parse(htmlStr);
    log.info("成功解析 HTML 字符串。");


    // 设置各个字段
    wordDetail.setPartOfSpeechList(getSafePartOfSpeechList(document));
    wordDetail.setWordPronunciation(getSafePhoneticSymbol(document));
    wordDetail.setDefinitionList(getSafeDefinitionList(document));
    wordDetail.setSentenceList(getSafeSentenceList(document));

    log.info("单词详情解析完成: {}", wordDetail);
    return wordDetail;
  }

  /**
   * 判断字符串是否为 null 或空
   *
   * @param str 输入字符串
   * @return true 表示字符串为 null 或空，否则返回 false
   */
  private static boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * 获取安全的词性列表，避免 null 引发异常
   *
   * @param document HTML 文档对象
   * @return 词性列表，若获取失败返回空列表
   */
  private static List<String> getSafePartOfSpeechList(Document document) {
    try {
      List<String> partOfSpeechList = getPartOfSpeech(document);
      log.debug("获取词性列表成功: {}", partOfSpeechList);
      return partOfSpeechList;
    } catch (Exception e) {
      log.error("获取词性列表失败", e);
      return Collections.emptyList();
    }
  }

  /**
   * 获取安全的音标对象，避免 null 引发异常
   *
   * @param document HTML 文档对象
   * @return WordPronunciation 对象，若获取失败返回 null
   */
  private static WordPronunciation getSafePhoneticSymbol(Document document) {
    try {
      WordPronunciation wordPronunciation = getPhoneticSymbol(document);
      log.debug("获取音标成功: {}", wordPronunciation);
      return wordPronunciation;
    } catch (Exception e) {
      log.error("获取音标失败", e);
      return null;
    }
  }

  /**
   * 获取安全的单词定义列表，避免 null 引发异常
   *
   * @param document HTML 文档对象
   * @return 单词定义列表，若获取失败返回空列表
   */
  private static List<MultiLanguageContent> getSafeDefinitionList(Document document) {
    try {
      List<MultiLanguageContent> definitionList = getDefinitionList(document);
      log.debug("获取单词定义列表成功: {}", definitionList);
      return definitionList;
    } catch (Exception e) {
      log.error("获取单词定义列表失败", e);
      return Collections.emptyList();
    }
  }

  /**
   * 获取安全的例句列表，避免 null 引发异常
   *
   * @param document HTML 文档对象
   * @return 例句列表，若获取失败返回空列表
   */
  private static List<MultiLanguageContent> getSafeSentenceList(Document document) {
    try {
      List<MultiLanguageContent> sentenceList = getSentences(document);
      log.debug("获取例句列表成功: {}", sentenceList);
      return sentenceList;
    } catch (Exception e) {
      log.error("获取例句列表失败", e);
      return Collections.emptyList();
    }
  }

  /**
   * 获取单词详情
   *
   * @param htmlStr html字符串
   * @return wordDetail
   */
  public static WordDetail getWordDetailFromHtmlStrBackupUglyVersion(String htmlStr) {
    if (htmlStr == null || htmlStr.isEmpty()) {
      return null;
    }
    Document document = Jsoup.parse(htmlStr);
    WordDetail wordDetail = new WordDetail();
    List<String> posList = getPartOfSpeech(document);
    wordDetail.setPartOfSpeechList(posList);
    WordPronunciation wordPronunciation = getPhoneticSymbol(document);
    wordDetail.setWordPronunciation(wordPronunciation);
    List<MultiLanguageContent> definitionList = getDefinitionList(document);
    wordDetail.setDefinitionList(definitionList);
    List<MultiLanguageContent> sentenceList = getSentences(document);
    wordDetail.setSentenceList(sentenceList);
    return wordDetail;
  }

  /**
   * 获取单词详情
   *
   * @param document document
   * @return wordDetail
   */
  public static WordDetail getWordDetail(Document document) {
    WordDetail wordDetail = new WordDetail();
    List<String> posList = getPartOfSpeech(document);
    wordDetail.setPartOfSpeechList(posList);
    WordPronunciation wordPronunciation = getPhoneticSymbol(document);
    wordDetail.setWordPronunciation(wordPronunciation);
    List<MultiLanguageContent> definitionList = getDefinitionList(document);
    wordDetail.setDefinitionList(definitionList);
    List<MultiLanguageContent> sentenceList = getSentences(document);
    wordDetail.setSentenceList(sentenceList);
    return wordDetail;
  }


  /**
   * 获取词性
   *
   * @param document document
   */
  public static List<String> getPartOfSpeech(Document document) {
    List<String> posList = new ArrayList<>();
    // 选择class为pos的span元素，获取其文本
    Elements posElements = document.select("span.pos");

    // 输出所有class为pos的元素的文本
    for (Element element : posElements) {
//      System.out.println(element.text()); // 输出: noun
      posList.add(element.text());  // 词性: noun
    }
    return posList;
  }


  /**
   * 获取词性
   *
   * @param document document
   */
  public static WordPronunciation getPhoneticSymbol(Document document) {

    WordPronunciation wordPronunciation = new WordPronunciation();
    // 选择英式发音的音标（geo="br"）
    Elements britishPronunciation = document.select("div.phons_br span.phon");

    // 选择美式发音的音标（geo="n_am"）
    Elements americanPronunciation = document.select(
      "div.phons_n_am span.phon");

    // 输出英式发音的音标
//    System.out.println("British Pronunciation:");
    for (Element element : britishPronunciation) {
//      System.out.println(element.text());  // 输出：/ˈælkəmɪst/

      wordPronunciation.setBritishPronunciation(element.text());
    }

    // 输出美式发音的音标
//    System.out.println("American Pronunciation:");
    for (Element element : americanPronunciation) {
//      System.out.println(element.text());  // 输出：/ˈælkəmɪst/
//      posList.add(element.text());
      wordPronunciation.setAmericanPronunciation(element.text());
    }

    return wordPronunciation;
  }


  /**
   * 获取句子
   *
   * @param document document
   */
  public static List<MultiLanguageContent> getSentences(Document document) {
    List<MultiLanguageContent> sentenceList = new ArrayList<>();

    // 选择英文例句
    Elements exampleSentences = document.select(".exText .x");

    // 选择简体中文翻译
    Elements simplifiedChinese = document.select(".exText .x .simple");

    // 选择繁体中文翻译
    Elements traditionalChinese = document.select(".exText .x .traditional");

//    // 创建列表保存解析结果
//    List<String> exampleList = new ArrayList<>();
//    List<String> simplifiedList = new ArrayList<>();
//    List<String> traditionalList = new ArrayList<>();

    int size1 = exampleSentences.size();
    int size2 = simplifiedChinese.size();
    int size3 = traditionalChinese.size();
    if (size1 == size2 && size1 == size3) {

      for (int i = 0; i < size1; i++) {
        MultiLanguageContent multiLanguageContent = new MultiLanguageContent();
        String definitionEnglish = extractEnglishText(
          exampleSentences.get(i).text());
        if (isSentence(definitionEnglish)) {
          multiLanguageContent.setContentEnglish(definitionEnglish);
          multiLanguageContent.setContentSimple(
            simplifiedChinese.get(i).text());
          multiLanguageContent.setContentTraditional(
            traditionalChinese.get(i).text());
          sentenceList.add(multiLanguageContent);
        }
      }
    }

//    // 提取英文例句 #chemistry_sng_1 > ul > li:nth-child(2) > div > span
//    for (Element element : exampleSentences) {
////      Elements ai = element.select("ai"); // #chemistry_sng_1 > ul > li:nth-child(2) > div > span
//      exampleList.add(extractEnglishText(element.text()));  // 英文例句
//    }
//
//    // 提取简体中文翻译
//    for (Element element : simplifiedChinese) {
//      Elements ai = element.select("ai");
//      simplifiedList.add(ai.text());  // 简体中文翻译
//    }
//
//    // 提取繁体中文翻译
//    for (Element element : traditionalChinese) {
//      Elements ai = element.select("ai");
//      traditionalList.add(ai.text());  // 繁体中文翻译
//    }
//
//    // 输出解析结果
//    System.out.println("English Example Sentences:");
//    for (String example : exampleList) {
//      System.out.println(example);
//    }
//
//    System.out.println("\nSimplified Chinese Translations:");
//    for (String simplified : simplifiedList) {
//      System.out.println(simplified);
//    }
//
//    System.out.println("\nTraditional Chinese Translations:");
//    for (String traditional : traditionalList) {
//      System.out.println(traditional);
//    }

    return sentenceList;
  }

  public static boolean isSentence(String str) {
    // 检查字符串是否为空或null
    if (str == null || str.isEmpty()) {
      return false;
    }

    // 获取字符串的最后一个字符
    char lastChar = str.charAt(str.length() - 1);

    // 判断是否为句子结束符
    return lastChar == '.' || lastChar == '?' || lastChar == '!';
  }

  /**
   * 获取词性
   *
   * @param document document
   */
  public static List<String> getDefinition(Document document) {
    List<String> posList = new ArrayList<>();

    // 提取英文解释
    String englishDefinition = document.select("span.def").text();

    // 提取简体中文解释
    String simplifiedChinese = document.select("chn.simple").text();

    // 提取繁体中文解释
    String traditionalChinese = document.select("chn.traditional").text();

    // 输出结果
    System.out.println("English Definition: " + englishDefinition);
    System.out.println("Simplified Chinese: " + simplifiedChinese);
    System.out.println("Traditional Chinese: " + traditionalChinese);

    return posList;
  }


  // 提取英文部分的方法
  public static String extractEnglishText(String text) {
    StringBuilder englishText = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      // 判断是否为中文字符
      if (isChinese(c)) {
        break;  // 遇到中文字符就停止
      }
      englishText.append(c);  // 只添加英文字符
    }
    return englishText.toString().trim();
  }

  // 判断是否是中文字符
  public static boolean isChinese(char c) {
    // 中文字符的Unicode范围
    return c >= '\u4e00' && c <= '\u9fa5';
  }


  public static List<MultiLanguageContent> getDefinitionList(
    Document document) {
    List<MultiLanguageContent> definitionList = new ArrayList<>();
    String classNameTopContainer = "li_sense";
    Elements elements = document.getElementsByClass(classNameTopContainer);
    new MultiLanguageContent();
    MultiLanguageContent multiLanguageContent;
    for (Element element : elements) {
      multiLanguageContent = new MultiLanguageContent();
      Elements def = element.select("span.def");
      for (Element element1 : def) {
//        System.out.println(element1.text());
        multiLanguageContent.setContentEnglish(element1.text());
      }
      // F12查看源码，进入【元素】面板，选中文字后，右键【复制】-》【复制 selector】
      String cssQueryChnSimple = "deft > chn.simple";
      Elements defChnSimple = element.select(cssQueryChnSimple);
      for (Element element1 : defChnSimple) {
//        System.out.println(element1.text());
        multiLanguageContent.setContentSimple(element1.text());
      }

      String cssQueryChnTraditional = "deft > chn.traditional"; // #chemistry_sng_1 > deft > chn.traditional
      Elements defChnTraditional = element.select(cssQueryChnTraditional);
      for (Element element1 : defChnTraditional) {
//        System.out.println(element1.text());
        multiLanguageContent.setContentTraditional(element1.text());
      }
//      System.out.println();
      definitionList.add(multiLanguageContent);
    }

    return definitionList;
  }

}
