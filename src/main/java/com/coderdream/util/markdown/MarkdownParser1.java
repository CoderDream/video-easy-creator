package com.coderdream.util.markdown;

import com.coderdream.util.cd.CdTimeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析 Markdown 文件并存储到 MarkdownBean 中的工具类
 */
@Slf4j
public class MarkdownParser1 {

  /**
   * MarkdownBean 类，用于存储 Markdown 文件解析后的数据
   */
  @Data
  public static class MarkdownBean {

    private String title; // 文章标题
    private String indexImg; // 索引图片
    private String date; // 发布日期
    private List<String> tags; // 标签列表
    private List<String> categories; // 分类列表
    private List<Word> words; // 单词列表

    @Data
    public static class Word {

      private String title; // 单词标题
      private String content; // 单词内容
      private String image; // 单词图片
      private String phonetic; // 单词音标

      // 构造函数
      public Word(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
      }

      public Word(String title, String content, String image, String phonetic) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.phonetic = phonetic;
      }
    }
  }

  /**
   * 解析 Markdown 文件，将数据存储到 MarkdownBean 中
   *
   * @param markdownFilePath Markdown文件路径
   * @return MarkdownBean 对象
   */
  public static MarkdownBean parseMarkdownFile(String markdownFilePath) {
    Instant start = Instant.now(); // 记录方法开始时间

    MarkdownBean markdownBean = new MarkdownBean();
    List<MarkdownBean.Word> wordList = new ArrayList<>();

    try {
      String markdownContent = readFileContent(
        markdownFilePath); // 读取 Markdown 文件内容

      // 解析 front matter
      String frontMatter = extractFrontMatter(markdownContent);
      if (frontMatter != null && !frontMatter.isEmpty()) {
        parseFrontMatter(frontMatter, markdownBean);
      }

      // 解析单词信息
      String[] lines = markdownContent.split("\\r?\\n"); // 使用换行符分割markdown文件内容
      Pattern h4Pattern = Pattern.compile("####\\s+(.+)"); // 匹配 #### 标题行
      Pattern imagePattern = Pattern.compile("!\\[.*\\]\\((.*)\\)"); // 匹配图片行

      String currentWordTitle = null;
      StringBuilder currentWordContent = new StringBuilder();
      String currentWordImage = null;

      for (String line : lines) {
        Matcher h4Matcher = h4Pattern.matcher(line);
        Matcher imageMatcher = imagePattern.matcher(line);
        if (h4Matcher.find()) { // 如果当前行是 #### 标题行
          if (currentWordTitle != null) {
            // 将上一个单词添加到单词列表
            wordList.add(new MarkdownBean.Word(currentWordTitle,
              currentWordContent.toString(), currentWordImage));
            currentWordContent = new StringBuilder();
            currentWordImage = null;
          }
          currentWordTitle = h4Matcher.group(1).trim(); // 获取标题
        } else if (imageMatcher.find()) {
          currentWordImage = imageMatcher.group(1).trim(); // 获取图片
        } else if (currentWordTitle != null) {
          currentWordContent.append(line).append("\n"); // 将非标题和图片行的内容加入到单词内容
        }
      }
      // 添加最后一个单词
      if (currentWordTitle != null) {
        wordList.add(
          new MarkdownBean.Word(currentWordTitle, currentWordContent.toString(),
            currentWordImage));
      }

      markdownBean.setWords(wordList); // 设置单词列表
    } catch (IOException e) {
      log.error("Error reading or parsing file: {}", markdownFilePath, e);
    }
    Instant end = Instant.now(); // 记录方法结束时间
    log.info("Method parseMarkdownFile execute time: {}",
      CdTimeUtil.formatDuration(Duration.between(start, end).toMillis()));
    return markdownBean; // 返回 MarkdownBean 对象
  }

  /**
   * 读取文件内容
   *
   * @param filePath 文件路径
   * @return 文件内容字符串
   * @throws IOException 读取文件失败时抛出异常
   */
  private static String readFileContent(String filePath) throws IOException {
    try {
      return Files.readString(Paths.get(filePath));
    } catch (IOException e) {
      log.error("Error reading file: {}", filePath, e);
      throw e;
    }
  }

  /**
   * 提取 front matter 部分
   *
   * @param markdownContent 文件内容
   * @return front matter 部分字符串
   */
  private static String extractFrontMatter(String markdownContent) {
    Pattern pattern = Pattern.compile("---(.*?)---", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(markdownContent);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return null;
  }


  /**
   * 解析 front matter 部分，并将数据存入 MarkdownBean
   *
   * @param frontMatter  front matter 部分内容
   * @param markdownBean MarkdownBean 对象
   */
  private static void parseFrontMatter(String frontMatter,
    MarkdownBean markdownBean) {
    String[] lines = frontMatter.split("\\r?\\n"); // 使用换行符分割 front matter 内容为行
    for (String line : lines) {
      if (line.contains(":")) { // 如果当前行包含冒号
        String[] parts = line.split(":", 2); // 使用冒号分割当前行
        if (parts.length == 2) {
          String key = parts[0].trim(); // 获取键
          String value = parts[1].trim(); // 获取值
          switch (key) {
            case "title":
              markdownBean.setTitle(value); // 设置文章标题
              break;
            case "index_img":
              markdownBean.setIndexImg(value); // 设置索引图片
              break;
            case "date":
              markdownBean.setDate(value); // 设置发布日期
              break;
            case "tags":
              markdownBean.setTags(parseList(value)); // 设置标签列表
              break;
            case "categories":
              markdownBean.setCategories(parseList(value)); // 设置分类列表
              break;
            default:
              log.warn("Unknown key in front matter: {}", key); // 记录未知键的警告
          }
        }
      }
    }
  }

  /**
   * 将字符串解析为列表
   *
   * @param value 字符串值
   * @return 字符串列表
   */
  private static List<String> parseList(String value) {
    List<String> list = new ArrayList<>();
    String trimValue = value.replaceAll("\\s+", " ").trim();
    if (trimValue.startsWith("[") && trimValue.endsWith("]")) {
      String listStr = trimValue.substring(1, trimValue.length() - 1);
      String[] items = listStr.split(",\\s*"); // 使用逗号和任意空白字符分割列表
      for (String item : items) {
        list.add(item.trim());
      }
    } else if (!trimValue.isEmpty()) {
      String[] items = trimValue.split(",\\s*"); // 使用逗号和任意空白字符分割列表
      for (String item : items) {
        list.add(item.trim());
      }
    }
    return list; // 返回解析后的列表
  }

  /**
   * 生成 Markdown 文件
   *
   * @param markdownBean MarkdownBean 对象
   * @param outputFile   输出文件路径
   */
  public static void generateMarkdownFile(MarkdownBean markdownBean,
    String outputFile) {
    Instant start = Instant.now(); // 记录方法开始时间
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(outputFile))) {
      // 写入 front matter
      writer.write("---\n");
      if (markdownBean.getTitle() != null) {
        writer.write("title: " + markdownBean.getTitle() + "\n");
      }
      if (markdownBean.getIndexImg() != null) {
        writer.write("index_img: " + markdownBean.getIndexImg() + "\n");
      }
      if (markdownBean.getDate() != null) {
        writer.write("date: " + markdownBean.getDate() + "\n");
      }
      if (markdownBean.getTags() != null && !markdownBean.getTags().isEmpty()) {
        writer.write("tags: \n    ");
        writer.write(String.join(", ", markdownBean.getTags()));
        writer.write("\n");
      }
      if (markdownBean.getCategories() != null && !markdownBean.getCategories()
        .isEmpty()) {
        writer.write("categories: \n    ");
        writer.write(String.join(", ", markdownBean.getCategories()));
        writer.write("\n");
      }
      writer.write("---\n\n");

      // 写入单词信息
      if (markdownBean.getWords() != null) {
        for (MarkdownBean.Word word : markdownBean.getWords()) {
          writer.write("#### " + word.getTitle() + "\n\n");
          writer.write(word.getContent() + "\n\n");
          if (word.getPhonetic() != null && !word.getPhonetic().isEmpty()) {
            writer.write("音标: " + word.getPhonetic() + "\n\n");
          }

          if (word.getImage() != null) {
            writer.write("![](" + word.getImage() + ")\n\n");
          }
        }
      }
      log.info("Markdown file generated successfully at: {}", outputFile);
    } catch (IOException e) {
      log.error("Error generating Markdown file: {}", outputFile, e);
    }
    Instant end = Instant.now(); // 记录方法结束时间
    log.info("Method generateMarkdownFile execute time: {}",
      CdTimeUtil.formatDuration(Duration.between(start, end).toMillis()));

  }

  /**
   * 设置单词的音标
   *
   * @param markdownBean MarkdownBean 对象
   * @param phoneticMap  单词标题和音标的map
   */
  public static void setWordPhonetics(MarkdownBean markdownBean,
    java.util.Map<String, String> phoneticMap) {
    if (markdownBean != null && markdownBean.getWords() != null) {
      for (MarkdownBean.Word word : markdownBean.getWords()) {
        String phonetic = phoneticMap.get(word.getTitle());
        if (phonetic != null && !phonetic.isEmpty()) {
          word.setPhonetic(phonetic);
        }
      }
    }
  }

  public static void main(String[] args) {
    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.md"; // Markdown 文件路径
    MarkdownBean markdownBean = MarkdownParser1.parseMarkdownFile(
      markdownFilePath); // 解析 Markdown 文件
    log.info("Parsed Markdown Bean: {}",
      markdownBean); // 打印解析后的 MarkdownBean 对象

    if (markdownBean != null && markdownBean.getWords() != null) {
      markdownBean.getWords().forEach(word -> {
        log.info("word before phonetic: {}", word);
      });
    }

    //  模拟通过第三方接口获取音标信息，设置单词的音标
    java.util.Map<String, String> phoneticMap = new java.util.HashMap<>();
    phoneticMap.put("01 exclusive", "/ɪkˈskluːsɪv/");
    phoneticMap.put("02 hoe", "/hoʊ/");
    phoneticMap.put("03 attraction", "/əˈtrækʃən/");
    phoneticMap.put("04 emission", "/ɪˈmɪʃən/");
    phoneticMap.put("05 proposition", "/ˌprɑːpəˈzɪʃən/");
    phoneticMap.put("06 frustration", "/frʌˈstreɪʃən/");
    phoneticMap.put("07 enact", "/ɪˈnækt/");
    phoneticMap.put("08 vest", "/vest/");
    phoneticMap.put("09 torture", "/ˈtɔːrtʃər/");
    phoneticMap.put("10 stroll", "/stroʊl/");

    MarkdownParser1.setWordPhonetics(markdownBean, phoneticMap);
    if (markdownBean != null && markdownBean.getWords() != null) {
      markdownBean.getWords().forEach(word -> {
        log.info("word after phonetic: {}", word);
      });
    }
    String outputFile = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001_output.md"; // 输出 Markdown 文件路径
    MarkdownParser1.generateMarkdownFile(markdownBean,
      outputFile); // 生成 Markdown 文件
  }
}
