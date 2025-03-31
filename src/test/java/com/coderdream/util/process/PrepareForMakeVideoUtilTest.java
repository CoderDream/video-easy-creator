package com.coderdream.util.process;

import org.junit.jupiter.api.Test;

class PrepareForMakeVideoUtilTest {

  @Test
  void processForSixMinutes() {
    String folderName = "170622";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processForSixMinutesFromTodo() {
    String folderName = "250220";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processYoutubeFromTodo_0003() {
    String typeName = "0003_PressBriefings";
    String folderName = "20250326";
    PrepareForMakeVideoUtil.processYoutube(typeName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0008() {
    String typeName = "0008_DailyNews";
    String folderName = "20250321";
    PrepareForMakeVideoUtil.processYoutube(typeName, folderName);
  }

}
