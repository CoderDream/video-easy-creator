package com.coderdream.util.gemini;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TranslationUtilTest {


  @Test
  void translate() {
    String text = "翻译成简体中文：" + "hello";
    String translate = TranslationUtil.translate(text);
    log.info("translate: {}", translate);
  }

  @Test
  void translate_0202() {
    String text =
      "翻译成简体中文：" + "Why are countryside walks no longer so popular?";
    String translate = TranslationUtil.translate(text);
    log.info("translate: {}", translate);
  }

  //

  @Test
  void translate_02() {
    String text = "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：cephalopod\nthe group of animals to which the octopus belongs\npublicity stunt\nsomething a company might do to grab your attention and promote its products\na common ancestor\na distant relative from which two different species evolved\ncomparable to\nsimilar to\nvertebrates\nanimals that have a spine\nprotean\n(adjective) adaptable and changeable";
    String translate = TranslationUtil.translate(text);
    log.info("translate: {}", translate);
  }

  @Test
  void genPhonetics_03() {
    String fileName = "D:\\0000\\EnBook001\\900\\dialog_single.txt";
//    TranslationUtil.genPhonetics(fileName);
//    log.info("translate: {}", translate);
  }

  //

  @Test
  void genPhonetics_04() {
    String fileName = "D:\\0000\\EnBook001\\900\\ch01\\900V1_ch0101_total.txt";
//    TranslationUtil.genPhonetics(fileName);
//    log.info("translate: {}", translate);
  }


  @Test
  void genPhonetics_05() {
    String fileName = "D:\\0000\\EnBook001\\900\\ch01\\900V1_ch0101_total.txt";
    String newFileName = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics_raw.txt";
    TranslationUtil.genPhonetics(fileName, newFileName);
//    log.info("translate: {}", translate);
  }


  @Test
  void genDescription() {
    String folderName = "250206"; // 250102
//    String folderPath = CommonUtil.getFullPath(folderName);
//    String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
    TranslationUtil.genDescription(folderName);
//    log.info("translate: {}", translate);
  }

  @Test
  void genDescription_02() {
//    String folderName = "180913"; // 250102
//    List<String> folderNames = List.of("180906", "180920");
    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
    List<String> folderNameList = FileUtil.readLines(todoFileName, "UTF-8");
    for (String folderName : folderNameList) {
      TranslationUtil.genDescription(folderName);
    }

//    String folderPath = CommonUtil.getFullPath(folderName);
//    String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
//    TranslationUtil.genDescription(fileName);
//    log.info("translate: {}", translate);
  }

  // File genAiFile(String fileName)

  @Test
  void genAiFile_01() {
//    String folderName = "180913"; // 250102
    String fileName = "D:\\0000\\EnBook002\\Chapter005\\Chapter005_total.txt";
    File file = TranslationUtil.genAiFile(fileName);
  }

  @Test
  void genAiFile_02() {
//    String folderName = "180913"; // 250102
    String folderName = "Chapter011";
    String fileName =
      "D:\\0000\\EnBook002\\" + folderName + "\\" + folderName + "_total.txt";
    File file = TranslationUtil.genAiFile(fileName);
  }


  @Test
  void genReadBookScript_01() {
//    String folderName = "180913"; // 250102
//    List<String> folderNames = List.of("180906", "180920");
//    String todoFileName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\data\\bbc\\todo.txt";
//    List<String> folderNameList = CdFileUtil.readLines(todoFileName, "UTF-8");
//    for (String folderName : folderNameList) {
//      TranslationUtil.genReadBookScript(folderName);
//    }
    String folderName = "ReadBook_0003";
    String bookFileName = "高难度沟通";
    String bookFileNameWithPath =
      OperatingSystem.getBaseFolder() + "ReadBook" + File.separator + folderName
        + File.separator + bookFileName + ".txt";
    DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
    String readBookScriptFileName =
      OperatingSystem.getBaseFolder() + "ReadBook" + File.separator + folderName
        + File.separator + folderName + "_" + dateFormat.format(new Date())
        + ".txt";

//    String folderPath = CommonUtil.getFullPath(folderName);
//    String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
    TranslationUtil.genReadBookScript(folderName, bookFileNameWithPath,
      readBookScriptFileName);
//    log.info("translate: {}", translate);
  }
}
