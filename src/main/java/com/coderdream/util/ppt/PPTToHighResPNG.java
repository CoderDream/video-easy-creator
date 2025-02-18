package com.coderdream.util.ppt;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PPTToHighResPNG {

    public static void main(String[] args) throws IOException {
        // 输入的 PPT 文件路径
        String pptFilePathStr = "D:\\0000\\ppt\\Book02\\Book02模板_01.pptx";
        Path pptFilePath = Paths.get(pptFilePathStr);

        // 从 PPT 文件路径中提取文件夹名称
        String folderName = pptFilePath.getFileName().toString().replace(".pptx", ""); // 去除 .pptx 后缀

        // 构建输出目录路径：与 PPT 文件同目录，名为 folderName 的子文件夹
        Path outputDir = pptFilePath.getParent().resolve(folderName);

        // 确保输出目录存在
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir); // 使用更现代的 Files.createDirectories
        }

        // 加载 PPT 文件
        try (FileInputStream pptFile = new FileInputStream(pptFilePath.toFile());
             XMLSlideShow ppt = new XMLSlideShow(pptFile)) {

            // 获取 PPT 页面大小
            Dimension pageSize = ppt.getPageSize();

            // 设置 4K 分辨率
            int targetWidth = 3840;  // 目标宽度
            int targetHeight = 2160; // 目标高度

            double scaleX = targetWidth / pageSize.getWidth();   // 宽度缩放比例, 强制类型转换为double
            double scaleY = targetHeight / pageSize.getHeight(); // 高度缩放比例, 强制类型转换为double

            // 遍历幻灯片并导出为 PNG
            int slideIndex = 0;
            for (XSLFSlide slide : ppt.getSlides()) {
                // 创建 BufferedImage，用于渲染幻灯片内容
                BufferedImage img = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = img.createGraphics();

                // 设置高质量渲染属性
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); //新增差值
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 填充背景为白色
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, targetWidth, targetHeight);

                // 设置缩放比例
                graphics.scale(scaleX, scaleY);

                // 修复中文乱码问题，设置默认字体 (如果需要)
                graphics.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

                // 渲染幻灯片内容
                slide.draw(graphics);
                graphics.dispose();

                // 导出为 PNG 文件
                File outputFile = outputDir.resolve("output_slide_" + slideIndex + ".png").toFile();
                ImageIO.write(img, "png", outputFile);
                System.out.println("导出幻灯片 " + slideIndex + " 为图片: " + outputFile.getAbsolutePath());
                slideIndex++;
            }
        }
    }
}
