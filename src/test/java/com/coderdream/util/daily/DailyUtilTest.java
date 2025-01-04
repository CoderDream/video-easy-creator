package com.coderdream.util.daily;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DailyUtilTest {

  @Test
  void process_250102() {

    String folderName = "250102";
    String title = "【BBC六分钟英语】你喝了足够的水吗？";
    DailyUtil.process(folderName, title);


  }
}
