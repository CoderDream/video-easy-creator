package com.coderdream.thread;

import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolExecutorNote {

    static class MyTask implements Runnable {

        private String name;

        public MyTask(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            Thread thread = Thread.currentThread();
            return thread.getName() + " ：" + name;
        }

        @Override
        public void run() {
            Random random = new Random();
            try {
                System.out.println("任务" + name + "开始执行");
                Thread.sleep(random.nextInt(1000) + 2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " 任务中断");
            }

            System.err.println("任务" + name + "执行完毕");
        }
    }

    public static void main(String[] args) {
        // 1. 创建线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            2, 4, 3, java.util.concurrent.TimeUnit.MILLISECONDS,
            new java.util.concurrent.LinkedBlockingQueue<Runnable>(3));
        // 2. 提交任务
        for (int i = 0; i < 6; i++) {
            pool.execute(new MyTask("worker_" + i));
        }


        // 3. 关闭线程池
        pool.shutdown();
    }
}
