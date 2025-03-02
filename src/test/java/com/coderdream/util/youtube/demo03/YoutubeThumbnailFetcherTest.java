package com.coderdream.util.youtube.demo03;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class YoutubeThumbnailFetcherTest {

  @Test
  void getThumbnail() {
    String videoUrl = "https://www.youtube.com/watch?v=5yjf1RvhflI";
    String thumbnailPath =
      OperatingSystem.getBaseFolder() + "cover" + File.separator;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    String thumbnailFileName = dateFormat.format(new Date()) + ".jpg";

    if (CdFileUtil.isFileEmpty(thumbnailFileName)) {
      YoutubeThumbnailFetcher.getThumbnail(videoUrl, thumbnailPath,
        thumbnailFileName);
    } else {
      log.info("封面文件已存在，无需重新获取");
    }
  }
}
