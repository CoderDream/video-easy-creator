package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.GenHeadVideo;
import com.coderdream.util.video.SingleCreateVideoUtil;

import com.coderdream.util.video.demo02.FFmpegOneByMany;
import com.coderdream.util.video.demo02.FFmpegOneByOne;
import com.coderdream.util.video.demo02.FFmpegParallelOptimized;

import com.coderdream.util.video.demo04.Mp4MergeUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.coderdream.util.video.demo01.BatchVideoCreator;
import lombok.extern.slf4j.Slf4j;
import com.coderdream.util.video.VideoMergerUtil;


@Slf4j
public class GenVideoUtil {

  public static void process(String folderPath, String subFolder)
    throws InterruptedException {
    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_cht"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;
    SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,
      videoPath);
  }

  public static void processV3(String folderPath, String subFolder) {
    long startTime = System.currentTimeMillis();
    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_cht"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;
    FFmpegParallelOptimized.process(imagePath, audioPath,
      videoPath);
    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("批量生成视频成功: {} , 耗时: {}", subFolder,
      CdTimeUtil.formatDuration(durationMillis));
  }

  public static void processV4(String bookName, String folderPath,
    String subFolder) {
    // 先检查头视频有没有生成，如果没有，则生成 TODO
    GenVideoUtil.genHeadVideo(bookName);

    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_cht"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;
    FFmpegOneByOne.process(imagePath, audioPath,
      videoPath);
  }

  public static void processV5(String bookName, String folderPath,
    String subFolder) {
    // 先检查头视频有没有生成，如果没有，则生成 TODO
    GenVideoUtil.genHeadVideo(bookName);
    List<Path> paths1 = processPartOne(folderPath, subFolder);
    List<Path> paths2 = processPartTwo(folderPath, subFolder);
    List<Path> paths3 = processPartThree(folderPath, subFolder);
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;
    Path outputDir = Path.of(videoPath);
    Mp4MergeUtil.mergeMp4Files(paths1, paths2, paths3, folderPath, subFolder,
      outputDir);
  }

  public static List<Path> processPartOne(String folderPath,
    String subFolder) {
    String backgroundImageName =
      OperatingSystem.getBaseFolder() + File.separator + "bgmusic"
        + File.separator
        + "content_bg.png";
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_dual_en"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_part1"
        + File.separator;
    return FFmpegOneByMany.process(backgroundImageName, audioPath, videoPath);
  }

  public static List<Path> processPartTwo(String folderPath,
    String subFolder) {
    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_en"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio"
        + File.separator + "en_slow" + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_part2"
        + File.separator;
    return FFmpegOneByOne.process(imagePath, audioPath,
      videoPath);
  }

  public static List<Path> processPartThree(String folderPath,
    String subFolder) {
    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_dual"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_part3"
        + File.separator;
    return FFmpegOneByOne.process(imagePath, audioPath, videoPath);
  }


  public static void genHeadVideo(String bookName) {
    String folderPath = OperatingSystem.getFolderPath(bookName);
    long startTime = System.currentTimeMillis();
    // D:\0000\ppt\Book02\cover
    String imagePath =
      folderPath + File.separator + "cover" + File.separator;
    String audioNameWithPath =
      OperatingSystem.getBaseFolder() + File.separator + bookName
        + File.separator + "head"
        + File.separator + bookName + "_head.wav";
    GenHeadVideo.process(folderPath, imagePath, audioNameWithPath);
    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("批量生成开场视频成功: {} , 耗时: {}", folderPath,
      CdTimeUtil.formatDuration(durationMillis));
  }


  public static void processV2(String folderPath, String subFolder) {
//        String imageDir = "path/to/your/images";
//        String audioDir = "path/to/your/audios";
//        String outputDir = "path/to/your/output/videos";

    String imagePath =
      folderPath + File.separator + subFolder + File.separator + "pic_cht"
        + File.separator;
    String audioPath =
      folderPath + File.separator + subFolder + File.separator + "audio_mix"
        + File.separator;
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;

    // 根据你的 M4 芯片的核心数和任务特性调整以下参数
    int corePoolSize = Runtime.getRuntime()
      .availableProcessors(); // 通常设置为 CPU 核心数
    int maxPoolSize = corePoolSize * 2;  // 最大线程数，可以根据需要调整
    long keepAliveTime = 60L; // 空闲线程存活时间
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(
      100); // 任务队列，设置合适的容量

    BatchVideoCreator creator = new BatchVideoCreator(imagePath, audioPath,
      videoPath,
      corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue);
    creator.processVideos();
  }

  /**
   * 生成视频合并
   *
   * @param folderPath      文件夹路径
   * @param subFolder       子文件夹
   * @param shortSubFolder  短子文件夹
   * @param bookFolderName  书籍文件夹名称
   * @param bookName        书籍名称
   * @param chapterFileName 章节文件名称
   */
  public static void processVideoMerger(String folderPath, String subFolder,
    String shortSubFolder, String bookFolderName,
    String bookName, String chapterFileName) {
    String videoPath =
      folderPath + File.separator + subFolder + File.separator + "video_cht"
        + File.separator;
//    String outputFile = folderPath + File.separator + subFolder + File.separator + "video_merge" + File.separator ;

    Map<String, String> chapterNameMap = new HashMap<>();
    List<String> stringList = FileUtil.readLines(chapterFileName,
      StandardCharsets.UTF_8);
    for (String line : stringList) {
      String[] split = line.split(" ");
      chapterNameMap.put(split[1], split[2]);
    }
//    String shortSubFolder = subFolder.substring(8);
    String chapterName = chapterNameMap.get(shortSubFolder);

    String mp4FileName =
      OperatingSystem.getVideoBaseFolder() + File.separator + bookFolderName
        + File.separator + bookName + "-EP-"
        + shortSubFolder + "-" + chapterName
        + File.separator + bookName + "-EP-" + shortSubFolder + "-"
        + chapterName
        + ".mp4";
    String listFileName =
      OperatingSystem.getVideoBaseFolder() + File.separator + bookFolderName
        + File.separator + bookName + "-EP-"
        + shortSubFolder + "-" + chapterName
        + File.separator + bookName + "-EP-" + shortSubFolder + "-"
        + chapterName
        + "_list.txt";

    VideoMergerUtil.mergerVideos(videoPath, mp4FileName, listFileName);
  }
}
