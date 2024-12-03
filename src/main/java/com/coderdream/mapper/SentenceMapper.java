package com.coderdream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coderdream.entity.Sentence;
import com.coderdream.entity.Sentence;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SentenceMapper extends BaseMapper<Sentence> {

    // 通过单词ID查询句子（可以根据需要自定义方法）
    Sentence selectOne(Long wordId);
}
