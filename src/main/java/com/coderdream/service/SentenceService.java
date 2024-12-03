package com.coderdream.service;

import com.coderdream.entity.Sentence;

import java.util.List;

public interface SentenceService {

    // 插入句子
    void insert(Sentence sentence);

    // 根据单词ID查询句子
    Sentence getSentenceByWordId(Long wordId);

    // 更新句子
    void update(Sentence sentence);

    // 删除句子
    void delete(Long sentenceId);

    // 获取所有句子
    List<Sentence> getAllSentences();
}
