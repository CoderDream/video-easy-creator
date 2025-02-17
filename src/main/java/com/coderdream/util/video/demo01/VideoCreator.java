package com.coderdream.util.video.demo01;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.Callable;

@Slf4j
public class VideoCreator implements Callable<Boolean> {

    private final File imageFile;
    private final File audioFile;
    private final File outputFile;
    private final double duration;

    public VideoCreator(File imageFile, File audioFile, File outputFile, double duration) {
        this.imageFile = imageFile;
        this.audioFile = audioFile;
        this.outputFile = outputFile;
        this.duration = duration;
    }

    @Override
    public Boolean call() {
        try {
            VideoCreatorUtil.createVideo(imageFile, audioFile, outputFile, duration);
            return true;
        } catch (Exception e) {
            log.error("创建视频失败: 图片={}, 音频={}, 目标文件={}",
                    imageFile.getAbsolutePath(), audioFile.getAbsolutePath(), outputFile.getAbsolutePath(), e);
            return false;
        }
    }
}