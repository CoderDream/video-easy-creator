package com.coderdream.util.mdict;


import com.coderdream.util.CdFileUtil;
import io.github.eb4j.mdict.MDictDictionary;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mdict4jDemo {


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
        List<String> list = Arrays.asList("apple","hello", "hangry", "irritability");
//
        list = Arrays.asList("appliance");
        for (String word : list) {
            Mdict4jDemo.getWordDetail(word, "collins");
        }
    }

    // a realistic possibility

    public static HtmlContentBean getWordDetail(String word, String dictType) throws Exception {

        String mdxFile = "D:\\Download\\柯林斯COBUILD高阶英汉双解学习词典.mdx";

        String folderPath =
          CdFileUtil.getResourceRealPath() + File.separatorChar + "dict" + File.separatorChar;

        switch (dictType) {
            case "cambridge":
                mdxFile = folderPath + "cdepe.mdx"; // 剑桥在线英汉双解词典完美版 400MB
                break;
            case "oaldpe":
                mdxFile = folderPath + "oaldpe.mdx"; // 74MB
                break;
            case "maldpe":
                mdxFile = folderPath + "maldpe.mdx"; // 28MB
                break;

            case "c8":
                mdxFile = folderPath + "牛津高阶8简体.mdx"; // 28MB
                break;   //
            case "collins":
                mdxFile = folderPath + "柯林斯COBUILD高阶英汉双解学习词典.mdx";
                break;   //
            default:
                mdxFile = folderPath + "柯林斯COBUILD高阶英汉双解学习词典.mdx";
                break;
        }
        MDictDictionary dictionary = MDictDictionary.loadDictionary(mdxFile);
        List<Entry<String, String>> list = dictionary.readArticles(word);

        HtmlContentBean bean = new HtmlContentBean();

        for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

            System.out.println(entry.getKey());
            String html = entry.getValue();
//            System.out.println(html);
            bean = DictHtmlParserUtil.parseOaldHtml(html);
            System.out.println(bean);
        }

//        list = dictionary.readArticlesPredictive(word);

//        for (Map.Entry<String, String> entry : list) {
////            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());
//
//            System.out.println(entry.getKey());
//            String html = entry.getValue();
////            System.out.println(html);
//            bean = DictHtmlParserUtil.parseHtml(html);
//            System.out.println(bean);
//        }

        return bean;
    }

    public static void getDictInfo(String word) throws Exception {
        System.out.println("Hello Mdict4j!");

        Path dictionaryPath = Paths.get("foo.mdx");
        MDictDictionary dictionary = MDictDictionary.loadDictionary(
            "C:\\Users\\CoderDream\\Downloads\\ABDM\\牛津高阶英汉双解词典（第10版）V3.mdx");

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
            bean = DictHtmlParserUtil.parseOaldHtml(html);
            System.out.println("bean1:" + bean);
        }

        list = dictionary.readArticlesPredictive(word);
        System.out.println("list2:" + list.size());
        for (Entry<String, String> entry : list) {
//            System.out.println("<div><span>%s</span>: %s</div>", entry.getKey(), entry.getValue());

            System.out.println(entry.getKey());
            String html = entry.getValue();
//            System.out.println(html);
            bean = DictHtmlParserUtil.parseOaldHtml(html);
            System.out.println("bean2:" + bean);
        }

//        return bean;

    }

}
