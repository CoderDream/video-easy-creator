package com.coderdream.util.process;

import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo06.VideoEncoder;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PreparePublishToYoutubeUtilTest02 {

  @Test
  void processTodo() {
    processYoutube();
  }

  public static void processYoutube() {
    // 下载视频
    List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
    for (YoutubeInfoEntity youtubeInfoEntity : youtubeVideoInfoEntityList) {
      String category = youtubeInfoEntity.getCategory();// "0003_PressBriefings"; // D:\0000\0003_PressBriefings
      String dateString = youtubeInfoEntity.getDateString();// "20250331";
      String title = youtubeInfoEntity.getTitle();// "oQE2dgqe_bI"; // // 替换为实际的视频链接

      String folderPath =
        OperatingSystem.getBaseFolder() + File.separator + category
          + File.separator + dateString;
      String destinationFileName =
        folderPath + File.separator + title + File.separator + title + ".mp4";
      // 重编码视频文件，用于B站发布
      String outputFilePath = CdFileUtil.addPostfixToFileName(
        destinationFileName,
        "_new");
      String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
        outputFilePath);
      log.info("{}视频编码完成: {}", category, encodedVideo);
    }
  }
}
