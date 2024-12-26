package com.coderdream.util;

import com.coderdream.util.pic.HighResImageVideoUtil;
import com.coderdream.util.video.SingleCreateVideoUtil;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchProcessNoContentUtil {

  public static File batchProcess(String fileName) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    // 1. 生成图片
    List<File> imageFiles = HighResImageVideoUtil.generateImages(fileName);

    // 2 生成中英文音频
//    String audioType = CdConstants.AUDIO_TYPE_WAV;
//    SpeechUtil.genDialog2Audio(fileName, audioType);
//
//    // 确保所有任务执行完毕后关闭线程池
//    SpeechUtil.shutdownExecutor();

//    File audioDurationFileList = AudioDurationUtil.createAudioDurationFileList(
//      fileName);
//    log.info("音频时长文件：{}", audioDurationFileList);

    // 生成单个音频文件
//    File audioFile = MergeSingleAudioUtil.mergeAudio(fileName);
//    // 计算AUDIO时长
//    double duration = FfmpegUtil.getAudioDuration(audioFile);
//    log.info("音频时长：{}", duration);


    // 4. 生成单个视频
    File file = SingleCreateVideoUtil.singleCreateVideo(fileName);

      // 5. 合并视频
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
