package com.coderdream.util.mdict.dict.util;

import com.coderdream.util.mdict.dict.parser.NormalDictUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class NormalDictUtilTest {

  @Test
  void query() {
    DictContentBean hello = NormalDictUtil.query("hello");
    log.info("hello: {}", hello);
  }

  @Test
  void query_02() {
    DictContentBean incinerator = NormalDictUtil.query("incinerator");
    log.info("incinerator: {}", incinerator);
  }
}
