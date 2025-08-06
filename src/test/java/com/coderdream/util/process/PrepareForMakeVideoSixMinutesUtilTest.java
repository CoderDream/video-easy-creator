package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrepareForMakeVideoSixMinutesUtilTest {

  private List<String> NUMBER_LIST;

  @BeforeEach
  void init() {
    String folderPathForSixMinutes =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
        + File.separatorChar;

    NUMBER_LIST = FileUtil.readLines(folderPathForSixMinutes + File.separator + "todo.txt", "UTF-8");
  }

  @Test
  void processTodoForSixMinutes() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      PrepareForMakeVideoUtil.processForSixMinutes(folderName);
    }
  }

  public static void main(String[] args) {


    String folderPathForSixMinutes =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
        + File.separatorChar;

    List<String> NUMBER_LIST = FileUtil.readLines(folderPathForSixMinutes + File.separator + "todo.txt", "UTF-8");
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      PrepareForMakeVideoUtil.processForSixMinutes(folderName);
    }
  }

}
