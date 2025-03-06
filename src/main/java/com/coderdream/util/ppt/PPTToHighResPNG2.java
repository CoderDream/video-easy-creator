package com.coderdream.util.ppt;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PPTToHighResPNG2 {
    public static void main(String[] args) throws IOException {
        // 加载生成的PPT文件
        try (FileInputStream pptFile = new FileInputStream("src/main/resources/generated_ppt_with_sentences.pptx");
             XMLSlideShow ppt = new XMLSlideShow(pptFile)) {

            // 设置4K分辨率（根据16:9幻灯片的比例设置）
            int width = 3840; // 4K宽度
            int height = 2160; // 4K高度

            // 遍历每一张幻灯片并导出为PNG
            int slideIndex = 0;
            for (XSLFSlide slide : ppt.getSlides()) {
                // 创建BufferedImage用于渲染幻灯片
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = img.createGraphics();

                // 设置高质量渲染属性
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                // 填充背景为白色
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, width, height);

                // 渲染幻灯片内容到BufferedImage
                slide.draw(graphics);
                graphics.dispose();

                // 输出PNG图片文件
                File outputFile = new File("src/main/resources/output_slide_" + slideIndex + ".png");
                ImageIO.write(img, "png", outputFile);
                System.out.println("导出幻灯片 " + slideIndex + " 为图片: " + outputFile.getAbsolutePath());
                slideIndex++;
            }
        }
    }
}
