package com.coderdream.util.cd;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TextProcessorTest {

  @Test
  void processFile() {
    String folderName = "170202";
    String srtEngRawFileName = CommonUtil.getFullPathFileName(folderName,
      "eng_raw",
      CdConstants.SRT_EXTENSION);

    String result = TextProcessor.processFile(srtEngRawFileName);
    log.info("result: {}", result);
  }
}
