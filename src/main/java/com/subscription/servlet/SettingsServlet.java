package com.subscription.servlet;

import com.subscription.dao.DBConnection;
import com.subscription.dao.UserDAO;
import com.subscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("systemInfo".equals(action)) {
            Map<String, Object> info = new HashMap<>();
            info.put("serverInfo", getServletContext().getServerInfo());
            info.put("javaVersion", System.getProperty("java.version"));
            info.put("osName", System.getProperty("os.name"));

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                info.put("dbConnected", true);
                info.put("dbCatalog", conn.getCatalog());

                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers")) {
                    if (rs.next()) info.put("totalCustomers", rs.getInt(1));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM subscriptions")) {
                    if (rs.next()) info.put("totalSubscriptions", rs.getInt(1));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM payments")) {
                    if (rs.next()) info.put("totalPayments", rs.getInt(1));
                }
            } catch (SQLException e) {
                info.put("dbConnected", false);
                info.put("dbError", e.getMessage());
            }

            ServletSupport.writeJson(res, HttpServletResponse.SC_OK, info);
            return;
        }

        if ("auditLogs".equals(action)) {
            java.util.List<Map<String, String>> logs = new java.util.ArrayList<>();
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Fetch recent payments activity
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT p.id, c.name as customer_name, p.amount, p.payment_date, p.payment_status " +
                        "FROM payments p JOIN customers c ON p.customer_id = c.id " +
                        "ORDER BY p.id DESC LIMIT 5")) {
                    while (rs.next()) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("timestamp", rs.getString("payment_date"));
                        entry.put("module", "Billing");
                        entry.put("action", "Payment #" + rs.getInt("id") + " recorded (" + rs.getString("customer_name") + " - ₹" + rs.getDouble("amount") + ")");
                        entry.put("status", rs.getString("payment_status"));
                        logs.add(entry);
                    }
                }

                // Fetch recent subscriptions activity
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT s.id, c.name as customer_name, pl.plan_name, s.status, s.start_date " +
                        "FROM subscriptions s JOIN customers c ON s.customer_id = c.id " +
                        "JOIN plans pl ON s.plan_id = pl.id " +
                        "ORDER BY s.id DESC LIMIT 5")) {
                    while (rs.next()) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("timestamp", rs.getString("start_date"));
                        entry.put("module", "Subscription");
                        entry.put("action", "Sub #" + rs.getInt("id") + " assigned: " + rs.getString("plan_name") + " to " + rs.getString("customer_name"));
                        entry.put("status", rs.getString("status"));
                        logs.add(entry);
                    }
                }
            } catch (SQLException e) {
                log("Error fetching audit logs", e);
            }

            ServletSupport.writeJson(res, HttpServletResponse.SC_OK, Collections.singletonMap("logs", logs));
            return;
        }

        if ("runExpirySync".equals(action)) {
            handleExpirySync(req, res);
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, res);
    }

    private void handleExpirySync(HttpServletRequest req, HttpServletResponse res) throws IOException {
        int updatedCount = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Update active/expiring subscriptions whose end date has passed to 'Inactive' (valid ENUM)
            updatedCount = stmt.executeUpdate(
                    "UPDATE subscriptions SET status = 'Inactive' " +
                    "WHERE end_date < CURRENT_DATE() AND status != 'Inactive'");

            Map<String, Object> response = new HashMap<>();
            response.put("updatedCount", updatedCount);
            response.put("message", updatedCount > 0
                    ? updatedCount + " overdue subscription(s) automatically marked as Inactive."
                    : "All subscriptions are up-to-date. No overdue active subscriptions found.");
            ServletSupport.writeJson(res, HttpServletResponse.SC_OK, response);
        } catch (SQLException e) {
            log("Unable to run expiry sync", e);
            ServletSupport.writeError(req, res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error during subscription sync: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        HttpSession session = req.getSession(false);
        User loggedUser = session != null ? (User) session.getAttribute("loggedUser") : null;

        if (loggedUser == null) {
            ServletSupport.writeError(req, res, HttpServletResponse.SC_UNAUTHORIZED, "Session expired. Please log in again.");
            return;
        }

        if ("changePassword".equals(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (currentPassword == null || currentPassword.isEmpty() ||
                newPassword == null || newPassword.length() < 12 ||
                !newPassword.equals(confirmPassword)) {
                ServletSupport.writeError(req, res, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid password parameters. New password must be at least 12 characters and match confirmation.");
                return;
            }

            try {
                boolean success = userDAO.updatePassword(loggedUser.getId(), currentPassword, newPassword);
                if (success) {
                    ServletSupport.writeJson(res, HttpServletResponse.SC_OK,
                            Collections.singletonMap("message", "Password updated successfully."));
                } else {
                    ServletSupport.writeError(req, res, HttpServletResponse.SC_BAD_REQUEST,
                            "Incorrect current password.");
                }
            } catch (SQLException e) {
                log("Unable to update password", e);
                ServletSupport.writeError(req, res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database error while updating password.");
            }
        } else if ("runExpirySync".equals(action)) {
            handleExpirySync(req, res);
        } else {
            ServletSupport.writeError(req, res, HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }
}
