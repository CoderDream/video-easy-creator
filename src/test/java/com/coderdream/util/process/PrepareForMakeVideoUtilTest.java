package com.coderdream.util.process;

import static org.junit.jupiter.api.Assertions.*;

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
}
