package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitter3 {

    private static final int MAX_LENGTH = 65;

    public static List<String> splitString(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
         if(input.length() <= MAX_LENGTH) {
            result.add(input);
            return result;
        }
        // 1. 优先按照逗号分割
        List<String> commaSplittedList = Arrays.stream(input.split(",")).map(String::trim).collect(Collectors.toList());


        for (String s : commaSplittedList) {
            if (s.length() <= MAX_LENGTH) {
                result.add(s);
            } else {
                // 2. 如果长度超过 65，则按照 "that, and" 分割
                List<String> thatAndSplittedList = Arrays.stream(s.split("that, and")).map(String::trim).collect(Collectors.toList());
                 for(String thatAndStr : thatAndSplittedList){
                     if(thatAndStr.length() <= MAX_LENGTH) {
                         result.add(thatAndStr);
                     }else{
                         result.add(thatAndStr);
                     }

                }
            }
        }


        return result;
    }

    public static void main(String[] args) {
//        String input1 = "This is a test string, which is short enough.";
//        String input2 = "This is a test string, which is longer than sixty-five characters, so it needs to be split.";
//         String input3 = "This is a test string, which is longer than sixty-five characters, so it needs to be split,that, and this is another part of the string that needs splitting.";
//          String input4 = "This is a very very very very very very very very very very very long string, that, and is more than 65 characters, it can't be split";
//           String input5 = "And when you walk in to the coffee shop in the morning and that smell hits you, you're getting physiological responses.";
//
//        System.out.println("Original string: " + input1);
//        System.out.println("Split string: " + splitString(input1));
//
//        System.out.println("\nOriginal string: " + input2);
//        System.out.println("Split string: " + splitString(input2));
//
//        System.out.println("\nOriginal string: " + input3);
//        System.out.println("Split string: " + splitString(input3));
//
//           System.out.println("\nOriginal string: " + input4);
//           System.out.println("Split string: " + splitString(input4));
//        System.out.println("\nOriginal string: " + input5);
//        System.out.println("Split string: " + splitString(input5));

        System.out.println("\n 测试空列表:");
        List<String> strings = splitString(
          "And when you walk in to the coffee shop in the morning and that smell hits you, you're getting physiological responses.");
        for (String string : strings) {
            System.out.println(string);
        }
//
//        System.out.println();
//        System.out.println("\n 测试null 列表:");
//        System.out.println(splitString(null));
    }
}
