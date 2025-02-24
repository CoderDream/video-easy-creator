package com.coderdream.util.bbc.demo02;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBC6MinuteEnglishScraper {

    private static final String BASE_URL = "https://www.bbc.co.uk/learningenglish/english/features/6-minute-english/";

    /**
     * 爬取指定 URL 的 BBC 6-Minute English 頁面，並提取信息。
     *
     * @param episodeId episode Id, 例如 "ep-170105"
     * @return 包含提取信息的 EpisodeData 對象，如果發生錯誤則返回 null。
     */
    public static EpisodeData scrapeEpisode(String episodeId) {
        String url = BASE_URL + episodeId;
        try {
            Document doc = Jsoup.connect(url).get();

            String level = "6 Minute English\nIntermediate level"; // Hardcoded, as the level is generally consistent
            String drivingInfo = extractDrivingInfo(doc);
            List<String> vocabulary = extractVocabulary(doc);
            String script = extractScript(doc);

            return new EpisodeData(level, drivingInfo, vocabulary, script);

        } catch (IOException e) {
            System.err.println("爬取 " + url + " 時發生錯誤: " + e.getMessage());
            return null;
        }
    }


    private static String extractDrivingInfo(Document doc) {
        String title = extractTitle(doc);
        String date = extractDate(doc);

        return String.format("%s\nEpisode %s / %s",
                title,
                extractEpisodeNumber(doc),
                date);
    }

    private static String extractEpisodeNumber(Document doc) {
        String url = doc.baseUri(); // 获取页面的 URL

        // 使用正则表达式提取 episode number
        Pattern pattern = Pattern.compile("ep-(\\d+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);  // 返回匹配到的 episode number
        } else {
            return "N/A"; // 如果未找到匹配项，则返回 "N/A"
        }
    }

    private static String extractTitle(Document doc) {
        // 提取標題
        Element titleElement = doc.selectFirst("head > title");
        //System.out.println(titleElement.toString());
        String title = titleElement.text();
        if (title.contains("/")){
            return title.split("/")[1].trim();
        }
        return (titleElement != null) ? titleElement.text() : null;
    }

    private static String extractDate(Document doc) {
        // 提取日期
        Element dateElement = doc.selectFirst(".widget-bbcle-featuresubheader .details > h3 > b");

        String text = dateElement.text();
        if (text.contains("/")){
            return text.split("/")[1].trim();
        }
        return (dateElement != null) ? dateElement.text() : null;
    }

    private static List<String> extractVocabulary(Document doc) {
        // 提取词汇
        List<String> vocabulary = new ArrayList<>();
        Element vocabularySection = doc.selectFirst(".widget-richtext > div.text");
        if (vocabularySection != null) {
            // 假设词汇以 <strong> 标签标记
            Elements strongTags = vocabularySection.select("strong");
            for (Element strongTag : strongTags) {
                String word = strongTag.text().trim();
                if (!word.isEmpty()) {
                    vocabulary.add(word);
                }
            }
            // Some vocabulary words are in italics <i> tags:
            Elements iTags = vocabularySection.select("i");
            for (Element iTag : iTags) {
                String word = iTag.text().trim();
                if (!word.isEmpty()) {
                    vocabulary.add(word);
                }
            }
        }
        return vocabulary;
    }

    private static String extractScript(Document doc) {
        // 提取腳本
        StringBuilder script = new StringBuilder();
        Elements paragraphs = doc.select(".widget-richtext > div.text > p");
        if (paragraphs != null && !paragraphs.isEmpty()) {
            // 添加第一个段落（简介）
            script.append(paragraphs.first().text()).append("\n\n");

            // 从第二个段落开始，添加剩余的段落
            for (int i = 1; i < paragraphs.size(); i++) {
                Element paragraph = paragraphs.get(i);
                String text = paragraph.text();
                // 找到说话者并确保在说话者后面添加换行符
                if (isSpeaker(text)) {
                    script.append(text).append("\n");
                } else {
                    script.append(text).append("\n\n");
                }
            }
        }
        return script.toString();
    }

    private static boolean isSpeaker(String text) {
        // 如果文字是"Both"，"Alice" or "Neil"就返回true
        return text.trim().equals("Both") || text.trim().equals("Alice") || text.trim().equals("Neil");
    }

    public static void main(String[] args) {
        String episodeId = "ep-170105"; // 替換為你想爬取的集數 ID
        EpisodeData episodeData = scrapeEpisode(episodeId);

        if (episodeData != null) {
            System.out.println(episodeData.getLevel());
            System.out.println(episodeData.getDrivingInfo());

            System.out.println("\nVocabulary");
            for (String word : episodeData.getVocabulary()) {
                System.out.println(word);
            }
            System.out.println("\nTranscript:\n" + episodeData.getScript());
        } else {
            System.out.println("爬取失敗。");
        }
    }
}

/**
 * 用於儲存提取的集數數據的簡單數據類。
 */
class EpisodeData {
    private String level;
    private String drivingInfo;
    private List<String> vocabulary;
    private String script;

    public EpisodeData(String level, String drivingInfo, List<String> vocabulary, String script) {
        this.level = level;
        this.drivingInfo = drivingInfo;
        this.vocabulary = vocabulary;
        this.script = script;
    }

    public String getLevel() {
        return level;
    }

    public String getDrivingInfo() {
        return drivingInfo;
    }

    public List<String> getVocabulary() {
        return vocabulary;
    }

    public String getScript() {
        return script;
    }
}