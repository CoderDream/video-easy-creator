package com.coderdream.util.mdict;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Java 版网页爬虫之 Jsoup 使用详解
 * <a href="https://zhuanlan.zhihu.com/p/624615302">...</a>
 */
public class JSoupTest {


  @Test
  public void test() {
    System.out.println("test");
  }

  /**
   * 2.1、直接载入 url
   */
  @Test
  @Order(201)
  public void demo201() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println(document.toString());
  }

  private final String FOLDER = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\test\\java\\com\\coderdream\\freeapps\\jsoup\\";

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
      String fileUrl = "demo.html";
      document = Jsoup.parse(new File(FOLDER + fileUrl), "utf-8");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println(document.toString());
  }

  /**
   * 2.3、通过字符串加载文档
   */
  @Test
  @Order(203)
  public void demo203() {
    // 定义一个 html 页面的字符串信息
    String html = "<html><head><title></title></head><body>Hello World</body></html>";
    Document document = Jsoup.parse(html);
    System.out.println(document.toString());
  }

  /**
   * 3.1、从 HTML 获取标题信息
   */
  @Test
  @Order(301)
  public void demo301() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取页面标题
      System.out.println(document.title());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.2、从 HTML 获取元信息
   */
  @Test
  @Order(302)
  public void demo302() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取元信息中的页面描述内容
      String description = document.select("meta[name=description]").first()
        .attr("content");
      System.out.println("Meta description : " + description);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.3、从 HTML 获取头部信息
   */
  @Test
  @Order(303)
  public void demo303() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取页面头部标签信息
      System.out.println(document.head().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.4、从 HTML 获取内容信息
   */
  @Test
  @Order(304)
  public void demo304() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取页面body标签信息
      System.out.println(document.body().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.5、从 HTML 获取页面所有超链接
   */
  @Test
  @Order(305)
  public void demo305() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取HTML页面中的所有链接
      Elements links = document.select("a[href]");
      for (Element link : links) {
        System.out.println(
          "link : " + link.attr("href") + "，text : " + link.text());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.6、从 HTML 获取页面所有图片
   */
  @Test
  @Order(306)
  public void demo306() {
    // 直接加载百度连接，获取百度首页页面信息
    Document document = null;
    try {
      document = Jsoup.connect("https://www.baidu.com").get();
      // 获取HTML页面中的所有链接
      Elements links = document.select("a[href]");
      for (Element link : links) {
        System.out.println(
          "link : " + link.attr("href") + "，text : " + link.text());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.7、从 HTML 获取指定标签的内容
   */
  @Test
  @Order(307)
  public void demo307() {
    String html = "<p><span>hello world</span></p>";
    Document document = Jsoup.parse(html);
    Elements elements = document.getElementsByTag("span");
    System.out.println(elements.toString());
    System.out.println(elements.text());
  }

  /**
   * 3.8、从 HTML 获取指定标签ID的内容
   */
  @Test
  @Order(308)
  public void demo308() {
    String html = "<p><span id=\"span111\">hello world</span></p>";
    Document document = Jsoup.parse(html);
    Element element = document.getElementById("span111");
    System.out.println(element.toString());
    System.out.println(element.text());
  }

  /**
   * 3.9、从 HTML 获取指定标签 class 的内容
   */
  @Test
  @Order(309)
  public void demo309() {
    String html = "<p><span class=\"class111\">hello world</span></p>";
    Document document = Jsoup.parse(html);
    Elements elements = document.getElementsByClass("class111");
    System.out.println(elements.toString());
    System.out.println(elements.text());
  }

  /**
   * 3.10、从 HTML 获取指定标签属性的内容
   */
  @Test
  @Order(310)
  public void demo310() {
    String html = "<p><span datafld=\"11111\">hello world</span></p>";
    Document document = Jsoup.parse(html);
    Elements elements = document.getElementsByAttribute("datafld");
    System.out.println(elements.first().attributes().html());
  }

  /**
   * 3.11、从 HTML 获取页面元素中任意内容
   */
  @Test
  @Order(311)
  public void demo311() {
    // 指定 HTML 文件地址，加载文档
    String fileUrl = FOLDER + "demo311.html";
    Document document = null;
    try {
      document = Jsoup.parse(new File(fileUrl), "utf-8");
      // 获取table标签，一层一层的解析全部元素
      Elements tables = document.body().getElementsByTag("table");

      for (Element table : tables) {
        // 遍历行
        Elements trs = table.getElementsByTag("tr");
        for (int j = 0; j < trs.size(); j++) {
          // 遍历列
          Elements tds = trs.get(j).getElementsByTag("td");
          for (int k = 0; k < tds.size(); k++) {
            int row = j + 1;
            int cell = k + 1;
            String print = "第 " + row + " 行，"
              + "第 " + cell + " 列，"
              + "内容：" + tds.get(k).text();
            System.out.println(print);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 3.12、消除不信任的HTML(以防止XSS)
   */
  @Test
  @Order(312)
  public void demo312() {
    // 原始文件
    String dirtyHTML = "<p><a href='http://www.baidu.com/' onclick='sendCookiesToMe()'>Link</a></p>";
    // 需要清理的标签
    String cleanHTML = Jsoup.clean(dirtyHTML, Whitelist.basic());
    // 输出结果
    System.out.println(cleanHTML);
  }
}
