package com.coderdream.util.video;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdConstants;
import org.junit.jupiter.api.Test;

class BatchCreateVideoUtilTest {

  @Test
  void backCreateVideo() {
    String fileName = "CampingInvitation_cht";
    String audioType = CdConstants.AUDIO_TYPE_WAV;
    String lang = CdConstants.LANG_CN;
    BatchCreateVideoUtil.batchCreateVideo(fileName, audioType, lang)
      .forEach(file -> {
        assertNotNull(file);
        assertTrue(file.exists());
      });
  }
}
