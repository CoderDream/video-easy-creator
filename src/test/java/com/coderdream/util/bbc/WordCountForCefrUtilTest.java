package com.coderdream.util.bbc;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import org.junit.jupiter.api.Test;

class WordCountForCefrUtilTest {

  @Test
  void genVocTable_0003() {
    String folderName = "20250402"; // DD:\0000\0003_PressBriefings\20250326
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0003_PressBriefings"
        + File.separator
        + folderName;
    WordCountForCefrUtil.genVocTable(folderPath, folderName);
  }

  @Test
  void genVocTable_0006() {
    String folderName = "20250222"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0006_KarolineLeavitt"
        + File.separator
        + folderName;
    WordCountForCefrUtil.genVocTable(folderPath, folderName);
  }

  @Test
  void genVocTable_0008() {

    String folderName = "20250321"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "0008_DailyNews"
        + File.separator
        + folderName;
    WordCountForCefrUtil.genVocTable(folderPath, folderName);
  }//D:\0000\0006_KarolineLeavitt\20250222

  @Test
  void genVocTable_EnBook010() {
    String bookName = "EnBook010";
    String folderName = "Chapter001"; // D:\0000\0007_Trump\20250227
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookName
        + File.separator
        + folderName;
    WordCountForCefrUtil.genVocTable(folderPath, folderName);
  }
}
