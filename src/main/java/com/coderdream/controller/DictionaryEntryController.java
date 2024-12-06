package com.coderdream.controller;

import com.coderdream.entity.DictionaryEntry;
import com.coderdream.service.DictionaryEntryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary-entry")
@Slf4j
public class DictionaryEntryController {

    @Resource
    private DictionaryEntryService dictionaryEntryService;

    @PostMapping("/add")
    public String addDictionary(@RequestBody DictionaryEntry dictionaryEntry) {
        dictionaryEntryService.save(dictionaryEntry);
        return "DictionaryEntity entry added successfully!";
    }

    @PostMapping("/insertOrUpdateBatch")
    public String insertOrUpdateBatch(@RequestBody List<DictionaryEntry> dictionaryEntryList) {
        if (dictionaryEntryList == null || dictionaryEntryList.isEmpty()) {
            throw new IllegalArgumentException("Request body must contain a non-empty list of dictionary entries.");
        }
        int i = dictionaryEntryService.insertOrUpdateBatch(dictionaryEntryList);
        log.info("insertOrUpdateBatch: {}", i);
        return "DictionaryEntity entry list insertOrUpdateBatch successfully!";
    }

    @GetMapping("/list")
    public List<DictionaryEntry> getAllDictionaries() {
        return dictionaryEntryService.list();
    }

    @GetMapping("/search")
    public DictionaryEntry getDictionaryByWord(@RequestParam String word) {
        return dictionaryEntryService.getDictionaryByWord(word);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteDictionary(@PathVariable Long id) {
        dictionaryEntryService.removeById(id);
        return "DictionaryEntity entry deleted successfully!";
    }

    @PutMapping("/update/{id}")
    public String updateDictionary(@PathVariable Long id, @RequestBody DictionaryEntry dictionaryEntry) {
        dictionaryEntry.setId(id);
        dictionaryEntryService.updateById(dictionaryEntry);
        return "DictionaryEntity entry updated successfully!";
    }
}
