package com.coderdream.util.daily;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.GenSubtitleUtil;
import com.coderdream.util.wechat.MarkdownToGitHub;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
class DailyUtilDemo {


  void processYoutube() {
    DailyUtil.processYoutube();
  }


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



  void downloadVideoAndThumbnail() {
// https://www.youtube.com/watch?v=B2t0cB87aDc 老梁四大名著情商课 第一课 完整未删减版
    String category = "0010_Temp";
    String dateString = "20250323";
    String videoId = "B2t0cB87aDc";
    DailyUtil.downloadVideoAndThumbnail(category, dateString, videoId);
  }


  void process_250103() {
    String folderName = "181213";
    TranslationUtil.genDescription(folderName);
  }


  void process_250123() {
    String folderName = "170105";
    TranslationUtil.genDescription(folderName);
  }


  void processBatch02() {
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      TranslationUtil.genDescription(folderName);
    }
  }


  void process_180927() {
    String folderName = "170330";
//    String folderName = "250213";
    String title = "【BBC六分钟英语】" + CdFileUtil.getArticleTitle(
      folderName);// "【BBC六分钟英语】泰国50年老汤真的能吃吗？";
    DailyUtil.process(folderName, title);

    String baseHexoFolder = OperatingSystem.getGitHubCoderDreamHexoFolder();

    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }


  void process_deploy() {
    String baseHexoFolder = OperatingSystem.getGitHubCoderDreamHexoFolder();
    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo clean",
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  void process_todo() {
    String folderName = "170330";
//    String folderName = "250213";
    String title = "【BBC六分钟英语】" + CdFileUtil.getArticleTitle(
      folderName);// "【BBC六分钟英语】泰国50年老汤真的能吃吗？";
    DailyUtil.process(folderName, title);

    String baseHexoFolder = OperatingSystem.getGitHubCoderDreamHexoFolder();

    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  private static List<String> NUMBER_LIST;

  static  {
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "bbc"
        + File.separatorChar;

    NUMBER_LIST = FileUtil.readLines(folderPath + File.separator + "todo.txt",
      "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));
  }

  public static void main(String[] args) {
    DailyUtilDemo   dailyUtilDemo =new DailyUtilDemo();
    dailyUtilDemo.processBatch();
  }


  void processBatch() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      String title = "【BBC六分钟英语】" + CdFileUtil.getArticleTitle(
        folderName);
      DailyUtil.process(folderName, title);
//      TranslationUtil.genDescription(folderName);
    }

    String baseHexoFolder = OperatingSystem.getGitHubCoderDreamHexoFolder();
    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo clean",
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }


  void processBatch_HalfHourEnglish() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      String title = "【BBC六分钟英语】" + CdFileUtil.getArticleTitle(
        folderName);
      DailyUtil.processHalfHourEnglish(folderName, title);
//      TranslationUtil.genDescription(folderName);
    }
  }


  void processPostHalfHourEnglish() {
    List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
    for (YoutubeInfoEntity youtubeInfoEntity : youtubeVideoInfoEntityList) {
      String category = youtubeInfoEntity.getCategory();
      String dateString = youtubeInfoEntity.getDateString();
      GenSubtitleUtil.processSrtAndGenDescription(category, dateString);
      MarkdownToGitHub.genGitHubArticle(category, dateString);
    }

    String baseHexoFolder = OperatingSystem.getHalfHourEnglishHexoFolder();
    List<String> commandList = Arrays.asList(
      "cd " + baseHexoFolder + " && hexo clean",
      "cd " + baseHexoFolder + " && hexo g",
      "cd " + baseHexoFolder + " && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }


  void syncFilesToQuark() {
    String year = "2025"; //    String year = "2017";
    DailyUtil.syncFilesToQuark(year);
  }


  void syncFilesToQuark2016() {
    String year = "2016"; //    String year = "2017";
    DailyUtil.syncFilesToQuark(year);
  }


  void syncFilesToQuarkBatch() {
//    List<String> years = Arrays.asList("2017", "2018", "2019", "2020", "2021",
//      "2022", "2023", "2024", "2025");
    List<String> years = Arrays.asList("2016", "2017", "2018", "2019", "2020", "2021",
      "2022", "2023", "2024", "2025");
    for (String year : years) {
      DailyUtil.syncFilesToQuark(year);
    }
  }


  void syncHistoryVideoToQuark_001() {
//    String year = "2017";
//    List<String> years = Arrays.asList("2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025");
    List<String> years = Arrays.asList("2018", "2019", "2020", "2021", "2022",
      "2023", "2024", "2025");
    for (String year : years) {
      DailyUtil.syncHistoryVideoToQuark(year);
    }
  }

  //

  void genTitleFile() {
//    String year = "2017";
    List<String> years = Arrays.asList("2018", "2019", "2020", "2021",
      "2022", "2023", "2024");
//    List<String> years = Arrays.asList("2017", "2018", "2019", "2024", "2025");
    for (String year : years) {
      DailyUtil.genTitleFile(year);
    }
  }


  void moveHistoryVideoToQuark_2017() {
    String year = "2017";
    DailyUtil.moveHistoryVideoToQuark(year);
  }



  void moveHistoryVideoToQuark_001() {
//    String year = "2017";
    List<String> years = Arrays.asList("2024");
//    List<String> years = Arrays.asList("2018", "2019", "2020", "2021", "2022", "2023", "2024");
    for (String year : years) {
      DailyUtil.moveHistoryVideoToQuark(year);
    }
  }


  void moveHistoryVideoToQuark_002() {
//    String year = "2017";
//    List<String> years = Arrays.asList("2025");
    List<String> years = Arrays.asList("2018", "2019", "2020", "2021", "2022",
      "2023", "2024", "2025");
    for (String year : years) {
      DailyUtil.moveHistoryVideoToQuarkV2(year);
    }
  }

}
