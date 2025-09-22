package com.coderdream.util.subtitle;

import lombok.Data;

import java.util.List;

/**
 * 字幕块实体类，用于表示一个完整的字幕单元（序号、时间戳、文本行）。
 *
 * @author Gemini Code Assist
 */
@Data
public class SubtitleBlock {

    private int index;
    private String timestamp;
    private List<String> textLines;

}
