package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo04.Mp4MergeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class GenAudioAndVideoUtilTest {

  @Test
  void processBatchAndMergeEnBook008() {
    String bookName = "EnBook008";
    String folderPath = OperatingSystem.getFolderPath(bookName);

    List<String> subFolders = new ArrayList<>();
    int end = 15; // 假定总共100章 101
    for (int i = 2; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      GenAudioUtil.processV20250317(folderPath, subFolder);
      GenVideoUtil.processV20250317(bookName, folderPath, subFolder);
      Mp4MergeUtil.processMerge(folderPath, subFolder);
    }
  }
}
