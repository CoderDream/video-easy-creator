package com.coderdream.util.sentence;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.process.GenVideoUtil;
import org.junit.jupiter.api.Test;

class DialogSingleEntityUtilTest {

  @Test
  void genPart1AndPart2File() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch004";
    DialogSingleEntityUtil.genPart1AndPart2File(folderPath, subFolder);
    // assert that the process completes without any error
    assertTrue(true);
  }
}
