package com.coderdream.util.nlp;

import cn.hutool.core.collection.CollectionUtil;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoderDream
 * 该类使用 Stanford CoreNLP 库进行词形还原（Lemmatization）
 */
@Slf4j
public class CoreNlpUtils {

    public static void main(String[] args) {

//        List<String> stringList = Arrays.asList("Add", "your", "text", "here", "Beijing", "sings", "Lenovo");

        List<String> stringList = Arrays.asList("English", "Indians", "Indian");

        // 获取词形还原后的结果
        Map<String, String> stringMap = CoreNlpUtils.getLemmaList(stringList);
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            System.out.println(mapKey + "：" + mapValue);
        }
    }

    /**
     * 对字符串列表进行词形还原
     *
     * @param stringList 待处理的字符串列表
     * @return 包含原始字符串和词形还原结果的 Map，Key 为原始字符串，Value 为词形还原后的字符串。如果输入列表为空，则返回 null。
     */
    public static Map<String, String> getLemmaList(List<String> stringList) {
        // 创建一个 LinkedHashMap 用于存储结果，保持原始顺序
        Map<String, String> stringMap = new LinkedHashMap<>();
        // 如果输入列表为空，直接返回 null
        if (CollectionUtil.isEmpty(stringList)) {
            return null;
        }

        // 创建 StanfordCoreNLP 对象，配置 POS 标注、词形还原、NER（命名实体识别）、解析和共指消解等功能
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // 将字符串列表合并成一个字符串，用空格分隔
        String text = stringList.stream().map(String::valueOf)
          .collect(Collectors.joining(" ")); //   "Add your text here:Beijing sings Lenovo";

        // 创建一个 Annotation 对象，包含要处理的文本
        Annotation document = new Annotation(text);

        // 对文本进行所有配置的标注处理
        pipeline.annotate(document);

        // 获取文本中的所有句子
        // CoreMap 本质上是一个 Map，使用 class 对象作为键，并具有自定义类型的值
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

//        System.out.println("word\tpos\tlemma\tner");
        // 遍历每个句子
        for (CoreMap sentence : sentences) {
            // 遍历当前句子中的每个单词
            // CoreLabel 是一个 CoreMap，具有特定于 token 的附加方法
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // 获取 token 的文本
                String word = token.get(TextAnnotation.class);
                // 获取 token 的词性标注
//                String pos = token.get(PartOfSpeechAnnotation.class);
                // 获取 token 的命名实体识别标签
//                String ne = token.get(NamedEntityTagAnnotation.class);
                // 获取 token 的词形还原结果
                String lemma = token.get(LemmaAnnotation.class);

//                System.out.println(word + "\t" + pos + "\t" + lemma + "\t" + ne);
//                log.info("word:{}, pos:{}, lemma:{}, ner:{}", word, pos, lemma, ne);
//                stringMap.put(word, lemma.toLowerCase()); // 可选择是否转换为小写
                stringMap.put(word, lemma); // 存储原始单词和词形还原结果
            }
            // 获取当前句子的解析树
//            Tree tree = sentence.get(TreeAnnotation.class);
//            log.info("tree:{}", tree);

            // 获取当前句子的 Stanford 依赖图
//            SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//            log.info("dependencies: {}", dependencies);
        }
        // 获取共指链接图
        // 每个链存储一组相互链接的提及项，以及获取最具代表性的提及项的方法
        // 句子和 token 偏移量均从 1 开始！
//        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
//        if(graph != null) {
//            for (CorefChain cc : graph.values()) {
//                log.info("CorefChain: {}", cc);
//            }
//        }

        return stringMap; // 返回包含词形还原结果的 Map
    }
}
