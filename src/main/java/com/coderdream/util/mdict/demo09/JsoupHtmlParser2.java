package com.coderdream.util.mdict.demo09;

import com.coderdream.entity.MultiLanguageContent;
import com.coderdream.entity.WordDetail;
import com.coderdream.entity.WordPronunciation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHtmlParser2 {

    public static void main(String[] args) throws IOException {
        String htmlFilePath = "D:\\04_GitHub\\video-easy-creator\\src\\test\\java\\com\\coderdream\\util\\mdict\\chemistry.html";
        WordDetail wordDetail = parseHtmlFileToWordDetail(htmlFilePath);
        System.out.println(wordDetail);
    }




     public static WordDetail parseHtmlFileToWordDetail(String htmlFilePath) throws IOException {
         File input = new File(htmlFilePath);
//         Document doc = Jsoup.parse(input, "UTF-8");

         HtmlContentBean htmlContentBean = parseHtmlFile(htmlFilePath);
         if (htmlContentBean == null || htmlContentBean.getBodyContent() == null
         || htmlContentBean.getBodyContent().getEntryContent() == null){
              return null;
         }
         HtmlContentBean.EntryContent entryContent = htmlContentBean.getBodyContent().getEntryContent();
         WordDetail wordDetail = new WordDetail();

         // 单词
         if(entryContent.getTopContainer() != null
         && entryContent.getTopContainer().getTopG() !=null
         && entryContent.getTopContainer().getTopG().getWebTop() != null
         && entryContent.getTopContainer().getTopG().getWebTop().getHeadword()!=null){
              wordDetail.setWord(entryContent.getTopContainer().getTopG().getWebTop().getHeadword().getText());
         }

        // 词性
         List<String> partOfSpeechList = new ArrayList<>();
        if(entryContent.getTopContainer() != null
        && entryContent.getTopContainer().getTopG() !=null
        && entryContent.getTopContainer().getTopG().getWebTop() != null
        && entryContent.getTopContainer().getTopG().getWebTop().getPos() != null){
             partOfSpeechList.add(entryContent.getTopContainer().getTopG().getWebTop().getPos());
             wordDetail.setPartOfSpeechList(partOfSpeechList);
        }


        // 音标
         WordPronunciation wordPronunciation = new WordPronunciation();
         if(entryContent.getTopContainer() != null
         && entryContent.getTopContainer().getTopG() !=null
         && entryContent.getTopContainer().getTopG().getWebTop() != null
         && entryContent.getTopContainer().getTopG().getWebTop().getPhonetics() != null){
               HtmlContentBean.Phonetics phonetics = entryContent.getTopContainer().getTopG().getWebTop().getPhonetics();
               if(phonetics.getPhonBr()!=null){
                    wordPronunciation.setBritishPronunciation(phonetics.getPhonBr().getPhon());
               }
             if(phonetics.getPhonNAm()!=null){
                 wordPronunciation.setAmericanPronunciation(phonetics.getPhonNAm().getPhon());
             }
             wordDetail.setWordPronunciation(wordPronunciation);
         }

         // 解释和句子
         List<MultiLanguageContent> definitionList = new ArrayList<>();
         List<MultiLanguageContent> sentenceList = new ArrayList<>();

         if(entryContent.getSenseGroups() != null){
            for(HtmlContentBean.SenseGroup senseGroup : entryContent.getSenseGroups()){
                if(senseGroup.getLiSenseList()!=null){
                    for(HtmlContentBean.LiSense liSense : senseGroup.getLiSenseList()){
                         HtmlContentBean.Sense sense = liSense.getSense();
                          if(sense!=null){
                               if(sense.getDef() !=null || (sense.getDeft()!=null
                                       && (sense.getDeft().getSimple()!=null || sense.getDeft().getTraditional() != null))){
                                    MultiLanguageContent definition = new MultiLanguageContent();
                                   if(sense.getDef()!=null){
                                        definition.setContentEnglish(sense.getDef());
                                   }
                                   if(sense.getDeft()!=null){
                                        if(sense.getDeft().getSimple()!=null){
                                            definition.setContentSimple(sense.getDeft().getSimple().getText());
                                        }
                                         if(sense.getDeft().getTraditional()!=null){
                                            definition.setContentTraditional(sense.getDeft().getTraditional().getText());
                                        }
                                   }

                                   definitionList.add(definition);
                               }
                              if(sense.getExampleGroups() != null){
                                 for(HtmlContentBean.ExampleGroup exampleGroup : sense.getExampleGroups()){
                                      MultiLanguageContent sentence = new MultiLanguageContent();
                                      if(exampleGroup.getExText() != null){
                                          if(exampleGroup.getExText().getX() != null){
                                              sentence.setContentEnglish(exampleGroup.getExText().getX());
                                          }
                                          if(exampleGroup.getExText().getXt() != null){
                                             if(exampleGroup.getExText().getXt().getSimple() !=null){
                                                 sentence.setContentSimple(exampleGroup.getExText().getXt().getSimple().getText());
                                             }
                                             if(exampleGroup.getExText().getXt().getTraditional() !=null){
                                                 sentence.setContentTraditional(exampleGroup.getExText().getXt().getTraditional().getText());
                                             }
                                          }
                                      }
                                     if(Objects.nonNull(sentence.getContentEnglish()) ||
                                        Objects.nonNull(sentence.getContentSimple()) ||
                                        Objects.nonNull(sentence.getContentTraditional())){
                                          sentenceList.add(sentence);
                                      }
                                 }
                              }
                          }
                    }
                }
            }
             wordDetail.setDefinitionList(definitionList);
             wordDetail.setSentenceList(sentenceList);
         }
        return wordDetail;
    }
    public static HtmlContentBean parseHtmlFile(String htmlFilePath) throws IOException {
        File input = new File(htmlFilePath);
        Document doc = Jsoup.parse(input, "UTF-8");

        HtmlContentBean htmlContentBean = new HtmlContentBean();
        htmlContentBean.setLinkHref(doc.select("link[href]").attr("href"));
        org.jsoup.select.Elements scripts = doc.select("script[src]");
        if(scripts.size() >= 2){
            htmlContentBean.setScriptSrc1(scripts.get(0).attr("src"));
            htmlContentBean.setScriptSrc2(scripts.get(1).attr("src"));
        }

        org.jsoup.nodes.Element bodyContentElement = doc.selectFirst("body-content");
        if(bodyContentElement!= null){
            HtmlContentBean.BodyContent bodyContent = new HtmlContentBean.BodyContent();
            bodyContent.setClassAttr(bodyContentElement.attr("class"));

            org.jsoup.nodes.Element entryContentElement = bodyContentElement.selectFirst("#entryContent > div.entry");
            if(entryContentElement!= null){
                HtmlContentBean.EntryContent entryContent = new HtmlContentBean.EntryContent();
                entryContent.setClassAttr(entryContentElement.attr("class"));
                entryContent.setSum(entryContentElement.attr("sum"));
                entryContent.setHclass(entryContentElement.attr("hclass"));
                entryContent.setId(entryContentElement.attr("id"));
                entryContent.setHlength(entryContentElement.attr("hlength"));
                entryContent.setHtag(entryContentElement.attr("htag"));
                entryContent.setSk(entryContentElement.attr("sk"));
                entryContent.setIdmId(entryContentElement.attr("idm_id"));

                org.jsoup.nodes.Element topContainerElement = entryContentElement.selectFirst("div.top-container");
                if(topContainerElement != null){
                    HtmlContentBean.TopContainer topContainer = new HtmlContentBean.TopContainer();

                    org.jsoup.nodes.Element topGElement = topContainerElement.selectFirst("div.top-g");
                    if(topGElement != null){
                        HtmlContentBean.TopG topG = new HtmlContentBean.TopG();

                        org.jsoup.nodes.Element webTopElement = topGElement.selectFirst("div.webtop");
                        if(webTopElement != null){
                            HtmlContentBean.WebTop webTop = new HtmlContentBean.WebTop();

                            org.jsoup.nodes.Element symbolsElement = webTopElement.selectFirst("div.symbols");
                            if(symbolsElement!=null){
                                HtmlContentBean.Symbols symbols = new HtmlContentBean.Symbols();
                                org.jsoup.nodes.Element linkElement = symbolsElement.selectFirst("a");
                                if(linkElement!=null){
                                    HtmlContentBean.Link link = new HtmlContentBean.Link();
                                    link.setHref(linkElement.attr("href"));
                                    link.setStyle(linkElement.attr("style"));
                                    org.jsoup.nodes.Element spanElement = linkElement.selectFirst("span");
                                    if(spanElement!=null){
                                        HtmlContentBean.Span span = new HtmlContentBean.Span();
                                        span.setClassAttr(spanElement.attr("class"));
                                        span.setText(spanElement.text());
                                        link.setOx3ksymSpan(span);
                                    }
                                    symbols.setOx3ksymLink(link);
                                }
                                webTop.setSymbols(symbols);
                            }

                            org.jsoup.nodes.Element headwordElement = webTopElement.selectFirst("h1.headword");
                            if (headwordElement!= null) {
                                HtmlContentBean.Headword headword = new HtmlContentBean.Headword();
                                headword.setId(headwordElement.attr("id"));
                                headword.setHclass(headwordElement.attr("hclass"));
                                headword.setOx3000(headwordElement.attr("ox3000"));
                                headword.setHtag(headwordElement.attr("htag"));
                                headword.setSyllable(headwordElement.attr("syllable"));
                                headword.setText(headwordElement.text());
                                webTop.setHeadword(headword);
                            }
                            org.jsoup.nodes.Element posElement = webTopElement.selectFirst("span.pos");
                            if(posElement!=null){
                                webTop.setPos(posElement.text());
                            }

                            org.jsoup.nodes.Element phoneticsElement = webTopElement.selectFirst("span.phonetics");
                            if(phoneticsElement!=null){
                                HtmlContentBean.Phonetics phonetics = new HtmlContentBean.Phonetics();
                                org.jsoup.nodes.Element phonsBrElement = phoneticsElement.selectFirst("div.phons_br");
                                if(phonsBrElement!=null){
                                    HtmlContentBean.PhonBr phonBr = new HtmlContentBean.PhonBr();
                                    phonBr.setHclass(phonsBrElement.attr("hclass"));
                                    phonBr.setWd(phonsBrElement.attr("wd"));
                                    phonBr.setHtag(phonsBrElement.attr("htag"));
                                    phonBr.setGeo(phonsBrElement.attr("geo"));
                                    org.jsoup.nodes.Element audioBrButtonElement = phonsBrElement.selectFirst("a.sound");
                                    if(audioBrButtonElement!= null){
                                        HtmlContentBean.AudioButton audioButton = new HtmlContentBean.AudioButton();
                                        audioButton.setClassAttr(audioBrButtonElement.attr("class"));
                                        audioButton.setHref(audioBrButtonElement.attr("href"));
                                        audioButton.setTitle(audioBrButtonElement.attr("title"));
                                        audioButton.setStyle(audioBrButtonElement.attr("style"));
                                        audioButton.setValign(audioBrButtonElement.attr("valign"));
                                        phonBr.setAudioButton(audioButton);
                                    }
                                    org.jsoup.nodes.Element phonBrSpanElement = phonsBrElement.selectFirst("span.phon");
                                    if(phonBrSpanElement!=null){
                                        phonBr.setPhon(phonBrSpanElement.text());
                                    }
                                    phonetics.setPhonBr(phonBr);
                                }
                                org.jsoup.nodes.Element phonsNAmElement = phoneticsElement.selectFirst("div.phons_n_am");
                                if(phonsNAmElement!=null){
                                    HtmlContentBean.PhonNAm phonNAm = new HtmlContentBean.PhonNAm();
                                    phonNAm.setWd(phonsNAmElement.attr("wd"));
                                    phonNAm.setHclass(phonsNAmElement.attr("hclass"));
                                    phonNAm.setGeo(phonsNAmElement.attr("geo"));
                                    phonNAm.setHtag(phonsNAmElement.attr("htag"));

                                    org.jsoup.nodes.Element audioNAmButtonElement = phonsNAmElement.selectFirst("a.sound");
                                    if(audioNAmButtonElement!= null){
                                        HtmlContentBean.AudioButton audioButton = new HtmlContentBean.AudioButton();
                                        audioButton.setClassAttr(audioNAmButtonElement.attr("class"));
                                        audioButton.setHref(audioNAmButtonElement.attr("href"));
                                        audioButton.setTitle(audioNAmButtonElement.attr("title"));
                                        audioButton.setStyle(audioNAmButtonElement.attr("style"));
                                        audioButton.setValign(audioNAmButtonElement.attr("valign"));
                                        phonNAm.setAudioButton(audioButton);
                                    }
                                    org.jsoup.nodes.Element phonNAmSpanElement = phonsNAmElement.selectFirst("span.phon");
                                    if(phonNAmSpanElement!=null){
                                        phonNAm.setPhon(phonNAmSpanElement.text());
                                    }
                                    phonetics.setPhonNAm(phonNAm);
                                }
                                webTop.setPhonetics(phonetics);
                            }

                            org.jsoup.nodes.Element grammarElement = webTopElement.selectFirst("span.grammar");
                            if(grammarElement!=null){
                                webTop.setGrammar(grammarElement.text());
                            }
                            topG.setWebTop(webTop);
                        }
                        topContainer.setTopG(topG);
                    }
                    entryContent.setTopContainer(topContainer);
                }

                List<HtmlContentBean.SenseGroup> senseGroupList = new ArrayList<>();
                org.jsoup.select.Elements liSenseDivElements = entryContentElement.select("ol.senses_multiple > div.li_sense");
                for(org.jsoup.nodes.Element liSenseDivElement : liSenseDivElements){
                    HtmlContentBean.SenseGroup senseGroup = new HtmlContentBean.SenseGroup();
                    senseGroup.setClassAttr(liSenseDivElement.attr("class"));
                    List<HtmlContentBean.LiSense> liSenseList = new ArrayList<>();
                    org.jsoup.nodes.Element liSenseBeforeElement = liSenseDivElement.selectFirst("div.li_sense_before");
                    org.jsoup.nodes.Element senseElement = liSenseDivElement.selectFirst("li.sense");

                    if(senseElement != null){
                        HtmlContentBean.LiSense liSense = new HtmlContentBean.LiSense();
                        if(liSenseBeforeElement!= null){
                            liSense.setLiSenseBefore(liSenseBeforeElement.text());
                        }
                        HtmlContentBean.Sense sense = new HtmlContentBean.Sense();
                        sense.setOx3000(senseElement.attr("ox3000"));
                        sense.setSensenum(senseElement.attr("sensenum"));
                        sense.setHtag(senseElement.attr("htag"));
                        sense.setHclass(senseElement.attr("hclass"));
                        sense.setId(senseElement.attr("id"));
                        sense.setCefr(senseElement.attr("cefr"));

                        org.jsoup.nodes.Element senseTopElement = senseElement.selectFirst("span.sensetop");
                        if(senseTopElement!= null){
                            HtmlContentBean.SenseTop senseTop = new HtmlContentBean.SenseTop();
                            senseTop.setHtag(senseTopElement.attr("htag"));
                            senseTop.setHclass(senseTopElement.attr("hclass"));
                            org.jsoup.nodes.Element symbolsElement = senseTopElement.selectFirst("div.symbols");
                            if(symbolsElement!=null){
                                HtmlContentBean.Symbols symbols = new HtmlContentBean.Symbols();
                                org.jsoup.nodes.Element linkElement = symbolsElement.selectFirst("a");
                                if(linkElement!=null){
                                    HtmlContentBean.Link link = new HtmlContentBean.Link();
                                    link.setHref(linkElement.attr("href"));
                                    org.jsoup.nodes.Element spanElement = linkElement.selectFirst("span");
                                    if(spanElement!=null){
                                        HtmlContentBean.Span span = new HtmlContentBean.Span();
                                        span.setClassAttr(spanElement.attr("class"));
                                        span.setText(spanElement.text());
                                        link.setOx3ksymSpan(span);
                                    }
                                    symbols.setOx3ksymLink(link);
                                }
                                senseTop.setSymbols(symbols);
                            }
                            org.jsoup.nodes.Element labelsElement = senseTopElement.selectFirst("span.labels");
                            if(labelsElement!=null){
                                HtmlContentBean.Labels labels = new HtmlContentBean.Labels();
                                labels.setHclass(labelsElement.attr("hclass"));
                                labels.setHtag(labelsElement.attr("htag"));
                                labels.setTitle(labelsElement.attr("title"));
                                org.jsoup.nodes.Element labelxElement = labelsElement.selectFirst("labelx");
                                if(labelxElement!=null){
                                    HtmlContentBean.Labelx labelx = new HtmlContentBean.Labelx();
                                    org.jsoup.nodes.Element chnSimpleElement = labelxElement.selectFirst("chn.simple");
                                    if(chnSimpleElement!=null){
                                        HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                                        chnSimple.setText(chnSimpleElement.text());
                                        labelx.setSimple(chnSimple);
                                    }
                                    org.jsoup.nodes.Element chnTraditionalElement = labelxElement.selectFirst("chn.traditional");
                                    if(chnTraditionalElement!=null){
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

                        org.jsoup.nodes.Element defElement = senseElement.selectFirst("span.def");
                        if(defElement!=null){
                            sense.setDef(defElement.text());
                        }

                        org.jsoup.nodes.Element deftElement = senseElement.selectFirst("deft");
                        if(deftElement!=null){
                            HtmlContentBean.Deft deft = new HtmlContentBean.Deft();
                            org.jsoup.nodes.Element chnSimpleElement = deftElement.selectFirst("chn.simple");
                            if(chnSimpleElement!=null){
                                HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                                chnSimple.setText(chnSimpleElement.text());
                                deft.setSimple(chnSimple);
                            }
                            org.jsoup.nodes.Element chnTraditionalElement = deftElement.selectFirst("chn.traditional");
                            if(chnTraditionalElement!=null){
                                HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                                chnTraditional.setText(chnTraditionalElement.text());
                                deft.setTraditional(chnTraditional);
                            }
                            sense.setDeft(deft);
                        }

                        List<HtmlContentBean.ExampleGroup> exampleGroupList = new ArrayList<>();
                        org.jsoup.select.Elements exampleElements = senseElement.select("ul.examples > li");
                        for(org.jsoup.nodes.Element exampleElement: exampleElements){
                            HtmlContentBean.ExampleGroup exampleGroup = new HtmlContentBean.ExampleGroup();
                            exampleGroup.setHtag(exampleElement.attr("htag"));
                            org.jsoup.nodes.Element exTextElement = exampleElement.selectFirst("div.exText");
                            if(exTextElement!=null){
                                HtmlContentBean.ExText exText = new HtmlContentBean.ExText();
                                org.jsoup.nodes.Element spanXElement = exTextElement.selectFirst("span.x");
                                if(spanXElement!=null){
                                    exText.setX(spanXElement.text());
                                    org.jsoup.nodes.Element xtElement = spanXElement.selectFirst("xt");
                                    if(xtElement!=null){
                                        HtmlContentBean.Xt xt = new HtmlContentBean.Xt();
                                        org.jsoup.nodes.Element chnSimpleElement = xtElement.selectFirst("chn.simple");
                                        if(chnSimpleElement!=null){
                                            HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                                            chnSimple.setText(chnSimpleElement.text());
                                            org.jsoup.nodes.Element aiElement = chnSimpleElement.selectFirst("ai");
                                            if(aiElement!=null){
                                                HtmlContentBean.Ai ai = new HtmlContentBean.Ai();
                                                ai.setText(aiElement.text());
                                                chnSimple.setAi(ai);
                                            }
                                            xt.setSimple(chnSimple);
                                        }
                                        org.jsoup.nodes.Element chnTraditionalElement = xtElement.selectFirst("chn.traditional");
                                        if(chnTraditionalElement!=null){
                                            HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                                            chnTraditional.setText(chnTraditionalElement.text());
                                            org.jsoup.nodes.Element aiElement = chnTraditionalElement.selectFirst("ai");
                                            if(aiElement!=null){
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
                            org.jsoup.nodes.Element exampleAudioAiElement = exampleElement.selectFirst("example-audio-ai");
                            if(exampleAudioAiElement!=null){
                                HtmlContentBean.ExampleAudioAi exampleAudioAi = new HtmlContentBean.ExampleAudioAi();
                                org.jsoup.nodes.Element audioUkElement = exampleAudioAiElement.selectFirst("a.audio_uk");
                                if(audioUkElement!=null){
                                    HtmlContentBean.AudioUK audioUK = new HtmlContentBean.AudioUK();
                                    audioUK.setHref(audioUkElement.attr("href"));
                                    exampleAudioAi.setAudioUK(audioUK);
                                }
                                org.jsoup.nodes.Element audioUsElement = exampleAudioAiElement.selectFirst("a.audio_us");
                                if(audioUsElement!=null){
                                    HtmlContentBean.AudioUS audioUS = new HtmlContentBean.AudioUS();
                                    audioUS.setHref(audioUsElement.attr("href"));
                                    exampleAudioAi.setAudioUS(audioUS);
                                }
                                exampleGroup.setExampleAudioAi(exampleAudioAi);
                            }
                            org.jsoup.nodes.Element exampleAudioElement = exampleElement.selectFirst("example-audio");
                            if(exampleAudioElement!=null){
                                HtmlContentBean.ExampleAudio exampleAudio = new HtmlContentBean.ExampleAudio();
                                org.jsoup.nodes.Element audioUkElement = exampleAudioElement.selectFirst("a.audio_uk");
                                if(audioUkElement!=null){
                                    HtmlContentBean.AudioUK audioUK = new HtmlContentBean.AudioUK();
                                    audioUK.setHref(audioUkElement.attr("href"));
                                    exampleAudio.setAudioUK(audioUK);
                                }
                                org.jsoup.nodes.Element audioUsElement = exampleAudioElement.selectFirst("a.audio_us");
                                if(audioUsElement!=null){
                                    HtmlContentBean.AudioUS audioUS = new HtmlContentBean.AudioUS();
                                    audioUS.setHref(audioUsElement.attr("href"));
                                    exampleAudio.setAudioUS(audioUS);
                                }
                                exampleGroup.setExampleAudio(exampleAudio);
                            }
                            exampleGroupList.add(exampleGroup);
                        }
                        sense.setExampleGroups(exampleGroupList);
                        org.jsoup.nodes.Element xrefsElement = senseElement.selectFirst("span.xrefs");
                        if(xrefsElement!=null){
                            HtmlContentBean.Xrefs xrefs = new HtmlContentBean.Xrefs();
                            xrefs.setXt(xrefsElement.attr("xt"));
                            xrefs.setHclass(xrefsElement.attr("hclass"));
                            xrefs.setHtag(xrefsElement.attr("htag"));
                            org.jsoup.nodes.Element prefixElement = xrefsElement.selectFirst("span.prefix");
                            if(prefixElement!=null){
                                xrefs.setPrefix(prefixElement.text());
                            }
                            org.jsoup.nodes.Element refElement = xrefsElement.selectFirst("a.Ref");
                            if(refElement!=null){
                                HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                                ref.setHref(refElement.attr("href"));
                                ref.setTitle(refElement.attr("title"));
                                org.jsoup.nodes.Element xrGElement = refElement.selectFirst("span.xr-g");
                                if(xrGElement!=null){
                                    HtmlContentBean.XrG xrG = new HtmlContentBean.XrG();
                                    xrG.setDict(xrGElement.attr("dict"));
                                    xrG.setBord(xrGElement.attr("bord"));
                                    xrG.setHref(xrGElement.attr("href"));
                                    org.jsoup.nodes.Element xhElement = xrGElement.selectFirst("span.xh");
                                    if(xhElement!=null){
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

                        org.jsoup.nodes.Element collapseElement = senseElement.selectFirst("div.collapse");
                        if(collapseElement!=null){
                            HtmlContentBean.Collapse collapse = new HtmlContentBean.Collapse();
                            collapse.setHclass(collapseElement.attr("hclass"));
                            collapse.setHtag(collapseElement.attr("htag"));
                            org.jsoup.nodes.Element unboxElement = collapseElement.selectFirst("span.unbox");
                            if(unboxElement!=null){
                                HtmlContentBean.Unbox unbox = new HtmlContentBean.Unbox();
                                unbox.setId(unboxElement.attr("id"));
                                unbox.setUnbox(unboxElement.attr("unbox"));

                                org.jsoup.nodes.Element boxTitleElement = unboxElement.selectFirst("span.box_title");
                                if(boxTitleElement!=null){
                                    HtmlContentBean.BoxTitle boxTitle = new HtmlContentBean.BoxTitle();
                                    org.jsoup.nodes.Element textTitleElement = boxTitleElement.selectFirst("div.text_title");
                                    if(textTitleElement!=null){
                                        HtmlContentBean.TextTitle textTitle = new HtmlContentBean.TextTitle();
                                        textTitle.setText(textTitleElement.text());
                                        boxTitle.setTextTitle(textTitle);
                                    }
                                    unbox.setBoxTitle(boxTitle);
                                }
                                org.jsoup.nodes.Element unboxxElement = unboxElement.selectFirst("unboxx");
                                if(unboxxElement!=null){
                                    HtmlContentBean.Unboxx unboxx = new HtmlContentBean.Unboxx();
                                    org.jsoup.nodes.Element chnSimpleElement = unboxxElement.selectFirst("chn.simple");
                                    if(chnSimpleElement!=null){
                                        HtmlContentBean.Chn chnSimple = new HtmlContentBean.Chn();
                                        chnSimple.setText(chnSimpleElement.text());
                                        unboxx.setSimple(chnSimple);
                                    }
                                    org.jsoup.nodes.Element chnTraditionalElement = unboxxElement.selectFirst("chn.traditional");
                                    if(chnTraditionalElement!=null){
                                        HtmlContentBean.Chn chnTraditional = new HtmlContentBean.Chn();
                                        chnTraditional.setText(chnTraditionalElement.text());
                                        unboxx.setTraditional(chnTraditional);
                                    }
                                    unbox.setUnboxx(unboxx);
                                }
                                org.jsoup.nodes.Element bodyElement = unboxElement.selectFirst("span.body");
                                if(bodyElement!=null){
                                    unbox.setBody(bodyElement.text());
                                    List<HtmlContentBean.Li> liList = new ArrayList<>();
                                    org.jsoup.select.Elements liElements = bodyElement.select("ul.bullet > li.li");
                                    for(org.jsoup.nodes.Element liElement : liElements){
                                        HtmlContentBean.Li li = new HtmlContentBean.Li();
                                        org.jsoup.nodes.Element refElement = liElement.selectFirst("a.Ref");
                                        if(refElement!=null){
                                            HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                                            ref.setHref(refElement.attr("href"));
                                            ref.setTitle(refElement.attr("title"));
                                            org.jsoup.nodes.Element spanElement = refElement.selectFirst("span.xref");
                                            if(spanElement!=null){
                                                li.setText(spanElement.text());
                                            }
                                            li.setRef(ref);
                                        }
                                        liList.add(li);
                                    }
                                    unbox.setLiList(liList);

                                    List<HtmlContentBean.CollocsList> collocsLists = new ArrayList<>();
                                    org.jsoup.select.Elements collocsListElements = bodyElement.select("ul.collocs_list > li.li");
                                    for(org.jsoup.nodes.Element collocsListElement : collocsListElements){
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
                        org.jsoup.nodes.Element topicGElement = senseElement.selectFirst("span.topic-g");
                        if(topicGElement!=null){
                            HtmlContentBean.TopicG topicG = new HtmlContentBean.TopicG();
                            org.jsoup.nodes.Element prefixElement = topicGElement.selectFirst("span.prefix");
                            if(prefixElement!=null){
                                topicG.setPrefix(prefixElement.text());
                            }
                            List<HtmlContentBean.Ref> refList = new ArrayList<>();
                            org.jsoup.select.Elements refElements = topicGElement.select("a.Ref");
                            for(org.jsoup.nodes.Element refElement : refElements){
                                HtmlContentBean.Ref ref = new HtmlContentBean.Ref();
                                ref.setHref(refElement.attr("href"));
                                ref.setTitle(refElement.attr("title"));
                                org.jsoup.nodes.Element topicElement = refElement.selectFirst("span.topic");
                                if(topicElement!=null){
                                    HtmlContentBean.Topic topic = new HtmlContentBean.Topic();
                                    topic.setHref(topicElement.attr("href"));
                                    org.jsoup.nodes.Element topicNameElement = topicElement.selectFirst("span.topic_name");
                                    if(topicNameElement!=null){
                                        HtmlContentBean.TopicName topicName = new HtmlContentBean.TopicName();
                                        topicName.setText(topicNameElement.text());
                                        topic.setTopicName(topicName);
                                    }
                                    org.jsoup.nodes.Element topicCefrElement = topicElement.selectFirst("span.topic_cefr");
                                    if(topicCefrElement!=null){
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
