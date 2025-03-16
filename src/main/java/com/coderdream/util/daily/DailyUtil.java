package com.coderdream.util.daily;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.coderdream.util.gemini.GeminiApiUtil;
import com.coderdream.util.wechat.MarkdownFileGenerator;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
      com.coderdream.util.cd.CdFileUtil.getResourceRealPath() + File.separator
        + "youtube"
        + File.separator + "description_prompt.txt",
      StandardCharsets.UTF_8);
    prompt += "字幕如下：";
    prompt += FileUtil.readString(srtFileName, StandardCharsets.UTF_8);
    // 生成文本内容（阻塞式）
    GeneratedContent generatedContent = GeminiApiUtil.generateContent(prompt);
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


  public static void syncFilesToQuark(String year) {
    String yearPath = "D:\\14_LearnEnglish\\6MinuteEnglish\\" + year;

    List<File> files = com.coderdream.util.cd.CdFileUtil.getFirstLevelDirectories(
      yearPath);
    if (CollectionUtil.isEmpty(files)) {
      log.error("图片文件夹为空，退出处理流程；{}", yearPath);
    }

    for (File file : files) {
      String folderName = file.getName();
      String folderPath = file.getAbsolutePath();
      log.info("文件路径：{}", folderName);
      // D:\14_LearnEnglish\000_BBC\BaiduSyncdisk\000_BBC
      // D:\14_LearnEnglish\6MinuteEnglish\quark_share
      String newFolderPath =
        "D:\\14_LearnEnglish\\000_BBC\\BaiduSyncdisk\\000_BBC" + File.separator
          + year + File.separator
          + folderName;
      if (!FileUtil.exist(newFolderPath)) {
        FileUtil.mkdir(newFolderPath);
        log.info("文件夹创建成功：{}", newFolderPath);
      }
      String fileNameC =
        folderName + "_中英双语对话脚本.txt";
      String fileNameD =
        folderName + "_完整词汇表.xlsx";
      String fileNameE =
        folderName + "_核心词汇表.xlsx";
      String fileNameF =
        folderName + "_高级词汇表.xlsx";
      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + fileNameC)
        && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        newFolderPath + File.separator + fileNameC)) {
        FileUtil.copyFile(folderPath + File.separator + fileNameC,
          newFolderPath + File.separator + fileNameC,
          StandardCopyOption.REPLACE_EXISTING);
      }

      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + fileNameD)
        && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        newFolderPath + File.separator + fileNameD)) {
        FileUtil.copyFile(folderPath + File.separator + fileNameD,
          newFolderPath + File.separator + fileNameD,
          StandardCopyOption.REPLACE_EXISTING);
      }

      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + fileNameE)
        && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        newFolderPath + File.separator + fileNameE)) {
        FileUtil.copyFile(folderPath + File.separator + fileNameE,
          newFolderPath + File.separator + fileNameE,
          StandardCopyOption.REPLACE_EXISTING);
      }

      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + fileNameF)
        && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        newFolderPath + File.separator + fileNameF)) {
        FileUtil.copyFile(folderPath + File.separator + fileNameF,
          newFolderPath + File.separator + fileNameF,
          StandardCopyOption.REPLACE_EXISTING);
      }

      // 找pdf和mp3
      String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + pdfFileName)
        && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        newFolderPath + File.separator + pdfFileName)) {
        FileUtil.copyFile(folderPath + File.separator + pdfFileName,
          newFolderPath + File.separator + pdfFileName,
          StandardCopyOption.REPLACE_EXISTING);
      }

      if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
        folderPath + File.separator + pdfFileName)) {
        String mp3FileName =
          com.coderdream.util.cd.CdFileUtil.getFileNameWithoutExtension(
            pdfFileName) + ".mp3";
        if (!com.coderdream.util.cd.CdFileUtil.isFileEmpty(
          folderPath + File.separator + mp3FileName)
          && com.coderdream.util.cd.CdFileUtil.isFileEmpty(
          newFolderPath + File.separator + mp3FileName)) {
          FileUtil.copyFile(folderPath + File.separator + mp3FileName,
            newFolderPath + File.separator + mp3FileName,
            StandardCopyOption.REPLACE_EXISTING);
        }
      }

      // 2025/02/20  18:20         9,065,952 170309_mermaids.mp3
      //2025/02/20  17:50           232,887 170309_mermaids.pdf
      //2025/03/03  10:34            12,786 170309_中英双语对话脚本.txt
      //2025/03/03  10:34            35,369 170309_完整词汇表.xlsx
      //2025/03/03  10:34             9,472 170309_核心词汇表.xlsx
      //2025/03/03  10:34            10,234 170309_高级词汇表.xlsx

      // 1. 图片
//        String imageFolderName = "D:\\14_LearnEnglish\\6MinuteEnglish\\quark_share" + File.separator;
//        CdFileUtil.copy(Paths.get(imageFolderName), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);

    }

//    String folderPath = CommonUtil.getFullPath(folderName);
//    String distFolderName = "D:\\14_LearnEnglish\\6MinuteEnglish\\quark_share";
//    // 0. 清理文件夹
////    boolean del = CdFileUtil.del(distFolderName);
////    log.info("删除文件夹结果：{}", del);
//
//    // 1. 图片 文件夹拷贝
//    String imageFolderName = folderPath + File.separator + folderName;
//    List<File> files = CdFileUtil.loopFiles(imageFolderName);
//    if (CollectionUtil.isEmpty(files)) {
//      log.error("图片文件夹为空，退出处理流程；{}", imageFolderName);
//      return;
//    }
//    for (File file : files) {
//      if (file.isFile()) {
//        String fileName = file.getName();
//        CdFileUtil.copyFile(file.getAbsolutePath(),
//          distFolderName + fileName,
//          StandardCopyOption.REPLACE_EXISTING);
//      }
//    }
//    // 2. 音频
//    String audioFileName = folderPath + File.separator +  "audio5.mp3";
//    String destinationAudioFileName = distFolderName + "audio.mp3";
//    CdFileUtil.copy(Paths.get(audioFileName),
//      Paths.get(destinationAudioFileName), StandardCopyOption.REPLACE_EXISTING);
//
//    // 3. 字幕
//    String subtitleFileNameEng = folderPath + File.separator + "eng.srt";
//    CdFileUtil.copy(subtitleFileNameEng, distFolderName, true);
//    String subtitleFileNameChn = folderPath + File.separator + "chn.srt";
//    CdFileUtil.copy(subtitleFileNameChn, distFolderName, true);

    // 4. 封面
  }
}
