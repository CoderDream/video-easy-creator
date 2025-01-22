package com.coderdream.util.ppt.demo;


import com.aspose.slides.Shape;
import com.aspose.slides.*;
import com.coderdream.util.ppt.LicenseUtil;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ReplacePicturePlaceholder {
    public static void main(String[] args) throws IOException {

      LicenseUtil.loadLicense();
      String filePath = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064.pptx";
        // 1. 加载演示文稿
        Presentation pres = new Presentation(filePath);

        // 2. 获取需要操作的幻灯片（例如第一张幻灯片）
        ISlide slide = pres.getSlides().get_Item(0);

        // 获取幻灯片中的第一个形状
        IAutoShape shape = (IAutoShape) slide.getShapes().get_Item(0);

        // 3. 判断形状是否是图片占位符
        if(shape.getPlaceholder() != null && shape.getPlaceholder().getType() == PlaceholderType.Picture) {

            // 加载替换图片，将 BufferedImage 转为 InputStream
             BufferedImage bufferedImage = ImageIO.read(new File("D:\\0000\\CoverSample\\c0004.png"));
             if (bufferedImage != null){
                  try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                      ImageIO.write(bufferedImage, "png", os);
                      try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
                            // 使用推荐方法  addImage(InputStream)
                          IPPImage pptxImage = pres.getImages().addImage(is);


                            // 5. 替换图片并保持样式
                            float x = shape.getX();
                            float y = shape.getY();
                            float width = shape.getWidth();
                            float height = shape.getHeight();

                             // 将图片添加到幻灯片
                           IPictureFrame picFrame = slide.getShapes().addPictureFrame(ShapeType.Rectangle, x, y, width, height, pptxImage);

                              // 删除原来的占位符形状
                           slide.getShapes().remove(shape);
                           // 保留占位符的 z-index

 com.aspose.slides.Shape s = (Shape)picFrame;
// s.set
//                             ((Shape)picFrame).setZOrderPosition(shape.getZOrderPosition());

                          }
                  }


             }



         }

        // 6. 保存修改后的演示文稿
        pres.save("D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064113.pptx", SaveFormat.Pptx);
    }
}
