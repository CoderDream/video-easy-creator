package com.coderdream.util.daily;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.gemini.TranslationUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class DailyUtilTest {

  @Test
  void process_250102() {

    String folderName = "250102";
    String title = "【BBC六分钟英语】你能闻到家的味道吗？";
    DailyUtil.process(folderName, title);

//    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
//    List<String> folderNameList = FileUtil.readLines(todoFileName, "UTF-8");
//    for (String folderName : folderNameList) {
//      TranslationUtil.genDescription(folderName);
//    }
    // MarkdownFileGenerator
  }

  @Test
  void process_250103() {
    String folderName = "250130";
    TranslationUtil.genDescription(folderName);
  }

  @Test
  void process_250123() {
    String folderName = "250123";
    TranslationUtil.genDescription(folderName);
  }



  @Test
  void process_180927() {

//    String folderName = "180927";
    String folderName = "250130";
    String title = "【BBC六分钟英语】" + CdFileUtil.getArticleTitle(
      folderName);// "【BBC六分钟英语】泰国50年老汤真的能吃吗？";
    DailyUtil.process(folderName, title);

    List<String> commandList = Arrays.asList(
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g",
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }
}
