package com.coderdream.util.file.demo02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WordExtractor02 {

    private static final Map<String, String> contractions = new HashMap<>();
    private static final Pattern VALID_WORD = Pattern.compile("^[a-z]+$"); // 匹配只包含小写字母的单词

    static {
        contractions.put("can't", "cannot");
        contractions.put("won't", "will not");
        contractions.put("shan't", "shall not");
        contractions.put("n't", "not");
        contractions.put("'re", "are");
        contractions.put("'s", "is");
        contractions.put("'d", "would");
        contractions.put("'ll", "will");
        contractions.put("'m", "am");
        contractions.put("'ve", "have");
        contractions.put("he's", "he is");
        contractions.put("she's", "she is");
        contractions.put("it's", "it is"); // 注意 its 的所有格
        contractions.put("they're", "they are");
        contractions.put("we're", "we are");
        contractions.put("i've", "I have"); // 注意大小写
        contractions.put("you've", "you have");
        contractions.put("he'd", "he would"); // 或者 he had，需要上下文
        contractions.put("she'd", "she would");
        contractions.put("i'd", "I would");
        contractions.put("you'd", "you would");
        contractions.put("we'd", "we would");
        contractions.put("they'd", "they would");
        contractions.put("there's", "there is");
        contractions.put("here's", "here is");
        contractions.put("who's", "who is"); // 注意 whose 所有格
        contractions.put("what's", "what is");
        contractions.put("where's", "where is");
        contractions.put("let's", "let us");
        contractions.put("don't", "do not");

        log.info("加载了 {} 个简写形式", contractions.size());
    }

    public static List<String> extractWords(String filePath) {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z\\s'-]", "");

                String[] lineWords = line.split("\\s+");

                for (String word : lineWords) {
                    if (word.isEmpty()) {
                        continue;
                    }

                    String cleanedWord = word.toLowerCase();
                    log.debug("原始单词: {}", cleanedWord);

                    // 简写替换
                    String expandedWord = cleanedWord;
                    for (Map.Entry<String, String> entry : contractions.entrySet()) {
                        if (expandedWord.endsWith(entry.getKey())) {
                            expandedWord = expandedWord.replace(entry.getKey(), " " + entry.getValue());
                            log.debug("展开后的单词: {}", expandedWord);
                            break;
                        }
                    }

                    // 分割展开后的单词
                    String[] expandedWords = expandedWord.split("\\s+");
                    for (String finalWord : expandedWords) {
                         if (finalWord.endsWith("'s")) {
                             finalWord = finalWord.substring(0, finalWord.length() - 2);
                         }

                         if (VALID_WORD.matcher(finalWord).matches()) { // 只添加有效的单词
                             words.add(finalWord);
                             log.debug("添加单词: {}", finalWord);
                         } else {
                             log.warn("过滤掉无效单词: {}", finalWord);
                         }
                    }
                }
            }
        } catch (IOException e) {
            log.error("读取文件时发生错误: {}", e.getMessage());
            System.err.println("Error reading file: " + e.getMessage());
        }

        return words;
    }

    public static Map<String, Integer> calculateWordFrequencies(List<String> words) {
        Map<String, Integer> wordFrequencies = new HashMap<>();

        for (String word : words) {
            wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
        }

        return wordFrequencies;
    }

    public static int countWords(List<String> words) {
        return words.size();
    }

    public static long countCharacters(String filePath) {
        long characterCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int character;
            while ((character = reader.read()) != -1) {
                characterCount++;
            }
        } catch (IOException e) {
            log.error("读取文件时发生错误: {}", e.getMessage());
            System.err.println("Error reading file: " + e.getMessage());
        }
        return characterCount;
    }

    public static void main(String[] args) {
        String filePath = "D:\\0000\\EnBook010\\Chapter001\\Chapter001.txt";
        List<String> extractedWords = extractWords(filePath);

//        System.out.println("提取出的单词:");
//        for (String word : extractedWords) {
//            System.out.println(word);
//        }

        Map<String, Integer> wordFrequencies = calculateWordFrequencies(extractedWords);

        System.out.println("\n单词频率:");
        wordFrequencies.forEach((word, frequency) -> System.out.println(word + ": " + frequency));

        int wordCount = countWords(extractedWords);
        System.out.println("\n单词总数: " + wordCount);

        long characterCount = countCharacters(filePath);
        System.out.println("字符总数: " + characterCount);
    }
}
