package com.subscription.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens MySQL connections. Checks:
 * 1. Environment variables (SMS_DB_USERNAME, SMS_DB_PASSWORD, SMS_DB_URL)
 * 2. Local uncommitted db.properties file on the classpath
 * Prevents plain-text passwords from ever being checked into source control.
 */
public final class DBConnection {

    private static final Properties FILE_PROPS = new Properties();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Log or let getConnection fail if driver isn't present
        }
        loadProperties();
    }

    private static void loadProperties() {
        // Try classpath first
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                FILE_PROPS.load(in);
                return;
            }
        } catch (Exception ignored) {}

        // Try direct file paths on workspace if classpath fails in SmartTomcat
        String[] candidatePaths = {
            "db.properties",
            "src/main/resources/db.properties",
            "WEB-INF/classes/db.properties"
        };
        for (String path : candidatePaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists() && file.isFile()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                    FILE_PROPS.load(fis);
                    return;
                } catch (Exception ignored) {}
            }
        }
    }

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/subscription_management?useSSL=false&serverTimezone=UTC";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        // Priority 1: Environment Variables
        String username = System.getenv("SMS_DB_USERNAME");
        String password = System.getenv("SMS_DB_PASSWORD");
        String url = System.getenv("SMS_DB_URL");

        // Priority 2: Local ignored db.properties file
        if (username == null || username.trim().isEmpty()) {
            username = FILE_PROPS.getProperty("db.username");
        }
        if (password == null) {
            password = FILE_PROPS.getProperty("db.password");
        }
        if (url == null || url.trim().isEmpty()) {
            url = FILE_PROPS.getProperty("db.url", DEFAULT_URL);
        }

        if (username == null || username.trim().isEmpty()) {
            throw new SQLException("Database username is not configured. Set SMS_DB_USERNAME or provide db.properties.");
        }
        if (password == null) {
            throw new SQLException("Database password is not configured. Set SMS_DB_PASSWORD or provide db.properties.");
        }

        return DriverManager.getConnection(url, username, password);
    }
}
