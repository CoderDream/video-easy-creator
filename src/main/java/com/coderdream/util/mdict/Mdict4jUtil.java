package com.coderdream.util.mdict;


import com.coderdream.entity.DictionaryEntry;
import com.coderdream.util.CdDateTimeUtils;
import com.coderdream.util.mdict.demo05.JsoupParser;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mdict4jUtil {


    public static void main(String[] args) throws Exception {
        //        String word = "a realistic possibility";
        //        word = "math";
        //        getWordDetail(word, "cambridge");
        //        System.out.println("===============================");
        //        getWordDetail(word, "oaldpe");
        //        System.out.println("===============================");
        //        getWordDetail(word, "maldpe");
        //        System.out.println("===============================");
        //        getWordDetail(word, "c8");
        //        System.out.println("===============================");
        //        getWordDetail(word, "");
        // hangry

//        List<String> strings = FileUtil.readLines("D:\\Download\\20000words.txt", StandardCharsets.UTF_8);
//
//        int i = 0;
//        int index = 10000;
//        index = 5;
//        for (String word : strings) {
//            String[] split = word.split("\t");
//            if (split.length > 1) {
//                word = split[1].trim();
//                log.info("{}", word);
//                Mdict4jDemo.getWordDetail(word, "collins");
//                if (i >= index) {
//                    break;
//                }
//                i++;
//            }
//        }

        System.out.println("===============================");
        List<String> list = Arrays.asList("hone", "demographic");
//
//        list = Arrays.asList("appliance");
        for (String word : list) {
//            System.out.println(    Mdict4jUtil.getWordDetail(word, "oald"));;
            System.out.println(    Mdict4jUtil.genDictionaryEntry(word));
//            Mdict4jUtil.getDictInfo(word);

           // DictionaryEntry genDictionaryEntry(String word)
        }
    }

    // a realistic possibility

    public static HtmlContentBean getWordDetail(String word, String dictType) {

        String mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";

        switch (dictType) {
            case "cambridge":
                mdxFile = "E:\\BaiduPan\\0002_词典共享\\剑桥在线英汉双解词典完美版\\cdepe.mdx"; // 剑桥在线英汉双解词典完美版 400MB
                break;
            case "oaldpe":
                mdxFile = "E:\\BaiduPan\\0002_词典共享\\牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx"; // 74MB
                break;
            case "maldpe":
                mdxFile = "E:\\BaiduPan\\0002_词典共享\\韦氏高阶英汉双解词典2019完美版\\maldpe.mdx"; // 28MB
                break;

            case "c8":
                mdxFile = "E:\\BaiduPan\\好用的词典~\\牛津高阶8简体spx\\牛津高阶8简体.mdx"; // 28MB
                break;   //
            case "collins":
                mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
                break;   //
            case "oald":
                mdxFile = "D:\\Download\\oald.mdx";
                break;
            default:
                mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
                break;
        }
        MDictDictionary dictionary = null;
        List<Entry<String, String>> list = null;
        try {
            dictionary = MDictDictionary.loadDictionary(mdxFile);
            list = dictionary.readArticles(word);
        } catch (MDException e) {
            throw new RuntimeException(e);
        }

        HtmlContentBean bean = new HtmlContentBean();

        for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

//            System.out.println(entry.getKey());
            String html = entry.getValue();
//            System.out.println(html);
            bean = HtmlParser.parseHtml(html);
//            System.out.println(bean);
        }

//        list = dictionary.readArticlesPredictive(word);

//        for (Map.Entry<String, String> entry : list) {
////            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());
//
//            System.out.println(entry.getKey());
//            String html = entry.getValue();
////            System.out.println(html);
//            bean = HtmlParser.parseHtml(html);
//            System.out.println(bean);
//        }

        return bean;
    }


    public static List<DictionaryEntry> genDictionaryEntryList(List<String> wordList) {
        List<DictionaryEntry> list = new ArrayList<>();
        DictionaryEntry entry = null;
        for (String word : wordList) {
            entry = genDictionaryEntry(word);
            list.add(entry);
        }

        return list;
    }

    public static List<DictionaryEntry> genDictionaryEntryList(List<String> wordList, Integer startCocaRank) {
        long startTime = System.currentTimeMillis();
        log.info("genDictionaryEntryList: {}", wordList.size());
        List<DictionaryEntry> list = new ArrayList<>();
        DictionaryEntry entry = null;
        for (String word : wordList) {
            entry = genDictionaryEntry(word);
            if (entry != null) {
                entry.setCocaRank(++startCocaRank);
                entry.setCreatedAt(new Date());
                list.add(entry);
            } else {
                log.error("{} not found", word);
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms");
        log.info("Elapsed time: {} ms", elapsedTime);
        log.error("genDictionaryEntryList 本次记录条数{}, 耗时{}。", wordList.size(), CdDateTimeUtils.genMessage(elapsedTime));

        return list;
    }

    public static DictionaryEntry genDictionaryEntry(String word) {
        HtmlContentBean htmlContentBean = Mdict4jUtil.getWordDetail(word, "oald");
        String html = htmlContentBean.getRawHtml();
        DictionaryEntry entry = JsoupParser.parseHtml(html);
        return entry;
    }

    public static void getDictInfo(String word) throws Exception {
        System.out.println("Hello Mdict4j!");

        Path dictionaryPath = Paths.get("foo.mdx");
        String mdxFile = "D:\\Download\\oald.mdx";
//        mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";
        MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);

//        if (dictionary.isMDX()) {
//            System.out.println("loaded file is .mdx");
//        }
        if (StandardCharsets.UTF_8.equals(dictionary.getEncoding())) {
            System.out.println("MDX file encoding is UTF-8");
        }
        if (dictionary.isHeaderEncrypted()) {
            System.out.println("MDX file is encrypted.");
        }
        if (dictionary.isIndexEncrypted()) {
            System.out.println("MDX index part is encrypted.");
        }
        System.out.println(dictionary.getMdxVersion());
        System.out.println(dictionary.getFormat());
//        System.out.printf("MDX version: %d, format: %s", dictionary.getMdxVersion(), dictionary.getFormat());
        System.out.println(dictionary.getCreationDate());
        System.out.println(dictionary.getTitle());
        System.out.println(dictionary.getDescription());

        List<Entry<String, String>> list = dictionary.readArticles(word);
        System.out.println("list1:" + list.size());
        HtmlContentBean bean = new HtmlContentBean();

        for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

            System.out.println(entry.getKey());
            String html = entry.getValue();
//            System.out.println(html);
            bean = HtmlParser.parseHtml(html);
            System.out.println("bean1:" + bean);
        }

        list = dictionary.readArticlesPredictive(word);
        System.out.println("list2:" + list.size());
        for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

            System.out.println(entry.getKey());
            String html = entry.getValue();
//            System.out.println(html);
            bean = HtmlParser.parseHtml(html);
            System.out.println("bean2:" + bean);
        }

//        return bean;

    }

}
