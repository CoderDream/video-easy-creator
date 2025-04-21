package com.coderdream.util.process;

import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cmd.CommandUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.GenSubtitleUtil;
import com.coderdream.util.subtitle.SubtitleUtil;
import com.coderdream.util.wechat.MarkdownToGitHub;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HalfHourEnglishTest {


  @Test
  void processSrtAndGenDescription_0003() {
//    String bookFolderName = "0003_PressBriefings";
//    String folderName = "20250416";
//    GenSubtitleUtil.processSrtAndGenDescription(bookFolderName, folderName);


    List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
    for (YoutubeInfoEntity youtubeInfoEntity : youtubeVideoInfoEntityList) {
      String category = youtubeInfoEntity.getCategory();
      String dateString = youtubeInfoEntity.getDateString();
//      PrepareForMakeVideoUtil.processYoutube(category, dateString);


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



}
