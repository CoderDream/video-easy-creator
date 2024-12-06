package com.coderdream.util.mdict;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

    public static HtmlContentBean parseHtml(String html) {
        HtmlContentBean bean = new HtmlContentBean();
        bean.setRawHtml(html);
        Document doc = Jsoup.parse(html);

        // Extract helloText
        Element helloElement = doc.select("font[size=+1][color=purple]").first();
        if (helloElement != null) {
            bean.setWord(helloElement.text());
        }

        // Extract ratingText
        Element ratingElement = doc.select("font[color=gold]").first();
        if (ratingElement != null) {
            bean.setRatingText(ratingElement.text());
        }

        // Extract captions
        List<String> captions = new ArrayList<>();
        Elements captionElements = doc.select("div.caption span.st");
        for (Element captionElement : captionElements) {
            captions.add(captionElement.text());
        }
        bean.setCaptions(captions);

        // Extract sentences and translations
        List<String> sentences = new ArrayList<>();
        List<String> translations = new ArrayList<>();
        Elements liElements = doc.select("ul li p");
        for (Element liElement : liElements) {
            sentences.add(liElement.text());
            // Assuming the translation is the next sibling of the sentence
            Element translationElement = liElement.nextElementSibling();
            if (translationElement != null) {
                translations.add(translationElement.text());
            }
        }
        bean.setSentences(sentences);
        bean.setTranslations(translations);

        return bean;
    }

    public static void main(String[] args) {
        String html = "<html>...</html>"; // Your HTML content here
        String word = "hello";
        HtmlContentBean htmlContentBean = Mdict4jUtil.getWordDetail(word, "oald");
        HtmlContentBean bean = HtmlParser.parseHtml(html);
        System.out.println(bean);
    }
}
