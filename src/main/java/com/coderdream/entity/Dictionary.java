package com.coderdream.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("dictionary")  // 指定与数据库的映射表为 dictionary
public class Dictionary {

    @TableId  // 主键，自增
    private Long id;

    @TableField("word")  // 字段映射
    private String word;  // 单词，唯一键，确保每个单词只出现一次

    @TableField("english_definition")
    private String englishDefinition;  // 单词的英文定义

    @TableField("chinese_definition")
    private String chineseDefinition;  // 单词的中文定义

    @TableField("source")
    private String source;  // 来源，记录单词的出处，如词典名称或其他出处

    @TableField("collins_star")
    private Integer collinsStar;  // 柯林斯五星词频，0~5 星级，默认 0

    @TableField("ielts_level")
    private String ieltsLevel;  // 雅思分类，A1、A2、B1、B2、C1、C2

    @TableField("coca_frequency")
    private Integer cocaFrequency;  // COCA 词频，表示该词在 COCA 数据库中的出现频率

    @TableField("reserved01")
    private String reserved01;  // 预留字段 1，用于存储额外的文本信息

    @TableField("reserved02")
    private String reserved02;  // 预留字段 2，用于存储额外的文本信息

    @TableField("reserved03")
    private String reserved03;  // 预留字段 3，用于存储额外的文本信息

    @TableField("reserved04")
    private String reserved04;  // 预留字段 4，用于存储额外的文本信息

    @TableField("reserved05")
    private String reserved05;  // 预留字段 5，用于存储额外的文本信息

    @TableField("reserved06")
    private Integer reserved06;  // 预留字段 6，整数类型，用于存储额外的数据

    @TableField("reserved07")
    private Integer reserved07;  // 预留字段 7，整数类型，用于存储额外的数据

    @TableField("reserved08")
    private Integer reserved08;  // 预留字段 8，整数类型，用于存储额外的数据

    @TableField("reserved09")
    private Integer reserved09;  // 预留字段 9，整数类型，用于存储额外的数据

    @TableField("reserved10")
    private Integer reserved10;  // 预留字段 10，整数类型，用于存储额外的数据

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private String createdAt;  // 创建时间，记录该条记录的创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private String updatedAt;  // 更新时间，自动更新为当前时间戳

}
