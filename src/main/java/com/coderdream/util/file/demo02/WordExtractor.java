package com.coderdream.util.file.demo02;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class WordExtractor {

    private static final Map<String, String> contractions = new HashMap<>();
    private static final Pattern VALID_WORD = Pattern.compile("^[a-z]+(-[a-z]+)*$"); // 匹配包含连字符的单词，不能以连字符开头
    private static final Set<Character> PUNCTUATION = new HashSet<>(Arrays.asList(
            '.', ',', '!', '?', ':', ';', '(', ')', '[', ']', '{', '}', '<', '>', '/', '\\', '|', '*', '+', '=', '^', '%', '$', '#', '@', '~', '`', '—', '…'
    )); // 定义要移除的标点符号集合

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
        contractions.put("it's", "it is");
        contractions.put("they're", "they are");
        contractions.put("we're", "we are");
        contractions.put("i've", "I have");
        contractions.put("you've", "you have");
        contractions.put("he'd", "he would");
        contractions.put("she'd", "she would");
        contractions.put("i'd", "I would");
        contractions.put("you'd", "you would");
        contractions.put("we'd", "we would");
        contractions.put("they'd", "they would");
        contractions.put("there's", "there is");
        contractions.put("here's", "here is");
        contractions.put("who's", "who is");
        contractions.put("what's", "what is");
        contractions.put("where's", "where is");
        contractions.put("let's", "let us");
        contractions.put("don't", "do not");

        log.info("加载了 {} 个简写形式", contractions.size());
    }

    private static String handleApostrophe(String word) {
        // 1. 移除单词开头和结尾的左右撇号和单引号
        String trimmedWord = word.replaceAll("^[‘’']|[‘’']$", "");

        // 2. 检查是否是省略形式 (使用标准的单引号)
        if (contractions.containsKey(trimmedWord)) {
            String expandedWord = contractions.get(trimmedWord);
            log.debug("展开简写: {} -> {}", trimmedWord, expandedWord);
            return expandedWord;
        } else {
            // 3. 不是省略形式，则将左右撇号和单引号替换为空格
            String result = trimmedWord.replaceAll("[‘’']", " ");
            log.debug("撇号替换为空格: {} -> {}", trimmedWord, result);
            return result;
        }
    }

    private static String removePunctuation(String line) {
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (!PUNCTUATION.contains(c) && c != '\'' && c != '‘' && c != '’' && c != '“' && c != '”') { // 移除明确的标点符号和各种单引号和双引号
                sb.append(c);
            } else {
                log.debug("移除标点符号: {}", c);
            }
        }
        String cleanedLine = sb.toString();
        log.debug("移除标点符号: {} -> {}", line, cleanedLine);
        return cleanedLine;
    }

//    private static String removePossessive(String word) {
//        if (word.endsWith("s")) { // 移除所有格 ('s)
//            String result = word.substring(0, word.length() - 2);
//            log.debug("移除所有格: {} -> {}", word, result);
//            return result;
//        }
//        return word;
//    }

    private static String removePossessive(String word) {
        if (word.endsWith("'s")) { // 确保是所有格 "'s"
            String result = word.substring(0, word.length() - 2);
            log.debug("移除所有格: {} -> {}", word, result);
            return result;
        }
        return word;
    }

    private static String cleanWord(String word) {
        String cleanedWord = word.toLowerCase();
        log.debug("转换为小写: {} -> {}", word, cleanedWord);
        return cleanedWord;
    }

    public static List<String> extractWords(String filePath) {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("原始行: {}", line);

                // 1. 移除标点符号
                String cleanedLine = removePunctuation(line);

                // 2. 分割单词
                String[] lineWords = cleanedLine.split("\\s+");
                log.debug("分割后的单词: {}", Arrays.toString(lineWords));

                for (String word : lineWords) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    log.debug("原始单词: {}", word);

                    // 3. 处理撇号
                    String apostropheHandledWord = handleApostrophe(word);

                    // 4. 分割处理撇号后的单词 (只在 handleApostrophe 替换为空格时才需要)
                    String[] apostropheHandledWords = apostropheHandledWord.split("\\s+");

                    for (String singleWord : apostropheHandledWords) {
                        // 5. 清理单词
                        String cleanedWord = cleanWord(singleWord);

                        // 6. 移除所有格
                        String possessiveRemovedWord = removePossessive(cleanedWord);

                        // 7. 验证单词
                        if (VALID_WORD.matcher(possessiveRemovedWord).matches()) {
                            words.add(possessiveRemovedWord);
                            log.debug("添加单词: {}", possessiveRemovedWord);
                        } else {
                            log.warn("过滤掉无效单词: {}", possessiveRemovedWord);
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


        System.out.println("\n单词数量: " + wordFrequencies.size());

        long characterCount = countCharacters(filePath);
        System.out.println("字符总数: " + characterCount);
    }
}
