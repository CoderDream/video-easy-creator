package com.coderdream.util.video;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.CdConstants;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author CoderDream
 * @version 1.0
 * @since 2024/12/21 15:41
 */
@Slf4j
class BatchCreateVideoCommonUtilTest {

  @Test
  void getImageFiles() {
    String fileName = "CampingInvitation_cht";
    List<File> imageFiles = BatchCreateVideoCommonUtil.getImageFiles(fileName);
    for (File imageFile : imageFiles) {
      log.info(imageFile.getAbsolutePath());
      assertTrue(imageFile.exists());
    }
  }

  @Test
  void getAudioFiles_01() {
    String fileName = "CampingInvitation_cht";
    List<File> imageFiles = BatchCreateVideoCommonUtil.getImageFiles(fileName);
    String audioType = CdConstants.AUDIO_TYPE_WAV;
    String lang = CdConstants.LANG_CN;

    List<File> audioFiles = BatchCreateVideoCommonUtil.getAudioFiles(fileName,
      audioType, lang);

    for (File audioFile : audioFiles) {
      log.info(audioFile.getAbsolutePath());
      assertTrue(audioFile.exists());
    }
  }

  @Test
  void getAudioFiles_02() {
    String fileName = "CampingInvitation_cht";
    List<File> imageFiles = BatchCreateVideoCommonUtil.getImageFiles(fileName);
    String audioType = CdConstants.AUDIO_TYPE_WAV;
    String lang = CdConstants.LANG_CN;

    List<File> audioFiles = BatchCreateVideoCommonUtil.getAudioFiles(fileName,
      audioType, lang);

    for (File audioFile : audioFiles) {
      log.info(audioFile.getAbsolutePath());
      assertTrue(audioFile.exists());
    }
  }

  @Test
  void getVideoFiles_01() {
    String fileName = "CampingInvitation_02";
    String lang = CdConstants.LANG_CN;

    List<File> videoFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
      lang);

    for (File videoFile : videoFiles) {
      log.info(videoFile.getAbsolutePath());
      assertTrue(videoFile.exists());
    }
  }

  @Test
  void getVideoFiles_02() {
    String fileName = "CampingInvitation_02";
    String lang = CdConstants.LANG_EN;

    List<File> videoFiles = BatchCreateVideoCommonUtil.getVideoFiles(fileName,
      CdConstants.LANG_EN);

    for (File videoFile : videoFiles) {
      log.info(videoFile.getAbsolutePath());
      assertTrue(videoFile.exists());
    }
  }
}
