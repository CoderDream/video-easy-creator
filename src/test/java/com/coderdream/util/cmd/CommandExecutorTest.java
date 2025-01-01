package com.coderdream.util.cmd;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommandExecutorTest {

  @Test
  void executeCommand_DeployHexo() {
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
}
