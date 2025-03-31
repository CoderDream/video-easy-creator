package com.coderdream.util.bbc;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * 字符串分割工具类
 */
@Slf4j
public class StringSplitUtil {

    /**
     * 使用逗号分割字符串，并将逗号添加到每个分割后的字符串末尾，并移除最后一个逗号。
     *
     * @param input 需要分割的字符串
     * @return 分割后的字符串列表
     */
     public static List<String> splitStringWithTrailingComma(String input) {
        long startTime = System.currentTimeMillis(); // 记录开始时间
        List<String> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            log.warn("输入字符串为 null 或空，返回空列表");
            return result;
        }

        try {
            String trimmedInput = input.trim();
            if (trimmedInput.endsWith(",")) {
                trimmedInput = trimmedInput.substring(0, trimmedInput.length() - 1);
            }
            String[] parts = trimmedInput.split(",");

            for (int i = 0; i < parts.length; i++) {
                StringBuilder sb = new StringBuilder(parts[i].trim());
                if (i != parts.length - 1) {
                    sb.append(",");
                }
                result.add(sb.toString());
            }

            log.debug("字符串分割成功， 分割后的字符串列表为：{}", result);
            return result;
        } catch (Exception e) {
            log.error("字符串分割失败", e);
            return result;
        } finally {
            long endTime = System.currentTimeMillis();  // 记录结束时间
            long elapsedTime = endTime - startTime;  // 计算耗时
            String formattedTime = CdTimeUtil.formatDuration(elapsedTime);
            log.info("splitStringWithTrailingComma 方法耗时：{}", formattedTime);
        }
    }


    public static void main(String[] args) {
        String input = "And when you walk in to the coffee shop in the morning, and that smell hits you, you're getting physiological responses,";
        List<String> strings = StringSplitUtil.splitStringWithTrailingComma(input);
        for (String str : strings) {
            System.out.println(str);
        }
    }
}
