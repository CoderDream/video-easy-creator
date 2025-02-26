package com.coderdream.util.video.demo02;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import com.coderdream.util.video.PureCreateVideo;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FFmpegOneByOne {

    public static void process(String imagePath, String audioPath,
                               String videoPath) {
        long startTime = System.currentTimeMillis();
        // --- 线程池配置 ---
//    int corePoolSize = 2; // 8;  // 核心线程数：与 CPU 核心数相同
//    int maximumPoolSize = 4;// 32; // 最大线程数：允许的峰值线程数，根据实际情况调整

        // --- FFmpeg 任务 ---
        // 假设你有多个输入文件和输出文件
        // 创建videoPath目录
        File videoDir = new File(videoPath);
        if (!videoDir.exists()) {
            boolean isSuccess = videoDir.mkdirs();
            log.info("创建目录：{}，结果：{}", videoPath, isSuccess);
        }

        List<String> imagePathNameList = FileUtil.listFileNames(imagePath)
                .stream()
                .filter(fileName -> fileName.toLowerCase().endsWith(".png"))
                .sorted() // 默认按字母顺序升序排序
                .collect(Collectors.toList());


        List<String> audioPathNameList = FileUtil.listFileNames(audioPath).stream()
                .filter(fileName -> fileName.toLowerCase().endsWith(".wav"))
                .sorted() // 默认按字母顺序升序排序
                .collect(Collectors.toList());

        // 如果图片列表数量和音频列表数量不一致，则抛出异常
        if (CollectionUtil.isEmpty(imagePathNameList) || CollectionUtil.isEmpty(
                audioPathNameList)
                || imagePathNameList.size() != audioPathNameList.size()) {
            log.error("图片列表数量和音频列表数量不一致: ({}, {})", imagePathNameList.size(), audioPathNameList.size());
            return;
        } else {
            imagePathNameList.sort(String::compareTo);
            audioPathNameList.sort(String::compareTo);
        }

        // 为每个 FFmpeg 命令创建一个任务 (Callable 可以获取返回值)
        for (int i = 0; i < imagePathNameList.size(); i++) {
            String imagePathName = imagePath + imagePathNameList.get(i);
            String audioFileName = audioPath + audioPathNameList.get(i);
            String videoFileName =
                    videoPath + CdFileUtil.getPureFileNameWithoutExtensionWithPath(
                            audioFileName) + ".mp4";
            if (!CdFileUtil.isFileEmpty(videoFileName)) {
                log.info("视频文件已存在，无需重新生成，{}", videoFileName);
                continue; // 跳过已存在的视频文件
            }
            // 计算AUDIO时长
            double duration = FfmpegUtil.getAudioDuration(
                    new File(audioFileName));
            PureCreateVideo.createVideoCore(imagePathName,
                    audioFileName, videoFileName, duration);
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        log.info("批量生成视频成功，共： {} 个文件, 耗时: {}", imagePathNameList.size(), CdTimeUtil.formatDuration(durationMillis));
    }
}
