package com.coderdream.util.excel;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.SceneDialogEntity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CdExcelUtilTest {

  @Test
  void genSceneDialogEntityList_01() {

    //
    String filePath = "D:\\0000\\EnBook005\\100个生活中的英文场景复杂版.xlsx";
    List<SceneDialogEntity> sceneDialogEntityList = CdExcelUtil.genSceneDialogEntityList(
      filePath);
    for (SceneDialogEntity sceneDialogEntity : sceneDialogEntityList) {
      log.info("sceneDialogEntity:{}", sceneDialogEntity);
    }
  }


  @Test
  void transferSceneDialogEntityListToTextFileList_01() {
    String filePath = "D:\\0000\\EnBook004\\100个生活中的英文场景简单版.xlsx";
    CdExcelUtil.transferSceneDialogEntityListToTextFileList(
      filePath);
  }

  @Test
  void transferSceneDialogEntityListToTextFileList_02() {
    String filePath = "D:\\0000\\EnBook005\\100个生活中的英文场景复杂版.xlsx";
    CdExcelUtil.transferSceneDialogEntityListToTextFileList(
      filePath);
  }

}
