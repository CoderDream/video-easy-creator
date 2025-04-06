package com.coderdream.util.mstts.demo04;

import com.coderdream.util.cd.CdConstants;
//import com.coderdream.util.mstts.demo03.PinyinConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PhonemeElementGenerator {

  public static Element generatePhonemeElement(Document doc, String character,
    String tone) {
    Element phoneme = doc.createElement("phoneme");
    phoneme.setAttribute("alphabet", CdConstants.SAPI); //  "sapi"
    //音标转为 国际音标
    String ph = "";// PinyinConverter.getPinyinIpa(character, Integer.parseInt(tone));
    phoneme.setAttribute("ph", ph);
    phoneme.setTextContent(character);
    return phoneme;
  }

  /**
   * 生成 <phoneme alphabet="sapi" ph="qu 5">去</phoneme> 格式的字符串
   *
   * @param character 汉字字符，例如 "去"
   * @param tone      声调，例如 "5"
   * @return <phoneme> 元素的字符串表示
   */
  public static String generatePhonemeString(String character, String tone) {
    //音标转为 国际音标
    String ph = "";//PinyinConverter.getPinyinIpa(character, Integer.parseInt(tone));
    return String.format(
      "<phoneme alphabet=\"" + CdConstants.SAPI + "\" ph=\"%s\">%s</phoneme>",
      ph, character);
  }

  public static void main(String[] args) {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();

      Element phonemeElement = generatePhonemeElement(doc, "重", "4");
      System.out.println(
        "phonemeElement: " + phonemeElement.getAttribute("ph"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
