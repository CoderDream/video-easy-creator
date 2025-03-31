package com.coderdream.util.daily;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo06.VideoEncoder02;
import com.coderdream.util.wechat.MarkdownFileGenerator;
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
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;

@Slf4j
public class DailyUtil {

  public static void process(String folderName, String title) {
//    TranslationUtil.genDescription(folderName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
    MarkdownFileGenerator.genWechatArticle(folderName, title);
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
    Map<String, String> map = new HashMap<>();
    String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
    for (String title : titleList) {
      log.info("标题：{}", title);
      String[] split = title.split("：");
      if (split.length == 2) {
        String sourceFolderName = year.substring(0, 2) + title.substring(0, 4);
        String targetFolderName = year.substring(2, 4) + title.substring(0, 4);
        String videoTitle = "【BBC六分钟英语】" + split[1];
        if (sourceVideoFilePathmap.containsKey(videoTitle)) {
          String sourcePath = sourceVideoFilePathmap.get(videoTitle);
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
    Map<String, String> map = new HashMap<>();
    String distFolderPath = OperatingSystem.getBaiduSyncDiskFolder();
    for (String title : titleList) {
      log.info("标题：{}", title);
      String[] split = title.split("：");
      if (split.length == 2) {
        String sourceFolderName = year.substring(2, 4) + title.substring(0, 4);
        String targetFolderName = year.substring(2, 4) + title.substring(0, 4);
        String videoTitle = "【BBC六分钟英语】" + split[1];
        if (sourceVideoFilePathmap.containsKey(videoTitle)) {
          String sourcePath = sourceVideoFilePathmap.get(videoTitle);
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
