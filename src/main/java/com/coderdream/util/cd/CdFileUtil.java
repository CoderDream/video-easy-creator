package com.coderdream.util.cd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.coderdream.entity.ArticleTitle;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.SubtitleEntity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ResourceUtils;

/**
 * Java按一行一行进行文件的读取或写入 https://blog.csdn.net/yuanhaiwn/article/details/83090540
 *
 * @author CoderDream
 */
@Slf4j
public class CdFileUtil {


  /**
   * 获取对话实体列表
   *
   * @param fileName 脚本位置
   * @return 对话实体列表
   */

  public static List<DialogSingleEntity> genDialogSingleEntityList(
    String fileName) {
    List<String> stringList = FileUtil.readLines(fileName,
      StandardCharsets.UTF_8);
    // 如果不为空，且最后一行不是空串，则添加一行空串
    if (CollectionUtil.isNotEmpty(stringList) && !StrUtil.isBlankOrUndefined(
      stringList.get(stringList.size() - 1))) {
      stringList.add("");
    }

    List<DialogSingleEntity> result = new ArrayList<>();

    DialogSingleEntity scriptEntity;
    if (CollectionUtils.isNotEmpty(stringList)) {
      int size = stringList.size();
      if (size % 3 != 0) {
        System.out.println(
          "文件格式有问题，行数应该是3的倍数，实际为：" + size + "; fileName: "
            + fileName);
        return null;
      }

      for (int i = 0; i < stringList.size(); i += 3) {
        scriptEntity = new DialogSingleEntity();
        scriptEntity.setHostEn(stringList.get(i));
        scriptEntity.setContentEn(stringList.get(i + 1));
        result.add(scriptEntity);
      }
    }

    return result;
  }

  /**
   * 读取resources文件夹下13500文件夹中的1-3500.txt文件并返回内容列表
   *
   * @return 文件内容的列表
   */
  public static List<String> readFileContent(String resourcePath) {
    // 获取资源的URL
//        String resourcePath = "classpath:13500/" + filename;
    try {
      // 使用HuTool的ResourceUtil获取资源路径
      // 指定要下载的文件
      File file = ResourceUtils.getFile(resourcePath);
      // 定义UTF-16 Little Endian编码
//      Charset utf16Le = StandardCharsets.UTF_16LE;
      // 读取文件内容到列表
//            return FileUtil.readLines(file, "UTF-8");
//            List<String> lines = FileUtil.readLines(file, utf16Le);
      List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
      // 移除每行首尾空格，并过滤掉空行
      return lines.stream()
        .map(String::trim)
        .filter(line -> !line.isEmpty())
        .collect(Collectors.toList());
    } catch (Exception e) {
//            e.printStackTrace();
      log.error("读取文件失败: {}, {}", resourcePath, e.getMessage(), e);
      // 抛出运行时异常或进行其他错误处理
//      throw new RuntimeException("读取文件失败", e);
    }
    return null;
  }

  /**
   * 读取resources文件夹下13500文件夹中的1-3500.txt文件并返回内容列表
   *
   * @return 文件内容的列表
   */
  public static List<String> readFileContentWithCharset(String resourcePath,
    Charset charset) {
    // 获取资源的URL
//        String resourcePath = "classpath:13500/" + filename;
    try {
      // 使用HuTool的ResourceUtil获取资源路径
      // 指定要下载的文件
      File file = ResourceUtils.getFile(resourcePath);
      // 定义UTF-16 Little Endian编码
//            Charset utf16Le = StandardCharsets.UTF_16LE;
      // 读取文件内容到列表
//            return FileUtil.readLines(file, "UTF-8");
      List<String> lines = FileUtil.readLines(file, charset);
      // 移除每行首尾空格，并过滤掉空行
      return lines.stream()
        .map(String::trim)
        .filter(line -> !line.isEmpty())
        .collect(Collectors.toList());
    } catch (Exception e) {
//            e.printStackTrace();
      log.error("读取文件失败: {}", e.getMessage());
      // 抛出运行时异常或进行其他错误处理
      throw new RuntimeException("读取文件失败", e);
    }
  }

  // ArticleTitle
  public static List<ArticleTitle> getArticleTitleList(String fileName) {
    List<ArticleTitle> articleTitleList = new ArrayList<>();
    List<String> titleList = FileUtil.readLines(fileName,
      StandardCharsets.UTF_8);
    ArticleTitle articleTitle = null;
    for (String title : titleList) {
      articleTitle = new ArticleTitle();
      String[] arrs = title.split("\t");
      if (arrs.length == 2) {
        articleTitle.setDateStr(arrs[0]);
        articleTitle.setTitle(arrs[1]);
        articleTitleList.add(articleTitle);
      }
    }
    return articleTitleList;
  }

  public static String getArticleTitle(String folderName) {
//    String fileName = CdConstants.RESOURCES_BASE_PATH + "\\bbc\\title.txt";
    String fileName =
      CdFileUtil.getResourceRealPath() + "\\data\\bbc\\title.txt";

    List<ArticleTitle> articleTitleList = getArticleTitleList(fileName);
    for (ArticleTitle article : articleTitleList) {
      if (article.getDateStr().equals(folderName)) {
        return article.getTitle();
      }
    }
    return "";
  }

  public static void writeToFile(String fileName, String content) {
    try {
//            String[] arrs = {
//                    "zhangsan,23,福建",
//                    "lisi,30,上海",
//                    "wangwu,43,北京",
//                    "laolin,21,重庆",
//                    "ximenqing,67,贵州"
//            };
      String[] contentList = content.split(" ");
      //写入中文字符时解决中文乱码问题
      FileOutputStream fos = null;

//            fos = new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt"));

      fos = new FileOutputStream(fileName);

      OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
      BufferedWriter bw = new BufferedWriter(osw);
      //简写如下：
      //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
      //        new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt")), "UTF-8"));

      for (String arr : contentList) {
        bw.write(arr + "\t\n");
      }

      //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
      bw.close();
      osw.close();
      fos.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

//    public static boolean writeToFile(String fileName, List<String> contentList) {
//        FileOutputStream fos = null;
//        OutputStreamWriter osw = null;
//        BufferedWriter bw = null;
//        try {
//            //写入中文字符时解决中文乱码问题
//            fos = new FileOutputStream(fileName);
//
//            osw = new OutputStreamWriter(fos, "UTF-8");
//            bw = new BufferedWriter(osw);
//            int size = contentList.size();
//            for (int i = 0; i < size; i++) {
//                String str = contentList.get(i);
//                //  str = "\uFEFF" + str; BOM格式，剪映不认识，Subindex 合并字幕时要打开
//                // 如果不是最后一行，就加上回车换行
//                if (i != size - 1) {
//                    if (str != null) {
//                        str = str.trim().replaceAll("  ", " ") + "\r\n";
//                    }
//                }
//
//                if (str != null) {
//                    bw.write(str);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } finally {
//            //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
//            if (bw != null) {
//                try {
//                    bw.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (osw != null) {
//                try {
//                    osw.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            return true;
//        }
//    }

  /**
   * 将内容写入指定的文件，并处理异常。
   *
   * @param fileName    文件名
   * @param contentList 要写入文件的内容列表
   * @return 如果写入成功返回 true，否则返回 false
   */
  public static boolean writeToFile(String fileName, List<String> contentList) {
    // 使用 try-with-resources 自动关闭资源
    try (FileOutputStream fos = new FileOutputStream(fileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos,
        StandardCharsets.UTF_8);
      BufferedWriter bw = new BufferedWriter(osw)) {

      int size = contentList.size();  // 获取内容列表的大小
      log.info("开始写入文件: {}", fileName);  // 记录开始写入文件的日志

      // 遍历所有内容并写入文件
      for (int i = 0; i < size; i++) {
        String str = contentList.get(i);

        // 如果内容不是最后一行，添加换行符
        if (i != size - 1 && str != null) {
          str = str.trim().replaceAll("  ", " ") + "\r\n";
        }

        // 如果内容不为空，则写入
        if (str != null) {
          bw.write(str);
        }
      }

      log.info("文件写入成功: {}", fileName);  // 记录成功写入文件的日志
      return true;  // 写入成功返回true

    } catch (IOException e) {
      // 捕获IOException并打印堆栈信息，返回false表示写入失败
      log.error("写入文件失败: {}，错误信息: {}", fileName, e.getMessage(), e);
      return false;
    }
  }

  /**
   * 返回 D:\04_GitHub\java-architect-util\free-apps\src\main\resources //
   * https://blog.csdn.net/qq_38319289/article/details/115236819 //
   * SpringBoot获取resources文件路径 // File directory = new
   * File("src/main/resources"); // String reportPath =
   * directory.getCanonicalPath(); // String resource =reportPath +
   * "/static/template/resultTemplate.docx";
   *
   * @return 资源文件夹路径
   */
  public static String getResourceRealPath() {
    File directory = new File("src/main/resources");
    String reportPath = "";
    try {
      reportPath = directory.getCanonicalPath();
    } catch (Exception e) {
      log.error("获取资源文件夹路径失败: {}", e.getMessage());
    }

    return reportPath;
  }

  /**
   * 在文件名后添加指定的字符串，保留文件路径和扩展名
   *
   * @param filePath 原始文件路径
   * @param part     要添加的字符串
   * @return 修改后的文件路径
   */
  public static String addPostfixToFileName(String filePath, String part) {
    // 将文件路径字符串转换为Path对象
    Path path = Paths.get(filePath);

    // 获取文件名
    Path fileName = path.getFileName();
    if (fileName == null) {
      return filePath;  // 如果没有文件名，则直接返回原始路径
    }

    String fileNameStr = fileName.toString();
    // 分割文件名和扩展名
    int dotIndex = fileNameStr.lastIndexOf('.');
    String baseName, extension = "";

    if (dotIndex > 0) {
      baseName = fileNameStr.substring(0, dotIndex);
      extension = fileNameStr.substring(dotIndex);
    } else {
      baseName = fileNameStr; // 没有扩展名
    }

    // 构建新的文件名
    String newFileName = baseName + part + extension;

    // 获取文件所在的目录
    Path parent = path.getParent();

    // 构建新的文件路径，如果parent为空，则保持原路径不变
    Path newPath =
      (parent != null) ? parent.resolve(newFileName) : Paths.get(newFileName);

    return newPath.toString();
  }

  public static String changeExtension(String filePathString,
    String newExtension) {
    Path filePath = Paths.get(filePathString);
    if (!Files.exists(filePath)) {
      log.warn("文件不存在: {}", filePath.toAbsolutePath());
      return null;
    }

    String originalFileName = filePath.getFileName().toString();
    String newFileName =
      getFileNameWithoutExtension(originalFileName) + "." + newExtension;

    Path newFilePath = filePath.resolveSibling(newFileName);

    //log.info("新的文件名已生成，但文件未被修改: {}", newFilePath.toAbsolutePath());
    return newFilePath.toAbsolutePath().toString(); // 返回新的绝对路径，但不做实际重命名

  }

  public static String getFileNameWithoutExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return fileName;
    }
    return fileName.substring(0, lastDotIndex);
  }

  /**
   * 获取不带扩展名的文件名
   */
  public static String getPureFileNameWithoutExtensionWithPath(
    String filePath) {
    Path path = Paths.get(filePath);
    String fileName = path.getFileName().toString();
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return fileName; // 如果没有点，直接返回
    }
    return fileName.substring(0, lastDotIndex);
  }

  public static List<SubtitleEntity> readSrcFileContent(String... fileName) {
    List<String> stringList = new ArrayList<>();
    if (fileName == null) {
      return null;
    }
    File file = new File(fileName[0]);//定义一个file对象，用来初始化FileReader
    FileReader reader;//定义一个fileReader对象，用来初始化BufferedReader
    try {
      reader = new FileReader(file);
      BufferedReader bReader = new BufferedReader(
        reader);//new一个BufferedReader对象，将文件内容读取到缓存
//            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
      String s = "";
      while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
//                sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
        if (fileName.length == 0) {
          s = s.replaceAll("“", "\"");

          s = s.replaceAll("”", "\"");
        }

        stringList.add(processStr(s.trim()));
//                System.out.println(s);
      }
      stringList.add("");// 补最后一行的空格
      bReader.close();
//            String str = sb.toString();
//            System.out.println(str);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    List<SubtitleEntity> result = getSubtitleEntityList(stringList);

    return result;
  }

  public static @NotNull List<SubtitleEntity> getSubtitleEntityList(
    List<String> stringList) {
    List<SubtitleEntity> result = new ArrayList<>();
    int firstSpaceIndex = 0;
    String subIndexStr = "";
    SubtitleEntity subtitleBaseEntity;
    if (CollectionUtils.isNotEmpty(stringList)) {

      int size = stringList.size();
      if (size % 4 != 0 && StrUtil.isEmpty(stringList.get(size - 1))
        && StrUtil.isEmpty(
        stringList.get(size - 2))) {
        stringList.remove(size - 1);
      }

      for (int i = 0; i < stringList.size(); i++) {
        if (StrUtil.isEmpty(stringList.get(i))) {
          subtitleBaseEntity = new SubtitleEntity();
          if (firstSpaceIndex == 0) {
            subIndexStr = stringList.get(0);
            subtitleBaseEntity.setSubIndex(
              Integer.parseInt(processStr(subIndexStr)));
            subtitleBaseEntity.setTimeStr(processStr(stringList.get(1)));
            subtitleBaseEntity.setSubtitle(processStr(stringList.get(2)));
          } else {
            if (StrUtil.isNotEmpty(stringList.get(firstSpaceIndex))) {
              subtitleBaseEntity.setSubIndex(
                Integer.parseInt(processStr(stringList.get(firstSpaceIndex))));
            }
            subtitleBaseEntity.setTimeStr(
              processStr(stringList.get(firstSpaceIndex + 1)));
            subtitleBaseEntity.setSubtitle(
              processStr(stringList.get(firstSpaceIndex + 2)));
          }
          firstSpaceIndex = i + 1;

          result.add(subtitleBaseEntity);
        }
      }
    }
    return result;
  }

  public static @NotNull List<SubtitleEntity> genSubtitleEntityList(
    List<String> stringList, String platformName) {
    List<SubtitleEntity> result = new ArrayList<>();
    int firstSpaceIndex = 0;
//    String subIndexStr = "";
    String subIndexStr = "";
//    String subIndexStr = "";
    SubtitleEntity subtitleBaseEntity;
//    // 如果最后一个字符串不为空，则补一个空字符串到列表中，以便处理最后一个字幕条目
//    if (StrUtil.isNotEmpty(stringList.get(stringList.size() - 1))) {
//      stringList.add("");
//    }
    // 移除stringList 的空行
    stringList = stringList.stream().filter(s -> !StrUtil.isBlankIfStr(s))
      .collect(Collectors.toList());

    if (CollectionUtils.isNotEmpty(stringList)) {
      int size = stringList.size();
      switch (platformName) {
        case CdConstants.TRANSLATE_PLATFORM_GEMINI:
          // 如果字符串的个数不是2的倍数，则直接返回空列表
          if (size % 2 != 0) {
            log.warn("字符串的个数不是2的倍数，则直接返回空列表，{}", size);
            return result;
          }

          for (int i = 0; i < stringList.size(); i += 2) {
            subtitleBaseEntity = new SubtitleEntity();
            subtitleBaseEntity.setSubtitle(
              processStr(stringList.get(i)));
            subtitleBaseEntity.setSecondSubtitle(
              processStr(stringList.get(i + 1)));
            result.add(subtitleBaseEntity);
          }
          break;
        case CdConstants.TRANSLATE_PLATFORM_MSTTS:
          for (int i = 0; i < stringList.size(); i++) {
            subtitleBaseEntity = new SubtitleEntity();
            subtitleBaseEntity.setSubtitle(
              processStr(stringList.get(i)));
            result.add(subtitleBaseEntity);
          }
          break;
        default:
          // 如果最后一个字符串不为空，则补一个空字符串到列表中，以便处理最后一个字幕条目
          if (StrUtil.isNotEmpty(stringList.get(size - 1))) {
            stringList.add("");
          }
      }


    }
    return result;
  }

  public static final String UTF8_BOM = "\uFEFF";

  public static String processStr(String string) {
    if (string.startsWith(UTF8_BOM)) {
      return string.substring(1);
    }
    return string;
  }

  public static boolean isFileEmpty(String targetPath) {
    File file = new File(targetPath);

    return !file.exists() || file.length() == 0;
  }


  /**
   * 根据文件名生成新的文件名，去掉最后的下划线加temp。
   *
   * @param inputFilePath 输入文件路径
   * @return 生成的新文件名
   */
  public static String generateOutputFilePath(String inputFilePath) {
    File inputFile = new File(inputFilePath);
    String fileName = inputFile.getName();
    String parentPath = inputFile.getParent(); // 获取父目录

    // 移除文件名最后的 "_temp"
    String newFileName = fileName.replace("_temp", "");  // 更简单的方法

    // 构建新的文件路径
    if (parentPath != null) {
      return parentPath + File.separator + newFileName;
    } else {
      return newFileName; // 如果没有父目录，直接返回文件名
    }
  }

  /**
   * 获取指定目录下第一层文件夹列表
   *
   * @param directoryPath 目录路径
   * @return 第一层文件夹列表，如果目录不存在或为空，则返回null
   */
  public static List<File> getFirstLevelDirectories(String directoryPath) {
    File directory = new File(directoryPath);

    // 检查目录是否存在并且是目录
    if (!directory.exists() || !directory.isDirectory()) {
      System.out.println("指定的路径不存在或不是一个目录。");
      return null;
    }

    // 使用 FileUtil.ls(String) 获取所有文件和文件夹，然后过滤出文件夹
    File[] filesArray = FileUtil.ls(directoryPath);  // 使用 String 路径，返回 File[]

    // 修复空目录的校验逻辑
    if (filesArray == null || filesArray.length == 0) {
      System.out.println("该目录为空。");
      return null;
    }

    // 将 File[] 转换为 List<File>
    List<File> files = Arrays.asList(filesArray);

    // 过滤出文件夹
    return files.stream()
      .filter(File::isDirectory)
      .collect(Collectors.toList());
  }

  public static void main(String[] args) {
//        String filePath = "D:\\0000\\EnBook001\\900\\900V1_ch0201.txt";
//    String filePath = "900V1_ch0201.txt";
//    String newFilePath = CdFileUtil.addPostfixToFileName(filePath, "_part01");
//    System.out.println("原始文件路径: " + filePath);
//    System.out.println("修改后的文件路径: " + newFilePath);
//
//    String filePath2 = "D:/0000/EnBook001/900/900V1_ch0201.txt";
//    String newFilePath2 = CdFileUtil.addPostfixToFileName(filePath2, "_part01");
//    System.out.println("原始文件路径2: " + filePath2);
//    System.out.println("修改后的文件路径2: " + newFilePath2);

    String inputFilePath = "D:\\0000\\EnBook002\\Chapter007\\Chapter007_temp.txt"; // 替换为你的输入文件路径
    String outputFilePath = CdFileUtil.generateOutputFilePath(inputFilePath);
    System.out.println("新的文件路径: " + outputFilePath);

  }
}
