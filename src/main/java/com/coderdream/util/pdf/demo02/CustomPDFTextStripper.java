package com.coderdream.util.pdf.demo02;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class CustomPDFTextStripper extends PDFTextStripper {

    private String previousLine = "";

    public CustomPDFTextStripper() throws IOException {
        super();
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        if (textPositions.isEmpty()) {
            super.writeString(string, textPositions);
            return;
        }

        TextPosition firstPosition = textPositions.get(0);
        float y = firstPosition.getYDirAdj(); // 文本的Y坐标
        float x = firstPosition.getXDirAdj();

        // 检查是否是新段落 (可以根据Y坐标的变化判断)
        if (!previousLine.isEmpty()) {
            TextPosition previousLastPosition = null;
            if ( lastPositions.size() > 0) {
                previousLastPosition = lastPositions.get(lastPositions.size() - 1);
            }

            // 根据Y坐标判断段落， 可以调整阈值
            if (previousLastPosition != null && Math.abs(y - previousLastPosition.getYDirAdj()) > 12) {
                writeParagraphSeparator(); // 插入段落分隔符
            }
        }

        // 检查是否是空行
        if (string.trim().isEmpty()) {
            writeLineSeparator();  //写入换行符，代表空行
        }
        super.writeString(string, textPositions); // 写入文本

        previousLine = string;
        lastPositions = textPositions;  // 保存当前行的 TextPosition 信息
    }

    protected void writeParagraphSeparator() throws IOException {
        getOutput().write(getParagraphSeparator());  // 默认是换行符，可以自定义
    }

    @Override
    protected void writeLineSeparator() throws IOException
    {
        getOutput().write(getLineSeparator());
    }

    private List<TextPosition> lastPositions = null;


    private String paragraphSeparator = System.lineSeparator() + System.lineSeparator(); // 段落分隔符 (两个换行)

    public String getParagraphSeparator() {
        return paragraphSeparator;
    }

    public void setParagraphSeparator(String paragraphSeparator) {
        this.paragraphSeparator = paragraphSeparator;
    }
}
