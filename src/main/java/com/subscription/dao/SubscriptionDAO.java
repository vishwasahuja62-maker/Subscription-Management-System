package com.subscription.dao;

import com.subscription.model.*;
import java.sql.*;
import java.util.*;

/**
 * SubscriptionDAO — Data Access Object for Subscriptions.
 *
 * SQL schema (reference):
 * ──────────────────────────────────────────────────────────────────
 * CREATE TABLE subscriptions (
 *     id            INT AUTO_INCREMENT PRIMARY KEY,
 *     customer_id   INT  NOT NULL,
 *     plan_id       INT  NOT NULL,
 *     start_date    DATE NOT NULL,
 *     end_date      DATE NOT NULL,
 *     status        ENUM('Active','Expiring','Inactive') DEFAULT 'Active',
 *     auto_renewal  TINYINT(1) DEFAULT 0,
 *     created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
 *     FOREIGN KEY (plan_id)     REFERENCES plans(id)
 * );
 * ──────────────────────────────────────────────────────────────────
 */
public class SubscriptionDAO {

    private Connection getConnection() throws SQLException {
        // TODO: return DBConnection.getConnection();
        throw new UnsupportedOperationException("JDBC not yet configured.");
    }

    public List<Subscription> getAllSubscriptions() throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS cust_name, p.plan_name " +
                     "FROM subscriptions s " +
                     "JOIN customers c ON s.customer_id = c.id " +
                     "JOIN plans     p ON s.plan_id     = p.id " +
                     "ORDER BY s.id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int addSubscription(Subscription sub) throws SQLException {
        String sql = "INSERT INTO subscriptions (customer_id, plan_id, start_date, end_date, status, auto_renewal) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,     sub.getCustomer().getId());
            ps.setInt(2,     sub.getPlan().getId());
            ps.setString(3,  sub.getStartDate());
            ps.setString(4,  sub.getEndDate());
            ps.setString(5,  sub.getStatus());
            ps.setBoolean(6, sub.isAutoRenewal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateSubscription(Subscription sub) throws SQLException {
        String sql = "UPDATE subscriptions SET customer_id=?, plan_id=?, start_date=?, end_date=?, status=?, auto_renewal=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,     sub.getCustomer().getId());
            ps.setInt(2,     sub.getPlan().getId());
            ps.setString(3,  sub.getStartDate());
            ps.setString(4,  sub.getEndDate());
            ps.setString(5,  sub.getStatus());
            ps.setBoolean(6, sub.isAutoRenewal());
            ps.setInt(7,     sub.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSubscription(int id) throws SQLException {
        String sql = "DELETE FROM subscriptions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Subscription mapRow(ResultSet rs) throws SQLException {
        Subscription s = new Subscription();
        s.setId(rs.getInt("id"));
        Customer c = new Customer(); c.setId(rs.getInt("customer_id")); c.setName(rs.getString("cust_name"));
        Plan     p = new Plan();     p.setId(rs.getInt("plan_id"));     p.setPlanName(rs.getString("plan_name"));
        s.setCustomer(c); s.setPlan(p);
        s.setStartDate(rs.getString("start_date"));
        s.setEndDate(rs.getString("end_date"));
        s.setStatus(rs.getString("status"));
        s.setAutoRenewal(rs.getBoolean("auto_renewal"));
        return s;
    }
}
