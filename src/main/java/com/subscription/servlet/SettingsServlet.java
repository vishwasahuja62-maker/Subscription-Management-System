package com.subscription.servlet;

import com.subscription.dao.UserDAO;
import com.subscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, res);
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
        } else {
            ServletSupport.writeError(req, res, HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }
}
