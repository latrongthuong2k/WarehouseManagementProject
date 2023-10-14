package org.example.Util;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    public static java.sql.Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/projectMD3_Db";
        String username = "root";
        String password = "thuong191020";
        String DRIVER = "com.mysql.cj.jdbc.Driver";

        Class.forName(DRIVER);
        return DriverManager.getConnection(url, username, password);
    }
}
