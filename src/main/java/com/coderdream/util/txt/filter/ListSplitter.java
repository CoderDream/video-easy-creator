package com.coderdream.util.txt.filter;

import cn.hutool.core.io.FileUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ListSplitter {

  public static List<List<String>> splitListByBlankLines(
    List<String> inputList) {
    List<List<String>> result = new ArrayList<>(); // 存储分割后的多个 List
    List<String> currentList = new ArrayList<>(); // 当前正在构建的 List

    if (inputList == null || inputList.isEmpty()) {
      return result; // 处理空列表
    }

    for (String line : inputList) {
      if (line == null || line.trim().isEmpty()) {  // 判断是否为空行 (trim() 去除首尾空格)
        if (!currentList.isEmpty()) {  // 如果当前列表不为空，则添加到结果列表中
          result.add(currentList);
          currentList = new ArrayList<>(); // 创建新的 List
        }
      } else {
        currentList.add(line); // 添加非空行到当前列表
      }
    }

    // 循环结束后，如果currentList不为空，则需要将其加入到result中
    if (!currentList.isEmpty()) {
      result.add(currentList);
    }

    return result;
  }

  public static void main(String[] args) {
    List<String> input = FileUtil.readLines("D:\\0000\\EnBook001\\900\\ch003\\input\\ch003.txt",
      StandardCharsets.UTF_8);
    List<List<String>> splittedList = splitListByBlankLines(input);

    // 打印分割后的结果
    for (int i = 0; i < splittedList.size(); i++) {
      System.out.println(splittedList.get(i).size());
//      System.out.println("List " + (i + 1) + ":");
//
//      for (String str : splittedList.get(i)) {
//        System.out.println("  " + str);
//      }
    }
  }
}
