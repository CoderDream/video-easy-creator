package com.coderdream.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PPTToHighResPNG {

    public static void main(String[] args) throws IOException {
        // 定义资源文件路径
        Path resourceDir = Paths.get("src", "main", "resources");
        Path pptFilePath = resourceDir.resolve("generated_ppt_with_sentences.pptx");
        Path outputDir = resourceDir.resolve("output");

        // 确保输出目录存在
        File outputDirectory = outputDir.toFile();
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (!created) {
                System.err.println("无法创建输出目录: " + outputDirectory.getAbsolutePath());
                return;
            }
        }

        // 加载PPT文件
        try (FileInputStream pptFile = new FileInputStream(pptFilePath.toFile());
             XMLSlideShow ppt = new XMLSlideShow(pptFile)) {

            // 获取PPT页面大小
            Dimension pageSize = ppt.getPageSize();

            // 设置4K分辨率
            int targetWidth = 3840; // 目标宽度
            int targetHeight = 2160; // 目标高度

            double scaleX = targetWidth / pageSize.getWidth();  // 宽度缩放比例
            double scaleY = targetHeight / pageSize.getHeight(); // 高度缩放比例

            // 遍历幻灯片并导出为PNG
            int slideIndex = 0;
            for (XSLFSlide slide : ppt.getSlides()) {
                // 创建BufferedImage，用于渲染幻灯片内容
                BufferedImage img = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = img.createGraphics();

                // 设置高质量渲染属性
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 填充背景为白色
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, targetWidth, targetHeight);

                // 设置缩放比例，确保内容在原始位置渲染
                graphics.scale(scaleX, scaleY);

                // 修复中文乱码问题，设置默认字体
                graphics.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

                // 渲染幻灯片内容
                slide.draw(graphics);
                graphics.dispose();

                // 导出为PNG文件
                File outputFile = outputDir.resolve("output_slide_" + slideIndex + ".png").toFile();
                ImageIO.write(img, "png", outputFile);
                System.out.println("导出幻灯片 " + slideIndex + " 为图片: " + outputFile.getAbsolutePath());
                slideIndex++;
            }
        }
    }
}
