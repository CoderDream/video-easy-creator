package com.coderdream.util.slides;

import com.aspose.slides.*;
import com.coderdream.util.ppt.LicenseUtil;
import com.coderdream.util.ppt.MicrosoftConstants;
//import com.aspose.slides.export.PdfOptions;

public class PptToPdfConverter {

    /**
     * 将 PPT/PPTX 转换为高清 PDF
     *
     * @param pptFilePath  PPT/PPTX 文件路径
     * @param pdfFilePath  输出 PDF 文件路径
     */
    public static void convertPptToHighQualityPdf(String pptFilePath, String pdfFilePath) {


        LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

        Presentation presentation = null; // 声明在 try 外面
        try {
            presentation = new Presentation(pptFilePath);

            // 创建 PdfOptions 对象，用于配置 PDF 转换选项
            PdfOptions pdfOptions = new PdfOptions();

            // ******** 设置 PDF 优化选项 (关键) ********

            // 1. 提高图片质量 (关键)
            pdfOptions.setJpegQuality((byte) 100); // JPEG 图片质量 (0-100, 100 最高)

            // 2. 使用 Deflate 压缩算法 (通常效果较好)
            pdfOptions.setTextCompression(PdfTextCompression.Flate);

            // 3. 嵌入所有字体 (确保字体一致性, 但会增大文件大小)
            pdfOptions.setEmbedFullFonts(true);

            // 4.  设置 PDF 标准 (根据需要选择)
            //    - PdfCompliance.PDF_A_1A:  长期存档, 包含所有信息, 文件较大
            //    - PdfCompliance.PDF_15:    通用标准, 兼容性较好
            pdfOptions.setCompliance(PdfCompliance.Pdf15);


            // 5.  (可选) 设置 DPI, 影响矢量图形和文本的清晰度
            //     注意:  较高的 DPI 会显著增大 PDF 文件大小,  300 DPI 通常足够
             pdfOptions.setSufficientResolution(300);  // DPI 设置


            // 6. (可选) 禁用渐变效果的降级 (如果幻灯片中有渐变)
            //     这可以防止渐变在 PDF 中出现条带状
            // pdfOptions.setDisableDownsampleImages(true);


            // 7. (可选)  其他优化选项...
            //    -  pdfOptions.setEmbedTrueTypeFontsForASCII(true); // 嵌入 ASCII 字符的 TrueType 字体
            //    -  pdfOptions.setDrawSlidesFrame(false);         // 不绘制幻灯片边框


            // 使用配置好的 PdfOptions 保存为 PDF
            presentation.save(pdfFilePath, SaveFormat.Pdf, pdfOptions);

            System.out.println("PPT 文件已成功转换为高清 PDF: " + pdfFilePath);

        } catch (Exception e) {
            System.err.println("Error converting PPT to PDF: " + pptFilePath);
            e.printStackTrace();
        }
    }
     /**
      * 测试
      * @param args
      */
    public static void main(String[] args) {
        String pptFilePath = "D:\\0000\\ppt\\Book02\\Book02模板_01.pptx"; // 替换为您的 PPT 文件路径
        String pdfFilePath = "D:\\0000\\ppt\\Book02\\Book02模板_01.pdf"; // 替换为输出 PDF 文件路径

        convertPptToHighQualityPdf(pptFilePath, pdfFilePath);
    }
}
