package com.coderdream.util.video;

import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.video.demo04.Mp4MergeUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
