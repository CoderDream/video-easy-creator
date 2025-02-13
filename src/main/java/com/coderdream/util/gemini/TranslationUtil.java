package com.coderdream.util.gemini;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdTextUtil;
import com.coderdream.util.process.ListSplitterStream;
import com.coderdream.util.sentence.demo1.SentenceParser;
import com.coderdream.vo.SentenceVO;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TranslationUtil {

    public static String translate(String text) {
        return GeminiApiClient.generateContent(text);
    }


    /**
     * 处理词汇信息，将其翻译后写入文件。
     *
     * @param vocInfoList 包含词汇信息的列表
     * @param fileName    文件名，用于存储翻译结果
     */
    public static void processVoc(List<VocInfo> vocInfoList, String fileName) {
        StringBuilder text = new StringBuilder(
                CdConstants.VOC_CN_PREFIX);  // 使用 StringBuilder 拼接字符串，避免多次创建字符串对象

        // 遍历 vocInfoList，拼接文本
        for (VocInfo vocInfo : vocInfoList) {
            text.append(vocInfo.getWord()).append("\n ");
            text.append(vocInfo.getWordExplainEn()).append("\n");
        }

        // 调用翻译方法并记录日志
        log.info("开始翻译文本内容，包含 {} 个词汇", vocInfoList.size());

        String result = GeminiApiClient.generateContent(text.toString());  // 翻译文本

        // 记录翻译后的结果日志
        log.info("翻译完成，开始写入文件: {}", fileName);

        // 将翻译结果写入文件
        boolean writeSuccess = writeToFile(fileName, result);

        if (writeSuccess) {
            log.info("文件写入成功: {}", fileName);
        } else {
            log.error("文件写入失败: {}", fileName);
        }
    }

    /**
     * 将翻译结果写入文件
     */
    public static boolean writeToFile(String newFileName, String result) {
        // 使用正则表达式直接处理换行符
        String temp = result.replaceAll("\n{2,}", "\n");  // 将连续的两个或更多的换行符替换为一个换行符
        temp = temp.replace("\n", "__");  // 替换单个换行符为 "__"

        // 使用 Arrays.asList 来避免不必要的 List.of
        List<String> list = Arrays.asList(temp.split("__"));

        // 写入文件
        boolean writeToFileResult = CdFileUtil.writeToFile(newFileName, list);
        log.info("写入文件结果: {}", writeToFileResult);
        return writeToFileResult;
    }

    // 增加英文音标 phonetics
    public static File genPhonetics(String fileName, String jsonFileName) {
        return writePhoneticsToFile(fileName, jsonFileName);
    }

    public static File genAiFile(String fileName) {
        List<String> sentences = CdTextUtil.getAllEnglishSentencesFromFile(
                fileName);
        String aiFileName = CdFileUtil.addPostfixToFileName(fileName, "_ai");
        if (CollectionUtil.isEmpty(sentences)) {
            log.error("sentences is empty");
            return null;
        }
        int groupSize = 100;
        // 拆分句子列表，每组包含 groupSize 个元素
        List<List<String>> sentencesLists = ListSplitterStream.splitList(
                sentences, groupSize);
        int i = 0;
        String englishFileNamePart;
        String jsonFileNamePart;
        String translate = "";

        List<String> totalTranslateList = new ArrayList<>();
        for (List<String> sentencesList : sentencesLists) {
            i++;
            englishFileNamePart = CdFileUtil.addPostfixToFileName(
                    fileName, "_en" + "_" + i);
            jsonFileNamePart = CdFileUtil.addPostfixToFileName(
                    fileName, "_ai" + "_" + i);
            assert jsonFileNamePart != null;
            File jsonFilePart = new File(jsonFileNamePart);
            // 如果已经存在的ai文件为空或者行数和句子的大小不一致，则需要重新调用API获取音标
            if (!jsonFilePart.exists() || jsonFilePart.length() == 0
                    || FileUtil.readLines(jsonFilePart, StandardCharsets.UTF_8).size() != sentencesList.size()) {
                String text = CdConstants.GEN_PHONETICS_TEXT;
                String content = String.join("\n", sentencesList);
                text += content;
                List<String> translateList = getStringsFromGemini(text, sentencesList.size());

                if (CollectionUtil.isNotEmpty(translateList)
                        && translateList.size() == sentencesList.size()) {
                    FileUtil.writeLines(translateList, jsonFilePart,
                            StandardCharsets.UTF_8);
                    FileUtil.writeLines(sentencesList, englishFileNamePart,
                            StandardCharsets.UTF_8);
                    totalTranslateList.addAll(translateList);
//          translateTotal.append(translate);
                } else {
//          FileUtil.writeLines(translateList, jsonFilePart,
//            StandardCharsets.UTF_8);
//          FileUtil.writeLines(sentencesList, englishFileNamePart,
//            StandardCharsets.UTF_8);

//          totalTranslateList.addAll(translateList);


                    if (CollectionUtil.isNotEmpty(translateList)
                            && translateList.size() == sentencesList.size()) {
                        FileUtil.writeLines(translateList, jsonFilePart,
                                StandardCharsets.UTF_8);
                        FileUtil.writeLines(sentencesList, englishFileNamePart,
                                StandardCharsets.UTF_8);
                        totalTranslateList.addAll(translateList);
//          translateTotal.append(translate);
                    } else {
//          FileUtil.writeLines(translateList, jsonFilePart,
//            StandardCharsets.UTF_8);
//          FileUtil.writeLines(sentencesList, englishFileNamePart,
//            StandardCharsets.UTF_8);

//          totalTranslateList.addAll(translateList);
                        log.error(
                                "translateList size is not equal to sentencesList size,"
                                        + " translateList.size {},"
                                        + " sentencesList.size {}, "
                                        + " jsonFileNamePart: {}",
                                translateList.size(), sentencesList.size(), jsonFileNamePart);
                        break;
                    }
                }
            } else {
                List<String> translateList = FileUtil.readLines(jsonFilePart,
                        StandardCharsets.UTF_8);
                totalTranslateList.addAll(translateList);
            }
//      log.info("genPhonetics Total: {}", translate);
        }

        // 把字符串中的空格+斜线替换为回车换行加斜线
//    String translateTotalString = RemoveEmptyLines.removeEmptyLines(
//      translateTotal.toString());
//
//    FileUtil.writeUtf8String(translateTotalString, aiFileName);
        if (totalTranslateList.size() != sentences.size()) {
            log.error("totalTranslateList size is not equal to sentences size,"
                            + " totalTranslateList.size {},"
                            + " sentences.size {}",
                    totalTranslateList.size(), sentences.size());
        } else {
            FileUtil.writeLines(totalTranslateList, aiFileName, StandardCharsets.UTF_8);
        }

//    log.info("genPhonetics: {}", translate);
        return new File(aiFileName);
    }

    // 最大重试次数
    private static final int MAX_RETRY_ATTEMPTS = 10;

    /**
     * 从 Gemini 获取字符串列表。
     *
     * @param text 输入文本
     * @param size 期望的列表大小
     * @return 翻译后的字符串列表
     */
    private static @NotNull List<String> getStringsFromGemini(String text, int size) {
        return getStringsFromGeminiWithRetry(text, size, MAX_RETRY_ATTEMPTS);
    }

    /**
     * 从 Gemini 获取字符串列表（带重试机制）。
     *
     * @param text              输入文本
     * @param size              期望的列表大小
     * @param remainingAttempts 剩余重试次数
     * @return 翻译后的字符串列表。如果多次重试后仍无法获得期望大小的列表，则返回空列表。
     */
    /**
     * 从 Gemini 获取字符串列表（带重试机制）。
     *
     * @param text              输入文本
     * @param size              期望的列表大小
     * @param remainingAttempts 剩余重试次数
     * @return 翻译后的字符串列表。如果多次重试后仍无法获得期望大小的列表，则返回空列表。
     */
    private static @NotNull List<String> getStringsFromGeminiWithRetry(String text, int size, int remainingAttempts) {
        // 如果重试次数用尽，返回空列表
        if (remainingAttempts <= 0) {
            // 可以选择抛出异常，或者返回空列表，具体取决于需求
            log.error("在多次尝试后，未能从 Gemini 获取到期望大小的翻译列表。"); // 使用 log.error
            return Collections.emptyList(); // 或者其他合适的默认值
        }

        // 调用 Gemini API 生成内容
        String translate = GeminiApiClient.generateContent(text);
        // 解析句子
        List<SentenceVO> sentenceVOList = SentenceParser.parseSentences(translate);
        // 提取音标
        List<String> translateList = new ArrayList<>();
        for (SentenceVO sentenceVO : sentenceVOList) {
            translateList.add(sentenceVO.getPhonetics());
        }

        // 如果列表大小不符合预期，则进行重试
        if (translateList.size() != size) {
            // 使用 log.error 记录错误信息，而不是 System.err.println
            log.error("翻译结果大小不匹配。期望: {}, 实际: {}, 正在重试... (剩余 {} 次尝试)",
                    size, translateList.size(), remainingAttempts - 1);
            // 递归调用，减少剩余重试次数
            return getStringsFromGeminiWithRetry(text, size, remainingAttempts - 1);
        }

        // 返回翻译结果
        return translateList;
    }

    private static File writePhoneticsToFile(String totalFileName,
                                             String jsonFileName) {
        String addPostfixToFileName = CdFileUtil.addPostfixToFileName(totalFileName,
                "_phonetics");
        List<String> lines = FileUtil.readLines(new File(jsonFileName),
                StandardCharsets.UTF_8);
        if (CollectionUtil.isEmpty(lines)) {
            log.error("lines is empty");
            return null;
        }

//    List<SentenceVO> sentenceVOPhList = CdTextUtil.parseSentencesFromFileWithEnglishAndPhonetics(
//      jsonFileName);
        List<SentenceVO> sentenceVOPhList = CdTextUtil.parseSentencesFromFileWithPhonetics(
                jsonFileName);
        List<SentenceVO> sentenceVOCnList = CdTextUtil.parseSentencesFromFile(
                totalFileName);
        if (CollectionUtil.isEmpty(sentenceVOPhList) || CollectionUtil.isEmpty(
                sentenceVOPhList) || (sentenceVOPhList.size() != sentenceVOCnList.size()
                && sentenceVOPhList.size() * 2 != sentenceVOCnList.size())) {
            log.error("音标列表和中文列表不一致, 音标列表大小： {}，中文列表大小： {},",
                    sentenceVOPhList.size(),
                    sentenceVOCnList.size());
            return null;
        }
        File file = new File(addPostfixToFileName);
        String filePath = file.getParent();
        log.info("对话信息将写入到文件: {}", file.getName());
        Path outputFilePath = Paths.get(filePath,
                file.getName());
        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
                StandardCharsets.UTF_8)) {
            SentenceVO sentenceVO = null;
            SentenceVO sentenceVOCn = null;
            for (int i = 0; i < sentenceVOPhList.size(); i++) {
                sentenceVO = sentenceVOPhList.get(i);
                sentenceVOCn = sentenceVOCnList.get(i);
                writer.write(sentenceVOCn.getEnglish());
                writer.newLine();
                writer.write(sentenceVO.getPhonetics());
                writer.newLine();
                writer.write(ZhConverterUtil.toTraditional(sentenceVOCn.getChinese()));
                writer.newLine();
            }
            log.info("对话信息已成功写入到文件: {}", outputFilePath);
        } catch (IOException e) {
            log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
        }
        return file;
    }

    /**
     * 生成文章描述
     *
     * @param folderName 文件名
     */
    public static void genDescription(String folderName) {

        String folderPath = CommonUtil.getFullPath(folderName);
        String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
        String text = "解析下面的文本，帮我写文章，用来发快手、小红书和公众号，要根据不同的平台特性生成不同风格的文章，快手的文章字数在500~600之间，小红书不超过800字，公众号不超过200字；另外，帮我每个平台取3个疑问句的标题，标题中间不要有任何标点符号、表情符号且不超过20个字，快手加入一些表情符号。文本如下：";
        List<String> vocInfoList = CdFileUtil.readFileContent(fileName);
        String content = String.join("\n", vocInfoList);
        text += content;
        String translate = GeminiApiClient.generateContent(text);
        log.info("translate: {}", translate);
        File file = new File(fileName);
        String filePath = file.getParent();
        Path outputFilePath = Paths.get(filePath,
                folderName + "_description.md");

        if (FileUtil.exist(fileName)) {
            log.info("文件已存在：{}", fileName);
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath,
                StandardCharsets.UTF_8)) {
            for (String line : translate.split("\n")) {
                if (StrUtil.isNotEmpty(line)) {
                    // 以斜线\ 分割字符串，然后逐个写入文件 斜线 \\\\ 反斜线  /
//          String[] split = line.split("/");
//          if (CollectionUtil.isNotEmpty(Arrays.asList(split))
//            && split.length == 3) {
//            writer.write(split[0]);
//            writer.newLine();
//            writer.write("/" + split[1] + "/");
//            writer.newLine();
                    writer.write(line);
                    writer.newLine();
//          }
                }
            }
            log.info("对话信息已成功写入到文件: {}", outputFilePath);
        } catch (IOException e) {
            log.error("写入文件 {} 发生异常：{}", outputFilePath, e.getMessage(), e);
        }

    }

}
