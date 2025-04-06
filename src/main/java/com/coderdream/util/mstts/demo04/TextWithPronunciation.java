package com.coderdream.util.mstts.demo04;

import lombok.Data;

@Data
class TextWithPronunciation {
    private String text;
    private String pronunciation; // IPA 或自定义音标
}
