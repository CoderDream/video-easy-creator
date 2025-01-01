package com.coderdream.util.mdict.demo09;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsoupHtmlParser1 {

  public static void main(String[] args) throws IOException {
    String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\test\\java\\com\\coderdream\\util\\mdict\\chemistry.html";
    HtmlContentBean htmlContentBean = parseHtmlFile(htmlFilePath);
    System.out.println(htmlContentBean);
  }

  public static HtmlContentBean parseHtmlFile(String htmlFilePath)
    throws IOException {
    File input = new File(htmlFilePath);
    Document doc = Jsoup.parse(input, "UTF-8");

    HtmlContentBean htmlContentBean = new HtmlContentBean();
    htmlContentBean.setLinkHref(doc.select("link[href]").attr("href"));
    Elements scripts = doc.select("script[src]");
    if (scripts.size() >= 2) {
      htmlContentBean.setScriptSrc1(scripts.get(0).attr("src"));
      htmlContentBean.setScriptSrc2(scripts.get(1).attr("src"));
    }

    Element bodyContentElement = doc.selectFirst("body-content");
    if (bodyContentElement != null) {
      HtmlContentBean.BodyContent bodyContent = new HtmlContentBean.BodyContent();
      bodyContent.setClassAttr(bodyContentElement.attr("class"));

      Element entryContentElement = bodyContentElement.selectFirst(
        "#entryContent > div.entry");
      if (entryContentElement != null) {
        HtmlContentBean.EntryContent entryContent = new HtmlContentBean.EntryContent();
        entryContent.setClassAttr(entryContentElement.attr("class"));
        entryContent.setSum(entryContentElement.attr("sum"));
        entryContent.setHclass(entryContentElement.attr("hclass"));
        entryContent.setId(entryContentElement.attr("id"));
        entryContent.setHlength(entryContentElement.attr("hlength"));
        entryContent.setHtag(entryContentElement.attr("htag"));
        entryContent.setSk(entryContentElement.attr("sk"));
        entryContent.setIdmId(entryContentElement.attr("idm_id"));

        Element topContainerElement = entryContentElement.selectFirst(
          "div.top-container");
        if (topContainerElement != null) {
          HtmlContentBean.TopContainer topContainer = new HtmlContentBean.TopContainer();

          Element topGElement = topContainerElement.selectFirst("div.top-g");
          if (topGElement != null) {
            HtmlContentBean.TopG topG = new HtmlContentBean.TopG();

            Element webTopElement = topGElement.selectFirst("div.webtop");
            if (webTopElement != null) {
              HtmlContentBean.WebTop webTop = new HtmlContentBean.WebTop();

              Element symbolsElement = webTopElement.selectFirst("div.symbols");
              if (symbolsElement != null) {
                HtmlContentBean.Symbols symbols = new HtmlContentBean.Symbols();
                Element linkElement = symbolsElement.selectFirst("a");
                if (linkElement != null) {
                  HtmlContentBean.Link link = new HtmlContentBean.Link();
                  link.setHref(linkElement.attr("href"));
                  link.setStyle(linkElement.attr("style"));
                  Element spanElement = linkElement.selectFirst("span");
                  if (spanElement != null) {
                    HtmlContentBean.Span span = new HtmlContentBean.Span();
                    span.setClassAttr(spanElement.attr("class"));
                    span.setText(spanElement.text());
                    link.setOx3ksymSpan(span);
                  }
                  symbols.setOx3ksymLink(link);
                }
                webTop.setSymbols(symbols);
              }

              Element headwordElement = webTopElement.selectFirst(
                "h1.headword");
              if (headwordElement != null) {
                HtmlContentBean.Headword headword = new HtmlContentBean.Headword();
                headword.setId(headwordElement.attr("id"));
                headword.setHclass(headwordElement.attr("hclass"));
                headword.setOx3000(headwordElement.attr("ox3000"));
                headword.setHtag(headwordElement.attr("htag"));
                headword.setSyllable(headwordElement.attr("syllable"));
                headword.setText(headwordElement.text());
                webTop.setHeadword(headword);
              }
              Element posElement = webTopElement.selectFirst("span.pos");
              if (posElement != null) {
                webTop.setPos(posElement.text());
              }

              Element phoneticsElement = webTopElement.selectFirst(
                "span.phonetics");
              if (phoneticsElement != null) {
                HtmlContentBean.Phonetics phonetics = new HtmlContentBean.Phonetics();
                Element phonsBrElement = phoneticsElement.selectFirst(
                  "div.phons_br");
                if (phonsBrElement != null) {
                  HtmlContentBean.PhonBr phonBr = new HtmlContentBean.PhonBr();
                  phonBr.setHclass(phonsBrElement.attr("hclass"));
                  phonBr.setWd(phonsBrElement.attr("wd"));
                  phonBr.setHtag(phonsBrElement.attr("htag"));
                  phonBr.setGeo(phonsBrElement.attr("geo"));
                  Element audioBrButtonElement = phonsBrElement.selectFirst(
                    "a.sound");
                  if (audioBrButtonElement != null) {
                    HtmlContentBean.AudioButton audioButton = new HtmlContentBean.AudioButton();
                    audioButton.setClassAttr(
                      audioBrButtonElement.attr("class"));
                    audioButton.setHref(audioBrButtonElement.attr("href"));
                    audioButton.setTitle(audioBrButtonElement.attr("title"));
                    audioButton.setStyle(audioBrButtonElement.attr("style"));
                    audioButton.setValign(audioBrButtonElement.attr("valign"));
                    phonBr.setAudioButton(audioButton);
                  }
                  Element phonBrSpanElement = phonsBrElement.selectFirst(
                    "span.phon");
                  if (phonBrSpanElement != null) {
                    phonBr.setPhon(phonBrSpanElement.text());
                  }
                  phonetics.setPhonBr(phonBr);
                }
                Element phonsNAmElement = phoneticsElement.selectFirst(
                  "div.phons_n_am");
                if (phonsNAmElement != null) {
                  HtmlContentBean.PhonNAm phonNAm = new HtmlContentBean.PhonNAm();
                  phonNAm.setWd(phonsNAmElement.attr("wd"));
                  phonNAm.setHclass(phonsNAmElement.attr("hclass"));
                  phonNAm.setGeo(phonsNAmElement.attr("geo"));
                  phonNAm.setHtag(phonsNAmElement.attr("htag"));

                  Element audioNAmButtonElement = phonsNAmElement.selectFirst(
                    "a.sound");
                  if (audioNAmButtonElement != null) {
                    HtmlContentBean.AudioButton audioButton = new HtmlContentBean.AudioButton();
                    audioButton.setClassAttr(
                      audioNAmButtonElement.attr("class"));
                    audioButton.setHref(audioNAmButtonElement.attr("href"));
                    audioButton.setTitle(audioNAmButtonElement.attr("title"));
                    audioButton.setStyle(audioNAmButtonElement.attr("style"));
                    audioButton.setValign(audioNAmButtonElement.attr("valign"));
                    phonNAm.setAudioButton(audioButton);
                  }
                  Element phonNAmSpanElement = phonsNAmElement.selectFirst(
                    "span.phon");
                  if (phonNAmSpanElement != null) {
                    phonNAm.setPhon(phonNAmSpanElement.text());
                  }
                  phonetics.setPhonNAm(phonNAm);
                }
                webTop.setPhonetics(phonetics);
              }

              Element grammarElement = webTopElement.selectFirst(
                "span.grammar");
              if (grammarElement != null) {
                webTop.setGrammar(grammarElement.text());
              }
              topG.setWebTop(webTop);
            }
            topContainer.setTopG(topG);
          }
          entryContent.setTopContainer(topContainer);
        }

        List<HtmlContentBean.SenseGroup> senseGroupList = new ArrayList<>();
        Elements liSenseDivElements = entryContentElement.select(
          "ol.senses_multiple > div.li_sense");
        for (Element liSenseDivElement : liSenseDivElements) {
          HtmlContentBean.SenseGroup senseGroup = new HtmlContentBean.SenseGroup();
          senseGroup.setClassAttr(liSenseDivElement.attr("class"));
          List<HtmlContentBean.LiSense> liSenseList = new ArrayList<>();
          Element liSenseBeforeElement = liSenseDivElement.selectFirst(
            "div.li_sense_before");
          Element senseElement = liSenseDivElement.selectFirst("li.sense");

          if (senseElement != null) {
            HtmlContentBean.LiSense liSense = new HtmlContentBean.LiSense();
            if (liSenseBeforeElement != null) {
              liSense.setLiSenseBefore(liSenseBeforeElement.text());
            }
            HtmlContentBean.Sense sense = new HtmlContentBean.Sense();
            sense.setOx3000(senseElement.attr("ox3000"));
            sense.setSensenum(senseElement.attr("sensenum"));
            sense.setHtag(senseElement.attr("htag"));
            sense.setHclass(senseElement.attr("hclass"));
            sense.setId(senseElement.attr("id"));
            sense.setCefr(senseElement.attr("cefr"));

            Element senseTopElement = senseElement.selectFirst("span.sensetop");
            if (senseTopElement != null) {
              HtmlContentBean.SenseTop senseTop = new HtmlContentBean.SenseTop();
              senseTop.setHtag(senseTopElement.attr("htag"));
              senseTop.setHclass(senseTopElement.attr("hclass"));
              Element symbolsElement = senseTopElement.selectFirst(
                "div.symbols");
              if (symbolsElement != null) {
                HtmlContentBean.Symbols symbols = new HtmlContentBean.Symbols();
                Element linkElement = symbolsElement.selectFirst("a");
                if (linkElement != null) {
                  HtmlContentBean.Link link = new HtmlContentBean.Link();
                  link.setHref(linkElement.attr("href"));
                  Element spanElement = linkElement.selectFirst("span");
                  if (spanElement != null) {
                    HtmlContentBean.Span span = new HtmlContentBean.Span();
                    span.setClassAttr(spanElement.attr("class"));
                    span.setText(spanElement.text());
                    link.setOx3ksymSpan(span);
                  }
                  symbols.setOx3ksymLink(link);
                }
                senseTop.setSymbols(symbols);
              }
              Element labelsElement = senseTopElement.selectFirst(
                "span.labels");
              if (labelsElement != null) {
                HtmlContentBean.Labels labels = new HtmlContentBean.Labels();
                labels.setHclass(labelsElement.attr("hclass"));
                labels.setHtag(labelsElement.attr("htag"));
                labels.setTitle(labelsElement.attr("title"));
                Element labelxElement = labelsElement.selectFirst("labelx");
                if (labelxElement != null) {
                  HtmlContentBean.Labelx labelx = new HtmlContentBean.Labelx();
                  Element chnSimpleElement = labelxElement.selectFirst(
                    "chn.simple");
                  if (chnSimpleElement != null) {
                    HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                    chnSimple.setText(chnSimpleElement.text());
                    labelx.setSimple(chnSimple);
                  }
                  Element chnTraditionalElement = labelxElement.selectFirst(
                    "chn.traditional");
                  if (chnTraditionalElement != null) {
                    HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                    chnTraditional.setText(chnTraditionalElement.text());
                    labelx.setTraditional(chnTraditional);
                  }
                  labels.setLabelx(labelx);
                }
                senseTop.setLabels(labels);
              }
              sense.setSenseTop(senseTop);
            }

            Element defElement = senseElement.selectFirst("span.def");
            if (defElement != null) {
              sense.setDef(defElement.text());
            }

            Element deftElement = senseElement.selectFirst("deft");
            if (deftElement != null) {
              HtmlContentBean.Deft deft = new HtmlContentBean.Deft();
              Element chnSimpleElement = deftElement.selectFirst("chn.simple");
              if (chnSimpleElement != null) {
                HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                chnSimple.setText(chnSimpleElement.text());
                deft.setSimple(chnSimple);
              }
              Element chnTraditionalElement = deftElement.selectFirst(
                "chn.traditional");
              if (chnTraditionalElement != null) {
                HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                chnTraditional.setText(chnTraditionalElement.text());
                deft.setTraditional(chnTraditional);
              }
              sense.setDeft(deft);
            }

            List<HtmlContentBean.ExampleGroup> exampleGroupList = new ArrayList<>();
            Elements exampleElements = senseElement.select("ul.examples > li");
            for (Element exampleElement : exampleElements) {
              HtmlContentBean.ExampleGroup exampleGroup = new HtmlContentBean.ExampleGroup();
              exampleGroup.setHtag(exampleElement.attr("htag"));
              Element exTextElement = exampleElement.selectFirst("div.exText");
              if (exTextElement != null) {
                HtmlContentBean.ExText exText = new HtmlContentBean.ExText();
                Element spanXElement = exTextElement.selectFirst("span.x");
                if (spanXElement != null) {
                  exText.setX(spanXElement.text());
                  Element xtElement = spanXElement.selectFirst("xt");
                  if (xtElement != null) {
                    HtmlContentBean.Xt xt = new HtmlContentBean.Xt();
                    Element chnSimpleElement = xtElement.selectFirst(
                      "chn.simple");
                    if (chnSimpleElement != null) {
                      HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                      chnSimple.setText(chnSimpleElement.text());
                      Element aiElement = chnSimpleElement.selectFirst("ai");
                      if (aiElement != null) {
                        HtmlContentBean.Ai ai = new HtmlContentBean.Ai();
                        ai.setText(aiElement.text());
                        chnSimple.setAi(ai);
                      }
                      xt.setSimple(chnSimple);
                    }
                    Element chnTraditionalElement = xtElement.selectFirst(
                      "chn.traditional");
                    if (chnTraditionalElement != null) {
                      HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                      chnTraditional.setText(chnTraditionalElement.text());
                      Element aiElement = chnTraditionalElement.selectFirst(
                        "ai");
                      if (aiElement != null) {
                        HtmlContentBean.Ai ai = new HtmlContentBean.Ai();
                        ai.setText(aiElement.text());
                        chnTraditional.setAi(ai);
                      }
                      xt.setTraditional(chnTraditional);
                    }
                    exText.setXt(xt);
                  }
                  exampleGroup.setExText(exText);
                }
              }
              Element exampleAudioAiElement = exampleElement.selectFirst(
                "example-audio-ai");
              if (exampleAudioAiElement != null) {
                HtmlContentBean.ExampleAudioAi exampleAudioAi = new HtmlContentBean.ExampleAudioAi();
                Element audioUkElement = exampleAudioAiElement.selectFirst(
                  "a.audio_uk");
                if (audioUkElement != null) {
                  HtmlContentBean.AudioUK audioUK = new HtmlContentBean.AudioUK();
                  audioUK.setHref(audioUkElement.attr("href"));
                  exampleAudioAi.setAudioUK(audioUK);
                }
                Element audioUsElement = exampleAudioAiElement.selectFirst(
                  "a.audio_us");
                if (audioUsElement != null) {
                  HtmlContentBean.AudioUS audioUS = new HtmlContentBean.AudioUS();
                  audioUS.setHref(audioUsElement.attr("href"));
                  exampleAudioAi.setAudioUS(audioUS);
                }
                exampleGroup.setExampleAudioAi(exampleAudioAi);
              }
              Element exampleAudioElement = exampleElement.selectFirst(
                "example-audio");
              if (exampleAudioElement != null) {
                HtmlContentBean.ExampleAudio exampleAudio = new HtmlContentBean.ExampleAudio();
                Element audioUkElement = exampleAudioElement.selectFirst(
                  "a.audio_uk");
                if (audioUkElement != null) {
                  HtmlContentBean.AudioUK audioUK = new HtmlContentBean.AudioUK();
                  audioUK.setHref(audioUkElement.attr("href"));
                  exampleAudio.setAudioUK(audioUK);
                }
                Element audioUsElement = exampleAudioElement.selectFirst(
                  "a.audio_us");
                if (audioUsElement != null) {
                  HtmlContentBean.AudioUS audioUS = new HtmlContentBean.AudioUS();
                  audioUS.setHref(audioUsElement.attr("href"));
                  exampleAudio.setAudioUS(audioUS);
                }
                exampleGroup.setExampleAudio(exampleAudio);
              }
              exampleGroupList.add(exampleGroup);
            }
            sense.setExampleGroups(exampleGroupList);
            Element xrefsElement = senseElement.selectFirst("span.xrefs");
            if (xrefsElement != null) {
              HtmlContentBean.Xrefs xrefs = new HtmlContentBean.Xrefs();
              xrefs.setXt(xrefsElement.attr("xt"));
              xrefs.setHclass(xrefsElement.attr("hclass"));
              xrefs.setHtag(xrefsElement.attr("htag"));
              Element prefixElement = xrefsElement.selectFirst("span.prefix");
              if (prefixElement != null) {
                xrefs.setPrefix(prefixElement.text());
              }
              Element refElement = xrefsElement.selectFirst("a.Ref");
              if (refElement != null) {
                HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                ref.setHref(refElement.attr("href"));
                ref.setTitle(refElement.attr("title"));
                Element xrGElement = refElement.selectFirst("span.xr-g");
                if (xrGElement != null) {
                  HtmlContentBean.XrG xrG = new HtmlContentBean.XrG();
                  xrG.setDict(xrGElement.attr("dict"));
                  xrG.setBord(xrGElement.attr("bord"));
                  xrG.setHref(xrGElement.attr("href"));
                  Element xhElement = xrGElement.selectFirst("span.xh");
                  if (xhElement != null) {
                    HtmlContentBean.Xh xh = new HtmlContentBean.Xh();
                    xh.setText(xhElement.text());
                    xrG.setXh(xh);
                  }
                  ref.setXrG(xrG);
                }
                xrefs.setRef(ref);
              }

              sense.setXrefs(xrefs);
            }

            Element collapseElement = senseElement.selectFirst("div.collapse");
            if (collapseElement != null) {
              HtmlContentBean.Collapse collapse = new HtmlContentBean.Collapse();
              collapse.setHclass(collapseElement.attr("hclass"));
              collapse.setHtag(collapseElement.attr("htag"));
              Element unboxElement = collapseElement.selectFirst("span.unbox");
              if (unboxElement != null) {
                HtmlContentBean.Unbox unbox = new HtmlContentBean.Unbox();
                unbox.setId(unboxElement.attr("id"));
                unbox.setUnbox(unboxElement.attr("unbox"));

                Element boxTitleElement = unboxElement.selectFirst(
                  "span.box_title");
                if (boxTitleElement != null) {
                  HtmlContentBean.BoxTitle boxTitle = new HtmlContentBean.BoxTitle();
                  Element textTitleElement = boxTitleElement.selectFirst(
                    "div.text_title");
                  if (textTitleElement != null) {
                    HtmlContentBean.TextTitle textTitle = new HtmlContentBean.TextTitle();
                    textTitle.setText(textTitleElement.text());
                    boxTitle.setTextTitle(textTitle);
                  }
                  unbox.setBoxTitle(boxTitle);
                }
                Element unboxxElement = unboxElement.selectFirst("unboxx");
                if (unboxxElement != null) {
                  HtmlContentBean.Unboxx unboxx = new HtmlContentBean.Unboxx();
                  Element chnSimpleElement = unboxxElement.selectFirst(
                    "chn.simple");
                  if (chnSimpleElement != null) {
                    HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                    chnSimple.setText(chnSimpleElement.text());
                    unboxx.setSimple(chnSimple);
                  }
                  Element chnTraditionalElement = unboxxElement.selectFirst(
                    "chn.traditional");
                  if (chnTraditionalElement != null) {
                    HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                    chnTraditional.setText(chnTraditionalElement.text());
                    unboxx.setTraditional(chnTraditional);
                  }
                  unbox.setUnboxx(unboxx);
                }
                Element bodyElement = unboxElement.selectFirst("span.body");
                if (bodyElement != null) {
                  unbox.setBody(bodyElement.text());
                  List<HtmlContentBean.Li> liList = new ArrayList<>();
                  Elements liElements = bodyElement.select("ul.bullet > li.li");
                  for (Element liElement : liElements) {
                    HtmlContentBean.Li li = new HtmlContentBean.Li();
                    Element refElement = liElement.selectFirst("a.Ref");
                    if (refElement != null) {
                      HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                      ref.setHref(refElement.attr("href"));
                      ref.setTitle(refElement.attr("title"));
                      Element spanElement = refElement.selectFirst("span.xref");
                      if (spanElement != null) {
                        li.setText(spanElement.text());
                      }
                      li.setRef(ref);
                    }
                    liList.add(li);
                  }
                  unbox.setLiList(liList);

                  List<HtmlContentBean.CollocsList> collocsLists = new ArrayList<>();
                  Elements collocsListElements = bodyElement.select(
                    "ul.collocs_list > li.li");
                  for (Element collocsListElement : collocsListElements) {
                    HtmlContentBean.CollocsList collocsList = new HtmlContentBean.CollocsList();
                    collocsList.setClassAttr(collocsListElement.attr("class"));
                    collocsList.setText(collocsListElement.text());
                    collocsLists.add(collocsList);
                  }
                  unbox.setCollocsLists(collocsLists);
                }
                collapse.setUnbox(unbox);
              }
              sense.setCollapse(collapse);
            }
            Element topicGElement = senseElement.selectFirst("span.topic-g");
            if (topicGElement != null) {
              HtmlContentBean.TopicG topicG = new HtmlContentBean.TopicG();
              Element prefixElement = topicGElement.selectFirst("span.prefix");
              if (prefixElement != null) {
                topicG.setPrefix(prefixElement.text());
              }
              List<HtmlContentBean.Ref> refList = new ArrayList<>();
              Elements refElements = topicGElement.select("a.Ref");
              for (Element refElement : refElements) {
                HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                ref.setHref(refElement.attr("href"));
                ref.setTitle(refElement.attr("title"));
                Element topicElement = refElement.selectFirst("span.topic");
                if (topicElement != null) {
                  HtmlContentBean.Topic topic = new HtmlContentBean.Topic();
                  topic.setHref(topicElement.attr("href"));
                  Element topicNameElement = topicElement.selectFirst(
                    "span.topic_name");
                  if (topicNameElement != null) {
                    HtmlContentBean.TopicName topicName = new HtmlContentBean.TopicName();
                    topicName.setText(topicNameElement.text());
                    topic.setTopicName(topicName);
                  }
                  Element topicCefrElement = topicElement.selectFirst(
                    "span.topic_cefr");
                  if (topicCefrElement != null) {
                    HtmlContentBean.TopicCefr topicCefr = new HtmlContentBean.TopicCefr();
                    topicCefr.setText(topicCefrElement.text());
                    topic.setTopicCefr(topicCefr);
                  }
                  ref.setTopic(topic);
                }
                refList.add(ref);
              }
              topicG.setRefList(refList);
              sense.setTopicG(topicG);
            }

            liSense.setSense(sense);
            liSenseList.add(liSense);
          }
          senseGroup.setLiSenseList(liSenseList);
          senseGroupList.add(senseGroup);
        }
        entryContent.setSenseGroups(senseGroupList);
        bodyContent.setEntryContent(entryContent);
      }
      htmlContentBean.setBodyContent(bodyContent);
    }
    return htmlContentBean;
  }
}
