package com.coderdream.util.video;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SingleCreateVideoUtilTest {

  @Test
  void singleCreateVideo_01() {
    String imagePath = "D:\\0000\\bgmusic\\background.png";
    String audioFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.wav";
    String videoFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.mp4";
    // 计算AUDIO时长
    double duration = FfmpegUtil.getAudioDuration(new File(audioFileName));
    log.info("本音频时长：{}", duration);
    File file = SingleCreateVideoUtil.singleCreateVideo(imagePath,
      audioFileName, videoFileName, duration);
    assertNotNull(file);
  }

  @Test
  void singleCreateVideo_02() {
    String imagePath = "D:\\0000\\bgmusic\\background.png";
    String audioFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.wav";
    String videoFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.mp4";
    // 计算AUDIO时长
    double duration = FfmpegUtil.getAudioDuration(new File(audioFileName));
    log.info("本音频时长：{}", duration);
    File file = SingleCreateVideoUtil.singleCreateVideo(imagePath,
      audioFileName, videoFileName, duration);
    assertNotNull(file);
  }

  @Test
  void singleCreateVideo_03() {
    String imagePath = "D:\\0000\\bgmusic\\background.png";
    String audioFileName = "D:\\0000\\EnBook001\\900\\ch01\\ch01_076.wav";
    String videoFileName = CdFileUtil.changeExtension(audioFileName, "mp4");
    // 计算AUDIO时长
    double duration = FfmpegUtil.getAudioDuration(new File(audioFileName));
    log.info("本音频时长：{}", duration);
    File file = SingleCreateVideoUtil.singleCreateVideo(imagePath,
      audioFileName, videoFileName, duration);
    assertNotNull(file);
  }

  /**
   * 批量测试
   */
  @Test
  void singleCreateVideo_04() {
    String imagePath = "D:\\0000\\bgmusic\\background.png";
    int size = 76;
    for (int i = 1; i <= size; i++) {
      String audioFileName =
        "D:\\0000\\EnBook001\\900\\ch01\\merge_audio\\ch01_"
          + CdStringUtil.int2N(i, 3) + ".wav";
      // 计算AUDIO时长
      double duration = FfmpegUtil.getAudioDuration(new File(audioFileName));
      log.info("本音频时长：{}", duration);
      String videoFileName = CdFileUtil.changeExtension(audioFileName, "mp4");
      File file = SingleCreateVideoUtil.singleCreateVideo(imagePath,
        audioFileName, videoFileName, duration);
      assertNotNull(file);
      i += 4;
    }
  }


  @Test
  void batchCreateSingleVideo_01() throws InterruptedException {
    String imagePath = "D:\\0000\\EnBook001\\900\\ch01\\pic_cht\\";
    String audioPath = "D:\\0000\\EnBook001\\900\\ch01\\audio\\";
    String videoPath = "D:\\0000\\EnBook001\\900\\ch01\\video_cht\\";
    SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,
      videoPath);
  }


}
