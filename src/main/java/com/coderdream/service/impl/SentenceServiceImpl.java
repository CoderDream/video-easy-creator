package com.coderdream.service.impl;

import com.coderdream.entity.Sentence;
import com.coderdream.mapper.SentenceMapper;
import com.coderdream.service.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentenceServiceImpl implements SentenceService {

    @Autowired
    private SentenceMapper sentenceMapper;

    @Override
    public void insert(Sentence sentence) {
        sentenceMapper.insert(sentence);
    }

    @Override
    public Sentence getSentenceByWordId(Long wordId) {
        return sentenceMapper.selectOne(wordId);  // 使用 selectOne 查询单个句子
    }

    @Override
    public void update(Sentence sentence) {
        sentenceMapper.updateById(sentence);  // 使用 MyBatis-Plus 的 updateById 更新数据
    }

    @Override
    public void delete(Long sentenceId) {
        sentenceMapper.deleteById(sentenceId);  // 使用 MyBatis-Plus 的 deleteById 删除数据
    }

    @Override
    public List<Sentence> getAllSentences() {
        return sentenceMapper.selectList(null);  // 查询所有句子
    }
}
