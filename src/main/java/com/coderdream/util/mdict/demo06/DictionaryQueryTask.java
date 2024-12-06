package com.coderdream.util.mdict.demo06;

import java.util.List;

public class DictionaryQueryTask implements Runnable {
    private String word;
    private DictionaryService dictionaryService;

    public DictionaryQueryTask(String word, DictionaryService dictionaryService) {
        this.word = word;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public void run() {
        // 调用字典服务查询接口
        String result = dictionaryService.queryDictionary(word);
        // 这里可以将结果保存到共享的地方（如List），或者直接处理结果
        System.out.println("Query result for " + word + ": " + result);
    }
}
