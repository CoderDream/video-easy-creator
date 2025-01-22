package com.coderdream.util.string;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class ZhConverterUtilTest {

  /**
   * 转为简体 toSimple
   */
  @Test
  void toSimple() {
    String original = "生命不息，奮鬥不止";
    String result = ZhConverterUtil.toSimple(original);
    log.info("toSimple result: {}", result);
    Assertions.assertEquals("生命不息，奋斗不止", result);
  }

  /**
   * 转为繁体 toTraditional
   */
  @Test
  void toTraditional() {
    String original = "生命不息，奋斗不止";
    String result = ZhConverterUtil.toTraditional(original);
    Assertions.assertEquals("生命不息，奮鬥不止", result);
    log.info("toTraditional result: {}", result);
  }

}
