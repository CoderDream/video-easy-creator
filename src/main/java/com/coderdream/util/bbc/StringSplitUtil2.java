package com.coderdream.util.bbc;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串分割工具类
 */
@Slf4j
public class StringSplitUtil2 {

  /**
   * 使用逗号分割字符串，并将逗号添加到每个分割后的字符串末尾，并移除最后一个逗号。
   *
   * @param input 需要分割的字符串
   * @return 分割后的字符串列表
   */
  public static List<String> splitStringWithComma(String input) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<String> result = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      log.warn("输入字符串为 null 或空，返回空列表");
      return result;
    }

    try {
      List<String> tempResult = Arrays.stream(input.split(","))
        .map(String::trim).toList();
      for (int i = 0; i < tempResult.size(); i++) {
        String item = tempResult.get(i);
        if (i != tempResult.size() - 1) {
          item = item + ", ";
        }
        result.add(item);
      }
//      log.info("字符串分割成功， 分割后的字符串列表为：{}", result);
      return result;
    } catch (Exception e) {
      log.error("字符串分割失败", e);
      return result;
    } finally {
      long endTime = System.currentTimeMillis();  // 记录结束时间
      long elapsedTime = endTime - startTime;  // 计算耗时
      String formattedTime = CdTimeUtil.formatDuration(elapsedTime);
//      log.info("splitStringWithComma 方法耗时：{}", formattedTime);
    }
  }


  public static void main(String[] args) {
    String input = "And when you walk in to the coffee shop in the morning, and that smell hits you, you're getting physiological responses,";
    List<String> strings = StringSplitUtil2.splitStringWithComma(input);
    for (String str : strings) {
      System.out.println(str);
    }
    System.out.println("\n 测试空列表:");
    List<String> strings2 = splitStringWithComma("");
    System.out.println(strings2);

    System.out.println("\n 测试null列表:");
    List<String> strings3 = splitStringWithComma(null);
    System.out.println(strings3);

  }
}
