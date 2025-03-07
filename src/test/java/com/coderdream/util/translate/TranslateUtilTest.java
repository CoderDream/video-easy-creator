package com.coderdream.util.translate;

import cn.hutool.core.thread.ThreadUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
class TranslateUtilTest {

    @Test
    void translateSrcWithPlatform() {
        String folderName = "250305";
        String filePath =
                OperatingSystem.getBaseFolder() + "0003_PressBriefings" + File.separator
                        + folderName + File.separator + folderName
                        + ".mp4";
        String srcFileNameEn = CdFileUtil.changeExtension(filePath, "srt");
        srcFileNameEn = CdFileUtil.addPostfixToFileName(srcFileNameEn,
                "." + CdConstants.SUBTITLE_EN);

        //  生成中文SRT文件
        String srcFileNameZhCn = CdFileUtil.changeExtension(filePath, "srt");
        srcFileNameZhCn = CdFileUtil.addPostfixToFileName(srcFileNameZhCn,
                "." + CdConstants.SUBTITLE_ZH_CN);
        String srcFileNameZhTw = CdFileUtil.changeExtension(filePath, "srt");
        srcFileNameZhTw = CdFileUtil.addPostfixToFileName(srcFileNameZhTw,
                "." + CdConstants.SUBTITLE_ZH_TW);

        //  通过DeepSeek服务翻译
        Integer subListSize = 10;
        int retryTime = 0;
        while ((CdFileUtil.isFileEmpty(srcFileNameZhCn) || CdFileUtil.isFileEmpty(
                srcFileNameZhCn)) && retryTime < 10) {
            if (retryTime > 0) {
                log.info(CdConstants.TRANSLATE_PLATFORM_DEEP_SEEK + " 重试次数: {}",
                        retryTime);
            }
            TranslateUtil.translateSrcWithPlatform(srcFileNameEn, srcFileNameZhCn,
                    srcFileNameZhTw, CdConstants.TRANSLATE_PLATFORM_DEEP_SEEK, subListSize);
            retryTime++;
            ThreadUtil.sleep(3000L);
        }
        if (!CdFileUtil.isFileEmpty(srcFileNameZhCn) && !CdFileUtil.isFileEmpty(
                srcFileNameZhTw)) {
            log.info("chnSrcFileName 文件已创建: {} {}", srcFileNameZhCn,
                    srcFileNameZhTw);
        } else {
            log.warn("重试 10 次后，文件仍为空: {} {}", srcFileNameZhCn,
                    srcFileNameZhTw);
        }
    }
}
