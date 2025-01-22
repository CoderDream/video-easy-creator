package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeforeGenerateUtilTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void process() {
    String folderPath = "D:\\0000\\EnBook001\\900\\";
    String subFolder = "ch009";
    BeforeGenerateUtil.process(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }

  @Test
  void processBook02() {
    String folderPath = "D:\\0000\\EnBook002\\";
    String subFolder = "Chapter001";
    BeforeGenerateUtil.processBook02(folderPath, subFolder);
    assertTrue(true);
    System.out.println("done");
  }


}
