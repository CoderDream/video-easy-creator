package com.coderdream.entity;

import lombok.Data;

@Data
public class CefrWordEntity {

    private Integer id;
    private String headword;
    private String englishPhonetic;
    private String americanPhonetic;
    private String pos;
    private String cefr;
    private String chineseDefinition;
    private String example;
    private String examplePhonetic;
    private String exampleTranslation;
    private Integer level;
}
