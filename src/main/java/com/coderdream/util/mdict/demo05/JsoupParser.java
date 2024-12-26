package com.coderdream.util.mdict.demo05;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.coderdream.entity.DictionaryEntry;
import com.coderdream.entity.ExampleSentence;
import com.coderdream.util.mdict.HtmlContentBean;
import com.coderdream.util.mdict.Mdict4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsoupParser {

    public static DictionaryEntry parseHtml(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        Document doc = Jsoup.parse(html);

        DictionaryEntry entry = new DictionaryEntry();

        // 获取单词
        Element wordElement = doc.select("h1.headword").first();
        if (wordElement != null) {
            entry.setWord(wordElement.text());
        } else {
            log.error("单词为空： {}", html);
            return null;
        }

        // 获取音节
//        Element syllableElement = wordElement.attr("syllable");
//        if (syllableElement != null) {
//            entry.setSyllable(syllableElement);
//        }

        // 获取音节
        String syllable = wordElement.attr("syllable");
        if (!syllable.isEmpty()) {
            entry.setSyllable(syllable);  // 将音节的字符串设置到字典条目
        }

        // 获取词性
        Element posElement = doc.select("span.pos").first();
        if (posElement != null) {
            entry.setPartOfSpeech(posElement.text());
        }

        // 获取英式发音
        Element ukPronElement = doc.select("div.phons_br .phon").first();
        if (ukPronElement != null) {
            entry.setUkPronunciation(ukPronElement.text());
        }

        // 获取美式发音
        Element usPronElement = doc.select("div.phons_n_am .phon").first();
        if (usPronElement != null) {
            entry.setUsPronunciation(usPronElement.text());
        }

        // 获取英文定义和中文定义
        Element defElement = doc.select("span.def").first();
        if (defElement != null) {
            entry.setDefinition(defElement.text());
        }

        Element chnDefElement = doc.select("deft>chn").first();
        if (chnDefElement != null) {
            entry.setChineseDefinition(chnDefElement.text());
        }

        // 获取例句
        Elements exampleElements = doc.select("ul.examples li");
        List<ExampleSentence> exampleSentences = new ArrayList<>();
        for (Element exampleElement : exampleElements) {
            ExampleSentence sentence = new ExampleSentence();
            sentence.setEnglishSentence(exampleElement.select(".x").text());
            sentence.setChineseTranslation(exampleElement.select("xt").text());
            sentence.setPronunciationUk(exampleElement.select(".pron-uk").attr("href"));
            sentence.setPronunciationUs(exampleElement.select(".pron-us").attr("href"));
            exampleSentences.add(sentence);
        }
        // 将 List<ExampleSentence> 转换为 JSONArray
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(exampleSentences));

        // 将 JSONArray 转换为 JSONObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("sentences", jsonArray);
        entry.setExampleSentences(jsonObject);

        // 获取搭配词
        Elements collocationsElements = doc.select(".collocs_list li");
        List<String> collocations = new ArrayList<>();
        for (Element coll : collocationsElements) {
            collocations.add(coll.text());
        }
        // 将 List<String> 转换为 JSONArray
        JSONArray jsonArrayCollocations = JSONArray.parseArray(JSONArray.toJSONString(collocations));

        // 将 JSONArray 转换为 JSONObject
        JSONObject jsonObjectCollocations = new JSONObject();
        jsonObjectCollocations.set("strings", jsonArrayCollocations);
        entry.setCollocations(jsonObjectCollocations);

        return entry;
    }

    public static void main(String[] args) {
        String html = "";
        String word = "hello";
        HtmlContentBean htmlContentBean = Mdict4jUtil.getHtmlContentBean(word, "oald");
        html = htmlContentBean.getRawHtml();
        DictionaryEntry entry = JsoupParser.parseHtml(html);
        System.out.println(entry);
    }
}

