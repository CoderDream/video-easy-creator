package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.YoutubeInfoEntity;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownToGitHub02 {


  private static final Pattern IMAGE_NAME_PATTERN = Pattern.compile(
    "幻灯片(\\d+)\\.(png|jpg|jpeg|gif)", Pattern.CASE_INSENSITIVE);

  /**
   * 生成 Markdown 文件
   *
   * @throws IOException 当发生 I/O 异常时抛出
   */
  public static void generateMarkdownFile(String category, String dateString)
    throws IOException {
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + category
        + File.separator + dateString;

    // 1. 构建文件名和文件夹路径
    String[] categoryArray = category.split("_");
    String folderName = "";
    if (categoryArray.length == 2) {
      folderName =
        CdStringUtil.convertToKebabCase(categoryArray[1]) + "-" + dateString;
    }
    Path targetFolder = Paths.get(CdConstants.HALF_HOUR_ENGLISH_POSTS_FOLDER,
      folderName);
    Path markdownFile = Paths.get(CdConstants.HALF_HOUR_ENGLISH_POSTS_FOLDER,
      folderName + ".md");
    log.info("目标文件夹：{}", targetFolder);
    log.info("目标markdown文件：{}", markdownFile);
    // 2. 创建目标文件夹
    createDirectory(targetFolder);
    // 3. 创建markdown文件
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(markdownFile.toFile()))) {

      List<YoutubeInfoEntity> youtubeVideoInfoEntityList = CdFileUtil.getTodoYoutubeVideoInfoEntityList();
      Map<String, YoutubeInfoEntity> map = new LinkedHashMap<>();
      for (YoutubeInfoEntity youtubeVideoInfoEntity : youtubeVideoInfoEntityList) {
        map.put(youtubeVideoInfoEntity.getCategory()
            + youtubeVideoInfoEntity.getDateString(),
          youtubeVideoInfoEntity);
      }
      YoutubeInfoEntity youtubeVideoInfoEntity = map.get(
        category + dateString);
      if (Objects.isNull(youtubeVideoInfoEntity)) {
        log.error("未找到对应的视频信息, category:{}, dateString:{}", category,
          dateString);
        return;
      }

      // 4. 写入简介
      writeIntroduction(writer, category, dateString,
        youtubeVideoInfoEntity.getTitle());

      // 5. 处理图片
//            List<Path> imageFiles = processImages(imageFolder, targetFolder,
//                    folderName);

      // 6. 插入第一张图片作为封面 20250321_cover
      String imageFileName =
        folderPath + File.separator + dateString + "_cover.png";
      insertImageAsCover(writer, targetFolder, imageFileName, folderName);

      // {% video youtube:LB8KwiiUGy0 width:100% autoplay:0 %}
      // 插入视频
      String youtubeVideoId = youtubeVideoInfoEntity.getMyVideoId();// "XSSeizMti1w";
      insertYoutubeVideo(writer, youtubeVideoId);

      //
//            // 9. 插如音频
      String audioFileName =
        folderPath + File.separator + dateString + ".mp3";
      insertLocalAudio(writer, targetFolder, audioFileName, imageFileName,
        youtubeVideoInfoEntity.getTitle());

//
//            // 7. 插入英文文本
//            insertTextFromFile(writer, "英文脚本", englishTextFile);
//
      String bilingualTextFile =
        folderPath + File.separator + dateString + "_script_pure_gemini.txt";
      // 8. 插入中英文双语文本
      insertTextFromFile(writer, "中英文双语脚本", bilingualTextFile);
//
//            // 9. 插入所有的图片
//            insertAllImages(writer, imageFiles, folderName);
//
//            // 10. 插入词汇文本
//            insertVocabularyWithH4(writer, vocabularyFile);
//
//            // 11. 插入结尾字符串
//            writeEndString(writer, endString);

    } catch (IOException e) {
      log.error("生成markdown文件失败", e);
      throw e; // 将异常抛出
    }
    log.info("markdown文件生成成功");
  }


  /**
   * 创建目录
   *
   * @param directoryPath 目录路径
   * @throws IOException 当创建目录失败时抛出
   */
  private static void createDirectory(Path directoryPath) throws IOException {
    if (Files.notExists(directoryPath)) {
      Files.createDirectories(directoryPath);
      log.info("成功创建目录：{}", directoryPath);
    } else {
      log.warn("目录已存在，无需创建：{}", directoryPath);
    }
  }


  /**
   * 写入简介
   *
   * @param writer     BufferedWriter
   * @param category   PressBriefings
   * @param dateString 20250228
   * @throws IOException 当写入简介失败时抛出
   */
  private static void writeIntroduction(BufferedWriter writer,
    String category, String dateString, String title) throws IOException {
//    writer.write(introduction);
//    writer.newLine();
//    writer.newLine(); // 添加两个空行

    //写入描述信息
    writer.write("---");
    writer.newLine();
    writer.write("title: " + title);
    writer.newLine();
    Date startDate = DateUtil.parse(dateString, "yyyyMMdd");
    writer.write(
      "date: " + DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss"));
    writer.newLine();
    writer.write("tags: ");
//        writer.write("\t" + String.join(", ", category));
    writer.write("\t" + category);
    writer.newLine();
    writer.write("categories: ");
    writer.write("\t" + category);
    writer.newLine();
    writer.write("---");
    writer.newLine();
    log.info("成功写入简介");
  }

  /**
   * 处理图片，返回排序后的图片列表
   *
   * @param imageFolder  图片文件夹路径
   * @param targetFolder 目标文件夹路径
   * @param folderName   文件夹名称
   * @return 排序后的图片列表
   * @throws IOException 当处理图片时抛出异常
   */
  private static List<Path> processImages(String imageFolder, Path targetFolder,
    String folderName)
    throws IOException {
    if (Objects.nonNull(imageFolder) && Files.exists(Paths.get(imageFolder))) {
      List<Path> imageFiles =
        Files.list(Paths.get(imageFolder))
          .filter(Files::isRegularFile)
          .filter(path -> {
            String fileName = path.getFileName().toString().toLowerCase();
            return fileName.endsWith(".png")
              || fileName.endsWith(".jpg")
              || fileName.endsWith(".jpeg")
              || fileName.endsWith(".gif");
          })
          .sorted((path1, path2) -> { // 添加图片排序逻辑
            String fileName1 = path1.getFileName().toString();
            String fileName2 = path2.getFileName().toString();
            int number1 = extractNumberFromImageName(fileName1);
            int number2 = extractNumberFromImageName(fileName2);
            return Integer.compare(number1, number2);
          })
          .collect(Collectors.toList());

      log.info("图片文件数量：{}", imageFiles.size());
      for (Path imageFile : imageFiles) {
        String imageName = imageFile.getFileName().toString();
        Path targetImage = targetFolder.resolve(imageName);
        Files.copy(imageFile, targetImage, StandardCopyOption.REPLACE_EXISTING);
        log.info("图片 {} 已复制到 {} ", imageFile.toString(),
          targetImage.toString());
      }
      return imageFiles;
    } else {
      log.warn("图片文件夹不存在或为空：{}", imageFolder);
      return null;
    }
  }


  /**
   * 插入第一张图片作为封面
   *
   * @param writer     BufferedWriter
   * @param imageFiles 图片文件列表
   * @param folderName 文件夹名称
   * @throws IOException 当写入封面图片时抛出异常
   */
  private static void insertFirstImageAsCover(BufferedWriter writer,
    List<Path> imageFiles, String folderName) throws IOException {
    if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
      String imageName = imageFiles.get(0).getFileName().toString();
      String markdownImage = String.format("![%s](%s/%s)\n", imageName,
        folderName, imageName);
      writer.write(markdownImage);
      writer.newLine();
      log.info("成功插入第一张图片作为封面：{}", imageName);
    } else {
      log.warn("没有图片可作为封面");
    }
  }

  /**
   * 插入第一张图片作为封面
   *
   * @param writer        BufferedWriter
   * @param imageFileName 图片文件
   * @param folderName    文件夹名称
   * @throws IOException 当写入封面图片时抛出异常
   */
  private static void insertImageAsCover(BufferedWriter writer,
    Path targetFolder,
    String imageFileName, String folderName) throws IOException {
    if (!CdFileUtil.isFileEmpty(imageFileName)) {

      Path targetImage = Path.of(targetFolder + File.separator +
        new File(imageFileName).getName());
      // targetFolder.resolve(imageFileName);
      Files.copy(Path.of(imageFileName), targetImage,
        StandardCopyOption.REPLACE_EXISTING);
      log.info("封面图片 {} 已复制到 {} ", imageFileName, targetImage);
// ![幻灯片1.PNG](wechat-2018-11-29/幻灯片1.PNG)
      String pureFileName = new File(imageFileName).getName();
      String markdownImage = String.format("![%s](%s/%s)", pureFileName,
        folderName, pureFileName);
      log.info("图片 {}  已添加到 Markdown 文件中，字符串为：{}", imageFileName,
        markdownImage);
      writer.write(markdownImage);
      writer.newLine();
      log.info("成功插入图片作为封面：{}", imageFileName);
    } else {
      log.warn("没有图片可作为封面：{}", imageFileName);
    }
  }

  /**
   * 插入油管视频
   *
   * @param writer  BufferedWriter
   * @param videoId 视频ID
   * @throws IOException 当写入封面图片时抛出异常
   */
  private static void insertYoutubeVideo(BufferedWriter writer,
    String videoId) throws IOException {
    if (StrUtil.isNotBlank(videoId)) {
      // {% video youtube:LB8KwiiUGy0 width:100% autoplay:0 %}
      String videoString =
        "{% video youtube:" + videoId + " width:100% autoplay:0 %}";
      writer.write(videoString);
      writer.newLine();
      log.info("成功插入视频：{}", videoId);
    } else {
      log.warn("没有视频ID：{}", videoId);
    }
  }

  /**
   * 插入音频
   * <pre>
   *   {% audio https://github.com/volantis-x/volantis-docs/releases/download/assets/Lumia1020.mp3 %}
   * </pre>
   *
   * @param writer       BufferedWriter
   * @param targetFolder 视频ID
   * @throws IOException 当写入封面图片时抛出异常
   */
  private static void insertLocalAudio(BufferedWriter writer, Path targetFolder,
    String audioFileName, String imageFileName, String title)
    throws IOException {
    if (!CdFileUtil.isFileEmpty(audioFileName)) {

      Path targetAudio = Path.of(targetFolder + File.separator +
        new File(audioFileName).getName());
      // targetFolder.resolve(imageFileName);
      Files.copy(Path.of(audioFileName), targetAudio,
        StandardCopyOption.REPLACE_EXISTING);
      log.info("音频文件 {} 已复制到 {} ", audioFileName, targetAudio);
      String pureFileName = new File(audioFileName).getName();
//      String markdownAudio = "{% audio " + pureFileName + " %}";
//
//      writer.write(markdownAudio);
//      writer.newLine();

      // {% aplayer "b1-repeat-english-01" "B1_Repeat_Listening" "b1-repeat-english-01.mp3" "https://img4.kuwo.cn/star/albumcover/300/50/69/4184500136.jpg" %}
      String imageFileNameStr = new File(imageFileName).getName();

      String markdownAudio =
        "{% aplayer '" + title + "' '" + title + "' '"
          + pureFileName + "' '" + imageFileNameStr + "' %}";

      writer.write(markdownAudio);
      writer.newLine();
      log.info(
        "音频文件 {}  已添加到 Markdown 文件中，字符串为：{}，封面图片为：{}",
        audioFileName, markdownAudio, imageFileNameStr);
      log.info("成功插入音频文件：{}", audioFileName);
    } else {
      log.warn("没有音频文件可插入：{}", audioFileName);
    }
  }


  /**
   * 插入所有图片到 Markdown 文件
   *
   * @param writer     BufferedWriter
   * @param imageFiles 图片文件列表
   * @param folderName 文件夹名称
   * @throws IOException 当写入图片时抛出异常
   */
  private static void insertAllImages(BufferedWriter writer,
    List<Path> imageFiles, String folderName) throws IOException {
    if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
      for (Path imageFile : imageFiles) {
        String imageName = imageFile.getFileName().toString();
        String markdownImage = String.format("![%s](%s/%s)\n", imageName,
          folderName, imageName);
        writer.write(markdownImage);
        log.info("图片 {}  已添加到 Markdown 文件中", imageName);
      }
      writer.newLine();
    }
  }

  /**
   * 从图片名称中提取数字
   *
   * @param imageName 图片名称
   * @return 图片名称中的数字，如果提取失败返回 -1
   */
  private static int extractNumberFromImageName(String imageName) {
    Matcher matcher = IMAGE_NAME_PATTERN.matcher(imageName);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("无法解析图片名称中的数字: {}", imageName, e);
      }
    }
    return -1;
  }


  /**
   * 插入文本文件内容
   *
   * @param writer   BufferedWriter
   * @param textFile 文本文件名
   * @throws IOException 当读取文本文件或写入失败时抛出
   */
  private static void insertTextFromFile(BufferedWriter writer, String tag,
    String textFile)
    throws IOException {
    if (Objects.nonNull(textFile) && Files.exists(Paths.get(textFile))) {
      List<String> lines = Files.readAllLines(Paths.get(textFile));
      writer.write("### 【" + tag + "】");
      writer.newLine();
      for (String line : lines) {
        writer.write(ZhConverterUtil.toTraditional(line));
        writer.newLine();
      }
      writer.newLine(); // 添加一个空行
      log.info("成功插入文本内容：{}", textFile);
    } else {
      log.warn("文本文件不存在或为空：{}", textFile);
    }
  }


  /**
   * 插入词汇文本，并为每组的第一个单词添加 H4 标题
   *
   * @param writer         BufferedWriter
   * @param vocabularyFile 词汇文本文件名
   * @throws IOException 当读取词汇文本或写入失败时抛出
   */
  private static void insertVocabularyWithH4(BufferedWriter writer,
    String vocabularyFile)
    throws IOException {
    if (Objects.nonNull(vocabularyFile) && Files.exists(
      Paths.get(vocabularyFile))) {
      List<String> lines = Files.readAllLines(Paths.get(vocabularyFile));
      // 每六行一组，第一行添加 H4 标题
      writer.write("### 【核心词汇】");
      writer.newLine();
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (i % 6 == 0) { // 每六行一组，第一行添加 H4 标题
          writer.write("#### " + line);
          writer.newLine();
        } else {
          writer.write(line);
          writer.newLine();
        }
      }
      writer.newLine();
      log.info("成功插入词汇文本，并添加H4标题");
    } else {
      log.warn("词汇文件不存在或为空：{}", vocabularyFile);
    }
  }

  /**
   * 写入结尾字符串
   *
   * @param writer    BufferedWriter
   * @param endString 结尾字符串
   * @throws IOException 当写入结尾字符串失败时抛出
   */
  private static void writeEndString(BufferedWriter writer, String endString)
    throws IOException {
    writer.write(endString);
    log.info("成功写入结尾字符串");
  }

  public static void genGitHubArticle(String category, String dateString) {
//    String folderPath =
//      OperatingSystem.getBaseFolder() + File.separator + category;

    try {
      generateMarkdownFile(category, dateString);
      log.info("Markdown 文件生成完毕！");
    } catch (IOException e) {
      log.error("生成 Markdown 文件时出现异常：", e);
    }
  }

  public static void main(String[] args) {

//    // D:\0000\0003_PressBriefings\20250128
//    String folderName = "123456";
//    String title = "【BBC六分钟英语】哪些人会购买高端相机？";
////        String category = "0003_PressBriefings";
////        String dateString = "20250128";
//
//    String category = "0008_DailyNews";
//    String dateString = "20250321";
//    MarkdownToGitHub.genGitHubArticle(category, dateString);

//    introduction = "【BBC六分钟英语】网络流行语雪花是什么意思？？";
//    String folderName = "180920";
////    folderName = "241226";
//    String folderPath = CommonUtil.getFullPath(folderName);
//    String imageFolder = folderPath  + File.separator + folderName;
//    String englishTextFile = folderPath  + File.separator + "script_dialog.txt";
//    String bilingualTextFile =
//      folderPath  + File.separator + folderName + "_中英双语对话脚本.txt";
//    String vocabularyFile = folderPath  + File.separator + "voc_cn.txt";
//    String endString = "在公众号里输入6位数字，获取【对话音频、英文文本、中文翻译、核心词汇和高级词汇表】电子档，6位数字【暗号】在文章的最后一张图片，如【220728】，表示22年7月28日这一期。公众号没有的文章说明还没有制作相关资料。年度合集在B站【六分钟英语】工房获取，每年共计300+文档，感谢支持！";
//
//    try {
//      generateMarkdownFile(folderName, date, introduction, imageFolder,
//        englishTextFile,
//        bilingualTextFile, vocabularyFile, endString);
//      log.info("Markdown 文件生成完毕！");
//    } catch (IOException e) {
//      log.error("生成 Markdown 文件时出现异常：", e);
//    }
  }
}
