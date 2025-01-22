//package com.coderdream.util.txt.filter;
//
//import com.coderdream.vo.SentenceVO;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//
//public class SentenceParser {
//
//    public static List<SentenceVO> parseSentences(List<String> inputList) {
//        List<SentenceVO> result = new ArrayList<>();
//
//        if(inputList == null || inputList.isEmpty()) {
//          return result; // 处理空列表
//        }
//
//        for (String line : inputList) {
//            if (line == null || line.trim().isEmpty()) {
//                continue; // 跳过空行或null
//            }
//
//            String[] parts = line.split("/", -1); // 使用 -1 参数，保留末尾的空字符串
//            if (parts.length == 3) {
//                //去除左右的空格，并确保三个字符串都存在值
//                String english = parts[0].trim();
//                String phonetics = parts[1].trim();
//                String chinese = parts[2].trim();
//
//
//                result.add(new SentenceVO(english, phonetics, chinese));
//            }
//
//        }
//        return result;
//    }
//
//    public static void main(String[] args) {
//        List<String> input = new ArrayList<>();
//        input.add("But hard work pays off, you know. /bʌt hɑːd wɜːrk peɪz ɒf juː nəʊ/ 但是你知道，辛苦工作总有回报的。");
//        input.add("What do you mean? /wɒt duː juː miːn/ 怎么讲？");
//        input.add("Invalid line with no slash");
//        input.add("   /    /   ");
//         input.add("   /a/   ");
//        input.add("");  // 空行
//         input.add(null);  // null行
//         input.add("   /   /a   ");
//
//        List<SentenceVO> sentenceVOList = parseSentences(input);
//
//        for (SentenceVO sentenceVO : sentenceVOList) {
//            System.out.println("English: " + sentenceVO.getEnglish());
//            System.out.println("Phonetics: " + sentenceVO.getPhonetics());
//            System.out.println("Chinese: " + sentenceVO.getChinese());
//            System.out.println("---");
//        }
//         System.out.println("\n 测试空列表:");
//          List<String> input2 = new ArrayList<>();
//          List<SentenceVO>  splittedList2 = parseSentences(input2);
//           System.out.println(splittedList2);
//
//         System.out.println("\n 测试 null 列表:");
//         List<SentenceVO>   splittedList3 = parseSentences(null);
//           System.out.println(splittedList3);
//
//    }
//}
