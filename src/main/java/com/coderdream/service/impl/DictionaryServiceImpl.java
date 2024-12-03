package com.coderdream.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coderdream.entity.Dictionary;
import com.coderdream.mapper.DictionaryMapper;
import com.coderdream.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Override
    public void saveDictionary(Dictionary dictionary) {
        this.save(dictionary);
    }

    @Override
    public List<Dictionary> getAllDictionaries() {
        return this.list();
    }

    @Override
    public Dictionary getDictionaryByWord(String word) {
        return this.lambdaQuery().eq(Dictionary::getWord, word).one();
    }

    @Override
    public void deleteDictionary(Long id) {
        this.removeById(id);
    }
}
