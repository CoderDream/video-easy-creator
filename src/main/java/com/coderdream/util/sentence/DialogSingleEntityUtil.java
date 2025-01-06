package com.coderdream.util.sentence;

import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.util.CdFileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DialogSingleEntityUtil {

  public static void genPart1File(String folderPath, String subFolder,
    String fileName) {
    //   String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
    List<DialogSingleEntity> dialogSingleEntities = parsePart1(
      folderPath + fileName);
    List<String> enSentenceList = new ArrayList<>();
    List<String> cnSentenceList = new ArrayList<>();
    dialogSingleEntities.forEach(dialogSingleEntity -> {
      parseDialogSingleEntity2DialogSingleEntityList(
        dialogSingleEntity).forEach(
        dialogSingleEntity1 -> {
          enSentenceList.add(dialogSingleEntity1.getContentEn());
          cnSentenceList.add(dialogSingleEntity1.getContentCn());
        });
      ;
    });

    String fileNamePart1En =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileName,
        "_part1_en");
    String fileNamePart1Cn =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileName,
        "_part1_cn");
    CdFileUtil.writeToFile(fileNamePart1En, enSentenceList);
    CdFileUtil.writeToFile(fileNamePart1Cn, cnSentenceList);
  }

  public static void genPart2File(String folderPath, String subFolder,
    String fileName) {
    //   String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
    List<String> enSentenceList = new ArrayList<>();
    List<String> cnSentenceList = new ArrayList<>();
    List<List<DialogSingleEntity>> lists = parsePart2(folderPath + fileName);
    lists.forEach(dialogSingleEntities2 -> {
      dialogSingleEntities2.forEach(dialogSingleEntity2 -> {
        Objects.requireNonNull(
            parseDialogSingleEntity2DialogSingleEntityList(dialogSingleEntity2))
          .forEach(
            dialogSingleEntity1 -> {
              enSentenceList.add(dialogSingleEntity1.getContentEn());
              cnSentenceList.add(dialogSingleEntity1.getContentCn());
            });
        ;
      });
    });

    String fileNamePart2En =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileName,
        "_part2_en");
    String fileNamePart2Cn =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileName,
        "_part2_cn");
    CdFileUtil.writeToFile(fileNamePart2En, enSentenceList);
    CdFileUtil.writeToFile(fileNamePart2Cn, cnSentenceList);
  }

  public static void genTotalFile(String folderPath, String subFolder,
    String fileNamePart1, String fileNamePart2) {
    //   String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
    List<String> enSentenceList = new ArrayList<>();
    List<String> cnSentenceList = new ArrayList<>();
    List<String> totalSentenceList = new ArrayList<>();

    List<DialogSingleEntity> dialogSingleEntities = parsePart1(
      folderPath + fileNamePart1);
    dialogSingleEntities.forEach(dialogSingleEntity -> {
      parseDialogSingleEntity2DialogSingleEntityList(
        dialogSingleEntity).forEach(
        dialogSingleEntity1 -> {
          enSentenceList.add(dialogSingleEntity1.getContentEn());
          cnSentenceList.add(dialogSingleEntity1.getContentCn());
        });
      ;
    });

    parsePart2(folderPath + fileNamePart2).forEach(dialogSingleEntities2 -> {
      dialogSingleEntities2.forEach(dialogSingleEntity2 -> {
        Objects.requireNonNull(
            parseDialogSingleEntity2DialogSingleEntityList(dialogSingleEntity2))
          .forEach(
            dialogSingleEntity1 -> {
              enSentenceList.add(dialogSingleEntity1.getContentEn());
              cnSentenceList.add(dialogSingleEntity1.getContentCn());
            });
        ;
      });
    });

    totalSentenceList.addAll(enSentenceList);
    totalSentenceList.addAll(cnSentenceList);

    String fileNamePart11 =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileNamePart1,
        "_part1");
    String fileNamePart21 =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileNamePart1,
        "_part2");
    String fileNameTotal =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        fileNamePart1,
        "total");

    CdFileUtil.writeToFile(fileNamePart11, enSentenceList);
    CdFileUtil.writeToFile(fileNamePart21, cnSentenceList);
    CdFileUtil.writeToFile(fileNameTotal, totalSentenceList);
  }


  public static List<DialogSingleEntity> parsePart1(String fileName) {
//    String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
    List<DialogSingleEntity> dialogList = DialogParser.parseDialogFile(
      fileName);
    if (dialogList.size() > 0) {
      dialogList.forEach(dialog -> log.info("{}", dialog));
    } else {
      log.warn("解析后的列表为空");
    }

    return dialogList;
  }

  public static List<List<DialogSingleEntity>> parsePart2(String fileName) {
    //String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0202.txt";
    List<List<DialogSingleEntity>> dialogList = TextParser.parseTextFile(
      fileName);
    if (dialogList != null) {
      dialogList.forEach(dialogSingleEntities -> {
        dialogSingleEntities.forEach(System.out::println);
      });
    }

    return dialogList;
  }

  public static List<DialogSingleEntity> parseDialogSingleEntity2DialogSingleEntityList(
    DialogSingleEntity dialogSingleEntity) {
    //String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0202.txt";
    List<DialogSingleEntity> dialogList = new ArrayList<>();
    String contentEn = dialogSingleEntity.getContentEn();
    String contentCn = dialogSingleEntity.getContentCn();

    List<String> enSentences = StanfordSentenceSplitter.splitSentences(
      contentEn);
    enSentences.forEach(System.out::println);

    List<String> cnSentences = StanfordSentenceSplitter.splitSentences(
      contentCn);
    cnSentences.forEach(System.out::println);

    if (enSentences.size() != cnSentences.size()) {
      log.warn("英文句子和中文句子的数量不一致");
      return null;
    } else {
      for (int i = 0; i < enSentences.size(); i++) {
        DialogSingleEntity dialogSingleEntity1 = new DialogSingleEntity();
        dialogSingleEntity1.setHostEn(dialogSingleEntity.getHostEn());
        dialogSingleEntity1.setHostCn(dialogSingleEntity.getHostCn());
        dialogSingleEntity1.setContentEn(enSentences.get(i));
        dialogSingleEntity1.setContentCn(cnSentences.get(i));
        dialogList.add(dialogSingleEntity1);
      }
    }

    if (dialogList != null) {
      dialogList.forEach(dialog -> log.info("{}", dialog));
    }

    return dialogList;
  }

  public static void main(String[] args) {
//    // DialogSingleEntity(hostEn=Mr.White, hostCn=怀特先生, contentEn=I think I'll give you a three months' trial. The salary for this period is 800 yuan a month, with no bonus. After that period, if we both feel satisfied, a formal contract will be signed., contentCn=我想我可以给你三个月的试用期。试用期内每月工资是800元，没有奖金。试用期过后，如果我们双方都满意的话，我们就可以签订正式合同了。)
//    DialogSingleEntity dialogSingleEntity = new DialogSingleEntity();
//    dialogSingleEntity.setHostEn("Mr.White");
//    dialogSingleEntity.setHostCn("怀特先生");
//    dialogSingleEntity.setContentEn(
//      "I think I'll give you a three months' trial. The salary for this period is 800 yuan a month, with no bonus. After that period, if we both feel satisfied, a formal contract will be signed.");
//    dialogSingleEntity.setContentCn(
//      "我想我可以给你三个月的试用期。试用期内每月工资是800元，没有奖金。试用期过后，如果我们双方都满意的话，我们就可以签订正式合同了。");
//    List<DialogSingleEntity> dialogSingleEntities = parseDialogSingleEntity2DialogSingleEntityList(
//      dialogSingleEntity);
//    if (dialogSingleEntities.size() > 0) {
//      dialogSingleEntities
//        .forEach(System.out::println);
//    } else {
//      log.warn("解析后的列表为空");
//    }
//    ;

    String folderPath = "D:\\0000\\EnBook001\\商务职场英语口语900句\\";
    String subFolder = "ch01";
    File file = new File(folderPath + subFolder);
    if (!file.exists()) {
      file.mkdirs();
    }

    String fileNamePart1 = "商务职场英语口语900句V1_" + subFolder + "01.txt";
    String fileNamePart2 = "商务职场英语口语900句V1_" + subFolder + "02.txt";
    genPart1File(folderPath, subFolder, fileNamePart1);
    genPart2File(folderPath, subFolder, fileNamePart2);
    genTotalFile(folderPath, subFolder, fileNamePart1, fileNamePart2);
  }

}


