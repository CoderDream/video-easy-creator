package com.coderdream.util.cd;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.DictionaryEntity;
import com.coderdream.entity.WordDetail;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

//@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
//@Rollback(false)
//@ExtendWith(SpringExtension.class)
class CdDictionaryUtilTest {

  @Test
  @Order(101)
  void getWordDetail101() {
    String word = "chemistry";
//    word = "hello";
    WordDetail wordDetail = CdDictionaryUtil.getWordDetail(word);
    assertNotNull(wordDetail);
    log.info("chemistry wordDetail: {}", wordDetail);
  }

  @Test
  @Order(102)
  void getWordDetail102() {
    String word = "hinge";
    WordDetail wordDetail = CdDictionaryUtil.getWordDetail(word);
    assertNotNull(wordDetail);
    log.info("wordDetail: {}", wordDetail);
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
      // 获取单词详情html字符串
      String htmlStr = dictionary.getReserved05();
      // 获取WordDetail实体
      WordDetail wordDetail = CdDictionaryUtil.getWordDetailFromHtmlStr(
        htmlStr);
      log.info("wordDetail: {}", wordDetail);
    }
//        System.out.println(list);
    long end = System.currentTimeMillis();
    System.out.println((end - start));
    System.out.println("end");
  }

  // getWordDetail

  @Test
  @Order(14)
  public void getWordDetail14() {
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

      // 获取单词详情html字符串
      String htmlStr = dictionary.getReserved05();
      // 获取WordDetail实体
      WordDetail wordDetail = CdDictionaryUtil.getWordDetailFromHtmlStr(
        htmlStr);
      log.info("wordDetail: {}", wordDetail);
    }
//        System.out.println(list);
    long end = System.currentTimeMillis();
    System.out.println((end - start));
    System.out.println("end");
  }


  @Test
  @Order(13)
  public void getWordDetail13() {
    long start = System.currentTimeMillis();
//        Mdict4jUtil.getWordDetail("chemistry");

    List<String> list = Arrays.asList("chemistry",
      "alchemist");//  Arrays.asList("chemistry", "alchemist")  ;// readFileContent("1-3500.txt");
//        list = list.stream()
//          .limit(10)
//          .toList();
    String word = "chemistry";
//    String source = CdConstants.OALDPE;
    DictionaryEntity dictionary = CdDictionaryUtil.getDictionaryEntity(
      word, CdConstants.OALDPE);

    System.out.println(dictionary);
//        System.out.println(list);
    long end = System.currentTimeMillis();
    System.out.println((end - start));
    System.out.println("end");
  }
}
