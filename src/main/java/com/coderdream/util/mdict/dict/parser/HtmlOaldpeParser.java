package com.coderdream.util.mdict.dict.parser;

import com.coderdream.entity.DictOaldpeBean;
import com.coderdream.util.CdConstants;
import com.coderdream.util.CdDateTimeUtils;
import com.coderdream.util.mdict.Mdict4jUtil;
import com.coderdream.util.mdict.dict.parser.HtmlOaldParser.DictOaldBean;
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
 * OALDPE 词典内容解析类
 */
@Slf4j
public class HtmlOaldpeParser {

  /**
   * 解析 HTML 文件并返回 DictOaldpeBean 对象
   *
   * @param htmlFilePath HTML 文件路径
   * @return DictOaldpeBean 对象
   */
  public static DictOaldpeBean parseHtml(String htmlFilePath) {
    Instant start = Instant.now();
    DictOaldpeBean dictOaldpeBean = new DictOaldpeBean();
    try {
      File htmlFile = new File(htmlFilePath);
      Document doc = Jsoup.parse(htmlFile, "UTF-8");
      Element entry = doc.selectFirst(".entry");
      if (entry != null) {
        parseEntry(entry, dictOaldpeBean);
      } else {
        log.warn("未找到 .entry 元素：{}", htmlFilePath);
      }
    } catch (IOException e) {
      log.error("解析 HTML 文件失败：{}", htmlFilePath, e);
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("解析HTML文件 {} 耗时：{}， 结果: {}", htmlFilePath,
      CdDateTimeUtils.genMessage(timeElapsed), dictOaldpeBean);

    return dictOaldpeBean;
  }


  /**
   * 解析 HTML 字符串并返回 DictOaldpeBean 对象
   *
   * @param htmlString HTML 字符串
   * @return DictOaldpeBean 对象
   */
  public static DictOaldpeBean parseHtmlString(String htmlString) {
    Instant start = Instant.now();
    DictOaldpeBean dictOaldpeBean = new DictOaldpeBean();
    try {
      Document doc = Jsoup.parse(htmlString);
      Element entry = doc.selectFirst(".entry");
      if (entry != null) {
        parseEntry(entry, dictOaldpeBean);
      } else {
        log.warn("未找到 .entry 元素：{}", htmlString);
      }
    } catch (Exception e) {
      log.error("解析 HTML 字符串失败：{}", htmlString, e);
    }
    Instant finish = Instant.now();
    long timeElapsed = Duration.between(start, finish).toMillis();
    log.info("解析HTML字符串 耗时：{}， 结果: {}",
      CdDateTimeUtils.genMessage(timeElapsed), dictOaldpeBean);
    return dictOaldpeBean;
  }

  /**
   * 解析 .entry 元素下的内容
   *
   * @param entry          .entry 元素
   * @param dictOaldpeBean DictOaldpeBean 对象
   */
  private static void parseEntry(Element entry, DictOaldpeBean dictOaldpeBean) {
    // 解析头部信息
    Element topG = entry.selectFirst(".top-g");
    if (topG != null) {
      parseTopG(topG, dictOaldpeBean);
    }

    // 解析词条的不同释义
    Elements senses = entry.select(".sense");
    if (senses != null) {
      List<DictOaldpeBean.Sense> senseList = new ArrayList<>();
      for (Element sense : senses) {
        parseSense(sense, senseList);
      }
      dictOaldpeBean.setSenses(senseList);
    }
  }


  /**
   * 解析 .top-g 元素
   *
   * @param topG           .top-g 元素
   * @param dictOaldpeBean DictOaldpeBean 对象
   */
  private static void parseTopG(Element topG, DictOaldpeBean dictOaldpeBean) {
    Element headword = topG.selectFirst(".headword");
    if (headword != null) {
      dictOaldpeBean.setHeadword(headword.text());
    }

    Element pos = topG.selectFirst(".pos");
    if (pos != null) {
      dictOaldpeBean.setPos(pos.text());
    }

    Element phonetics = topG.selectFirst(".phonetics");
    if (phonetics != null) {
      // 获取英国音标
      Element phonsBr = topG.selectFirst(".phons_br .phon");
      if (phonsBr != null) {
        dictOaldpeBean.setUkPronunciation(phonsBr.text());
      }

      // 获取美式音标
      Element phonsNa = topG.selectFirst(".phons_n_am .phon");
      if (phonsNa != null) {
        dictOaldpeBean.setUsPronunciation(phonsNa.text());
      }
    }

  }

  /**
   * 解析 .sense 元素
   *
   * @param sense     .sense 元素
   * @param senseList 释义列表
   */
  private static void parseSense(Element sense,
    List<DictOaldpeBean.Sense> senseList) {
    DictOaldpeBean.Sense senseBean = new DictOaldpeBean.Sense();
    Element def = sense.selectFirst(".def");
    if (def != null) {
      senseBean.setDefinition(def.text());
      Element chn = sense.selectFirst("deft chn");
      if (chn != null) {
        senseBean.setTranslation(chn.text());
      }
    }

    Elements examples = sense.select(".examples li .x");
    if (examples != null) {
      List<DictOaldpeBean.Example> exampleList = new ArrayList<>();
      for (Element example : examples) {
        DictOaldpeBean.Example exampleBean = new DictOaldpeBean.Example();
        exampleBean.setExample(example.text());

        Element translation = example.selectFirst("xt chn");
        if (translation != null) {
          exampleBean.setTranslation(translation.text());
        }

        exampleList.add(exampleBean);
      }
      senseBean.setExamples(exampleList);

    }
    senseList.add(senseBean);
  }


  //  public static void main(String[] args) {
//    String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\main\\java\\com\\coderdream\\util\\mdict\\attractive1.html";
//    DictOaldpeBean dictOaldpeBean = HtmlOaldpeParser.parseHtml(htmlFilePath);
//    System.out.println("from file: " + dictOaldpeBean);
//
//    String htmlString = ""; // TODO
//
//  }
  public static DictOaldpeBean query(String word) {
    String htmlString = Mdict4jUtil.getHtmlString(word,
      CdConstants.DICT_OALDPE);
    return HtmlOaldpeParser.parseHtmlString(htmlString);
  }

}
