package com.coderdream.util.whisper;


import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class WhisperUtilTest {

  @Test
  void transcribeToSrt() {

    String category = "0003_PressBriefings";
    String dateString = "20250416";
    String inputPathMp3 =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator
        + dateString + File.separator + dateString
        + ".mp3";
    WhisperUtil.transcribeToSrt(inputPathMp3);

  }
}
