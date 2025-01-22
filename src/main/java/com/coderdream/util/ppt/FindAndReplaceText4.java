package com.coderdream.util.ppt;

import cn.hutool.core.util.StrUtil;
import com.aspose.slides.FillType;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IPPImage;
import com.aspose.slides.IPictureFillFormat;
import com.aspose.slides.IPlaceholder;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.ITable;
import com.aspose.slides.PictureFillMode;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.coderdream.entity.VocInfo;
import com.coderdream.entity.WordInfo;
import com.coderdream.entity.WordInfoEntity;
import com.coderdream.util.excel.CdExcelUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
//import com.aspose.slides.examples.RunExamples;

@Slf4j
public class FindAndReplaceText4 {

  public static void main(String[] args) {
    //    String presentationName =
    //      RunExamples.getDataDir_Text() + "TextReplaceExample.pptx";
    //    String outPath = RunExamples.getOutPath() + "TextReplaceExample-out.pptx";
    //
    //    //ExStart:FindAndReplaceText
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
    String presentationName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501065.pptx";
    // 实例化Presentation类
    Presentation pres = new Presentation(presentationName);
    try {

      Map<String, String> props = new HashMap<>(); // 末日滚动：我们为什么喜欢末日滚动？
      String folderName = "250109";
      props.put("folderName", folderName);
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
        System.out.println(
          sampleSentenceEn + " : " + sampleSentenceEn.length());
        if (StrUtil.isNotEmpty(sampleSentenceEn)
          && sampleSentenceEn.length() < 50) {
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
        System.out.println(
          "##############" + pres.getSlides().get_Item(j).getShapes().size());
        ISlide sld = pres.getSlides().get_Item(j);
        //      ISlide sld = pres.getSlides().get_Item(1);
        System.out.println("##$$## " + 1);

        List<IShape> shapesToRemove = new ArrayList<IShape>(); // 用于存储需要删除的形状的列表
        // 创建 Shapes 集合的副本
//                List<IShape> shapesCopy = new ArrayList<IShape>(sld.getShapes());
//                List<IShape> shapesCopy = sld.getShapes().stream().collect(java.util.stream.Collectors.toList());
        List<IShape> shapesCopy = new ArrayList<IShape>();
        for (IShape shape : sld.getShapes()) {
          shapesCopy.add(shape);
        }
        // 遍历形状以查找占位符
        for (IShape shp : shapesCopy) {
          if (shp.getPlaceholder() != null) {
            IPlaceholder placeholder = shp.getPlaceholder();

            int type = placeholder.getType();

            switch (type) {
              case 1:
                System.out.println(
                  "## \t " + shp.getPlaceholder().getType() + "这是占位符");
                System.out.println(((IAutoShape) shp).getTextFrame().getText());

                String text = ((IAutoShape) shp).getTextFrame().getText();
                // 去掉{{}}
                text = text.replaceAll("\\{", "");
                text = text.replaceAll("\\}", "");
                // TODO
                String newText = props.get(text);
                log.info("## \t {} \t {}", text, newText);
                if (StrUtil.isNotEmpty(newText)) {
                  ((IAutoShape) shp).getTextFrame().setText(newText);
                }

                break;
              case 15:

                System.out.println(
                  "## \t " + shp.getPlaceholder().getType() + "这是图片");

                // 加载替换图片，将 BufferedImage 转为 InputStream
                BufferedImage bufferedImage = null;
                try {
                  bufferedImage = ImageIO.read(new File(
                    "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180920\\180920.jpg"));
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                if (bufferedImage != null) {
                  try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", os);
                    try (InputStream is = new ByteArrayInputStream(
                      os.toByteArray())) {
                      // 使用推荐方法 addImage(InputStream)
                      IPPImage pptxImage = pres.getImages().addImage(is);

                      // 5. 替换图片并保持样式
                      float x = shp.getX();
                      float y = shp.getY();
                      float width = shp.getWidth();
                      float height = shp.getHeight();

                      // 设置圆角大小

                      // 创建圆角矩形形状
                      IAutoShape shape = sld.getShapes()
                        .addAutoShape(ShapeType.RoundCornerRectangle, x, y,
                          width, height);
                      shape.getEffectFormat().enableSoftEdgeEffect();
                      shape.getEffectFormat().getSoftEdgeEffect().setRadius(15);

//                                            shape.getEffectFormat().enableGlowEffect();
//                                            shape.getEffectFormat().getGlowEffect().getColor().setColor(
//                                              Color.MAGENTA);
//                                            shape.getEffectFormat().enableOuterShadowEffect();
//                                            shape.getEffectFormat().getOuterShadowEffect().getShadowColor().setColor(Color.DARK_GRAY);
//                                            shape.getEffectFormat().getOuterShadowEffect().setDistance(10);
//                                            shape.getEffectFormat().getOuterShadowEffect().setDirection(45);

                      // 将图片添加到幻灯片
//                      IPictureFrame picFrame = sld.getShapes().addPictureFrame(
//                        ShapeType.Rectangle, x + 20, y + 20, width - 40,
//                        height - 40, pptxImage);

                      // 设置图片填充
                      shape.getFillFormat().setFillType(
                        FillType.Picture);
                      shape.getFillFormat().getPictureFillFormat().getPicture()
                        .setImage(pptxImage);

                      // 设置图片填充
                      shape.getFillFormat().setFillType(FillType.Picture);
                      IPictureFillFormat pictureFillFormat = shape.getFillFormat()
                        .getPictureFillFormat();
                      pictureFillFormat.getPicture().setImage(pptxImage);

                      // 设置填充模式为拉伸
                      pictureFillFormat.setPictureFillMode(
                        PictureFillMode.Stretch);
                      // 设置缩放比例

                      // 获取图片对象
//                      IPicture picture = pictureFillFormat.getPicture();
//                      // 设置缩放比例, 通过裁剪实现
//                      double scaleX = 0.8; // 水平缩放比例 0.8
//                      double scaleY = 0.8; // 垂直缩放比例 0.8
//
//                      picture.setCropLeft((float) ((1 - scaleX) / 2));
//                      picture.setCropRight((float) ((1 - scaleX) / 2));
//                      picture.setCropTop((float) ((1 - scaleY) / 2));
//                      picture.setCropBottom((float) ((1 - scaleY) / 2));

                      // 设置圆角大小
//                                            shape.getAdjustments().add((float)0.2);
                      //设置圆角大小
//                                            IAdjustValueCollection adjustments = shape.getAdjustments();
//                                            adjustments.add(0).setValue((float)0.2);

//                                            IAdjustValueCollection adjustments = shape.getAdjustments();
//                                            if(adjustments.size() == 0){
//                                                adjustments.add(0).setValue((float)0.2);
//                                            }else{
//                                                adjustments.get_Item(0).setValue((float)0.2);
//                                            }

                      // 删除原来的占位符形状
                      shapesToRemove.add(shp);

                      // 保留占位符的 z-index
//                                            ((Shape) picFrame).setZOrderPosition(shp.getZOrderPosition());
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }

                break;
              default:
                System.out.println("## \t " + "DEFAULT");
            }


          }
        }
        //  统一删除需要删除的形状
        for (IShape shapeToRemove : shapesToRemove) {
          sld.getShapes().remove(shapeToRemove);
        }

      }

      // 填充高级词汇
      List<WordInfoEntity> wordInfoEntityList = CdExcelUtil.getAdvancedWordList(
        folderName);// generateWordInfoList();
      List<WordInfo> wordInfoList = new ArrayList<>();
      WordInfo wordInfo;
      for (WordInfoEntity wordInfoEntity : wordInfoEntityList) {
        wordInfo = new WordInfo();
        BeanUtils.copyProperties(wordInfoEntity, wordInfo);
        wordInfo.setCn(wordInfoEntity.getComment());
        wordInfoList.add(wordInfo);
      }

      // 将列表分割成12个对象为一组，每组包含12个对象
      List<List<WordInfo>> wordInfoListGroups = new ArrayList<>();
      for (int j = 0; j < wordInfoList.size(); j += 12) {
        wordInfoListGroups.add(
          wordInfoList.subList(j, Math.min(j + 12, wordInfoList.size())));
      }
      int count = 0;
      // 遍历每一页幻灯片
      if (wordInfoListGroups.size() > 0) {
        int groupSize = wordInfoListGroups.size();
        switch (groupSize) {
          case 1:
          case 2:
          case 3:
            List<WordInfo> wordInfoList1 = wordInfoListGroups.get(0);

            // 获取当前幻灯片
            ISlide sld = pres.getSlides().get_Item(8);

            // Initializes null TableEx
            ITable tbl = null;
            // Accesses the first slide
            // Iterates through the shapes and sets a reference to the table found
            for (IShape shp : sld.getShapes()) {
              System.out.println("## \t " + shp);
              if (shp instanceof ITable) {
                tbl = (ITable) shp;
                // Sets the text for the first column of the second row
                int x = 0;
                int y = 0;
                for (WordInfo wordInfo2 : wordInfoList1) {
                  x++;
                  count+=x;
                  tbl.get_Item(0, 1).getTextFrame().setText(count+"");
                  tbl.get_Item(1, 1).getTextFrame().setText(wordInfo2.getWord());

                  tbl.get_Item(2, 1).getTextFrame().setText(wordInfo2.getUk());
                  tbl.get_Item(3, 1).getTextFrame().setText(wordInfo2.getCn());
                  tbl.get_Item(4, 1).getTextFrame().setText(wordInfo2.getLevelStr());
                  tbl.get_Item(5, 1).getTextFrame().setText(wordInfo2.getTimes()+"");
                }
              }
            }

            break;

          case 4:
            break;

//          case 3:
//            break;
          default:

        }
        // 获取当前幻灯片
//        ISlide sld = pres.getSlides().get_Item(8);
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
