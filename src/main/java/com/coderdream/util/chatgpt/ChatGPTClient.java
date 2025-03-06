package com.coderdream.util.chatgpt;

import com.coderdream.entity.GptRequest;
import com.coderdream.entity.GptResponse;
import com.coderdream.util.youtube.YouTubeApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ChatGPTClient {

  private static final String API_KEY = "sk-MQ5dB6vIMdg4YoknE3504750D35e49Cd9aD605132b2902Aa"; // 替换为你的 API Key
  private static final String API_URL = "https://free.v36.cm/v1/chat/completions";
  private static final MediaType JSON = MediaType.get(
    "application/json; charset=utf-8");
  private static final OkHttpClient client = new OkHttpClient();
  private static final int MAX_RETRIES = 10;
  private static final int RETRY_DELAY_MS = 3000; // 3 seconds

  public static com.coderdream.entity.GptResponse sendMessage(
    GptRequest request) throws IOException {
    YouTubeApiUtil.enableProxy();
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(request);

    RequestBody body = RequestBody.create(json, JSON);
    Request httpRequest = new Request.Builder()
      .url(API_URL)
      .header("Authorization", "Bearer " + API_KEY)
      .post(body)
      .build();

    int retryCount = 0;
    while (retryCount < MAX_RETRIES) {
      try (Response response = client.newCall(httpRequest).execute()) {
        if (!response.isSuccessful()) {
          System.err.println(
            "Request failed, code: " + response.code() + ", retry count: " + (
              retryCount + 1));
          retryCount++;
          Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
          continue;
        }
        String responseBody = response.body().string();
        return objectMapper.readValue(responseBody, GptResponse.class);
      } catch (IOException e) {
        System.err.println(
          "IO Exception occurred: " + e.getMessage() + ", retry count: " + (
            retryCount + 1));
        retryCount++;
        try {
          Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new IOException("Retry interrupted", ie);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Interrupted during API call", e);
      }
    }
    throw new IOException(
      "Failed to get a successful response after " + MAX_RETRIES + " retries");
  }


  public static void main(String[] args) throws IOException {
    // 英文字幕内容
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
      "and U S"
      ;

    List<String> stringList = SubtitleConverter.convertToSubtitleList(
      englishSubtitles);
    String text = String.join("\n", stringList);
    System.out.println("待翻译的英文字幕列表：\n" + text);

    // 构建 GptRequest 对象
    GptRequest request = new GptRequest();
    request.setModel("gpt-4o-mini");

    GptRequest.Message message = new GptRequest.Message();
    message.setRole("user");
    // 构建提示信息，包含翻译指示和英文字幕内容
    message.setContent("请帮我把下面的英文字符串列表翻译成中文字符串列表，"
//      + "给你多少行，返回多少行，不要任何其他信息。"
//      + "返回前先统计一下行数，如果与输入的行数不一致，则重新生成后再返回给我，直到行数一致为止。"
//      + "切记返回的中文字符串行数一定要和请求待翻译英文字符串的行数相等。"
//      + "给你100行英文字符串就返回100行中文字符串，如果不相等，就重新翻译。"
//      + "返回格式为一行英文一行中文。"
      + "待翻译的英文字符串列表如下：\n"
      + text);

    log.info("message：{}", message);
    List<GptRequest.Message> messages = List.of(message);
    request.setMessages(messages);
    request.setTemperature(0.7);

    try {
      // 发送请求并获取响应
      GptResponse response = ChatGPTClient.sendMessage(request);

      // 处理响应
      if (response != null && response.getChoices() != null
        && !response.getChoices().isEmpty()) {
        String translatedSubtitles = response.getChoices().get(0).getMessage()
          .getContent();
        System.out.println("翻译后的中文字幕：\n" + translatedSubtitles);
        List<String> responseList = SubtitleConverter.convertToSubtitleList(
          translatedSubtitles);
        String responseText = String.join("\n", responseList);
        System.out.println("翻译后的中文字符串列表：\n" + responseText);
      } else {
        System.out.println("No response received from ChatGPT.");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
