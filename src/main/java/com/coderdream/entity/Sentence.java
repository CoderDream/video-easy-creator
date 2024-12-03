package com.coderdream.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sentences")
public class Sentence {
    @TableId
    private Long id;
    private Long wordId; // 外键，关联到 `dictionary` 表的 id
    private String englishSentence; // 英文例句
    private String chineseSentence; // 中文例句
    private String source; // 来源
    private String createdAt; // 创建时间
    private String updatedAt; // 更新时间
}
