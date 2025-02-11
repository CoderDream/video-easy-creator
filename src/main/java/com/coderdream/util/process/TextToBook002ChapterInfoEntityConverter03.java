//package com.coderdream.util.process;
//
//import com.coderdream.entity.Book002ChapterInfoEntity;
//import com.coderdream.entity.Book002ContentEntity;
//import com.coderdream.entity.Book002DialogPairEntity;
//import com.coderdream.entity.Book002SceneEntity;
//import com.coderdream.entity.SentencePair;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 用于解析特定格式的文本文件并生成 Book002ChapterInfoEntity 对象的工具类。
// */
//@Slf4j
//public class TextToBook002ChapterInfoEntityConverter03 {
//    // 新增：章节标题的正则表达式
//    private static final Pattern CHAPTER_PATTERN = Pattern.compile("Chapter \\d+　.+");
//    private static final Pattern SCENE_PATTERN = Pattern.compile("Scene \\d+　.+");
//    private static final Pattern NUMBERED_SENTENCE_PATTERN = Pattern.compile("^[❶❷❸❹❺❻❼❽❾❿] (.+)$");
//   // private static final Pattern SIMILAR_EXPRESSION_PATTERN = Pattern.compile("^同类表达 (.+)$");
//    // 匹配 "同类表达", 后面可以没有内容
//    private static final Pattern SIMILAR_EXPRESSION_PATTERN = Pattern.compile("^同类表达\\s*(.*)$");
//    private static final Pattern DIALOG_A_PATTERN = Pattern.compile("^对话 A: (.+)$");
//    private static final Pattern DIALOG_B_PATTERN = Pattern.compile("^B: (.+)$");
//    private static final Pattern  ANSWER_PATTERN = Pattern.compile("这样回答\\s*(.+)");
//
//    //句子至少包含一个英文字符
//    private static final Pattern SENTENCE_PATTERN = Pattern.compile(".*[a-zA-Z].*");
//
//    // 匹配英文和中文
//    private static final Pattern EN_CN_PATTERN = Pattern.compile("(.+?)([。？！])\\s*(.*)");
//
//    /**
//     * 将文本文件内容解析为 Book002ChapterInfoEntity 对象。
//     *
//     * @param filePath 文本文件路径
//     * @return Book002ChapterInfoEntity 对象，如果解析失败则返回 null
//     */
//    public static Book002ChapterInfoEntity parseTextFile(String filePath) {
//        LocalDateTime startTime = LocalDateTime.now();
//        log.info("开始解析文件: {}", filePath);
//
//        Book002ChapterInfoEntity bookInfo = new Book002ChapterInfoEntity();
//        List<Book002SceneEntity> sceneList = new ArrayList<>();
//        bookInfo.setSceneEntityList(sceneList);
//
//        Book002SceneEntity currentScene = null;
//        Book002ContentEntity currentContent = null;
//      //  SentencePair lastSentencePair = null; // 上一个句子对（非对话）
//
//        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//            String line;
//
//            // --- 优先解析章节标题 ---
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                Matcher chapterMatcher = CHAPTER_PATTERN.matcher(line);
//                if (chapterMatcher.matches()) {
//                    bookInfo.setChapterStr(line);
//                    log.info("解析到章节标题: {}", line);
//                    break; // 找到章节标题后，跳出循环
//                }
//            }
//            // --- 章节标题解析结束 ---
//
//            // 继续处理剩余的文本内容
//            while ((line = reader.readLine()) != null) {
//                line = line.trim(); // 去除首尾空白
//
//                if (line.isEmpty() || line.equals(bookInfo.getChapterStr())) {
//                    continue; // 跳过空行 和 章节标题
//                }
//
//                // 匹配 Scene 标题
//                Matcher sceneMatcher = SCENE_PATTERN.matcher(line);
//                if (sceneMatcher.matches()) {
//                    currentScene = new Book002SceneEntity();
//                    currentScene.setSceneTitle(line);
//                    currentScene.setContentEntityList(new ArrayList<>());
//                    sceneList.add(currentScene);
//                    continue;
//                }
//
//                // 确保当前在一个 Scene 中
//                if (currentScene == null) {
//                    log.warn("未找到 Scene 标题，跳过行: {}", line);
//                    continue;
//                }
//
//                // 匹配 "同类表达"
//                Matcher similarExpressionMatcher = SIMILAR_EXPRESSION_PATTERN.matcher(line);
//                if (similarExpressionMatcher.matches()) {
//                    String similarExpressions = similarExpressionMatcher.group(1).trim(); // 获取"同类表达"后面的内容
//                    if (currentContent != null) {
//                        // 确保 sentencePairList 不为空
//                        if (currentContent.getSameSentencePairList() == null) {
//                            currentContent.setSameSentencePairList(new ArrayList<>());
//                        }
//
//                        // 如果 "同类表达" 后面有内容，则解析为句子对
//                        if (!similarExpressions.isEmpty()) {
//                            currentContent.getSameSentencePairList().add(parseSentencePair(similarExpressions));
//                        } else {
//                            // 只有“同类表达”四个字, 使用上一句的中文
//                            if (currentContent.getSentencePair() != null) {
//                                SentencePair lastSentencePair = currentContent.getSentencePair();
//                                SentencePair pair = new SentencePair();
//                                pair.setChineseSentence(lastSentencePair.getChineseSentence());
//                                currentContent.getSameSentencePairList().add(pair);
//                            }
//                        }
//                    } else {
//                        log.warn("在 '同类表达' 之前未找到有效的句子。行: {}", line);
//                    }
//                    continue;
//                }
//
//
//                // 匹配带序号的句子 (❶, ❷, ...)  // 移到 "同类表达" 之后
//                Matcher numberedSentenceMatcher = NUMBERED_SENTENCE_PATTERN.matcher(line);
//                if (numberedSentenceMatcher.find()) {
//                    String content = numberedSentenceMatcher.group(1);
//                    currentContent = new Book002ContentEntity();
//                    currentContent.setSceneIndex(line.substring(0, line.indexOf(" "))); //提取序号
//                    currentContent.setSentencePair(parseSentencePair(content));
//                    currentScene.getContentEntityList().add(currentContent);
//                    continue;
//                }
//
//                // 匹配 "这样回答"
//                Matcher answerMatcher = ANSWER_PATTERN.matcher(line);
//                if(answerMatcher.find()) {
//                    String content = answerMatcher.group(1);
//                    currentContent = new Book002ContentEntity();
//                    currentContent.setSceneIndex(""); //提取序号
//                    currentContent.setSentencePair(parseSentencePair(content)); // 拆分句子
//                  //  lastSentencePair = currentContent.getSentencePair(); // 保存到lastSentencePair
//                    currentScene.getContentEntityList().add(currentContent);
//                    continue;
//                }
//
//                // 匹配对话 A:
//                Matcher dialogAMatcher = DIALOG_A_PATTERN.matcher(line);
//                if (dialogAMatcher.find()) {
//                    //中英文句子
//                    String content = dialogAMatcher.group(1);
//                    if(currentContent != null){
//                        Book002DialogPairEntity dialogPairEntity = new Book002DialogPairEntity();
//                        SentencePair sentencePair = parseSentencePair(content);
//                        dialogPairEntity.setContentAEn(sentencePair.getEnglishSentence());
//                        dialogPairEntity.setContentACn(sentencePair.getChineseSentence());
//                        // 确保 dialogPairEntityList 不为空
//                        if (currentContent.getDialogPairEntityList() == null) {
//                            currentContent.setDialogPairEntityList(new ArrayList<>());
//                        }
//                        currentContent.getDialogPairEntityList().add(dialogPairEntity);
//                    }
//
//                    continue;
//                }
//
//                // 匹配对话 B:
//                Matcher dialogBMatcher = DIALOG_B_PATTERN.matcher(line);
//                if (dialogBMatcher.find()) {
//                    String content = dialogBMatcher.group(1);
//                    // 确保 currentContent 和 dialogPairEntityList 不为空
//                    if(currentContent != null && currentContent.getDialogPairEntityList() != null && !currentContent.getDialogPairEntityList().isEmpty()){
//                        Book002DialogPairEntity dialogPairEntity = currentContent.getDialogPairEntityList().get(currentContent.getDialogPairEntityList().size() - 1);
//                        SentencePair sentencePair = parseSentencePair(content);
//                        dialogPairEntity.setContentBEn(sentencePair.getEnglishSentence());
//                        dialogPairEntity.setContentBCn(sentencePair.getChineseSentence());
//                    }
//                    continue;
//                }
//                 // 如果所有模式都不匹配，也尝试解析为句子（保底策略）
//                SentencePair sentencePair = parseSentencePair(line);
//                if(sentencePair.getEnglishSentence() != null && !sentencePair.getEnglishSentence().isEmpty()) {
//                     currentContent = new Book002ContentEntity();
//                    currentContent.setSceneIndex(""); // 没有序号
//                    currentContent.setSentencePair(sentencePair);
//                    currentScene.getContentEntityList().add(currentContent);
//                } else
//                {
//                    log.warn("无法解析的行: {}", line);
//                }
//            }
//        } catch (IOException e) {
//            log.error("读取文件时发生错误: {}", filePath, e);
//            return null;
//        }
//        //计算总共的句子数
////        int totalCount = 0;
////        for (Book002SceneEntity scene: sceneList ) {
////            totalCount += scene.getContentEntityList().size();
////        }
////        bookInfo.setSectionCount(totalCount);
////        log.info("解析完成，共解析句子数量：{}，耗时：{}", totalCount, formatElapsedTime(
////            Duration.between(startTime, LocalDateTime.now())));
//        return bookInfo;
//    }
//
//
//    /**
//     * 将一行文本拆分为中英文句子对。 假设中文在后，英文在前.
//     */
//    private static SentencePair parseSentencePair(String content) {
//        // 使用正则表达式匹配中英文
//        Matcher m = EN_CN_PATTERN.matcher(content);
//        String englishSentence = "";
//        String chineseSentence = "";
//        if (m.find()) {
//            // group(1) 是捕获到的英文部分, group(2) 是标点符号, group(3) 是中文部分
//            englishSentence = m.group(1).trim(); // 英文部分
//            chineseSentence = m.group(3).trim(); // 中文部分
//
//             // 如果中文部分为空，而英文部分不为空，则可能是格式问题，尝试修复
//            if (chineseSentence.isEmpty() && !englishSentence.isEmpty()) {
//                log.warn("发现可能的格式问题，尝试修复: {}", content);
//                // 尝试从下一行获取中文（这里需要reader, 所以这个逻辑最好放在主循环里）
//                // 这里只是一个示例，你需要根据实际情况调整
//            }
//
//        } else {
//            log.warn("未能正确解析中英文句子对: {}", content);
//            //  return new SentencePair("", ""); // 或者可以返回空字符串, 根据需求决定
//        }
//        return new SentencePair(englishSentence, chineseSentence);
//    }
//
//    private static String formatElapsedTime(Duration duration) {
//        long hours = duration.toHours();
//        long minutes = duration.toMinutesPart();
//        long seconds = duration.toSecondsPart();
//        long millis = duration.toMillisPart();
//        return String.format("%02dh %02dm %02ds %03dms", hours, minutes, seconds, millis);
//    }
//
//    public static void main(String[] args) {
//        String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt"; // 替换为你的文件路径
//        Book002ChapterInfoEntity entity = parseTextFile(filePath);
//
//        // 打印解析结果 (示例)
//        if (entity != null) {
//            System.out.println(entity);
//        }
//    }
//}
