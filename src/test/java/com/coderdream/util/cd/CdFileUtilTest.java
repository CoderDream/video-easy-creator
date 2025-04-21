package com.coderdream.util.cd;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CdFileUtilTest {

  @Test
  void emptyYoutubeVideoSplitFile() {
    boolean b = CdFileUtil.emptyYoutubeVideoSplitFile();
    if (b) {
      log.info("清空文件成功");
    } else {
      log.error("清空文件失败");
    }
  }
}
