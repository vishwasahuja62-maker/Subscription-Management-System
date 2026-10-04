package com.subscription.dao;

import com.subscription.model.Plan;
import java.sql.*;
import java.util.*;

/**
 * PlanDAO — Data Access Object for Subscription Plans.
 *
 * SQL schema (reference):
 * ──────────────────────────────────────────────────────────────────
 * CREATE TABLE plans (
 *     id            INT AUTO_INCREMENT PRIMARY KEY,
 *     plan_name     VARCHAR(100) NOT NULL,
 *     description   TEXT,
 *     duration      INT          NOT NULL,
 *     duration_unit ENUM('Days','Months','Years') DEFAULT 'Days',
 *     price         DECIMAL(10,2) NOT NULL,
 *     features      TEXT,          -- comma-separated feature list
 *     status        ENUM('Active','Inactive') DEFAULT 'Active',
 *     created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * ──────────────────────────────────────────────────────────────────
 */
public class PlanDAO {

    private Connection getConnection() throws SQLException {
        // TODO: return DBConnection.getConnection();
        throw new UnsupportedOperationException("JDBC not yet configured.");
    }

    public List<Plan> getAllPlans() throws SQLException {
        List<Plan> list = new ArrayList<>();
        String sql = "SELECT * FROM plans ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Plan getPlanById(int id) throws SQLException {
        String sql = "SELECT * FROM plans WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public int addPlan(Plan p) throws SQLException {
        String sql = "INSERT INTO plans (plan_name, description, duration, duration_unit, price, features, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPlanName());
            ps.setString(2, p.getDescription());
            ps.setInt(3,    p.getDuration());
            ps.setString(4, p.getDurationUnit());
            ps.setDouble(5, p.getPrice());
            ps.setString(6, String.join(",", p.getFeatures()));
            ps.setString(7, p.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public boolean updatePlan(Plan p) throws SQLException {
        String sql = "UPDATE plans SET plan_name=?, description=?, duration=?, duration_unit=?, price=?, features=?, status=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPlanName());
            ps.setString(2, p.getDescription());
            ps.setInt(3,    p.getDuration());
            ps.setString(4, p.getDurationUnit());
            ps.setDouble(5, p.getPrice());
            ps.setString(6, String.join(",", p.getFeatures()));
            ps.setString(7, p.getStatus());
            ps.setInt(8,    p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePlan(int id) throws SQLException {
        String sql = "DELETE FROM plans WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Plan mapRow(ResultSet rs) throws SQLException {
        Plan p = new Plan();
        p.setId(rs.getInt("id"));
        p.setPlanName(rs.getString("plan_name"));
        p.setDescription(rs.getString("description"));
        p.setDuration(rs.getInt("duration"));
        p.setDurationUnit(rs.getString("duration_unit"));
        p.setPrice(rs.getDouble("price"));
        String features = rs.getString("features");
        p.setFeatures(features != null ? Arrays.asList(features.split(",")) : new ArrayList<>());
        p.setStatus(rs.getString("status"));
        return p;
    }
}
