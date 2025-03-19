package com.coderdream.util.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import com.coderdream.entity.SceneDialogEntity;
import com.coderdream.entity.WordInfoEntity;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdFileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoderDream
 */
@Slf4j
public class CdExcelUtil {

  public static List<WordInfoEntity> genWordInfoEntityList(String filePath,
    String sheetName) {
//        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String path = BaseUtils.getPath();
//        String fileName = File.separator + path + File.separator + dateStr + ".xlsx";
//        ExcelReader reader = ExcelUtil.getReader(CdFileUtil.file(fileName), "Sheet1");
    ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath),
      sheetName);
    // 单词	英音	美音	释义	等级
    reader.addHeaderAlias("单词", "word");
    reader.addHeaderAlias("英音", "uk");
    reader.addHeaderAlias("美音", "us");
    reader.addHeaderAlias("释义", "comment");
    reader.addHeaderAlias("等级", "level");
    reader.addHeaderAlias("次数", "times");
    List<WordInfoEntity> recommendAppList = reader.readAll(
      WordInfoEntity.class);
    for (WordInfoEntity wordInfo : recommendAppList) {
      wordInfo.setWord(wordInfo.getWord().toLowerCase());
      wordInfo.setComment(wordInfo.getComment().replaceAll("\n", ";"));
      wordInfo.setLevelStr(wordInfo.getLevel());
    }
    reader.close();

    return recommendAppList;
  }

  public static List<SceneDialogEntity> genSceneDialogEntityList(
    String filePath) {
    return genSceneDialogEntityList(filePath, "Sheet1");
  }

  public static List<SceneDialogEntity> genSceneDialogEntityList(
    String filePath, String sheetName) {
//        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String path = BaseUtils.getPath();
//        String fileName = File.separator + path + File.separator + dateStr + ".xlsx";
//        ExcelReader reader = ExcelUtil.getReader(CdFileUtil.file(fileName), "Sheet1");
    ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath),
      sheetName);
    // 场景名称	英文对话	中文翻译
    reader.addHeaderAlias("场景名称", "scene");
    reader.addHeaderAlias("英文对话", "english");
    reader.addHeaderAlias("中文翻译", "chinese");
    List<SceneDialogEntity> sceneDialogEntityList = reader.readAll(
      SceneDialogEntity.class);
    String sceneOld = "";
    String scene = "";
    for (SceneDialogEntity sceneDialogEntity : sceneDialogEntityList) {
      if (StrUtil.isEmpty(sceneDialogEntity.getEnglish()) && StrUtil.isEmpty(
        sceneDialogEntity.getChinese())) {
        continue;
      }
      scene = sceneDialogEntity.getScene();
      if (StrUtil.isNotEmpty(scene)) {
        scene = scene.replace("（早起习惯）", "")
          .trim();// （早起习惯）（复杂对话）（闹钟响了、赖床）（简单对话）
        scene = scene.replace("（闹钟响了、赖床）", "").trim();// （早起习惯）（复杂对话）
        scene = scene.replace("（复杂对话）", "").trim();// （早起习惯）（复杂对话）
        scene = scene.replace("（简单对话）", "").trim();// （早起习惯）（复杂对话）
        sceneOld = scene;
      }
      if (StrUtil.isEmpty(scene)) {
        sceneDialogEntity.setScene(sceneOld);
      }
    }
    reader.close();

    return sceneDialogEntityList;
  }

  public static void transferSceneDialogEntityListToTextFileList(
    String filePath) {
    transferSceneDialogEntityListToTextFileList(filePath, "Sheet1");
  }

  public static void transferSceneDialogEntityListToTextFileList(
    String filePath, String sheetName) {
    List<SceneDialogEntity> sceneDialogEntityList = genSceneDialogEntityList(
      filePath, sheetName);
    String folderName = new File(filePath).getParent();
    String scene = "";
    List<String> contentList;
    int idx = 0;
    // 分割列表
    Map<String, List<SceneDialogEntity>> sceneMap = splitByScene(
      sceneDialogEntityList);
    for (Map.Entry<String, List<SceneDialogEntity>> entry : sceneMap.entrySet()) {
      scene = entry.getKey();
      log.info("scene:{}", scene);
      contentList = new ArrayList<>();
      String english = "";
      String chinese = "";
      // 去掉冒号之前的字符
      for (SceneDialogEntity sceneDialogEntity : entry.getValue()) {
        english = sceneDialogEntity.getEnglish();
        english = !english.contains(": ")
          ? english : english.substring(english.indexOf(": ") + 1);
        contentList.add(english);
//        contentList.add("\n");
        chinese = sceneDialogEntity.getChinese();
        chinese = !chinese.contains("：")
          ? chinese : chinese.substring(chinese.indexOf("：") + 1);
        contentList.add(chinese);
//        contentList.add("\n");
      }
      idx++;
      String subFolderName =
        folderName + File.separator + "Chapter" + String.format("%03d", idx);
      // 若文件夹不存在，则创建
      if (!FileUtil.exist(subFolderName)) {
        FileUtil.mkdir(subFolderName);
      }

      String fileName =
        subFolderName
          + File.separator + "Chapter" + String.format("%03d", idx) + ".txt";
//      if (!CdFileUtil.isFileEmpty(fileName)) {
      CdFileUtil.writeToFile(fileName, contentList);
//      }
    }
  }

  /**
   * 将 SceneDialogEntity 列表按 scene 属性分割成多个子列表。
   *
   * @param dialogList 原始的 SceneDialogEntity 列表
   * @return 一个 Map，其中键是 scene 名称，值是对应 scene 的 SceneDialogEntity 子列表
   */
  public static Map<String, List<SceneDialogEntity>> splitByScene(
    List<SceneDialogEntity> dialogList) {
    Map<String, List<SceneDialogEntity>> sceneMap = new LinkedHashMap<>();

    if (dialogList == null || dialogList.isEmpty()) {
      return sceneMap; // 返回空的 Map
    }

    for (SceneDialogEntity entity : dialogList) {
      String scene = entity.getScene();
      scene = scene.replace("（早起习惯）", "")
        .trim();// （早起习惯）（复杂对话）（闹钟响了、赖床）（简单对话）
      scene = scene.replace("（闹钟响了、赖床）", "").trim();// （早起习惯）（复杂对话）
      scene = scene.replace("（复杂对话）", "").trim();// （早起习惯）（复杂对话）
      scene = scene.replace("（简单对话）", "").trim();// （早起习惯）（复杂对话）
      if (scene == null) {
        scene = "NULL_SCENE";  // 或者你可以选择抛出异常,根据你的需求
      }

      //computeIfAbsent: 如果指定的键尚未与值关联（或映射为 null），
      //则尝试使用给定的映射函数计算其值，并将其输入此映射，除非为 null。
      sceneMap.computeIfAbsent(scene, k -> new ArrayList<>()).add(entity);

      //旧方法，没有computeIfAbsent高效
      //if (!sceneMap.containsKey(scene)) {
      //    sceneMap.put(scene, new ArrayList<>());
      //}
      //sceneMap.get(scene).add(entity);
    }

    return sceneMap;
  }


  /**
   * @return
   */
  public static List<WordInfoEntity> getAdvancedWordList(String folderName) {
    String fileName = folderName + "_完整词汇表";
    String filePath = CommonUtil.getFullPathFileName(folderName, fileName,
      ".xlsx");

    return CdExcelUtil.genWordInfoEntityList(filePath, "四六级及以上");
  }
}

