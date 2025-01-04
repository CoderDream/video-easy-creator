package com.coderdream.util.pdf;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfToImageConverterUtil {

    public static void convertPdfToImages(String pdfPath, String fileName, String outputDir, int dpi) {

        checkDirs(pdfPath);
        checkDirs(outputDir);

        try (PDDocument document = PDDocument.load(new File(pdfPath + fileName))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();

            for (int page = 0; page < numberOfPages; page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.RGB);
                // 时分秒
                String timeStamp = DateUtil.format(new Date(),  DatePattern.PURE_DATE_FORMAT);
                File outputfile = new File(
                    outputDir + "/Snapshot_" + timeStamp + "_" + String.format("%03d", (page + 1)) + ".png");
                ImageIO.write(bim, "PNG", outputfile);
                System.out.println("Page " + (page + 1) + " converted to " + outputfile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkDirs(String pdfPath) {
        File directory = new File(pdfPath);

        // 检查文件夹是否存在
        if (!directory.exists()) {
            // 如果不存在，则创建文件夹及其所有父文件夹
            boolean success = directory.mkdirs();
            if (success) {
                System.out.println("文件夹已成功创建: " + pdfPath);
            } else {
                System.out.println("无法创建文件夹: " + pdfPath);
            }
        } else {
            System.out.println("文件夹已存在: " + pdfPath);
        }
    }
// PDF used fonts: [Arial-BoldMT, MicrosoftYaHei, OpenSans-Bold, MicrosoftYaHei-Bold, OpenSans-Regular, ArialMT]
}
