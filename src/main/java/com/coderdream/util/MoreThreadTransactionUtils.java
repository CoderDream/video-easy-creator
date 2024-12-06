package com.coderdream.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程事务处理工具类，用于在多线程环境下执行多个数据库操作并且这些操作要么全部成功，要么全部失败的场景
 */
@Slf4j
public class MoreThreadTransactionUtils {
    /**
     * 多线程事务处理
     * 在多线程环境下执行多个数据库操作并且这些操作要么全部成功，要么全部失败的场景
     *
     * @param platformTransactionManager Spring的事务管理器，用于控制事务的提交和回滚
     * @param taskList                   一个可变数量的Runnable任务，每个任务代表一个数据库操作
     * @return 如果所有任务都成功完成，则返回true；否则返回false
     */
    public static boolean execute(PlatformTransactionManager platformTransactionManager, Runnable... taskList) {
        if (taskList == null || taskList.length == 0) {
            throw new IllegalArgumentException("taskList is empty");
        }
        // 任务数量
        int taskSize = taskList.length;
        // 任务成功数量计数器
        AtomicInteger taskSuccessAccount = new AtomicInteger(0);

        List<Future<?>> taskFutureList = new ArrayList<>(taskSize);

        // 循环屏障，用于让多线程事务一起提交 || 一起回滚
        CyclicBarrier cyclicBarrier = new CyclicBarrier(taskSize);
        int i = 1;
        // 定义了一个线程池，线程池核心线程数大小和任务大小必须一致，因为里面的任务都必须同时去执行，否则会死锁
        ExecutorService executorService = Executors.newFixedThreadPool(taskSize);

        try {
            //使用线程池执行循环处理任务，每个任何会交给线程池中的一个线程执行
            for (Runnable task : taskList) {
                final int taskIndex = i;

                Future<?> future = executorService.submit(() -> {
                    TransactionStatus transactionStatus = null;
                    try {
                        // 使用spring编程式事务，开启事务
                        transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
                        // 执行任务
                        task.run();
                        // 成功数量+1
                        taskSuccessAccount.incrementAndGet();
                        log.debug("task：{} 等待提交事务", taskIndex);
                    } catch (Throwable e) {
                        log.error("task：{}，执行异常，异常原因：{}", taskIndex, e.getMessage());
                    } finally {
                        // 走到这里，会阻塞，直到当前线程池中所有的任务都执行到这个位置后，才会被唤醒，继续向下走
                        try {
                            cyclicBarrier.await();
                        } catch (Exception e) {
                            log.error("cyclicBarrier.await error:{}", e.getMessage(), e);
                        }
                    }

                    if (transactionStatus != null) {
                        // 如果所有任务都成功（successAccount的值等于任务总数），则一起提交事务，如果有任何任务失败，则一起回滚事务
                        if (taskSuccessAccount.get() == taskSize) {
                            // 成功，提交事务
                            log.debug("task：{} 提交事务", taskIndex);
                            platformTransactionManager.commit(transactionStatus);
                        } else {
                            //失败，回滚事务
                            log.debug("task：{} 回滚事务", taskIndex);
                            platformTransactionManager.rollback(transactionStatus);
                        }
                    }
                });
                taskFutureList.add(future);
                i++;
            }

            for (Future<?> future : taskFutureList) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("future.get error:{}", e.getMessage(), e);
                }
            }
        } finally {
            //关闭线程池
            executorService.shutdown();
        }

        //如果所有任务都成功完成，则返回true；否则返回false
        return taskSuccessAccount.get() == taskSize;
    }

}
