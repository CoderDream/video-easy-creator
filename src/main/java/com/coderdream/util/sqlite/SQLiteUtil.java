package com.coderdream.util.sqlite;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.coderdream.entity.WordEntity;
import java.util.Collections;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SQLite 数据库读写工具类，用于存储数据。
 */
@Slf4j
public class SQLiteUtil {

  private static final String FOLDER_PATH =
    new File("").getAbsolutePath() + File.separatorChar
      + "src" + File.separatorChar + "main" + File.separatorChar + "resources"
      + File.separatorChar + "data" + File.separatorChar + "dict"
      + File.separatorChar;
  private static final String DB_URL =
    "jdbc:sqlite:" + FOLDER_PATH + "dict.db"; // 数据库文件路径


  /**
   * 创建 SQLite 表，增加 id 自增主键，headword + pos 唯一索引。
   * 在创建表之前，先判断表是否存在，如果存在则直接返回，不再执行创建操作。 避免删除历史数据。
   *
   * @param tableName 表名
   * @return 创建表所用时间
   */
  public static String createTable(final String tableName) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("开始创建表: {}", tableName);

    // 1. 检查表是否存在
    if (isTableExist(tableName)) {
      log.warn("表 {} 已经存在，跳过创建。", tableName);
      stopWatch.stop();
      return "表 " + tableName + " 已经存在，跳过创建。耗时：" + formatElapsedTime(
        stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
        + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"  // 自增主键
        + "    headword TEXT NOT NULL,\n"                // 单词
        + "    pos TEXT NOT NULL,\n"                     // 词性
        + "    cefr TEXT,\n"                           // 等级
        + "    chineseDefinition TEXT,\n"                 // 中文释义
        + "    example TEXT,\n"                         // 例句
        + "    phonetic TEXT,\n"                         // 音标
        + "    exampleTranslation TEXT,\n"               // 例句的中文含义
        + "    UNIQUE(headword, pos)\n"              // 唯一约束：单词和词性组合唯一
        + ");"; // 创建表 SQL 语句

    try (Connection conn = DriverManager.getConnection(DB_URL);
      Statement stmt = conn.createStatement()) {

      // 2. 创建表 (因为已经确认表不存在，所以可以直接创建)
      stmt.execute(CREATE_TABLE_SQL);
      log.info("表 {} 创建成功", tableName);

    } catch (SQLException e) {
      log.error("创建表 {} 失败: {}", tableName, e.getMessage(), e);
      return "创建表失败: " + e.getMessage();
    } finally {
      stopWatch.stop();
      log.info("创建表 {} 结束", tableName);
    }
    return formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS));
  }

  /**
   * 辅助方法：判断表是否存在
   *
   * @param tableName 表名
   * @return 如果表存在返回 true，否则返回 false
   */
  private static boolean isTableExist(String tableName) {
    try (Connection conn = DriverManager.getConnection(DB_URL);
      ResultSet rs = conn.getMetaData()
        .getTables(null, null, tableName, null)) {
      return rs.next(); // 如果 rs 中有数据，说明表存在
    } catch (SQLException e) {
      log.error("检查表 {} 是否存在时发生错误: {}", tableName, e.getMessage(),
        e);
      return false; // 发生错误时，保守起见，也返回 false
    }
  }

  /**
   * 格式化耗时时间
   *
   * @param milliseconds 毫秒数
   * @return 格式化后的时间字符串
   */
  private static String formatElapsedTime(long milliseconds) {
    long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
    long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
    long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
    long millis = milliseconds % 1000;
    return String.format("%02dh %02dm %02ds %03dms", hours, minutes, seconds,
      millis);
  }

  /**
   * 从 Markdown 文件读取数据并写入 SQLite 数据库。
   * 假设 Markdown 文件格式是标准的表格格式，并按照固定顺序解析数据。
   * 忽略表头。
   * 如果遇到重复数据，则更新已有的数据。
   *
   * @param tableName       表名
   * @param markdownFilePath Markdown 文件路径
   * @return 导入结果描述
   */
  public static String importDataFromMarkdown(String tableName, String markdownFilePath) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("开始从 Markdown 文件导入数据到表: {}", tableName);

    File markdownFile = new File(markdownFilePath);
    if (!markdownFile.exists()) {
      log.error("Markdown 文件不存在: {}", markdownFilePath);
      return "Markdown 文件不存在: " + markdownFilePath;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL)) {
      List<WordInfo> data = readDataFromMarkdown(markdownFilePath);
      if (data == null || data.isEmpty()) {
        log.warn("Markdown 文件中没有数据: {}", markdownFilePath);
        return "Markdown 文件中没有数据";
      }

      // 使用 INSERT OR REPLACE 语句, 如果 headword 和 pos 已经存在，则更新数据
      String insertOrReplaceSql = "INSERT OR REPLACE INTO " + tableName + " (headword,pos,CEFR,chineseDefinition,example,phonetic,exampleTranslation) VALUES (?,?,?,?,?,?,?)";
      log.debug("SQL: {}", insertOrReplaceSql);

      try (PreparedStatement pstmt = conn.prepareStatement(insertOrReplaceSql)) {
        conn.setAutoCommit(false); // 开启事务
        int batchSize = 100;
        int count = 0;

        for (WordInfo wordInfo : data) {
          int columnIndex = 1;
          pstmt.setString(columnIndex++, wordInfo.getHeadword());
          pstmt.setString(columnIndex++, wordInfo.getPos());
          pstmt.setString(columnIndex++, wordInfo.getCefr());
          pstmt.setString(columnIndex++, wordInfo.getChineseDefinition());
          pstmt.setString(columnIndex++, wordInfo.getExample());
          pstmt.setString(columnIndex++, wordInfo.getPhonetic());
          pstmt.setString(columnIndex++, wordInfo.getExampleTranslation());

          pstmt.addBatch();
          count++;
          if (count % batchSize == 0) {
            pstmt.executeBatch();
            conn.commit();
            log.info("已提交 {} 条数据", count);
          }
        }

        pstmt.executeBatch(); // 提交剩余的数据
        conn.commit(); // 提交事务
        log.info("数据从 Markdown 文件成功导入到表: {}", tableName);

      } catch (SQLException e) {
        conn.rollback();
        log.error("插入/更新数据失败，事务已回滚: {}", e.getMessage(), e);
        return "从 Markdown 文件导入数据失败: " + e.getMessage();
      } finally {
        conn.setAutoCommit(true); // 恢复自动提交
      }

    } catch (Exception e) {
      log.error("从 Markdown 文件导入数据到表 {} 失败: {}", tableName,
        e.getMessage(), e);
      return "从 Markdown 文件导入数据失败: " + e.getMessage();
    } finally {
      stopWatch.stop();
      log.info("从 Markdown 文件导入数据到表 {} 结束", tableName);
    }
    return formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS));
  }

  /**
   * 从 Markdown 文件中读取数据，解析表格内容，忽略表头和非表格内容。
   * 按照固定的列顺序读取数据，不再依赖表头信息。
   * 使用 WordInfo 对象封装数据 (英文属性)
   *
   * @param markdownFilePath Markdown 文件路径
   * @return 解析后的 WordInfo 对象列表
   * @throws IOException 读取文件失败时抛出
   */
  public static List<WordInfo> readDataFromMarkdown(String markdownFilePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(markdownFilePath));
    List<WordInfo> data = new ArrayList<>();

    boolean headerSkipped = false; // 用于标记是否跳过了表头行

    // 从第二行开始解析数据 (跳过表头和分割线)
    for (int i = 0; i < lines.size(); i++) {
      String dataLine = lines.get(i).trim();

      // 1. 跳过表头 (只跳过一次)
      if (!headerSkipped) {
        if (dataLine.startsWith("|") && dataLine.endsWith("|") && StringUtils.countMatches(dataLine, "|") >= 7) {
          log.debug("跳过表头行: {}", dataLine);
          headerSkipped = true; // 设置标记
          continue; // 跳过该行
        } else {
          log.debug("未识别到表头, 跳过此行 {}", dataLine);
          continue;
        }
      }

      // 2. 确保数据行以 "|" 分隔符开始和结束，并且包含至少 6 个 "|" (因为有 7 列)
      if (dataLine.startsWith("|") && dataLine.endsWith("|") && StringUtils.countMatches(dataLine, "|") >= 7) {

        // 3. 跳过分割线
        if (isSeparatorLine(dataLine)) {
          log.debug("检测到分割线, 跳过: {}", dataLine);
          continue; // 跳过分割线
        }

        List<String> values = Arrays.stream(dataLine.split("\\|"))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .toList();

        // 4. 确保数据行的列数等于 7
        if (values.size() == 7) {
          WordInfo wordInfo = new WordInfo();
          wordInfo.setHeadword(values.get(0));
          wordInfo.setPos(values.get(1));
          wordInfo.setCefr(values.get(2));
          wordInfo.setChineseDefinition(values.get(3));
          wordInfo.setExample(values.get(4));
          wordInfo.setPhonetic(values.get(5));
          wordInfo.setExampleTranslation(values.get(6));
          data.add(wordInfo);
        } else {
          log.warn("数据行 {} 列数不正确，跳过该行， 在 {} 文件中：", dataLine, markdownFilePath);
        }
      } else {
        log.debug("非表格数据行，跳过: {}", dataLine);
      }
    }

    log.info("成功从 Markdown 文件读取 {} 条数据", data.size());
    return data;
  }

//    /**
//     * 新增方法，用于判断是否为分割线
//     *
//     * @param line 需要判断的字符串
//     * @return 如果是全由 "-" 和 ":" 和 "|" 组成的分割线，返回 true，否则返回 false
//     */
//    private static boolean isSeparatorLine(String line) {
//        // 使用正则表达式检查是否只包含 "-", ":" 和 "|"
//        return line.matches("[\\s\\-\\|:]+");
//    }

  /**
   * 从 Excel 文件读取数据并写入 SQLite 数据库 (使用 Hutool)
   *
   * @return 写入数据库所用时间
   */
  public static String importDataFromExcel(String tableName, String excelPath) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("开始从 Excel 文件导入数据到表: {}", tableName);

    File excelFile = new File(excelPath);
    if (!excelFile.exists()) {
      log.error("Excel 文件不存在: {}", excelPath);
      return "Excel 文件不存在: " + excelPath;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL)) {
      ExcelReader reader = ExcelUtil.getReader(excelFile);
      List<Map<String, Object>> readAll = reader.readAll();

      // 获取表头信息
      List<Object> headListObject = reader.readRow(0); // 读取第一行作为表头
      List<String> headList = headListObject.stream().map(String::valueOf)
        .toList();
      log.info("表头: {}", headList);

      String insertSql = "INSERT INTO " + tableName + " (" +
        String.join(",", headList) +
        ") VALUES (" +
        String.join(",", headList.stream().map(h -> "?").toArray(String[]::new))
        +
        ")";
      log.info("SQL: {}", insertSql);

      try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
        conn.setAutoCommit(false); // 开启事务
        int batchSize = 100;
        int count = 0;

        for (Map<String, Object> row : readAll) {
          int columnIndex = 1;
          for (String head : headList) {
            Object value = row.get(head);
            pstmt.setObject(columnIndex++, value);
          }
          pstmt.addBatch();

          count++;
          if (count % batchSize == 0) {
            pstmt.executeBatch();
            conn.commit();
            log.info("已提交 {} 条数据", count);
          }
        }

        pstmt.executeBatch(); // 提交剩余的数据
        conn.commit(); // 提交事务
        log.info("数据从 Excel 文件成功导入到表: {}", tableName);

      } catch (SQLException e) {
        conn.rollback();
        log.error("插入数据失败，事务已回滚: {}", e.getMessage(), e);
        return "从 Excel 文件导入数据失败: " + e.getMessage();
      } finally {
        conn.setAutoCommit(true); // 恢复自动提交
      }

    } catch (Exception e) {
      log.error("从 Excel 文件导入数据到表 {} 失败: {}", tableName,
        e.getMessage(), e);
      return "从 Excel 文件导入数据失败: " + e.getMessage();
    } finally {
      stopWatch.stop();
      log.info("从 Excel 文件导入数据到表 {} 结束", tableName);
    }
    return formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS));
  }

  /**
   * 从 SQLite 数据库读取数据到 WordEntity 对象列表
   *
   * @return WordEntity 对象列表
   */
  public static List<WordEntity> getAllWords(String tableName) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<WordEntity> wordList = new ArrayList<>();
    log.info("开始从表 {} 读取数据到 WordEntity 对象列表", tableName);

    try {
      // 尝试加载 SQLite JDBC 驱动程序（通常不需要）
      Class.forName("org.sqlite.JDBC");

      try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

        while (rs.next()) {
          WordEntity wordEntity = new WordEntity();
          wordEntity.setWord(rs.getString("headword"));
          wordEntity.setUk(rs.getString("phonetic"));
          wordEntity.setUs(rs.getString("exampleTranslation"));
          wordEntity.setComment(rs.getString("chineseDefinition"));
          wordEntity.setLevel(rs.getString("cefr"));
          wordList.add(wordEntity);
        }
        log.info("成功从表 {} 读取 {} 条数据到 WordEntity 对象列表", tableName,
          wordList.size());

      } catch (SQLException e) {
        log.error("从表 {} 读取数据失败: {}", tableName, e.getMessage(), e);
        return null; // 或者抛出异常
      }
    } catch (ClassNotFoundException e) {
      log.error("找不到 SQLite JDBC 驱动程序: {}", e.getMessage(), e);
      return null;
    } finally {
      stopWatch.stop();
      log.info("从表 {} 读取数据到 WordEntity 对象列表结束", tableName);
    }

    return wordList;
  }

  /**
   * 根据提供的单词列表，从词汇总表中查询对应的记录。 使用 PreparedStatement 和 动态 IN 子句，避免 SQL 注入，提高效率。
   *
   * @param words 要查询的单词列表
   * @return 包含查询结果的 WordEntity 对象列表, 如果出错返回null
   */
  public static List<WordEntity> findWordsInSummaryTable(List<String> words) {
    if (words == null || words.isEmpty()) {
      log.warn("单词列表为空，不执行查询。");
      return Collections.emptyList(); // 返回空列表，而不是 null
    }

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<WordEntity> wordList = new ArrayList<>();
    log.info("开始从词汇总表中查询单词...");

    // 1. 动态构建 IN 子句的占位符 (?, ?, ?, ...)
    String placeholders = String.join(",",
      Collections.nCopies(words.size(), "?"));

    // 2. 构建完整的 SQL 语句
    String sql = "SELECT * FROM 词汇总表 WHERE 单词 IN (" + placeholders + ")";
    log.debug("SQL: {}", sql);

    try (Connection conn = DriverManager.getConnection(DB_URL);
      // 3. 创建 PreparedStatement
      PreparedStatement pstmt = conn.prepareStatement(sql)) {

      // 4. 设置参数 (将单词列表中的每个单词设置为参数)
      for (int i = 0; i < words.size(); i++) {
        pstmt.setString(i + 1, words.get(i)); // 注意：参数索引从 1 开始
      }

      // 5. 执行查询
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          WordEntity wordEntity = new WordEntity();
          wordEntity.setWord(rs.getString("单词"));
          wordEntity.setUk(rs.getString("英音"));
          wordEntity.setUs(rs.getString("美音"));
          wordEntity.setComment(rs.getString("释义"));
          wordEntity.setLevel(rs.getString("等级"));
          wordList.add(wordEntity);
        }
      }
      log.info("从词汇总表中查询到 {} 条记录", wordList.size());

    } catch (SQLException e) {
      log.error("从词汇总表中查询单词失败: {}", e.getMessage(), e);
      return null; // 或者抛出自定义异常, 由调用者决定
    } finally {
      stopWatch.stop();
      log.info("从词汇总表中查询单词结束，耗时: {}",
        formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS)));
    }

    return wordList;
  }

  /**
   * 创建数据库和表，并导入数据（带参数）
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    //  initData();
    String tableName = "cefr_c1_words";
    String markdownFilePath = "D:/0000/CEFR/gemini_output_001.md";

    // 1. 创建表
    String createTableTime = createTable(tableName);
    System.out.println("创建表耗时: " + createTableTime);

    // 2. 从 Markdown 文件导入数据
    String importDataTime = importDataFromMarkdown(tableName, markdownFilePath);
    System.out.println("从 Markdown 文件导入数据耗时: " + importDataTime);
  }

//    /**
//     * 从 Markdown 文件中读取数据，解析表格内容，忽略表头和非表格内容。
//     * 按照固定的列顺序读取数据，不再依赖表头信息。
//     * 使用 WordInfo 对象封装数据 (英文属性)
//     *
//     * @param markdownFilePath Markdown 文件路径
//     * @return 解析后的 WordInfo 对象列表
//     * @throws IOException 读取文件失败时抛出
//     */
//    public static List<WordInfo> readDataFromMarkdown(String markdownFilePath) throws IOException {
//        List<String> lines = Files.readAllLines(Paths.get(markdownFilePath));
//        List<WordInfo> data = new ArrayList<>();
//
//        // 从第二行开始解析数据 (跳过表头和分割线)
//        for (int i = 1; i < lines.size(); i++) {
//            String dataLine = lines.get(i).trim();
//
//            // 确保数据行以 "|" 分隔符开始和结束，并且包含至少 6 个 "|" (因为有 7 列)
//            if (dataLine.startsWith("|") && dataLine.endsWith("|") && StringUtils.countMatches(dataLine, "|") >= 7) {
//
//                // 跳过分割线
//                if (isSeparatorLine(dataLine)) {
//                    log.debug("检测到分割线, 跳过: {}", dataLine);
//                    continue;
//                }
//
//                List<String> values = Arrays.stream(dataLine.split("\\|"))
//                        .map(String::trim)
//                        .filter(s -> !s.isEmpty())
//                        .toList();
//
//                // 确保数据行的列数等于 7
//                if (values.size() == 7) {
//                    WordInfo wordInfo = new WordInfo();
//                    wordInfo.setHeadword(values.get(0));
//                    wordInfo.setPos(values.get(1));
//                    wordInfo.setCefr(values.get(2));
//                    wordInfo.setChineseDefinition(values.get(3));
//                    wordInfo.setExample(values.get(4));
//                    wordInfo.setPhonetic(values.get(5));
//                    wordInfo.setExampleTranslation(values.get(6));
//                    data.add(wordInfo);
//                } else {
//                    log.warn("数据行 {} 列数不正确，跳过该行", dataLine);
//                }
//            } else {
//                log.debug("非表格数据行，跳过: {}", dataLine);
//            }
//        }
//
//        log.info("成功从 Markdown 文件读取 {} 条数据", data.size());
//        return data;
//    }

  /**
   * 新增方法，用于判断是否为分割线
   *
   * @param line 需要判断的字符串
   * @return 如果是全由 "-" 和 ":" 和 "|" 组成的分割线，返回 true，否则返回 false
   */
  private static boolean isSeparatorLine(String line) {
    // 使用正则表达式检查是否只包含 "-", ":" 和 "|"
    return line.matches("[\\s\\-\\|:]+");
  }
}
