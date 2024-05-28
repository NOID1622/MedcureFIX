package com.medcure.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static volatile DatabaseUtil instance = null;
    private final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private Connection connection;

    private DatabaseUtil() {
    }

    public static DatabaseUtil getInstance() {
        if (instance == null) {
            synchronized (DatabaseUtil.class) {
                if (instance == null) {
                    instance = new DatabaseUtil();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                // Handle database connection error
                throw new SQLException("Failed to establish database connection: " + e.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Handle database connection closure error
                System.err.println("Failed to close database connection: " + e.getMessage());
            }
        }
    }
}
