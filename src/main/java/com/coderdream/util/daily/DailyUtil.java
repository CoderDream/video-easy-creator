package com.coderdream.util.daily;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.ThumbnailInfoEntity;
import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.entity.YoutubeVideoSplitEntity;
import com.coderdream.util.audio.FfmpegUtil2;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.file.FileRenameUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.pic.ImageTextOverlayUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.GenSubtitleUtil;
import com.coderdream.util.video.Mp4Splitter;
import com.coderdream.util.video.demo06.VideoEncoder02;
import com.coderdream.util.wechat.MarkdownFileGenerator;
import com.coderdream.util.wechat.MarkdownFileGenerator05;
import com.coderdream.util.whisper.WhisperUtil;
import com.coderdream.util.youtube.demo03.YoutubeThumbnailFetcher;
import com.coderdream.util.youtube.demo06.CommandUtil06;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;

@Slf4j
public class DailyUtil {

  public static void processYoutube() {

  }

  private static void genRawSubtitle(String category, String dateString) {
  }

  /**
   *  生成双语字幕脚本
   * @param category category
   * @param dateString dateString
   */
  private static void genDualSubtitleContent(String category,
    String dateString) {
    String inputPathMp3 =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator
        + dateString + File.separator + dateString
        + ".mp3";

    final String txtFileName = CdFileUtil.changeExtension(inputPathMp3, "txt");
    String pureFileName = CdFileUtil.changeExtension(txtFileName, "txt");
    pureFileName = CdFileUtil.addPostfixToFileName(pureFileName,
      "_script_pure");
    String pureGeminiFileName = CdFileUtil.addPostfixToFileName(pureFileName,
      "_gemini");

    if (!CdFileUtil.isFileEmpty(inputPathMp3) && !CdFileUtil.isFileEmpty(
      pureFileName) && CdFileUtil.isFileEmpty(pureGeminiFileName)) {
      // 提取音频文件，生成mp3文件
      String prompt = FileUtil.readString(
        CdFileUtil.getResourceRealPath() + File.separator + "youtube"
          + File.separator + "gemini_prompt.txt",
        StandardCharsets.UTF_8);
      prompt += FileUtil.readString(pureFileName, StandardCharsets.UTF_8);
      // 生成文本内容（阻塞式）
      GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);

      try {
        String text = generatedContent.text();
        text = text.replace("[", "");
        text = text.replace("]", "");
        FileUtils.writeStringToFile(
          new File(pureGeminiFileName),
          text, "UTF-8");
      } catch (IOException e) {
        log.error("生成文本内容失败", e);
      }
      log.info("4. Generated content: {}", generatedContent);
    } else {
      log.info("生成双语字幕脚本: {}", pureGeminiFileName);
    }
  }

  private static void genMp3(String category, String dateString) {
    String inputPathMp4 =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator
        + dateString + File.separator + dateString
        + ".mp4";
    // 生成mp3
    String inputPathMp3 = CdFileUtil.changeExtension(inputPathMp4, "mp3");
    if (CdFileUtil.isFileEmpty(inputPathMp3)) {
      // 提取音频文件，生成mp3文件
      FfmpegUtil2.extractAudioFromMp4(inputPathMp4, inputPathMp3);
    }
  }

  private static void generateNewThumbnail(String category, String dateString) {
    String folderPath =
      OperatingSystem.getFolderPath(category) + File.separator + dateString;
    String formatName = "png";
    String backgroundImagePath =
      folderPath + File.separator + dateString + "." + formatName;

    String outputImagePath =
      folderPath + File.separator + dateString + "_cover." + formatName;
    ThumbnailInfoEntity thumbnailInfoEntity = getThumbnailInfoEntity(category,
      dateString);
    if (thumbnailInfoEntity != null) {
      ImageTextOverlayUtil.addTextOverlay(backgroundImagePath, outputImagePath,
        thumbnailInfoEntity.getHeadTitle(), thumbnailInfoEntity.getSubTitle(),
        thumbnailInfoEntity.getMainTitle(), formatName);
    } else {
      log.error("未找到封面字符串");
    }
  }

  private static void processSubtitle(String category, String dateString) {

    String mp4FilePath =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator
        + dateString + File.separator + dateString
        + ".mp4";

    GenSubtitleUtil.processSrtAndGenDescription(mp4FilePath);
  }

  private static void cutVideo(String category, String dateString) {
//    String folderName = "20250401";
    String timeStr = getTimeStr(category, dateString);
    if (StrUtil.isEmpty(timeStr)) {
      log.error("未找到时间字符串");
      return;
    }

    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator + dateString;
    String mp4FilePath =
      folderPath + File.separator + dateString + ".mp4";
    String rawFilePath = CdFileUtil.addPostfixToFileName(mp4FilePath,
      "_raw");
    boolean success = FileRenameUtil.renameFileOverride(mp4FilePath,
      rawFilePath);
    if (success) {
      log.debug("文件原地重命名成功: {} -> {}", mp4FilePath, rawFilePath);
    } else {
      log.error("文件原地重命名失败: {}", mp4FilePath);
      return;
    }
    // 示例用法
//    String inputFilePath = "D:\\0000\\0003_PressBriefings\\250128\\250128.mp4";
    // "00:00:34,000 --> 00:36:47,000";// 00:00:45,560 --> 00:00:49,960
//        00:00:49,960 --> 00:00:53,640
    String[] times = timeStr.split(" --> ");
    String startTime = times[0];//"00:00:03,400";
    String endTime = times[1];//"00:00:13,680";

    String splitFile = Mp4Splitter.splitVideo(rawFilePath, startTime,
      endTime, mp4FilePath);

    if (splitFile != null) {
      log.info("视频分割成功，文件保存在: {}", splitFile);
      // 清空文件
      boolean b =  CdFileUtil.emptyYoutubeVideoSplitFile();
      if (b) {
        log.info("清空文件成功");
      } else {
        log.error("清空文件失败");
      }
    } else {
      log.error("视频分割失败!");
    }
  }

  public static ThumbnailInfoEntity getThumbnailInfoEntity(String category,
    String dateString) {
    List<ThumbnailInfoEntity> thumbnailInfoEntityList = CdFileUtil.getThumbnailInfoEntityList();
    if (CollectionUtil.isNotEmpty(thumbnailInfoEntityList)) {
      for (ThumbnailInfoEntity thumbnailInfoEntity : thumbnailInfoEntityList) {
        if (thumbnailInfoEntity.getCategory().equals(category)
          && thumbnailInfoEntity.getDateString().equals(dateString)) {
          return thumbnailInfoEntity;
        }
      }
    }
    return null;
  }

  public static String getTimeStr(String category, String dateString) {
    List<YoutubeVideoSplitEntity> youtubeVideoSplitEntityList = CdFileUtil.getYoutubeVideoSplitEntityList();
    if (CollectionUtil.isNotEmpty(youtubeVideoSplitEntityList)) {
      for (YoutubeVideoSplitEntity youtubeVideoSplitEntity : youtubeVideoSplitEntityList) {
        if (youtubeVideoSplitEntity.getCategory().equals(category)
          && youtubeVideoSplitEntity.getDateString().equals(dateString)) {
          return youtubeVideoSplitEntity.getTimeStr();
        }
      }
    }
    return "";
  }

  public static void downloadVideoAndThumbnail(String category,
    String dateString, String videoId) {
    String videoLink =
      "https://www.youtube.com/watch?v=" + videoId; // // 替换为实际的视频链接
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator + dateString;
    if (!new File(folderPath).exists()) {
      boolean mkdir = new File(folderPath).mkdir();
      log.info("mkdir: {}", mkdir);
    }

    String outputFileName =
      folderPath + File.separator + dateString + ".mp4"; // 替换为期望的输出路径和文件名
    if (CdFileUtil.isFileEmpty(outputFileName)) {
      CommandUtil06.downloadBest720p(videoLink, outputFileName);
    } else {
      log.info("视频文件已存在，无需重新下载: {}", outputFileName);
    }

    String thumbnailPath =
      CdFileUtil.changeExtension(outputFileName, "png");

    if (CdFileUtil.isFileEmpty(thumbnailPath)) {
      YoutubeThumbnailFetcher.getThumbnail(videoLink, thumbnailPath);
      log.info("封面文件下载成功: {}", thumbnailPath);
    } else {
      log.info("封面文件已存在，无需重新获取: {}", thumbnailPath);
    }
  }


  public static void process(String folderName, String title) {
//    TranslationUtil.genDescription(folderName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
    MarkdownFileGenerator.genWechatArticle(folderName, title);
  }

  public static void processHalfHourEnglish(String folderName, String title) {
//    TranslationUtil.genDescription(folderName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
    MarkdownFileGenerator05.genWechatArticle(folderName, title);
//    MarkdownFileGenerator06.genYoutubeArticle(folderName, title);
  }

  /**
   * 生成描述
   *
   * @param srtFileName    生成描述的文件名
   * @param srtFileNameChn 生成简体描述的文件名
   * @param srtFileNameCht 生成繁体描述的文件名
   */
  public static void generateDescription(String srtFileName,
    String srtFileNameChn, String srtFileNameCht) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    // 2. 生成描述
    String prompt = FileUtil.readString(
      CdFileUtil.getResourceRealPath() + File.separator
        + "youtube"
        + File.separator + "description_prompt.txt",
      StandardCharsets.UTF_8);
    prompt += "字幕如下：";
    prompt += FileUtil.readString(srtFileName, StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
    File fileChn = new File(srtFileNameChn);
    File fileCht = new File(srtFileNameCht);
    try {
      FileUtils.writeStringToFile(fileChn,
        ZhConverterUtil.toSimple(generatedContent.text()), "UTF-8");
      FileUtils.writeStringToFile(fileCht,
        ZhConverterUtil.toTraditional(generatedContent.text()), "UTF-8");
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时
      log.info("写入完成，文件路径: {}， {}，共计耗时：{}", srtFileNameChn,
        srtFileNameCht, CdTimeUtil.formatDuration(elapsedTime));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static void syncFilesToQuark(String year) {
    String yearPath = "D:\\14_LearnEnglish\\6MinuteEnglish\\" + year;

    List<File> files = CdFileUtil.getFirstLevelDirectories(
      yearPath);
    if (CollectionUtil.isEmpty(files)) {
      log.error("图片文件夹为空，退出处理流程；{}", yearPath);
    } else {
      String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
      for (File file : files) {
        String folderName = file.getName();
        String folderPath = file.getAbsolutePath();
        log.info("文件路径：{}", folderName);
        // D:\14_LearnEnglish\000_BBC\BaiduSyncdisk\000_BBC
        // D:\14_LearnEnglish\6MinuteEnglish\quark_share
        String newFolderPath =
          OperatingSystem.getBaiduSyncDiskFolder() + File.separator
            + year + File.separator
            + folderName;
        if (!FileUtil.exist(newFolderPath)) {
          FileUtil.mkdir(newFolderPath);
          log.info("文件夹创建成功：{}", newFolderPath);
        }
        String fileNameC =
          folderName + "_中英双语对话脚本.txt";
        String fileNameD =
          folderName + "_完整词汇表.xlsx";
        String fileNameE =
          folderName + "_核心词汇表.xlsx";
        String fileNameF =
          folderName + "_高级词汇表.xlsx";
        if (!CdFileUtil.isFileEmpty(
          folderPath + File.separator + fileNameC)
          && CdFileUtil.isFileEmpty(
          newFolderPath + File.separator + fileNameC)) {
          FileUtil.copyFile(folderPath + File.separator + fileNameC,
            newFolderPath + File.separator + fileNameC,
            StandardCopyOption.REPLACE_EXISTING);
        }

        if (!CdFileUtil.isFileEmpty(
          folderPath + File.separator + fileNameD)
          && CdFileUtil.isFileEmpty(
          newFolderPath + File.separator + fileNameD)) {
          FileUtil.copyFile(folderPath + File.separator + fileNameD,
            newFolderPath + File.separator + fileNameD,
            StandardCopyOption.REPLACE_EXISTING);
        }

        if (!CdFileUtil.isFileEmpty(
          folderPath + File.separator + fileNameE)
          && CdFileUtil.isFileEmpty(
          newFolderPath + File.separator + fileNameE)) {
          FileUtil.copyFile(folderPath + File.separator + fileNameE,
            newFolderPath + File.separator + fileNameE,
            StandardCopyOption.REPLACE_EXISTING);
        }

        if (!CdFileUtil.isFileEmpty(
          folderPath + File.separator + fileNameF)
          && CdFileUtil.isFileEmpty(
          newFolderPath + File.separator + fileNameF)) {
          FileUtil.copyFile(folderPath + File.separator + fileNameF,
            newFolderPath + File.separator + fileNameF,
            StandardCopyOption.REPLACE_EXISTING);
        }

        // 找pdf和mp3
        String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
        String pdfFileNamePath = folderPath + File.separator + pdfFileName;
        if (!CdFileUtil.isFileEmpty(pdfFileNamePath)) {
          if (folderName.equals("250327")) {
            System.out.println("找到了" + folderName);
          }
          String targetPdfFileName =
            newFolderPath + File.separator + pdfFileName;
          if (CdFileUtil.isFileEmpty(targetPdfFileName)) {
            FileUtil.copyFile(pdfFileNamePath, targetPdfFileName,
              StandardCopyOption.REPLACE_EXISTING);
          } else {
            log.error("目标pdf文件已存在，退出处理流程；{}", targetPdfFileName);
//            continue;
          }
          // 同步mp3文件
          String mp3FileNamePath = CdFileUtil.changeExtension(pdfFileNamePath,
            "mp3");
          String mp3FileName = new File(mp3FileNamePath).getName();
          if (!CdFileUtil.isFileEmpty(mp3FileNamePath)) {
            if (!CdFileUtil.isFileEmpty(mp3FileNamePath)
              && CdFileUtil.isFileEmpty(
              newFolderPath + File.separator + mp3FileName)) {
              FileUtil.copyFile(mp3FileNamePath,
                newFolderPath + File.separator + mp3FileName,
                StandardCopyOption.REPLACE_EXISTING);
            } else {
              log.error("目标mp3文件已存在，退出处理流程；{}",
                newFolderPath + File.separator + mp3FileName);
            }
          } else {
            log.error("找不到mp3文件，退出处理流程；{}", mp3FileNamePath);
          }
        } else {
          log.error("找不到源pdf文件，退出处理流程；{}", pdfFileNamePath);
        }

        // 同步视频
        List<File> subFolders = CdFileUtil.getFirstLevelDirectories(folderPath);
        if (CollectionUtil.isNotEmpty(subFolders)) {
          for (File subFolder : subFolders) {
            String fileName = subFolder.getName();
            if (fileName.length() > 6) {
              String subFolderPath = subFolder.getAbsolutePath();
              List<File> secondLevelSubFolders = FileUtil.loopFiles(
                subFolderPath);
              for (File secondLevelSubFolder : secondLevelSubFolders) {
                String fileName2 = secondLevelSubFolder.getName();
                String fileName2Path = secondLevelSubFolder.getAbsolutePath();
                if (fileName2Path.contains(".mp4")) {
                  // 视频文件
                  log.info("视频文件：{}", fileName2Path);
                  String targetFileName =
                    distFolderPath + File.separator + year + File.separator
                      + folderName
                      + File.separator + fileName2;
                  log.info("4.源视频路径：{}", fileName2Path);
                  log.info("4.视频目标路径：{}", targetFileName);
                  if (FileUtil.exist(fileName2Path) && !FileUtil.exist(
                    targetFileName)) {
                    String encodedVideo = VideoEncoder02.encodeVideo(
                      fileName2Path,
                      targetFileName);
                    log.info("4.视频编码完成: {}", encodedVideo);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public static void syncHistoryVideoToQuark(String year) {
    List<File> fileNamesWithPath = CdFileUtil.getFirstLevelDirectories(
      OperatingSystem.getHistoryBBCFolder());
    Map<String, String> sourceVideoFilePathmap = new HashMap<>();
    assert fileNamesWithPath != null;
    for (File file : fileNamesWithPath) {
      sourceVideoFilePathmap.put(file.getName(), file.getAbsolutePath());
    }

    List<String> titleList = FileUtil.readLines(
      OperatingSystem.getBaseFolder() + File.separator + "input"
        + File.separator + "title"
        + File.separator + "title_" + year
        + ".txt", CharsetUtil.CHARSET_UTF_8);
    String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
    for (String title : titleList) {
      log.info("标题：{}", title);
      String[] split = title.split("：");
      if (split.length == 2) {
        String folderName = year.substring(2, 4) + title.substring(0, 4);
        String videoTitle = "【BBC六分钟英语】" + split[1];
        if (sourceVideoFilePathmap.containsKey(videoTitle)) {
          String sourcePath = sourceVideoFilePathmap.get(videoTitle);
          String sourceFileName =
            sourcePath + File.separator + videoTitle + ".mp4";
          String targetFileNameOld =
            distFolderPath + File.separator + year
              + File.separator + folderName
              + "_" + videoTitle + ".mp4";
          String targetFileName =
            distFolderPath + File.separator + year + File.separator + folderName
              + File.separator + folderName
              + "_" + videoTitle + ".mp4";
          log.info("1.源视频路径：{}", sourceFileName);
          log.info("1.视频目标路径：{}", targetFileName);
          if (FileUtil.exist(targetFileNameOld) && !FileUtil.exist(
            targetFileName)) {
            FileUtil.copyFile(targetFileNameOld,
              targetFileName,
              StandardCopyOption.REPLACE_EXISTING);
            log.info("复制视频完成:源 {} 目标 {}", targetFileNameOld,
              targetFileName);
          }

          if (FileUtil.exist(sourceFileName) && !FileUtil.exist(
            targetFileName)) {
            String encodedVideo = VideoEncoder02.encodeVideo(sourceFileName,
              targetFileName);
            log.info("视频编码完成: {}", encodedVideo);
          }
        }
      }
    }
  }

  public static void moveHistoryVideoToQuark(String year) {
    List<File> fileNamesWithPath = CdFileUtil.getFirstLevelDirectories(
      "C:\\Users\\CoderDream\\Videos\\History_BBC\\");
    Map<String, String> sourceVideoFilePathmap = new HashMap<>();
    assert fileNamesWithPath != null;
    for (File file : fileNamesWithPath) {
      sourceVideoFilePathmap.put(file.getName(), file.getAbsolutePath());
    }

    String titlePath =
      OperatingSystem.getBaseFolder() + File.separator + "input"
        + File.separator + "title";
    List<String> titleList = FileUtil.readLines(
      titlePath + File.separator + "title_" + year
        + ".txt", CharsetUtil.CHARSET_UTF_8);
//    Map<String, String> map = new HashMap<>();
    String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
    for (String title : titleList) {
      log.info("标题：{}", title);
      String[] split = title.split("：");
      if (split.length == 2) {
        String sourceFolderName = year.substring(0, 2) + title.substring(0, 4);
        String targetFolderName = year.substring(2, 4) + title.substring(0, 4);
        String videoTitle = "【BBC六分钟英语】" + split[1];
        if (sourceVideoFilePathmap.containsKey(videoTitle)) {
//          String sourcePath = sourceVideoFilePathmap.get(videoTitle);
//          String sourceFileName =
//            sourcePath + File.separator + videoTitle + ".mp4";
          String sourceFileName =
            distFolderPath + File.separator + year + File.separator
              + sourceFolderName
              + "_" + videoTitle + ".mp4";
          String targetFileName =
            distFolderPath + File.separator + year + File.separator
              + targetFolderName + File.separator
              + targetFolderName
              + "_" + videoTitle + ".mp4";
          log.info("2.源视频路径：{}", sourceFileName);
          log.info("2.视频目标路径：{}", targetFileName);
          if (FileUtil.exist(sourceFileName) && !FileUtil.exist(
            targetFileName)) {
//            FileUtil.move(sourceFileName, targetFileName);
            try {
              Files.move(Paths.get(sourceFileName), Paths.get(targetFileName));
            } catch (IOException e) {
              log.error("移动文件出错: {}", e.getMessage(), e);
            }
//            String encodedVideo = VideoEncoder02.encodeVideo(sourceFileName,
//              targetFileName);
//            log.info("视频编码完成: {}", encodedVideo);
          }
        }
      }
    }
  }

  public static void moveHistoryVideoToQuarkV2(String year) {
    List<File> fileNamesWithPath = CdFileUtil.getFirstLevelDirectories(
      "C:\\Users\\CoderDream\\Videos\\History_BBC\\");
    Map<String, String> sourceVideoFilePathmap = new HashMap<>();
    assert fileNamesWithPath != null;
    for (File file : fileNamesWithPath) {
      sourceVideoFilePathmap.put(file.getName(), file.getAbsolutePath());
    }

    List<String> titleList = FileUtil.readLines(
      "D:\\input\\title_" + year
        + ".txt", CharsetUtil.CHARSET_UTF_8);
//    Map<String, String> map = new HashMap<>();
    String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
    for (String title : titleList) {
      log.info("标题：{}", title);
      String[] split = title.split("：");
      if (split.length == 2) {
        String sourceFolderName = year.substring(2, 4) + title.substring(0, 4);
        String targetFolderName = year.substring(2, 4) + title.substring(0, 4);
        String videoTitle = "【BBC六分钟英语】" + split[1];
        if (sourceVideoFilePathmap.containsKey(videoTitle)) {
//          String sourcePath = sourceVideoFilePathmap.get(videoTitle);
//          String sourceFileName =
//            sourcePath + File.separator + videoTitle + ".mp4";
          String sourceFileName =
            distFolderPath + File.separator + year + File.separator
              + sourceFolderName
              + "_" + videoTitle + ".mp4";
          String targetFileName =
            distFolderPath + File.separator + year + File.separator
              + sourceFolderName + File.separator
              + targetFolderName
              + "_" + videoTitle + ".mp4";
          log.info("3.源视频路径：{}", sourceFileName);
          log.info("3.视频目标路径：{}", targetFileName);
          if (FileUtil.exist(sourceFileName) && !FileUtil.exist(
            targetFileName)) {
//            FileUtil.move(sourceFileName, targetFileName);
            try {
              Files.move(Paths.get(sourceFileName), Paths.get(targetFileName));
            } catch (IOException e) {
              log.error("移动文件出错: {}", e.getMessage(), e);
            }
//            String encodedVideo = VideoEncoder02.encodeVideo(sourceFileName,
//              targetFileName);
//            log.info("视频编码完成: {}", encodedVideo);
          }
        }
      }
    }
  }

  /**
   * 生成标题文件
   *
   * @param year 年份
   */
  public static void genTitleFile(String year) {
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + "input"
        + File.separator + "raw";// "D:\\input"; // D:\0000\input\raw
    String sourceFileName =
      folderPath + File.separator + "title_" + year + "_raw.txt";
    List<String> sourceFileContent = FileUtil.readLines(sourceFileName,
      CharsetUtil.CHARSET_UTF_8);
    List<String> targetFileContent = new ArrayList<>();
    for (int i = 0; i < sourceFileContent.size(); i += 2) {
      targetFileContent.add(sourceFileContent.get(i));
    }

    String targetFileName =
      folderPath + File.separator + "title_" + year + ".txt";
    if (CdFileUtil.isFileEmpty(targetFileName)) {
      CdFileUtil.writeToFile(targetFileName, targetFileContent);
    }
  }
}
