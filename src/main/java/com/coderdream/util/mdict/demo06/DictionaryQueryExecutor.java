package com.coderdream.util.mdict.demo06;

import java.io.*;
import java.util.concurrent.*;
import org.springframework.util.ResourceUtils;

public class DictionaryQueryExecutor {
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

        // 读取 TXT 文件并将每个单词提交给线程池
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String word;
            while ((word = reader.readLine()) != null) {
                // 为每个单词创建任务并提交给线程池
                DictionaryQueryTask task = new DictionaryQueryTask(word, dictionaryService);
                executor.submit(task);
            }
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
