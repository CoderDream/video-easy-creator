package com.coderdream.util.audio;

import org.junit.jupiter.api.Test;

class AudioFolderDurationUtilTest {

  @Test
  void createAudioDurationFileList_01() {
    // 创建临时文件夹
    String folderName = "D:\\0000\\EnBook001\\900\\ch01\\audio\\";
    String durationFileName = "D:\\0000\\EnBook001\\900\\ch01\\audio_duration.txt";
    AudioFolderDurationUtil.createAudioDurationFileList(folderName,
      durationFileName);
  }

  @Test
  void createAudioDurationFileList_02() {
    // 创建临时文件夹
    String folderName = "D:\\0000\\EnBook001\\900\\ch01\\merge_audio\\";
    String durationFileName = "D:\\0000\\EnBook001\\900\\ch01\\merge_audio_duration.txt";
    AudioFolderDurationUtil.createAudioDurationFileList(folderName,
      durationFileName);
  }
}
