package com.coderdream.util.mstts;

import com.coderdream.util.mstts.demo04.SSMLGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class SsmlGenerator {

    /**
     * 生成SSML字符串并写入ssml.xml文件
     *
     * @param textList  文本列表
     * @param voiceName 语音名称
     * @param pitch     音调
     * @param volume    音量
     * @param rate      语速
     * @return 生成的SSML字符串
     */
    public static String genXmlString(List<String> textList, String voiceName,
                                      String pitch, String volume, String rate) {
        StringBuilder ssmlBuilder = new StringBuilder();
        ssmlBuilder.append("<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"en-US\">\n");
        ssmlBuilder.append("  <voice name=\"").append(voiceName).append("\">\n");
        ssmlBuilder.append("    <prosody pitch=\"").append(pitch)
                .append("\" volume=\"").append(volume).append("\" rate=\"").append(rate)
                .append("\">\n");

        for (String text : textList) {
          String escapedText = text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
          // 生成 phoneme，处理多音字
          escapedText = SSMLGenerator.generatePhoneme(escapedText);
          ssmlBuilder.append(escapedText).append(" ");
        }

        ssmlBuilder.append("    </prosody>\n");
        ssmlBuilder.append("  </voice>\n");
        ssmlBuilder.append("</speak>");

        String ssmlString = ssmlBuilder.toString();

        // 将SSML字符串写入ssml.xml文件
        try (FileWriter writer = new FileWriter("ssml.xml")) {
            writer.write(ssmlString);
//      log.info("SSML字符串已写入ssml.xml文件");
        } catch (IOException e) {
            log.error("写入ssml.xml文件时发生错误", e);
        }

        return ssmlString;
    }

  /**
   * x-slow /ɪks sloʊ/ (极慢)
   *
   * slow /sloʊ/ (慢)
   *
   * medium /ˈmiːdiəm/ (中等/正常)
   *
   * fast /fæst/ (快)
   *
   * x-fast /ɪks fæst/ (极快)
   *
   * default /dɪˈfɒlt/ (默认值，相当于 medium)
   * @param args
   */
  public static void main(String[] args) {
        List<String> textList = List.of("你好", "欢迎使用微软的文本转语音服务！", "现在很晚了，要准备睡觉[4]了！");
        String voiceName = "zh-CN-XiaoxiaoNeural";
        String pitch = "medium";
        String volume = "medium";
        String rate = "medium";

        String ssmlString = genXmlString(textList, voiceName, pitch, volume, rate);
        log.info("生成的SSML字符串：\n{}", ssmlString);
    }
}
