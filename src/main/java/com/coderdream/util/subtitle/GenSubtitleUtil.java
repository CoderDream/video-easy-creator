package com.coderdream.util.subtitle;


import com.coderdream.util.audio.FfmpegUtil2;
import com.coderdream.util.bbc.StringSplitter4;
import com.coderdream.util.bbc.TranslateUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.daily.DailyUtil;
import com.coderdream.util.sentence.demo03.SentenceMerger;
import com.coderdream.util.sentence.demo03.StanfordNLPSentenceSplitter;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenSubtitleUtil {

  public static void process(String filePath, int maxLength) {
    //
    String sentenceFilePath = CdFileUtil.addPostfixToFileName(filePath,
      "_sentence");
    if (CdFileUtil.isFileEmpty(sentenceFilePath)) {
      genRawSrtFile(filePath, sentenceFilePath, maxLength);
    }

    String inputPathMp3 = CdFileUtil.changeExtension(filePath, "mp3");
    if (CdFileUtil.isFileEmpty(inputPathMp3)) {
      // 获取 mp4 文件路径信息
      String inputPathMp4 = CdFileUtil.changeExtension(filePath, "mp4");
      // 提取音频文件
      FfmpegUtil2.extractAudioFromMp4(inputPathMp4, inputPathMp3);
    }

    // 生成英文SRT文件
    String srcFileNameEng = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameEng = CdFileUtil.addPostfixToFileName(srcFileNameEng, ".eng");
    if (CdFileUtil.isFileEmpty(srcFileNameEng)) {
      log.info("srcFileNameEng 文件不存在, {}", srcFileNameEng);
      SubtitleUtil.genSrtByExecuteCommand(inputPathMp3, sentenceFilePath,
        srcFileNameEng, "eng");
    }
    //  生成中文SRT文件
    String srcFileNameChn = CdFileUtil.changeExtension(filePath, "srt");
    srcFileNameChn = CdFileUtil.addPostfixToFileName(srcFileNameChn, ".chn");
    if (CdFileUtil.isFileEmpty(srcFileNameChn)) {
      TranslateUtil.translateSrcWithGemini(srcFileNameEng, srcFileNameChn);
    } else {
      log.info("srcFileNameChn 文件已存在: {}", srcFileNameChn);
    }

    //  生成中文SRT文件 TODO
    String inputPathSrt = CdFileUtil.changeExtension(filePath, "srt");
    if (CdFileUtil.isFileEmpty(inputPathSrt)) {
      SubtitleUtil.mergeSubtitleFile(srcFileNameEng, srcFileNameChn,
        inputPathSrt);
    } else {
      log.info("inputPathSrt 文件已存在: {}", inputPathSrt);
    }

    // 生成Markdown文件
    String mdFileName = Objects.requireNonNull(
      CdFileUtil.changeExtension(filePath, "md"));
    String chnMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_chn");
    String chtMdFileName = CdFileUtil.addPostfixToFileName(mdFileName, "_cht");

    if (CdFileUtil.isFileEmpty(chnMdFileName) || CdFileUtil.isFileEmpty(
      chtMdFileName)) {
      DailyUtil.generateDescription(inputPathSrt, chnMdFileName, chtMdFileName);
    } else {
      log.info("描述文件已存在: {}， {}", chnMdFileName, chtMdFileName);
    }
  }

  /**
   * 生成原始的SRT文件
   *
   * @param filePath         文件路径
   * @param sentenceFilePath 句子文件路径
   * @param maxLength        最大长度
   */
  public static void genRawSrtFile(String filePath, String sentenceFilePath,
    int maxLength) {
    // step 1: read file，生成句子
    List<String> stringLists = CdFileUtil.readFileContent(filePath);

    List<String> sentenceList;
    assert stringLists != null;
    StringBuilder stringBuilder = new StringBuilder();
    for (String string : stringLists) {
      log.info(string);
      // 预处理
      // 1.先去掉双引号 ” “ (Laughs.)  (Laughter.)
      string = string.replace("\"", "");
      string = string.replace("”", "");
      string = string.replace("“", "");
      string = string.replace("(Laughs.)", "");
      string = string.replace("(Laughter.)", "");
      stringBuilder.append(string).append(" ");
    }

    // 1.1 使用JDK自带的方法
//    sentenceList= SentenceSplitterWithJDK.splitIntoSentences(stringBuilder.toString());

    // 1.2 使用StanfordCoreNLP
    sentenceList = StanfordNLPSentenceSplitter.splitIntoSentences(
      stringBuilder.toString());

    // 1.3 处理长句子 SentenceSplitterWithChar.processLongSentence(text, ";", ":")
//    sentenceList = sentenceList.stream()
//      .map(sentence -> SentenceSplitterWithChar03.processLongSentence(sentence,
//        ";", ":"))
//      .flatMap(List::stream)
//      .toList();

    // 分割句子
    sentenceList = StringSplitter4.splitStringList(sentenceList, maxLength);

    // 合并句子
    sentenceList = SentenceMerger.mergeSentences(sentenceList, maxLength);

    // 移除空行
    sentenceList = sentenceList.stream().filter(s -> !s.isEmpty()).toList();

    CdFileUtil.writeToFile(sentenceFilePath, sentenceList);
  }

}
