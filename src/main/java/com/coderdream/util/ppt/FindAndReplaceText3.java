package com.coderdream.util.ppt;

import cn.hutool.core.util.StrUtil;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IPPImage;
import com.aspose.slides.IPictureFrame;
import com.aspose.slides.IPlaceholder;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
//import com.coderdream.freeapps.util.bbc.DictUtils;
//import com.coderdream.freeapps.util.bbc.VocInfo;
import com.aspose.slides.ShapeType;
import com.coderdream.entity.VocInfo;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
//import com.aspose.slides.examples.RunExamples;

@Slf4j
public class FindAndReplaceText3 {

  public static void main(String[] args) {
//    String presentationName =
//      RunExamples.getDataDir_Text() + "TextReplaceExample.pptx";
//    String outPath = RunExamples.getOutPath() + "TextReplaceExample-out.pptx";
//
//    //ExStart:FindAndReplaceText3
//    Presentation pres = new Presentation(presentationName);
//    try {
//      PortionFormat format = new PortionFormat();
//      format.setFontHeight(24f);
//      format.setFontItalic(NullableBool.True);
//      format.getFillFormat().setFillType(FillType.Solid);
//      format.getFillFormat().getSolidFillColor().setColor(Color.RED);
//
//      SlideUtil.findAndReplaceText(pres, true, "[this block] ", "my text",
//        format);
//      pres.save(outPath, SaveFormat.Pptx);
//    } finally {
//      if (pres != null) {
//        pres.dispose();
//      }
//    }
    //ExEnd:FontFamily
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    String presentationName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064.pptx";
    // 实例化Presentation类
    Presentation pres = new Presentation(presentationName);
    try {

      Map<String, String> props = new HashMap<>(); // 末日滚动：我们为什么喜欢末日滚动？
      String folderName = "250109";
      List<VocInfo> vocInfoList = DictUtils.getVocInfoList(folderName);
      int i = 1;
      String sampleSentenceEn;
      String sampleSentenceCn;
      for (VocInfo vocInfo : vocInfoList) {
//            System.out.println(vocInfo);
        props.put("word" + i, vocInfo.getWord());
        props.put("wordEn" + i,
          vocInfo.getWordExplainEn());
        props.put("wordCn" + i, vocInfo.getWordCn());
        props.put("wordCnEx" + i, vocInfo.getWordExplainCn());
        sampleSentenceEn = vocInfo.getSampleSentenceEn();
        sampleSentenceCn = vocInfo.getSampleSentenceCn();
        String mid = "";
        System.out.println(sampleSentenceEn + " : " + sampleSentenceEn.length());
        if (StrUtil.isNotEmpty(sampleSentenceEn) && sampleSentenceEn.length() < 50) {
          mid = "\n";
        }
        // TODO
        props.put("wordSen" + i, sampleSentenceEn + mid + sampleSentenceCn);
        props.put("wordSenEn" + i, sampleSentenceEn);
        props.put("wordSenCn" + i, sampleSentenceCn);

        i++;
      }
      // 访问第一张幻灯片
      int sizePpt = pres.getSlides().size();
//      sizePpt = 1;
      for (int j = 0; j < sizePpt; j++) {
        System.out.println("##############"+pres.getSlides().get_Item(j).getShapes().size());
        ISlide sld = pres.getSlides().get_Item(j);
//      ISlide sld = pres.getSlides().get_Item(1);
        System.out.println("##$$## " + 1);
        // 遍历形状以查找占位符
        for (IShape shp : sld.getShapes()) {
          if (shp.getPlaceholder() != null) {
            IPlaceholder placeholder = shp.getPlaceholder();
//            System.out.println("## \t " + shp.getPlaceholder().getType());
//            shp.getPlaceholder().

            int type = placeholder.getType();

//            System.out.println("## \t " + type);
            switch (type) {
              case 1:
                System.out.println(
                  "## \t " + shp.getPlaceholder().getType() + "这是占位符");
                // 更改每个占位符中的文本
//                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
                System.out.println(((IAutoShape) shp).getTextFrame().getText());

                String text = ((IAutoShape) shp).getTextFrame().getText();
                // 去掉{{}}
                text = text.replaceAll("\\{", "");
                text = text.replaceAll("\\}", "");
                // TODO
                String newText = props.get(text);
                log.info("## \t {} \t {}",text, newText);
                if (StrUtil.isNotEmpty(newText)) {
                  ((IAutoShape) shp).getTextFrame().setText(newText);
                }

//                switch (text) {
//                  case "":
//                    ((IAutoShape) shp).getTextFrame().setText("这是占位符");
//                    break;
//                }



                break;
              case 15:
//                PictureFrame sh = shp.getFrame();
                System.out.println(
                  "## \t " + shp.getPlaceholder().getType() + "这是图片");

// 加载替换图片，将 BufferedImage 转为 InputStream
                BufferedImage bufferedImage = null;
                try {
                  bufferedImage = ImageIO.read(new File("D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180920\\180920.jpg"));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                if (bufferedImage != null) {
                  try{
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", os);
                    try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
                      // 使用推荐方法 addImage(InputStream)
                      IPPImage pptxImage = pres.getImages().addImage(is);

                      // 5. 替换图片并保持样式
                      float x = shp.getX();
                      float y = shp.getY();
                      float width = shp.getWidth();
                      float height = shp.getHeight();

                      // 将图片添加到幻灯片
                      IPictureFrame picFrame = sld.getShapes().addPictureFrame(
                        ShapeType.Rectangle, x, y, width, height, pptxImage);

                      // 删除原来的占位符形状
                      sld.getShapes().remove(shp);

                      // 保留占位符的 z-index
//                      ((Shape) picFrame).setZOrderPosition(shape.getZOrderPosition());
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }

                // 更改每个占位符中的文本
//                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
                break;
              default:
                System.out.println("## \t " + "DEFAULT");
            }

            // com.aspose.slides.PictureFrame cannot be cast to com.aspose.slides.IAutoShape
          }
        }
      }

      // 将演示文稿保存到磁盘
      pres.save(
        "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064_output2.pptx",
        SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }

  }
}
