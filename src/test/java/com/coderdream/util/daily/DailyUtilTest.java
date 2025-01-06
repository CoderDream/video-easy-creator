package com.coderdream.util.daily;

import com.coderdream.util.cmd.CommandUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class DailyUtilTest {

  @Test
  void process_250102() {

    String folderName = "250102";
    String title = "【BBC六分钟英语】你喝了足够的水吗？";
    DailyUtil.process(folderName, title);

    // MarkdownFileGenerator
  }

  @Test
  void process_180927() {

//    String folderName = "180927";
//    String title = "【BBC六分钟英语】工地标配变时尚单品？";
//    DailyUtil.process(folderName, title);

    List<String> commandList = Arrays.asList(
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g",
      "cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d");
    for (String command : commandList) {
      CommandUtil.executeCommand(command);
    }


  }
}
