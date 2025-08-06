package com.coderdream.util.db;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseSchemaExporter {

    // --- 请根据你的数据库配置修改以下常量 ---
    private static final String DB_URL = "jdbc:mysql://192.168.1.37:3306/ry-vue?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_SCHEMA_NAME = "ry-vue"; // 对于MySQL，通常与数据库名相同
    private static final String OUTPUT_FILE_PATH = "database_schema_export_scs_tables.xlsx";
    private static final String TABLE_NAME_PREFIX_FILTER = "scs_"; // 新增：表名前缀过滤器
    // --- 配置结束 ---

    private static final String[] EXCEL_COLUMN_HEADERS = {"字段名称", "数据类型", "描述", "是否主键", "外键关联", "示例数据"};

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8.x driver
            // Class.forName("com.mysql.jdbc.Driver"); // MySQL 5.x driver
        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            exportSchemaToExcel(connection, DB_SCHEMA_NAME, OUTPUT_FILE_PATH);
            System.out.println("Excel 文件导出成功 (仅含 " + TABLE_NAME_PREFIX_FILTER + " 开头的表): " + OUTPUT_FILE_PATH);
        } catch (SQLException e) {
            System.err.println("数据库连接或操作失败: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Excel 文件写入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportSchemaToExcel(Connection connection, String schemaName, String outputFilePath) throws SQLException, IOException {
        DatabaseMetaData metaData = connection.getMetaData();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("数据库表结构");

        // 字体和样式
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setFont(headerFont);
        tableHeaderStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle columnHeaderStyle = workbook.createCellStyle();
        columnHeaderStyle.setFont(headerFont);
        columnHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        columnHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        columnHeaderStyle.setBorderBottom(BorderStyle.THIN);
        columnHeaderStyle.setBorderTop(BorderStyle.THIN);
        columnHeaderStyle.setBorderLeft(BorderStyle.THIN);
        columnHeaderStyle.setBorderRight(BorderStyle.THIN);
        columnHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        dataCellStyle.setWrapText(true);

        int rowIndex = 0;
        int tableIndex = 1;

        // 获取所有以指定前缀开头的表名和表注释
        Map<String, String> tableComments = getTableComments(connection, schemaName, TABLE_NAME_PREFIX_FILTER);

        if (tableComments.isEmpty()) {
            System.out.println("在数据库 '" + schemaName + "' 中没有找到以 '" + TABLE_NAME_PREFIX_FILTER + "' 开头的表。");
            Row noTablesRow = sheet.createRow(rowIndex++);
            noTablesRow.createCell(0).setCellValue("在数据库 '" + schemaName + "' 中没有找到以 '" + TABLE_NAME_PREFIX_FILTER + "' 开头的表。");
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, EXCEL_COLUMN_HEADERS.length -1));
        } else {
            System.out.println("找到 " + tableComments.size() + " 个以 '" + TABLE_NAME_PREFIX_FILTER + "' 开头的表进行导出。");
        }

        for (Map.Entry<String, String> tableEntry : tableComments.entrySet()) {
            String tableName = tableEntry.getKey();
            String tableComment = tableEntry.getValue() != null ? tableEntry.getValue() : "";

            // 1. 表头：表X：table_name (中文名)
            Row tableTitleRow = sheet.createRow(rowIndex++);
            Cell tableTitleCell = tableTitleRow.createCell(0);
            tableTitleCell.setCellValue("表" + tableIndex + "：" + tableName + " (" + tableComment + ")");
            tableTitleCell.setCellStyle(tableHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, EXCEL_COLUMN_HEADERS.length - 1));
            rowIndex++; // 空一行

            // 2. 字段列头
            Row columnHeaderRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < EXCEL_COLUMN_HEADERS.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(EXCEL_COLUMN_HEADERS[i]);
                cell.setCellStyle(columnHeaderStyle);
            }

            // 获取主键列
            Set<String> primaryKeys = getPrimaryKeys(metaData, schemaName, tableName);
            // 获取外键信息
            Map<String, String> foreignKeys = getForeignKeys(metaData, schemaName, tableName);

            // 3. 字段详情
            String sql = "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT, COLUMN_KEY, EXTRA " +
                         "FROM INFORMATION_SCHEMA.COLUMNS " +
                         "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
                         "ORDER BY ORDINAL_POSITION";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, schemaName);
                ps.setString(2, tableName);
                try (ResultSet rsColumns = ps.executeQuery()) {
                    while (rsColumns.next()) {
                        Row dataRow = sheet.createRow(rowIndex++);
                        String columnName = rsColumns.getString("COLUMN_NAME");
                        String columnType = rsColumns.getString("COLUMN_TYPE").toUpperCase();
                        String columnCommentText = rsColumns.getString("COLUMN_COMMENT");
                        String columnKey = rsColumns.getString("COLUMN_KEY");
                        String extra = rsColumns.getString("EXTRA");

                        if (extra != null && extra.toLowerCase().contains("auto_increment")) {
                            columnType += " AUTO_INCREMENT";
                        }

                        boolean isPrimaryKey = "PRI".equalsIgnoreCase(columnKey) || primaryKeys.contains(columnName);
                        String fkInfo = foreignKeys.getOrDefault(columnName, "");

                        if (columnCommentText != null && (columnCommentText.contains("字典") || columnCommentText.contains("dict"))) {
                            if (!fkInfo.isEmpty()) {
                                fkInfo += "\n";
                            }
                            int dictIndex = columnCommentText.toLowerCase().indexOf("字典");
                          if (dictIndex == -1) {
                            dictIndex = columnCommentText.toLowerCase()
                              .indexOf("dict");
                          }

                            int startIndex = Math.max(0, columnCommentText.lastIndexOf("(", dictIndex));
                            startIndex = Math.max(startIndex, columnCommentText.lastIndexOf("（", dictIndex));
                            int endIndex = columnCommentText.indexOf(")", dictIndex);
                          if (endIndex == -1) {
                            endIndex = columnCommentText.indexOf("）",
                              dictIndex);
                          }

                            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                                fkInfo += "(或 " + columnCommentText.substring(startIndex, endIndex + 1).trim() + ")";
                            } else {
                                fkInfo += "(或 字典: " + columnCommentText + ")";
                            }
                        }

                        dataRow.createCell(0).setCellValue(columnName);
                        dataRow.createCell(1).setCellValue(columnType);
                        dataRow.createCell(2).setCellValue(columnCommentText);
                        dataRow.createCell(3).setCellValue(isPrimaryKey ? "是" : "否");
                        dataRow.createCell(4).setCellValue(fkInfo);
                        dataRow.createCell(5).setCellValue(getExampleData(columnName, columnType, columnCommentText));

                        for (int i = 0; i < EXCEL_COLUMN_HEADERS.length; i++) {
                            if(dataRow.getCell(i) != null) {
                                dataRow.getCell(i).setCellStyle(dataCellStyle);
                            } else {
                                dataRow.createCell(i).setCellStyle(dataCellStyle);
                            }
                        }
                    }
                }
            }

            rowIndex++;
            tableIndex++;
        }

        // 自动调整列宽
        if (!tableComments.isEmpty()) {
            for (int i = 0; i < EXCEL_COLUMN_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024);
                if (i == 2 || i == 4) {
                     if (sheet.getColumnWidth(i) > 200 * 256) {
                        sheet.setColumnWidth(i, 200 * 256);
                    }
                }
            }
        }


        try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    /**
     * 获取指定 schema 下，表名以特定前缀开头的表及其注释。
     * @param connection 数据库连接
     * @param schemaName Schema 名称
     * @param tableNamePrefix 表名前缀过滤器 (例如 "scs_")
     * @return 表名 -> 表注释 的 Map
     * @throws SQLException SQL异常
     */
    private static Map<String, String> getTableComments(Connection connection, String schemaName, String tableNamePrefix) throws SQLException {
        Map<String, String> tableComments = new LinkedHashMap<>();
        // 对于MySQL，可以直接查 INFORMATION_SCHEMA.TABLES
        String sql = "SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE TABLE_SCHEMA = ? AND TABLE_NAME LIKE ? ORDER BY TABLE_NAME";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, schemaName);
            ps.setString(2, tableNamePrefix + "%"); // 使用 LIKE 操作符
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableComments.put(rs.getString("TABLE_NAME"), rs.getString("TABLE_COMMENT"));
                }
            }
        }
        // 如果上面的查询不适用于您的数据库（例如非MySQL），或者需要更通用的方式，可以回退到 DatabaseMetaData
        // 注意：DatabaseMetaData 的 REMARKS 列可能不总是表注释，取决于驱动
        if (tableComments.isEmpty() && !(connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("MySQL"))) {
            System.out.println("尝试使用 DatabaseMetaData 获取表信息...");
            DatabaseMetaData metaData = connection.getMetaData();
            // catalog 参数对于 MySQL 可能是 null 或 schemaName，对于其他数据库可能不同
            String catalog = null;
            try{ // 有些驱动在getSchemaTerm不被支持时会抛异常
              if (metaData.getDatabaseProductName()
                .equalsIgnoreCase("Oracle")) {
                catalog = metaData.getUserName();
              }
                // else catalog = schemaName; // 很多驱动用 catalog 代表 schema
            } catch(Exception e){/*ignore*/}

            try (ResultSet rs = metaData.getTables(catalog, schemaName, tableNamePrefix + "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    // 确保只添加符合前缀的表，尽管 pattern 应该已经处理了
                    if (tableName.startsWith(tableNamePrefix)) {
                        String tableComment = rs.getString("REMARKS");
                        tableComments.put(tableName, tableComment);
                    }
                }
            }
             // 对 LinkedHashMap 按键（表名）排序，如果 DatabaseMetaData 返回的不是有序的
            if (!tableComments.isEmpty()) {
                List<Map.Entry<String, String>> list = new ArrayList<>(tableComments.entrySet());
                list.sort(Map.Entry.comparingByKey());
                tableComments.clear();
                for (Map.Entry<String, String> entry : list) {
                    tableComments.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return tableComments;
    }


    private static Set<String> getPrimaryKeys(DatabaseMetaData metaData, String schemaName, String tableName) throws SQLException {
        Set<String> primaryKeys = new HashSet<>();
        String catalog = null;
        try{
          if (metaData.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
            catalog = metaData.getUserName();
          }
        } catch(Exception e){/*ignore*/}
        try (ResultSet rs = metaData.getPrimaryKeys(catalog, schemaName, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        }
        return primaryKeys;
    }

    private static Map<String, String> getForeignKeys(DatabaseMetaData metaData, String schemaName, String tableName) throws SQLException {
        Map<String, String> foreignKeys = new HashMap<>();
        String catalog = null;
        try{
          if (metaData.getDatabaseProductName().equalsIgnoreCase("Oracle")) {
            catalog = metaData.getUserName();
          }
        } catch(Exception e){/*ignore*/}
        try (ResultSet rs = metaData.getImportedKeys(catalog, schemaName, tableName)) {
            while (rs.next()) {
                String fkColumnName = rs.getString("FKCOLUMN_NAME");
                String pkTableName = rs.getString("PKTABLE_NAME");
                String pkColumnName = rs.getString("PKCOLUMN_NAME");
                String fkInfo = pkTableName + "." + pkColumnName;
                foreignKeys.merge(fkColumnName, fkInfo, (oldVal, newVal) -> oldVal + "\n" + newVal);
            }
        }
        return foreignKeys;
    }

  /**
   * 生成示例数据。这个实现会根据字段名、类型、注释等信息生成示例数据。
   * 对于日期和时间类型，会使用当前日期/时间。
   * @param columnName 字段名
   * @param columnType 字段类型 (从 INFORMATION_SCHEMA.COLUMNS 获取的原始类型)
   * @param columnComment 字段描述
   * @return 示例数据字符串
   */
  private static String getExampleData(String columnName, String columnType, String columnComment) {
    String lowerColumnName = columnName.toLowerCase();
    String upperColumnType = columnType.toUpperCase(); // 确保比较时大小写一致

    if (lowerColumnName.endsWith("_id") || lowerColumnName.equals("id")) return "1";
    if (lowerColumnName.contains("name")) return "示例名称";
    if (lowerColumnName.contains("number") || lowerColumnName.contains("code")) return "CODE001";
    if (lowerColumnName.contains("phone") || lowerColumnName.contains("mobile")) return "13800138000";
    if (lowerColumnName.contains("email")) return "example@example.com";
    if (lowerColumnName.contains("url")) return "https://www.example.com";
    if (lowerColumnName.contains("address")) return "示例地址XX路YY号";
    if (lowerColumnName.contains("desc") || lowerColumnName.contains("remark") || lowerColumnName.contains("comment")) return "这是一个示例描述。";


    // 日期和时间类型处理
    // 注意：这里的 startsWith 检查的是从 INFORMATION_SCHEMA.COLUMNS 获取的原始类型字符串
    // 例如 MySQL 的 DATE 是 "DATE", DATETIME 是 "DATETIME", TIMESTAMP 是 "TIMESTAMP"
    if (upperColumnType.equals("DATE")) {
      return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE); // 例如: "2025-06-05"
    }
    if (upperColumnType.equals("DATETIME") || upperColumnType.equals("TIMESTAMP")) {
      return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 例如: "2025-06-05 14:30:55"
    }
    // 有些数据库可能有 TIME 类型
    if (upperColumnType.equals("TIME")) {
      return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); // 例如: "14:30:55"
    }
    // 有些数据库可能有 YEAR 类型
    if (upperColumnType.equals("YEAR") || upperColumnType.equals("YEAR(4)")) {
      return String.valueOf(LocalDate.now().getYear()); // 例如: "2025"
    }


    // 根据数据类型（更通用的检查）
    if (upperColumnType.startsWith("VARCHAR") || upperColumnType.startsWith("CHAR") || upperColumnType.contains("TEXT")) return "示例文本";
    if (upperColumnType.startsWith("BIGINT") || upperColumnType.startsWith("INT") || upperColumnType.startsWith("SMALLINT") || upperColumnType.startsWith("TINYINT")) return "100";
    if (upperColumnType.startsWith("DECIMAL") || upperColumnType.startsWith("NUMERIC") || upperColumnType.startsWith("FLOAT") || upperColumnType.startsWith("DOUBLE")) return "123.45";
    if (upperColumnType.startsWith("BOOL") || upperColumnType.startsWith("BIT")) return "true"; //或者 "1" / "0" 取决于数据库习惯


    // 尝试从注释中提取示例，例如注释中含有 "例：XXX" 或 "e.g. XXX"
    if (columnComment != null) {
      String lowerComment = columnComment.toLowerCase();
      String[] exampleMarkers = {"例：", "例如：", "eg:", "e.g:", "示例："};
      for (String marker : exampleMarkers) {
        if (lowerComment.contains(marker)) {
          int startIndex = lowerComment.indexOf(marker) + marker.length();
          // 尝试找到示例的结束位置，可能是空格、分号、逗号或字符串末尾
          int endIndex = columnComment.length();
          String[] delimiters = {" ", "；", "，", ";", ","};
          for (String delimiter : delimiters) {
            int tempEndIndex = columnComment.indexOf(delimiter, startIndex);
            if (tempEndIndex != -1) {
              endIndex = Math.min(endIndex, tempEndIndex);
            }
          }
          return columnComment.substring(startIndex, endIndex).trim();
        }
      }
    }

    return ""; // 默认返回空字符串
  }

}
