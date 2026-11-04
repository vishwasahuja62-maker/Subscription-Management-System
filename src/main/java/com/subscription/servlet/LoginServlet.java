package com.subscription.servlet;

import com.subscription.dao.UserDAO;
import com.subscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * LoginServlet — handles user authentication.
 *
 * GET  /LoginServlet  → redirect to login.jsp
 * POST /LoginServlet  → validate credentials, start session, redirect to dashboard
 *
 * Credentials are verified against the users table using salted PBKDF2 hashes.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("dashboard".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
        } else {
            // Redirect to login page
            res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String requestedRole = req.getParameter("role");

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            req.setAttribute("loginError", "Invalid username or password. Please try again.");
            req.getRequestDispatcher("/login.jsp").forward(req, res);
            return;
        }

        try {
            User user = new UserDAO().authenticate(username.trim(), password);
            if (user == null || !"Admin".equals(user.getRole())
                    || (requestedRole != null && !requestedRole.equals(user.getRole()))) {
                req.setAttribute("loginError", "Invalid username or password. Please try again.");
                req.getRequestDispatcher("/login.jsp").forward(req, res);
                return;
            }

            HttpSession previousSession = req.getSession(false);
            if (previousSession != null) {
                previousSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);
            session.setMaxInactiveInterval(30 * 60);
            res.sendRedirect(req.getContextPath() + "/LoginServlet?action=dashboard");
        } catch (SQLException e) {
            log("Unable to authenticate user.", e);
            res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("loginError", "Login service is temporarily unavailable.");
            req.getRequestDispatcher("/login.jsp").forward(req, res);
        }
    }
}
