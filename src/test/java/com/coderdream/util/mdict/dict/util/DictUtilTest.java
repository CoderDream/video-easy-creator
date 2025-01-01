package com.coderdream.util.mdict.dict.util;

import com.coderdream.entity.DictCollinsBean;
import com.coderdream.entity.DictOaldpeBean;
import com.coderdream.util.mdict.dict.parser.HtmlCollinsParser;
import com.coderdream.util.mdict.dict.parser.HtmlOaldParser;
import com.coderdream.util.mdict.dict.parser.HtmlOaldParser.DictOaldBean;
import com.coderdream.util.mdict.dict.parser.HtmlOaldpeParser;
import com.coderdream.util.mdict.dict.parser.NormalDictUtil;
import com.coderdream.util.mdict.dict.parser.SimpleDictUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DictUtilTest {

  private static final String SAMPLE_WORD = "incinerator";

  @Test
  @Order(1)
  void query_01() {
    DictSimpleContentBean dictSimpleContentBean = SimpleDictUtil.query(SAMPLE_WORD);
    log.info("DictSimpleContentBean  {}", dictSimpleContentBean);
  }

  @Test
  @Order(2)
  void query_02() {
    DictContentBean dictContentBean = NormalDictUtil.query(SAMPLE_WORD);
    log.info("DictContentBean incinerator: {}", dictContentBean);
  }

  @Test
  @Order(3)
  void query_03() {
    DictCollinsBean dictCollinsBean = HtmlCollinsParser.query(SAMPLE_WORD);
    log.info("DictCollinsBean incinerator: {}", dictCollinsBean);
  }

  @Test
  @Order(4)
  void query_04() {
    DictOaldpeBean dictOaldpeBean = HtmlOaldpeParser.query(SAMPLE_WORD);
    log.info("DictOaldpeBean incinerator: {}", dictOaldpeBean);
  }

  @Test
  @Order(5)
  void query_05() {
    DictOaldBean dictOaldBean = HtmlOaldParser.query(SAMPLE_WORD);
    log.info("DictOaldBean incinerator: {}", dictOaldBean);
  }
}
