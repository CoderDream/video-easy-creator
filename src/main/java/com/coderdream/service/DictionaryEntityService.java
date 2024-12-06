package com.coderdream.service;

import com.coderdream.entity.DictionaryEntity;
import java.util.List;

public interface DictionaryEntityService {
    void saveDictionary(DictionaryEntity dictionary);
    List<DictionaryEntity> getAllDictionaries();
    DictionaryEntity getDictionaryByWord(String word);
    void deleteDictionary(Long id);
}
