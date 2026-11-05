package com.subscription.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public Map<String, Object> getRevenueReport(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT p.plan_name, COUNT(s.id) AS sub_count, "
                + "COALESCE(SUM(CASE WHEN pay.payment_status = 'Paid' THEN pay.amount ELSE 0 END), 0) AS revenue "
                + "FROM plans p "
                + "LEFT JOIN subscriptions s ON p.id = s.plan_id "
                + "LEFT JOIN payments pay ON s.id = pay.subscription_id "
                + "AND pay.payment_date BETWEEN ? AND ? "
                + "GROUP BY p.id, p.plan_name "
                + "ORDER BY revenue DESC";

        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalSubs = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    String planName = rs.getString("plan_name");
                    long subCount = rs.getLong("sub_count");
                    BigDecimal rev = rs.getBigDecimal("revenue");

                    row.put("planName", planName);
                    row.put("subscriptions", subCount);
                    row.put("revenue", rev);
                    rows.add(row);

                    totalRevenue = totalRevenue.add(rev != null ? rev : BigDecimal.ZERO);
                    totalSubs += subCount;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "Revenue Report");
        result.put("rows", rows);
        result.put("totalRevenue", totalRevenue);
        result.put("totalSubscriptions", totalSubs);
        return result;
    }

    public Map<String, Object> getSubscriptionReport(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT s.status, COUNT(*) AS count "
                + "FROM subscriptions s "
                + "WHERE s.start_date BETWEEN ? AND ? "
                + "GROUP BY s.status";

        List<Map<String, Object>> rows = new ArrayList<>();
        long total = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    String status = rs.getString("status");
                    long count = rs.getLong("count");
                    row.put("status", status);
                    row.put("count", count);
                    rows.add(row);
                    total += count;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "Subscription Report");
        result.put("rows", rows);
        result.put("totalCount", total);
        return result;
    }

    public Map<String, Object> getCustomerReport(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT c.id, c.name, c.email, c.phone, c.status, "
                + "COUNT(s.id) AS active_subs "
                + "FROM customers c "
                + "LEFT JOIN subscriptions s ON c.id = s.customer_id AND s.start_date BETWEEN ? AND ? "
                + "GROUP BY c.id, c.name, c.email, c.phone, c.status "
                + "ORDER BY c.name ASC";

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("name", rs.getString("name"));
                    row.put("email", rs.getString("email"));
                    row.put("phone", rs.getString("phone"));
                    row.put("status", rs.getString("status"));
                    row.put("subscriptions", rs.getLong("active_subs"));
                    rows.add(row);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "Customer Report");
        result.put("rows", rows);
        result.put("totalCustomers", rows.size());
        return result;
    }

    public Map<String, Object> getPaymentReport(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT p.id, c.name AS customer_name, pl.plan_name, "
                + "p.amount, p.payment_date, p.payment_status "
                + "FROM payments p "
                + "JOIN customers c ON p.customer_id = c.id "
                + "JOIN subscriptions s ON p.subscription_id = s.id "
                + "JOIN plans pl ON s.plan_id = pl.id "
                + "WHERE p.payment_date BETWEEN ? AND ? "
                + "ORDER BY p.payment_date DESC, p.id DESC";

        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    int id = rs.getInt("id");
                    String customer = rs.getString("customer_name");
                    String plan = rs.getString("plan_name");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    String date = rs.getDate("payment_date").toString();
                    String status = rs.getString("payment_status");

                    row.put("id", id);
                    row.put("customerName", customer);
                    row.put("planName", plan);
                    row.put("amount", amount);
                    row.put("paymentDate", date);
                    row.put("status", status);
                    rows.add(row);

                    totalAmount = totalAmount.add(amount != null ? amount : BigDecimal.ZERO);
                    if ("Paid".equalsIgnoreCase(status)) {
                        paidAmount = paidAmount.add(amount != null ? amount : BigDecimal.ZERO);
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "Payment Report");
        result.put("rows", rows);
        result.put("totalAmount", totalAmount);
        result.put("paidAmount", paidAmount);
        return result;
    }
}
