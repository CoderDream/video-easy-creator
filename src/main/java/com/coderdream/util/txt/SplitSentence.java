package com.coderdream.util.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitSentence {

    public static void main(String[] args) {
        String sentence = "The book title is \"The Lord of the Rings\" and it's great。 他说：“你好，世界！” 这本书怎么样？Is it good?";
        List<String> sentences = splitSentences(sentence);
        for (String s : sentences) {
            System.out.println(s);
        }
    }

    public static List<String> splitSentences(String sentence) {
        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        Pattern pattern = Pattern.compile("(\"[^\"]*\")|([^\"\\s\\p{P}]+)|([\\p{P}])");
        Matcher matcher = pattern.matcher(sentence);

        while (matcher.find()) {
            String part = matcher.group();
            currentSentence.append(part);

            if (part.matches("[。？！?!]") || (part.length() > 0 && part.charAt(0) == '”' &&  (sentence.indexOf(part) + part.length() < sentence.length() && sentence.charAt(sentence.indexOf(part) + part.length()) !=' ')))
             {
                 sentences.add(currentSentence.toString().trim());
                currentSentence.setLength(0);
            } else {
                currentSentence.append(" ");
            }
        }

        if (currentSentence.length() > 0) {
            sentences.add(currentSentence.toString().trim());
        }
        return sentences;
    }
}
