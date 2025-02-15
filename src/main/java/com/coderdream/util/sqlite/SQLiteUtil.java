package com.coderdream.util.sqlite;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.coderdream.entity.WordEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.coderdream.util.cd.CdFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SQLite 数据库读写工具类，用于存储 Excel 表格内容（使用 Hutool）。
 */
@Slf4j
public class SQLiteUtil {

    private static final String FOLDER_PATH = CdFileUtil.getResourceRealPath() + File.separatorChar
            + "data" + File.separatorChar + "dict" + File.separatorChar;
//  private static final String DB_URL = FOLDER_PATH + "dict.db"; // 数据库文件路径


    private static final String DB_URL = "jdbc:sqlite:" + FOLDER_PATH + "dict.db"; // 数据库文件路径
//  private static final String EXCEL_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C01_初中词汇正序版.xlsx"; // Excel 文件路径
    //private static final String tableName = "C01_初中词汇正序版"; // 表名


    /**
     * 创建 SQLite 表
     *
     * @return 创建表所用时间
     */
    public static String createTable(final String tableName) {
        String CREATE_TABLE_SQL =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                        + "    单词 TEXT,\n"
                        + "    英音 TEXT,\n"
                        + "    美音 TEXT,\n"
                        + "    释义 TEXT,\n"
                        + "    等级 TEXT\n"
                        + ");"; // 创建表 SQL 语句

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("开始创建表: {}", tableName);
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // 创建表
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
                    wordEntity.setWord(rs.getString("单词"));
                    wordEntity.setUk(rs.getString("英音"));
                    wordEntity.setUs(rs.getString("美音"));
                    wordEntity.setComment(rs.getString("释义"));
                    wordEntity.setLevel(rs.getString("等级"));
                    wordList.add(wordEntity);
                }
                log.info("成功从表 {} 读取 {} 条数据到 WordEntity 对象列表", tableName, wordList.size());

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
     * 根据提供的单词列表，从词汇总表中查询对应的记录。
     *
     * @param words 要查询的单词列表
     * @return 包含查询结果的 WordEntity 对象列表
     */
//  public static List<WordEntity> findWordsInSummaryTable(List<String> words) {
//    StopWatch stopWatch = new StopWatch();
//    stopWatch.start();
//    List<WordEntity> wordList = new ArrayList<>();
//    log.info("开始从词汇总表中查询单词...");
//
//    // 构建 IN 子句的字符串
//    String inClause = String.join("','", words);
//    inClause = "'" + inClause + "'";
//
//    String sql = "SELECT * FROM 词汇总表 WHERE 单词 IN (" + inClause + ")";
//    log.debug("SQL: {}", sql);
//
//    try (Connection conn = DriverManager.getConnection(DB_URL);
//      PreparedStatement pstmt = conn.prepareStatement(sql)) {
//      // 不需要设置参数，因为 IN 子句已经包含在 SQL 语句中了
//
//      try (ResultSet rs = pstmt.executeQuery()) {
//        while (rs.next()) {
//          WordEntity wordEntity = new WordEntity();
//          wordEntity.setWord(rs.getString("单词"));
//          wordEntity.setUk(rs.getString("英音"));
//          wordEntity.setUs(rs.getString("美音"));
//          wordEntity.setCn(rs.getString("释义"));
//          wordEntity.setLevel(rs.getString("等级"));
//          wordList.add(wordEntity);
//        }
//      }
//      log.info("从词汇总表中查询到 {} 条记录", wordList.size());
//
//    } catch (SQLException e) {
//      log.error("从词汇总表中查询单词失败: {}", e.getMessage(), e);
//      return null; // 或者抛出自定义异常
//    } finally {
//      stopWatch.stop();
//      log.info("从词汇总表中查询单词结束，耗时: {}", formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS)));
//    }
//
//    return wordList;
//  }

    /**
     * 根据提供的单词列表，从词汇总表中查询对应的记录。
     * 使用 PreparedStatement 和 动态 IN 子句，避免 SQL 注入，提高效率。
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
        String placeholders = String.join(",", Collections.nCopies(words.size(), "?"));

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
            log.info("从词汇总表中查询单词结束，耗时: {}", formatElapsedTime(stopWatch.getTime(TimeUnit.MILLISECONDS)));
        }

        return wordList;
    }

    public static void initData() {
        // 创建数据库目录（如果不存在）
        File dbFile = new File(
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/dict.db");
        File parentDir = dbFile.getParentFile();
        if (!parentDir.exists()) {
            try {
                Files.createDirectories(Paths.get(parentDir.getAbsolutePath()));
            } catch (Exception e) {
                log.error("创建目录失败: {}", e.getMessage(), e);
            }
        }
        //  String tableName = "C01_初中词汇正序版"; // 表名

        List<String> tableNames = Arrays.asList("C01_初中词汇正序版", "C02_高中英语词汇正序版",
                "C03_四级词汇正序版",
                "C04_六级词汇正序版",
                "C05_2013考研词汇正序版",
                "C06_雅思词汇正序版");

        String EXCEL_PATH = "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C01_初中词汇正序版.xlsx"; // Excel 文件路径
        List<String> excelPaths = Arrays.asList(
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C01_初中词汇正序版.xlsx",
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C02_高中英语词汇正序版.xlsx",
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C03_四级词汇正序版.xlsx",
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C04_六级词汇正序版.xlsx",
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C05_2013考研词汇正序版.xlsx",
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/C06_雅思词汇正序版.xlsx"
        );
//    for (String tableName : tableNames) {
//      System.out.println("正在处理表: " + tableName);
//
//      String createTableTime = createTable(tableName);
//      System.out.println("创建表耗时: " + createTableTime);
//
//      String importDataTime = importDataFromExcel(EXCEL_PATH, tableName);
//      System.out.println("导入数据耗时: " + importDataTime);
//    }

        int i = 0;
        for (String tableName : tableNames) {
            System.out.println("正在处理表: " + tableName);

            String createTableTime = createTable(tableName);
            System.out.println("创建表耗时: " + createTableTime);

            String importDataTime = importDataFromExcel(tableName, excelPaths.get(i++));
            System.out.println("导入数据耗时: " + importDataTime);

        }
    }

    public static void initTableData(String tableName) {
        // 创建数据库目录（如果不存在）
        File dbFile = new File(
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/dict.db");
        File parentDir = dbFile.getParentFile();
        if (!parentDir.exists()) {
            try {
                Files.createDirectories(Paths.get(parentDir.getAbsolutePath()));
            } catch (Exception e) {
                log.error("创建目录失败: {}", e.getMessage(), e);
            }
        }

        List<String> tableNames = Arrays.asList("C01_初中词汇正序版", "C02_高中英语词汇正序版",
                "C03_四级词汇正序版",
                "C04_六级词汇正序版",
                "C05_2013考研词汇正序版",
                "C06_雅思词汇正序版");

        String excelPath =
                "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/" + tableName + ".xlsx";

        System.out.println("正在处理表: " + tableName);

        String createTableTime = createTable(tableName);
        System.out.println("创建表耗时: " + createTableTime);

        String importDataTime = importDataFromExcel(tableName, excelPath);
        System.out.println("导入数据耗时: " + importDataTime);

    }

    /**
     * 创建数据库和表，并导入数据
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        initData();
    }

}
