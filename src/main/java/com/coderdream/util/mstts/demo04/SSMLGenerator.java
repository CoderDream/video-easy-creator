package com.coderdream.util.mstts.demo04;

//import com.coderdream.util.mstts.demo03.PinyinConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

public class SSMLGenerator {

  public static String generateSSML(String text, String voiceName)
    throws Exception {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

    // 创建根元素
    Document doc = docBuilder.newDocument();
    Element speak = doc.createElement("speak");
    doc.appendChild(speak);
    speak.setAttribute("version", "1.0");
    speak.setAttribute("xmlns", "http://www.w3.org/2001/10/synthesis");
    speak.setAttribute("xml:lang", "zh-CN"); // 设置语言

    // 创建 voice 元素
    Element voice = doc.createElement("voice");
    voice.setAttribute("name", voiceName); // 选择中文语音
    speak.appendChild(voice);

    // 解析文本
    List<TextWithPronunciation> parts = TextParser.parseTextWithPronunciation(
      text);

    // 循环处理每个文本片段
    for (TextWithPronunciation part : parts) {
      if (part.getPronunciation() != null) {
        // 如果有注音，则创建 <phoneme> 元素
        Element phoneme = PhonemeElementGenerator.generatePhonemeElement(doc,
          part.getText(), part.getPronunciation());
        voice.appendChild(phoneme);
      } else {
        // 如果没有注音，则直接添加文本
        voice.appendChild(doc.createTextNode(part.getText()));
      }
    }

    // 将 XML 文档转换为 String
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);

    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    transformer.transform(source, result);

    return writer.toString();
  }

  /**
   * 生成带有 <phoneme> 标签的文本字符串
   *
   * @param text 带有注音信息的文本，例如 "这个物体的重[4]量[4]是五公斤，请重[2]复[4]一遍。"
   * @return 带有 <phoneme> 标签的文本字符串
   */
  public static String generatePhoneme(String text) {
    StringBuilder result = new StringBuilder();
    List<TextWithPronunciation> parts = TextParser.parseTextWithPronunciation(
      text);

    for (TextWithPronunciation part : parts) {
      if (part.getPronunciation() != null) {
        // 如果有注音，则创建 <phoneme> 元素
        String phonemeString = PhonemeElementGenerator.generatePhonemeString(
          part.getText(), part.getPronunciation());
        result.append(phonemeString);
      } else {
        // 如果没有注音，则直接添加文本
        result.append(part.getText());
      }
    }

    return result.toString();
  }

  public static void main(String[] args) {
    String text = "这个物体的重[4]量[4]是五公斤，请重[2]复[4]一遍。";
    String phonemeText = SSMLGenerator.generatePhoneme(text);
    System.out.println(phonemeText);
  }

//
//    public static void main(String[] args) {
//        try {
//            String text = "这个物体的重[4]量[4]是五公斤，请重[2]复[4]一遍。";
//            String ssml = generateSSML(text, "zh-CN-XiaoxiaoNeural");
//            System.out.println(ssml);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
