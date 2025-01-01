package com.coderdream.util.mdict.dict.parser;

import com.coderdream.util.mdict.dict.util.DictContentBean;
import com.coderdream.util.mdict.dict.util.DictionaryQueriedUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 词典内容解析类
 */
@Slf4j
public class NormalDictUtil {

  /**
   * 解析 HTML 文件并返回 DictContentBean 对象
   *
   * @param htmlFilePath HTML 文件路径
   * @return DictContentBean 对象
   */
  public static DictContentBean parseHtml(String htmlFilePath) {
    DictContentBean dictContentBean = new DictContentBean();
    try {
      File htmlFile = new File(htmlFilePath);
      Document doc = Jsoup.parse(htmlFile, "UTF-8");
      Element entryElement = doc.selectFirst(".entry");

      if (entryElement != null) {
        parseEntry(entryElement, dictContentBean);
      } else {
        log.warn("未找到 .entry 元素：{}", htmlFilePath);
      }

    } catch (IOException e) {
      log.error("解析 HTML 文件失败：{}", htmlFilePath, e);
    }
    return dictContentBean;
  }

  /**
   * 解析 HTML 字符串并返回 DictContentBean 对象
   *
   * @param htmlString HTML 字符串
   * @return DictContentBean 对象
   */
  public static DictContentBean parseHtmlString(String htmlString) {
    DictContentBean dictContentBean = new DictContentBean();
    try {
      Document doc = Jsoup.parse(htmlString);
      Element entryElement = doc.selectFirst(".entry");

      if (entryElement != null) {
        parseEntry(entryElement, dictContentBean);
      } else {
        log.warn("未找到 .entry 元素：{}", htmlString);
      }

    } catch (Exception e) {
      log.error("解析 HTML 字符串失败：{}", htmlString, e);
    }
    return dictContentBean;
  }


  /**
   * 解析 .entry 元素下的内容
   *
   * @param entryElement    .entry 元素
   * @param dictContentBean DictContentBean 对象
   */
  private static void parseEntry(Element entryElement,
    DictContentBean dictContentBean) {
    // 解析头部信息
    Element topG = entryElement.selectFirst(".top-g");
    if (topG != null) {
      parseTopG(topG, dictContentBean);
    }

    // 解析词条的不同释义
    Elements nGs = entryElement.select(".n-g");
    if (nGs != null) {
      List<DictContentBean.Meaning> meanings = new ArrayList<>();
      for (Element nG : nGs) {
        parseNG(nG, meanings);
      }
      dictContentBean.setMeanings(meanings);
    }

    // 解析发音部分
    Element pronG = entryElement.selectFirst(".pron-g");
    if (pronG != null) {
      parsePronG(pronG, dictContentBean);
    }

    //解析反义词和同义词
    Elements xrGs = entryElement.select(".xr-g");
    if (xrGs != null) {
      parseXrG(xrGs, dictContentBean);
    }
  }

  /**
   * 解析 .top-g 元素
   *
   * @param topG            .top-g 元素
   * @param dictContentBean DictContentBean 对象
   */
  private static void parseTopG(Element topG, DictContentBean dictContentBean) {
    Element h = topG.selectFirst(".h");
    if (h != null) {
      dictContentBean.setHeadword(h.text());
    }

    Elements phonGbs = topG.select(".phon-gb");
    if (!phonGbs.isEmpty()) {
      List<String> phonetics = new ArrayList<>();
      for (Element phonGb : phonGbs) {
        phonetics.add(phonGb.text());
      }
      dictContentBean.setPhonetics(phonetics);

    }

    Element pos = topG.selectFirst(".pos");
    if (pos != null) {
      dictContentBean.setPos(pos.text());
    }
  }


  /**
   * 解析 .n-g 元素
   *
   * @param nG       .n-g 元素
   * @param meanings 释义列表
   */
  private static void parseNG(Element nG,
    List<DictContentBean.Meaning> meanings) {
    DictContentBean.Meaning meaning = new DictContentBean.Meaning();

    Element zN = nG.selectFirst(".z_n");
    if (zN != null) {
      meaning.setNumber(zN.text());
    }

    Element defG = nG.selectFirst(".def-g");
    if (defG != null) {
      Element d = defG.selectFirst(".d");
      if (d != null) {
        meaning.setDefinition(d.text());
        Element chn = d.selectFirst(".chn");
        if (chn != null) {
          meaning.setChineseDefinition(chn.text());
        }
      }
    }

    Elements xGs = nG.select(".x-g");
    if (xGs != null) {
      List<DictContentBean.Example> examples = new ArrayList<>();
      for (Element xG : xGs) {
        DictContentBean.Example example = new DictContentBean.Example();
        Element x = xG.selectFirst(".x");
        if (x != null) {
          example.setExample(x.text());
          Element tx = xG.selectFirst(".tx");
          if (tx != null) {
            example.setTranslation(tx.text());
          }
          examples.add(example);
        }
      }
      meaning.setExamples(examples);
    }

    meanings.add(meaning);
  }

  /**
   * 解析 .pron-g 元素
   *
   * @param pronG           .pron-g 元素
   * @param dictContentBean DictContentBean 对象
   */
  private static void parsePronG(Element pronG,
    DictContentBean dictContentBean) {
    Elements phonGbs = pronG.select(".phon-gb");
    if (!phonGbs.isEmpty()) {
      List<String> pronunciations = new ArrayList<>();
      for (Element phonGb : phonGbs) {
        pronunciations.add(phonGb.text());
      }
      dictContentBean.setPronunciations(pronunciations);
    }
  }


  /**
   * 解析 .xr-g 元素 （反义词和同义词）
   *
   * @param xrGs            .xr-g 元素列表
   * @param dictContentBean DictContentBean 对象
   */
  private static void parseXrG(Elements xrGs, DictContentBean dictContentBean) {
    List<String> synonyms = new ArrayList<>();
    List<String> antonyms = new ArrayList<>();
    for (Element xrG : xrGs) {
      String type = xrG.className();
      Elements xrs = xrG.select(".xr");
      if (xrs != null) {
        for (Element xr : xrs) {
          Element a = xr.selectFirst("a");
          if (a != null) {
            if ("xr-g syn".equals(type)) {
              synonyms.add(a.text());
            } else if ("xr-g opp".equals(type)) {
              antonyms.add(a.text());
            }
          }
        }
      }
    }
    if (!synonyms.isEmpty()) {
      dictContentBean.setSynonyms(synonyms);
    }

    if (!antonyms.isEmpty()) {
      dictContentBean.setAntonyms(antonyms);
    }
  }

  /**
   * 解析 HTML 字符串
   *
   * @param word 单词
   * @return DictContentBean 对象
   */
  public static DictContentBean query(String word) {
    String htmlString = DictionaryQueriedUtil.query(word);
    return NormalDictUtil.parseHtmlString(htmlString);
  }

//  public static void main(String[] args) {
////      String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\main\\java\\com\\coderdream\\util\\mdict\\dict\\util\\realistic.html";
////        DictContentBean dictContentBean = NormalDictUtil.parseHtml(htmlFilePath);
////        System.out.println("from file: " + dictContentBean);
//
//    String htmlString = DictionaryQueriedUtil.query("realistic");
//
//    DictContentBean dictContentBean2 = NormalDictUtil.parseHtmlString(
//      htmlString);
//    System.out.println("from string: " + dictContentBean2);
//  }
}
