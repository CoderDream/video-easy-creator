package com.coderdream.util.audio.demo02;

import com.coderdream.util.cd.CdFileUtil;
import java.util.ArrayList;
import java.util.List;

public class SentenceParser {
    public static List<SentenceVO> parseSentencesFromFile(String filePath) {
        List<String> lines = CdFileUtil.readFileContent(filePath);
        List<SentenceVO> sentences = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length == 4) {
                SentenceVO vo = new SentenceVO();
                vo.setNumber(Integer.parseInt(parts[0].trim()));
                vo.setEnglish(parts[1].trim());
                vo.setPhonetic(parts[2].trim());
                vo.setChinese(parts[3].trim());
                sentences.add(vo);
            }
        }
        return sentences;
    }
}
