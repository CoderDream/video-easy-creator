package com.coderdream.util;

import com.coderdream.util.audio.AudioDurationUtil;
import com.coderdream.util.audio.MergeSingleAudioUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.ffmpeg.FfmpegUtil;
import com.coderdream.util.mstts.SpeechUtil;
import com.coderdream.util.pic.HighResImageVideoUtil5;
import com.coderdream.util.video.BatchCreateVideoUtil;
import com.coderdream.util.video.VideoMergeUtil;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchProcessUtil {

  public static File batchProcess(String fileName) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    // 1. 生成图片
    List<File> imageFiles = HighResImageVideoUtil5.generateImages(fileName);

    // 2 生成中英文音频
    String audioType = CdConstants.AUDIO_TYPE_WAV;
    SpeechUtil.genDialog2Audio(fileName, audioType);
//
//    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();

    File audioDurationFileList = AudioDurationUtil.createAudioDurationFileList(
      fileName);
    log.info("音频时长文件：{}", audioDurationFileList);

    File audioFile = MergeSingleAudioUtil.mergeAudio(fileName);
    // 计算AUDIO时长
    double duration = FfmpegUtil.getAudioDuration(audioFile);
    log.info("音频时长：{}", duration);

//     3.1 生成中文视频
    String lang = CdConstants.LANG_CN;
    BatchCreateVideoUtil.batchCreateVideo(fileName, audioType, lang);
    // 3.1 生成中文视频
//    lang = CdConstants.LANG_EN;
//    BatchCreateVideoUtil.batchCreateVideo(fileName, audioType, lang);

    // 4. 合并单个视频
//    File file = MergeSingleVideoUtil.mergeVideos(fileName);

      // 5. 合并视频
    File file = VideoMergeUtil.mergeVideos(fileName);
//
//    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
//    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
//    log.info("批处理完成，共： {} 张，耗时： {}， 返回文件为：{}", imageFiles.size(),
//      CdTimeUtil.formatDuration(durationMillis), file.getName());
//    log.info("批处理完成，耗时: {}", CdTimeUtil.formatDuration(durationMillis));

//    return file;
    return null;
  }

}
