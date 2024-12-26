package com.coderdream.util;

import org.apache.commons.codec.language.Metaphone;

public class MetaphoneExample {
    public static void main(String[] args) {
        Metaphone metaphone = new Metaphone();

        String word = "hello";
        String encodedWord = metaphone.encode(word);

        System.out.println("Original word: " + word);
        System.out.println("Encoded word: " + encodedWord);
    }
}
