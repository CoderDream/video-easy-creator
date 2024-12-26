package com.coderdream.util.subtitle;

import com.coderdream.vo.SentenceDurationVO;
import java.text.DecimalFormat;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DurationCalculator {

    public static void main(String[] args) {
        String filePath = "src/main/resources/input.txt"; // 输入文件路径

        // 读取并计算时长
        List<SentenceDurationVO> result = calculateTotalDurationFromFile(filePath);

        // 输出计算结果
        result.forEach(vo -> log.info(vo.toString()));
    }

    /**
     * 从指定文件读取数据并计算每个句子的总时长
     *
     * @param filePath 输入文本文件路径
     * @return List<SentenceDurationVO> 每个句子的时长信息
     */
    public static List<SentenceDurationVO> calculateTotalDurationFromFile(String filePath) {
        List<SentenceDurationVO> result = new ArrayList<>();
        int id = 1; // 初始化句子ID

        // 使用DecimalFormat来确保保留3位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 按制表符分割中文和英文文本及其时长
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    try {
                        String chinese = parts[0].trim();
                        double chineseDuration = Double.parseDouble(parts[1].trim());
                        String english = parts[2].trim();
                        double englishDuration = Double.parseDouble(parts[3].trim());

                        // 计算总时长: 总时长 = 4 * 英文时长 + 中文时长
                        double totalDuration = 4 * englishDuration + chineseDuration;

                        // 格式化总时长为保留3位小数
                        totalDuration = Double.parseDouble(decimalFormat.format(totalDuration));

                        // 创建并填充 SentenceDurationVO 对象
                        SentenceDurationVO vo = new SentenceDurationVO();
                        vo.setId(id++);
                        vo.setChinese(chinese);
                        vo.setChineseDuration(chineseDuration);
                        vo.setEnglish(english);
                        vo.setEnglishDuration(englishDuration);
                        vo.setTotalDuration(totalDuration);

                        // 添加到结果列表
                        result.add(vo);
                    } catch (NumberFormatException e) {
                        log.warn("Skipping line due to number format error: {}", line);
                    }
                } else {
                    log.warn("Skipping invalid line (not 4 parts): {}", line);
                }
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
        }

        return result;
    }
}
