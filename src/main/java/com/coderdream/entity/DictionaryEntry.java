package com.coderdream.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName(value = "dictionary_entry", autoResultMap = true)
public class DictionaryEntry implements Serializable {

    @TableId
    private Long id;

    // 词条的基本信息
    private String word;  // 单词
    private String syllable;  // 音节
    private String partOfSpeech;  // 词性（如：noun）
    private String ukPronunciation;  // 英式发音
    private String usPronunciation;  // 美式发音

    // 释义及中文翻译
    private String definition;  // 英文释义
    private String chineseDefinition;  // 中文释义

    // 例句列表
//    private List<ExampleSentence> exampleSentences;  // 例句
//    @Handler(value = ExampleSentencesTypeHandler.class)
//    private List<ExampleSentence> exampleSentences;  // 例句

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject exampleSentences;


    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject collocations;

    @TableField("collins_star")
    private Integer collinsStar;  // 柯林斯五星词频，0~5 星级，默认 0

    @TableField("ielts_level")
    private Integer ieltsLevel;  // 雅思分类，1:A1、2:A2、3:B1、4:B2、5:C1、6:C2

    @TableField("coca_rank")
    private Integer cocaRank;  // COCA 词频，表示该单词在COCA中的排名 (如1~3500, 3501~5000)

    //    private List<String> collocations;  // 搭配词
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;  // 创建时间，记录该条记录的创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;  // 更新时间，自动更新为当前时间戳
}
