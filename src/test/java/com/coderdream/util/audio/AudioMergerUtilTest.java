package com.coderdream.util.audio;


import com.coderdream.util.cd.CdStringUtil;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class AudioMergerUtilTest {

  @Test
  void mergeWavFiles() {

    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\ch01_new.wav"; // 替换为你的输出文件路径
    try {
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath);
    } catch (IOException e) {
      log.error("合并文件失败：{}", e.getMessage());
    }
  }

  @Test
  void mergeWavFiles_0201() {

    int size = 1;
    try {
      String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输入目录
      String outputFilePath =
        "D:\\0000\\EnBook001\\900\\ch01\\ch01_" + size + ".wav"; // 替换为你的输出文件路径
      // 合并前1个且不加表头
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, size);
    } catch (IOException e) {
      log.error("合并1{}文件失败：{}", size, e.getMessage());
    }
  }

  @Test
  void mergeWavFiles_0202() {
    int size = 2;
    try {
      String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输入目录
      String outputFilePath =
        "D:\\0000\\EnBook001\\900\\ch01\\ch01_" + CdStringUtil.int2N(size, 3)
          + ".wav"; // 替换为你的输出文件路径
      // 合并前2个且不加表头
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, size);
    } catch (IOException e) {
      log.error("合并2{}文件失败：{}", size, e.getMessage());
    }
  }


  @Test
  void mergeWavFiles_0203() {

    int size = 3;
    try {
      String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输入目录
      String outputFilePath =
        "D:\\0000\\EnBook001\\900\\ch01\\ch01_" + size + ".wav"; // 替换为你的输出文件路径
      // 合并前3个且不加表头
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, size);
    } catch (IOException e) {
      log.error("合并{}文件失败：{}", size, e.getMessage());
    }
  }


  @Test
  void mergeWavFiles_020N() {
    int size = 76;
    try {
      String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\audio\\"; // 替换为你的输入目录
      for (int i = 1; i <= size; i++) {
        String outputFilePath =
          "D:\\0000\\EnBook001\\900\\ch01\\merge_audio\\ch01_" + CdStringUtil.int2N(i, 3)
            + ".wav"; // 替换为你的输出文件路径
        // 合并前2个且不加表头
        AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, i);
      }
    } catch (IOException e) {
      log.error("合并76{}文件失败：{}", size, e.getMessage());
    }
  }


  @Test
  void testMergeWavFiles_0401() {

    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\mix"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.wav"; // 替换为你的输出文件路径
    try {
      // 全部并加开头音乐，每个子文件前加翻页
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, true, true, 0);
    } catch (IOException e) {
      log.error("合并全部并加开头音乐文件失败：{}", e.getMessage());
    }
  }

  @Test
  void testMergeWavFiles_0402() {

    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\mix"; // 替换为你的输入目录
    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\ch01_new.wav"; // 替换为你的输出文件路径
    try {
      // 不并加开头音乐，每个子文件前不加翻页
      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath, false, false, 0);
    } catch (IOException e) {
      log.error("合并全部并加开头音乐文件失败：{}", e.getMessage());
    }
  }
}
