package com.coderdream.util.mdict.dict.util;

import com.coderdream.util.mdict.dict.parser.SimpleDictUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SimpleDictUtilTest {

  @Test
  void query() {
    DictSimpleContentBean hello = SimpleDictUtil.query("hello");
    log.info("hello: {}", hello);
  }
}
