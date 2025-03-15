package com.coderdream.util.word;

//import com.coderdream.util.markdown.demo05.Markdown2WordUtil;
import java.io.File;

public class Main2 {

  public static void main(String[] args) {
    System.out.println("hello world");
    String folderPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts";
    String fileName = "bai-ci-zan-108";
    String mdFileName = folderPath + File.separator + fileName + ".md";
    String docxFileName = folderPath + File.separator + fileName + ".docx";
    Main2.process(mdFileName, docxFileName);
  }

  public static void process(String mdFileName, String docxFileName) {
//    System.out.println("hello world");
//    String folderPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts";
//    String fileName = "bai-ci-zan-108";
//    String mdFileName = folderPath + File.separator + fileName + ".md";
//    String docxFileName = folderPath + File.separator + fileName + ".docx";
//    Markdown2WordUtil.singleMarkdown(mdFileName, docxFileName);
  }
}
