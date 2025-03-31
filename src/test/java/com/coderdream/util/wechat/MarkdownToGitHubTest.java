package com.coderdream.util.wechat;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class MarkdownToGitHubTest {

  @Test
  void genGitHubArticle() {
    String category = "0008_DailyNews";
    String dateString = "20250321";
    MarkdownToGitHub.genGitHubArticle(category, dateString);
  }
}
