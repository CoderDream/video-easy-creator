package com.coderdream.util.word;

import java.io.File;
import org.junit.jupiter.api.Test;

class Main2Test {

  @Test
  void process() {
    System.out.println("hello world");
    String folderPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts";
    String fileName = "bai-ci-zan-108";
    String mdFileName = folderPath + File.separator + fileName + ".md";
    String docxFileName = folderPath + File.separator + fileName + ".docx";
    Main2.process(mdFileName, docxFileName);
  }
}
