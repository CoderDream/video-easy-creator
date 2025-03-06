package com.coderdream.util.chatgpt;

import java.util.ArrayList;
import java.util.List;

public class SubtitleConverter {

  public static void main(String[] args) {
    String englishSubtitles = "hello everybody\n" +
      "hello how are we great\n" +
      "I apologize for my tardiness\n" +
      "it's quite the newsy day\n" +
      "and I was with the president talking about that news\n" +
      "so I look forward to taking your questions on it\n" +
      "but first\n" +
      "I want to talk about President Trump's historic\n" +
      "and incredible speech\n" +
      "last night the American people and the entire world\n" +
      "watch President Trump powerfully lay out\n" +
      "how he's renewing the American dream\n" +
      "in a record breaking joint address to Congress\n" +
      "and Americans loved what they heard\n" +
      "according to a CBS Yugov survey\n" +
      "an overwhelming 76 percent of those watching approved\n" +
      "of President Trump's speech last night\n" +
      "the president spoke about how he's taken more than\n" +
      "400 executive actions on his key promises\n" +
      "the expectations were high\n" +
      "and President Trump is exceeding them\n" +
      "according to brand new polling from the Daily Mail\n" +
      "President Trump has never been more popular\n" +
      "as his approval ratings are reaching\n" +
      "historic highs\n" +
      "more Americans believe\n" +
      "America is headed in the right direction\n" +
      "than the wrong direction\n" +
      "everyday Americans love this president because he\n" +
      "tells it like it is\n" +
      "no matter what\n" +
      "and he did that last night\n" +
      "President Trump level set\n" +
      "with the American people on the economy\n" +
      "and exposed how badly Joe Biden screwed it up\n" +
      "by causing the worst inflation crisis in four decades\n" +
      "President Trump was honest about where we are\n" +
      "while making clear that help is on the way\n" +
      "as the president declared last night\n" +
      "he will make America affordable again\n" +
      "last night you also saw\n" +
      "who motivates the president to work so hard\n" +
      "everyday Americans\n" +
      "who President Trump shined a spotlight on last night\n" +
      "in his speech\n" +
      "from Mark Fogel\n" +
      "who President Trump was finally able to reunite with\n" +
      "his family and his beautiful 95 year old mother\n" +
      "after being detained in Russia\n" +
      "to Payton McNab\n" +
      "whose heart wrenching story\n" +
      "motivated President Trump to\n" +
      "end men and women's sports\n" +
      "and to Allison and Lauren Phillips\n" +
      "the mother and daughter and sister of Lakin Riley\n" +
      "who President Trump honored\n" +
      "by signing the Lakin Riley Act\n" +
      "to ensure her name will live on forever\n" +
      "in other amazing and surprised moments\n" +
      "President Trump honored the life of Jocelyn Nungesser\n" +
      "who was brutally murdered\n" +
      "by illegal alien gang members\n" +
      "he ensured Jocelyn will never be forgotten\n" +
      "by renaming a national wildlife refuge\n" +
      "in her home state of Texas\n" +
      "to honor her life\n" +
      "and in one of the greatest\n" +
      "surprise moments of the night\n" +
      "DJ Daniel an incredible 13 year old boy\n" +
      "who is beating brain cancer\n" +
      "saw his dreams fulfilled by President Trump\n" +
      "when he was made an honorary Secret Service agent\n" +
      "and finally after nearly four years\n" +
      "President Trump delivered justice\n" +
      "for the families of the 13 American heroes\n" +
      "who were killed at Abbey Gate\n" +
      "in the Biden botched Afghanistan withdrawal\n" +
      "which was one of the worst humiliations in the history\n" +
      "of our country\n" +
      "President Trump announced that we have detained\n" +
      "Muhammad Shirifullah\n" +
      "the monster\n" +
      "who was responsible for that horrific attack\n" +
      "and he was delivered to Dulles Airfield earlier\n" +
      "this morning\n" +
      "on his first day in office\n" +
      "President Trump's national security team\n" +
      "across the federal government\n" +
      "prioritized intelligence gathering to locate\n" +
      "this evil individual\n" +
      "President Trump's team\n" +
      "shared intelligence with regional partners\n" +
      "such as Pakistan\n" +
      "who helped identify this monster\n" +
      "in the borderland area\n" +
      "late last month\n" +
      "Mohammed confessed to his crimes related to Abu Gheit\n" +
      "and other attacks in Russia\n" +
      "and Iran as well to the Pakistanis\n" +
      "and U S";

    List<String> subtitleList = convertToSubtitleList(englishSubtitles);

    // Print the result (optional)
    for (int i = 0; i < subtitleList.size(); i++) {
      System.out.println((i + 1) + ": " + subtitleList.get(i));
    }
  }

  public static List<String> convertToSubtitleList(String text) {
    List<String> subtitleList = new ArrayList<>();
    String[] lines = text.split("\n"); // Split the string by newline character
    int index = 1;
    for (String line : lines) {
      String indexStr = String.format("%03d", index++);
      subtitleList.add(indexStr + ": " + line.trim());
    }
    return subtitleList;
  }
}
