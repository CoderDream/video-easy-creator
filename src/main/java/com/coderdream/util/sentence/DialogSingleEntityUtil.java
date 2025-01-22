package com.coderdream.util.sentence;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.DialogSingleEntityError;
import com.coderdream.entity.DialogSingleEntityResponse;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.vo.SentenceVO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class DialogSingleEntityUtil {

//  /**
//   * @param folderPath 文件夹路径，根路径，如： D:\\0000\\EnBook001\\900\\
//   * @param subFolder  章节名称，如：ch001、ch002
//   */
//  public static void genPart1File(String folderPath, String subFolder) {
//    // 构造part1文件名
//    String part1FileName = subFolder + "01.txt";
//    // 构造part1文件路径
//    String part1FilePath =
//      folderPath + subFolder + File.separator + "input" + File.separator
//        + part1FileName;
//    //   String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0201.txt";
//    List<DialogSingleEntity> dialogSingleEntities = parsePart1(part1FilePath);
//    List<String> enSentenceList = new ArrayList<>();
//    List<String> cnSentenceList = new ArrayList<>();
//    dialogSingleEntities.forEach(dialogSingleEntity -> {
//      Objects.requireNonNull(parseDialogSingleEntity2DialogSingleEntityList(
//        dialogSingleEntity)).forEach(
//        dialogSingleEntity1 -> {
//          enSentenceList.add(dialogSingleEntity1.getContentEn());
//          cnSentenceList.add(dialogSingleEntity1.getContentCn());
//        });
//    });
//
//    String fileNamePart1En =
//      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
//        part1FileName,
//        "_part1_en");
//    String fileNamePart1Cn =
//      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
//        part1FileName,
//        "_part1_cn");
//    CdFileUtil.writeToFile(fileNamePart1En, enSentenceList);
//    CdFileUtil.writeToFile(fileNamePart1Cn, cnSentenceList);
//  }
//
//  public static void genPart2File(String folderPath, String subFolder) {
//    // 构造part2文件名
//    String part2FileName = subFolder + "02.txt";
//    // 构造part2文件路径
//    String part2FilePath =
//      folderPath + subFolder + File.separator + "input" + File.separator
//        + part2FileName;
//    //   String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0201.txt";
//    List<String> enSentenceList = new ArrayList<>();
//    List<String> cnSentenceList = new ArrayList<>();
//    List<List<DialogSingleEntity>> lists = parsePart2(part2FilePath);
//    lists.forEach(dialogSingleEntities2 -> {
//      dialogSingleEntities2.forEach(dialogSingleEntity2 -> {
//        Objects.requireNonNull(
//            parseDialogSingleEntity2DialogSingleEntityList(dialogSingleEntity2))
//          .forEach(
//            dialogSingleEntity1 -> {
//              enSentenceList.add(dialogSingleEntity1.getContentEn());
//              cnSentenceList.add(dialogSingleEntity1.getContentCn());
//            });
//        ;
//      });
//    });
//
//    String fileNamePart2En =
//      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
//        part2FileName,
//        "_part1_en");
//    String fileNamePart2Cn =
//      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
//        part2FileName,
//        "_part1_cn");
//    CdFileUtil.writeToFile(fileNamePart2En, enSentenceList);
//    CdFileUtil.writeToFile(fileNamePart2Cn, cnSentenceList);
//  }

  public static boolean genPart1AndPart2File(String folderPath,
    String subFolder) {
    // 构造文件名
    String fileName = subFolder + ".txt";
    // 构造文件路径
    String filePath =
      folderPath + subFolder + File.separator + "input" + File.separator
        + fileName;
    // 构造part1文件名
    String part1FileName = subFolder + "01.txt";
    // 构造part1文件路径
    String part1FilePath =
      folderPath + subFolder + File.separator + "input" + File.separator
        + part1FileName;
    // 构造part2文件名
    String part2FileName = subFolder + "02.txt";
    // 构造part2文件路径
    String part2FilePath =
      folderPath + subFolder + File.separator + "input" + File.separator
        + part2FileName;
    List<String> contentList = FileUtil.readLines(filePath, "UTF-8");
    // 找到列表的第一个空行作为分割点，将列表分为part1和part2两个部分
    int index = contentList.indexOf("");
    List<String> part1List = contentList.subList(0, index);
    // 校验1：是否为偶数行
    // 校验2：是否存在不含中英文冒号的行或者有多个冒号，则返回false并提示是哪一行
    // 解析为 ScriptSingleEntity 列表
    List<DialogSingleEntity> result1 = ScriptParser.parseScript(part1List);

    List<String> part2List = contentList.subList(index + 1, contentList.size());

    List<List<DialogSingleEntity>> result2 = TextParser.parseTextFile(
      part2List);

    System.out.println(part1List.size());
    // 以空行为分割part2List列表，打印子列表的大小
    List<List<String>> subLists = new ArrayList<>();
    for (int i = 0; i < part2List.size(); ) {
      int j = i;
      while (j < part2List.size() && !part2List.get(j).isEmpty()) {
        j++;
      }
//      part2List.subList(i, j);

      subLists.add(part2List.subList(i, j));
      i = j + 1;
    }
//    // 打印子列表的大小，如果存在奇数大小，则返回false并提示是哪个子列表
//    if (subLists.size() % 2 != 0) {
//      System.out.println("存在奇数大小的子列表：" + subLists.get(subLists.size() - 1));
//      return;
//    } else {
//      System.out.println("所有子列表大小都为偶数");
//    }

    for (int i = 0; i < subLists.size(); i++) {
      if (subLists.get(i).size() % 2 != 0) {
        log.error("存在奇数大小的子列表：第 {} 个部分，大小为：{} ", i,
          subLists.get(i).size());
        return false;
      }
    }

    boolean parsePart1CheckResult = checkSentencePair(result1);
//    List<List<DialogSingleEntity>> lists = parsePart2(part2FilePath);
//    List<DialogSingleEntity> dialogSingleEntities =new ArrayList<>();
//    lists.forEach(dialogSingleEntities::addAll);
    boolean parsePart2CheckResult = true;
    for (List<DialogSingleEntity> list2 : result2) {
      parsePart2CheckResult = parsePart2CheckResult && checkSentencePair(list2);
    }

//    subLists.forEach(list -> System.out.println("子列表大小：" + list.size()));

    if (!parsePart1CheckResult || !parsePart2CheckResult) {
      return false;
    }
    CdFileUtil.writeToFile(part1FilePath, part1List);
    CdFileUtil.writeToFile(part2FilePath, part2List);

    return true;
  }


  public static File genTotalFile(String folderPath, String subFolder) {

    // 构造part1文件名
    String part1FileName = subFolder + "01.txt";
    // 构造part1文件路径
    String part1FilePath =
      folderPath + subFolder + File.separator + "input" + File.separator
        + part1FileName;

    // 构造part2文件名
    String part2FileName = subFolder + "02.txt";
    // 构造part2文件路径
    String part2FilePath =
      folderPath + subFolder + File.separator + "input" + File.separator
        + part2FileName;

    List<String> enSentenceList = new ArrayList<>();
    List<String> cnSentenceList = new ArrayList<>();
    List<String> totalSentenceList = new ArrayList<>();
    List<SentenceVO> sentenceVOList = new ArrayList<>();
    List<DialogSingleEntity> dialogSingleEntities = parsePart1(
      part1FilePath);
    gen(enSentenceList, cnSentenceList, totalSentenceList, sentenceVOList,
      dialogSingleEntities);

    List<List<DialogSingleEntity>> lists = parsePart2(part2FilePath);

    if (CollectionUtil.isNotEmpty(lists)) {
      lists.forEach(
        dialogSingleEntities2 -> {
          gen(enSentenceList, cnSentenceList, totalSentenceList, sentenceVOList,
            dialogSingleEntities2);
        });
    } else {
      log.error("parsePart2 解析后的列表为空");
    }

    String fileNameTotal =
      folderPath + subFolder + File.separator + CdFileUtil.addPostfixToFileName(
        subFolder, "_total" + ".txt");

    CdFileUtil.writeToFile(fileNameTotal, totalSentenceList);

    return new File(fileNameTotal);
  }

  private static void gen(List<String> enSentenceList,
    List<String> cnSentenceList, List<String> totalSentenceList,
    List<SentenceVO> sentenceVOList,
    List<DialogSingleEntity> dialogSingleEntities) {
    if (CollectionUtil.isEmpty(dialogSingleEntities)) {
      log.warn("parsePart1 解析后的列表为空");
      return;
    }

    for (DialogSingleEntity dialogSingleEntity : dialogSingleEntities) {
      DialogSingleEntityResponse dialogSingleEntityResponse = parseDialogSingleEntity2DialogSingleEntityList(
        dialogSingleEntity);
      List<DialogSingleEntityError> dialogSingleEntityErrorList = dialogSingleEntityResponse.getDialogSingleEntityErrorList();
      if (CollectionUtil.isNotEmpty(dialogSingleEntityErrorList)) {
        log.error("有问题的句子如下2：");
        for (DialogSingleEntityError dialogSingleEntityError : dialogSingleEntityErrorList) {
          log.error("存在问题的句子2：{}", dialogSingleEntityError);
        }
        return;
      }
      List<DialogSingleEntity> dialogSingleEntityList = dialogSingleEntityResponse.getDialogSingleEntityList();
      if (CollectionUtil.isNotEmpty(dialogSingleEntityList)) {
        for (DialogSingleEntity dialogSingleEntity1 : dialogSingleEntityList) {
          enSentenceList.add(dialogSingleEntity1.getContentEn());
          cnSentenceList.add(dialogSingleEntity1.getContentCn());
          totalSentenceList.add(dialogSingleEntity1.getContentEn());
          totalSentenceList.add(dialogSingleEntity1.getContentCn());
          SentenceVO sentenceVO = new SentenceVO();
          sentenceVO.setEnglish(dialogSingleEntity1.getContentEn());
          sentenceVO.setChinese(dialogSingleEntity1.getContentCn());
          sentenceVOList.add(sentenceVO);
        }
      }
    }
  }

  private static boolean checkSentencePair(
    List<DialogSingleEntity> dialogSingleEntities) {
    if (CollectionUtil.isEmpty(dialogSingleEntities)) {
      log.warn("parsePart1 解析后的列表为空");
      return false;
    }
    boolean parsePart1CheckResult = true;
    for (DialogSingleEntity dialogSingleEntity : dialogSingleEntities) {
      DialogSingleEntityResponse dialogSingleEntityResponse = parseDialogSingleEntity2DialogSingleEntityList(
        dialogSingleEntity);
      List<DialogSingleEntityError> dialogSingleEntityErrorList = dialogSingleEntityResponse.getDialogSingleEntityErrorList();
      if (CollectionUtil.isNotEmpty(dialogSingleEntityErrorList)) {
        log.error("有问题的句子如下1：");
        for (DialogSingleEntityError dialogSingleEntityError : dialogSingleEntityErrorList) {
          log.error("存在问题的句子1：{}", dialogSingleEntityError);
          parsePart1CheckResult = false;
        }
      }
    }
    return parsePart1CheckResult;
  }

  public static List<DialogSingleEntity> parsePart1List(List<String> fileName) {
//    String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0201.txt";
    // D:\0000\EnBook001\900\ch002\ch00201.txt
    // D:\0000\EnBook001\900\900V1_ch00201.txt
    log.info("开始解析 parsePart1 文件:{}", fileName);
    List<DialogSingleEntity> dialogList = null;// DialogParser.parseDialogFile(      fileName);
    if (dialogList.size() > 0) {
      dialogList.forEach(dialog -> log.info("{}", dialog));
    } else {
      log.warn("解析后的列表为空");
    }

    return dialogList;
  }

  public static List<DialogSingleEntity> parsePart1(String fileName) {
//    String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0201.txt";
    // D:\0000\EnBook001\900\ch002\ch00201.txt
    // D:\0000\EnBook001\900\900V1_ch00201.txt
    log.info("开始解析 parsePart1 文件:{}", fileName);
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
    //String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0202.txt";

    log.info("开始解析 parsePart2 文件:{}", fileName);
    List<List<DialogSingleEntity>> dialogList = TextParser.parseTextFile(
      fileName);
    if (dialogList != null) {
      dialogList.forEach(dialogSingleEntities -> {
        dialogSingleEntities.forEach(System.out::println);
      });
    }

    return dialogList;
  }

  public static DialogSingleEntityResponse parseDialogSingleEntity2DialogSingleEntityList(
    DialogSingleEntity dialogSingleEntity) {
    DialogSingleEntityResponse dialogSingleEntityResponse = new DialogSingleEntityResponse();
    List<DialogSingleEntityError> dialogSingleEntityErrorList = new ArrayList<>();
    DialogSingleEntityError dialogSingleEntityError = new DialogSingleEntityError();
    //String fileName = "D:\\0000\\EnBook001\\900\\900V1_ch0202.txt";
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
      log.warn("英文句子和中文句子的数量不一致, {}, {}", enSentences.size(),
        cnSentences.size());
      dialogSingleEntityError = new DialogSingleEntityError();
      dialogSingleEntityError.setEnSentences(enSentences);
      dialogSingleEntityError.setCnSentences(cnSentences);
      dialogSingleEntityError.setErrorMessage(
        "英文句子和中文句子的数量不一致，英文句子为：" + enSentences.size()
          + "，中文句子为：" + cnSentences.size() + "。");
      dialogSingleEntityErrorList.add(dialogSingleEntityError);
      // return null;
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

    if (CollectionUtil.isNotEmpty(dialogList)) {
      dialogList.forEach(dialog -> log.info("{}", dialog));
    }

    dialogSingleEntityResponse.setDialogSingleEntityList(dialogList);
    dialogSingleEntityResponse.setDialogSingleEntityErrorList(
      dialogSingleEntityErrorList);
    return dialogSingleEntityResponse;
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

//    String folderPath = "D:\\0000\\EnBook001\\900\\";
//    String subFolder = "ch01";
//    File file = new File(folderPath + subFolder);
//    if (!file.exists()) {
//      file.mkdirs();
//    }
//
//    String fileNamePart1 = "900V1_" + subFolder + "01.txt";
//    String fileNamePart2 = "900V1_" + subFolder + "02.txt";
//    DialogSingleEntityUtil.genPart1File(folderPath, subFolder, fileNamePart1);
//    DialogSingleEntityUtil.genPart2File(folderPath, subFolder, fileNamePart2);
//    DialogSingleEntityUtil.genTotalFile(folderPath, subFolder, fileNamePart1,
//      fileNamePart2);
  }

}


