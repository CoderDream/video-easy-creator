package com.coderdream.util.sqlite;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * SQLite 词汇汇总工具类
 * 用于将多个 SQLite 词汇表合并成一个汇总表，并处理等级字段。
 * 确保 "单词" 列在汇总表中是唯一的。
 */
@Slf4j
public class SQLiteVocabularyMerger {

    private static final String SUMMARY_TABLE_NAME = "词汇总表";
    private static final String[] SOURCE_TABLE_NAMES = {
            "C01_初中词汇正序版",
            "C02_高中英语词汇正序版",
            "C03_四级词汇正序版",
            "C04_六级词汇正序版",
            "C05_2013考研词汇正序版",
            "C06_雅思词汇正序版"
    };
    private static final String WORD_COLUMN = "单词";
    private static final String LEVEL_COLUMN = "等级";


    /**
     * 将多个词汇表合并成一个汇总表。
     *
     * @param dbPath SQLite 数据库文件路径
     * @return 包含时、分、秒、毫秒的耗时字符串
     */
    public String mergeVocabularyTables(String dbPath) {
        long startTime = System.currentTimeMillis();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            createSummaryTable(connection);
            mergeData(connection);
        } catch (SQLException e) {
            log.error("合并词汇表时发生错误:", e);
            return null; // 或者抛出自定义异常
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        return CdTimeUtil.formatDuration(duration);
    }


    /**
     * 创建词汇总表, 并设置主键.
     *
     * @param connection 数据库连接
     * @throws SQLException 如果创建表失败
     */
    private void createSummaryTable(Connection connection) throws SQLException {
        // 使用与源表相同的结构创建汇总表, 并设置 单词 为主键.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS \"" + SUMMARY_TABLE_NAME + "\" (" +
                "\"单词\" TEXT PRIMARY KEY, " + // 设置主键
                "\"英音\" TEXT, " +
                "\"美音\" TEXT, " +
                "\"释义\" TEXT, " +
                "\"等级\" TEXT" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
            log.info("已创建或已存在词汇总表: {}, 并设置主键", SUMMARY_TABLE_NAME);
        }
    }

    /**
     * 合并数据到词汇总表
     *
     * @param connection 数据库连接
     * @throws SQLException
     */
    private void mergeData(Connection connection) throws SQLException {
        // 使用 LinkedHashMap 来保持插入顺序，并方便后续处理等级
        // Map<String, String> vocabularyMap = new LinkedHashMap<>(); // 不需要map了

        for (String tableName : SOURCE_TABLE_NAMES) {
            log.info("正在处理表: {}", tableName);
            String selectSQL = "SELECT * FROM \"" + tableName + "\"";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)) {

                while (resultSet.next()) {
                    String word = resultSet.getString(WORD_COLUMN);
                    String phoneticUK = resultSet.getString("英音");
                    String phoneticUS = resultSet.getString("美音");
                    String definition = resultSet.getString("释义");
                    String level = resultSet.getString(LEVEL_COLUMN);

                    // 先检查数据库中是否已存在该单词
                    if (!wordExists(connection, word)) {
                        insertData(connection, word, phoneticUK, phoneticUS, definition, level);
                    } else {
                        //如果存在, 则更新
                        String existingLevel = getExistingLevel(connection, word);
                        String newLevel = mergeLevels(existingLevel, level);  // 合并等级
                        updateExistingWord(connection, word, phoneticUK, phoneticUS, definition, newLevel);
                    }
                }
            }
            log.info("表 {} 处理完成", tableName);
        }
    }

    /**
     * 检查单词是否已存在于汇总表中。
     */
    private boolean wordExists(Connection connection, String word) throws SQLException {
        String sql = "SELECT COUNT(*) FROM \"" + SUMMARY_TABLE_NAME + "\" WHERE \"单词\" = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, word);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * 获取数据库里已经存在的单词的等级
     *
     * @param connection
     * @param word
     * @return
     * @throws SQLException
     */
    private String getExistingLevel(Connection connection, String word) throws SQLException {
        String existingLevel = "";
        String selectSQL = "SELECT 等级 FROM " + SUMMARY_TABLE_NAME + " WHERE 单词 = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, word);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    existingLevel = resultSet.getString("等级");
                    return existingLevel;
                }
            }
        }
        return existingLevel;
    }

    /**
     * 更新现有单词数据, 包括合并等级.
     *
     * @param connection
     * @param word
     * @param phoneticUK
     * @param phoneticUS
     * @param definition
     * @param level      合并后的等级
     * @throws SQLException
     */
    private void updateExistingWord(Connection connection, String word, String phoneticUK, String phoneticUS, String definition, String level)
            throws SQLException {
        //构建更新sql
        String updateSQL = "UPDATE \"" + SUMMARY_TABLE_NAME
                + "\" SET \"英音\" = ?, \"美音\" = ?, \"释义\" = ?, \"等级\" = ? WHERE \"单词\" = ?";
        //更新汇总表
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, phoneticUK);
            preparedStatement.setString(2, phoneticUS);
            preparedStatement.setString(3, definition);
            preparedStatement.setString(4, level); // 使用合并后的等级
            preparedStatement.setString(5, word);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * @param connection
     * @param word
     * @param phoneticUK
     * @param phoneticUS
     * @param definition
     * @param level
     */
    private void insertData(Connection connection, String word, String phoneticUK,
                            String phoneticUS, String definition, String level) throws SQLException {
        //构建插入sql
        String insertSQL = "INSERT INTO \"" + SUMMARY_TABLE_NAME
                + "\" (\"单词\", \"英音\", \"美音\", \"释义\", \"等级\") VALUES (?, ?, ?, ?, ?)";
        //插入汇总表
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                insertSQL)) {
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, phoneticUK);
            preparedStatement.setString(3, phoneticUS);
            preparedStatement.setString(4, definition);
            preparedStatement.setString(5, level);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * 合并等级，例如将 "C01" 和 "C02" 合并成 "C01,C02"
     *
     * @param existingLevel 已存在的等级
     * @param newLevel      新的等级
     * @return 合并后的等级
     */
    private String mergeLevels(String existingLevel, String newLevel) {
        if (existingLevel == null || existingLevel.trim().isEmpty()) {
            return newLevel;
        }
        if (newLevel == null || newLevel.trim().isEmpty()) {
            return existingLevel;
        }

        Set<String> levels = new TreeSet<>(Arrays.asList(existingLevel.split(",")));
        levels.addAll(Arrays.asList(newLevel.split(",")));

        return String.join(",", levels);
    }


    /**
     * 将毫秒数格式化为时分秒毫秒的字符串。
     *
     * @param duration 毫秒数
     * @return 格式化后的字符串
     */
    private String formatDuration(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
        long milliseconds = duration % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    private static final String FOLDER_PATH = CdFileUtil.getResourceRealPath() + File.separatorChar
            + "data" + File.separatorChar + "dict" + File.separatorChar;
    private static final String DB_URL = FOLDER_PATH + "dict.db"; // 数据库文件路径

    public static void main(String[] args) {
        SQLiteVocabularyMerger merger = new SQLiteVocabularyMerger();
//        String dbPath = "your_database_file.db"; // 替换为你的数据库文件路径
        String duration = merger.mergeVocabularyTables(DB_URL);
        if (duration != null) {
            log.info("词汇表合并完成，耗时: {}", duration);
        }
    }
}
