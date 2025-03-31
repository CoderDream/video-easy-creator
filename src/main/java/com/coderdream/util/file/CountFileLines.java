package com.coderdream.util.file;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CountFileLines {

  public static void main(String[] args) {
    String filePath = "C:\\Users\\CoderDream\\Desktop\\words2";
    List<String> fileNames = FileUtil.listFileNames(filePath);
    for (String fileName : fileNames) {
      List<String> lines = FileUtil.readLines(filePath + File.separator + fileName, StandardCharsets.UTF_8);
      if(lines.size() != 201){
        System.out.println(fileName + " " + lines.size());
      }
    }

  }


}
