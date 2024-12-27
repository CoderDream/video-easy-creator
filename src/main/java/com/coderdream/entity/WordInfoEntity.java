package com.coderdream.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ContentStyle(borderBottom = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN, verticalAlignment = VerticalAlignmentEnum.CENTER)
//@ContentFontStyle(fontHeightInPoints = 11, fontName = "微软雅黑")
public class WordInfoEntity {

//    private String word;    // 单词
//    private String uk;      // 英音
//    private String cn;      // 释义
//    private String levelStr;    // 等级
//    private Integer times;  // 次数

    private String word;    // 单词
    private String cn;      // 中文释义
    private String uk;      // 英文释义
    private String us;      // 美文释义
    private String comment; // 备注

    private String level;   // 单词级别
    private String levelStr;    // 单词级别
    private Integer times;  // 出现次数
}
