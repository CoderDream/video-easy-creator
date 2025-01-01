package com.coderdream.util.mdict.dict.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DictCollinsBean;
import com.coderdream.entity.DictOaldpeBean;
import com.coderdream.util.mdict.dict.parser.HtmlOaldParser.DictOaldBean;
import com.coderdream.util.mdict.dict.util.DictContentBean;
import com.coderdream.util.mdict.dict.util.DictSimpleContentBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WordUtil {

  public static String getWordPhonetics(String word) {

    DictSimpleContentBean dictSimpleContentBean = SimpleDictUtil.query(word);
    if (dictSimpleContentBean != null && StrUtil.isNotBlank(
      dictSimpleContentBean.getPronunciation())) {
//      log.info("DictSimpleContentBean  {}", dictSimpleContentBean);
      return dictSimpleContentBean.getPronunciation();
    }

    DictContentBean dictContentBean = NormalDictUtil.query(word);
    if (dictContentBean != null && CollectionUtil.isNotEmpty(
      dictContentBean.getPronunciations()) &&
      StrUtil.isNotBlank(
        dictContentBean.getPronunciations().stream()
          .findFirst()
          .orElse(""))) {
//      log.info("DictContentBean incinerator: {}", dictContentBean);
      return dictContentBean.getPronunciations().stream().findFirst()
        .orElse("");
    }

    DictCollinsBean dictCollinsBean = HtmlCollinsParser.query(word);
//    if (StrUtil.isNotBlank(
//          dictCollinsBean.getSenses().stream()
//              .findFirst()
//              .orElse(new DictCollinsBean.Sense())
//              .getPronunciation())) {

//    log.info("DictCollinsBean incinerator: {}", dictCollinsBean);
//      return dictCollinsBean.getSenses().stream()
//          .findFirst()
//          .orElse(new DictCollinsBean.Sense())
//          .getPronunciation();
//    }

    DictOaldpeBean dictOaldpeBean = HtmlOaldpeParser.query(word);
    if (dictOaldpeBean != null && StrUtil.isNotBlank(
      dictOaldpeBean.getUkPronunciation())) {
//      log.info("DictOaldpeBean incinerator: {}", dictOaldpeBean);
      return dictOaldpeBean.getUkPronunciation();
    }

    DictOaldBean dictOaldBean = HtmlOaldParser.query(word);
    if (dictOaldBean != null && StrUtil.isNotBlank(
      dictOaldBean.getUkPronunciation())) {
//      log.info("DictOaldBean incinerator: {}", dictOaldBean);
      return dictOaldBean.getUkPronunciation();
    }

    return "  ";
  }

}
