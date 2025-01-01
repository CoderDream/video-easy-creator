//package com.coderdream.util.mdict.demo09;
//
//import com.coderdream.entity.MultiLanguageContent;
//import com.coderdream.entity.WordDetail;
//import com.coderdream.entity.WordPronunciation;
////import io.github.eb4j.mdict.Dictionary;
//import io.github.eb4j.mdict.MDException;
////import io.github.eb4j.mdict.LookupResult;
//import io.github.eb4j.mdict.MDictDictionary;
//import lombok.extern.slf4j.Slf4j;
//import org.codehaus.groovy.control.ClassNodeResolver.LookupResult;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
///**
// * 使用 Jsoup 解析 HTML 和 io.github.eb4j.mdict.MDictDictionary 查询单词的工具类
// */
//@Slf4j
//public class JsoupHtmlParser {
//
//    private static final String DICTIONARY_PATH = "D:\\java_output\\dict\\oaldpe.mdx";
//
//    /**
//     * 主方法，用于测试单词查询
//     * @param args 命令行参数
//     * @throws IOException IO异常
//     */
//    public static void main(String[] args) throws IOException {
//        String word = "chemistry"; // 要查询的单词
//        WordDetail wordDetail = lookupWord(word);
//        log.info("查询单词 {} 的结果：{}", word, wordDetail);
//    }
//
//    /**
//     * 查询单词并返回 WordDetail 对象
//     * @param word 要查询的单词
//     * @return WordDetail 对象
//     * @throws IOException IO异常
//     */
//    public static WordDetail lookupWord(String word) throws IOException {
//        Instant start = Instant.now();
//        log.info("开始查询单词: {}", word);
//        String htmlContent = getHtmlContentFromMDict(word, DICTIONARY_PATH);
//        if (htmlContent == null || htmlContent.isEmpty()) {
//            log.warn("单词 {} 在词典中未找到。", word);
//            return null;
//        }
//        WordDetail wordDetail = parseHtmlToWordDetail(htmlContent);
//        Instant finish = Instant.now();
//        long timeElapsed = Duration.between(start, finish).toMillis();
//        log.info("查询单词 {} 耗时：{}毫秒", word, timeElapsed);
//        return wordDetail;
//    }
//
//    /**
//     * 解析 HTML 内容并返回 WordDetail 对象
//     * @param htmlContent HTML 内容
//     * @return WordDetail 对象
//     * @throws IOException IO异常
//     */
//    public static WordDetail parseHtmlToWordDetail(String htmlContent) throws IOException {
//        Instant start = Instant.now();
//        log.info("开始解析 HTML 内容");
//        Document doc = Jsoup.parse(htmlContent);
//
//        HtmlContentBean htmlContentBean = parseHtmlDocument(doc);
//        if (htmlContentBean == null || htmlContentBean.getBodyContent() == null
//                || htmlContentBean.getBodyContent().getEntryContent() == null) {
//            log.warn("HTML 解析后数据为空。");
//            return null;
//        }
//        HtmlContentBean.EntryContent entryContent = htmlContentBean.getBodyContent().getEntryContent();
//        WordDetail wordDetail = new WordDetail();
//
//        // 单词
//        if (entryContent.getTopContainer() != null
//                && entryContent.getTopContainer().getTopG() != null
//                && entryContent.getTopContainer().getTopG().getWebTop() != null
//                && entryContent.getTopContainer().getTopG().getWebTop().getHeadword() != null) {
//            wordDetail.setWord(entryContent.getTopContainer().getTopG().getWebTop().getHeadword().getText());
//        }
//
//        // 词性
//        List<String> partOfSpeechList = new ArrayList<>();
//        if (entryContent.getTopContainer() != null
//                && entryContent.getTopContainer().getTopG() != null
//                && entryContent.getTopContainer().getTopG().getWebTop() != null
//                && entryContent.getTopContainer().getTopG().getWebTop().getPos() != null) {
//            partOfSpeechList.add(entryContent.getTopContainer().getTopG().getWebTop().getPos());
//            wordDetail.setPartOfSpeechList(partOfSpeechList);
//        }
//
//
//        // 音标
//        WordPronunciation wordPronunciation = new WordPronunciation();
//        if (entryContent.getTopContainer() != null
//                && entryContent.getTopContainer().getTopG() != null
//                && entryContent.getTopContainer().getTopG().getWebTop() != null
//                && entryContent.getTopContainer().getTopG().getWebTop().getPhonetics() != null) {
//            HtmlContentBean.Phonetics phonetics = entryContent.getTopContainer().getTopG().getWebTop().getPhonetics();
//            if (phonetics.getPhonBr() != null) {
//                wordPronunciation.setBritishPronunciation(phonetics.getPhonBr().getPhon());
//            }
//            if (phonetics.getPhonNAm() != null) {
//                wordPronunciation.setAmericanPronunciation(phonetics.getPhonNAm().getPhon());
//            }
//            wordDetail.setWordPronunciation(wordPronunciation);
//        }
//
//        // 解释和句子
//        List<MultiLanguageContent> definitionList = new ArrayList<>();
//        List<MultiLanguageContent> sentenceList = new ArrayList<>();
//
//        if (entryContent.getSenseGroups() != null) {
//            for (HtmlContentBean.SenseGroup senseGroup : entryContent.getSenseGroups()) {
//                if (senseGroup.getLiSenseList() != null) {
//                    for (HtmlContentBean.LiSense liSense : senseGroup.getLiSenseList()) {
//                        HtmlContentBean.Sense sense = liSense.getSense();
//                        if (sense != null) {
//                            if (sense.getDef() != null || (sense.getDeft() != null
//                                    && (sense.getDeft().getSimple() != null || sense.getDeft().getTraditional() != null))) {
//                                MultiLanguageContent definition = new MultiLanguageContent();
//                                if (sense.getDef() != null) {
//                                    definition.setContentEnglish(sense.getDef());
//                                }
//                                if (sense.getDeft() != null) {
//                                    if (sense.getDeft().getSimple() != null) {
//                                        definition.setContentSimple(sense.getDeft().getSimple().getText());
//                                    }
//                                    if (sense.getDeft().getTraditional() != null) {
//                                        definition.setContentTraditional(sense.getDeft().getTraditional().getText());
//                                    }
//                                }
//
//                                definitionList.add(definition);
//                            }
//                            if (sense.getExampleGroups() != null) {
//                                for (HtmlContentBean.ExampleGroup exampleGroup : sense.getExampleGroups()) {
//                                    MultiLanguageContent sentence = new MultiLanguageContent();
//                                    if (exampleGroup.getExText() != null) {
//                                        if (exampleGroup.getExText().getX() != null) {
//                                            sentence.setContentEnglish(exampleGroup.getExText().getX());
//                                        }
//                                        if (exampleGroup.getExText().getXt() != null) {
//                                            if (exampleGroup.getExText().getXt().getSimple() != null) {
//                                                sentence.setContentSimple(exampleGroup.getExText().getXt().getSimple().getText());
//                                            }
//                                            if (exampleGroup.getExText().getXt().getTraditional() != null) {
//                                                sentence.setContentTraditional(exampleGroup.getExText().getXt().getTraditional().getText());
//                                            }
//                                        }
//                                    }
//                                    if (Objects.nonNull(sentence.getContentEnglish()) ||
//                                            Objects.nonNull(sentence.getContentSimple()) ||
//                                            Objects.nonNull(sentence.getContentTraditional())) {
//                                        sentenceList.add(sentence);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            wordDetail.setDefinitionList(definitionList);
//            wordDetail.setSentenceList(sentenceList);
//        }
//        Instant finish = Instant.now();
//        long timeElapsed = Duration.between(start, finish).toMillis();
//        log.info("解析 HTML 内容 耗时：{}毫秒",  timeElapsed);
//        return wordDetail;
//    }
//
//
//    /**
//     * 从 MDict 词典中获取 HTML 内容
//     * @param word      要查询的单词
//     * @param mdxPath MDict 词典文件路径
//     * @return HTML 内容
//     */
//    private static String getHtmlContentFromMDict(String word, String mdxPath) {
//        Instant start = Instant.now();
//        log.info("开始从 MDict 词典查询单词: {}", word);
//        try (Dictionary dictionary = new MDictDictionary(Paths.get(mdxPath))) {
//            LookupResult lookupResult = dictionary.lookup(word);
//            if (lookupResult != null && lookupResult.getHtml() != null) {
//                Instant finish = Instant.now();
//                long timeElapsed = Duration.between(start, finish).toMillis();
//                log.info("从 MDict 词典查询单词 {} 耗时：{}毫秒", word, timeElapsed);
//                return lookupResult.getHtml();
//            }
//            log.warn("单词 {} 在 MDict 词典中未找到。", word);
//            return null;
//        } catch (IOException e) {
//            log.error("从 MDict 词典查询单词 {} 发生异常：", word, e);
//            return null; // 如果没有找到结果或发生错误，返回 null
//        }
//    }
//
//     /**
//      * 解析 HTML Document 对象并返回 HtmlContentBean 对象
//      * @param doc HTML Document 对象
//      * @return HtmlContentBean 对象
//      */
//    public static HtmlContentBean parseHtmlDocument(Document doc);
//
//}
