package com.coderdream.util.mdict;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.CdConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback(false)
@ExtendWith(SpringExtension.class)
class Mdict4jUtilTest {

  @Test
  void getHtmlContentBean() {
  }

  @Test
  void getMdxFilePath01() {
    String dictType = "";
    String result = Mdict4jUtil.getMdxFilePath(dictType);
    assertNotNull(result);
    log.info("mdxFilePath: {}", result);
  }

  /**
   * 牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx
   */
  @Test
  void getMdxFilePath02() {
    String dictType = CdConstants.OALDPE;
    String result = Mdict4jUtil.getMdxFilePath(dictType);
    assertNotNull(result);
    assertEquals("D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\dict\\oaldpe.mdx", result);
    log.info("mdxFilePath: {}", result);
  }

  @Test
  void genDictionaryEntryList() {
  }

  @Test
  void testGenDictionaryEntryList() {
  }

  @Test
  void genDictionaryEntry() {
  }

  @Test
  void getDictInfo() {
  }
}
