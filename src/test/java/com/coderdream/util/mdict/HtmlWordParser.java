package com.coderdream.util.mdict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Vocabulary {
    private String word;
    private String pos;
    private String phonetic;
    private String definition;
    private List<String> englishSentences;
    private String chineseSimplifiedTranslation;
    private String chineseTraditionalTranslation;

    public Vocabulary(String word, String pos, String phonetic, String definition,
                       List<String> englishSentences, String chineseSimplifiedTranslation,
                       String chineseTraditionalTranslation) {
        this.word = word;
        this.pos = pos;
        this.phonetic = phonetic;
        this.definition = definition;
        this.englishSentences = englishSentences;
        this.chineseSimplifiedTranslation = chineseSimplifiedTranslation;
        this.chineseTraditionalTranslation = chineseTraditionalTranslation;
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getDefinition() {
        return definition;
    }

    public List<String> getEnglishSentences() {
        return englishSentences;
    }

    public String getChineseSimplifiedTranslation() {
        return chineseSimplifiedTranslation;
    }

    public String getChineseTraditionalTranslation() {
        return chineseTraditionalTranslation;
    }
}

public class HtmlWordParser {
    public static Vocabulary parseWord(String filePath) {
        try {
            // 读取 HTML 文件并解析为 Document 对象
            Document doc = Jsoup.parse(new File(filePath), "UTF-8");

            // 提取词汇信息
            Element entry = doc.selectFirst("#alchemist");
            if (entry!= null) {
                String word = entry.selectFirst(".headword").text();
                String pos = entry.selectFirst(".pos").text();
                String phonetic = entry.select(".phon").first().text();
                String definition = entry.selectFirst(".def").text();

                // 提取例句
                Elements exampleElements = entry.select(".exText span.x");
                List<String> englishSentences = new ArrayList<>();
                for (Element exampleElement : exampleElements) {
                    englishSentences.add(exampleElement.text());
                }

//                // 提取简体中文翻译，先检查是否为 null
//                Element simplifiedTranslationElement = entry.selectFirst(".chn.simple");
//                String chineseSimplifiedTranslation = simplifiedTranslationElement!= null? simplifiedTranslationElement.text() : "";
//
//                // 提取繁体中文翻译，先检查是否为 null
//                Element traditionalTranslationElement = entry.selectFirst(".chn.traditional");
//                String chineseTraditionalTranslation = traditionalTranslationElement!= null? traditionalTranslationElement.text() : "";

                // 精确选取简体中文翻译元素
                Element simplifiedTranslationElement = entry.selectFirst(".sensetop.chn.simple");
                String chineseSimplifiedTranslation = simplifiedTranslationElement!= null? simplifiedTranslationElement.text() : "";
                Element traditionalTranslationElement = entry.selectFirst(".sensetop.chn.traditional");
                String chineseTraditionalTranslation = traditionalTranslationElement!= null? traditionalTranslationElement.text() : "";

                return new Vocabulary(word, pos, phonetic, definition, englishSentences,
                        chineseSimplifiedTranslation, chineseTraditionalTranslation);
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String folderPath = "D:\\04_GitHub\\video-easy-creator\\src\\test\\java\\com\\coderdream\\util\\mdict\\";
        String fileName = "word06.html";

        Vocabulary vocabulary = parseWord(folderPath + File.separator + fileName);
        if (vocabulary!= null) {
            System.out.println("词汇: " + vocabulary.getWord());
            System.out.println("词性: " + vocabulary.getPos());
            System.out.println("音标: " + vocabulary.getPhonetic());
            System.out.println("英文释义: " + vocabulary.getDefinition());
            System.out.println("英文例句:");
            for (String sentence : vocabulary.getEnglishSentences()) {
                System.out.println(sentence);
            }
            System.out.println("简体中文翻译: " + vocabulary.getChineseSimplifiedTranslation());
            System.out.println("繁体中文翻译: " + vocabulary.getChineseTraditionalTranslation());
        } else {
            System.out.println("未找到词汇信息");
        }
    }
}
