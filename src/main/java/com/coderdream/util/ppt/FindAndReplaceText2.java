//package com.coderdream.util.ppt;
//
//import com.aspose.slides.IAutoShape;
//import com.aspose.slides.IPlaceholder;
//import com.aspose.slides.IShape;
//import com.aspose.slides.ISlide;
//import com.aspose.slides.Presentation;
//import com.aspose.slides.SaveFormat;
//
//public class FindAndReplaceText3 {
//
//  public static void main(String[] args) {
////    String presentationName =
////      RunExamples.getDataDir_Text() + "TextReplaceExample.pptx";
////    String outPath = RunExamples.getOutPath() + "TextReplaceExample-out.pptx";
////
////    //ExStart:FindAndReplaceText3
////    Presentation pres = new Presentation(presentationName);
////    try {
////      PortionFormat format = new PortionFormat();
////      format.setFontHeight(24f);
////      format.setFontItalic(NullableBool.True);
////      format.getFillFormat().setFillType(FillType.Solid);
////      format.getFillFormat().getSolidFillColor().setColor(Color.RED);
////
////      SlideUtil.findAndReplaceText(pres, true, "[this block] ", "my text",
////        format);
////      pres.save(outPath, SaveFormat.Pptx);
////    } finally {
////      if (pres != null) {
////        pres.dispose();
////      }
////    }
//    //ExEnd:FontFamily
//    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
//    String presentationName = "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064.pptx";
//    // 实例化Presentation类
//    Presentation pres = new Presentation(presentationName);
//    try {
//      // 访问第一张幻灯片
////      int size = pres.getSlides().size();
////      size = 1;
////      for (int i = 0; i < size; i++) {
////        System.out.println("##############"+pres.getSlides().get_Item(i).getShapes().size());
////        ISlide sld = pres.getSlides().get_Item(i);
//      ISlide sld = pres.getSlides().get_Item(1);
//        System.out.println("##$$## " + 1);
//        // 遍历形状以查找占位符
//        for (IShape shp : sld.getShapes()) {
//          if (shp.getPlaceholder() != null) {
//            IPlaceholder placeholder = shp.getPlaceholder();
////            System.out.println("## \t " + shp.getPlaceholder().getType());
////            shp.getPlaceholder().
//
//            int type = placeholder.getType();
////            System.out.println("## \t " + type);
//            switch (type) {
//              case 1:
//                System.out.println(
//                  "## \t " + shp.getPlaceholder().getType() + "这是占位符");
//                // 更改每个占位符中的文本
////                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
//                System.out.println(((IAutoShape) shp).getTextFrame().getText());
//                break;
//              case 15:
////                PictureFrame sh = shp.getFrame();
//                System.out.println(
//                  "## \t " + shp.getPlaceholder().getType() + "这是图片");
//
//
//
//                // 更改每个占位符中的文本
////                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
//                break;
//              default:
//                System.out.println("## \t " + "DEFAULT");
//            }
//
//            // com.aspose.slides.PictureFrame cannot be cast to com.aspose.slides.IAutoShape
//          }
//        }
////      }
//
//      // 将演示文稿保存到磁盘
//      pres.save(
//        "D:\\04_GitHub\\java-architect-util\\free-apps\\src\\main\\resources\\ppt\\6min_202501064_output.pptx",
//        SaveFormat.Pptx);
//    } finally {
//      if (pres != null) {
//        pres.dispose();
//      }
//    }
//
//  }
//}
