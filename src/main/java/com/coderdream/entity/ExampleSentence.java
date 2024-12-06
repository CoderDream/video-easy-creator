package com.coderdream.entity;

import lombok.Data;

// 内部类：例句
@Data
public class ExampleSentence {
    private String englishSentence;  // 英文例句
    private String chineseTranslation;  // 中文翻译
    private String pronunciationUk;  // 英式发音音频链接
    private String pronunciationUs;  // 美式发音音频链接
}
