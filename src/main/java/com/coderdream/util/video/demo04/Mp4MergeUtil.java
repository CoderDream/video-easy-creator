package com.coderdream.util.video.demo04;

import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo06.VideoEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mp4MergeUtil {

  /**
   * 合并指定文件夹中的所有MP4文件，每10个文件为一组合并，最后剩余的文件继续合并
   *
   * @param inputDir  MP4文件夹路径
   * @param outputDir 合并后输出文件夹路径
   */
  public static void mergeMp4Files(String folderPath, String chapterName,
    Path inputDir, Path outputDir) {
    // 记录方法调用的开始时间
    long startTime = System.currentTimeMillis();

    try {
      if (!Files.exists(inputDir)) {
        Files.createDirectories(inputDir);
        log.info("目录创建成功: {}", inputDir);
      }

      // 获取文件夹中所有的MP4文件
      List<Path> mp4Files;
      try (Stream<Path> pathStream = Files.walk(
        inputDir)) {  // 使用 try-with-resources
        mp4Files = pathStream
          .filter(file -> file.toString().endsWith(".mp4"))
          .sorted(Comparator.comparing(Path::getFileName))
          .collect(Collectors.toList());
      }

      log.info("找到{}个MP4文件进行合并", mp4Files.size());

      // 按每10个文件分为一组
      List<List<Path>> fileGroups = partitionFiles(mp4Files, 10);

      // 创建存放list.txt文件的目录
      String listFilePath =
        folderPath + File.separator + chapterName + File.separator
          + "video_list_file";
      Path listDir = Paths.get(listFilePath);
      if (!Files.exists(listDir)) {
        Files.createDirectories(listDir);
        log.info("目录创建成功: {}", listDir);
      }

      int count = 0;
      for (List<Path> group : fileGroups) {
        group.sort(Comparator.comparing(Path::getFileName));
        // 生成合并后文件的名称，使用第一个和最后一个文件的文件名的数字部分
        String prefix = getFileNamePrefix(group);
        String outputFileName = "merged_" + prefix + ".mp4";
        Path groupOutputFile = outputDir.resolve(outputFileName);

        count++;
        // 生成list.txt文件存放路径
        File inputListFile = createInputListFile(group, listDir, count);
        if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(groupOutputFile.toString())) {
          log.info("文件已存在，跳过合并: {}", outputFileName);
        } else {
          log.info("开始合并文件: {}", outputFileName);
          // 合并文件
          mergeFileGroup(inputListFile, groupOutputFile);
        }
      }

      // 输出合并完成日志
      log.info("所有MP4文件合并完成");

    } catch (IOException e) {
      log.error("文件处理异常", e);
    } finally {
      // 记录方法调用的结束时间并计算耗时
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      log.info("合并操作耗时：{}毫秒", duration);
    }
  }

  /**
   * 将MP4文件按指定的大小分组
   *
   * @param files     待分组的文件列表
   * @param groupSize 每组的文件数量
   * @return 按照指定大小分组后的文件列表
   */
  private static List<List<Path>> partitionFiles(List<Path> files,
    int groupSize) {
    List<List<Path>> result = new ArrayList<>();
    for (int i = 0; i < files.size(); i += groupSize) {
      result.add(files.subList(i, Math.min(i + groupSize, files.size())));
    }
    return result;
  }

  /**
   * 使用FFmpeg合并指定的MP4文件组
   *
   * @param inputListFile 输入文件列表的路径
   * @param output        合并后的输出文件路径
   * @throws IOException 如果FFmpeg命令执行失败
   */
  private static void mergeFileGroup(File inputListFile, Path output)
    throws IOException {
    // 拼接FFmpeg命令，进行重新编码以避免音画不同步
//        String command = String.format(
//                "ffmpeg -y -f concat -safe 0 -i %s -c:v libx264 -c:a aac -strict experimental %s",
//                inputListFile.getAbsolutePath(), output.toString());

    long startTime = System.currentTimeMillis();
    List<String> command = new ArrayList<>();
    command.add("ffmpeg");
    command.add("-y");
    command.add("-f");
    command.add("concat");
    command.add("-safe");
    command.add("0");
    command.add("-i");
    command.add(inputListFile.getAbsolutePath());
    String os = OperatingSystem.getOS();
    if (OS_WINDOWS.equals(os)) {
      command.add("-c:v");
      command.add("h264_nvenc");
    } else if (OS_MAC.equals(os)) {
      command.add("-c:v");
      command.add("h264_videotoolbox");
    } else {
      command.add("-c:v");
      command.add("libx264");
    }

    command.add("-c:a");
    command.add("aac");
    command.add("-strict");
    command.add("experimental");
    command.add(output.toString());

    log.error("执行 FFmpeg 命令: {}", String.join(" ", command));

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    Process process;
    try {
      process = processBuilder.start();

      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.trace("{}", line);
        }
      }

//            try (BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    log.info("FFmpeg输出：{}", line);
//                }
//            }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        log.error("FFmpeg命令执行失败，退出码：{}", exitCode);
      } else {
        log.info("MP4文件组合并成功：{}", output);
        long endTime = System.currentTimeMillis();
        log.info("MP4文件组合并成功: {}, 耗时: {}", output,
          CdTimeUtil.formatDuration(endTime - startTime));

      }

    } catch (InterruptedException e) {
      log.error("FFmpeg进程被中断", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 创建包含所有需要合并的文件路径的输入文件
   *
   * @param fileGroup 需要合并的MP4文件组
   * @param listDir   存放list.txt的文件夹路径
   * @return 创建的list.txt文件
   * @throws IOException 如果创建文件失败
   */
  private static File createInputListFile(List<Path> fileGroup, Path listDir,
    int count)
    throws IOException {
    String countStr = String.format("%03d", count);
    File inputListFile = new File(listDir.toFile(),
      "input_list" + countStr + ".txt");
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(inputListFile))) {
      for (Path file : fileGroup) {
        writer.write("file '" + file.toString() + "'\n");
      }
    }
    return inputListFile;
  }

  /**
   * 获取文件名的前缀（第一个和最后一个文件的文件名）
   *
   * @param fileGroup 文件组
   * @return 文件名的前缀
   */
  private static String getFileNamePrefix(List<Path> fileGroup) {
    // 获取第一个和最后一个文件的文件名（假设文件名是三位数字）
    String firstFileName = com.coderdream.util.cd.CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      fileGroup.get(0).toFile()
        .getName());
    // 截取后三位数字作为前缀（如果文件名不以三位数开头，则直接使用整个文件名） merge_001_002.mp4
    if (firstFileName.length() == 14) {
      firstFileName = firstFileName.substring(7, 10);
    } else if (firstFileName.length() == 15) {
      firstFileName = firstFileName.substring(8, 10);
    }
    String lastFileName = com.coderdream.util.cd.CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      fileGroup.get(fileGroup.size() - 1).toFile()
        .getName());
    if (lastFileName.length() > 3) {
      lastFileName = lastFileName.substring(lastFileName.length() - 3);
    }
    return firstFileName + "_" + lastFileName;
  }

  public static void processMerge(String folderPath, String subFolder) {
    // 测试示例
//    Path inputDir = Paths.get("D:/0000/EnBook002/Chapter015/video_cht");
    String destinationDirectory =
      folderPath + File.separator + subFolder + File.separator + "video";
    int count = 1;
    String inputDirStr =
      folderPath + subFolder + File.separator + "video_cht";
    Path inputDir = Paths.get(inputDirStr);
    String outputDirStr =
      folderPath + subFolder + File.separator + "video_cht_"
        + count;
    String destinationFileName =
      destinationDirectory + File.separator + subFolder + ".mp4";

    // 确保输出目录存在
    File dir = new File(outputDirStr);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("目录创建成功: {}", dir.getAbsolutePath());
    }
    Path outputDir = Paths.get(outputDirStr);
    // 获取文件夹中的所有文件（不包括子文件夹中的文件）
    List<File> files = FileUtil.loopFiles(inputDir,
      file -> !file.isDirectory());
    if (files.size() <= 1) {
      return;
    } else {
      // 如果video文件夹下的ChapterXXX.mp4已经存在，则不再处理
      if(com.coderdream.util.cd.CdFileUtil.isFileEmpty(destinationFileName)) {
        mergeMp4Files(folderPath, subFolder, inputDir, outputDir);

        files = FileUtil.loopFiles(outputDir,
                file -> !file.isDirectory());
        while (files.size() > 1) {
          inputDirStr =
                  folderPath + File.separator + subFolder + File.separator + "video_cht_"
                          + count;
          inputDir = Paths.get(inputDirStr);
          outputDirStr =
                  folderPath + File.separator + subFolder + File.separator + "video_cht_"
                          + (count + 1);
          // 确保输出目录存在
          dir = new File(outputDirStr);
          if (!dir.exists() && dir.mkdirs()) {
            log.info("目录创建成功: {}", dir.getAbsolutePath());
          }
          outputDir = Paths.get(outputDirStr);

          mergeMp4Files(folderPath, subFolder, inputDir, outputDir);

          // 获取文件夹中的所有文件（不包括子文件夹中的文件）
          files = FileUtil.loopFiles(outputDir, file -> !file.isDirectory());

          count++;
        }
      }
    }

    // 拷贝 outputDir 到最终的文件夹中
    if (files.size() == 1 && com.coderdream.util.cd.CdFileUtil.isFileEmpty(destinationFileName)) {
      // 将视频拷贝到最终的文件夹中
      log.info("已完成合并，无需进一步操作");
      String sourceFile = files.get(0).getAbsolutePath(); // 替换成你的源文件路径
      // 确保输出目录存在
      File destinationDir = new File(destinationDirectory);
      if (!destinationDir.exists() && destinationDir.mkdirs()) {
        log.info("目录创建成功: {}", destinationDir.getAbsolutePath());
      }
      try {
        Path copiedFile = FileCopyUtils.copyFileToDirectory(sourceFile,
          destinationFileName,
          destinationDirectory, true); // 需要替换现有文件就设置为 true
        log.info("文件拷贝成功到: {}", copiedFile);
      } catch (IOException e) {
        log.error("拷贝文件出错: {}", e.getMessage(), e); // 打印完整的堆栈信息
      }
    }
//        else {
//            // 将视频拷贝到最终的文件夹中
//            log.info("已完成合并，无需进一步操作");
//        }
//    // 删除临时文件夹
//    if(!CdFileUtil.isFileEmpty(destinationFileName)) {
//      for (String tempOutputDir : fileSet) {
//        boolean del = CdFileUtil.del(tempOutputDir);
//        if (del) {
//          log.info("临时文件夹删除成功: {}", tempOutputDir);
//        }
//      }
//    } else {
//      log.info("最终文件不为空，不删除临时文件夹");
//    }

    // 如果最终文件不为空，则不删除临时文件夹
    if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(destinationFileName)) {
      log.info("文件已存在，无需合并: {}", destinationFileName);
      // 删除旧的合并文件
      for (int index = 1; index < 6; index++) {
        String outputDirStr2 =
          folderPath + subFolder + File.separator + "video_cht_"
            + index;
        boolean del = FileUtil.del(outputDirStr2);
        if (del) {
          log.info("临时文件夹删除成功: {}", outputDirStr2);
        } else {
          log.info("临时文件夹删除失败: {}", outputDirStr2);
        }
      }
    }

    // 重编码视频文件，用于B站发布
    String outputFilePath = com.coderdream.util.cd.CdFileUtil.addPostfixToFileName(destinationFileName,
      "_new");
    if (
      com.coderdream.util.cd.CdFileUtil.isFileEmpty(outputFilePath) && !com.coderdream.util.cd.CdFileUtil.isFileEmpty(
      destinationFileName)) {      String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
      outputFilePath);
      log.info("视频编码完成: {}", encodedVideo);

    }

  }
}
