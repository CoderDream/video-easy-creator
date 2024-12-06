package com.coderdream.util.mdict.demo06;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.util.ResourceUtils;

public class DictionaryQueryExecutor2 {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public static void main(String[] args) throws IOException {
        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            new LinkedBlockingDeque<>(), // 阻塞队列
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 创建字典查询服务实例
        DictionaryService dictionaryService = new DictionaryService();
        // 创建新的字典对象
        String filename = "1-3500.txt";
        filename = "total.txt";
        String resourcePath = "classpath:13500/" + filename;

        File file = ResourceUtils.getFile(resourcePath);

        // 创建 BlockingDeque
        BlockingDeque<String> taskQueue = new LinkedBlockingDeque<>();

        // 生产者：读取文件并放入队列
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String word;
                while ((word = reader.readLine()) != null) {
                    taskQueue.put(word);  // 将单词放入队列
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 消费者：从队列中取出单词并查询字典
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        String word = taskQueue.take();  // 从队列中取出任务
                        String result = dictionaryService.queryDictionary(word);  // 查询字典
                        System.out.println("Query result for " + word + ": " + result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 关闭线程池，等待所有任务完成
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

    }
}
