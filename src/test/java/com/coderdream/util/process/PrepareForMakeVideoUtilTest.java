package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import org.junit.jupiter.api.Test;

class PrepareForMakeVideoUtilTest {

  @Test
  void processForSixMinutes() {
    String folderName = "170420";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processForSixMinutesFromTodo() {
    String folderName = "250313";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processYoutubeFromTodo_0008() {
    String typeName = "0008_DailyNews";
    String folderName = "20250314";
    PrepareForMakeVideoUtil.processYoutube(typeName, folderName);
  }
}
