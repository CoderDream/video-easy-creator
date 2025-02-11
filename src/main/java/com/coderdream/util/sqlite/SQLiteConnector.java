package com.coderdream.util.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {

  public static void main(String[] args) {
    Connection connection = null;

//    org.sqlite.JDBC jdbc = new org.sqlite.JDBC();
    try {
      // 加载SQLite JDBC驱动程序
      Class.forName("org.sqlite.JDBC");
      // 创建数据库连接 D:\04_GitHub\video-easy-creator\src\main\resources\data\dict\db2.db
      connection = DriverManager.getConnection(
        "jdbc:sqlite:D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/db2.db");
      System.out.println("成功连接到SQLite数据库！");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    } finally {
      // 关闭数据库连接
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
