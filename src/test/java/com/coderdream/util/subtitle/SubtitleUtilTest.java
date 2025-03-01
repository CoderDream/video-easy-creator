package com.coderdream.util.subtitle;


import com.coderdream.entity.SubtitleEntity;
import com.coderdream.util.bbc.TranslateUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class SubtitleUtilTest {

  @Test
  void genSubtitle() {
  }

  @Test
  void genSubtitleRaw() {
  }

  @Test
  void genSrtByExecuteCommand() {
    String fileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics.txt";
    String srtRawFileName = CdFileUtil.addPostfixToFileName(fileName, "_raw");
    // 生成字幕文件，调用 python 命令
    File file = new File(srtRawFileName);
    String path = file.getParent();
    // D:\0000\EnBook001\900\ch01\dialog_single_with_phonetics.txt
    // D:\0000\EnBook001\900\ch01\dialog_single_with_phonetics\audio\ch01_mix.wav
    String audioFileName =
      path + File.separator
        + CdFileUtil.getPureFileNameWithoutExtensionWithPath(fileName)
        + File.separator + "audio\\" + "ch01_mix.wav";
    File audioFile = new File(audioFileName);
    if (!audioFile.exists()) {
      log.error("音频文件不存在:{}", audioFileName);
      return;
    }

    String srtFileName = CdFileUtil.changeExtension(audioFileName, "srt");

    String srtFileNameEng = CdFileUtil.addPostfixToFileName(srtFileName, ".eng");
    String lang = "eng"; //  String lang = "cmn";
    log.info("srtFileNameEng:{}", srtFileNameEng);
    SubtitleUtil.genSrtByExecuteCommand(audioFileName, srtRawFileName, srtFileNameEng, lang);

    String srtFileNameChn = CdFileUtil.addPostfixToFileName(srtFileName, ".chn");
    lang = "cmn"; //  String lang = "cmn";
    log.info("srtFileNameChn:{}", srtFileNameChn);
    SubtitleUtil.genSrtByExecuteCommand(audioFileName, srtRawFileName, srtFileNameChn, lang);
  }


  @Test
  void genSrtByExecuteCommand_02() {
    String folderName = "D:\\0000\\【中英雙語】2025川普就職演講\\";
    String srtRawFileName = "D:\\0000\\【中英雙語】2025川普就職演講\\【中英雙語】2025川普就職演講.txt";
//    String srtFileNameEng = CdFileUtil.changeExtension(srtRawFileName, "srt");

    // D:\0000\EnBook001\900\ch01\dialog_single_with_phonetics.txt
    // D:\0000\EnBook001\900\ch01\dialog_single_with_phonetics\audio\ch01_mix.wav
    String audioFileName =
      CdFileUtil.changeExtension(srtRawFileName, "mp3");
    File audioFile = new File(audioFileName);
    if (!audioFile.exists()) {
      log.error("音频文件不存在:{}", audioFileName);
      return;
    }

    String srtFileName = CdFileUtil.changeExtension(srtRawFileName, "srt");

    String srtFileNameEng = CdFileUtil.addPostfixToFileName(srtFileName, ".eng");
    String lang = "eng"; //  String lang = "cmn";
    log.info("srtFileNameEng:{}", srtFileNameEng);
    if (CdFileUtil.isFileEmpty(srtFileNameEng)) {
      SubtitleUtil.genSrtByExecuteCommand(audioFileName, srtRawFileName, srtFileNameEng, lang);
    } else {
      log.info("文件已存在: {}", srtFileNameEng);
    }

    String srtFileNameChn = CdFileUtil.addPostfixToFileName(srtFileName, ".chn");
    if (CdFileUtil.isFileEmpty(srtFileNameChn)) {
      TranslateUtil.translateEngSrc(folderName, CdFileUtil.getPureFileNameWithoutExtensionWithPath(srtRawFileName));
    } else {
      log.info("文件已存在: {}", srtFileNameChn);
    }

  }

  @Test
  void genMultiSubtitle() {
    String fileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics.txt";
    String srtFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.srt";
    SubtitleUtil.genMultiSubtitle(fileName, srtFileName);
    File file = new File(srtFileName);
    Assertions.assertTrue(file.exists());
    List<SubtitleEntity> subtitleList = SubtitleParser.parseSubtitleFile(srtFileName);
    Assertions.assertNotNull(subtitleList);
    Assertions.assertNotEquals(0, subtitleList.size());
  }

  @Test
  void writeToSubtitleFile() {
  }

  @Test
  void modifySubtitleFile_01() {
    String folderName = "20250227"; // D:\0000\0007_Trump\20250227
    String filePath =
      OperatingSystem.getBaseFolder() + "0007_Trump" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    String srcFileNameEn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEn = CdFileUtil.addPostfixToFileName(srcFileNameEn,
      "." + CdConstants.SUBTITLE_EN);
    SubtitleUtil.modifySubtitleFile(srcFileNameEn);
  }

  @Test
  void modifySubtitleFile_02() {
    String folderName = "20250227"; // D:\0000\0007_Trump\20250227
    String filePath =
      OperatingSystem.getBaseFolder() + "0007_Trump" + File.separator
        + folderName + File.separator + folderName
        + ".mp4";
    String srcFileNameZhTw = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameZhTw = CdFileUtil.addPostfixToFileName(srcFileNameZhTw,
      "." + CdConstants.SUBTITLE_ZH_TW);
    SubtitleUtil.modifySubtitleFile(srcFileNameZhTw);
  }

}
