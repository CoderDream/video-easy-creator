package com.coderdream.util.ppt;

import cn.hutool.core.util.StrUtil;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IPlaceholder;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
//import com.coderdream.freeapps.util.bbc.DictUtils;
//import com.coderdream.freeapps.util.bbc.VocInfo;
import com.coderdream.util.cd.CdFileUtil;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
//import com.aspose.slides.examples.RunExamples;

@Slf4j
public class GenCoverUtil {

  public static void main(String[] args) {
    String presentationName = "D:\\0000\\ppt\\商务英语.pptx";
    String chapterFileName = "900_cht_name.txt";
    GenCoverUtil.process(chapterFileName, presentationName);
  }

  public static void process(String chapterFileName,
    String presentationName) {

    List<String> contentList = CdFileUtil.readFileContent(
      "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\"
        + chapterFileName);
    //ExEnd:FontFamily
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    List<String> slogans = Arrays.asList(
      "越聽越明瞭",
      "越聽越專注",
      "越聽越共鳴",
      "越聽越上癮",
      "越聽越開闊");
    for (String content : contentList) {
      String[] split = content.split(" ");
      if (split.length == 3) {
        // 随机整数0~4
        int i = new Random().nextInt(slogans.size());
        extracted(split[1], split[2], slogans.get(i), presentationName);
      }
    }
  }

  private static void extracted(String ep, String topic, String slogan,
    String presentationName) {
    Map<String, String> props = new HashMap<>(); // 末日滚动：我们为什么喜欢末日滚动？
    props.put("ep", "EP-" + ep);
    props.put("topic", topic);
    props.put("slogan", slogan);

    // 实例化Presentation类
    Presentation pres = new Presentation(presentationName);
    try {

      ISlide sld = pres.getSlides().get_Item(0);
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
              String newText = props.get(text);
              log.info("## \t {} \t {}", text, newText);
              if (StrUtil.isNotEmpty(newText)) {
                ((IAutoShape) shp).getTextFrame().setText(newText);
              }

              break;
            case 15:
//                PictureFrame sh = shp.getFrame();

              // 更改每个占位符中的文本
//                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
              break;
            default:
              System.out.println("## \t " + "DEFAULT");
          }

          // com.aspose.slides.PictureFrame cannot be cast to com.aspose.slides.IAutoShape
        }
      }

      // 将演示文稿保存到磁盘
      String newPptName = CdFileUtil.addPostfixToFileName(presentationName,
        "_" + ep);
      pres.save(newPptName, SaveFormat.Pptx);
      File file = new File(newPptName);
      String coverDir = file.getParent() + "\\cover\\";
      File coverDirFile = new File(coverDir);
      if (!coverDirFile.exists()) {
        boolean mkdir = coverDirFile.mkdirs();
        log.info("## \t {}", mkdir);
      }
      PptToPng1080p.savePptToPng1080p(newPptName, coverDir);
    } finally {
      pres.dispose();
    }
  }
}
