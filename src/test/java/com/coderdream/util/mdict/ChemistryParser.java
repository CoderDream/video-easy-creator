package com.coderdream.util.mdict;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ChemistryParser {
    public static void main(String[] args) {

        // 指定 HTML 文件地址，加载文档
        String fileName = "word05.html";
        JSoup02Test jSoup02Test = new JSoup02Test();
        File file = jSoup02Test.getTestFile(fileName);

        Document document = null;
        try {
            document = Jsoup.parse(file, "utf-8");
//            getSentences(document);

            // 提取第1条解释
            Element sense1 = document.select("li.sense#chemistry_sng_1").first();
            String def1Eng = sense1.select(".def").text();
            String def1Chn = sense1.select(".chn.simple").text();

            // 提取第2条解释
            Element sense2 = document.select("li.sense#chemistry_sng_2").first();
            String def2Eng = sense2.select(".def").text();
            String def2Chn = sense2.select(".chn.simple").text();

            // 提取第3条解释
            Element sense3 = document.select("li.sense#chemistry_sng_3").first();
            String def3Eng = sense3.select(".def").text();
            String def3Chn = sense3.select(".chn.simple").text();

            // 输出结果
            System.out.println("1. " + def1Eng + " " + def1Chn);
            System.out.println("2. " + def2Eng + " " + def2Chn);
            System.out.println("3. " + def3Eng + " " + def3Chn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        // 假设你已经获取了 HTML 内容，这里用变量 htmlContent 存储
//        String htmlContent = "<html> ... </html>"; // 替换为你的 HTML 内容
//
//        // 解析 HTML 内容
//        Document document = Jsoup.parse(htmlContent);
//
//        // 提取第1条解释
//        Element sense1 = document.select("li.sense#chemistry_sng_1").first();
//        String def1Eng = sense1.select(".def").text();
//        String def1Chn = sense1.select(".chn.simple").text();
//
//        // 提取第2条解释
//        Element sense2 = document.select("li.sense#chemistry_sng_2").first();
//        String def2Eng = sense2.select(".def").text();
//        String def2Chn = sense2.select(".chn.simple").text();
//
//        // 提取第3条解释
//        Element sense3 = document.select("li.sense#chemistry_sng_3").first();
//        String def3Eng = sense3.select(".def").text();
//        String def3Chn = sense3.select(".chn.simple").text();
//
//        // 输出结果
//        System.out.println("1. " + def1Eng + " " + def1Chn);
//        System.out.println("2. " + def2Eng + " " + def2Chn);
//        System.out.println("3. " + def3Eng + " " + def3Chn);
    }
}
