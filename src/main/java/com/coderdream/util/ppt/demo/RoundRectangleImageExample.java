package com.coderdream.util.ppt.demo;

import com.aspose.slides.*;
import com.coderdream.util.ppt.LicenseUtil;
import com.coderdream.util.ppt.MicrosoftConstants;
import java.io.FileNotFoundException;

public class RoundRectangleImageExample {
    public static void main(String[] args) throws FileNotFoundException {
        LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
        // 创建一个新的演示文稿
        Presentation presentation = new Presentation();

        // 获取幻灯片
        ISlide slide = presentation.getSlides().get_Item(0);

        // 添加图片
        String imagePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180920\\180920.jpg"; // 请替换为实际的图片路径
        IPPImage image = presentation.getImages().addImage(new java.io.FileInputStream(imagePath));

        // 设置图片框的位置和大小
        float x = 100;
        float y = 100;
        float width = 400;
        float height = 300;

        // 创建圆角矩形形状
        IAutoShape shape = slide.getShapes().addAutoShape(ShapeType.RoundCornerRectangle, x, y, width, height);

        // 设置图片作为填充
        shape.getFillFormat().setFillType(FillType.Picture);
        shape.getFillFormat().getPictureFillFormat().getPicture().setImage(image);

        // 设置圆角的半径
//        shape.getGeometry().setRoundCornersRadius(30);
        // 设置圆角的半径
//        shape.getLines().setWidth(0); // 无边框
//        shape.getFillFormat().getPictureFillFormat().getPicture().setImage(image);
//        shape.getCornerRadius().setRadius(30);  // 设置圆角半径

        // 保存演示文稿
        try {
            presentation.save("output3333.pptx", SaveFormat.Pptx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
