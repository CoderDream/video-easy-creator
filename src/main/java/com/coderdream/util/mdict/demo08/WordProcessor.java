package com.coderdream.util.mdict.demo08;

import com.coderdream.entity.DictionaryEntry;
import com.coderdream.util.cd.CdDateTimeUtils;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.mdict.Mdict4jUtil2;
import java.util.*;
import java.util.concurrent.*;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WordProcessor {

    // 假设有一个函数来读取文本并返回单词列表
    public static List<String> readTextFile(String resourcePath) {

//        // 创建新的字典对象
//        String filename = "1-3500.txt";
//        filename = "total.txt";
//        String resourcePath = "classpath:13500/" + filename;
        // 读取文件内容
        List<String> list = CdFileUtil.readFileContent(resourcePath);
        list = list.stream()
            .limit(5000)
            .toList();
        return list;// Arrays.asList("apple", "banana", "cherry", "date"); // 模拟返回的单词列表
    }

    // 假设有一个函数来获取单词的详情
    public static DictionaryEntry getDictionaryEntry(String word) {

//        DictionaryEntry dictionaryEntry = null;
//
//        try {
//            dictionaryEntry = / Thread.sleep(100); // 模拟查询过程的耗时
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        return Mdict4jUtil2.genDictionaryEntry(word);
    }

    // 模拟批量写入数据库的方法
    public static void batchInsertToDatabase(List<DictionaryEntry> dictionaryEntries) {
        System.out.println("Batch inserting the following word details into the database:");
        for (DictionaryEntry detail : dictionaryEntries) {
            System.out.println(detail);
        }
    }

    // 批量插入 MyBatis-Plus 实现
    public static void batchInsert(List<DictionaryEntry> dictionaryEntries, IService<DictionaryEntry> wordDetailService) {
        int batchSize = 500;  // 每次插入 500 个
        for (int i = 0; i < dictionaryEntries.size(); i += batchSize) {
            int end = Math.min(i + batchSize, dictionaryEntries.size());
            List<DictionaryEntry> batchList = dictionaryEntries.subList(i, end);
//            wordDetailService.saveBatch(batchList);  // MyBatis-Plus 的批量插入
        }
    }

    // 主函数，使用线程池并发处理
    public static void processWords(String filePath, IService<DictionaryEntry> wordDetailService) throws InterruptedException, ExecutionException {
        List<String> words = readTextFile(filePath);

        int numThreads = 16; // 假设使用 16 个线程
        ExecutorService executorService = new ThreadPoolExecutor(
                numThreads, numThreads * 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy()
        );

        List<Future<DictionaryEntry>> futures = new ArrayList<>();
        for (String word : words) {
            futures.add(executorService.submit(() -> {
                return getDictionaryEntry(word);
            }));
        }

        List<DictionaryEntry> dictionaryEntries = new ArrayList<>();
        for (Future<DictionaryEntry> future : futures) {
            dictionaryEntries.add(future.get());
        }

        // 批量插入数据库
        batchInsert(dictionaryEntries, wordDetailService);

        executorService.shutdown();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
//            String filePath = "path/to/your/text/file.txt";
            // 创建新的字典对象
            String filename = "1-3500.txt";
            filename = "total.txt";
            String resourcePath = "classpath:13500/" + filename;
            // 假设有 MyBatis-Plus 注入的服务
            IService<DictionaryEntry> wordDetailService = null;
            processWords(resourcePath, wordDetailService);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        log.info("Elapsed time: {} ms", elapsedTime);
        log.error("耗时{}。", CdDateTimeUtils.genMessage(elapsedTime));
    }
}
