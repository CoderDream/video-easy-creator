package com.coderdream.util.nlp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
class CoreNlpUtilsTest {

  @Test
  void getLemmaList() {

    List<String> stringList = Arrays.asList("I", "ORANGE","Orange", "MARMALADE", "New",
      "lit", "a");

    // 获取词形还原后的结果
    Map<String, String> stringMap = CoreNlpUtils.getLemmaList(stringList);
    assert stringMap != null;
    for (Map.Entry<String, String> entry : stringMap.entrySet()) {
      String mapKey = entry.getKey();
      String mapValue = entry.getValue();
      log.error("{}:{}", mapKey, mapValue);
    }
  }
}
