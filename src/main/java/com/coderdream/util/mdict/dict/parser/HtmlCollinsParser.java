package com.coderdream.util.mdict.dict.parser;

import com.coderdream.entity.DictCollinsBean;
import com.coderdream.util.CdConstants;
import com.coderdream.util.CdDateTimeUtils;
import com.coderdream.util.mdict.Mdict4jUtil;
import com.coderdream.util.mdict.dict.util.DictSimpleContentBean;
import com.coderdream.util.mdict.dict.util.DictionaryQueriedUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Collins 词典内容解析类
 */
@Slf4j
public class HtmlCollinsParser {

  /**
   * 解析 HTML 文件并返回 DictCollinsBean 对象
   *
   * @param htmlFilePath HTML 文件路径
   * @return DictCollinsBean 对象
   */
  public static DictCollinsBean parseHtml(String htmlFilePath) {
    Instant start = Instant.now();
    DictCollinsBean dictCollinsBean = new DictCollinsBean();
    try {
      File htmlFile = new File(htmlFilePath);
      Document doc = Jsoup.parse(htmlFile, "UTF-8");
      Element partMain = doc.selectFirst(".part_main");
      if (partMain != null) {
        parsePartMain(partMain, dictCollinsBean);
      } else {
        log.warn("未找到 .part_main 元素：{}", htmlFilePath);
      }
    } catch (IOException e) {
      log.error("解析 HTML 文件失败：{}", htmlFilePath, e);
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("解析HTML文件 {} 耗时：{}， 结果: {}", htmlFilePath,
      CdDateTimeUtils.genMessage(timeElapsed), dictCollinsBean);
    return dictCollinsBean;
  }

  /**
   * 解析 HTML 字符串并返回 DictCollinsBean 对象
   *
   * @param htmlString HTML 字符串
   * @return DictCollinsBean 对象
   */
  public static DictCollinsBean parseHtmlString(String htmlString) {
    Instant start = Instant.now();
    DictCollinsBean dictCollinsBean = new DictCollinsBean();
    try {
      Document doc = Jsoup.parse(htmlString);
      Element partMain = doc.selectFirst(".part_main");
      if (partMain != null) {
        parsePartMain(partMain, dictCollinsBean);
      } else {
        log.warn("未找到 .part_main 元素：{}", htmlString);
      }
    } catch (Exception e) {
      log.error("解析 HTML 字符串失败：{}", htmlString, e);
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("解析HTML字符串 耗时：{}， 结果: {}",
      CdDateTimeUtils.genMessage(timeElapsed), dictCollinsBean);
    return dictCollinsBean;
  }


  /**
   * 解析 .part_main 元素下的内容
   *
   * @param partMain        .part_main 元素
   * @param dictCollinsBean DictCollinsBean 对象
   */
  private static void parsePartMain(Element partMain,
    DictCollinsBean dictCollinsBean) {
    // 解析词条的不同释义
    Elements collinsEnCns = partMain.select(".collins_en_cn");
    if (collinsEnCns != null) {
      List<DictCollinsBean.Sense> senses = new ArrayList<>();
      for (Element collinsEnCn : collinsEnCns) {
        parseCollinsEnCn(collinsEnCn, senses);
      }
      dictCollinsBean.setSenses(senses);
    }
  }

  /**
   * 解析 .collins_en_cn 元素
   *
   * @param collinsEnCn .collins_en_cn 元素
   * @param senses      释义列表
   */
  private static void parseCollinsEnCn(Element collinsEnCn,
    List<DictCollinsBean.Sense> senses) {
    DictCollinsBean.Sense sense = new DictCollinsBean.Sense();
    Element caption = collinsEnCn.selectFirst(".caption");
    if (caption != null) {
      Element num = caption.selectFirst(".num");
      if (num != null) {
        sense.setNumber(num.text());
      }

      Element st = caption.selectFirst(".st");
      if (st != null) {
        sense.setPos(st.text());
      }
      Element textBlue = caption.selectFirst(".text_blue");
      if (textBlue != null) {
        sense.setDefinition(textBlue.text());
      }

      String text = caption.text();
      if (text != null) {
        sense.setCaption(text);
      }
    }

    Elements lis = collinsEnCn.select("ul li");
    if (lis != null) {
      List<DictCollinsBean.Example> examples = new ArrayList<>();
      for (Element li : lis) {
        DictCollinsBean.Example example = new DictCollinsBean.Example();
        Element p = li.selectFirst("p");
        if (p != null) {
          example.setExample(p.text());
          Element textBlue = p.selectFirst(".text_blue");
          if (textBlue != null) {
            example.setExampleWithHighlight(p.html());
          }
          examples.add(example);
        }
      }
      sense.setExamples(examples);
    }

    senses.add(sense);
  }

  public static DictCollinsBean query(String word) {
    String htmlString = Mdict4jUtil.getHtmlString(word, CdConstants.DICT_COLLINS);
    return HtmlCollinsParser.parseHtmlString(htmlString);
  }

//  public static void main(String[] args) {
//    String word = "chemistry";
//    String dictType = "collins";
//    String htmlString = Mdict4jUtil.getHtmlString(word, dictType);
//    // "D:\\04_GitHub\\video-easy-creator\\src\\test\\java\\com\\coderdream\\util\\mdict\\chemistry1.html";
//    DictCollinsBean dictCollinsBean = HtmlCollinsParser.parseHtmlString(htmlString);
//    System.out.println("from file: " + dictCollinsBean);
//
////    String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\test\\java\\com\\coderdream\\util\\mdict\\chemistry1.html";
////    DictCollinsBean dictCollinsBean = HtmlCollinsParser.parseHtml(htmlFilePath);
////    System.out.println("from file: " + dictCollinsBean);
////
////    String htmlString = "<font size=+1 color=purple>chemistry</font><font color=gold> ★★☆☆☆</font><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><link href=\"collins.css\" rel=\"stylesheet\" type=\"text/css\" /></head><div class=\"tab_content\" id=\"dict_tab_101\" style=\"display:block\"><div class=\"part_main\"><div class=\"collins_content\"><div class=\"collins_en_cn\"><div class=\"caption\"><span class=\"num\">1.</span><span class=\"st\" tid=\"1_10298\">N-UNCOUNT	不可数名词</span><span class=\"text_blue\">化学</span> <b>Chemistry</b> is the scientific study of the structure of substances and of the way that they react with other substances. </div><ul></ul></div><div class=\"collins_en_cn\"><div class=\"caption\"><span class=\"num\">2.</span><span class=\"st\" tid=\"2_10299\">N-UNCOUNT	不可数名词</span><span class=\"text_blue\">(物质的)化学组成，化学成分，化学反应</span> The <b>chemistry</b> of an organism or a material is the chemical substances that make it up and the chemical reactions that go on inside it. <span ><div   id=\"word_gram_2_10299\"><div ><div><br>【搭配模式】：usu with supp</div></div></div></span></div><ul><li ><p>We have literally altered the <span class='text_blue'>chemistry</span> of our planet's atmosphere...</p><p>我们确实改变了地球大气层的化学构成。</p></li><li ><p>If the supply of vitamins and minerals in the diet is inadequate, this will result in changes in body <span class='text_blue'>chemistry</span>.</p><p>如果饮食中的维生素和矿物质含量太低，将会引起人体化学结构的变化。</p></li></ul></div><div class=\"collins_en_cn\"><div class=\"caption\"><span class=\"num\">3.</span><span class=\"st\" tid=\"3_10300\">N-UNCOUNT	不可数名词</span><span class=\"text_blue\">(互相之间的)吸引，亲密</span> If you say that there is <b>chemistry</b> between two people, you mean that is obvious they are attracted to each other or like each other very much. </div><ul><li ><p>...the extraordinary <span class='text_blue'>chemistry</span> between Ingrid and Bogart...</p><p>英格丽和鲍嘉之间的情意绵绵</p></li><li ><p>Janis and I became friends but we were never close. The <span class='text_blue'>chemistry</span> wasn't there.</p><p>我和贾妮斯成了朋友，可我们从未走得太近，没有那种感觉。</p></li></ul></div></div></div></div>";
////    DictCollinsBean dictCollinsBean2 = HtmlCollinsParser.parseHtmlString(
////      htmlString);
////    System.out.println("from string: " + dictCollinsBean2);
//  }
}
