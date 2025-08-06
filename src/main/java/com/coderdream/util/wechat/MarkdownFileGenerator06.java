package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
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
public class MarkdownFileGenerator06 {


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
        Path targetFolder = Paths.get(CdConstants.HALF_HOUR_ENGLISH_POSTS_FOLDER, folderName);
        Path markdownFile = Paths.get(CdConstants.HALF_HOUR_ENGLISH_POSTS_FOLDER, folderName + ".md");
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

            // 6. 插入第一张图片作为封面
            // 6. 插入第一张图片作为封面 20250321_cover
            String imageFileName =
              folderPath + File.separator + dateString + "_cover.png";
            insertImageAsCover(writer, targetFolder, imageFileName, folderName);

            // 7. 插入英文文本
//            insertTextFromFile(writer, "英文脚本", englishTextFile);

            // 8. 插入中英文双语文本 TODO
//            insertTextFromFile(writer, "中英文双语脚本", bilingualTextFile);
            // {% video youtube:LB8KwiiUGy0 width:100% autoplay:0 %}
            // 插入视频
//            String youtubeVideoId = youtubeVideoInfoEntity.getVideoId();
//            insertYoutubeVideo(writer, youtubeVideoId);
//
//            // 9. 插入音频
            String audioFileName =
              folderPath + File.separator + dateString + ".mp3";
            insertLocalAudio(writer, targetFolder, audioFileName, imageFileName,
              youtubeVideoInfoEntity.getTitle());

            // 7. 插入英文文本（如果需要）
            // insertTextFromFile(writer, "英文脚本", englishTextFile);

            String bilingualTextFile =
              folderPath + File.separator + dateString + "_script_pure_gemini.txt";
            // 8. 插入中英文双语文本
            insertTextFromFile(writer, "中英文双语脚本", bilingualTextFile);

            // ------------------- 新增：插入Excel词汇表 -------------------
            String excelFileName = dateString + "_完整词汇表.xlsx";
            String excelFilePath = folderPath + File.separator + excelFileName;
            // 指定要读取的 Sheet 名称
            String[] vocabularySheetNames = {"B1_C2", "A2", "A1", "Others"};
            // 调用新方法插入表格
            insertExcelAsMarkdownTable(writer, excelFilePath, vocabularySheetNames,
              "核心词汇");
            // ----------------------------------------------------------

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
     * 插入指定图片作为封面
     *
     * @param writer        BufferedWriter
     * @param imageFileName 图片文件路径
     * @param folderName    Markdown 中引用的文件夹名称
     * @throws IOException 当写入封面图片时抛出异常
     */
    private static void insertImageAsCover(BufferedWriter writer,
      Path targetFolder,
      String imageFileName, String folderName) throws IOException {
        Path sourceImagePath = Paths.get(imageFileName);
        if (Files.exists(sourceImagePath) && Files.isRegularFile(sourceImagePath)) {
            String pureFileName = sourceImagePath.getFileName().toString();
            Path targetImage = targetFolder.resolve(pureFileName);

            // 复制图片到目标文件夹
            Files.copy(sourceImagePath, targetImage,
              StandardCopyOption.REPLACE_EXISTING);
            log.info("封面图片 {} 已复制到 {} ", imageFileName, targetImage);

            // 写入 Markdown 图片引用
            // 确保路径使用 / 分隔符
            String markdownPath = folderName.replace("\\", "/") + "/" + pureFileName;
            String markdownImage = String.format("![%s](%s)", pureFileName,
              markdownPath);
            log.info("图片 {} 已添加到 Markdown 文件中，字符串为：{}", imageFileName,
              markdownImage);
            writer.write(markdownImage);
            writer.newLine();
            log.info("成功插入图片作为封面：{}", imageFileName);
        } else {
            log.warn("没有有效的图片文件可作为封面：{}", imageFileName);
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
        writer.write("---");
        writer.newLine();
        writer.write("title: " + title);
        writer.newLine();
        Date startDate = DateUtil.parse(dateString, "yyyyMMdd");
        writer.write(
          "date: " + DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss"));
        writer.newLine();
        writer.write("tags: ");
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
     * 插入油管视频
     *
     * @param writer  BufferedWriter
     * @param videoId 视频ID
     * @throws IOException 当写入视频引用时抛出异常
     */
    private static void insertYoutubeVideo(BufferedWriter writer,
      String videoId) throws IOException {
        writer.newLine();
        if (StrUtil.isNotBlank(videoId)) {
            String videoString =
              "{% video youtube:" + videoId + " width:100% autoplay:0 %}";
            writer.write(videoString);
            writer.newLine();
            writer.newLine();
            log.info("成功插入视频：{}", videoId);
        } else {
            log.warn("没有视频ID：{}", videoId);
        }
    }

    /**
     * 插入本地音频文件引用
     *
     * @param writer        BufferedWriter
     * @param targetFolder  音频文件目标文件夹
     * @param audioFileName 音频文件源路径
     * @param imageFileName 封面图片文件名（仅文件名部分）
     * @param title         音频标题
     * @throws IOException 当写入音频引用或复制文件时抛出异常
     */
    private static void insertLocalAudio(BufferedWriter writer, Path targetFolder,
      String audioFileName, String imageFileName, String title)
      throws IOException {
        Path sourceAudioPath = Paths.get(audioFileName);
        writer.write("### 音频");
        writer.newLine();
        writer.newLine();
//        if (Files.exists(sourceAudioPath) && Files.isRegularFile(sourceAudioPath)) {
//            String pureAudioFileName = sourceAudioPath.getFileName().toString();
//            Path targetAudio = targetFolder.resolve(pureAudioFileName);
//
//            // 复制音频文件到目标文件夹
//            Files.copy(sourceAudioPath, targetAudio,
//              StandardCopyOption.REPLACE_EXISTING);
//            log.info("音频文件 {} 已复制到 {} ", audioFileName, targetAudio);
//
//            // 获取纯图片文件名
//            String pureImageFileName = Paths.get(imageFileName).getFileName()
//              .toString();
//
//            // 写入 Markdown 音频引用 (aplayer 格式)
//            String markdownAudio =
//              "{% aplayer \"" + title + "\" \"" + title + "\" \""
//                + pureAudioFileName + "\" \"" + pureImageFileName + "\" %}";
//
//            writer.write(markdownAudio);
//            writer.newLine();
//            writer.newLine();
//            log.info(
//              "音频文件 {} 已添加到 Markdown 文件中，字符串为：{}，封面图片为：{}",
//              audioFileName, markdownAudio, pureImageFileName);
//            log.info("成功插入音频文件：{}", audioFileName);
//        } else {
//            log.warn("没有有效的音频文件可插入：{}", audioFileName);
//        }
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
            writer.write("### " + tag);
            writer.newLine();
            int count = 0;
            for (String line : lines) {
                writer.write(ZhConverterUtil.toTraditional(line));
                writer.newLine();
                count++;
                if (count % 2 == 0) {
                    writer.newLine();
                }
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

//    public static void genYoutubeArticle(String folderName, String title) {
//        String date =
//                "20" + folderName.substring(0, 2) + "-" + folderName.substring(2, 4) + "-"
//                        + folderName.substring(4);
//
//        String folderPath = CommonUtil.getFullPath(folderName);
//        String imageFolder = folderPath + File.separator + folderName;
//        String englishTextFile = folderPath + File.separator + "script_dialog.txt";
//        String bilingualTextFile =
//                folderPath + File.separator + folderName + "_中英双语对话脚本.txt";
//        String vocabularyFile = folderPath + File.separator + "voc_cn.txt";
//        String endString = "在公众号里输入6位数字，获取【对话音频、英文文本、中文翻译、核心词汇和高级词汇表】电子档，6位数字【暗号】在文章的最后一张图片，如【220728】，表示22年7月28日这一期。公众号没有的文章说明还没有制作相关资料。年度合集在B站【六分钟英语】工房获取，每年共计300+文档，感谢支持！";
//
//        try {
//            generateMarkdownFile(folderName, date, title, imageFolder,
//                    englishTextFile,
//                    bilingualTextFile, vocabularyFile, endString);
//            log.info("Markdown 文件生成完毕！");
//        } catch (IOException e) {
//            log.error("生成 Markdown 文件时出现异常：", e);
//        }
//    }

    // ================== 新增方法 ==================

    /**
     * 从 Excel 文件读取指定 Sheet 的内容，并将其作为 Markdown 表格插入。 使用 Hutool 的 ExcelUtil 读取数据。
     *
     * @param writer           BufferedWriter 用于写入 Markdown 文件
     * @param excelFilePath    Excel 文件的完整路径
     * @param sheetNames       要读取的 Sheet 名称数组
     * @param tableTitlePrefix 表格标题的前缀（例如，“核心词汇”）
     * @throws IOException 写入文件时发生 I/O 错误
     */
    public static void insertExcelAsMarkdownTable(BufferedWriter writer,
      String excelFilePath,
      String[] sheetNames,
      String tableTitlePrefix) throws IOException {

        // 1. 检查 Excel 文件是否存在
        if (!Files.exists(Paths.get(excelFilePath))) {
            log.warn("Excel 文件不存在，无法插入表格: {}", excelFilePath);
            return;
        }

        log.info("开始处理 Excel 文件: {}", excelFilePath);

        // 2. 遍历指定的 Sheet 名称
        for (String sheetName : sheetNames) {
            ExcelReader reader = null;
            try {
                // 3. 获取 Excel 读取器
                reader = ExcelUtil.getReader(FileUtil.file(excelFilePath), sheetName);

                // 4. 读取 Sheet 的所有内容（包括表头）
                // readAll() 读取所有行，返回 List<List<Object>>
                // 如果第一行是标题，我们可以用它来构建表头，或者直接读取数据部分
                // 这里我们假设第一行是表头，从第二行开始是数据
                // 或者，更健壮的方式是读取所有行，手动处理第一行作为表头
                List<List<Object>> rows = reader.read();

                // 5. 检查是否有数据（至少需要表头和一行数据）
                if (rows == null || rows.size() < 2) {
                    log.warn("Sheet '{}' 在文件 '{}' 中没有数据或只有表头。", sheetName,
                      excelFilePath);
                    continue; // 处理下一个 Sheet
                }

                log.info("从 Sheet '{}' 读取到 {} 行数据（包括表头）。", sheetName,
                  rows.size());

                // 6. 提取表头
                List<Object> headerRow = rows.get(0);
                String[] headers = headerRow.stream()
                  .map(obj -> obj == null ? "" : obj.toString().trim())
                  .toArray(String[]::new);

                // 7. 写入表格标题 (例如：### 【核心词汇 - B1_C2】)
                // 繁体转换标题中的Sheet名
                String traditionalSheetName = ZhConverterUtil.toTraditional(sheetName);
                writer.write(
                  "### 【" + tableTitlePrefix + " - " + traditionalSheetName + "】");
                writer.newLine();

                // 8. 写入 Markdown 表头
                writer.write("| " + String.join(" | ", headers) + " |");
                writer.newLine();

                // 9. 写入 Markdown 分隔符行
                writer.write("|" + StrUtil.repeat("---|", headers.length));
                writer.newLine();

                // 10. 遍历数据行（从第二行开始）并写入表格内容
                for (int i = 1; i < rows.size(); i++) {
                    List<Object> dataRow = rows.get(i);
                    writer.write("|");
                    for (int j = 0; j < headers.length; j++) {
                        Object value =
                          (j < dataRow.size()) ? dataRow.get(j) : null; // 处理行长度不一致的情况
                        String cellValue = (value == null) ? "" : value.toString().trim();

                        // 转换为繁体
                        cellValue = ZhConverterUtil.toTraditional(cellValue);

                        // 处理 Markdown 特殊字符：替换 | 为 \| , 替换换行符为 <br>
                        cellValue = cellValue.replace("|", "\\|");
                        cellValue = cellValue.replace("\n", "<br>");

                        writer.write(" " + cellValue + " |");
                    }
                    writer.newLine();
                }
                writer.newLine(); // 表格后加一个空行
                log.info("成功将 Sheet '{}' 的内容作为 Markdown 表格写入。", sheetName);

            } catch (Exception e) {
                // 捕获更广泛的异常，因为 Hutool 可能抛出各种运行时异常
                log.error("读取 Excel Sheet '{}' 或写入 Markdown 表格时出错: {}",
                  sheetName,
                  excelFilePath, e);
                // 可选：根据需要决定是否继续处理其他 Sheet 或抛出异常
            } finally {
                // 11. 确保关闭 reader
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    // ============================================

    public static void genGitHubArticle(String category, String dateString) {
        try {
            generateMarkdownFile(category, dateString);
            log.info("Markdown 文件 {}/{} 生成完毕！", category, dateString);
        } catch (IOException e) {
            log.error("为 {}/{} 生成 Markdown 文件时出现异常：", category, dateString,
              e);
        }
    }

    public static void main(String[] args) {

//        String folderName = "123456";
//        String title = "【BBC六分钟英语】哪些人会购买高端相机？";
//        MarkdownFileGenerator06.genYoutubeArticle(folderName, title);

        // 示例调用
        String category = "0003_PressBriefings"; // 替换为你的分类
        String dateString = "20250402"; // 替换为你的日期

        // 确保你的 OperatingSystem.getBaseFolder() 和 CdConstants 配置正确
        // 确保 D:\0000\0003_PressBriefings\20250326\ 目录下存在 20250326_完整词汇表.xlsx 文件
        // 确保目标输出目录 D:\GitHub\half-hour-english\_posts\ 存在或可以被创建

        MarkdownFileGenerator06.genGitHubArticle(category, dateString);

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
