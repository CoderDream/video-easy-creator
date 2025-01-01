package com.coderdream.util.mdict.demo09;

import lombok.Data;

import java.util.List;

@Data
public class HtmlContentBean {

    private String linkHref;
    private String scriptSrc1;
    private String scriptSrc2;
    private BodyContent bodyContent;

    @Data
    public static class BodyContent {
        private String classAttr;
        private EntryContent entryContent;
    }

    @Data
    public static class EntryContent {
        private String classAttr;
        private String sum;
        private String hclass;
        private String id;
        private String hlength;
        private String htag;
        private String sk;
        private String idmId;
        private TopContainer topContainer;
        private List<SenseGroup> senseGroups;
    }

    @Data
    public static class TopContainer {
        private TopG topG;
    }

    @Data
    public static class TopG {
       private WebTop webTop;
    }

    @Data
    public static class WebTop{
        private Symbols symbols;
        private Headword headword;
        private String pos;
        private Phonetics phonetics;
        private String grammar;
    }

    @Data
    public static class Symbols {
        private Link ox3ksymLink;
    }
    @Data
    public static class Link {
        private String href;
        private Span ox3ksymSpan;
        private String style;
    }
    @Data
    public static class Span{
        private String classAttr;
        private String text;
    }

    @Data
    public static class Headword {
        private String id;
        private String hclass;
        private String ox3000;
        private String htag;
        private String syllable;
        private String text;
    }

    @Data
    public static class Phonetics {
        private PhonBr phonBr;
        private PhonNAm phonNAm;
    }
    @Data
    public static class PhonBr{
        private String hclass;
        private String wd;
        private String htag;
        private String geo;
        private AudioButton audioButton;
        private String phon;
    }
    @Data
    public static class PhonNAm{
        private String wd;
        private String hclass;
        private String geo;
        private String htag;
        private AudioButton audioButton;
        private String phon;
    }
    @Data
    public static class AudioButton {
        private String classAttr;
        private String href;
        private String title;
        private String style;
        private String valign;
    }
    @Data
    public static class SenseGroup {
        private String classAttr;
        private List<LiSense> liSenseList;
    }

    @Data
    public static class LiSense {
        private String liSenseBefore;
        private Sense sense;
    }

    @Data
    public static class Sense {
         private String ox3000;
         private String sensenum;
         private String htag;
         private String hclass;
         private String id;
         private String cefr;
         private SenseTop senseTop;
         private String def;
         private Deft deft;
         private List<ExampleGroup> exampleGroups;
         private Xrefs xrefs;
         private Collapse collapse;
         private TopicG topicG;
    }
    @Data
    public static class SenseTop {
        private String htag;
        private String hclass;
        private Symbols symbols;
        private Labels labels;
    }
    @Data
    public static class Labels{
        private String hclass;
        private String htag;
        private String title;
        private Labelx labelx;
    }

    @Data
    public static class Labelx{
        private Chn simple;
        private Chn traditional;
    }

    @Data
    public static class Chn{
        private String text;
        private Ai ai;
    }
    @Data
    public static class Ai{
        private String text;
    }

    @Data
    public static class Deft {
        private Chn simple;
        private Chn traditional;
    }

    @Data
    public static class ExampleGroup {
        private String classAttr;
        private String htag;
        private ExText exText;
        private ExampleAudioAi exampleAudioAi;
        private ExampleAudio exampleAudio;
    }

    @Data
    public static class ExText {
        private String x;
        private Xt xt;
    }
    @Data
    public static class Xt {
        private Chn simple;
        private Chn traditional;
    }


    @Data
    public static class ExampleAudioAi {
         private AudioUK audioUK;
         private AudioUS audioUS;
    }
    @Data
    public static class AudioUK {
        private String href;
        private String text;
    }
    @Data
    public static class AudioUS {
        private String href;
        private String text;
    }
    @Data
    public static class ExampleAudio {
        private AudioUK audioUK;
        private AudioUS audioUS;
    }


    @Data
    public static class Xrefs {
        private String xt;
        private String htag;
        private String hclass;
        private String prefix;
        private Ref ref;
    }
    @Data
    public static class Ref {
        private String href;
        private String title;
        private XrG xrG;
        private Topic topic;
    }
    @Data
    public static class XrG{
        private String dict;
        private String bord;
        private String href;
        private Xh xh;
    }
    @Data
    public static class Xh{
        private String text;
    }

    @Data
    public static class Collapse {
         private String htag;
         private String hclass;
         private Unbox unbox;
    }
    @Data
    public static class Unbox {
        private String id;
        private String unbox;
        private BoxTitle boxTitle;
        private Unboxx unboxx;
        private String body;
        private List<Li> liList;
        private String text;
        private List<CollocsList> collocsLists;
    }
    @Data
    public static class BoxTitle{
        private TextTitle textTitle;
    }
    @Data
    public static class TextTitle{
        private String text;
    }
    @Data
    public static class Unboxx{
        private Chn simple;
        private Chn traditional;
    }
    @Data
    public static class Li {
        private Ref ref;
        private String text;
        private String classAttr;

    }
    @Data
    public static class CollocsList{
        private String classAttr;
        private String text;
    }

    @Data
    public static class TopicG {
        private String prefix;
        private List<Ref> refList;
    }
    @Data
    public static class Topic {
         private String href;
         private TopicName topicName;
         private TopicCefr topicCefr;
    }
    @Data
    public static class TopicName {
        private String text;
    }
    @Data
    public static class TopicCefr{
        private String text;
    }
}
