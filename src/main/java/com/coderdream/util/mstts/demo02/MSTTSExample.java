package com.coderdream.util.mstts.demo02;

import com.coderdream.util.cd.CdConstants;
//import com.coderdream.util.mstts.demo03.PinyinConverter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j; // 引入Slf4j

@Slf4j  // 使用Slf4j
public class MSTTSExample {

  private static final String AZURE_SPEECH_KEY = CdConstants.SPEECH_KEY_EASTASIA; // 替换成你的Azure Speech Key
  private static final String AZURE_SPEECH_REGION = CdConstants.SPEECH_REGION_EASTASIA; // 替换成你的Azure Speech Region

  public static String generateSSML(String sentence, String char1,
    String pronunciation1, String char2, String pronunciation2, String char3,
    String pronunciation3, String char4, String pronunciation4)
    throws Exception {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // 创建根元素
    Document doc = docBuilder.newDocument();
    Element speak = doc.createElement("speak");
    doc.appendChild(speak);
    speak.setAttribute("version", "1.0");
    speak.setAttribute("xmlns", "http://www.w3.org/2001/10/synthesis");
    speak.setAttribute("xmlns:mstts", "http://www.w3.org/2001/mstts");
    speak.setAttribute("xmlns:emo", "http://www.w3.org/2009/10/emotionml");
    speak.setAttribute("xml:lang", "zh-CN"); // 设置语言

    // 创建 voice 元素
    Element voice = doc.createElement("voice");
    voice.setAttribute("name", "zh-CN-XiaoxiaoNeural"); // 选择中文语音
    speak.appendChild(voice);

    // 分割句子
    String[] parts = sentence.split("", 0); // 将句子分割成单个字符

    for (String part : parts) {
      if (part.isEmpty()) {
        continue; // 忽略空字符串
      }
      if (part.equals(char1)) {
        // 创建 phoneme 元素
        Element phoneme = doc.createElement("phoneme");
        phoneme.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
        phoneme.setAttribute("ph", pronunciation1); // 设置发音
        phoneme.setTextContent(char1); // 设置文本
        voice.appendChild(phoneme);
      } else if (part.equals(char2)) {
        // 创建 phoneme 元素
        Element phoneme = doc.createElement("phoneme");
        phoneme.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
        phoneme.setAttribute("ph", pronunciation2); // 设置发音
        phoneme.setTextContent(char2); // 设置文本
        voice.appendChild(phoneme);
      } else if (part.equals(char3)) {
        // 创建 phoneme 元素
        Element phoneme = doc.createElement("phoneme");
        phoneme.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
        phoneme.setAttribute("ph", pronunciation3); // 设置发音
        phoneme.setTextContent(char3); // 设置文本
        voice.appendChild(phoneme);
      } else if (part.equals(char4)) {
        // 创建 phoneme 元素
        Element phoneme = doc.createElement("phoneme");
        phoneme.setAttribute("alphabet", CdConstants.SAPI); // 使用国际音标
        phoneme.setAttribute("ph", pronunciation4); // 设置发音
        phoneme.setTextContent(char4); // 设置文本
        voice.appendChild(phoneme);
      } else {
        voice.appendChild(doc.createTextNode(part));
      }
    }

    // 将 XML 文档转换为 String
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);

    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    transformer.transform(source, result);

    String ssmlString = writer.toString();
    log.error("Generated SSML: {}", ssmlString); // 打印生成的SSML
    return ssmlString;
  }

  public static byte[] textToSpeech(String ssml)
    throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();

    String subscriptionKey = AZURE_SPEECH_KEY;
    String region = AZURE_SPEECH_REGION;
    String endpoint =
      "https://" + region + ".tts.speech.microsoft.com/cognitiveservices/v1";

    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(endpoint))
      .header("Ocp-Apim-Subscription-Key", subscriptionKey)
      .header("Content-Type", "application/ssml+xml")
      .header("X-Microsoft-OutputFormat",
        "audio-16khz-128kbitrate-mono-mp3") // 指定输出格式
      .header("User-Agent", "MSTTS Java Example")
      .header("X-Microsoft-Language", "zh-CN")
      .POST(HttpRequest.BodyPublishers.ofString(ssml))
      .build();

    log.error("Request URL: {}", endpoint); // 打印请求URL
    log.error(
      "Request Headers: Ocp-Apim-Subscription-Key=*****, Content-Type=application/ssml+xml, X-Microsoft-OutputFormat=audio-16khz-128kbitrate-mono-mp3, User-Agent=MSTTS Java Example, X-Microsoft-Language=zh-CN"); // 打印请求头 (敏感信息已屏蔽)
    log.error("Request Body (SSML): {}", ssml);  // 打印请求体

    HttpResponse<byte[]> response = client.send(request,
      HttpResponse.BodyHandlers.ofByteArray());

    int statusCode = response.statusCode();
    byte[] responseBody = response.body();

    log.error("Response Status Code: {}", statusCode);  // 打印响应状态码

    if (statusCode == 200) {
      log.info("Text-to-speech successful.");
      return responseBody;
    } else {
      String errorBody = new String(responseBody);  // 将byte数组转换为字符串
      log.error("Error during text-to-speech: {} - {}", statusCode,
        errorBody); // 打印更详细的错误信息
      return null;
    }
  }

  public static void main(String[] args) {
//    try {
//      // 示例：
//      String sentence = "这个物体的重量是五公斤，请重复一遍。";
//      String char1 = "重";
//      String pronunciation1 = PinyinConverter.getPinyinIpa(char1,
//        4); // "zhòng" 的 IPA
//      String char2 = "量";
//      String pronunciation2 = PinyinConverter.getPinyinIpa(char2,
//        4);  // "liàng" 的 IPA
//      String char3 = "重";
//      String pronunciation3 = PinyinConverter.getPinyinIpa(char3,
//        2);  // "2"; // "chóng" 的 IPA
//      String char4 = "复";
//      String pronunciation4 = PinyinConverter.getPinyinIpa(char4,
//        4);  //  "4"; // "fù" 的 IPA
//
//      log.error("IPA1 for {}: {}", char1, pronunciation1);
//      log.error("IPA2 for {}: {}", char2, pronunciation2);
//      log.error("IPA3 for {}: {}", char3, pronunciation3);
//      log.error("IPA4 for {}: {}", char4, pronunciation4);
//
//      String ssml = generateSSML(sentence, char1, pronunciation1, char2,
//        pronunciation2, char3, pronunciation3, char4, pronunciation4);
//
//      byte[] audioData = textToSpeech(ssml);
//
//      if (audioData != null) {
//        // 将音频数据保存到文件 (可选)
//        java.nio.file.Files.write(java.nio.file.Paths.get("output.mp3"),
//          audioData);
//        System.out.println("Audio saved to output.mp3");
//        // 或者你可以直接播放音频数据，具体实现取决于你使用的音频库。
//      }
//
//    } catch (Exception e) {
//      log.error("An exception occurred: ", e);  // 打印异常堆栈信息
//    }
  }
}
