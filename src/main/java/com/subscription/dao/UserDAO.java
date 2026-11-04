package com.subscription.dao;

import com.subscription.model.User;
import com.subscription.security.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password_hash, password_salt, role, email, active "
                + "FROM users WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !result.getBoolean("active")) {
                    return null;
                }

                String salt = result.getString("password_salt");
                String expectedHash = result.getString("password_hash");
                if (!PasswordUtil.matches(password, salt, expectedHash)) {
                    return null;
                }

                return new User(
                        result.getInt("id"),
                        result.getString("username"),
                        "",
                        result.getString("role"),
                        result.getString("email"),
                        true);
            }
        }
    }
}
