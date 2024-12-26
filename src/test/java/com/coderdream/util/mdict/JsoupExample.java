package com.coderdream.util.mdict;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class JsoupExample {
    public static void main(String[] args) throws IOException {
        // 假设 HTML 字符串
        String html = "<link href=\"oaldpe.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
          "<script src=\"oaldpe-jquery-3.6.0.min.js\"></script>\n" +
          "<script src=\"oaldpe.js\"></script>\n" +
          "<body>\n" +
          "<body-content class=\"oaldpe\">\n" +
          "  <div id=\"entryContent\" class=\"oald\">\n" +
          "    <div class=\"entry\" sum=\"1008\" hclass=\"entry\" id=\"chemistry\" hlength=\"9\"\n" +
          "         htag=\"section\" sk=\"chemistry: :0\" idm_id=\"000010051\">\n" +
          "      <div class=\"top-container\">\n" +
          "        <div class=\"top-g\" id=\"chemistry_topg_1\">\n" +
          "          <div class=\"webtop\">\n" +
          "            <h1 class=\"headword\" id=\"chemistry_h_1\" hclass=\"headword\" syllable=\"chem·is·try\">\n" +
          "            chem·is·try</h1> <span class=\"pos\" hclass=\"pos\" htag=\"span\">noun</span>\n" +
          "            <span class=\"phonetics\"><div class=\"phons_br\" hclass=\"phons_br\"\n" +
          "                                      wd=\"chemistry\" htag=\"div\" geo=\"br\"><span class=\"phon\">/ˈkemɪstri/</span></div>\n" +
          "            <div class=\"phons_n_am\" wd=\"chemistry\" hclass=\"phons_n_am\" geo=\"n_am\"><span class=\"phon\">/ˈkemɪstri/</span></div>\n" +
          "            </span></div>\n" +
          "        </div>\n" +
          "      </div>\n" +
          "      <ol class=\"senses_multiple\" htag=\"ol\">\n" +
          "        <div class=\"li_sense\">\n" +
          "          <div class=\"li_sense_before\">1</div>\n" +
          "          <li class=\"sense\" id=\"chemistry_sng_1\" hclass=\"sense\" htag=\"li\">\n" +
          "            <span class=\"def\" htag=\"span\" hclass=\"def\">the scientific study of the structure of substances, how they react when combined or in contact with one another, and how they behave under different conditions</span>\n" +
          "            <deft>\n" +
          "              <chn class=\"simple\">化学</chn>\n" +
          "              <chn class=\"traditional\">化學</chn>\n" +
          "            </deft>\n" +
          "          </li>\n" +
          "        </div>\n" +
          "        <div class=\"li_sense\">\n" +
          "          <div class=\"li_sense_before\">2</div>\n" +
          "          <li class=\"sense\" cefr=\"c1\" id=\"chemistry_sng_2\" hclass=\"sense\" htag=\"li\">\n" +
          "            <span class=\"def\" htag=\"span\" hclass=\"def\">the chemical structure and behaviour of a particular substance</span>\n" +
          "            <deft>\n" +
          "              <chn class=\"simple\">物质的化学组成（或性质）</chn>\n" +
          "              <chn class=\"traditional\">物質的化學組成（或性質）</chn>\n" +
          "            </deft>\n" +
          "          </li>\n" +
          "        </div>\n" +
          "      </ol>\n" +
          "    </div>\n" +
          "  </div>\n" +
          "</body-content>\n" +
          "</body>";

        // 解析HTML文档
        Document document = Jsoup.parse(html);

        // 输出整个HTML文档，检查它是否正确解析
        System.out.println("Parsed HTML:\n" + document.toString());

        // 获取所有定义部分
        Elements senses = document.select("li.sense");

        // 检查是否找到任何定义
        if (senses.isEmpty()) {
            System.out.println("No definitions found!");
        } else {
            // 遍历所有的定义，并提取英文、简体中文和繁体中文解释
            for (Element sense : senses) {
                // 提取英文解释
                String englishDefinition = sense.select("span.def").text();
                // 提取简体中文解释
                String simplifiedChinese = sense.select("chn.simple").text();
                // 提取繁体中文解释
                String traditionalChinese = sense.select("chn.traditional").text();

                // 输出每个定义
                System.out.println("English Definition: " + englishDefinition);
                System.out.println("Simplified Chinese Definition: " + simplifiedChinese);
                System.out.println("Traditional Chinese Definition: " + traditionalChinese);
                System.out.println("-----------------------------------");
            }
        }
    }
}
