package com.coderdream.util.gemini.demo01;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.gemini.GeminiApiClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

@Slf4j
public class TxtPhoneticFiller {

  private static final String INPUT_TXT_PATH = "C:\\Users\\CoderDream\\Desktop\\words2\\cefr_total_words2.txt";
  private static final String OUTPUT_TXT_PATH = "C:\\Users\\CoderDream\\Desktop\\words2\\cefr_total_words_new2.txt";
  private static final String GEMINI_API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 API 密钥
  private static final String API_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key="
      + GEMINI_API_KEY;
  private static final String PROXY_HOST = "127.0.0.1";
  private static final int PROXY_PORT = 1080;

  public static void main(String[] args) {
    try {
      Map<String, CefrTotalWords> map = new HashMap<>();
      List<CefrTotalWords> words = readWordsFromTxt(INPUT_TXT_PATH);
      List<CefrTotalWords> processedWords = processWordsInBatches(words,
        INPUT_TXT_PATH);

      writeWordsToTxt(processedWords, OUTPUT_TXT_PATH);
      log.info("TXT processing complete. Output file: " + OUTPUT_TXT_PATH);
    } catch (IOException e) {
      log.error("Error processing TXT file: " + e.getMessage(), e);
    }
  }

  private static List<CefrTotalWords> readWordsFromTxt(String inputPath)
    throws IOException {
    List<CefrTotalWords> words = new ArrayList<>();
    List<String> lines = FileUtils.readLines(new File(inputPath),
      StandardCharsets.UTF_8);
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      String[] parts = line.split("\t");
      if (parts.length == 4) {
        CefrTotalWords word = new CefrTotalWords(parts[0], parts[1], parts[2],
          parts[3]);
        words.add(word);
      } else if (parts.length == 2) {
        CefrTotalWords word = new CefrTotalWords(parts[0], parts[1]);
        words.add(word);
      } else {
        log.warn("Skipping line {} due to incorrect number of fields: {}",
          i, line);
      }
    }

    return words;
  }

  private static List<CefrTotalWords> processWordsInBatches(
    List<CefrTotalWords> words, String sourceFileName) {
    List<CefrTotalWords> processedWords = new ArrayList<>();
    int BATCH_SIZE = 200;
//    final String fileName = "C:\\Users\\CoderDream\\Desktop\\cefr_total_words2.txt";
    String indexStr = "";
    List<CefrTotalWords> cefrTotalWords = null;
    for (int i = 0; i < words.size(); i += BATCH_SIZE) {
      List<CefrTotalWords> batch = words.subList(i,
        Math.min(i + BATCH_SIZE, words.size()));
      indexStr =
        "_" + String.format("%05d", i + 1) + "_" + String.format("%05d",
          Math.min(i + BATCH_SIZE, words.size()));
      String tempFileName = CdFileUtil.addPostfixToFileName(sourceFileName,
        indexStr);
      if (CdFileUtil.isFileEmpty(tempFileName)) {
        cefrTotalWords = processBatch(batch, tempFileName);
        if (CollectionUtil.isNotEmpty(cefrTotalWords)) {
          writeWordsToTxt(cefrTotalWords, tempFileName);
        }
      } else {
        cefrTotalWords = new ArrayList<>();
        List<String> lines = CdFileUtil.readFileContent(tempFileName);
        assert lines != null;
        for (String line : lines) {
          String[] parts = line.split("\t");
          if (parts.length == 4) {
            CefrTotalWords word = new CefrTotalWords(parts[0], parts[1],
              parts[2], parts[3]);
            cefrTotalWords.add(word);
          } else if (parts.length == 2) {
            CefrTotalWords word = new CefrTotalWords(parts[0], parts[1]);
            cefrTotalWords.add(word);
          }
        }
      }
      processedWords.addAll(cefrTotalWords);
    }
    return processedWords;
  }

  private static List<CefrTotalWords> processBatch(List<CefrTotalWords> batch,
    String tempFileName) {
    List<CefrTotalWords> processedBatchNew = new ArrayList<>();
    int retryTimes = 10;
    List<String> headwords = batch.stream().map(CefrTotalWords::getHeadword)
      .distinct()
      .toList();

    try {
      List<CefrTotalWords> processedBatch = getPhoneticData(headwords,
        tempFileName,
        retryTimes);

      if (CollectionUtil.isNotEmpty(processedBatch)) {
        Map<String, CefrTotalWords> map = new HashMap<>();
        for (CefrTotalWords word : processedBatch) {
          map.put(word.getHeadword(), word);
        }
        for (CefrTotalWords word : batch) {
          CefrTotalWords temp = map.get(word.getHeadword());
          if (temp != null) {
            word.setEnglishPhonetic(temp.getEnglishPhonetic());
            word.setAmericanPhonetic(temp.getAmericanPhonetic());
            processedBatchNew.add(word);
          } else {
            log.error("No phonetic data found for word: {}",
              word.getHeadword());
          }
        }
      } else {
        log.error("No phonetic data found for batch.");
      }

    } catch (Exception e) {
      log.error("Error processing batch: {}", e.getMessage(), e);
    }
    return processedBatchNew;
  }

  private static List<CefrTotalWords> getPhoneticData(List<String> headwords,
    String tempFileName,
    int retryTimes) {
    // 使用 API 获取音标数据
    String prompt =
      "Provide the English (UK) and American phonetic transcriptions (IPA) for the word: ["
        + String.join("|", headwords) +
        "]. Separate the UK and US transcriptions with a semicolon. If not available, leave it blank，你返回的text字段只需要返回结果，只包含单词、英式音标和美式音标，用竖线|隔开，不要用TAB、分号等标点符号，不要解释说明等等；给你几个单词，返回多少行，给100个单词，返回100行。";

    String jsonRequest = String.format("""
      {
        "contents": [{
          "parts":[{
            "text": "%s"
          }]
        }],
        "safetySettings": [
              {
                  "category": "HARM_CATEGORY_HARASSMENT",
                  "threshold": "BLOCK_NONE"
              },
              {
                  "category": "HARM_CATEGORY_HATE_SPEECH",
                  "threshold": "BLOCK_NONE"
              },
              {
                  "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                  "threshold": "BLOCK_NONE"
              },
              {
                  "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                  "threshold": "BLOCK_NONE"
              }
         ]
      }
      """, prompt);
    String tempFileNameTemp = CdFileUtil.addPostfixToFileName(tempFileName,
      "_temp");
    log.error("tempFileNameTemp: {}", tempFileNameTemp);
    String text = "";
    List<String> textList = new ArrayList<>();
    if (!CdFileUtil.isFileEmpty(tempFileNameTemp)) {
      textList = CdFileUtil.readFileContent(tempFileNameTemp);
    } else {
// 解析 API 响应
      String responseBody = GeminiApiClient.generateRawContent(
        jsonRequest);//response.body();
      // 解析 JSON 响应 ResponseBean.class
      JSONObject jsonObject = JSONUtil.parseObj(responseBody);
      ResponseBean responseBean = jsonObject.toBean(ResponseBean.class);

      text = responseBean.getCandidates().get(0).getContent().getParts()
        .get(0).getText();
      if (text.startsWith("{")) {
        JSONObject jsonObject2 = JSONUtil.parseObj(text);
        ResponseBean.Content content = jsonObject2.toBean(
          ResponseBean.Content.class);
        text = content.getParts().get(0).getText();
      }

      textList = Arrays.asList(text.split("\n"));
      CdFileUtil.writeToFile(tempFileNameTemp, textList);
    }

    if (textList.size() != headwords.size()) {
      if (retryTimes > 0) {
        retryTimes--;
        // 使用 log.error 记录错误信息，而不是 System.err.println
        log.error("text: {}", text);
        log.error(
          "翻译结果大小不匹配。期望: {}, 实际: {}, 正在重试... (剩余 {} 次尝试)",
          headwords.size(), textList.size(), retryTimes);
        try {
          Thread.sleep(3000); // 等待一秒后重试
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return getPhoneticData(headwords, tempFileName, retryTimes);
      } else {
        return null;
      }
    } else {
      List<CefrTotalWords> phoneticDataList = new ArrayList<>();
      for (String line : textList) {
        line = line.replace(":", "\t");
        line = line.replace(";", "\t");
        line = line.replace("\t\t", "\t");
        String[] parts = line.split("\\|",
          3); // Split into headword and phonetic data
        if (parts.length == 3) {
          String headword = parts[0].trim();
          String englishPhonetic = parts[1].trim();
          englishPhonetic = englishPhonetic.replace("  ", " ");
          if (!englishPhonetic.startsWith("/")) {
            englishPhonetic = "/" + englishPhonetic + "/";
          }
          String americanPhonetic = parts[2].trim();
          if (!americanPhonetic.startsWith("/")) {
            americanPhonetic = "/" + americanPhonetic + "/";
          }
          americanPhonetic = americanPhonetic.replace("  ", " ");
          if (!englishPhonetic.equals(americanPhonetic)) {
            log.error("Different phonetic data for {}: {}, {}", headword,
              englishPhonetic, americanPhonetic);
          }

          CefrTotalWords word = new CefrTotalWords(headword, englishPhonetic,
            americanPhonetic);
          phoneticDataList.add(word);
        } else {
          log.warn("Skipping line due to incorrect format: {}", line);
        }

      }

      return phoneticDataList;
    }

  }

  private static void writeWordsToTxt(List<CefrTotalWords> words,
    String outputPath) {
    try (BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(outputPath),
        StandardCharsets.UTF_8))) {
      writer.write("id\theadword\tenglish_phonetic\tamerican_phonetic");
      writer.newLine();
      for (CefrTotalWords word : words) {
        String line = String.join("\t", word.getId(), word.getHeadword(),
          (word.getEnglishPhonetic() != null ? word.getEnglishPhonetic()
            : ""),
          (word.getAmericanPhonetic() != null ? word.getAmericanPhonetic()
            : ""));
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      log.error("Error writing to file: {}", e.getMessage());
//      throw new RuntimeException(e);
    }
  }

  // 存储音标数据的内部类
  @Data
  private static class PhoneticData {

    String index;
    String headword;

    String englishPhonetic;
    String americanPhonetic;

    public PhoneticData(String index, String headword, String englishPhonetic,
      String americanPhonetic) {
      this.index = index;
      this.headword = headword;
      this.englishPhonetic = englishPhonetic;
      this.americanPhonetic = americanPhonetic;
    }

    public PhoneticData(String englishPhonetic, String americanPhonetic) {
      this.englishPhonetic = englishPhonetic;
      this.americanPhonetic = americanPhonetic;
    }
  }

  @lombok.Data
  private static class CefrTotalWords {

    private String id;
    private String headword;
    private String englishPhonetic;
    private String americanPhonetic;

    public CefrTotalWords(String headword, String englishPhonetic,
      String americanPhonetic) {
      this.headword = headword;
      this.englishPhonetic = englishPhonetic;
      this.americanPhonetic = americanPhonetic;
    }

    public CefrTotalWords(String id, String headword, String englishPhonetic,
      String americanPhonetic) {
      this.id = id;
      this.headword = headword;
      this.englishPhonetic = englishPhonetic;
      this.americanPhonetic = americanPhonetic;
    }

    public CefrTotalWords(String id, String headword) {
      this.id = id;
      this.headword = headword;
    }

    public CefrTotalWords() {

    }
  }
}
