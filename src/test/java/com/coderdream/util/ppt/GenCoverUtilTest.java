package com.coderdream.util.ppt;

import com.coderdream.util.proxy.OperatingSystem;
import org.junit.jupiter.api.Test;

class GenCoverUtilTest {

  @Test
  void process() {
    String presentationName = "D:\\0000\\ppt\\Book02\\Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName, imageFormat);
  }

  @Test
  void process_02() {
    String presentationName = "D:\\0000\\ppt\\Book02\\Book02模板.pptx";
    String chapterFileName = "book02_name.txt";
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String imageFormat = "jpg";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName, imageFormat);
  }
}
