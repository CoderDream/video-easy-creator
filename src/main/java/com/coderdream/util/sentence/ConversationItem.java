package com.coderdream.util.sentence;

import lombok.Data;
import java.util.List;

@Data
public class ConversationItem {
    private String speaker;
    private List<String> sentences;
}
