package com.coderdream.util.process;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.util.audio.AudioMergerSingleBatch;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.mstts.GenDualAudioUtil;
import com.coderdream.util.mstts.SpeechUtil;
import com.coderdream.util.pic.HighResImageVideoUtil;

import java.io.File;
import java.util.List;

import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenAudioUtil {

  public static boolean process(String folderPath, String subFolder) {
    String subFolderPath = folderPath + subFolder;
    if (CdFileUtil.isFileEmpty(subFolderPath)) {
      File file = new File(folderPath + subFolder);
      boolean mkdir = file.mkdirs();
      log.info("文件不存在或为空， {}，已生成新文件: {}", mkdir,
        file.getAbsolutePath());
    } else {
      log.info("文件夹已存在: {}", subFolderPath);
    }

    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    List<String> contentList = CdFileUtil.readFileContent(phoneticsFileName);
    assert contentList != null;
    int size = 0;
    if (CollectionUtil.isEmpty(contentList) || contentList.size() % 3 != 0) {
      log.warn("文件行数不是3的倍数，大小: {}", contentList.size());
//            return ;
    } else {
      size = contentList.size() / 3;
    }

    // 2. 生成音频文件
    long startTime = System.currentTimeMillis(); // 开始时间
    String pureFileNamePhonetics = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      phoneticsFileName);
    String audioType = "wav";
    GenDualAudioUtil.genDialog2Audio900(folderPath, subFolder, pureFileNamePhonetics,
      audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
//    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("音频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));

    String inputDirCn =
      folderPath + subFolder + File.separator + "audio" + File.separator + "cn"
        + File.separator; // 替换为你的输入目录
    String inputDirEn =
      folderPath + subFolder + File.separator + "audio" + File.separator + "en"
        + File.separator;  // 替换为你的输入目录
    String outputFilePath =
      folderPath + subFolder + File.separator + "audio_mix"
        + File.separator; // 替换为你的输出文件路径

    for (int i = 0; i < 10; i++) {
      List<String> wavFilesCn = AudioMergerSingleBatch.listWavFiles(
        inputDirCn); // 获取中文音频文件列表
      if (wavFilesCn.isEmpty()) {
        log.warn("中文目录{}下没有找到wav文件", inputDirCn);
        SpeechUtil.genDialog2Audio900(folderPath, subFolder,
                pureFileNamePhonetics,
                audioType);
      }

      List<String> wavFilesEn = AudioMergerSingleBatch.listWavFiles(
        inputDirEn); // 获取英文音频文件列表
      if (wavFilesEn.isEmpty()) {
        log.warn("英文目录{}下没有找到wav文件", inputDirEn);
        SpeechUtil.genDialog2Audio900(folderPath, subFolder,
                pureFileNamePhonetics,
                audioType);
      }

      // 如果两个列表大小不一致则立即退出
      if (wavFilesCn.size() != wavFilesEn.size() || size != wavFilesCn.size()) {
        log.error(
          "### 两个列表大小不一致，无法合并，重试第 {} 次, size {}, 中文音频数量：{}， 英文音频数量：{} ",
          i + 1, size, wavFilesCn.size(), wavFilesEn.size());
        SpeechUtil.genDialog2Audio900(folderPath, subFolder,
          pureFileNamePhonetics,
          audioType);
      }
      else {
        break;
      }
    }

    // 合成音频文件
    File file = AudioMergerSingleBatch.mergeWavFiles(inputDirCn, inputDirEn,
      outputFilePath);
    assert file != null;
    log.info("合并文件生成成功！文件名为：{}", file.getAbsolutePath());

    String backgroundImageName =
      OperatingSystem.getBaseFolder() + "bgmusic" + File.separator
        + "content_bg.png";// "D:\\0000\\bgmusic\\background.png";

    String filePath = folderPath + subFolder
      + File.separator;
    String language = "cht";
    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      language);
    return true;
  }
}
