package com.coderdream.util.bbc;

import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.VocInfo;
import java.util.ArrayList;
import java.util.List;


public class DialogParser {


  public static List<DialogSingleEntity> parseDialogSingleEntity(List<String> inputList) {
    List<DialogSingleEntity> result = new ArrayList<>();
    if (inputList == null || inputList.isEmpty()) {
      return result; // 处理空列表
    }

    DialogSingleEntity currentEntity = null;
    boolean firstLine = true; // 标记是否是第一个非空行

    for (String line : inputList) {

      if (line == null || line.trim().isEmpty()) {

        if (currentEntity != null) {
          result.add(currentEntity); // 加入到结果集
          currentEntity = null; // 重置
          firstLine = true;
        }
        continue; // 跳过空行
      }

      if (firstLine) {
        currentEntity = new DialogSingleEntity(); // 创建新对象
        currentEntity.setHostEn(line.trim()); // 设置 hostEn
        firstLine = false; // 重置标记
      } else {
        if (currentEntity.getContentEn() == null) { //判断contentEn 是否为空
          currentEntity.setContentEn(line.trim()); // 设置 contentEn
        } else {
          //如果不是第一个非空行，并且已经设置了ContentEn,则需要将内容添加到contentEn 中，并且换行
          currentEntity.setContentEn(
            currentEntity.getContentEn() + " " + line.trim());
        }

      }
    }
    if (currentEntity != null) {
      result.add(currentEntity); // 处理最后的对象
    }

    return result;
  }

  public static List<VocInfo> parseDialogVocInfo(List<String> inputList) {
    List<VocInfo> result = new ArrayList<>();
    if (inputList == null || inputList.isEmpty()) {
      return result; // 处理空列表
    }

    VocInfo currentEntity = null;
    boolean firstLine = true; // 标记是否是第一个非空行

    for (String line : inputList) {

      if (line == null || line.trim().isEmpty()) {

        if (currentEntity != null) {
          result.add(currentEntity); // 加入到结果集
          currentEntity = null; // 重置
          firstLine = true;
        }
        continue; // 跳过空行
      }

      if (firstLine) {
        currentEntity = new VocInfo(); // 创建新对象
        currentEntity.setWord(line.trim()); // 设置 hostEn
        firstLine = false; // 重置标记
      } else {
        if (currentEntity.getWordExplainEn() == null) { //判断contentEn 是否为空
          currentEntity.setWordExplainEn(line.trim()); // 设置 contentEn
        } else {
          //如果不是第一个非空行，并且已经设置了ContentEn,则需要将内容添加到contentEn 中，并且换行
          currentEntity.setWordExplainEn(
            currentEntity.getWordExplainEn() + " " + line.trim());
        }

      }
    }
    if (currentEntity != null) {
      result.add(currentEntity); // 处理最后的对象
    }

    return result;
  }


  public static void main(String[] args) {
    List<String> input = new ArrayList<>();
    input.add("");
    input.add("");
    input.add("");
    input.add("");
    input.add("Neil");
    input.add("Hello. Welcome to 6 Minute English, I'm Neil.");
    input.add("");
    input.add("Catherine");
    input.add("And I'm Catherine.");
    input.add("");
    input.add("Neil");
    input.add(
      "Catherine, I'm going to start this programme with a quick test, just for you.");
    input.add("");
    input.add("Catherine");
    input.add("Ooo, I love tests!");
    input.add("Neil");
    input.add("Complete this phrase: Wake up and smell the...");
    input.add("Catherine");
    input.add(
      "Coffee Neil! It's coffee. I have to say, I love coffee, it's great.");
    input.add("Neil");
    input.add("OK, so do you drink much?");
    input.add("Catherine");
    input.add("Well, just a couple of cups, you know.");
    input.add("");
    input.add("Neil");
    input.add("Every day?");

    List<DialogSingleEntity> dialogList = DialogParser.parseDialogSingleEntity(input);

    for (DialogSingleEntity entity : dialogList) {
      System.out.println("HostEn: " + entity.getHostEn());
      System.out.println("ContentEn: " + entity.getContentEn());
      System.out.println("---");
    }
    System.out.println("  测试空列表:");
    List<String> input2 = new ArrayList<>();
    List<DialogSingleEntity> splittedList2 = parseDialogSingleEntity(input2);
    System.out.println(splittedList2);

    System.out.println("  测试 null 列表:");
    List<DialogSingleEntity> splittedList3 = parseDialogSingleEntity(null);
    System.out.println(splittedList3);
  }
}
