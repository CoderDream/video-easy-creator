package com.coderdream.util.subtitle;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.DurationEntity;
import com.coderdream.entity.SubtitleEntity;
import com.coderdream.util.CdConstants;
import com.coderdream.util.chatgpt.TextParserUtil;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceDurationVO;
import com.coderdream.vo.SentenceVO;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubtitleUtil {

  /**
   * 生成带有文本的图片。
   *
   * @param fileName            文件名称
   * @return 图片文件列表
   */
  public static List<SentenceDurationVO> genSubtitle(String fileName) {
    String fullPath =
      CdConstants.RESOURCES_BASE_PATH + File.separator + fileName + ".txt";
//    fullPath = fileName + ".txt";

    List<SubtitleEntity> subtitleEntities = new ArrayList<>();
    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(
      fullPath);

    String durationFileName =
      BatchCreateVideoCommonUtil.getAudioPath(fileName) + fileName
        + "_duration.txt";
    File durationFile = new File(durationFileName);
//    durationFile.readAllLines();
//    Files.readAllLines(Paths.get(durationFile));

    Map<String, DurationEntity> durationEntityMap = new LinkedHashMap<>();

    // 使用 Hutool 的 readLines 方法读取文件所有行
    List<String> lines = FileUtil.readLines(durationFile, "UTF-8");
    for (String line : lines) {

      DurationEntity durationEntity = new DurationEntity();
      // 解析每行数据，并将其存储到 durationEntityMap 中
      int lastIndex = line.lastIndexOf("." + CdConstants.AUDIO_TYPE_WAV);
      int index = line.lastIndexOf(fileName);
      String line2 = line.substring(index + fileName.length() + 1, lastIndex);
      log.info("line2:{}", line2);
      String[] split = line2.split("_");

      if (split.length == 2) {
        durationEntity.setIndexStr(split[0]);
        durationEntity.setLang(split[1]);
      } else {
        log.error("未找到:{}", line2);
      }

      String[] splitDuration = line.split("\t");
      if (splitDuration.length == 2) {
        String duration = splitDuration[1];
        durationEntity.setDuration(Double.parseDouble(duration.trim()));
      } else {
        durationEntity.setDuration(0);
        log.error("未找到:{}", line);
      }

//      durationEntity.setDuration(line);
      durationEntityMap.put(line2, durationEntity);
    }

    SubtitleEntity subtitleEntity = null;


    List<SentenceDurationVO> sentenceDurationVOs = new ArrayList<>();
    SentenceDurationVO sentenceDurationVO = null;


    // 使用DecimalFormat来确保保留3位小数
    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    // 3
    //00:00:04,000 --> 00:00:06,000
    int number = 0;
    for (SentenceVO sentenceVO : sentenceVOs) {
      number++;
      // 创建字幕实体
      sentenceDurationVO = new SentenceDurationVO();
      sentenceDurationVO.setId(number);
      String indexStr = MessageFormat.format(
        "{0,number,000}",
        number);
      subtitleEntity = new SubtitleEntity();
      subtitleEntity.setSubIndex(1);
      subtitleEntity.setTimeStr("00:00");
      subtitleEntity.setSubtitleSecond(sentenceVO.getEnglish());
      subtitleEntity.setSubtitle(sentenceVO.getChinese());
      subtitleEntities.add(subtitleEntity);
//      log.info("subtitleEntity:{}", subtitleEntity);
      String lang = CdConstants.LANG_CN;
      String key = indexStr + "_" + lang;
      String str = sentenceVO.getChinese() + "\t" + durationEntityMap.get(key)
        .getDuration();
      log.info("strCn:{}", str);

      sentenceDurationVO.setChinese(sentenceVO.getChinese());
      double chineseDuration = durationEntityMap.get(key).getDuration();
      sentenceDurationVO.setChineseDuration(chineseDuration);
      sentenceDurationVO.setPhonetics(sentenceVO.getPhonetics());
      // 英文
      lang = CdConstants.LANG_EN;
      key = indexStr + "_" + lang;
      str = sentenceVO.getEnglish() + "\t" + durationEntityMap.get(key)
        .getDuration();
      log.info("strEn:{}", str);
      sentenceDurationVO.setEnglish(sentenceVO.getEnglish());
      double englishDuration = durationEntityMap.get(key).getDuration();

      sentenceDurationVO.setEnglishDuration(englishDuration);

      // 计算时长

      // 计算总时长: 总时长 = 4 * 英文时长 + 中文时长
      double totalDuration = 4 * englishDuration + chineseDuration;


      // 格式化总时长为保留3位小数
      totalDuration = Double.parseDouble(decimalFormat.format(totalDuration));

      sentenceDurationVO.setTotalDuration(totalDuration);
      sentenceDurationVOs.add(sentenceDurationVO);
    }

    // 遍历 sentenceDurationVOs
    for (SentenceDurationVO sentenceDurationVO1 : sentenceDurationVOs) {
      log.info("sentenceDurationVO:{}", sentenceDurationVO1);
    }



    // 设置路径
//    String outputDir = BatchCreateVideoCommonUtil.getPicPath(
//      fileName);//  "src/main/resources/pic"; // 输出目录

    return sentenceDurationVOs;
  }

  public static void main(String[] args) {
    genSubtitle("CampingInvitation_cht_03");
  }


}
