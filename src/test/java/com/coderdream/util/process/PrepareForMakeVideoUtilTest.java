package com.coderdream.util.process;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.process.bbc.SixMinutesStepByStep;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrepareForMakeVideoUtilTest {

  private List<String> NUMBER_LIST;


  private List<String> YOUTUBE_LIST;

  @BeforeEach
  void init() {
    String folderPathForSixMinutes =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
        + File.separatorChar;

    NUMBER_LIST = FileUtil.readLines(folderPathForSixMinutes + File.separator + "todo.txt", "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));

    String folderPathForYoutube =
      CdFileUtil.getResourceRealPath() + File.separatorChar + "youtube"
        + File.separatorChar;

    YOUTUBE_LIST = FileUtil.readLines(folderPathForYoutube + File.separator + "yt_todo.txt", "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));
  }


  @Test
  void processTodoForSixMinutes() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : NUMBER_LIST) {
      String folderName = "" + num;
      PrepareForMakeVideoUtil.processForSixMinutes(folderName);
    }
  }


  @Test
  void processTodoForYoutube() {
    // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
    for (String num : YOUTUBE_LIST) {
      String folderName = "" + num;
      PrepareForMakeVideoUtil.processForSixMinutes(folderName);
    }
  }



  @Test
  void processForSixMinutes() {
    String folderName = "170803";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processForSixMinutesFromTodo() {
    String folderName = "250403";
    PrepareForMakeVideoUtil.processForSixMinutes(folderName);
  }

  @Test
  void processYoutubeFromTodo_0003() {
    String categoryName = "0003_PressBriefings";
    String folderName = "20250402";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo() {
    List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
    for (YoutubeInfoEntity youtubeInfoEntity : youtubeVideoInfoEntityList) {
      String category = youtubeInfoEntity.getCategory();
      String dateString = youtubeInfoEntity.getDateString();
      PrepareForMakeVideoUtil.processYoutube(category, dateString);
    }
  }

  @Test
  void processYoutubeFromTodo_0007() {
    String categoryName = "0007_Trump";
    String folderName = "20250331";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0008() {
    String categoryName = "0008_DailyNews";
    String folderName = "20250321";
    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

  @Test
  void processYoutubeFromTodo_0009() {
    String categoryName = "0009_TechNews";
    String folderName = "20250319"; // D:\0000\0007_Trump\20250227

    PrepareForMakeVideoUtil.processYoutube(categoryName, folderName);
  }

}
