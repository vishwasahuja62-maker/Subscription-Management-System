package com.subscription.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens MySQL connections using environment variables so credentials are not
 * stored in source control.
 */
public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/subscription_management?useSSL=false&serverTimezone=UTC";

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        String username = System.getenv("SMS_DB_USERNAME");
        String password = System.getenv("SMS_DB_PASSWORD");

        if (username == null || username.trim().isEmpty()) {
            throw new SQLException("SMS_DB_USERNAME is not configured.");
        }
        if (password == null) {
            throw new SQLException("SMS_DB_PASSWORD is not configured.");
        }

        String url = System.getenv("SMS_DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = DEFAULT_URL;
        }
        return DriverManager.getConnection(url, username, password);
    }
}
