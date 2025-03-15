package com.coderdream.util.file;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CdFileBatchUtil {

  /**
   * 删除指定的文件夹及其中的所有文件和子文件夹
   *
   * @param directory 文件夹路径
   * @return 如果删除成功则返回true，否则返回false
   */
  public static boolean deleteDirectory(File directory) {
    // 判断文件夹是否为空
    if (directory.isDirectory()) {
      // 获取文件夹中的所有文件和子文件夹
      String[] files = directory.list();
      if (files != null) {
        // 递归删除文件夹中的内容
        for (String file : files) {
          File fileOrDir = new File(directory, file);
          if (fileOrDir.isDirectory()) {
            // 如果是子文件夹，递归删除
            deleteDirectory(fileOrDir);
          } else {
            // 如果是文件，直接删除
            fileOrDir.delete();
          }
        }
      }
    }
    // 删除文件夹本身
    return directory.delete();
  }

  public static void batchDelete() {
    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 51;//51
    for (int i = 10; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    List<String> subFolders2 = Arrays.asList("audio", "audio_mix", "video_cht",
      "video_cht_1", "video_cht_2", "video_cht_3"); // directoryPath
    String directoryPath;
    for (String subFolder : subFolders) {
//            BeforeGenerateUtil.processBook002_AI(folderPath, subFolder);
      directoryPath = folderPath + File.separator + subFolder;
      for (String subFolder2 : subFolders2) {
        directoryPath =
          folderPath + File.separator + subFolder + File.separator + subFolder2;
        File directory = new File(directoryPath);
        if (directory.exists()) {
          // 删除文件夹及其中的所有内容
          boolean result = deleteDirectory(directory);
          if (result) {
            System.out.println("文件夹及其中的文件已成功删除！");
          } else {
            System.out.println("删除文件夹时发生错误！");
          }
        } else {
          System.out.println("指定的文件夹不存在！");
        }
      }
    }
  }

  public static void batchCopy() {
    String bookName = "EnBook005";
    String folderPath = OperatingSystem.getFolderPath(bookName);
    List<String> subFolders = new ArrayList<>();
    int end = 101;//51
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    String destFilePath =
      OperatingSystem.getDiskFolder() + File.separator + "OBS_Video"
        + File.separator + bookName;
    File destFilePathDir = new File(destFilePath);
    if (!destFilePathDir.exists()) {
      // 创建文件夹
      boolean mkdirs = destFilePathDir.mkdirs();
      if (mkdirs) {
        log.info("创建文件夹成功 {}", destFilePath);
      } else {
        System.out.println("指定的文件夹不存在！");
      }
    }
    for (String subFolder : subFolders) {
      String srcFilePath =
        folderPath + File.separator + subFolder + File.separator + "video"
          + File.separator + subFolder
          + ".mp4";

      String destFileName =
        destFilePath + File.separator + subFolder + ".mp4";
      if (!CdFileUtil.isFileEmpty(srcFilePath) && CdFileUtil.isFileEmpty(
        destFileName)) {
        File file = FileUtil.copyFile(srcFilePath, destFileName);
        if (file != null) {
          log.info("文件复制成功 {}", file.getAbsolutePath());
        }
      } else {
        log.info("原始文件不存在 {}", srcFilePath);
      }
    }
  }

//    public static void main(String[] args){
//      // 要删除的文件夹路径
////        String directoryPath = "D:\\0000\\EnBook002\\Chapter010\\audio\\";
////
////        File directory = new File(directoryPath);
////        if (directory.exists()) {
////            // 删除文件夹及其中的所有内容
////            boolean result = deleteDirectory(directory);
////            if (result) {
////                System.out.println("文件夹及其中的文件已成功删除！");
////            } else {
////                System.out.println("删除文件夹时发生错误！");
////            }
////        } else {
////            System.out.println("指定的文件夹不存在！");
////        }
//
//    }


  public static void main(String[] args) {
    batchCopy();
  }
}
