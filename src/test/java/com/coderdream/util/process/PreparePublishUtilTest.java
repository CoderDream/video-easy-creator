package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PreparePublishUtilTest {

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch009";
    PreparePublishUtil.process(folderPath, subFolder);
  }

  @Test
  void process02() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter001";
    String bookName = "一輩子夠用的英語口語大全集";
    String chapterName = "book02_name.txt";
    PreparePublishUtil.process(folderPath, subFolder,bookName, chapterName);
  }


  @Test
  void process03() {
    String srtFileName = "D:\\0000\\【中英雙語】2025川普就職演講\\【中英雙語】2025川普就職演講.srt";
    PreparePublishUtil.generateDescription(srtFileName);
  }
}
