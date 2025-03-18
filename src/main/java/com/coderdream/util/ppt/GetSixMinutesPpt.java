package com.coderdream.util.ppt;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aspose.slides.FillType;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IPPImage;
import com.aspose.slides.IPictureFillFormat;
import com.aspose.slides.IPlaceholder;
import com.aspose.slides.IShape;
import com.aspose.slides.IShapeCollection;
import com.aspose.slides.ISlide;
import com.aspose.slides.ITable;
import com.aspose.slides.PictureFillMode;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.coderdream.entity.VocInfo;
import com.coderdream.entity.WordInfo;
import com.coderdream.entity.WordInfoEntity;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.excel.CdExcelUtil;
import com.coderdream.util.translate.TranslatorTextUtil;
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
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;

@Slf4j // 使用 Lombok 的 @Slf4j 注解，自动生成 log 对象，用于日志记录
public class GetSixMinutesPpt {


  public static void main(String[] args) {
    String folderName = "181206"; // 定义文件夹名称
    GetSixMinutesPpt.process(folderName); // 调用 process 方法处理 PPT
//    GetSixMinutesPpt.genPic(folderName); // 调用 genPic 方法生成图片，已注释掉
  }

  /**
   * 生成PPT图片
   *
   * @param folderName 文件夹名称
   */
  public static void genPic(String folderName) {

    String pptFilePath = CommonUtil.getFullPathFileName(
      folderName, folderName, ".pptx"); // 获取 PPT 文件的完整路径
    String outputDir =
      CommonUtil.getFullPath(folderName) + folderName
        + File.separator; // 定义输出目录
    if (!new File(outputDir).exists()) { // 检查输出目录是否存在
      boolean mkdir = new File(outputDir).mkdirs(); // 如果不存在，则创建目录
      if (mkdir) {
        log.info("创建目录成功:{}", outputDir); // 使用 log.info 记录创建目录成功的日志
      }
    }

    PptToPng.savePptToPng(pptFilePath, outputDir, 1920,
      1080); // 调用 PptToPng 工具类将 PPT 转换为 PNG 图片，并保存到输出目录，指定分辨率为 1920x1080
  }

  /**
   * 处理PPT的主要方法
   *
   * @param folderName 文件夹名称
   */
  public static void process(String folderName, String chapterName) {
    LicenseUtil.loadLicense(
      MicrosoftConstants.PPTX_TO_OTHER); // 加载 Aspose.Slides 的 License，用于去除水印等限制
    Presentation pres = null; // 定义 Presentation 对象，用于表示 PPT 文档
    try {
      // 填充高级词汇
      List<WordInfoEntity> wordInfoEntityList = CdExcelUtil.getAdvancedWordList(
        folderName);// generateWordInfoList(); // 从 Excel 文件中读取高级词汇信息
      List<WordInfo> wordInfoList = new ArrayList<>(); // 创建 WordInfo 列表，用于存储转换后的词汇信息
      WordInfo wordInfo; // 定义 WordInfo 对象
      int no = 0; // 定义序号，用于记录词汇的顺序
      for (WordInfoEntity wordInfoEntity : wordInfoEntityList) { // 遍历从 Excel 中读取的词汇信息
        wordInfo = new WordInfo(); // 创建 WordInfo 对象
        BeanUtils.copyProperties(wordInfoEntity,
          wordInfo); // 使用 BeanUtils 将 WordInfoEntity 的属性复制到 WordInfo
        ++no; // 序号自增
        wordInfo.setNo(no); // 设置序号
        wordInfo.setComment(wordInfoEntity.getComment()); // 设置注释
        wordInfoList.add(wordInfo); // 将 WordInfo 对象添加到列表中
      }

      // 将列表分割成10个对象为一组，每组包含10个对象
      List<List<WordInfo>> wordInfoListGroups = new ArrayList<>(); // 创建列表的列表，用于存储分割后的词汇信息
      for (int j = 0; j < wordInfoList.size();
        j += 10) { // 遍历词汇列表，每 10 个词汇分割成一组
        wordInfoListGroups.add(
          wordInfoList.subList(j,
            Math.min(j + 10, wordInfoList.size()))); // 将分割后的词汇信息添加到列表中
      }
      // 判断是否为空
      if (CollectionUtil.isEmpty(wordInfoListGroups)) { // 检查分割后的词汇信息列表是否为空
        log.info("wordInfoListGroups is empty"); // 如果为空，则记录日志
        return; // 结束方法
      }

      // 6min_page6.pptx 根据词汇多少确定模板文件
      String presentationName = CdConstants.PPT_TEMPLATE_FILE_PATH + "6min_page"
        + wordInfoListGroups.size() + ".pptx"; // 根据词汇组的数量确定使用的 PPT 模板文件
      // 实例化Presentation类
      pres = new Presentation(
        presentationName); // 实例化 Presentation 类，加载 PPT 模板文件
      Map<String, String> props = new HashMap<>(); // 末日滚动：我们为什么喜欢末日滚动？
      props.put("folderName", folderName);
      props.put("title",
        chapterName); // getArticleTitle // 从文件中读取文章标题，并添加到属性映射中
      List<VocInfo> vocInfoList = DictUtils.getVocInfoList(
        folderName); // 从文件中读取词汇信息
      int i = 1; // 定义序号
      String sampleSentenceEn; // 定义英文例句
      String sampleSentenceCn; // 定义中文例句
      for (VocInfo vocInfo : vocInfoList) { // 遍历词汇信息列表
        //            System.out.println(vocInfo);
        props.put("word" + i, vocInfo.getWord()); // 将词汇添加到属性映射中
        props.put("wordEn" + i,
          vocInfo.getWordExplainEn()); // 将英文解释添加到属性映射中
        props.put("wordCn" + i, vocInfo.getWordCn()); // 将词汇的中文翻译添加到属性映射中
        props.put("wordCnEx" + i, vocInfo.getWordExplainCn()); // 将中文解释添加到属性映射中
        sampleSentenceEn = vocInfo.getSampleSentenceEn(); // 获取英文例句
        sampleSentenceCn = vocInfo.getSampleSentenceCn(); // 获取中文例句
        String mid = ""; // 定义分隔符
//        log.info(          sampleSentenceEn + " : "            + sampleSentenceEn.length()); // 使用 log.info 记录英文例句和长度
        if (StrUtil.isNotEmpty(sampleSentenceEn)
          && sampleSentenceEn.length() < 50) { // 如果英文例句不为空，且长度小于 50
          mid = "\n"; // 则使用换行符作为分隔符
        }
        // TODO
        props.put("wordSen" + i,
          sampleSentenceEn + mid + sampleSentenceCn); // 将例句添加到属性映射中
        props.put("wordSenEn" + i, sampleSentenceEn); // 将英文例句添加到属性映射中
        props.put("wordSenCn" + i, sampleSentenceCn); // 将中文例句添加到属性映射中
        String midStr = " "; // 定义分隔符
        if (sampleSentenceEn.length() < 50
          && sampleSentenceCn.length() < 50) { // 如果英文和中文例句长度都小于 50
          midStr = "\n"; // 则使用换行符作为分隔符
        }
        props.put("wordSenEn" + i + "MixwordSenCn" + i,
          sampleSentenceEn + midStr + sampleSentenceCn); // 将混合例句添加到属性映射中
        i++; // 序号自增
      }
      int sizePpt = pres.getSlides().size(); // 获取幻灯片的数量
      for (int j = 0; j < sizePpt; j++) { // 遍历每一张幻灯片
//        log.info(          "##############" + pres.getSlides().get_Item(j).getShapes()            .size()); // 使用 log.info 记录幻灯片中形状的数量
        ISlide sld = pres.getSlides().get_Item(j); // 获取当前幻灯片
//        log.info("##$$## " + 1); // 使用 log.info 记录信息

        List<IShape> shapesToRemove = new ArrayList<IShape>(); // 用于存储需要删除的形状的列表
        List<IShape> shapesCopy = new ArrayList<IShape>(); // 用于存储幻灯片形状的列表，复制一份，避免在遍历时修改集合
        for (IShape shape : sld.getShapes()) { // 遍历幻灯片中的每一个形状
          shapesCopy.add(shape); // 将形状添加到 shapesCopy 列表中
        }
        // 遍历形状以查找占位符
        for (IShape shp : shapesCopy) { // 遍历幻灯片中的每一个形状
          if (shp.getPlaceholder() != null) { // 判断形状是否是占位符
            IPlaceholder placeholder = shp.getPlaceholder(); // 获取占位符

            int type = placeholder.getType(); // 获取占位符的类型

            switch (type) { // 根据占位符类型进行不同的处理
              case 1: // 文本占位符
//                log.info(                  "## \t " + shp.getPlaceholder().getType()                    + "这是占位符"); // 使用 log.info 记录占位符类型
//                log.info(((IAutoShape) shp).getTextFrame()                  .getText()); // 使用 log.info 记录占位符中的文本

                String text = ((IAutoShape) shp).getTextFrame()
                  .getText(); // 获取占位符中的文本
                // 去掉{{}}
                text = text.replaceAll("\\{", ""); // 去掉文本中的 {{ 符号
                text = text.replaceAll("\\}", ""); // 去掉文本中的 }} 符号
                String newText = props.get(text); // 从属性映射中获取替换文本
//                log.info("## \t {} \t {}", text,                  newText); // 使用 log.info 记录占位符文本和替换文本
                if (StrUtil.isNotEmpty(newText)) { // 如果替换文本不为空
                  ((IAutoShape) shp).getTextFrame()
                    .setText(newText); // 将占位符中的文本替换为新的文本
                }

                break;
              case 15: // 图片占位符
//                log.info(                  "## \t " + shp.getPlaceholder().getType()                    + "这是图片"); // 使用 log.info 记录占位符类型

                // 加载替换图片，将 BufferedImage 转为 InputStream
                BufferedImage bufferedImage = null; // 定义 BufferedImage 对象
                try {
                  String picFilePath = CommonUtil.getFullPathFileName(
                    folderName, folderName, ".jpg"); // 获取图片文件的完整路径
                  bufferedImage = ImageIO.read(new File(
                    picFilePath)); // 读取图片文件
                } catch (IOException e) { // 捕获 IO 异常
                  throw new RuntimeException(e); // 抛出运行时异常
                }
                if (bufferedImage != null) { // 如果图片读取成功
                  try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream(); // 创建 ByteArrayOutputStream 对象
                    ImageIO.write(bufferedImage, "png",
                      os); // 将 BufferedImage 写入 ByteArrayOutputStream
                    try (InputStream is = new ByteArrayInputStream(
                      os.toByteArray())) { // 使用 try-with-resources 创建 InputStream
                      // 使用推荐方法 addImage(InputStream)
                      IPPImage pptxImage = pres.getImages()
                        .addImage(is); // 将 InputStream 中的图片添加到 PPT 中

                      // 5. 替换图片并保持样式
                      float x = shp.getX(); // 获取占位符的 X 坐标
                      float y = shp.getY(); // 获取占位符的 Y 坐标
                      float width = shp.getWidth(); // 获取占位符的宽度
                      float height = shp.getHeight(); // 获取占位符的高度

                      // 创建圆角矩形形状
                      IAutoShape shape = sld.getShapes()
                        .addAutoShape(ShapeType.RoundCornerRectangle, x, y,
                          width, height); // 在指定位置创建一个圆角矩形
                      shape.getEffectFormat()
                        .enableSoftEdgeEffect(); // 启用柔化边缘效果
                      shape.getEffectFormat().getSoftEdgeEffect()
                        .setRadius(15); // 设置柔化边缘的半径

                      // 设置图片填充
                      shape.getFillFormat().setFillType(
                        FillType.Picture); // 设置填充类型为图片
                      shape.getFillFormat().getPictureFillFormat().getPicture()
                        .setImage(pptxImage); // 将图片设置为填充图片

                      // 设置图片填充
                      shape.getFillFormat()
                        .setFillType(FillType.Picture); // 设置填充类型为图片
                      IPictureFillFormat pictureFillFormat = shape.getFillFormat()
                        .getPictureFillFormat(); // 获取图片填充格式
                      pictureFillFormat.getPicture()
                        .setImage(pptxImage); // 设置填充图片

                      // 设置填充模式为拉伸
                      pictureFillFormat.setPictureFillMode(
                        PictureFillMode.Stretch); // 设置填充模式为拉伸

                      // 删除原来的占位符形状
                      shapesToRemove.add(shp); // 将原来的占位符添加到待删除列表中
                    }
                  } catch (Exception e) { // 捕获异常
                    log.error("替换图片出现异常", e); //使用log.error记录异常日志
                  }
                }
                break;
              default:
                log.info("## \t " + "DEFAULT"); // 使用 log.info 记录默认情况
            }
          }
        }
        //  统一删除需要删除的形状
        for (IShape shapeToRemove : shapesToRemove) { // 遍历待删除列表
          sld.getShapes().remove(shapeToRemove); // 删除需要删除的形状
        }

      }
      int count = 0; // 定义计数器
      int tableIndex = 10; // 定义表格的起始索引
      // 遍历每一页幻灯片
      if (!wordInfoListGroups.isEmpty()) { // 如果词汇分组列表不为空
        int groupSize = wordInfoListGroups.size(); // 获取分组的数量
        switch (groupSize) { // 根据分组的数量进行不同的处理
          case 1: // 1 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            break;
          case 2: // 2 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 1),
              wordInfoListGroups.get(1)); // 填充表格
            break;
          case 3: // 3 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 1),
              wordInfoListGroups.get(1)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 2),
              wordInfoListGroups.get(2)); // 填充表格
            break;

          case 4: // 4 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 1),
              wordInfoListGroups.get(1)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 2),
              wordInfoListGroups.get(2)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 3),
              wordInfoListGroups.get(3)); // 填充表格
            break;
          case 5: // 5 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 1),
              wordInfoListGroups.get(1)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 2),
              wordInfoListGroups.get(2)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 3),
              wordInfoListGroups.get(3)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 4),
              wordInfoListGroups.get(4)); // 填充表格
            break;

          case 6: // 6 组词汇
            fillTable(pres.getSlides().get_Item(tableIndex),
              wordInfoListGroups.get(0)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 1),
              wordInfoListGroups.get(1)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 2),
              wordInfoListGroups.get(2)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 3),
              wordInfoListGroups.get(3)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 4),
              wordInfoListGroups.get(4)); // 填充表格
            fillTable(pres.getSlides().get_Item(tableIndex + 5),
              wordInfoListGroups.get(5)); // 填充表格
            break;

//          case 3:
//            break;
          default: // 其他情况，可以添加异常处理或者默认行为
            log.warn("WordInfoListGroups size is {}, which is not supported.",
              groupSize);
            break;

        }
        // 获取当前幻灯片
//        ISlide sld = pres.getSlides().get_Item(8);
      }

      // 将演示文稿保存到磁盘
      pres.save(
        CommonUtil.getFullPathFileName(
          folderName, folderName, ".pptx"),
        SaveFormat.Pptx); // 将修改后的 PPT 保存到文件中
      log.info("PPT生成成功:{}", CommonUtil.getFullPathFileName(
        folderName, folderName, ".pptx"));//使用log.info记录PPT生成成功日志
    } catch (Exception e) {
      log.error("PPT生成过程中出现异常", e); //使用log.error记录PPT生成异常日志
    } finally {
      if (pres != null) { // 如果 Presentation 对象不为空
        pres.dispose(); // 释放资源
      }
    } // D:\04_GitHub\video-easy-creator\target\classes\bbc
  }

  /**
   * 处理PPT的主要方法
   *
   * @param folderName 文件夹名称
   */
  public static void process(String folderName) {
    // 查询章节名称
    String chapterName = GetSixMinutesPpt.queryChapterNameForSixMinutes(folderName);

    process(folderName, chapterName); // 调用 process 方法，传入文件夹名称和标题
  }

  public static @Nullable String queryChapterNameForSixMinutes(
    String folderName) {
    String title = CdFileUtil.getArticleTitle(folderName); // 将文件夹名称添加到属性映射中
    if (StrUtil.isEmpty(title)) {
      String fileName = folderName + "_script";
      String fileNameWithPath = CommonUtil.getFullPathFileName(folderName,
        fileName,
        ".txt");
      List<String> stringList = CdFileUtil.readFileContent(fileNameWithPath);
      StringBuilder titleEn = new StringBuilder();
      if (CollectionUtil.isNotEmpty(stringList)) {
        for (String string : stringList) {
          if (!string.startsWith("BBC LEARNING ENGLISH")
            && !string.startsWith("6 Minute English") && !string.startsWith(
            "This is not")) {
            titleEn.append(string);
          }
          if (string.startsWith("This is not")) {
            List<String> stringListTrans = TranslatorTextUtil.translatorText(
              titleEn.toString());
            if (CollectionUtil.isNotEmpty(stringListTrans)) {
              title = stringListTrans.get(0);
            }
            break;
          }
        }
      }
    }
    return title;
  }

  /**
   * 填充表格
   *
   * @param sld           幻灯片对象
   * @param wordInfoList1 词汇信息列表
   */
  private static void fillTable(ISlide sld, List<WordInfo> wordInfoList1) {
    ITable tbl; // 定义 ITable 对象
    IShapeCollection shapes = sld.getShapes();
//    System.out.println(      "## 幻灯片中的形状数量: " + shapes.toString()); // 输出幻灯片中的形状数量

    for (IShape shp : sld.getShapes()) { // 遍历幻灯片中的每一个形状
//      log.info("## \t {} ", shp); // 使用 log.info 记录当前形状
      if (shp instanceof ITable) { // 判断形状是否是表格
        tbl = (ITable) shp; // 将形状转换为表格
        // Sets the text for the first column of the second row
        int y = 0; // 定义行号
        for (WordInfo wordInfo2 : wordInfoList1) { // 遍历词汇信息列表
          y++; // 行号自增
          tbl.get_Item(0, y).getTextFrame()
            .setText(wordInfo2.getNo() + ""); // 设置序号
          tbl.get_Item(1, y).getTextFrame()
            .setText(wordInfo2.getWord()); // 设置单词 psychologist
          if(wordInfo2.getWord().equals("psychologist")) {
            System.out.println("###### wordInfo2.getUk() is null");
          }

          String uk = wordInfo2.getUk();
          if (StrUtil.isNotBlank(uk) && uk.length() >= 15) {
            uk = uk.substring(0, 13) ;// + "…"
          }
          tbl.get_Item(2, y).getTextFrame().setText(uk); // 设置音标
          String comment = wordInfo2.getComment();
          if (StrUtil.isNotBlank(comment) && comment.length() > 49) {
            comment = comment.substring(0, 48) ;// + "…"
          }

          tbl.get_Item(3, y).getTextFrame()
            .setText(comment); // 设置注释
          tbl.get_Item(4, y).getTextFrame()
            .setText(wordInfo2.getLevelStr()); // 设置等级
          tbl.get_Item(5, y).getTextFrame()
            .setText(wordInfo2.getTimes() + ""); // 设置次数
        }
      }
    }
  }
}
