package com.coderdream.util.sqlite;

import com.coderdream.entity.WordEntity;
import com.coderdream.util.sqlite.SQLiteUtil; // 假设这是你的SQLite工具类
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import swiss.ameri.gemini.api.GenAi.GeneratedContent;
import swiss.ameri.gemini.api.ModelVariant;
import com.coderdream.util.gemini.GeminiApiUtil; // 引入你的Gemini工具类


/**
 * 使用 Google Gemini API 更新 SQLite 数据库中 C06_雅思词汇正序版 表的工具类。
 * 此工具类从数据库读取数据，调用 Gemini API 补充美音和修改释义，然后更新回数据库。
 * 整合了自定义的Gemini API工具类。
 */
@Slf4j
public class SQLiteGeminiUpdaterPro {

    private final String dbPath;
    private static final String TABLE_NAME = "C06_雅思词汇正序版";
    private static final String WORD_COLUMN = "单词";
    private static final String UK_PHONETIC_COLUMN = "英音";
    private static final String US_PHONETIC_COLUMN = "美音";
    private static final String DEFINITION_COLUMN = "释义";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 构造函数
     *
     * @param dbPath SQLite 数据库文件路径
     */
    public SQLiteGeminiUpdaterPro(String dbPath) {
        this.dbPath = dbPath;
    }
    /**
     * 更新 C06_雅思词汇正序版 表的美音和释义字段。
     */
    public void updateUSTranslation() {
        int batchSize = 1000;
        int totalRecords = getRecordCount();
        if (totalRecords == -1) {
            return;
        }

        log.info("总记录数: {}", totalRecords);

        for (int offset = 0; offset < totalRecords; offset += batchSize) {
            try {
                log.info("正在处理批次：{} 到 {}", offset, offset + batchSize);
                List<WordData> batch = getWordDataBatch(offset, batchSize);
                if (batch.isEmpty()) {
                    log.info("批次为空，跳过。");
                    continue;
                }
                updateBatch(batch);
                log.info("批次处理完成：{} 到 {}", offset, offset + batchSize);

            } catch (Exception e) {
                log.error("处理批次时出错：{} 到 {}", offset, offset + batchSize, e);
                //根据需要决定是否继续
                return; // 直接退出
            }
            // 延迟1秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("线程中断", e);
            }
        }
    }

    /**
     * 从数据库中获取一批单词数据。
     */
    private List<WordData> getWordDataBatch(int offset, int limit) throws SQLException {
        List<WordData> wordDataList = new ArrayList<>();
        String sql = "SELECT 单词, 英音, 释义 FROM " + TABLE_NAME  + " WHERE \"" + US_PHONETIC_COLUMN + "\" IS NULL" + " LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String word = resultSet.getString(WORD_COLUMN);
                    String ukPhonetic = resultSet.getString(UK_PHONETIC_COLUMN);
                    String definition = resultSet.getString(DEFINITION_COLUMN);
                    wordDataList.add(new WordData(word, ukPhonetic, null, definition)); // 创建时不带美音
                }
            }
        }
        return wordDataList;
    }

    /**
     * 使用 Gemini API 更新一批单词的美音和释义，并更新回数据库。
     * @param batch 单词数据批次
     */
    private void updateBatch(List<WordData> batch) {
        // 构建批量请求的提示
        StringBuilder batchPrompt = new StringBuilder();
//        batchPrompt.append("请为以下英文单词补充美式音标，并修正和润色中文释义，使其更准确、地道，在释义的第二个词性前加上中文分号。请按以下格式返回结果(包括原单词)：\n\n");
        batchPrompt.append("请为以下英文单词补充美式音标（使用国际音标 IPA），并优化中文释义，使之更准确、地道。请严格按照以下格式返回结果（包括原单词）：\n\n");
        batchPrompt.append("单词\t英音\t美音\t释义\t等级\n");
        for (WordData wordData : batch) {
            batchPrompt.append(wordData.word).append("\t").append(wordData.ukPhonetic).append("\t\t")
                .append(wordData.definition).append("\t").append("C06\n");
        }

        batchPrompt.append("\n请严格按照以上格式和要求提供数据,不要增加任何其他的说明性文字。");
        log.info("请求参数为：{}", batchPrompt);

        try {
            // 使用你的 GeminiApiUtil 来调用 API
            GeneratedContent generatedContent = GeminiApiUtil.generateContent(batchPrompt.toString());

            // 检查是否成功生成了内容
            if (generatedContent != null && generatedContent.text() != null) {
               String responseText =  generatedContent.text();
                log.info("返回结果为：{}", responseText);
                // 解析响应并更新数据库
                parseAndUpdate(responseText);
            } else {
                log.error("Gemini API 响应不完整或出错: {}", generatedContent);
                if (generatedContent != null) {
                  log.error("错误信息: {}", generatedContent.finishReason());
                  log.error("安全评级: {}", generatedContent);
                }
            }

        } catch (Exception e) {
          log.error("调用Gemini API时发生错误",e);
        }
    }

    /**
     * 解析 Gemini API 的响应并更新数据库。
     */
    private void parseAndUpdate(String responseText) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            connection.setAutoCommit(false); // 开启事务

            String[] lines = responseText.split("\n");
            for (String line : lines) {
                //去除可能存在的markdown标记
//                line = line.replace("```", "").replace("text", "");
//                line = line.replace("```", "");
                if (line.trim().startsWith("单词") || line.trim().isEmpty()) {
                    continue; // 跳过标题行和空行
                }
                String[] parts = line.split("\t");
                if (parts.length >= 4) { // 确保有足够多的列
                    String word = parts[0].trim();
                    //String ukPhonetic = parts[1].trim(); // 此处不需要更新英音
                    String usPhonetic = parts[2].trim();
                    String definition = parts[3].trim();
                    // String level = parts[4].trim(); // 这里也不需要更新等级.

                    updateWordData(connection, word, usPhonetic, definition);
                } else {
                    log.warn("跳过无效行: {}", line);
                }
            }
            connection.commit(); // 提交事务
        } catch (SQLException e) {
            log.error("解析和更新数据库时出错", e);
            throw e; // 重新抛出异常，让调用者处理
        }
    }

    /**
     * 更新数据库中的单个单词数据。
     */
    private void updateWordData(Connection connection, String word, String usPhonetic,
        String definition) throws SQLException {
        String updateSQL = "UPDATE " + TABLE_NAME +
            " SET \"" + US_PHONETIC_COLUMN + "\" = ?, \"" + DEFINITION_COLUMN + "\" = ? " +
            "WHERE \"" + WORD_COLUMN + "\" = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, usPhonetic);
            preparedStatement.setString(2, definition);
            preparedStatement.setString(3, word);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                log.warn("没有更新任何行. 单词: {}", word);
            }
        }
    }

    /**
     * 获取表的总记录数。
     *
     * @return 总记录数，如果出错返回 -1
     */
    private int getRecordCount() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE \"" + US_PHONETIC_COLUMN + "\" IS NULL";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            log.error("获取记录数时出错", e);
        }
        return -1;
    }

    /**
     * 内部类，用于封装单词数据。
     */
    private static class WordData {

        String word;
        String ukPhonetic;
        String usPhonetic;
        String definition;

        public WordData(String word, String ukPhonetic, String usPhonetic, String definition) {
            this.word = word;
            this.ukPhonetic = ukPhonetic;
            this.usPhonetic = usPhonetic;
            this.definition = definition;
        }
    }


    public static void main(String[] args) {
        // 替换为你的实际参数
        String dbPath = "D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/dict.db";

        SQLiteGeminiUpdaterPro updater = new SQLiteGeminiUpdaterPro(dbPath);
        for (int i = 0; i < 10; i++) {
            updater.updateUSTranslation();
        }
    }
}
