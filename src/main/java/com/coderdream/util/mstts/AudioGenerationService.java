//package com.coderdream.util.mstts;
//
//import com.google.common.util.concurrent.ThreadFactoryBuilder; // 使用 Guava 的 ThreadFactoryBuilder 简化命名
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//public class AudioGenerationService {
//
//    // --- 线程池配置 ---
//
//    // 核心线程数：根据 CPU 核心数或业务特性调整
//    private static final int CORE_POOL_SIZE = Math.max(2, Runtime.getRuntime().availableProcessors());
//    // 最大线程数：允许创建的最大线程数
//    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;
//    // 线程空闲时间：非核心线程空闲多久后被回收
//    private static final long KEEP_ALIVE_TIME = 60L;
//    // 时间单位
//    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
//    // 工作队列容量：使用有界队列防止 OOM
//    private static final int QUEUE_CAPACITY = 200;
//    // 线程工厂：设置线程名称前缀，方便排查问题
//    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
//            .setNameFormat("audio-gen-pool-%d").build();
//    // 拒绝策略：当队列满且达到最大线程数时，抛出异常（也可以选择其他策略如 CallerRunsPolicy）
//    private static final ThreadPoolExecutor.AbortPolicy rejectionHandler = new ThreadPoolExecutor.AbortPolicy();
//
//    // --- 创建线程池实例 ---
//    // 务必使用 ThreadPoolExecutor 显示创建
//    private static final ExecutorService executorService = new ThreadPoolExecutor(
//            CORE_POOL_SIZE,
//            MAXIMUM_POOL_SIZE,
//            KEEP_ALIVE_TIME,
//            TIME_UNIT,
//            new ArrayBlockingQueue<>(QUEUE_CAPACITY), // 使用有界队列
//            namedThreadFactory,                       // 指定线程工厂
//            rejectionHandler                          // 指定拒绝策略
//    );
//
//    // --- 重试配置 ---
//    private static final int MAX_RETRIES = 10; // 最大重试次数
//    private static final long RETRY_DELAY_MS = 1000; // 重试间隔（毫秒）
//
//    /**
//     * 提交音频生成任务到线程池，包含重试逻辑
//     *
//     * @param textList      要转换为语音的文本列表
//     * @param audioFileName 输出的音频文件完整路径和名称
//     * @param lang          语言代码 (例如 "cn", "en")
//     */
//    public void submitAudioGenerationTask(final List<String> textList, final String audioFileName, final String lang) {
//        log.info("提交音频生成任务: 文件名={}, 语言={}", audioFileName, lang);
//
//        Runnable taskWithRetry = () -> {
//            int attempt = 0;
//            boolean success = false;
//            while (attempt < MAX_RETRIES && !success) {
//                attempt++;
//                try {
//                    log.info("尝试生成音频 (第 {} 次): {}", attempt, audioFileName);
//                    // 调用你的静态工具方法
//                    MsttsAudioUtil.genAudioFile(textList, audioFileName, lang);
//
//                    // **重要**: 你的 MsttsAudioUtil.genAudioFile 方法内部似乎捕获了所有异常并仅记录日志。
//                    // 这意味着这里的 try-catch 可能无法捕获到业务逻辑上的失败（比如API返回错误但没抛异常）。
//                    // 为了让重试机制有效工作，MsttsAudioUtil.genAudioFile 应该在发生可重试错误时抛出异常。
//                    // 如果无法修改 MsttsAudioUtil，你可能需要在调用后添加检查逻辑（如检查文件是否生成成功）。
//                    // 这里我们假设调用成功即完成。
//
//                    log.info("音频生成成功: {}", audioFileName);
//                    success = true; // 标记成功，退出循环
//
//                } catch (Exception e) { // 捕获 MsttsAudioUtil 可能抛出的未捕获异常
//                    log.error("音频生成失败 (第 {} 次): {}, 错误: {}", attempt, audioFileName, e.getMessage(), e);
//
//                    if (attempt >= MAX_RETRIES) {
//                        log.error("已达到最大重试次数 ({})，放弃任务: {}", MAX_RETRIES, audioFileName);
//                        // 这里可以根据需要进行额外处理，比如记录到失败队列、发送告警等
//                    } else {
//                        try {
//                            log.warn("将在 {} ms后重试 ({}/{})", RETRY_DELAY_MS, attempt, MAX_RETRIES);
//                            Thread.sleep(RETRY_DELAY_MS);
//                        } catch (InterruptedException ie) {
//                            log.warn("重试等待被中断，可能服务正在关闭: {}", audioFileName);
//                            Thread.currentThread().interrupt(); // 重新设置中断状态
//                            break; // 中断则不再重试
//                        }
//                    }
//                }
//            }
//        };
//
//        try {
//            executorService.execute(taskWithRetry); // 提交任务到线程池
//        } catch (Exception e) {
//            // 这个 catch 主要捕获 RejectedExecutionException，当线程池和队列都满时
//            log.error("无法提交音频生成任务到线程池 (可能已满): {}, 错误: {}", audioFileName, e.getMessage());
//            // 根据业务需要处理提交失败的情况，例如：记录失败、稍后重试提交、降级处理等
//        }
//    }
//
//    /**
//     * 在应用程序关闭时，优雅地关闭线程池
//     */
//    public void shutdown() {
//        log.info("开始关闭音频生成线程池...");
//        executorService.shutdown(); // 不再接受新任务，等待现有任务完成
//        try {
//            // 等待一段时间让现有任务完成
//            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//                log.warn("线程池未在60秒内完全关闭，尝试强制关闭...");
//                executorService.shutdownNow(); // 尝试取消正在执行的任务
//                // 再次等待
//                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
//                    log.error("线程池未能正常关闭");
//                }
//            }
//        } catch (InterruptedException ie) {
//            log.error("关闭线程池时被中断", ie);
//            // (重新) 发出中断请求给当前线程
//            executorService.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//        log.info("音频生成线程池已关闭。");
//    }
//
//    // --- 示例用法 ---
////    public static void main(String[] args) {
////        AudioGenerationService service = new AudioGenerationService();
////
////        // 模拟提交多个任务
////        List<String> sampleText1 = List.of("你好，世界！", "这是第一个测试。");
////        service.submitAudioGenerationTask(sampleText1, "output1_cn.wav", "cn");
////
////        List<String> sampleText2 = List.of("Hello, world!", "This is the second test.");
////        service.submitAudioGenerationTask(sampleText2, "output2_en.wav", "en");
////
////        // ... 提交更多任务
////
////        // 在应用程序退出前，需要调用 shutdown 方法
////        // 实际项目中，这通常放在 Spring 的 @PreDestroy 方法或 main 方法的 finally 块，
////        // 或者使用 Runtime.getRuntime().addShutdownHook()
////        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
////            log.info("JVM 关闭钩子触发，开始关闭服务...");
////            service.shutdown();
////            log.info("服务关闭完成。");
////        }));
////
////        // 保持主线程运行一段时间，以便观察线程池活动（仅为演示）
////        try {
////            log.info("主线程等待中... 按 Ctrl+C 退出。");
////            Thread.sleep(Long.MAX_VALUE);
////        } catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////        }
////    }
//}
