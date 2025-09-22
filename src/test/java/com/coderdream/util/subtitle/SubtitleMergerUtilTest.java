package com.coderdream.util.subtitle;

import org.junit.jupiter.api.Test;

/**
 * SubtitleMergerUtil 的测试类。
 *
 * @author Gemini Code Assist
 */
class SubtitleMergerUtilTest {

    @Test
    void testProcessSubtitleFile() {
        // 定义输入和输出文件路径
        String inputPath = "D:\\BT\\Esports.World.Cup.Level.Up.S01.1080p.WEB.h264-EDITH\\Esports.World.Cup.Level.Up.S01E01.1080p.WEB.h264-EDITH_eng.txt";
        String outputPath = "D:\\BT\\Esports.World.Cup.Level.Up.S01.1080p.WEB.h264-EDITH\\Esports.World.Cup.Level.Up.S01E01.1080p.WEB.h264-EDITH_eng_single.txt";

        // 调用工具类的主方法进行处理
        SubtitleMergerUtil.processSubtitleFile(inputPath, outputPath);
    }
}
