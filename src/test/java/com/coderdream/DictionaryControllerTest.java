package com.coderdream;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.coderdream.entity.Dictionary;
import com.coderdream.service.DictionaryService;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback(false)
@ExtendWith(SpringExtension.class)
public class DictionaryControllerTest {

    @Autowired
    private DictionaryService dictionaryService;

    private Dictionary dictionary;

    private final String BASE_URL = "http://localhost:8080/api/dictionary"; // 假设Spring Boot默认端口8080

    @BeforeEach
    public void setUp() {
        // 初始化 Dictionary 对象
        dictionary = new Dictionary();
        dictionary.setWord("hello");
        dictionary.setEnglishDefinition("A greeting or expression of goodwill.");
        dictionary.setChineseDefinition("一个问候或表示友好的表达");
        dictionary.setSource("Oxford Dictionary");
        dictionary.setCollinsStar(5);
        dictionary.setIeltsLevel("B2");
        dictionary.setCocaFrequency(1000);
        dictionary.setReserved01("reserved text 1");
        dictionary.setReserved02("reserved text 2");
        dictionary.setReserved03("reserved text 3");
        dictionary.setReserved04("reserved text 4");
        dictionary.setReserved05("reserved text 5");
        dictionary.setReserved06(1);
        dictionary.setReserved07(2);
        dictionary.setReserved08(3);
        dictionary.setReserved09(4);
        dictionary.setReserved10(5);

        // 使用服务层保存数据（或者你也可以直接使用Controller层进行调用）
        dictionaryService.saveDictionary(dictionary);
    }

    @Test
    @Order(11)
    public void testAddDictionary() {
        // 创建新的字典对象
        Dictionary newDictionary = new Dictionary();
        newDictionary.setWord("world");
        newDictionary.setEnglishDefinition("The earth and all its countries and peoples.");
        newDictionary.setChineseDefinition("地球及其所有国家和人民");
        newDictionary.setSource("Cambridge Dictionary");
        newDictionary.setCollinsStar(4);
        newDictionary.setIeltsLevel("B1");
        newDictionary.setCocaFrequency(2000);
        newDictionary.setReserved01("reserved text 6");

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


        JSONObject json = new JSONObject(newDictionary);

//        post.setHeader("Content-Type", "application/json");
//        String response = HttpUtil.post(BASE_URL + "/add", paramMap);
//        System.out.println(response);
        String response = com.coderdream.util.mdict.HttpUtil.sendPost(BASE_URL + "/add", json);
//        sendPost(BASE_URL + "/add", paramMap);

        // 断言返回值是否包含期望的成功信息
        assertTrue(response.contains("Dictionary entry added successfully!"));

        // 查询字典，验证是否插入成功
        Dictionary inserted = dictionaryService.getDictionaryByWord("world");
        assertNotNull(inserted);
        assertEquals("world", inserted.getWord());
    }

    @Test
    @Order(21)
    public void testGetAllDictionaries() {
        // 使用 Hutool 发起 GET 请求，模拟获取所有字典条目
        String response = HttpUtil.get(BASE_URL + "/list");

        // 断言返回的内容包含字典中的数据
        assertTrue(response.contains("hello"));
        assertTrue(response.contains("A greeting or expression of goodwill."));
    }

    @Test
    @Order(31)
    public void testGetDictionaryByWord() {
        // 使用 Hutool 发起 GET 请求，模拟按单词查询字典条目
        String response = HttpUtil.get(BASE_URL + "/search?word=hello");

        // 断言返回的数据包含正确的字典条目
        assertTrue(response.contains("hello"));
        assertTrue(response.contains("A greeting or expression of goodwill."));
    }

    @Test
    @Order(41)
    public void testDeleteDictionary() {
        // 使用 Hutool 发起 DELETE 请求，模拟删除字典条目
//        String response = HttpUtil.delete(BASE_URL + "/delete/" + dictionary.getId());

        String url = "http://localhost:8080/api/dictionary/delete/"  + dictionary.getId(); // 假设要删除 id 为 1 的字典条目
        String response = HttpRequest.delete(url).execute().body();  // 发送 DELETE 请求

        System.out.println(response);  // 输出响应内容

        // 断言返回值包含期望的删除成功信息
        assertTrue(response.contains("Dictionary entry deleted successfully!"));

        // 查询字典，验证删除是否成功
        Dictionary deleted = dictionaryService.getDictionaryByWord("hello");
        assertNull(deleted);
    }
}
