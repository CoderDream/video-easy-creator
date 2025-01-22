package com.coderdream.util.mdict;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.coderdream.entity.DictionaryEntity;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdDictionaryUtil;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback(false)
@ExtendWith(SpringExtension.class)
class Mdict4jDemo2Test {

  @Test
  @Order(11)
  public void getWordDetail() {
    long start = System.currentTimeMillis();
//        Mdict4jUtil.getWordDetail("hello");

    List<String> list = readFileContent("1-3500.txt");
    list = list.stream()
      .limit(10)
      .toList();

    List<DictionaryEntity> dictionaries = new ArrayList<>();

    // 创建新的字典对象
    new DictionaryEntity();
    DictionaryEntity newDictionary;
    for (String word : list) {
      System.out.println(word);
//            log.info("s:{}", word);
      try {

        // 创建新的字典对象
        newDictionary = new DictionaryEntity();
        HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word,
          "collins");
        newDictionary.setSource("collins");
        newDictionary.setWord(word);
        newDictionary.setReserved01(htmlContentBean.getRatingText());
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

        dictionaries.add(newDictionary);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    for (DictionaryEntity dictionary : dictionaries) {
      System.out.println(dictionary);
    }
//        System.out.println(list);
    long end = System.currentTimeMillis();
    System.out.println((end - start));
    System.out.println("end");
  }

  @Test
  @Order(12)
  public void getWordDetail12() {
    long start = System.currentTimeMillis();
//        Mdict4jUtil.getWordDetail("chemistry");

    List<String> list = Arrays.asList("chemistry",
      "alchemist");//  Arrays.asList("chemistry", "alchemist")  ;// readFileContent("1-3500.txt");
//        list = list.stream()
//          .limit(10)
//          .toList();

    String source = "Oxford10"; // collins
    source = "oaldpe";
    source = CdConstants.OALDPE;
    List<DictionaryEntity> dictionaries = CdDictionaryUtil.getDictionaryEntityList(
      list, source);

    for (DictionaryEntity dictionary : dictionaries) {
      System.out.println(dictionary);
    }
//        System.out.println(list);
    long end = System.currentTimeMillis();
    System.out.println((end - start));
    System.out.println("end");
  }


  @Test
  void getDictInfo() {
  }

  /**
   * 读取resources文件夹下13500文件夹中的1-3500.txt文件并返回内容列表
   *
   * @return 文件内容的列表
   */
  public static List<String> readFileContent(String filename) {
    // 获取资源的URL
    String resourcePath = "classpath:13500/" + filename;
    try {
      // 使用HuTool的ResourceUtil获取资源路径
      // 指定要下载的文件
      File file = ResourceUtils.getFile(resourcePath);
      // 定义UTF-16 Little Endian编码
      Charset utf16Le = StandardCharsets.UTF_16LE;
      // 读取文件内容到列表
//            return FileUtil.readLines(file, "UTF-8");
      List<String> lines = FileUtil.readLines(file, utf16Le);
      // 移除每行首尾空格，并过滤掉空行
      return lines.stream()
        .map(String::trim)
        .filter(line -> !line.isEmpty())
        .collect(Collectors.toList());
    } catch (Exception e) {
//            e.printStackTrace();
      log.error("读取文件失败: {}", e.getMessage());
      // 抛出运行时异常或进行其他错误处理
      throw new RuntimeException("读取文件失败", e);
    }
  }
}
