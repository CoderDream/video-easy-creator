package com.coderdream.util.daily;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.gemini.TranslationUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.coderdream.util.proxy.OperatingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DailyUtilTest {

  @Test
  void process_250102() {

    String folderName = "250102";
    String title = "【BBC六分钟英语】你能闻到家的味道吗？";
    DailyUtil.process(folderName, title);

//    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
//    List<String> folderNameList = CdFileUtil.readLines(todoFileName, "UTF-8");
//    for (String folderName : folderNameList) {
//      TranslationUtil.genDescription(folderName);
//    }
    // MarkdownFileGenerator
  }

  @Test
  void process_250103() {
    String folderName = "181213";
    TranslationUtil.genDescription(folderName);
  }

  @Test
  void process_250123() {
    String folderName = "170105";
    TranslationUtil.genDescription(folderName);
  }

  @Test
  void processBatch02() {
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      TranslationUtil.genDescription(folderName);
    }
  }

  @Test
  void process_180927() {
    String folderName = "170330";
//    String folderName = "250213";
    String title = "【BBC六分钟英语】" + com.coderdream.util.cd.CdFileUtil.getArticleTitle(
      folderName);// "【BBC六分钟英语】泰国50年老汤真的能吃吗？";
    DailyUtil.process(folderName, title);

    String baseHexoFolder = OperatingSystem.getHexoFolder();

    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  @Test
  void process_deploy() {
    String baseHexoFolder = OperatingSystem.getHexoFolder();
    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  void process_todo() {
    String folderName = "170330";
//    String folderName = "250213";
    String title = "【BBC六分钟英语】" + com.coderdream.util.cd.CdFileUtil.getArticleTitle(
      folderName);// "【BBC六分钟英语】泰国50年老汤真的能吃吗？";
    DailyUtil.process(folderName, title);

    String baseHexoFolder = OperatingSystem.getHexoFolder();

    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  private List<String> NUMBER_LIST;

  @BeforeEach
  void init() {
    String folderPath =
      com.coderdream.util.cd.CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "bbc"
        + File.separatorChar;

    NUMBER_LIST = FileUtil.readLines(folderPath + "todo.txt", "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));
  }

  @Test
  void processBatch() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      String title = "【BBC六分钟英语】" + com.coderdream.util.cd.CdFileUtil.getArticleTitle(
        folderName);
      DailyUtil.process(folderName, title);
//      TranslationUtil.genDescription(folderName);
    }

    String baseHexoFolder = OperatingSystem.getHexoFolder();
    List<String> commandList = Arrays.asList(
    //  "cd " + baseHexoFolder + " && hexo clean",
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  //

  @Test
  void syncFilesToQuark() {
    String year = "2017";
    DailyUtil.syncFilesToQuark(year);
  }

  @Test
  void syncFilesToQuarkBatch() {
    List<String> years = Arrays.asList("2017", "2025");
    for (String year : years) {
      DailyUtil.syncFilesToQuark(year);
    }
  }


}
