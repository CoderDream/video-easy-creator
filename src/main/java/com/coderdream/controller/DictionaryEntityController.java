package com.coderdream.controller;

import com.coderdream.entity.DictionaryEntity;
import com.coderdream.service.DictionaryEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryEntityController {

    @Autowired
    private DictionaryEntityService dictionaryService;

    @PostMapping("/add")
    public String addDictionary(@RequestBody DictionaryEntity dictionary) {
        dictionaryService.saveDictionary(dictionary);
        return "DictionaryEntity entry added successfully!";
    }

    @GetMapping("/list")
    public List<DictionaryEntity> getAllDictionaries() {
        return dictionaryService.getAllDictionaries();
    }

    @GetMapping("/search")
    public DictionaryEntity getDictionaryByWord(@RequestParam String word) {
        return dictionaryService.getDictionaryByWord(word);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteDictionary(@PathVariable Long id) {
        dictionaryService.deleteDictionary(id);
        return "DictionaryEntity entry deleted successfully!";
    }
}
