package com.coderdream.util.process.bbc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.SubtitleEntity;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.DictUtil;
import com.coderdream.util.bbc.GenSrtUtil;
import com.coderdream.util.bbc.ProcessScriptUtil;
import com.coderdream.util.process.PreparePublishUtil;
import com.coderdream.util.translate.TranslateUtil;
import com.coderdream.util.bbc.WordCountUtil;
import com.coderdream.util.cd.CdMP3SplitterUtil;
import com.coderdream.util.cd.TextProcessor;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.ppt.GetSixMinutesPpt;
import com.coderdream.util.ppt.PptToImageConverter;
import com.coderdream.util.subtitle.SubtitleUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SixMinutesStepByStep {

  public static void process(String folderName) {
    // 生成 script_dialog.txt
//    String scriptDialogFileName = "script_dialog.txt";

    // Step00: 处理 YYMMDD_script.txt 文件，过滤字符串
    String fileName = folderName + "_script";
    String srcFileName = CommonUtil.getFullPathFileName(folderName, fileName,
      ".txt");
    File file1 = ProcessScriptUtil.processScriptTxt(srcFileName);
    if (file1 != null) {
      log.info("script文件已处理: {}", file1.getAbsolutePath());
    }


    // Step01: 生成 script_dialog.txt 和 voc.txt
    String scriptDialogFileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog", ".txt");

    if (CdFileUtil.isFileEmpty(scriptDialogFileName)) {
      File file = ProcessScriptUtil.genScriptDialogTxt(folderName,
        scriptDialogFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", scriptDialogFileName);
    }

    // Step02: 生成 voc.txt
    String vocFileName = CommonUtil.getFullPathFileName(folderName, "voc",
      ".txt");
    if (CdFileUtil.isFileEmpty(vocFileName)) {
      File file = ProcessScriptUtil.genVocTxt(folderName,
        vocFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", vocFileName);
    }

    // Step03: 生成 script_dialog_cn.txt
    String scriptDialogCnFileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog",
      "_cn.txt");
    if (CdFileUtil.isFileEmpty(scriptDialogCnFileName)) {
      File file = TranslateUtil.genScriptDialogCn(folderName,
        scriptDialogCnFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", scriptDialogCnFileName);
    }

    // Step04: 生成 voc_cn.txt
    String vocCnFileName = CommonUtil.getFullPathFileName(folderName,
      "voc_cn", ".txt");
    if (CdFileUtil.isFileEmpty(vocCnFileName)) {
      File file = DictUtil.genVocCnWithGemini(vocFileName, vocCnFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", vocCnFileName);
    }

    // Step05: 生成 script_dialog_new.txt
    String scriptDialogNewFileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog",
      "_new.txt");
    if (CdFileUtil.isFileEmpty(scriptDialogNewFileName)) {
      File file = GenSrtUtil.genScriptDialogNew(folderName,
        scriptDialogNewFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", scriptDialogNewFileName);
    }

    // Step06: 生成中英双语对话脚本.txt
    String scriptDialogMergeFileName = CommonUtil.getFullPathFileName(
      folderName,
      folderName, "_中英双语对话脚本.txt");
    if (CdFileUtil.isFileEmpty(scriptDialogMergeFileName)) {
      File file = TranslateUtil.mergeScriptContent(scriptDialogFileName,
        scriptDialogCnFileName, scriptDialogMergeFileName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", scriptDialogMergeFileName);
    }

    // Step07: 生成完整词汇表.xlsx
    String fullVocFileName = CommonUtil.getFullPathFileName(folderName,
      folderName, "_完整词汇表.xlsx");

    if (CdFileUtil.isFileEmpty(fullVocFileName)) {
      File file = WordCountUtil.genVocTable(folderName);
      log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
    } else {
      log.info("文件已存在: {}", fullVocFileName);
    }

    String mp3FileName = "";
    String folderPath = CommonUtil.getFullPath(folderName);
    List<String> fileNames = FileUtil.listFileNames(folderPath);

    // 输出文件名
    for (String subFileName : fileNames) {
      // 找到文件夹下一文件夹名开头，且以.mp3结尾的文件
      if (subFileName.startsWith(folderName) && subFileName.endsWith(
        CdConstants.MP3_EXTENSION)) {
        mp3FileName = subFileName.substring(0, subFileName.length() - 4);
      }
    }
    if (StrUtil.isBlank(mp3FileName)) {
      log.error("未找到音频文件，请检查文件名是否正确");
      return;
    }

    // Step 9. 生成字幕文件 eng_raw.srt
    String audioFileName = CommonUtil.getFullPathFileName(folderName,
      mp3FileName,
      CdConstants.MP3_EXTENSION);
    String srtScriptFileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog_new",
      CdConstants.TXT_EXTENSION);
    String srtEngRawFileName = CommonUtil.getFullPathFileName(folderName,
      "eng_raw",
      CdConstants.SRT_EXTENSION);
    String lang = "eng";
    if (CdFileUtil.isFileEmpty(srtEngRawFileName)) {
      SubtitleUtil.genSrtByExecuteCommand(audioFileName,
        srtScriptFileName,
        srtEngRawFileName, lang);
    } else {
      log.info("文件已存在: {}", srtEngRawFileName);
    }

    // Step 10. 生成字幕文件 mp3.txt
    List<String> timeList = new ArrayList<>();
    String result = TextProcessor.processFile(srtEngRawFileName);
    System.out.println(
      Objects.requireNonNullElse(result, "未找到符合条件的字符串。"));
    timeList.add(folderName + "\t" + result);

//        String srcFileNameCn = CommonUtil.getFullPathFileName(num, "mp3", ProcessDramaUtil.TXT_EXTENSION);
    String mp3InfoFileName =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data"
        + File.separatorChar + "bbc"
        + File.separatorChar + "mp3.txt";
    if (CdFileUtil.isFileEmpty(srtEngRawFileName)) {
      // 写中文翻译文本
      File mp3InfoFile = FileUtil.writeLines(timeList, mp3InfoFileName,
        StandardCharsets.UTF_8);
      if (CdFileUtil.isFileEmpty(mp3InfoFileName)) {
        log.info("已生成新的Mp3 Info 文件: {}", mp3InfoFile.getAbsolutePath());
      }
    } else {
      log.info("文件已存在: {}", mp3InfoFileName);
    }

    // 以tab分隔符，得到数组
    String[] split = timeList.get(0).split("\\s+");
    // 如果数组为空或者不为3，则跳过
    if (split.length == 0) {
      log.error("未找到符合条件的字符串。");
      return;
    }

    String mp3FileNameFull = CommonUtil.getFullPathFileName(folderName,
      mp3FileName,
      CdConstants.MP3_EXTENSION);
    String mp3FileNameFullNew = CommonUtil.getFullPathFileName(folderName,
      "audio5",
      CdConstants.MP3_EXTENSION);

    if (CdFileUtil.isFileEmpty(mp3FileNameFullNew)) {
      String startTime = split[1];
      String endTime = split[2];
      // 写入临时文件，用于切割音频
      CdMP3SplitterUtil.splitMP3(mp3FileNameFull, mp3FileNameFullNew, startTime,
        endTime);
    } else {
      log.info("文件已存在: {}", mp3FileNameFullNew);
    }

    // Step08: 生成 script_dialog_new2.txt
    String scriptDialogNew2FileName = CommonUtil.getFullPathFileName(folderName,
      "script_dialog_new2",
      CdConstants.TXT_EXTENSION);

    if (CdFileUtil.isFileEmpty(scriptDialogNew2FileName)) {
      // 读取原文件内容并删除前四行和最后一行
      List<String> lines = new ArrayList<>();
      List<SubtitleEntity> subtitleEntityList = CdFileUtil.readSrtFileContent(srtEngRawFileName);
      if(CollectionUtil.isNotEmpty(subtitleEntityList)) {
        boolean isSubtitle = false;
        for (SubtitleEntity subtitleEntity : subtitleEntityList) {
          if(subtitleEntity.getTimeStr().startsWith(split[1])){
            isSubtitle = true;
          }
          if(isSubtitle){
            lines.add(subtitleEntity.getSubtitle());
          }
        }
      }
      // lines移除最后一项
      lines.remove(lines.size() - 1);

      // 检查文件是否为空
      if (!lines.isEmpty()) {
        // 将修改后的内容写入新文件
        File file = FileUtil.writeLines(lines, scriptDialogNew2FileName,
          StandardCharsets.UTF_8);

        log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
      } else {
        System.out.println(
          scriptDialogNewFileName + " 文件内容少于5行，无需处理。");
        return;
      }
    } else {
      log.info("文件已存在: {}", scriptDialogNew2FileName);
    }

    // TODO
    // 翻译 script_dialog_new2.txt，生成 script_dialog_new2_gemini.txt
//    String scriptDialogNew2GeminiFileName = CdFileUtil.addPostfixToFileName(scriptDialogNew2FileName,"_gemini");
//
//    if (CdFileUtil.isFileEmpty(scriptDialogNew2GeminiFileName) && !CdFileUtil.isFileEmpty(scriptDialogNew2FileName)) {
//      // 读取原文件内容并删除前四行和最后一行
//      List<String> lines = new ArrayList<>();
//      List<SubtitleEntity> subtitleEntityList = CdFileUtil.readSrtFileContent(srtEngRawFileName);
//      if(CollectionUtil.isNotEmpty(subtitleEntityList)) {
//        boolean isSubtitle = false;
//        for (SubtitleEntity subtitleEntity : subtitleEntityList) {
//          if(subtitleEntity.getTimeStr().startsWith(split[1])){
//            isSubtitle = true;
//          }
//          if(isSubtitle){
//            lines.add(subtitleEntity.getSubtitle());
//          }
//        }
//      }
//      // lines移除最后一项
//      lines.remove(lines.size() - 1);
//
//      // 检查文件是否为空
//      if (!lines.isEmpty()) {
//        // 将修改后的内容写入新文件
//        File file = FileUtil.writeLines(lines, scriptDialogNew2FileName,
//          StandardCharsets.UTF_8);
//
//        log.info("文件不存在或为空，已生成新文件: {}", file.getAbsolutePath());
//      } else {
//        System.out.println(
//          scriptDialogNewFileName + " 文件内容少于5行，无需处理。");
//        return;
//      }
//    } else {
//      log.info("文件已存在: {}", scriptDialogNew2GeminiFileName);
//    }

    // 5. 生成字幕文件 eng.srt
    String srtFileName = CommonUtil.getFullPathFileName(folderName, "eng",
      CdConstants.SRT_EXTENSION);
    if (CdFileUtil.isFileEmpty(srtFileName)) {
      SubtitleUtil.genSrtByExecuteCommand(mp3FileNameFullNew,
        scriptDialogNew2FileName,
        srtFileName, lang);
    } else {
      log.info("文件已存在: {}", srtFileName);
    }
    // python -m aeneas.tools.execute_task audio5.mp3 script_dialog_new.txt "task_language=eng|os_task_file_format=srt|is_text_type=plain" eng.srt

    // 6. 生成字幕文件 chn.srt
    String srcFileNameCn = CommonUtil.getFullPathFileName(folderName, "chn",
      ".srt");
    if (CdFileUtil.isFileEmpty(srcFileNameCn)) {
      TranslateUtil.translateEngSrc(folderName);
    } else {
      log.info("chn 文件已存在: {}", srcFileNameCn);
    }

    String excelCoreVocFileName = CommonUtil.getFullPathFileName(folderName,
      folderName, "_核心词汇表.xlsx");
    if (CdFileUtil.isFileEmpty(excelCoreVocFileName)) {
      CoreWordUtil.genCoreWordTable(folderName);
    } else {
      log.info("文件已存在: {}", excelCoreVocFileName);
    }
    String excelAdvancedFileName = CommonUtil.getFullPathFileName(folderName,
      folderName, "_高级词汇表.xlsx");
    if (CdFileUtil.isFileEmpty(excelAdvancedFileName)) {
      AdvancedWordUtil.genAdvancedWordTable(folderName,
        CdConstants.TEMPLATE_FLAG);
    } else {
      log.info("文件已存在: {}", excelAdvancedFileName);
    }

    // 查询章节名称
    String chapterName = GetSixMinutesPpt.queryChapterNameForSixMinutes(
      folderName);

    // 生成pptx文件
    String pptxFileName = CommonUtil.getFullPathFileName(
      folderName, folderName, ".pptx");
    if (CdFileUtil.isFileEmpty(pptxFileName)) {
      GetSixMinutesPpt.process(folderName, chapterName);
    } else {
      log.info("ppt文件已存在: {}", pptxFileName);
    }

    // 生成pptx的图片
    String pptPicDir =
      new File(pptxFileName).getParent() + File.separator + folderName
        + File.separator;
    PptToImageConverter.convertPptToImages(pptxFileName, pptPicDir, "snapshot");
    if (!new File(pptPicDir).exists()) {
      PptToImageConverter.convertPptToImages(pptxFileName, pptPicDir,
        "snapshot");
    } else {
      log.info("ppt图片文件夹已存在: {}", pptPicDir);
    }

    // 生成国内平台描述文件
    String descriptionFileName = CdFileUtil.changeExtension(pptxFileName, "md");
    descriptionFileName = CdFileUtil.addPostfixToFileName(descriptionFileName,
      "_description");
    if (CdFileUtil.isFileEmpty(descriptionFileName)) {
      TranslationUtil.genDescription(scriptDialogMergeFileName,
        descriptionFileName);
    } else {
      log.info("Md 文件已存在: {}", descriptionFileName);
    }

    // 生成油管平台描述文件
    String descriptionFileNameYT = CdFileUtil.changeExtension(pptxFileName,
      "md");
    descriptionFileNameYT = CdFileUtil.addPostfixToFileName(
      descriptionFileNameYT,
      "_description");
    if (CdFileUtil.isFileEmpty(descriptionFileNameYT)) {
      TranslationUtil.genDescription(scriptDialogMergeFileName,
        descriptionFileNameYT);
    } else {
      log.info("Md 文件已存在: {}", descriptionFileNameYT);
    }

    // 2. 生成描述
//    String mdFileName = CommonUtil.getFullPathFileName(folderPath, folderName,
//      ".md");
    String chnMdFileName = CdFileUtil.addPostfixToFileName(
      descriptionFileNameYT, "_chn");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(
      descriptionFileNameYT, "_cht");
    if (CdFileUtil.isFileEmpty(chnMdFileName) || CdFileUtil.isFileEmpty(
      chtMdFileName)) {
      PreparePublishUtil.genDescriptionForYT(folderPath, folderName, "", "",
        "6",
        srtFileName, chapterName);

    }
  }

}
