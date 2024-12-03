package com.coderdream.service;

import com.coderdream.entity.Dictionary;
import java.util.List;

public interface DictionaryService {
    void saveDictionary(Dictionary dictionary);
    List<Dictionary> getAllDictionaries();
    Dictionary getDictionaryByWord(String word);
    void deleteDictionary(Long id);
}
