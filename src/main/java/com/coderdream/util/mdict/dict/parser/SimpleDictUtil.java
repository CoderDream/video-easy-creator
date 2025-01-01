package com.coderdream.util.mdict.dict.parser;

import com.coderdream.util.mdict.dict.util.DictSimpleContentBean;
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
 * 词典内容解析类，用于解析简化的词典内容
 */
@Slf4j
public class SimpleDictUtil {
  /**
   * 解析 HTML 文件并返回 DictSimpleContentBean 对象
   *
   * @param htmlFilePath HTML 文件路径
   * @return DictSimpleContentBean 对象
   */
  public static DictSimpleContentBean parseHtml(String htmlFilePath) {
    DictSimpleContentBean dictSimpleContentBean = new DictSimpleContentBean();
    try {
      File htmlFile = new File(htmlFilePath);
      Document doc = Jsoup.parse(htmlFile, "UTF-8");
      Element entryBody = doc.selectFirst(".entry-body");

      if (entryBody != null) {
        parseEntry(entryBody, dictSimpleContentBean);
      } else {
        log.warn("parseHtml 未找到 .entry-body 元素：{}", htmlFilePath);
      }

    } catch (IOException e) {
      log.error("解析 HTML 文件失败：{}", htmlFilePath, e);
    }
    return dictSimpleContentBean;
  }


  /**
   * 解析 HTML 字符串并返回 DictSimpleContentBean 对象
   *
   * @param htmlString HTML 字符串
   * @return DictSimpleContentBean 对象
   */
  public static DictSimpleContentBean parseHtmlString(String htmlString) {
    DictSimpleContentBean dictSimpleContentBean = new DictSimpleContentBean();
    try {
      Document doc = Jsoup.parse(htmlString);
      Element entryBody = doc.selectFirst(".entry-body");
      if (entryBody != null) {
        parseEntry(entryBody, dictSimpleContentBean);
      } else {
        log.warn("parseHtmlString 未找到 .entry-body 元素：{}", htmlString);
      }

    } catch (Exception e) {
      log.error("解析 HTML 字符串失败：{}", htmlString, e);
    }
    return dictSimpleContentBean;
  }


  /**
   * 解析 .entry-body 元素下的内容
   *
   * @param entryBody             .entry-body 元素
   * @param dictSimpleContentBean DictSimpleContentBean 对象
   */
  private static void parseEntry(Element entryBody,
    DictSimpleContentBean dictSimpleContentBean) {
    // 解析头部信息
    Element diHead = entryBody.selectFirst(".di-head");
    if (diHead != null) {
      parseDiHead(diHead, dictSimpleContentBean);
    }

    // 解析词条的不同释义
    Elements senseBlocks = entryBody.select(".sense-block");
    if (senseBlocks != null) {
      List<DictSimpleContentBean.Sense> senses = new ArrayList<>();
      for (Element senseBlock : senseBlocks) {
        parseSenseBlock(senseBlock, senses);
      }
      dictSimpleContentBean.setSenses(senses);
    }
  }


  /**
   * 解析 .di-head 元素
   *
   * @param diHead                .di-head 元素
   * @param dictSimpleContentBean DictSimpleContentBean 对象
   */
  private static void parseDiHead(Element diHead,
    DictSimpleContentBean dictSimpleContentBean) {
    Element diTitle = diHead.selectFirst(".di-title");
    if (diTitle != null) {
      dictSimpleContentBean.setHeadword(diTitle.text());
    }

    Element pos = diHead.selectFirst(".pos");
    if (pos != null) {
      dictSimpleContentBean.setPos(pos.text());
    }

    Element pron = diHead.selectFirst(".pron");
    if (pron != null) {
      // 去除 / / 符号
      String pronText = pron.text().replaceAll("/", "");
      dictSimpleContentBean.setPronunciation(pronText);
    }

    Elements ukPron = diHead.select(".uk_pron");
    if (!ukPron.isEmpty()) {
      dictSimpleContentBean.setUkPronunciation(true);
    }

    Elements usPron = diHead.select(".us_pron");
    if (!usPron.isEmpty()) {
      dictSimpleContentBean.setUsPronunciation(true);
    }

  }

  /**
   * 解析 .sense-block 元素
   *
   * @param senseBlock .sense-block 元素
   * @param senses     释义列表
   */
  private static void parseSenseBlock(Element senseBlock,
    List<DictSimpleContentBean.Sense> senses) {
    DictSimpleContentBean.Sense sense = new DictSimpleContentBean.Sense();

    Element defBlock = senseBlock.selectFirst(".def-block");
    if (defBlock != null) {
      Element def = defBlock.selectFirst(".def");
      if (def != null) {
        sense.setDefinition(def.text());
        Element trans = defBlock.selectFirst(".trans");
        if (trans != null) {
          sense.setTranslation(trans.text());
        }
      }

      Elements exampElements = defBlock.select(".examp");
      if (exampElements != null) {
        List<DictSimpleContentBean.Example> examples = new ArrayList<>();
        for (Element exampElement : exampElements) {
          DictSimpleContentBean.Example example = new DictSimpleContentBean.Example();
          example.setExample(exampElement.text());
          Element trans = exampElement.selectFirst(".trans");
          if (trans != null) {
            example.setTranslation(trans.text());
          }
          examples.add(example);
        }
        sense.setExamples(examples);
      }

    }

    senses.add(sense);
  }

  public static DictSimpleContentBean query(String word) {
    String htmlString = DictionaryQueriedUtil.query(
      "Cambridge English-Chinese (Simplified) Dictionary.mdx", word);
    return SimpleDictUtil.parseHtmlString(
      htmlString);
  }

  public static void main(String[] args) {
    String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\main\\java\\com\\coderdream\\util\\mdict\\dict\\util\\realistic2.html";
    DictSimpleContentBean dictSimpleContentBean = SimpleDictUtil.parseHtml(
      htmlFilePath);
    System.out.println("from file: " + dictSimpleContentBean);

    String htmlString = DictionaryQueriedUtil.query(
      "Cambridge English-Chinese (Simplified) Dictionary.mdx", "realistic");

    DictSimpleContentBean dictSimpleContentBean2 = SimpleDictUtil.parseHtmlString(
      htmlString);
    System.out.println("from string: " + dictSimpleContentBean2);
  }
}
