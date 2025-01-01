package com.coderdream.util.mdict.demo07;

import cn.hutool.core.collection.CollectionUtil;
import com.coderdream.entity.DictionaryEntry;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.mdict.Mdict4jUtil;
import com.coderdream.util.mdict.Mdict4jUtil2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CoderDream
 */
public class MultiThreadDownloadAppleSrtEnExecutor {

    public static Integer POOL_SIZE = 50;

    public static ConcurrentLinkedDeque<String> urlEnList = new ConcurrentLinkedDeque<>();


    private Integer corePoolSize = POOL_SIZE;

    private Integer maximumPoolSize = POOL_SIZE;

    private Integer keepAliveTime = 10;

    private static long startTime;

    private TimeUnit unit = TimeUnit.MILLISECONDS;

    private BlockingDeque workQueue = new LinkedBlockingDeque();

    public static boolean isFinish = false;

    public static List<DictionaryEntry> subtitleBaseEntityListEn = new ArrayList<>();

    private RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    };

    public ThreadPoolExecutor coreThreadPool = new ThreadPoolExecutor(corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        handler);

    public void initTestArr() {

        Integer SIZE = 964;

        // 先试前10个
        SIZE = 50;

        SIZE = 964; // TODO

//        SIZE = 1074; // TODO

//        String urlBase = "https://events-delivery.apple.com/1505clvgxdwlbjrjhxtjdgcdxaiabvuf/vod_main_BveVQvhWftXzpUakjHjEUkbmUYLbRdcV/";


        int startEn = 0;

        // 创建新的字典对象
        String filename = "1-3500.txt";
        filename = "total.txt";
        String resourcePath = "classpath:13500/" + filename;
        // 读取文件内容
        List<String> list = CdFileUtil.readFileContent(resourcePath);
        list = list.stream()
            .limit(100)
            .toList();

        List<String> urlEnListInit = list;// GetSrtUtil.genUrlEnList(startEn, SIZE);

        urlEnList.addAll(urlEnListInit);
    }

    public class MyThread extends Thread {

        @Override
        public void run() {

            while (!urlEnList.isEmpty()) {
//                System.out.println(getName() + "start");
                long startTime = System.currentTimeMillis();
                String urlEn = urlEnList.pop();
               // List<DictionaryEntry> subtitleBaseEntitiesEn = GetSrtUtil.m1(urlEn);
//                System.out.println("SIZE##" + subtitleBaseEntitiesCn.size() + ":" + urlEn);+
                subtitleBaseEntityListEn.add(Mdict4jUtil2.genDictionaryEntry(urlEn));

//                System.out.println("Thread" + getName() + "  " + downloadInfoEntityList.pop());
                long period = System.currentTimeMillis() - startTime;
//                System.out.println("耗时毫秒数：\t" + period);
            }

            if (coreThreadPool.getActiveCount() == 1) {
                coreThreadPool.shutdown();
                isFinish = true;
//                long current = System.currentTimeMillis();
//                long period = current - startTime;
//                System.out.println("总耗时毫秒数：\t" + period);
            }
        }
    }

    public void printByMulThread() {
        for (int i = 0; i < POOL_SIZE; i++) {
            MyThread newThread = new MyThread();
            coreThreadPool.execute(newThread);
        }
    }

    public void printT() {
        Set<String> timeSet = new TreeSet<>();
        Map<String, DictionaryEntry> subtitleBaseEntityMapEn = new HashMap<>();
        DictionaryEntry tempDictionaryEntry;
        String timeStr;
        String subtitle;

        // 处理英文字幕
//        System.out.println("####");
        if (CollectionUtil.isNotEmpty(subtitleBaseEntityListEn)) {

            // TODO
            for (DictionaryEntry subtitleBaseEntity : subtitleBaseEntityListEn) {
//                System.out.println("$$$$$$$En：" + subtitleBaseEntity);
//                if(subtitleBaseEntity == null || StrUtil.isEmpty(subtitleBaseEntity.getTimeStr())) {
//                    break;
//                }
//
//                timeStr = subtitleBaseEntity.getTimeStr().substring(0, 9);
//                subtitle = subtitleBaseEntity.getSubtitle();
//                timeSet.add(timeStr);
//                tempDictionaryEntry = subtitleBaseEntityMapEn.get(timeStr);
//                if (tempDictionaryEntry == null) {
//                    tempDictionaryEntry = new DictionaryEntry();
//                }
//                BeanUtil.copyProperties(subtitleBaseEntity, tempDictionaryEntry);
//                tempDictionaryEntry.setSubtitle(subtitle);// 设置第一字幕为英文文字幕
//                subtitleBaseEntityMapEn.put(timeStr, tempDictionaryEntry);
            }
        }

//        Set<String> sortSet = new TreeSet<>(Comparator.naturalOrder());
//        sortSet.addAll(timeSet);
//        Map<String, DictionaryEntry> subtitleBaseEntityTreeMap = new TableMap<>();
//        int i = 0;
//        for (String timeStrKey : timeSet) {
//            i++;
//            DictionaryEntry temp = new DictionaryEntry();
//            DictionaryEntry tempEn = subtitleBaseEntityMapEn.get(timeStrKey);
//            temp.setSubIndex(i);
//            if (tempEn != null) {
//                temp.setTimeStr(tempEn.getTimeStr());
//                temp.setSubtitle(tempEn.getSubtitle());
//            }
//
//            if (StrUtil.isNotEmpty(temp.getTimeStr())) {
//                System.out.println(temp);
//                subtitleBaseEntityTreeMap.put(timeStrKey, temp);
//            }
//
//        }
//
//        //subtitleBaseEntityTreeMap.putAll(subtitleBaseEntityMap);
//        System.out.println("#@#@#@#@");
//        subtitleBaseEntityTreeMap = Java8Feature.sortByKey(subtitleBaseEntityTreeMap, false);

        // 按照Map的键进行排序
//        Map<String, Integer> sortedMap = subtitleBaseEntityMap.entrySet().stream()
//            .sorted(Map.Entry.comparingByKey())
//            .collect(
//                Collectors.toMap(
//                    Map.Entry::getKey,
//                    Map.Entry::getValue,
//                    (oldVal, newVal) -> oldVal,
//                    LinkedHashMap::new
//                )
//            );
//
//        // 将排序后的Map打印
//        sortedMap.entrySet().forEach(System.out::println);
//        subtitleBaseEntityTreeMap.entrySet().forEach(System.out::println);
//
//        List<String> newList = new ArrayList<>();
//
//        for (String timeStrKey : subtitleBaseEntityTreeMap.keySet()) {
//            DictionaryEntry subtitleBaseEntity = subtitleBaseEntityTreeMap.get(timeStrKey);
//
//            newList.add(subtitleBaseEntity.getSubIndex().toString());
//            newList.add(subtitleBaseEntity.getTimeStr());
//            if (StrUtil.isNotEmpty(subtitleBaseEntity.getSubtitle())) {
//                newList.add(subtitleBaseEntity.getSubtitle());
//            }
//            if (StrUtil.isNotEmpty(subtitleBaseEntity.getSubtitleSecond())) {
//                newList.add(subtitleBaseEntity.getSubtitleSecond());
//            }
//            newList.add("");
////            System.out.println(subtitleBaseEntityMap.get(timeStrKey));
////            System.out.println(timeStrKey);
//        }
//        System.out.println("字幕大小：" + i);
//        String srcFileName = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN) + "_en.srt";
//        // 写文本
//        CdFileUtil.writeToFile(srcFileName, newList);

        // 将排序后的Map打印
//        subtitleBaseEntityTreeMap.keySet().forEach(System.out::println);

    }

    public static void main(String[] args) throws InterruptedException {
        startTime = System.currentTimeMillis();
        MultiThreadDownloadAppleSrtEnExecutor test = new MultiThreadDownloadAppleSrtEnExecutor();
        test.initTestArr();
        test.printByMulThread();
        while (true) {
            Thread.sleep(500);
            if (isFinish) {
                test.printT();
                break;
            }
        }
        long current = System.currentTimeMillis();
        long period = current - startTime;
        System.out.println("总耗时毫秒数：\t" + period);
    }
}

