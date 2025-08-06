package com.coderdream.util.wechat;

import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.process.PrepareForMakeVideoUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class MarkdownToGitHubTest {

  @Test
  void genGitHubArticle_0003() {
    String category = "0003_PressBriefings"; //D:\0000\0003_PressBriefings\20250326
    String dateString = "20250416";
    MarkdownToGitHub.genGitHubArticle(category, dateString);
  }

  @Test
  void genGitHubArticle_Batch() {
//    String category = "0003_PressBriefings"; //D:\0000\0003_PressBriefings\20250326
//    String dateString = "20250402";
//    MarkdownToGitHub.genGitHubArticle(category, dateString);

    List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
    for (YoutubeInfoEntity youtubeInfoEntity : youtubeVideoInfoEntityList) {
      String category = youtubeInfoEntity.getCategory();
      String dateString = youtubeInfoEntity.getDateString();
      MarkdownToGitHub.genGitHubArticle(category, dateString);
    }
  }

  @Test
  void genGitHubArticle_0008() {
    String category = "0008_DailyNews";
    String dateString = "20250321";
    MarkdownToGitHub.genGitHubArticle(category, dateString);
  }

  @Test
  void genGitHubArticle_0009() {
    String category = "0009_TechNews";
    String dateString = "20250319";
    MarkdownToGitHub.genGitHubArticle(category, dateString);
  }
}
