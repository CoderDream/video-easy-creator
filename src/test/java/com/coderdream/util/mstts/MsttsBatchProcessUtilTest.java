package com.coderdream.util.mstts;

import com.coderdream.util.cd.CdConstants;
import org.junit.jupiter.api.Test;

class MsttsBatchProcessUtilTest {

  @Test
  void process() {
    String bookName = "EnBook010"; // 确保这个路径和文件在测试环境中存在！
    String subFolder = "Chapter001";
    String lang = CdConstants.LANG_EN;
    int groupSize = 5;
    MsttsBatchProcessUtil.process(bookName, subFolder, lang, groupSize);
  }

}
