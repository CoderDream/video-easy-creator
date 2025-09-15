package com.coderdream.util.process.bbc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.DictUtil;
import com.coderdream.util.bbc.GenSrtUtil;
import com.coderdream.util.bbc.ProcessScriptUtil;
import com.coderdream.util.process.PreparePublishUtil;
import com.coderdream.util.translate.TranslateUtil;
import com.coderdream.util.bbc.WordCountUtil;
import com.coderdream.util.cd.CdMP3SplitterUtil;
import com.coderdream.util.cd.TextProcessor;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.ppt.GetSixMinutesPpt;
import com.coderdream.util.ppt.PptToImageConverter;
import com.coderdream.util.subtitle.SubtitleUtil;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 6 Minute English 全流程端到端集成测试
 * <p>
 * 测试从原始脚本和音频文件开始，逐步生成对话脚本、词汇表、翻译、字幕、PPT、描述等所有产物的完整流程。
 *
 * @author Gemini Code Assist
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class SixMinutesProcessTest {

    // 配置要测试的文件夹名称
    private static final String FOLDER_NAME = "240104"; // 您可以在此处更改要测试的文件夹

    // 用于在测试方法间传递关键文件名
    private static String folderPath;
    private static String mp3FileName;
    private static String mp3FileNameFull;
    private static String srtEngRawFileName;
    private static String scriptDialogNew2FileName;
    private static String mp3FileNameFullNew;
    private static String srtFileName;
    private static String chapterName;

    @BeforeAll
    static void setUp() {
        folderPath = CommonUtil.getFullPath(FOLDER_NAME);
        log.info("========【测试初始化】========");
        log.info("测试文件夹路径: {}", folderPath);
        // 在所有测试开始前，先找到MP3文件名
        List<String> fileNames = FileUtil.listFileNames(folderPath);
        for (String subFileName : fileNames) {
            if (subFileName.startsWith(FOLDER_NAME) && subFileName.endsWith(CdConstants.MP3_EXTENSION)) {
                mp3FileName = subFileName.substring(0, subFileName.length() - 4);
                break;
            }
        }
        assertNotNull(mp3FileName, "初始化失败：在文件夹 " + FOLDER_NAME + " 中未找到对应的MP3文件");
        mp3FileNameFull = CommonUtil.getFullPathFileName(FOLDER_NAME, mp3FileName, CdConstants.MP3_EXTENSION);
        log.info("找到音频文件: {}", mp3FileNameFull);
    }

    @Test
    @Order(1)
    @DisplayName("1. 预处理：生成对话脚本和词汇表")
    void test01_GenerateInitialScripts() {
        log.info("\n========【1. 开始测试：生成对话脚本和词汇表】========");

        // Step00: 处理 YYMMDD_script.txt
        String srcScriptFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME + "_script", ".txt");
        ProcessScriptUtil.processScriptTxt(srcScriptFileName);

        // Step01: 生成 script_dialog.txt
        String scriptDialogFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog", ".txt");
        ProcessScriptUtil.genScriptDialogTxt(FOLDER_NAME, scriptDialogFileName);
        assertFalse(CdFileUtil.isFileEmpty(scriptDialogFileName), "script_dialog.txt 生成失败或为空");
        log.info("成功生成 script_dialog.txt");

        // Step02: 生成 voc.txt
        String vocFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "voc", ".txt");
        ProcessScriptUtil.genVocTxt(FOLDER_NAME, vocFileName);
        assertFalse(CdFileUtil.isFileEmpty(vocFileName), "voc.txt 生成失败或为空");
        log.info("成功生成 voc.txt");
    }

    @Test
    @Order(2)
    @DisplayName("2. 翻译：生成中文脚本和词汇表")
    void test02_GenerateTranslations() {
        log.info("\n========【2. 开始测试：生成中文脚本和词汇表】========");

        // Step03: 生成 script_dialog_cn.txt
        String scriptDialogCnFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog", "_cn.txt");
        TranslateUtil.genScriptDialogCn(FOLDER_NAME, scriptDialogCnFileName);
        assertFalse(CdFileUtil.isFileEmpty(scriptDialogCnFileName), "script_dialog_cn.txt 生成失败或为空");
        log.info("成功生成 script_dialog_cn.txt");

        // Step04: 生成 voc_cn.txt
        String vocFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "voc", ".txt");
        String vocCnFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "voc_cn", ".txt");
        DictUtil.genVocCnWithGemini(vocFileName, vocCnFileName);
        assertFalse(CdFileUtil.isFileEmpty(vocCnFileName), "voc_cn.txt 生成失败或为空");
        log.info("成功生成 voc_cn.txt");
    }

    @Test
    @Order(3)
    @DisplayName("3. 脚本处理：生成合并脚本和SRT生成用脚本")
    void test03_ProcessAndMergeScripts() {
        log.info("\n========【3. 开始测试：生成合并脚本和SRT生成用脚本】========");

        // Step05: 生成 script_dialog_new.txt
        String scriptDialogNewFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog", "_new.txt");
        GenSrtUtil.genScriptDialogNew(FOLDER_NAME, scriptDialogNewFileName);
        assertFalse(CdFileUtil.isFileEmpty(scriptDialogNewFileName), "script_dialog_new.txt 生成失败或为空");
        log.info("成功生成 script_dialog_new.txt");

        // Step06: 生成中英双语对话脚本.txt
        String scriptDialogFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog", ".txt");
        String scriptDialogCnFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog", "_cn.txt");
        String scriptDialogMergeFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, "_中英双语对话脚本.txt");
        TranslateUtil.mergeScriptContent(scriptDialogFileName, scriptDialogCnFileName, scriptDialogMergeFileName);
        assertFalse(CdFileUtil.isFileEmpty(scriptDialogMergeFileName), "中英双语对话脚本.txt 生成失败或为空");
        log.info("成功生成 中英双语对话脚本.txt");
    }

    @Test
    @Order(4)
    @DisplayName("4. 音频与字幕：生成原始SRT并切割音频")
    void test04_GenerateRawSrtAndSplitAudio() {
        log.info("\n========【4. 开始测试：生成原始SRT并切割音频】========");

        // Step 9. 生成字幕文件 eng_raw.srt
        String srtScriptFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog_new", CdConstants.TXT_EXTENSION);
        srtEngRawFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "eng_raw", CdConstants.SRT_EXTENSION);
        SubtitleUtil.genSrtByExecuteCommand(mp3FileNameFull, srtScriptFileName, srtEngRawFileName, "eng");
        assertFalse(CdFileUtil.isFileEmpty(srtEngRawFileName), "eng_raw.srt 生成失败或为空");
        log.info("成功生成 eng_raw.srt");

        // Step 10. 处理时间并切割音频
        String result = TextProcessor.processFile(srtEngRawFileName);
        assertNotNull(result, "从eng_raw.srt中未能提取到起止时间");
        String[] split = result.split("\\s+");
        assertTrue(split.length >= 2, "提取的起止时间格式不正确");

        mp3FileNameFullNew = CommonUtil.getFullPathFileName(FOLDER_NAME, "audio5", CdConstants.MP3_EXTENSION);
        CdMP3SplitterUtil.splitMP3(mp3FileNameFull, mp3FileNameFullNew, split[0], split[1]);
        assertFalse(CdFileUtil.isFileEmpty(mp3FileNameFullNew), "切割后的音频 audio5.mp3 生成失败或为空");
        log.info("成功切割音频，生成 audio5.mp3");

        // Step08: 生成 script_dialog_new2.txt
        scriptDialogNew2FileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "script_dialog_new2", CdConstants.TXT_EXTENSION);
        SixMinutesStepByStep.process(FOLDER_NAME); // 调用原方法来生成这个文件，因为逻辑比较复杂
        assertFalse(CdFileUtil.isFileEmpty(scriptDialogNew2FileName), "script_dialog_new2.txt 生成失败或为空");
        log.info("成功生成 script_dialog_new2.txt");
    }

    @Test
    @Order(5)
    @DisplayName("5. 最终字幕：生成中英文字幕")
    void test05_GenerateFinalSubtitles() {
        log.info("\n========【5. 开始测试：生成最终中英文字幕】========");
        assertNotNull(mp3FileNameFullNew, "前置条件失败：切割后的音频路径为空");
        assertNotNull(scriptDialogNew2FileName, "前置条件失败：用于生成最终字幕的脚本为空");

        // 5. 生成字幕文件 eng.srt
        srtFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, "eng", CdConstants.SRT_EXTENSION);
        SubtitleUtil.genSrtByExecuteCommand(mp3FileNameFullNew, scriptDialogNew2FileName, srtFileName, "eng");
        assertFalse(CdFileUtil.isFileEmpty(srtFileName), "eng.srt 生成失败或为空");
        log.info("成功生成 eng.srt");

        // 6. 生成字幕文件 chn.srt
        String srcFileNameCn = CommonUtil.getFullPathFileName(FOLDER_NAME, "chn", ".srt");
        TranslateUtil.translateEngSrc(FOLDER_NAME);
        assertFalse(CdFileUtil.isFileEmpty(srcFileNameCn), "chn.srt 生成失败或为空");
        log.info("成功生成 chn.srt");
    }

    @Test
    @Order(6)
    @DisplayName("6. 词汇表生成")
    void test06_GenerateVocabularyTables() {
        log.info("\n========【6. 开始测试：生成词汇表】========");

        // 生成完整词汇表.xlsx
        String fullVocFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, "_完整词汇表.xlsx");
        WordCountUtil.genVocTable(FOLDER_NAME);
        assertFalse(CdFileUtil.isFileEmpty(fullVocFileName), "完整词汇表.xlsx 生成失败或为空");
        log.info("成功生成 完整词汇表.xlsx");

        // 生成核心词汇表.xlsx
        String excelCoreVocFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, "_核心词汇表.xlsx");
        CoreWordUtil.genCoreWordTable(FOLDER_NAME);
        assertFalse(CdFileUtil.isFileEmpty(excelCoreVocFileName), "核心词汇表.xlsx 生成失败或为空");
        log.info("成功生成 核心词汇表.xlsx");

        // 生成高级词汇表.xlsx
        String excelAdvancedFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, "_高级词汇表.xlsx");
        AdvancedWordUtil.genAdvancedWordTable(FOLDER_NAME, CdConstants.TEMPLATE_FLAG);
        assertFalse(CdFileUtil.isFileEmpty(excelAdvancedFileName), "高级词汇表.xlsx 生成失败或为空");
        log.info("成功生成 高级词汇表.xlsx");
    }

    @Test
    @Order(7)
    @DisplayName("7. PPT及图片生成")
    void test07_GeneratePptAndImages() {
        log.info("\n========【7. 开始测试：PPT及图片生成】========");

        // 查询章节名称
        chapterName = GetSixMinutesPpt.queryChapterNameForSixMinutes(FOLDER_NAME);
        assertFalse(StrUtil.isBlank(chapterName), "未能查询到章节名称");
        log.info("查询到章节名称: {}", chapterName);

        // 生成pptx文件
        String pptxFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, ".pptx");
        GetSixMinutesPpt.process(FOLDER_NAME, chapterName);
        assertFalse(CdFileUtil.isFileEmpty(pptxFileName), "PPTX 文件生成失败或为空");
        log.info("成功生成 PPTX 文件");

        // 生成pptx的图片
        String pptPicDir = new File(pptxFileName).getParent() + File.separator + FOLDER_NAME + File.separator;
        PptToImageConverter.convertPptToImages(pptxFileName, pptPicDir, "snapshot");
        assertTrue(new File(pptPicDir).exists() && new File(pptPicDir).isDirectory(), "PPT 图片文件夹生成失败");
        log.info("成功生成 PPT 图片");
    }

    @Test
    @Order(8)
    @DisplayName("8. 发布内容生成")
    void test08_GeneratePublishingContent() {
        log.info("\n========【8. 开始测试：发布内容生成】========");
        assertNotNull(srtFileName, "前置条件失败：最终英文字幕文件名为空");
        assertNotNull(chapterName, "前置条件失败：章节名称为空");

        // 生成国内平台描述文件
        String scriptDialogMergeFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, "_中英双语对话脚本.txt");
        String pptxFileName = CommonUtil.getFullPathFileName(FOLDER_NAME, FOLDER_NAME, ".pptx");
        String descriptionFileName = CdFileUtil.addPostfixToFileName(CdFileUtil.changeExtension(pptxFileName, "md"), "_description");
        TranslationUtil.genDescription(scriptDialogMergeFileName, descriptionFileName);
        assertFalse(CdFileUtil.isFileEmpty(descriptionFileName), "国内平台描述文件生成失败或为空");
        log.info("成功生成国内平台描述文件");

        // 生成油管平台描述文件
        String descriptionFileNameYT = CdFileUtil.addPostfixToFileName(CdFileUtil.changeExtension(pptxFileName, "md"), "_description_yt");
        TranslationUtil.genDescription(scriptDialogMergeFileName, descriptionFileNameYT);
        assertFalse(CdFileUtil.isFileEmpty(descriptionFileNameYT), "油管平台描述文件（英文）生成失败或为空");
        log.info("成功生成油管平台描述文件（英文）");

        // 生成油管平台中、繁体描述
        String chnMdFileName = CdFileUtil.addPostfixToFileName(descriptionFileNameYT, "_chn");
        String chtMdFileName = CdFileUtil.addPostfixToFileName(descriptionFileNameYT, "_cht");
        PreparePublishUtil.genDescriptionForYT(folderPath, FOLDER_NAME, "", "", "6", srtFileName, chapterName);
        assertFalse(CdFileUtil.isFileEmpty(chnMdFileName), "油管平台描述文件（简体中文）生成失败或为空");
        assertFalse(CdFileUtil.isFileEmpty(chtMdFileName), "油管平台描述文件（繁体中文）生成失败或为空");
        log.info("成功生成油管平台中、繁体描述文件");
    }
}
