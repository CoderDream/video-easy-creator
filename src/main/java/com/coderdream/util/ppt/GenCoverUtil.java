package com.coderdream.util.ppt;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IPlaceholder;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.resource.ResourcesSourcePathUtil;

import java.io.File;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenCoverUtil {

  public static void main(String[] args) {
    String presentationName = "D:\\0000\\ppt\\商务英语.pptx";
    String folderPath = "D:\\0000\\ppt\\";
    String chapterFileName = "900_cht_name.txt";

    String imageFormat = "png";
    GenCoverUtil.process(folderPath, chapterFileName, presentationName,
      imageFormat, 1920, 1080);
  }

  public static void process(String folderPath, String chapterFileName,
    String presentationName, String imageFormat) {
    process(folderPath, chapterFileName, presentationName, imageFormat,
      1920, 1080);
  }

  public static void process(String folderPath, String chapterFileName,
    String presentationName, String imageFormat, int width, int height) {
    String resourcesPath = ResourcesSourcePathUtil.getResourcesSourceAbsolutePath();
    List<String> contentList = CdFileUtil.readFileContent(
      resourcesPath + File.separator + chapterFileName);
    if (CollectionUtil.isEmpty(contentList)) {
      log.error("{} 文件内容为空", chapterFileName);
      return;
    }
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    List<String> slogans = Arrays.asList(
      "越聽越明瞭",
      "越聽越專注",
      "越聽越共鳴",
      "越聽越上癮",
      "越聽越開闊");
    for (String content : contentList) {
      String[] split = content.split("-");
      if (split.length == 4) {
        // 随机整数0~4
        int i = new Random().nextInt(slogans.size());
        extracted(folderPath, split[2], split[3], slogans.get(i),
          presentationName, imageFormat, width, height);
      }
    }
  }

  private static void extracted(String folderPath, String ep, String topic,
    String slogan, String presentationName, String imageFormat, int width,
    int height) {
    Locale locale = new Locale("zh", "cn");
    Locale.setDefault(locale);
    // 生成封面图
    String coverPath = folderPath + "cover" + File.separator;
    File coverPathFile = new File(coverPath);
    if (!coverPathFile.exists()) {
      boolean mkdir = coverPathFile.mkdirs();
      if (mkdir) {
        log.info("封面图创建目录成功，路径：{}", coverPath);
      } else {
        log.error("封面图创建目录失败，路径：{}", coverPath);
        return;
      }
    }
    String prefix = "Chapter0"; // ch0
    if (ep.length() == 3) {
      prefix = "Chapter"; // ch0
    }
    String outputFileName =
      coverPath + prefix + ep + "_" + height + "p." + imageFormat;

    // 将演示文稿保存到磁盘
    String newPptName = CdFileUtil.addPostfixToFileName(presentationName,
      "_" + ep);
//    pres.getDocumentProperty().setLanguage("zh-CN"); // 使用标准的简体中文代码
//    pres.getDocument().getProperties().setLanguage("zh-CN");

    // 防止重复生成
    if (CdFileUtil.isFileEmpty(newPptName) && CdFileUtil.isFileEmpty(
      outputFileName)) {

      // 实例化Presentation类
      Presentation pres = new Presentation(presentationName);
      // begin
      try {

        Map<String, String> props = new HashMap<>(); // 末日滚动：我们为什么喜欢末日滚动？
        props.put("ep", "EP-" + ep);
        props.put("topic", topic);
        props.put("slogan", slogan);

        ISlide sld = pres.getSlides().get_Item(0);
//        System.out.println("##$$## " + 1);
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
//                System.out.println(
//                  "## \t " + shp.getPlaceholder().getType() + "这是占位符");
                // 更改每个占位符中的文本
//                ((IAutoShape) shp).getTextFrame().setText("这是占位符");
//                System.out.println(((IAutoShape) shp).getTextFrame().getText());
                String text = ((IAutoShape) shp).getTextFrame().getText();
                String newText = props.get(text);
//                log.info("## \t {} \t {}", text, newText);
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
        // 保存演示文稿到磁盘
        pres.save(newPptName, SaveFormat.Pptx);
        log.info("生成ppt成功，路径：{}", newPptName);

        // end
      } finally {
        pres.dispose();
      }
    }

//      String imageFormat = "png";

    if (!CdFileUtil.isFileEmpty(outputFileName)) {
      log.info("封面图已存在，路径：{}", outputFileName);
      if (!CdFileUtil.isFileEmpty(newPptName)) {
        // 删除ppt
        boolean delete = new File(newPptName).delete();
        if (delete) {
          log.info("删除ppt成功，路径：{}", newPptName);
        }
      }
    } else {
//        log.info("封面图不存在，开始生成，路径：{}", outputFileName);
      PptToImageConverter.convertFirstSlideToImage(newPptName,
        outputFileName, width, height, imageFormat);
    }

  }
}
