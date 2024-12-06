package com.coderdream.util.mdict;

import java.util.List;
import lombok.Data;

@Data
public class HtmlContentBean {
    private String word;
    private String ratingText;
    private List<String> captions;
    private List<String> sentences;
    private List<String> translations;
    private String rawHtml;

}
