package com.coderdream.util.process;

import com.coderdream.util.audio.AudioMergerSingleBatch;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.mstts.SpeechUtil;
import com.coderdream.util.pic.HighResImageVideoUtil;
import com.coderdream.util.video.SingleCreateVideoUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenVideoUtil {

  public static void process(String folderPath, String subFolder) {
    // 1. 生成SentenceVOList对象的文本
//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch002";
//    File file = new File(folderPath + subFolder);
//    if (!file.exists()) {
//      boolean mkdir = file.mkdirs();
//      log.info("文件夹创建成功：{}", mkdir);
//    } else {
//      log.info("文件夹已存在");
//    }

    String subFolderPath = folderPath + subFolder;

    if (CdFileUtil.isFileEmpty(subFolderPath)) {
      File file = new File(folderPath + subFolder);
      boolean mkdir = file.mkdirs();
      log.info("文件不存在或为空， {}，已生成新文件: {}", mkdir,
        file.getAbsolutePath());
    } else {
      log.info("文件夹已存在: {}", subFolderPath);
    }

//    String inputFolderName = "input";
//    String part1FileName = subFolder + "part01.txt";
//    String part2FileName = subFolder + "part02.txt";
//    DialogSingleEntityUtil.genPart1File(folderPath, subFolder);
//    DialogSingleEntityUtil.genPart2File(folderPath, subFolder);

//    boolean b = DialogSingleEntityUtil.genPart1AndPart2File(folderPath,
//      subFolder);
//    if (!b) {
//      log.info("分割文本文件失败！");
//      return;
//    }

    String fileNameTotal =
      subFolderPath + File.separator + subFolder + "_total.txt";
//    String fileName = "D:\\0000\\EnBook001\\900\\ch01\\900V1_ch0101_total.txt";
    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_total_phonetics.txt";
    if (CdFileUtil.isFileEmpty(phoneticsFileName)) {
      File file = TranslationUtil.genPhonetics(fileNameTotal);
      log.info("带音标文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", phoneticsFileName);
    }

    // 2. 生成音频文件
    long startTime = System.currentTimeMillis(); // 开始时间
    // "D:\\0000\\EnBook001\\900\\ch002\\";
    String fileName = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      phoneticsFileName); //"ch002_total_phonetics";
    String audioType = "wav";
    SpeechUtil.genDialog2Audio900(folderPath, subFolder, fileName, audioType);

    // 确保所有任务执行完毕后关闭线程池
    SpeechUtil.shutdownExecutor();
    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));

    String inputDirCn =
      folderPath + subFolder + File.separator + "audio\\cn\\"; // 替换为你的输入目录
    String inputDirEn =
      folderPath + subFolder + File.separator + "audio\\en\\";  // 替换为你的输入目录
    String outputFilePath =
      folderPath + subFolder + File.separator + "audio_mix\\"; // 替换为你的输出文件路径

    if (CdFileUtil.isFileEmpty(outputFilePath)) {
      File file = AudioMergerSingleBatch.mergeWavFiles(inputDirCn, inputDirEn,
        outputFilePath);
      assert file != null;
      log.info("合并文件生成成功！文件名为：{}", file.getAbsolutePath());
    } else {
      log.info("合并文件已存在: {}", outputFilePath);
    }

    String backgroundImageName = "D:\\0000\\商务英语(5)\\bgv4.png";// "D:\\0000\\bgmusic\\background.png";
    String filePath = folderPath + subFolder
      + File.separator;// "D:\\0000\\EnBook001\\900\\ch002\\";
//    String chapter = subFolder;// "ch002";
    String language = "cht";
//    String contentFileName = chapter + "_" + language;// "ch01_cht"; // 生成图片
    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      language);

    // 3. 生成视频文件

//        String backgroundImageName = "D:\\0000\\商务英语(5)\\bgv4.png";// "D:\\0000\\bgmusic\\background.png";
//    String filePath = "D:\\0000\\EnBook001\\900\\ch002\\";
//    String chapter = "ch002";
//    String language = "cht";
//    String contentFileName = chapter + "_" + language;// "ch01_cht"; // 生成图片
//    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
//      filePath, contentFileName, language);

    String imagePath = folderPath + subFolder + File.separator + "pic_cht\\";
    String audioPath = folderPath + subFolder + File.separator + "audio_mix\\";
    String videoPath = folderPath + subFolder + File.separator + "video_cht\\";
    SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,
      videoPath);

    // 4. 生成字幕文件
  }
}
