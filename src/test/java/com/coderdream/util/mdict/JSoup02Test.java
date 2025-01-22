package com.coderdream.util.mdict;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.coderdream.entity.WordDetail;
import com.coderdream.util.cd.CdDictionaryUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 *
 */
@Slf4j
public class JSoup02Test {

  /**
   * 2.2、通过文件加载文档
   */
  @Test
  @Order(202)
  public void demo202() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      // 指定 HTML 文件地址，加载文档
      String fileName = "word06.html";
      File file = getTestFile(fileName);
      document = Jsoup.parse(file, "utf-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println(document.toString());
  }

  // top-container

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(101)
  public void word101() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word06.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");

      String classNameTopContainer = "top-container";
      Elements elements = document.getElementsByClass(classNameTopContainer);
      System.out.println(elements.toString());
      System.out.println(elements.text());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(1021)
  public void word1021() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word05.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");

      String classNameTopContainer = "top-container";
      Elements elements = document.getElementsByClass(classNameTopContainer);
      System.out.println(elements.toString());
      System.out.println(elements.text());

      List<String> partOfSpeech = CdDictionaryUtil.getPartOfSpeech(document);
      if (!partOfSpeech.isEmpty()) {
        System.out.println("词性: " + partOfSpeech.get(0));
      }

      CdDictionaryUtil.getPhoneticSymbol(document);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  //

  @Test
  @Order(1022)
  public void word1022() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word05.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");

      String classNameTopContainer = "sensetop";
      Elements elements = document.getElementsByClass(classNameTopContainer);
      System.out.println(elements.toString());
      System.out.println(elements.text());


    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取定义
   */
  @Test
  @Order(1023)
  public void getDefinition() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word05.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");

      CdDictionaryUtil.getDefinitionList(document);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(103)
  public void word103() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word06.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");

      String classNameTopContainer = "top-container";
      Elements elements = document.getElementsByClass(classNameTopContainer);
//      System.out.println(elements.toString());
//      System.out.println(elements.text());

      List<String> partOfSpeech = CdDictionaryUtil.getPartOfSpeech(document);
      if (!partOfSpeech.isEmpty()) {
        System.out.println("词性: " + partOfSpeech.get(0));
      }

      CdDictionaryUtil.getPhoneticSymbol(document);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(104)
  public void word104() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word05.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");
      CdDictionaryUtil.getSentences(document);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(1055)
  public void word1055() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word05.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");
      CdDictionaryUtil.getSentences(document);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(1056)
  public void word1056() {
    // 指定 HTML 文件地址，加载文档
    String fileName = "word06.html";
    File file = getTestFile(fileName);

    Document document = null;
    try {
      document = Jsoup.parse(file, "utf-8");
      CdDictionaryUtil.getSentences(document);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(1050)
  public void word1050() {

    List<String> fileNames = Arrays.asList("word05.html", "word06.html");
    for (String fileName : fileNames) {
      File file = getTestFile(fileName);
      Document document = null;
      try {
        document = Jsoup.parse(file, "utf-8");
        CdDictionaryUtil.getDefinition(document);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }


  /**
   * 101、获取指定class的元素
   */
  @Test
  @Order(2050)
  public void word2050() {

    List<String> fileNames = Arrays.asList("word05.html", "word06.html");
    for (String fileName : fileNames) {
      File file = getTestFile(fileName);
      Document document = null;
      try {
        document = Jsoup.parse(file, "utf-8");
        WordDetail wordDetail = CdDictionaryUtil.getWordDetail(document);
        log.info("wordDetail: {}", wordDetail);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  //

  // Elements parentElements = document.select("div.entry .pos");
  // 方法 2：使用其他选择器（如果需要根据上下文进一步定位）
  //  除了 span.pos，你还可以使用其他 CSS 选择器来定位 noun，例如：
  // 方法 3：通过 attr() 方法查找特定属性的元素
  //如果你想更精确地查找元素，比如根据 hclass 或 htag 属性来定位，可以使用 attr() 方法。例如：
  //
  //java
  //复制代码
  //Elements posElements = document.select("span.pos[hclass=pos]");
  //这个选择器查找所有 class 为 pos 且 hclass 为 pos 的 span 元素。
  //
  //这几种方法都可以根据你的需要来选择，最常用的是直接使用 .select(".pos") 来查找带有特定类名的元素。






  // =========================================================================


  // 抽象方法：获取当前类同级的word06.html文件
  public File getTestFile(String fileName) {
    URL url = getClass().getClassLoader().getResource(fileName);
    if (url != null) {
      return new File(url.getPath());
    }
    return null;  // 文件未找到
  }

  // JUnit 测试方法
  @Test
  public void testGetWord06File() {
    File file = getTestFile("word06.html");
    assertNotNull(file, "文件应该存在");
    System.out.println("文件路径: " + file.getAbsolutePath());
  }
}
