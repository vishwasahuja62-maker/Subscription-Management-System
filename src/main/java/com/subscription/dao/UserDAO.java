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

    public boolean updatePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        String query = "SELECT password_hash, password_salt FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String currentSalt = rs.getString("password_salt");
                String currentHash = rs.getString("password_hash");
                if (!PasswordUtil.matches(currentPassword, currentSalt, currentHash)) {
                    return false;
                }
            }
        }

        String newSalt = PasswordUtil.newSalt();
        String newHash = PasswordUtil.hash(newPassword, newSalt);

        String updateSql = "UPDATE users SET password_hash = ?, password_salt = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setString(1, newHash);
            updateStmt.setString(2, newSalt);
            updateStmt.setInt(3, userId);
            return updateStmt.executeUpdate() > 0;
        }
    }
}
