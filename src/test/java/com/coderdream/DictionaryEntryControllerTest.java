package com.coderdream;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.coderdream.controller.DictionaryEntryController;
import com.coderdream.entity.DictionaryEntry;
import com.coderdream.entity.ExampleSentence;
import com.coderdream.service.DictionaryEntryService;
import com.coderdream.util.CdDateTimeUtils;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.mdict.Mdict4jUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class DictionaryEntryControllerTest {

    @Autowired
    private DictionaryEntryService dictionaryEntryService;

    @Autowired
    private DictionaryEntryController dictionaryEntryController;

    private DictionaryEntry entry;


    private final String BASE_URL = "http://localhost:8080/api/dictionary-entry"; // 假设Spring Boot默认端口8080

    @BeforeEach
    void setUp() {
        // 初始化一个 DictionaryEntry
        entry = new DictionaryEntry();
        entry.setWord("appliance");
        entry.setSyllable("ap·pli·ance");
        entry.setPartOfSpeech("noun");
        entry.setUkPronunciation("/əˈplaɪəns/");
        entry.setUsPronunciation("/əˈplaɪəns/");
        entry.setDefinition("A machine that is designed to do a particular thing in the home.");
        entry.setChineseDefinition("家用电器");

        ExampleSentence exampleSentence1 = new ExampleSentence();
        exampleSentence1.setEnglishSentence("They sell a wide range of domestic appliances.");
        exampleSentence1.setChineseTranslation("他们出售各种家用电器。");
        exampleSentence1.setPronunciationUk("sound://appliance__gb_1.mp3");
        exampleSentence1.setPronunciationUs("sound://appliance__us_1.mp3");

        ExampleSentence exampleSentence2 = new ExampleSentence();
        exampleSentence2.setEnglishSentence("Modern heating appliances are energy efficient.");
        exampleSentence2.setChineseTranslation("现代加热设备是节能的。");
        exampleSentence2.setPronunciationUk("sound://appliance__gb_2.mp3");
        exampleSentence2.setPronunciationUs("sound://appliance__us_2.mp3");

       List<ExampleSentence>  exampleSentences=    Arrays.asList(exampleSentence1, exampleSentence2);
        // 将 List<ExampleSentence> 转换为 JSONArray
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(exampleSentences));

        // 将 JSONArray 转换为 JSONObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("sentences", jsonArray);
        entry.setExampleSentences(jsonObject);

        // 将 List<String> 转换为 JSONArray
        List<String> collocations = Arrays.asList("electrical appliances", "household appliances");
        JSONArray jsonArrayCollocations = JSONArray.parseArray(JSONArray.toJSONString(collocations));

        // 将 JSONArray 转换为 JSONObject
        JSONObject jsonObjectCollocations = new JSONObject();
        jsonObjectCollocations.set("strings", jsonArrayCollocations);
        entry.setCollocations(jsonObjectCollocations);
    }

    @Test
    @Order(1)
    @Transactional
    void testAddDictionaryEntry() {
//        // 插入数据到数据库
//        DictionaryEntry savedEntry = dictionaryEntryService.save(entry);
//
//        // 验证保存后的数据
//        assertNotNull(savedEntry);
//        assertNotNull(savedEntry.getId());
//        assertEquals("appliance", savedEntry.getWord());

        // 创建新的字典对象
//        DictionaryEntity newDictionary = new DictionaryEntity();
//        newDictionary.setWord("world");
//        newDictionary.setEnglishDefinition("The earth and all its countries and peoples.");
//        newDictionary.setChineseDefinition("地球及其所有国家和人民");
//        newDictionary.setSource("Cambridge DictionaryEntity");
//        newDictionary.setCollinsStar(4);
//        newDictionary.setIeltsLevel("B1");
//        newDictionary.setCocaFrequency(2000);
//        newDictionary.setReserved01("reserved text 6");

//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("word", newDictionary.getWord());
//        paramMap.put("englishDefinition", newDictionary.getEnglishDefinition());
//        paramMap.put("chineseDefinition", newDictionary.getChineseDefinition());
//        paramMap.put("source", newDictionary.getSource());
//        paramMap.put("collinsStar", newDictionary.getCollinsStar());
//        paramMap.put("ieltsLevel", newDictionary.getIeltsLevel());
//        paramMap.put("cocaFrequency", newDictionary.getCocaFrequency());
//        paramMap.put("reserved01", newDictionary.getReserved01());
//        paramMap.put("reserved02", newDictionary.getReserved02());

        // 使用 HuTool 发起 POST 请求，模拟添加字典条目

        JSONObject json = new JSONObject(entry);

//        post.setHeader("Content-Type", "application/json");
//        String response = HttpUtil.post(BASE_URL + "/add", paramMap);
//        System.out.println(response);
        String response = com.coderdream.util.mdict.HttpUtil.sendPost(BASE_URL + "/add", json);
//        sendPost(BASE_URL + "/add", paramMap);

        // 断言返回值是否包含期望的成功信息
        assertTrue(response.contains("DictionaryEntity entry added successfully!"));

        // 查询字典，验证是否插入成功
        DictionaryEntry inserted = dictionaryEntryService.getDictionaryByWord("appliance");
        assertNotNull(inserted);
        assertEquals("appliance", inserted.getWord());
    }

    @Test
    @Order(21)
    @Transactional
    void testInsertOrUpdateBatch() {
//        // 插入数据到数据库
//        DictionaryEntry savedEntry = dictionaryEntryService.save(entry);
//
//        // 验证保存后的数据
//        assertNotNull(savedEntry);
//        assertNotNull(savedEntry.getId());
//        assertEquals("appliance", savedEntry.getWord());

        // 创建新的字典对象
//        DictionaryEntity newDictionary = new DictionaryEntity();
//        newDictionary.setWord("world");
//        newDictionary.setEnglishDefinition("The earth and all its countries and peoples.");
//        newDictionary.setChineseDefinition("地球及其所有国家和人民");
//        newDictionary.setSource("Cambridge DictionaryEntity");
//        newDictionary.setCollinsStar(4);
//        newDictionary.setIeltsLevel("B1");
//        newDictionary.setCocaFrequency(2000);
//        newDictionary.setReserved01("reserved text 6");

//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("word", newDictionary.getWord());
//        paramMap.put("englishDefinition", newDictionary.getEnglishDefinition());
//        paramMap.put("chineseDefinition", newDictionary.getChineseDefinition());
//        paramMap.put("source", newDictionary.getSource());
//        paramMap.put("collinsStar", newDictionary.getCollinsStar());
//        paramMap.put("ieltsLevel", newDictionary.getIeltsLevel());
//        paramMap.put("cocaFrequency", newDictionary.getCocaFrequency());
//        paramMap.put("reserved01", newDictionary.getReserved01());
//        paramMap.put("reserved02", newDictionary.getReserved02());

        // 使用 HuTool 发起 POST 请求，模拟添加字典条目
        List<DictionaryEntry> entryList = Arrays.asList(entry);

//        JSONObject json = new JSONObject(entryList);
        // 将 List<ExampleSentence> 转换为 JSONArray
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(entryList));

        // 将 JSONArray 转换为 JSONObject
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.set("sentences", jsonArray);


//        post.setHeader("Content-Type", "application/json");
//        String response = HttpUtil.post(BASE_URL + "/add", paramMap);
//        System.out.println(response);
        String response = com.coderdream.util.mdict.HttpUtil.sendPost(BASE_URL + "/insertOrUpdateBatch", jsonArray.toJSONString());
//        sendPost(BASE_URL + "/add", paramMap);

        // 断言返回值是否包含期望的成功信息
        assertTrue(response.contains("DictionaryEntity entry list insertOrUpdateBatch successfully!"));

        // 查询字典，验证是否插入成功
        DictionaryEntry inserted = dictionaryEntryService.getDictionaryByWord("appliance");
        assertNotNull(inserted);
        assertEquals("appliance", inserted.getWord());
    }


    @Test
    @Order(22)
    @Transactional
    void testInsertOrUpdateBatch22() {
        long startTime = System.currentTimeMillis();
        // 创建新的字典对象
        String filename = "1-3500.txt";
        filename = "total.txt";
        String resourcePath = "classpath:13500/" + filename;
        // 读取文件内容
        List<String> list = CdFileUtil.readFileContent(resourcePath);
        list = list.stream()
            .limit(10)
            .toList();
        // 生成字典条目列表
        List<DictionaryEntry> entryList = null;// Mdict4jUtil.genDictionaryEntryList(list, 0);

        // 使用 HuTool 发起 POST 请求，模拟添加字典条目
//        List<DictionaryEntry> entryList = Arrays.asList(entry);

//        JSONObject json = new JSONObject(entryList);
        // 将 List<ExampleSentence> 转换为 JSONArray
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(entryList));

        // 将 JSONArray 转换为 JSONObject
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.set("sentences", jsonArray);


//        post.setHeader("Content-Type", "application/json");
//        String response = HttpUtil.post(BASE_URL + "/add", paramMap);
//        System.out.println(response);
        String response = com.coderdream.util.mdict.HttpUtil.sendPost(BASE_URL + "/insertOrUpdateBatch", jsonArray.toJSONString());
//        sendPost(BASE_URL + "/add", paramMap);

        // 断言返回值是否包含期望的成功信息
        assertTrue(response.contains("DictionaryEntity entry list insertOrUpdateBatch successfully!"));

        // 查询字典，验证是否插入成功
//        DictionaryEntry inserted = dictionaryEntryService.getDictionaryByWord("appliance");
//        assertNotNull(inserted);
//        assertEquals("appliance", inserted.getWord());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        log.info("Elapsed time: {} ms", elapsedTime);
        log.error("本次记录条数{}, 耗时{}。", entryList.size(), CdDateTimeUtils.genMessage(elapsedTime));
    }

//    @Test
//    @Order(2)
//    void testGetDictionaryEntryByWord() {
//        // 查询数据库
//        DictionaryEntry result = dictionaryEntryService.getByWord("appliance");
//
//        // 验证返回结果
//        assertNotNull(result);
//        assertEquals("appliance", result.getWord());
//        assertEquals("A machine that is designed to do a particular thing in the home.", result.getDefinition());
//        assertEquals("家用电器", result.getChineseDefinition());
//    }
//
//    @Test
//    @Order(3)
//    void testUpdateDictionaryEntry() {
//        // 查找已经插入的条目
//        DictionaryEntry existingEntry = dictionaryEntryService.getByWord("appliance");
//        assertNotNull(existingEntry);
//
//        // 更新条目
//        existingEntry.setDefinition("Updated definition.");
//        dictionaryEntryService.update(existingEntry);
//
//        // 查找更新后的条目
//        DictionaryEntry updatedEntry = dictionaryEntryService.getByWord("appliance");
//        assertEquals("Updated definition.", updatedEntry.getDefinition());
//    }
//
//    @Test
//    @Order(4)
//    void testDeleteDictionaryEntry() {
//        // 查找并删除条目
//        DictionaryEntry existingEntry = dictionaryEntryService.getByWord("appliance");
//        assertNotNull(existingEntry);
//        dictionaryEntryService.delete(existingEntry.getId());
//
//        // 确认条目已删除
//        DictionaryEntry deletedEntry = dictionaryEntryService.getByWord("appliance");
//        assertNull(deletedEntry);
//    }
}
