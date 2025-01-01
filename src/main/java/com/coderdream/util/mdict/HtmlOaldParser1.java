package com.coderdream.util.mdict;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  OALD 词典内容解析类
 */
@Slf4j
public class HtmlOaldParser1 {

    /**
     *  解析 HTML 文件并返回 DictOaldBean 对象
     * @param htmlFilePath HTML 文件路径
     * @return DictOaldBean 对象
     */
    public static DictOaldBean parseHtml(String htmlFilePath) {
        DictOaldBean dictOaldBean = new DictOaldBean();
        try {
            File htmlFile = new File(htmlFilePath);
            Document doc = Jsoup.parse(htmlFile, "UTF-8");
            Element entry = doc.selectFirst(".entry");
            if (entry != null) {
                parseEntry(entry, dictOaldBean);
            } else {
                log.warn("未找到 .entry 元素：{}", htmlFilePath);
            }
        } catch (IOException e) {
            log.error("解析 HTML 文件失败：{}", htmlFilePath, e);
        }
        return dictOaldBean;
    }


    /**
     *  解析 HTML 字符串并返回 DictOaldBean 对象
     * @param htmlString HTML 字符串
     * @return DictOaldBean 对象
     */
    public static DictOaldBean parseHtmlString(String htmlString) {
        DictOaldBean dictOaldBean = new DictOaldBean();
        try {
            Document doc = Jsoup.parse(htmlString);
           Element entry = doc.selectFirst(".entry");
           if (entry != null) {
              parseEntry(entry, dictOaldBean);
           } else {
             log.warn("未找到 .entry 元素：{}", htmlString);
           }
        } catch (Exception e) {
            log.error("解析 HTML 字符串失败：{}", htmlString, e);
        }
        return dictOaldBean;
    }


    /**
     * 解析 .entry 元素下的内容
     * @param entry .entry 元素
     * @param dictOaldBean  DictOaldBean 对象
     */
    private static void parseEntry(Element entry, DictOaldBean dictOaldBean) {
        // 解析头部信息
        Element topG = entry.selectFirst(".top-g");
        if (topG != null) {
            parseTopG(topG, dictOaldBean);
        }

        // 解析词条的不同释义
        Elements senses = entry.select(".sense");
        if (senses != null) {
            List<DictOaldBean.Sense> senseList = new ArrayList<>();
             for(Element sense : senses){
                parseSense(sense,senseList);
             }
            dictOaldBean.setSenses(senseList);
        }
    }


    /**
     *  解析 .top-g 元素
     * @param topG .top-g 元素
     * @param dictOaldBean DictOaldBean 对象
     */
    private static void parseTopG(Element topG, DictOaldBean dictOaldBean) {
        Element headword = topG.selectFirst(".headword");
        if (headword != null) {
            dictOaldBean.setHeadword(headword.text());
        }

      Element pos = topG.selectFirst(".pos");
      if (pos != null) {
          dictOaldBean.setPos(pos.text());
      }

       Element phonetics = topG.selectFirst(".phonetics");
        if (phonetics != null) {
          // 获取英国音标
         Element phonsBr = topG.selectFirst(".phons_br .phon");
         if(phonsBr != null){
             dictOaldBean.setUkPronunciation(phonsBr.text());
         }

         // 获取美式音标
           Element phonsNa = topG.selectFirst(".phons_n_am .phon");
           if(phonsNa != null){
             dictOaldBean.setUsPronunciation(phonsNa.text());
           }
        }

    }


    /**
     *  解析 .sense 元素
     * @param sense .sense 元素
     * @param senseList 释义列表
     */
    private static void parseSense(Element sense, List<DictOaldBean.Sense> senseList) {
        DictOaldBean.Sense senseBean = new DictOaldBean.Sense();

        Element def = sense.selectFirst(".def");
      if (def != null) {
        senseBean.setDefinition(def.text());
         Element chn = sense.selectFirst("chn");
         if (chn != null) {
             senseBean.setTranslation(chn.text());
         }
      }

      Elements examples = sense.select(".examples li .x");
      if (examples != null) {
           List<DictOaldBean.Example> exampleList = new ArrayList<>();
          for (Element example : examples) {
              DictOaldBean.Example exampleBean = new DictOaldBean.Example();
              exampleBean.setExample(example.text());
             Element translation = example.selectFirst("xt chn");
              if(translation != null){
                exampleBean.setTranslation(translation.text());
              }
             exampleList.add(exampleBean);
          }
         senseBean.setExamples(exampleList);
      }

        senseList.add(senseBean);
    }




    /**
     *  用于存储 OALD 词典内容的 Bean
     */
    @Data
    public static class DictOaldBean {
        private String headword; // 词头
        private String pos;  // 词性
        private String ukPronunciation; // 英式发音
        private String usPronunciation; // 美式发音
        private List<Sense> senses; // 词义列表


        @Data
        public static class Sense {
            private String definition; // 英文定义
            private String translation; // 中文翻译
             private List<Example> examples; // 例句列表
        }

        @Data
        public static class Example {
            private String example; // 例句
           private String translation; // 例句翻译
        }
    }

    public static void main(String[] args) {
       String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\main\\java\\com\\coderdream\\util\\mdict\\commonly1.html";
        DictOaldBean dictOaldBean = HtmlOaldParser1.parseHtml(htmlFilePath);
        System.out.println("from file: " + dictOaldBean);

        String htmlString = "<link href=\"oald.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "<script src=\"oald.js\"></script>\n" +
                "<body>\n" +
                "<div id=\"entryContent\" class=\"oald leon-oald\">\n" +
                "  <div class=\"entry\" htag=\"section\" sk=\"commonly: :0\" sum=\"589\" hlength=\"8\"\n" +
                "       id=\"commonly\" hclass=\"entry\" idm_id=\"000011925\">\n" +
                "    <div class=\"top-container\">\n" +
                "      <div class=\"top-g\" id=\"commonly_topg_1\">\n" +
                "        <div class=\"webtop\"><h1 class=\"headword\" random=\"y\" ox3000=\"y\" htag=\"h1\"\n" +
                "                                opal_written=\"y\" hclass=\"headword\"\n" +
                "                                id=\"commonly_h_1\" syllable=\"com·mon·ly\">\n" +
                "          com·mon·ly</h1> <span class=\"pos\" htag=\"span\"\n" +
                "                                hclass=\"pos\">adverb</span>\n" +
                "          <div class=\"symbols\" hclass=\"symbols\" htag=\"div\"><a\n" +
                "              href=\"entry://@wordlists_oxford3000-5000_dataset=english&list=ox3000&level=b2\"><span\n" +
                "              class=\"ox3ksym_b2\"> </span></a><a\n" +
                "              href=\"entry://@wordlists_opal_dataset=english&list=opal_written&level=sublist_5\"><span\n" +
                "              class=\"opal_symbol\"\n" +
                "              href=\"OPAL_Written::Sublist_5\">OPAL W</span></a></div>\n" +
                "          <span class=\"phonetics\"> <div class=\"phons_br\" geo=\"br\" htag=\"div\"\n" +
                "                                        hclass=\"phons_br\" wd=\"commonly\"><a\n" +
                "              class=\"sound audio_play_button pron-uk icon-audio\"\n" +
                "              href=\"sound://commonly__gb_1.mp3\"\n" +
                "              title=\"commonly pronunciation English\" style=\"cursor: pointer\"\n" +
                "              valign=\"top\"></a><span class=\"phon\">/ˈkɒmənli/</span></div> <div\n" +
                "              class=\"phons_n_am\" hclass=\"phons_n_am\" wd=\"commonly\" htag=\"div\"\n" +
                "              geo=\"n_am\"><a class=\"sound audio_play_button pron-us icon-audio\"\n" +
                "                            href=\"sound://commonly__us_2.mp3\"\n" +
                "                            title=\"commonly pronunciation American\"\n" +
                "                            style=\"cursor: pointer\" valign=\"top\"></a><span\n" +
                "              class=\"phon\">/ˈkɑːmənli/</span></div></span></div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "    <ol class=\"sense_single\" htag=\"ol\">\n" +
                "      <li class=\"sense\" fkox3000=\"y\" id=\"commonly_sng_1\" hclass=\"sense\"\n" +
                "          fkcefr=\"b2\" htag=\"li\"><span class=\"sensetop\" htag=\"span\"\n" +
                "                                      hclass=\"sensetop\"><span class=\"def\"\n" +
                "                                                              hclass=\"def\"\n" +
                "                                                              htag=\"span\">usually; very often; by most people</span><deft><chn>通常；常常；大多数人地</chn></deft></span>\n" +
                "        <ul class=\"examples\" htag=\"ul\" hclass=\"examples\">\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">Christopher is <span\n" +
                "              class=\"cl\">commonly known as</span> Kit.<a\n" +
                "              class=\"sound audio_play_button pron-uk icon-audio app\"\n" +
                "              href=\"sound://_commonly%23_gbs_1.mp3\"></a><a\n" +
                "              class=\"sound audio_play_button pron-us icon-audio app\"\n" +
                "              href=\"sound://_commonly%23_uss_1.mp3\"></a><xt><chn>克里斯托弗通常被称为基特。</chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">the sudden increase in the birth rate after the Second World War, commonly referred to as the ‘baby boom’<xt><chn><ai>第二次世界大战后出生率突然上升，通常称为“婴儿潮”</ai></chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">avian influenza, commonly called bird flu<xt><chn><ai>禽流感，俗称禽流感</ai></chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">This is one of the most <span\n" +
                "              class=\"cl\">commonly used</span> methods.<a\n" +
                "              class=\"sound audio_play_button pron-uk icon-audio app\"\n" +
                "              href=\"sound://_commonly%23_brs_2.mp3\"></a><a\n" +
                "              class=\"sound audio_play_button pron-us icon-audio app\"\n" +
                "              href=\"sound://_commonly%23_uss_2.mp3\"></a><xt><chn>这是最常采用的方法之一。</chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">symptoms <span class=\"cl\">commonly associated with</span> HIV<xt><chn><ai>通常与艾滋病毒相关的症状</ai></chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">These fats are <span\n" +
                "              class=\"cl\">commonly found</span> in processed foods.<xt><chn>这些脂肪常见于加工食品。</chn></xt></span>\n" +
                "          </li>\n" +
                "          <li class=\"\" htag=\"li\"><span class=\"x\">one of the most commonly available substances on the planet<xt><chn><ai>地球上最常见的物质之一</ai></chn></xt></span>\n" +
                "          </li>\n" +
                "        </ul>\n" +
                "        <div class=\"collapse\" htag=\"div\" hclass=\"collapse\"><span class=\"unbox\"\n" +
                "                                                                 id=\"commonly_unbox_1\"\n" +
                "                                                                 unbox=\"extra_examples\"><span\n" +
                "            class=\"box_title\">Extra Examples<unboxx><chn>更多例句</chn></unboxx></span><ul\n" +
                "            class=\"examples\" hclass=\"examples\" htag=\"ul\"><li class=\"\" htag=\"li\"><span\n" +
                "            class=\"unx\">skin infections commonly seen in children<unxt><chn><ai>儿童常见的皮肤感染</ai></chn></unxt></span></li><li\n" +
                "            class=\"\" htag=\"li\"><span class=\"unx\">It's a list of additives in foods we commonly consume.<unxt><chn><ai>这是我们通常食用的食品中的添加剂清单。</ai></chn></unxt></span></li><li\n" +
                "            class=\"\" htag=\"li\"><span class=\"unx\">The lesson teaches you how to use the more commonly used features of the software.<unxt><chn><ai>本课程教您如何使用该软件的更常用功能。</ai></chn></unxt></span></li><li\n" +
                "            class=\"\" htag=\"li\"><span class=\"unx\">These companies are commonly thought of as models of efficiency.<unxt><chn><oald>这些公司通常被视为效率的典范。</oald></chn></unxt></span></li><li\n" +
                "            class=\"\" htag=\"li\"><span class=\"unx\">To receive satellite TV you need an antenna (commonly known as a dish).<unxt><chn><oald>要想接收卫星电视，得有天线（俗称“锅”）。</oald></chn></unxt></span></li></ul></span>\n" +
                "        </div>\n" +
                "        <div class=\"collapse\" htag=\"div\" hclass=\"collapse\"><span class=\"unbox\"\n" +
                "                                                                 unbox=\"snippet\"\n" +
                "                                                                 id=\"commonly_unbox_2\"><span\n" +
                "            class=\"box_title\">Oxford Collocations Dictionary<unboxx><chn>牛津搭配词典</chn></unboxx></span><span\n" +
                "            class=\"body\"><span class=\"p\"><span class=\"eb\">Commonly</span> is used with these adjectives: <ul\n" +
                "            class=\"collocs_list\"><li class=\"li\" bord=\"n\">available</li><li\n" +
                "            class=\"li\" bord=\"n\">used</li></ul></span><span class=\"p\"><span\n" +
                "            class=\"eb\">Commonly</span> is used with these verbs: <ul\n" +
                "            class=\"collocs_list\"><li class=\"li\" bord=\"n\">arise</li><li\n" +
                "            class=\"li\" bord=\"n\">assume</li><li\n" +
                "            class=\"li\">…</li></ul></span></span></span></div>\n" +
                "      </li>\n" +
                "    </ol>\n" +
                "  </div>\n" +
                "</div>\n" +
                "</body>";
        DictOaldBean dictOaldBean2 = HtmlOaldParser1.parseHtmlString(htmlString);
       System.out.println("from string: " + dictOaldBean2);
    }
}
