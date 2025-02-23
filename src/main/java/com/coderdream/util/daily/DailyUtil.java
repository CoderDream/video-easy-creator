package com.coderdream.util.daily;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.wechat.MarkdownFileGenerator;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;

@Slf4j
public class DailyUtil {

  public static void process(String folderName, String title) {
//    TranslationUtil.genDescription(folderName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
    MarkdownFileGenerator.genWechatArticle(folderName, title);
  }

  /**
   * 生成描述
   *
   * @param srtFileName    生成描述的文件名
   * @param srtFileNameChn 生成简体描述的文件名
   * @param srtFileNameCht 生成繁体描述的文件名
   */
  public static void generateDescription(String srtFileName,
    String srtFileNameChn, String srtFileNameCht) {

    long startTime = System.currentTimeMillis(); // 记录开始时间
    // 2. 生成描述
    String prompt = FileUtil.readString(
      CdFileUtil.getResourceRealPath() + File.separator + "youtube"
        + File.separator + "description_prompt.txt",
      StandardCharsets.UTF_8);
    prompt += "字幕如下：";
    prompt += FileUtil.readString(
      srtFileName,
      StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent;
    try {
      generatedContent = GeminiApiUtil.generateContent(prompt);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }

    File fileChn = new File(srtFileNameChn);
    File fileCht = new File(srtFileNameCht);
    try {
      FileUtils.writeStringToFile(fileChn,
        ZhConverterUtil.toSimple(generatedContent.text()), "UTF-8");
      FileUtils.writeStringToFile(fileCht,
        ZhConverterUtil.toTraditional(generatedContent.text()), "UTF-8");
      long elapsedTime = System.currentTimeMillis() - startTime; // 计算耗时
      log.info("写入完成，文件路径: {}， {}，共计耗时：{}", srtFileNameChn,
        srtFileNameCht, CdTimeUtil.formatDuration(elapsedTime));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
