package com.example.roomease;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/com/example/roomease/roomease.db";

    /**
     * Opens and returns a new connection to the SQLite database.
     * Foreign key enforcement is enabled for every new connection.
     *
     * @return a new Connection object
     */
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DB_URL);
            try (var stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;"); // Enable foreign key constraints
            }
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            return null;
        }
    }
}
