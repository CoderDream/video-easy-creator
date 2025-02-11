/*
package com.coderdream.util.sqlite;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.api.Candidate.FinishReason;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

*/
/**
 * 使用 Google Gemini API 更新 SQLite 数据库中 C06_雅思词汇正序版 表的工具类。
 * 此工具类从数据库读取数据，调用 Gemini API 补充美音和修改释义，然后更新回数据库。
 *//*

@Slf4j
public class SQLiteGeminiUpdater {

    private final String dbPath;
    private final String projectId;
    private final String location;
    private final String modelName;
    private static final String TABLE_NAME = "C06_雅思词汇正序版";
    private static final String WORD_COLUMN = "单词";
    private static final String UK_PHONETIC_COLUMN = "英音";
    private static final String US_PHONETIC_COLUMN = "美音";
    private static final String DEFINITION_COLUMN = "释义";

    */
/**
     * 构造函数
     *
     * @param dbPath    SQLite 数据库文件路径
     * @param projectId Google Cloud 项目 ID
     * @param location  Google Cloud 位置 (例如 "us-central1")
     * @param modelName Gemini 模型名称 (例如 "gemini-1.5-pro-002")
     *//*

    public SQLiteGeminiUpdater(String dbPath, String projectId, String location, String modelName) {
        this.dbPath = dbPath;
        this.projectId = projectId;
        this.location = location;
        this.modelName = modelName;
    }

    */
/**
     * 更新 C06_雅思词汇正序版 表的美音和释义字段。
     *//*

    public void updateUSTranslation() {
        int batchSize = 1000;
        int totalRecords = getRecordCount(); // 获取总记录数
        if (totalRecords == -1) {
            return; // 或者抛出异常
        }

        log.info("总记录数: {}", totalRecords);

        for (int offset = 0; offset < totalRecords; offset += batchSize) {
            try {
                log.info("正在处理批次：{} 到 {}", offset, offset + batchSize);
                List<WordData> batch = getWordDataBatch(offset, batchSize); //从数据库中读取数据
                if (batch.isEmpty()) {
                    log.info("批次为空，跳过。");
                    continue; // 跳过空批次
                }
                updateBatch(batch);    //更新数据
                log.info("批次处理完成：{} 到 {}", offset, offset + batchSize);

            } catch (Exception e) {
                log.error("处理批次时出错：{} 到 {}", offset, offset + batchSize, e);
                // 可选：根据需要决定是否继续处理其他批次或直接退出
                // continue; // 继续下一个批次
                return; //  或者直接退出
            }
            // 可以考虑加一个延迟，避免请求过于频繁。
            try {
                TimeUnit.SECONDS.sleep(1); // 暂停1秒,防止调用过于频繁
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("线程中断", e);
            }
        }
    }

    */
/**
     * 从数据库中获取一批单词数据。
     *
     * @param offset   偏移量
     * @param limit    每批数量限制
     * @return 单词数据列表
     *//*

    private List<WordData> getWordDataBatch(int offset, int limit) throws SQLException {
        List<WordData> wordDataList = new ArrayList<>();
        String sql = "SELECT 单词, 英音, 释义 FROM " + TABLE_NAME + " LIMIT ? OFFSET ?";

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

    */
/**
     * 使用 Gemini API 更新一批单词的美音和释义，并更新回数据库。
     *
     * @param batch 单词数据批次
     *//*

    private void updateBatch(List<WordData> batch) throws IOException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)  // 增加最大输出tokens
                            .setTemperature(0.2F) // 降低温度
                            .setTopK(16) //降低TopK
                            .build();

            GenerativeModel model = new GenerativeModel(modelName, generationConfig, vertexAI);

            // 构建批量请求的提示
            StringBuilder batchPrompt = new StringBuilder();
            batchPrompt.append("请为以下英文单词补充美式音标，并修正和润色中文释义，使其更准确、地道，在释义的第二个词性前加上中文分号。请按以下格式返回结果(包括原单词)：\n\n");
            batchPrompt.append("单词\t英音\t美音\t释义\t等级\n");
            for (WordData wordData : batch) {
                 batchPrompt.append(wordData.word).append("\t").append(wordData.ukPhonetic).append("\t\t").append(wordData.definition).append("\t").append("C06\n");
            }

             batchPrompt.append("\n请严格按照以上格式和要求提供数据,不要增加任何其他的说明性文字。");
            log.info("请求参数为：{}",batchPrompt.toString());
            GenerateContentResponse response = model.generateContent(Content.newBuilder()
                            .addParts(Part.newBuilder().setText(batchPrompt.toString()))
                            .build());

            // 检查是否成功生成了内容
            if (response.getCandidatesCount() > 0 && response.getCandidates(0).getFinishReason() == FinishReason.STOP) {
                String responseText = response.getCandidates(0).getContent().getParts(0).getText();
                // 解析响应并更新数据库
                parseAndUpdate(responseText);
            } else {
                log.error("Gemini API 响应不完整或出错: {}", response);
                if (response.getCandidatesCount() > 0)
                {
                    log.error("Finish Reason: {}", response.getCandidates(0).getFinishReason());
                    if(response.getCandidates(0).getSafetyRatingsList()!=null && !response.getCandidates(0).getSafetyRatingsList().isEmpty()) {
                        log.error("Safety Ratings: {}", response.getCandidates(0).getSafetyRatingsList());
                    }
                }

            }
        }
    }

    */
/**
     * 解析 Gemini API 的响应并更新数据库。
     *
     * @param responseText Gemini API 的响应文本
     *//*

    private void parseAndUpdate(String responseText) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            connection.setAutoCommit(false); // 开启事务

            String[] lines = responseText.split("\n");
            for (String line : lines) {
              //去除可能存在的markdown标记
              line = line.replace("```","").replace("text","");
                if (line.trim().startsWith("单词") || line.trim().isEmpty()) {
                    continue; // 跳过标题行和空行
                }
                String[] parts = line.split("\t");
                if (parts.length >= 4) { // 确保有足够多的列
                    String word = parts[0].trim();
                    //String ukPhonetic = parts[1].trim(); // 这里不需要更新英音
                    String usPhonetic = parts[2].trim();
                    String definition = parts[3].trim();
                    //String level = parts[4].trim(); // 这里也不需要更新等级.

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

    */
/**
     * 更新数据库中的单个单词数据。
     *
     * @param connection 数据库连接
     * @param word       单词
     * @param usPhonetic 美音
     * @param definition 释义
     *//*

    private void updateWordData(Connection connection, String word, String usPhonetic, String definition) throws SQLException {
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

    */
/**
     * 获取表的总记录数。
     *
     * @return 总记录数，如果出错返回 -1
     *//*

    private int getRecordCount() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
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
    */
/**
     * 内部类，用于封装单词数据。
     *//*

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
        String dbPath = "your_database.db"; // 你的 SQLite 数据库路径
        String projectId = "your-project-id"; // 你的 Google Cloud 项目 ID
        String location = "us-central1"; // 或者其他合适的区域
        String modelName = "gemini-1.5-pro-002"; // 或者其他 Gemini 模型

        SQLiteGeminiUpdater updater = new SQLiteGeminiUpdater(dbPath, projectId, location, modelName);
        updater.updateUSTranslation(); // 调用方法开始更新
    }
}
*/
