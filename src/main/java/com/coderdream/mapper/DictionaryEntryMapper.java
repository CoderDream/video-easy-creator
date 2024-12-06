package com.coderdream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coderdream.entity.DictionaryEntry;
import java.util.List;

public interface DictionaryEntryMapper extends BaseMapper<DictionaryEntry> {
    // 根据单词查询字典条目
    DictionaryEntry selectByWord(String word);


    int insertOrUpdateBatch(List<DictionaryEntry> entities);
}
