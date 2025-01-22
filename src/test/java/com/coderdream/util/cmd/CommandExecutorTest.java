package com.coderdream.util.cmd;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommandExecutorTest {

  @Test
  void executeCommandDeployHexo() {
    //cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g
    //cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d
    List<String> commandList = Arrays.asList(
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g",
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }
  }

  // String mp3FileName, String subtitleFileName,
  //        String srtFileName) {
  //        try {
  //            String pythonCommand = "python -m aeneas.tools.execute_task " + mp3FileName + " " + subtitleFileName
  //                + " \"task_language=eng|os_task_file_format=srt|is_text_type=plain\" "
  //                + srtFileName;
  @Test
  void executeCommand_02() {
    String path = "D:/0000/";
    String mp3FileName = path + "v1.mp3";
    String subtitleFileName = path + "v1_srt.txt";
    String srtFileName = path + "v1.srt";
    String lang = "cmn";

    String command = "python -m aeneas.tools.execute_task " + mp3FileName + " "
      + subtitleFileName
      + " \"task_language=" + lang
      + "|os_task_file_format=srt|is_text_type=plain\" "
      + srtFileName;
    CommandUtil.executeCommand(command);
    // python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=cmn|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
    //python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=eng|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
  }

  @Test
  void executeCommand_03() {
    String path = "D:/0000/Book02/";
    String pureName = "Boo02_v2";
    String mp3FileName = path + pureName + ".mp3";
    String subtitleFileName = path + pureName + "_srt.txt";
    String srtFileName = path + pureName + ".srt";
    String lang = "cmn";

    String command = "python -m aeneas.tools.execute_task " + mp3FileName + " "
      + subtitleFileName
      + " \"task_language=" + lang
      + "|os_task_file_format=srt|is_text_type=plain\" "
      + srtFileName;
    CommandUtil.executeCommand(command);
    // python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=cmn|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
    //python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=eng|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
  }

  @Test
  void executeCommand_04() {
    String path = "D:/0000/Book02/";
    String pureName = "Boo02_v2";
    String mp3FileName = path + pureName + ".mp3";
    String subtitleFileName = path + pureName + "_srt.txt";
    String srtFileName = path + pureName + ".srt";
    String lang = "cmn";

    mp3FileName = "D:\\0000\\商務英語-EP-03-工作當中\\商務英語-EP-03-工作當中.MP3";
    subtitleFileName = "D:\\0000\\EnBook001\\900\\ch003\\ch003_total.txt";

    List<String> textList = new ArrayList<>(List.of(
      "Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.",
      "英文加中文配音，每次半小時，增强你的英文听力。"));
    List<String> srtList = FileUtil.readLines(subtitleFileName, StandardCharsets.UTF_8);
    textList.addAll(srtList);
    String newSubtitleFileName = "D:\\0000\\EnBook001\\900\\ch003\\ch003_total_new.txt";
    CdFileUtil.writeToFile(newSubtitleFileName, textList);

    srtFileName = "D:\\0000\\EnBook001\\900\\ch003\\ch003_total.srt";
    lang = "eng";

    String command = "python -m aeneas.tools.execute_task " + mp3FileName + " "
      + newSubtitleFileName
      + " \"task_language=" + lang
      + "|os_task_file_format=srt|is_text_type=plain\" "
      + srtFileName;
    CommandUtil.executeCommand(command);
    // python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=cmn|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
    //python -m aeneas.tools.execute_task D:/0000/v1.mp3 D:/0000/v1_srt.txt "task_language=eng|os_task_file_format=srt|is_text_type=plain" D:/0000/v1.srt
  }
}
