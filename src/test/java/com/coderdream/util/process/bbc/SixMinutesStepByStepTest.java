package com.coderdream.util.process.bbc;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SixMinutesStepByStepTest {


  private List<String> NUMBER_LIST;

  @BeforeEach
  void init() {
    String folderPath =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
        + File.separatorChar;

    NUMBER_LIST = FileUtil.readLines(folderPath + "todo.txt", "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));
  }

  @Test
  void process() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      SixMinutesStepByStep.process(folderName);
    }
  }
}
