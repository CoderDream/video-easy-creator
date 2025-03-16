package com.coderdream.util.video;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenHeadVideo {

    public static void process(String folderPath, String imagePath,
                               String audioNameWithPath) {
        long startTime = System.currentTimeMillis();

        // D:\0000\ppt\Book02\cover Book02模板_41.png
        List<String> imagePathNameList = FileUtil.listFileNames(imagePath);

        // 如果图片列表数量和音频列表数量不一致，则抛出异常
        if (CollectionUtil.isEmpty(imagePathNameList)) {
            log.error("图片列表数量和音频列表数量不一致");
            return;
        } else {
            imagePathNameList.sort(String::compareTo);
        }

        // 为每个 FFmpeg 命令创建一个任务 (Callable 可以获取返回值)
        for (int i = 0; i < imagePathNameList.size(); i++) {
            String imagePathName = imagePath + imagePathNameList.get(i);
            String pureFileName = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
                    imagePathName);
            if (pureFileName.lastIndexOf("_") != -1) {
                String part = pureFileName.substring(pureFileName.lastIndexOf("_"));
                pureFileName = CdFileUtil.removePostfixToFileName(pureFileName, part);
            }
            String videoPath =
                    folderPath + File.separator + pureFileName + File.separator + "video_cht"
                            + File.separator;
            // --- FFmpeg 任务 ---
            // 假设你有多个输入文件和输出文件
            // 创建videoPath目录
            File videoDir = new File(videoPath);
            if (!videoDir.exists()) {
                boolean isSuccess = videoDir.mkdirs();
                log.info("创建目录：{}，结果：{}", videoPath, isSuccess);
            }

            String videoFileName = videoPath + "000.mp4";
            if (!CdFileUtil.isFileEmpty(videoFileName)) {
                log.info("视频文件已存在，无需重新生成，{}", videoFileName);
                continue; // 跳过已存在的视频文件
            }
            // 计算AUDIO时长
            double duration = FfmpegUtil.getAudioDuration(
                    new File(audioNameWithPath));
            PureCreateVideo.createVideoCore(imagePathName, audioNameWithPath, videoFileName,
                    duration);
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        log.info("批量生成视频成功，共： {} 个文件, 耗时: {}",
                imagePathNameList.size(), CdTimeUtil.formatDuration(durationMillis));
    }

}
