package com.coderdream.util.process;

import org.junit.jupiter.api.Test;

class PrepareForMakeVideoUtilTest {

  @Test
  void processForSixMinutes() {
    String folderName = "170706";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processForSixMinutesFromTodo() {
    String folderName = "250403";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processYoutubeFromTodo_0003() {
    String categoryName = "0003_PressBriefings";
    String folderName = "20250402";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0007() {
    String categoryName = "0007_Trump";
    String folderName = "20250331";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0008() {
    String categoryName = "0008_DailyNews";
    String folderName = "20250321";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0009() {
    String categoryName = "0009_TechNews";
    String folderName = "20250319"; // D:\0000\0007_Trump\20250227

    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

}
