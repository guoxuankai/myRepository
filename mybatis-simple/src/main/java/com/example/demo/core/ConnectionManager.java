package com.example.demo.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(
                Config.DEFAULT.getUrl(),
                Config.DEFAULT.getUser(),
                Config.DEFAULT.getPwd()
        );
    }
}
