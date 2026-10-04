package com.subscription.dao;

import com.subscription.model.*;
import java.sql.*;
import java.util.*;

/**
 * PaymentDAO — Data Access Object for Payments.
 *
 * SQL schema (reference):
 * ──────────────────────────────────────────────────────────────────
 * CREATE TABLE payments (
 *     id              INT AUTO_INCREMENT PRIMARY KEY,
 *     customer_id     INT            NOT NULL,
 *     subscription_id INT            NOT NULL,
 *     amount          DECIMAL(10,2)  NOT NULL,
 *     payment_date    DATE           NOT NULL,
 *     payment_status  ENUM('Paid','Pending','Failed') DEFAULT 'Pending',
 *     created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     FOREIGN KEY (customer_id)     REFERENCES customers(id),
 *     FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
 * );
 * ──────────────────────────────────────────────────────────────────
 */
public class PaymentDAO {

    private Connection getConnection() throws SQLException {
        // TODO: return DBConnection.getConnection();
        throw new UnsupportedOperationException("JDBC not yet configured.");
    }

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT pay.*, c.name AS cust_name, p.plan_name " +
                     "FROM payments pay " +
                     "JOIN customers    c ON pay.customer_id     = c.id " +
                     "JOIN subscriptions s ON pay.subscription_id = s.id " +
                     "JOIN plans         p ON s.plan_id           = p.id " +
                     "ORDER BY pay.id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int addPayment(Payment pay) throws SQLException {
        String sql = "INSERT INTO payments (customer_id, subscription_id, amount, payment_date, payment_status) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    pay.getCustomer().getId());
            ps.setInt(2,    pay.getSubscription().getId());
            ps.setDouble(3, pay.getAmount());
            ps.setString(4, pay.getPaymentDate());
            ps.setString(5, pay.getPaymentStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean updatePayment(Payment pay) throws SQLException {
        String sql = "UPDATE payments SET customer_id=?, subscription_id=?, amount=?, payment_date=?, payment_status=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    pay.getCustomer().getId());
            ps.setInt(2,    pay.getSubscription().getId());
            ps.setDouble(3, pay.getAmount());
            ps.setString(4, pay.getPaymentDate());
            ps.setString(5, pay.getPaymentStatus());
            ps.setInt(6,    pay.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePayment(int id) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Total revenue from paid payments (for dashboard stat) */
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_status = 'Paid'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment pay = new Payment();
        pay.setId(rs.getInt("id"));
        Customer c = new Customer(); c.setId(rs.getInt("customer_id")); c.setName(rs.getString("cust_name"));
        pay.setCustomer(c);
        pay.setAmount(rs.getDouble("amount"));
        pay.setPaymentDate(rs.getString("payment_date"));
        pay.setPaymentStatus(rs.getString("payment_status"));
        return pay;
    }
}
