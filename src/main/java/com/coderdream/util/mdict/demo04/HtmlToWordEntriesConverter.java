package com.coderdream.util.mdict.demo04;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlToWordEntriesConverter {

    /**
     * 将HTML字符串转换为WordEntry对象的列表
     *
     * @param html HTML字符串
     * @return WordEntry对象的列表
     */
    public static List<WordEntry> convertHtmlToWordEntries(String html) {
        List<WordEntry> wordEntries = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements entries = doc.select("div.collins_en_cn");

        for (Element entry : entries) {
            WordEntry wordEntry = new WordEntry();
            Element caption = entry.selectFirst("div.caption");
            if (caption != null) {
                wordEntry.setTitle(caption.text());
                Elements liElements = entry.select("li");
                List<ExampleSentence> exampleSentences = new ArrayList<>();

                for (int i = 0; i < liElements.size(); i += 2) {
                    ExampleSentence example = new ExampleSentence();
                    example.setEnglish(liElements.get(i).selectFirst("p").text());

                    if (i + 1 < liElements.size()) {
                        example.setChinese(liElements.get(i + 1).selectFirst("p").text());
                    }

                    exampleSentences.add(example);
                }

                wordEntry.setExampleSentences(exampleSentences);
                wordEntries.add(wordEntry);
            }
        }

        return wordEntries;
    }

    // WordEntry 和 ExampleSentence 类与之前相同，这里不再重复
}
