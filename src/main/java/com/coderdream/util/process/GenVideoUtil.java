package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.GenHeadVideo;
import com.coderdream.util.video.SingleCreateVideoUtil;

import com.coderdream.util.video.demo02.FFmpegOneByOne;
import com.coderdream.util.video.demo02.FFmpegParallelOptimized;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
      folderPath + subFolder + File.separator + "pic_cht" + File.separator;
    String audioPath =
      folderPath + subFolder + File.separator + "audio_mix" + File.separator;
    String videoPath =
      folderPath + subFolder + File.separator + "video_cht" + File.separator;
    SingleCreateVideoUtil.batchCreateSingleVideo(imagePath, audioPath,
      videoPath);
  }

  public static void processV3(String folderPath, String subFolder) {
    long startTime = System.currentTimeMillis();
    String imagePath =
      folderPath + subFolder + File.separator + "pic_cht" + File.separator;
    String audioPath =
      folderPath + subFolder + File.separator + "audio_mix" + File.separator;
    String videoPath =
      folderPath + subFolder + File.separator + "video_cht" + File.separator;
    FFmpegParallelOptimized.process(imagePath, audioPath,
      videoPath);
    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("批量生成视频成功: {} , 耗时: {}", subFolder,
      CdTimeUtil.formatDuration(durationMillis));
  }

  public static void processV4(String folderPath, String subFolder) {
    String imagePath =
      folderPath + subFolder + File.separator + "pic_cht" + File.separator;
    String audioPath =
      folderPath + subFolder + File.separator + "audio_mix" + File.separator;
    String videoPath =
      folderPath + subFolder + File.separator + "video_cht" + File.separator;
    FFmpegOneByOne.process(imagePath, audioPath,
      videoPath);
  }

  public static void genHeadVideo(String folderPath) {
    long startTime = System.currentTimeMillis();
    // D:\0000\ppt\Book02\cover
    String imagePath =
      folderPath + "cover" + File.separator;
    String audioPath =
      OperatingSystem.getBaseFolder() + "bgmusic" + File.separator;
    GenHeadVideo.process(folderPath, imagePath, audioPath);
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
      folderPath + subFolder + File.separator + "pic_cht" + File.separator;
    String audioPath =
      folderPath + subFolder + File.separator + "audio_mix" + File.separator;
    String videoPath =
      folderPath + subFolder + File.separator + "video_cht" + File.separator;

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
   * @param folderPath  文件夹路径
   * @param subFolder 子文件夹
   * @param shortSubFolder  短子文件夹
   * @param bookFolderName  书籍文件夹名称
   * @param bookName  书籍名称
   * @param chapterFileName   章节文件名称
   */
  public static void processVideoMerger(String folderPath, String subFolder,
    String shortSubFolder, String bookFolderName,
    String bookName, String chapterFileName) {
    String videoPath =
      folderPath + subFolder + File.separator + "video_cht" + File.separator;
//    String outputFile = folderPath + subFolder + File.separator + "video_merge" + File.separator ;

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
