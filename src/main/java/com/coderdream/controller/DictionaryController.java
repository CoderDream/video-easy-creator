package com.coderdream.controller;

import com.coderdream.entity.Dictionary;
import com.coderdream.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @PostMapping("/add")
    public String addDictionary(@RequestBody Dictionary dictionary) {
        dictionaryService.saveDictionary(dictionary);
        return "Dictionary entry added successfully!";
    }

    @GetMapping("/list")
    public List<Dictionary> getAllDictionaries() {
        return dictionaryService.getAllDictionaries();
    }

    @GetMapping("/search")
    public Dictionary getDictionaryByWord(@RequestParam String word) {
        return dictionaryService.getDictionaryByWord(word);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteDictionary(@PathVariable Long id) {
        dictionaryService.deleteDictionary(id);
        return "Dictionary entry deleted successfully!";
    }
}
