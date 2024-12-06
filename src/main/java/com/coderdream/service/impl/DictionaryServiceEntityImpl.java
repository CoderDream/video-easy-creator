package com.coderdream.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coderdream.entity.DictionaryEntity;
import com.coderdream.mapper.DictionaryEntityMapper;
import com.coderdream.service.DictionaryEntityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryServiceEntityImpl extends ServiceImpl<DictionaryEntityMapper, DictionaryEntity> implements
    DictionaryEntityService {

    @Override
    public void saveDictionary(DictionaryEntity dictionary) {
        this.save(dictionary);
    }

    @Override
    public List<DictionaryEntity> getAllDictionaries() {
        return this.list();
    }

    @Override
    public DictionaryEntity getDictionaryByWord(String word) {
        return this.lambdaQuery().eq(DictionaryEntity::getWord, word).one();
    }

    @Override
    public void deleteDictionary(Long id) {
        this.removeById(id);
    }
}
