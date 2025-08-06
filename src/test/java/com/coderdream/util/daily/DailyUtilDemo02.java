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
class DailyUtilDemo02 {




  public static void main(String[] args) {


      String folderPath =
        CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
          + File.separatorChar + "bbc"
          + File.separatorChar;

    List<String> NUMBER_LIST = FileUtil.readLines(folderPath + File.separator + "todo.txt",
        "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));

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


}
