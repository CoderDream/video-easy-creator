package com.coderdream.util.nlp;

import java.io.*;
import java.util.*;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

/**
 * app for testing if Maven distribution is working properly
 */

public class StanfordCoreNLPEnglishTestApp {

  public static void main(String[] args)
    throws IOException, ClassNotFoundException {
//    com.sun.xml.bind.v2.ContextFactory contextFactory = new com.sun.xml.bind.v2.ContextFactory();
    String[] englishArgs = new String[]{"-file",
      "E:\\Download\\Download\\CoreNLP-main\\CoreNLP-main\\examples\\sample-maven-project\\sample-english.txt",
      "-outputFormat", "text", "-props", "english.properties"};
    StanfordCoreNLP.main(englishArgs);
  }
}
