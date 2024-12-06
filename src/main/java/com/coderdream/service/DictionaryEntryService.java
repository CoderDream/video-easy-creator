package com.coderdream.service;

import com.coderdream.entity.DictionaryEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface DictionaryEntryService extends IService<DictionaryEntry> {
    // 你可以在这里定义自定义的方法，MyBatis-Plus 会自动实现其他的 CRUD 操作
    DictionaryEntry getDictionaryByWord(String word);

    /**
     * 批量插入或更新
     * @param dictionaryEntryList   词典列表
     * @return  int
     */
    int insertOrUpdateBatch(List<DictionaryEntry> dictionaryEntryList);
}
