package com.coderdream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.coderdream.entity.Sentence;
import com.coderdream.mapper.SentenceMapper;
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
public class SentenceServiceTest {

    @Autowired
    private SentenceMapper sentenceMapper;

    private Sentence testSentence;

    @BeforeEach
    public void setUp() {
        // Set up a test sentence entry
        testSentence = new Sentence();
        testSentence.setWordId(1L);  // Assuming word_id 1 exists
        testSentence.setEnglishSentence("This is a test sentence.");
        testSentence.setChineseSentence("这是一个测试句子。");
        testSentence.setSource("Unit Test");
        testSentence.setCreatedAt(LocalDateTime.now().toString());
        testSentence.setUpdatedAt(LocalDateTime.now().toString());
    }

    @Test
    @Order(11)
    public void testInsert() {
        // Test Insert operation
        sentenceMapper.insert(testSentence);
        assertNotNull(testSentence.getId(), "Sentence ID should be generated after insert");
    }

    @Test
    @Order(21)
    public void testSelectByWordId() {
        // Test Select operation by wordId using selectOne
        sentenceMapper.insert(testSentence);  // Insert test sentence first

        QueryWrapper<Sentence> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", "test");
        Sentence fetchedSentence = sentenceMapper.selectOne(queryWrapper);  // Fetch using selectOne method
        assertNotNull(fetchedSentence, "The sentence should be found");
        assertEquals("This is a test sentence.", fetchedSentence.getEnglishSentence(), "The English sentence should match");
    }

    @Test
    @Order(31)
    public void testUpdate() {
        // Test Update operation
        sentenceMapper.insert(testSentence);  // Insert test sentence first
        testSentence.setEnglishSentence("Updated test sentence.");
        testSentence.setUpdatedAt(LocalDateTime.now().toString());
        sentenceMapper.updateById(testSentence);

        Sentence updatedSentence = sentenceMapper.selectOne(1L);  // Fetch using selectOne
        assertEquals("Updated test sentence.", updatedSentence.getEnglishSentence(), "The English sentence should be updated");
    }

    @Test
    @Order(41)
    public void testDelete() {
        // Test Delete operation
        sentenceMapper.insert(testSentence);  // Insert test sentence first
        sentenceMapper.deleteById(testSentence.getId());

        Sentence deletedSentence = sentenceMapper.selectOne(1L);  // Fetch using selectOne
        assertNull(deletedSentence, "The sentence should be deleted and not found");
    }

    @AfterEach
    public void tearDown() {
        // Clean up database after each test
        sentenceMapper.deleteById(testSentence.getId());
    }
}
