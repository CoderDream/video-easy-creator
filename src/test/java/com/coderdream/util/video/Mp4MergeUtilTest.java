package com.coderdream.util.video;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo04.Mp4MergeUtil;
import com.coderdream.util.video.demo06.VideoEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class Mp4MergeUtilTest {

    @Test
    void mergeMp4Files() {
    }

    @Test
    void processMerge() {
        String bookPath = OperatingSystem.getBaseFolder() + File.separator + "EnBook002";
        String chapterName = "Chapter012";
        Mp4MergeUtil.processMerge(bookPath, chapterName);
    }

    //

    @Test
    void processMerge_0007() {
        String bookFolderName = "0003_PressBriefings";
        String subFolder = "20250311";
        String chapterName = "20250311_白宫简报";

        String folderPath =
          OperatingSystem.getBaseFolder() + File.separator + bookFolderName + File.separator + subFolder;
        String destinationFileName =
          folderPath + File.separator + chapterName + File.separator + chapterName + ".mp4";
        // 重编码视频文件，用于B站发布
        String outputFilePath = CdFileUtil.addPostfixToFileName(destinationFileName,
          "_new");
        String encodedVideo = VideoEncoder.encodeVideo(destinationFileName,
          outputFilePath);
        log.info("视频编码完成: {}", encodedVideo);
    }

    @Test
    void processBatch02() throws InterruptedException {
        String bookName = "EnBook002";
        String folderPath = OperatingSystem.getFolderPath(bookName);

        List<String> subFolders = new ArrayList<>();
        int end = 51;
        for (int i = 13; i < end; i++) {
            String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
            subFolders.add("Chapter" + dayNumberString);
        }

        for (String subFolder : subFolders) {
//            GenVideoUtil.processV4(folderPath, subFolder);

            Mp4MergeUtil.processMerge(folderPath, subFolder);
        }
    }
}
