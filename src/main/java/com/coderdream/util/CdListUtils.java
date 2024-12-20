package com.coderdream.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CoderDream
 * @date 2024-12-04 15:22:27
 */
public class CdListUtils {

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
}
