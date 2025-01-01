package com.coderdream.util.markdown.my;

import java.util.List;
import lombok.Data;

@Data
public class MarkdownBean {

  private String description;
  private List<WordContent> wordContentList;
}
