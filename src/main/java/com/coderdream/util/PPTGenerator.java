package com.coderdream.util;

import org.apache.poi.xslf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PPTGenerator {

    // 定义 Sentence 类，包含中文、英文和音标
    static class Sentence {
        private final String chinese;
        private final String english;
        private final String phonetic;

        public Sentence(String chinese, String english, String phonetic) {
            this.chinese = chinese;
            this.english = english;
            this.phonetic = phonetic;
        }

        public String getChinese() {
            return chinese;
        }

        public String getEnglish() {
            return english;
        }

        public String getPhonetic() {
            return phonetic;
        }
    }

    public static void main(String[] args) throws IOException {
        // 1. 准备对象列表
        List<Sentence> sentences = Arrays.asList(
            new Sentence("你好，世界！", "Hello, World!", "[həˈləʊ wɜːld]"),
            new Sentence("今天天气很好。", "The weather is nice today.", "[ðə ˈweðər ɪz naɪs təˈdeɪ]"),
            new Sentence("我喜欢编程。", "I love programming.", "[aɪ lʌv ˈprəʊɡræmɪŋ]")
        );

        // resources/template.pptx 是你的PPT模板文件，确保它存在且格式正确。
        String folderPath = "src/resources/";

        // 2. 加载模板文件
        try (FileInputStream templateStream = new FileInputStream("src/main/resources/template.pptx");
             XMLSlideShow templatePPT = new XMLSlideShow(templateStream)) {

            // 创建新PPT
            XMLSlideShow newPPT = new XMLSlideShow();

            // 复制首页
            XSLFSlide masterSlide = templatePPT.getSlides().get(0);
            XSLFSlide newMasterSlide = newPPT.createSlide();
            copySlideContent(masterSlide, newMasterSlide);

            // 动态生成子页并填充内容
            XSLFSlide subPageTemplate = templatePPT.getSlides().get(1); // 子页模板假设为第二页
            for (Sentence sentence : sentences) {
                XSLFSlide newSubPage = newPPT.createSlide();
                copySlideContent(subPageTemplate, newSubPage);

                // 填充内容
                for (XSLFShape shape : newSubPage.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String placeholderText = textShape.getText();

                        // 根据占位符替换文本
                        if (placeholderText.contains("{chinese}")) {
                            textShape.setText(sentence.getChinese());
                        } else if (placeholderText.contains("{english}")) {
                            textShape.setText(sentence.getEnglish());
                        } else if (placeholderText.contains("{phonetic}")) {
                            textShape.setText(sentence.getPhonetic());
                        }
                    }
                }
            }

            // 复制尾页
            XSLFSlide footerSlide = templatePPT.getSlides().get(2); // 假设尾页是第三页
            XSLFSlide newFooterSlide = newPPT.createSlide();
            copySlideContent(footerSlide, newFooterSlide);

            // 保存新文件
            try (FileOutputStream out = new FileOutputStream("src/main/resources/generated_ppt_with_sentences.pptx")) {
                newPPT.write(out);
            }

            System.out.println("PPT 生成完成！");
        }
    }

    // 辅助方法：复制幻灯片内容
    private static void copySlideContent(XSLFSlide sourceSlide, XSLFSlide targetSlide) {
        for (XSLFShape shape : sourceSlide.getShapes()) {
            // 复制文本框
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape textShape = (XSLFTextShape) shape;
                XSLFTextShape newShape = targetSlide.createTextBox();
                newShape.setAnchor(textShape.getAnchor());
                newShape.setText(textShape.getText());
                newShape.setFillColor(textShape.getFillColor());
            }
            // 可以根据需求扩展复制图像、表格等其他形状
        }
    }
}
