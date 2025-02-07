package com.coderdream.util.cd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CdListUtil {


  /**
   * 分批插⼊-公共⽅法
   *
   * @param objects：数据集合
   * @param subSize：单次插⼊的条数
   */
  public static <T> List<List<T>> splitTo(List<T> objects, int subSize) {
    //1.确定数据要分⼏次插⼊（根据总条数和每次插⼊条数）
    List<List<T>> lists = new ArrayList<>();
    int idCount = objects.size();
    //插⼊次数（批量插⼊数据库次数）
    int loopTimes = idCount / subSize;
    if (loopTimes * subSize < idCount) {
      loopTimes++;
    }
    //2.把每⼀次插⼊的数据放到双重集合⾥
    for (int i = 0; i < loopTimes; ++i) {
      int fromIndex = i * subSize;
      int toIndex = (i + 1) * subSize;
      lists.add(objects.subList(fromIndex, Math.min(toIndex, idCount)));
    }
    return lists;
  }

    public static List<String> removeEmptyAndNoSlashLines(List<String> inputList) {
        if (inputList == null || inputList.isEmpty()) {
            return new ArrayList<>(); // 处理空列表或null，返回空列表
        }

        return inputList.stream()
                .filter(line -> line != null && !line.trim().isEmpty() && line.contains("/"))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<String> input = new ArrayList<>();
        input.add("Line 1 / abc");
        input.add("Line 2");
        input.add(""); // 空行
        input.add("  "); // 只有空格的行
        input.add("Line 3 / def");
         input.add(null); // null 行
        input.add("Line 4 / ");
         input.add("/Line5");
         input.add("Line6 /");

        List<String> result = removeEmptyAndNoSlashLines(input);

        System.out.println("原始列表：");
        input.forEach(System.out::println);
        System.out.println("\n移除空行和不存在 / 的行的列表:");
        result.forEach(System.out::println);


         System.out.println("\n测试空列表:");
          List<String> input2 = new ArrayList<>();
           List<String> splittedList2 = removeEmptyAndNoSlashLines(input2);
           System.out.println(splittedList2);

         System.out.println("\n测试 null 列表:");
          List<String> splittedList3 = removeEmptyAndNoSlashLines(null);
           System.out.println(splittedList3);
    }
}
