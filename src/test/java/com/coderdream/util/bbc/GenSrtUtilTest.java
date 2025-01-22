package com.coderdream.util.bbc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GenSrtUtilTest {

  @Test
  void genScriptDialogNewV2() {
    String folderName = "D:\\0000\\【中英雙語】2025川普就職演講\\";
    String newFileName = folderName + "【中英雙語】2025川普就職演講_new.txt";
    String fileName = folderName + "【中英雙語】2025川普就職演講.txt";
    GenSrtUtil.genScriptDialogNewV2(newFileName, fileName);
  }
}
