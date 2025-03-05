package com.coderdream.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@ContentStyle(borderBottom = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderLeft = BorderStyleEnum.THIN, borderRight = BorderStyleEnum.THIN, verticalAlignment = VerticalAlignmentEnum.CENTER)
//@ContentFontStyle(fontHeightInPoints = 11, fontName = "微软雅黑")
public class SceneDialogEntity {
    private String scene;    // 场景名称
    private String english;     // 英文对话
    private String chinese;    // 中文翻译
}
