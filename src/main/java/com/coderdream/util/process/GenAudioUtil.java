package com.coderdream.util.process;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.util.audio.AudioMergerDualEnBatch;
import com.coderdream.util.audio.AudioMergerMixBatch;
import com.coderdream.util.audio.AudioMergerMixBatchV20230317;
import com.coderdream.util.audio.AudioMergerSingleBatch;
import com.coderdream.util.audio.WavMerger;
import com.coderdream.util.cd.CdConstants;
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
    String subFolderPath = folderPath + File.separator + subFolder;
    if (CdFileUtil.isFileEmpty(subFolderPath)) {
      File file = new File(folderPath + File.separator + subFolder);
      boolean mkdir = file.mkdirs();
      log.info("文件不存在或为空， {}，已生成新文件: {}", mkdir,
        file.getAbsolutePath());
    } else {
      log.info("文件夹已存在: {}", subFolderPath);
    }

    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + "_phonetics.txt";
    List<String> contentList = CdFileUtil.readFileContent(phoneticsFileName);
    assert contentList != null;
    int size = 0;
    if (CollectionUtil.isEmpty(contentList) || contentList.size() % 3 != 0) {
      log.warn("文件行数不是3的倍数，大小: {}", contentList.size());
    } else {
      size = contentList.size() / 3;
    }

    // 2. 生成音频文件
    long startTime = System.currentTimeMillis(); // 开始时间
    String pureFileNamePhonetics = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      phoneticsFileName);
    String audioType = "wav";
    GenDualAudioUtil.genDialog2Audio900(folderPath, subFolder,
      pureFileNamePhonetics,
      audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
//    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("音频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));

    String inputDirCn =
      folderPath + File.separator + subFolder + File.separator + "audio"
        + File.separator + "cn"
        + File.separator; // 替换为你的输入目录
    String inputDirEn =
      folderPath + File.separator + subFolder + File.separator + "audio"
        + File.separator + "en"
        + File.separator;  // 替换为你的输入目录
    String outputFilePath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
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
      } else {
        break;
      }
    }

    // 合成音频文件
    File file = AudioMergerSingleBatch.mergeWavFiles(inputDirCn, inputDirEn,
      outputFilePath);
    assert file != null;
    log.info("合并文件生成成功！文件名为：{}", file.getAbsolutePath());

    String backgroundImageName =
      OperatingSystem.getBaseFolder() + File.separator + "bgmusic"
        + File.separator
        + "content_bg.png";// "D:\\0000\\bgmusic\\background.png";

    String filePath = folderPath + File.separator + subFolder
      + File.separator;
    String language = "cht";
    List<File> files = HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      language);
    return true;
  }

  /**
   * <pre>
   *   1. 文件包含7行一组的对话，名称为ChapterXXX.txt；
   *   2. 生成音频文件，英文包含常速和慢速；
   *   3. 视频为两遍遍英文无中文； 一遍有字幕英文慢速英文；一遍双语字幕中文，一遍双语字幕英文，翻页
   *
   *   4. 图片包含带中文和不带中文的两种图片；另外还会用到无字幕的图片
   * </pre>
   *
   * @param folderPath
   * @param subFolder
   * @return
   */
  public static boolean processV2(String folderPath, String subFolder) {
    String subFolderPath = folderPath + File.separator + subFolder;
    if (CdFileUtil.isFileEmpty(subFolderPath)) {
      File file = new File(subFolderPath);
      boolean mkdir = file.mkdirs();
      log.info("文件不存在或为空， {}，已生成新文件: {}", mkdir,
        file.getAbsolutePath());
    } else {
      log.info("文件夹已存在: {}", subFolderPath);
    }

    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".txt";
    List<String> contentList = CdFileUtil.readFileContent(phoneticsFileName);
    assert contentList != null;
    int size = 0;
    if (CollectionUtil.isEmpty(contentList) || contentList.size() % 3 != 0) {
      log.warn("文件行数不是3的倍数，大小: {}", contentList.size());
    } else {
      size = contentList.size() / 3;
    }

    // 2. 生成音频文件
    long startTime = System.currentTimeMillis(); // 开始时间
    String pureFileNamePhonetics = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      phoneticsFileName);
    String audioType = "wav";
    GenDualAudioUtil.genDialogToAudioThreeTypes(folderPath, subFolder,
      pureFileNamePhonetics,
      audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
//    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("音频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));

    String inputDirCn =
      folderPath + File.separator + subFolder + File.separator + "audio"
        + File.separator + CdConstants.LANG_CN
        + File.separator; // 替换为你的输入目录
    String inputDirEn =
      folderPath + File.separator + subFolder + File.separator + "audio"
        + File.separator + CdConstants.LANG_EN
        + File.separator;  // 替换为你的输入目录

    String outputFilePathDualEn =
      folderPath + File.separator + subFolder + File.separator + "audio_dual_en"
        + File.separator; // 替换为你的输出文件路径

    String outputFilePathMix =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
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
      } else {
        break;
      }
    }

    // 合成音频文件(双英语）
    File fileDualEn = AudioMergerDualEnBatch.mergeWavFiles(inputDirEn,
      outputFilePathDualEn);
    assert fileDualEn != null;
    log.info("合并英文文件生成成功！文件名为：{}", fileDualEn.getAbsolutePath());

    // 合成音频文件
    File fileMix = AudioMergerMixBatch.mergeWavFiles(inputDirCn, inputDirEn,
      outputFilePathMix);
    assert fileMix != null;
    log.info("合并混合文件生成成功！文件名为：{}", fileMix.getAbsolutePath());

    // 3. 图片包含带中文和不带中文的两种图片；另外还会用到无字幕的图片
    String backgroundImageName =
      OperatingSystem.getBaseFolder() + File.separator + "bgmusic"
        + File.separator
        + "content_bg.png";
// "D:\\0000\\bgmusic\\background.png";

    String filePath = folderPath + File.separator + subFolder
      + File.separator;
//    String language = "dual";
//    String picType = "dual";
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_NO_SUBTITLE);

    // 生成只带英文的图片
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_EN);

    // 生成带英文和中文的图片
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_MIX);

//    language = "no_subtitle";
//    picType = "no_subtitle";

    return true;
  }

  /**
   * <pre>
   *   1. 文件包含7行一组的对话，名称为ChapterXXX.txt；
   *   2. 生成音频文件，英文包含常速和慢速；
   *   3. 视频为两遍遍英文无中文； 一遍有字幕英文慢速英文；一遍双语字幕中文，一遍双语字幕英文，翻页
   *
   *   4. 图片包含带中文和不带中文的两种图片；另外还会用到无字幕的图片
   * </pre>
   *
   * @param folderPath
   * @param subFolder
   * @return
   */
  public static boolean processV20250317(String folderPath, String subFolder) {
    String subFolderPath = folderPath + File.separator + subFolder;
    if (CdFileUtil.isFileEmpty(subFolderPath)) {
      File file = new File(subFolderPath);
      boolean mkdir = file.mkdirs();
      log.info("文件不存在或为空， {}，已生成新文件: {}", mkdir,
        file.getAbsolutePath());
    } else {
      log.info("文件夹已存在: {}", subFolderPath);
    }

    String phoneticsFileName =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".txt";
    List<String> contentList = CdFileUtil.readFileContent(phoneticsFileName);
    assert contentList != null;
    int size = 0;
    if (CollectionUtil.isEmpty(contentList) || contentList.size() % 3 != 0) {
      log.warn("文件行数不是3的倍数，大小: {}", contentList.size());
    } else {
      size = contentList.size() / 3;
    }

    // 2. 生成音频文件
    long startTime = System.currentTimeMillis(); // 开始时间
    String pureFileNamePhonetics = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      phoneticsFileName);
    String audioType = "wav";
    GenDualAudioUtil.genDialogToAudioThreeTypes(folderPath, subFolder,
      pureFileNamePhonetics,
      audioType);

    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();
//    System.out.println("所有任务执行完毕，线程池已关闭！");
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("音频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));

    String inputDirCn =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER
        + File.separator + CdConstants.LANG_CN
        + File.separator; // 替换为你的输入目录
    String inputDirEn =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER
        + File.separator + CdConstants.LANG_EN
        + File.separator;  // 替换为你的输入目录
    String inputDirEnSlow =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER
        + File.separator + CdConstants.LANG_EN_SLOW
        + File.separator;  // 替换为你的输入目录

//    String outputFilePathDualEn =
//      folderPath + File.separator + subFolder + File.separator + "audio_dual_en"
//        + File.separator; // 替换为你的输出文件路径

    String outputFilePathMix =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_MIX_FOLDER
        + File.separator; // 替换为你的输出文件路径

    for (int i = 0; i < 10; i++) {
      List<String> wavFilesCn = AudioMergerSingleBatch.listWavFiles(
        inputDirCn); // 获取中文音频文件列表
      if (wavFilesCn.isEmpty()) {
        log.warn("中文目录{}下没有找到wav文件", inputDirCn);
        SpeechUtil.genDialog2AudioThreeTypes(folderPath, subFolder,
          pureFileNamePhonetics,
          audioType);
      }

      List<String> wavFilesEn = AudioMergerSingleBatch.listWavFiles(
        inputDirEn); // 获取英文音频文件列表
      if (wavFilesEn.isEmpty()) {
        log.warn("英文目录{}下没有找到wav文件", inputDirEn);
        SpeechUtil.genDialog2AudioThreeTypes(folderPath, subFolder,
          pureFileNamePhonetics,
          audioType);
      }

      List<String> wavFilesEnSlow = AudioMergerSingleBatch.listWavFiles(
        inputDirEnSlow); // 获取英文音频文件列表
      if (wavFilesEnSlow.isEmpty()) {
        log.warn("英文目录{}下没有找到wav文件", inputDirEnSlow);
        SpeechUtil.genDialog2AudioThreeTypes(folderPath, subFolder,
          pureFileNamePhonetics,
          audioType);
      }

      // 如果两个列表大小不一致则立即退出
      if (wavFilesCn.size() != wavFilesEn.size() || size != wavFilesCn.size()
        || size != wavFilesEnSlow.size()) {
        log.error(
          "### 两个列表大小不一致，无法合并，重试第 {} 次, size {}, 中文音频数量：{}， 英文音频数量：{} ",
          i + 1, size, wavFilesCn.size(), wavFilesEn.size());
        SpeechUtil.genDialog2AudioThreeTypes(folderPath, subFolder,
          pureFileNamePhonetics,
          audioType);
      } else {
        break;
      }
    }

    // 合成音频文件(双英语）
//    File fileDualEn = AudioMergerDualEnBatch.mergeWavFiles(inputDirEn,
//      outputFilePathDualEn);
//    assert fileDualEn != null;
//    log.info("合并英文文件生成成功！文件名为：{}", fileDualEn.getAbsolutePath());

    // 合成音频文件 中文-》英文正常語速-》慢速正常-》語速
    File fileMix = AudioMergerMixBatchV20230317.mergeWavFiles(inputDirCn,
      inputDirEn, inputDirEnSlow,
      outputFilePathMix);
    assert fileMix != null;
    log.info("合并混合文件生成成功！文件名为：{}", fileMix.getAbsolutePath());

    // 3. 图片包含带中文和不带中文的两种图片；另外还会用到无字幕的图片
    String backgroundImageName =
      OperatingSystem.getBaseFolder() + File.separator + "bgmusic"
        + File.separator
        + "content_bg.png";
// "D:\\0000\\bgmusic\\background.png";

    String filePath = folderPath + File.separator + subFolder
      + File.separator;
//    String language = "dual";
//    String picType = "dual";
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_NO_SUBTITLE);

    // 生成只带英文的图片
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_EN);

    // 生成带英文和中文的图片
    HighResImageVideoUtil.generateImages(backgroundImageName,
      filePath,
      CdFileUtil.getPureFileNameWithoutExtensionWithPath(phoneticsFileName),
      CdConstants.PIC_TYPE_MIX);

//    language = "no_subtitle";
//    picType = "no_subtitle";

    return true;
  }

  public static void genHeadAudio() {
    GenDualAudioUtil.genHeadAudio();
    // 示例用法
    List<String> bookNameList = List.of(
//      "EnBook001",
//      "EnBook002",
//      "EnBook003",
//      "EnBook004",
//      "EnBook005",
      "EnBook008"
    );
    String baseFolder = OperatingSystem.getBaseFolder() + File.separator;
    for (String bookName : bookNameList) {
      // 示例用法
      List<String> wavFilePaths = List.of(
        baseFolder + File.separator + bookName + File.separator + "head"
          + File.separator + bookName + "_en.wav",
        baseFolder + File.separator + bookName + File.separator + "head"
          + File.separator + bookName + "_cn.wav"
      );
      String outputFilePath =
        baseFolder + File.separator + bookName + File.separator + "head"
          + File.separator + bookName + "_head.wav";

      if (CdFileUtil.isFileEmpty(outputFilePath)) {
        String duration = WavMerger.mergeWavFiles(wavFilePaths, outputFilePath);
        System.out.println("合并完成，耗时: " + duration);
      }
    }


  }

  public static void genHeadAudio(String bookName) {
    GenDualAudioUtil.genHeadAudio();

    String baseFolder = OperatingSystem.getBaseFolder() + File.separator;
    // 示例用法
    List<String> wavFilePaths = List.of(
      baseFolder + File.separator + bookName + File.separator + "head"
        + File.separator + bookName + "_en.wav",
      baseFolder + File.separator + bookName + File.separator + "head"
        + File.separator + bookName + "_cn.wav"
    );
    String outputFilePath =
      baseFolder + File.separator + bookName + File.separator + "head"
        + File.separator + bookName + "_head.wav";

    if (CdFileUtil.isFileEmpty(outputFilePath)) {
      String duration = WavMerger.mergeWavFiles(wavFilePaths, outputFilePath);
      System.out.println("合并完成，耗时: " + duration);
    }


  }
}
