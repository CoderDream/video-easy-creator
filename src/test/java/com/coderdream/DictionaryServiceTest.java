package com.coderdream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.coderdream.entity.DictionaryEntity;
import com.coderdream.mapper.DictionaryEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback(false)
public class DictionaryServiceTest {

    @Autowired
    private DictionaryEntityMapper dictionaryMapper;

    private DictionaryEntity testWord;

    @BeforeEach
    public void setUp() {
        // Set up a test dictionary entry
        testWord = new DictionaryEntity();
        testWord.setWord("test");
        testWord.setEnglishDefinition("A test word");
        testWord.setChineseDefinition("一个测试单词");
        testWord.setSource("Unit Test");
        testWord.setCollinsStar(3);
        testWord.setIeltsLevel(3);
        testWord.setCocaFrequency(5000);
        testWord.setCreatedAt(LocalDateTime.now().toString());
        testWord.setUpdatedAt(LocalDateTime.now().toString());
    }

    @Test
    @Order(11)
    public void testInsert11() {
        // Test Insert operation
        dictionaryMapper.insert(testWord);
        assertNotNull(testWord.getId(), "Word ID should be generated after insert");
        log.info("Test word inserted with ID: {}", testWord.getId());
    }

    @Test
    @Order(12)
    public void testInsert12() {
        // Test Insert operation
        dictionaryMapper.insert(testWord);
        assertNotNull(testWord.getId(), "Word ID should be generated after insert");
        log.info("Test word inserted with ID: {}", testWord.getId());
    }

    @Test
    @Order(21)
    public void testSelectByWord() {
        // Test Select operation by word
        dictionaryMapper.insert(testWord);
        QueryWrapper<DictionaryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", "test");
        // Insert test word first
        DictionaryEntity fetchedWord = dictionaryMapper.selectOne(queryWrapper);
        assertNotNull(fetchedWord, "The word should be found");
        assertEquals("test", fetchedWord.getWord(), "The word should match the inserted word");
    }

    @Test
    @Order(31)
    public void testUpdate() {
        // Test Update operation
        dictionaryMapper.insert(testWord);  // Insert test word first
        testWord.setEnglishDefinition("Updated definition");
        testWord.setUpdatedAt(LocalDateTime.now().toString());
        dictionaryMapper.updateById(testWord);
        QueryWrapper<DictionaryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", "test");
        DictionaryEntity updatedWord = dictionaryMapper.selectOne(queryWrapper);
        assertEquals("Updated definition", updatedWord.getEnglishDefinition(),
            "The English definition should be updated");
    }

    @Test
    @Order(41)
    public void testDelete() {
        // Test Delete operation
        dictionaryMapper.insert(testWord);  // Insert test word first
        dictionaryMapper.deleteById(testWord.getId());
        QueryWrapper<DictionaryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", "test");
        DictionaryEntity deletedWord = dictionaryMapper.selectOne(queryWrapper);
        assertNull(deletedWord, "The word should be deleted and not found");
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        dictionaryMapper.deleteById(testWord.getId());
    }
}
