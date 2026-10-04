package com.subscription.dao;

import com.subscription.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO — Data Access Object for the Customer entity.
 *
 * All methods use JDBC to interact with the 'customers' MySQL table.
 * Replace the DBConnection usage with your actual connection utility in the JDBC phase.
 *
 * SQL schema (reference):
 * ──────────────────────────────────────────────────────────────────
 * CREATE TABLE customers (
 *     id           INT AUTO_INCREMENT PRIMARY KEY,
 *     name         VARCHAR(100) NOT NULL,
 *     email        VARCHAR(150) NOT NULL UNIQUE,
 *     phone        VARCHAR(15)  NOT NULL,
 *     date_of_birth DATE,
 *     address      TEXT,
 *     status       ENUM('Active','Inactive') DEFAULT 'Active',
 *     created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * ──────────────────────────────────────────────────────────────────
 */
public class CustomerDAO {

    // ── Connection helper (implement DBConnection in JDBC phase) ──
    private Connection getConnection() throws SQLException {
        // TODO: return DBConnection.getConnection();
        throw new UnsupportedOperationException("JDBC not yet configured. Implement DBConnection first.");
    }

    /**
     * Retrieve all customers.
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Retrieve a customer by primary key.
     */
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Insert a new customer. Returns the generated ID.
     */
    public int addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone, date_of_birth, address, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getDateOfBirth());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Update an existing customer.
     */
    public boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, email=?, phone=?, date_of_birth=?, address=?, status=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getDateOfBirth());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getStatus());
            ps.setInt(7,    c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete a customer by ID.
     */
    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Search customers by name or email (for the search bar).
     */
    public List<Customer> searchCustomers(String query) throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?";
        String like = "%" + query + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── Helper: map ResultSet row to Customer object ──
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setDateOfBirth(rs.getString("date_of_birth"));
        c.setAddress(rs.getString("address"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
