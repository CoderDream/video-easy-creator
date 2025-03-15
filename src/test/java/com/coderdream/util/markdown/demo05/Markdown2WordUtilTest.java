package com.coderdream.util.markdown.demo05;

import java.io.File;
import org.junit.jupiter.api.Test;

class Markdown2WordUtilTest {

  @Test
  void singleMarkdown() {
    String folderPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts";
    String fileName = "bai-ci-zan-108";
    String mdFileName = folderPath + File.separator + fileName + ".md";
    String docxFileName = folderPath + File.separator + fileName + ".docx";
//    Markdown2WordUtil.singleMarkdown(mdFileName, docxFileName);
  }
}
