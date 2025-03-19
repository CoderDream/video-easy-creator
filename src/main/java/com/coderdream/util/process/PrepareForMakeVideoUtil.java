package com.coderdream.util.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.subtitle.SubtitleUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;

@Slf4j
public class PrepareForMakeVideoUtil {


  public static void processForSixMinutes(String folderName) {
    String folderPath = CommonUtil.getFullPath(folderName);
    String distFolderName = "D:\\0000\\video\\0001_SixMinutes_Draft\\";
    // 0. 清理文件夹
    boolean del = FileUtil.del(distFolderName);
    log.info("删除文件夹结果：{}", del);

    // 1. 图片 文件夹拷贝
    String imageFolderName = folderPath + File.separator + folderName;
    List<File> files = FileUtil.loopFiles(imageFolderName);
    if (CollectionUtil.isEmpty(files)) {
      log.error("图片文件夹为空，退出处理流程；{}", imageFolderName);
      return;
    }
    for (File file : files) {
      if (file.isFile()) {
        String fileName = file.getName();
        FileUtil.copyFile(file.getAbsolutePath(),
          distFolderName + fileName,
          StandardCopyOption.REPLACE_EXISTING);
      }
    }
    // 2. 音频
    String audioFileName = folderPath + File.separator + "audio5.mp3";
    String destinationAudioFileName = distFolderName + "audio.mp3";
    FileUtil.copy(Paths.get(audioFileName),
      Paths.get(destinationAudioFileName), StandardCopyOption.REPLACE_EXISTING);

    // 3. 字幕
    String subtitleFileNameEng = folderPath + File.separator + "eng.srt";
    FileUtil.copy(subtitleFileNameEng, distFolderName, true);
    String subtitleFileNameChn = folderPath + File.separator + "chn.srt";
    FileUtil.copy(subtitleFileNameChn, distFolderName, true);

    // 4. 封面
  }

  public static void processYoutube(String typeName, String folderName) {
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + typeName
        + File.separator + folderName;
    String distFolderName = "D:\\0000_video" + File.separator + typeName;
    if (!FileUtil.exist(distFolderName)) {
      File mkdir = FileUtil.mkdir(distFolderName);
      log.info("创建文件夹结果：{}", mkdir);
    }
    // 0. 清理文件夹
//    boolean del = FileUtil.del(distFolderName);
//    log.error("删除文件夹结果：{} {}", del, distFolderName);
    // 2. 拷贝视频
    String mp4FilePath = folderPath + File.separator + folderName + ".mp4";
    if (!CdFileUtil.isFileEmpty(mp4FilePath)) {
      String destinationMp4FileName =
        distFolderName + File.separator + "video.mp4";
      Path copy = FileUtil.copy(Paths.get(mp4FilePath),
        Paths.get(destinationMp4FileName), StandardCopyOption.REPLACE_EXISTING);
      log.info("视频文件拷贝结果：{}", copy);
    } else {
      log.error("视频文件不存在，退出处理流程；{}", mp4FilePath);
    }

    // 3. 字幕
    String subtitleFileNameEng =
      folderPath + File.separator + folderName + ".en.srt";
    if (!CdFileUtil.isFileEmpty(subtitleFileNameEng)) {
      String destinationSubtitleFileNameEng =
        distFolderName + File.separator + CdConstants.SUBTITLE_EN + ".srt";
      Path copy = FileUtil.copy(Paths.get(subtitleFileNameEng),
        Paths.get(destinationSubtitleFileNameEng),
        StandardCopyOption.REPLACE_EXISTING);
      log.info("英文字幕文件拷贝结果：{}", copy);
    } else {
      log.error("英文字幕文件不存在，退出处理流程；{}", subtitleFileNameEng);
    }

    String subtitleFileNameZhTw =
      folderPath + File.separator + folderName + "."
        + CdConstants.SUBTITLE_ZH_TW + ".srt";
    if (!CdFileUtil.isFileEmpty(subtitleFileNameZhTw)) {
      String destinationSubtitleFileNameZhTw =
        distFolderName + File.separator + CdConstants.SUBTITLE_ZH_TW + ".srt";
      Path copy = FileUtil.copy(Paths.get(subtitleFileNameZhTw),
        Paths.get(destinationSubtitleFileNameZhTw),
        StandardCopyOption.REPLACE_EXISTING);
      log.info("繁体字幕文件拷贝结果：{}", copy);
    } else {
      log.error("繁体字幕文件不存在，退出处理流程；{}", subtitleFileNameZhTw);
    }

    // 4. 封面
    String formatName = "png";
    String coverFileName =
      folderPath + File.separator + folderName + "_cover." + formatName;
    if (!CdFileUtil.isFileEmpty(coverFileName)) {
      String destinationCoverFileName =
        distFolderName + File.separator + "cover." + formatName;
      Path copy = FileUtil.copy(Paths.get(coverFileName),
        Paths.get(destinationCoverFileName),
        StandardCopyOption.REPLACE_EXISTING);
      log.info("封面文件拷贝结果：{}", copy);
    } else {
      log.error("封面文件不存在，退出处理流程；{}", coverFileName);
    }
  }

}
