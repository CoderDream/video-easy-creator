package com.coderdream.util.txt.demo01;

import com.coderdream.entity.SentencePair;
import java.util.List;
import lombok.Data;

@Data
public class Book003Info {

  private SentencePair scnceSentencePair;

  private List<SentencePair> sentencePairs;
}
