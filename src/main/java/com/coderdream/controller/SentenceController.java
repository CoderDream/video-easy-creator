package com.coderdream.controller;

import com.coderdream.entity.Sentence;
import com.coderdream.service.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sentences")
public class SentenceController {

    @Autowired
    private SentenceService sentenceService;

    // 插入句子
    @PostMapping
    public void addSentence(@RequestBody Sentence sentence) {
        sentenceService.insert(sentence);
    }

    // 根据单词ID查询句子
    @GetMapping("/{wordId}")
    public Sentence getSentenceByWordId(@PathVariable Long wordId) {
        return sentenceService.getSentenceByWordId(wordId);
    }

    // 获取所有句子
    @GetMapping
    public List<Sentence> getAllSentences() {
        return sentenceService.getAllSentences();
    }

    // 更新句子
    @PutMapping("/{sentenceId}")
    public void updateSentence(@PathVariable Long sentenceId, @RequestBody Sentence sentence) {
        sentence.setId(sentenceId);  // 设置ID，确保更新操作指向正确的记录
        sentenceService.update(sentence);
    }

    // 删除句子
    @DeleteMapping("/{sentenceId}")
    public void deleteSentence(@PathVariable Long sentenceId) {
        sentenceService.delete(sentenceId);
    }
}
