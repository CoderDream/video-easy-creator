package com.coderdream.util.sqlite;

import com.coderdream.util.cd.CdFileUtil;

import java.io.File;
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
            String folderPath = CdFileUtil.getResourceRealPath() + File.separatorChar
                    + "data" + File.separatorChar + "dict" + File.separatorChar;
            String url = "jdbc:sqlite:" + folderPath + "db2.db";
            // "jdbc:sqlite:D:/04_GitHub/video-easy-creator/src/main/resources/data/dict/db2.db"
            System.out.println(url);
            connection = DriverManager.getConnection(url);
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
