package com.subscription.servlet;

import com.subscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet — handles user authentication.
 *
 * GET  /LoginServlet  → redirect to login.jsp
 * POST /LoginServlet  → validate credentials, start session, redirect to dashboard
 *
 * Future: replace hardcoded check with UserDAO.authenticate(username, password)
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ── Temporary demo credentials (replace with DAO call later) ──
    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "admin123";
    private static final String DEMO_ROLE     = "Admin";

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

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role     = req.getParameter("role");

        // ── TODO: Replace this block with DAO authentication ──────
        // UserDAO dao = new UserDAO();
        // User user = dao.authenticate(username, password);
        // ──────────────────────────────────────────────────────────

        boolean valid = DEMO_USERNAME.equals(username) && DEMO_PASSWORD.equals(password);

        if (valid) {
            // Create session
            HttpSession session = req.getSession(true);
            User user = new User(1, username, "", role != null ? role : DEMO_ROLE, "", true);
            session.setAttribute("loggedUser", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // Redirect to dashboard (Use server-side forward for WEB-INF)
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
        } else {
            // Return to login with error
            req.setAttribute("loginError", "Invalid username or password. Please try again.");
            req.getRequestDispatcher("/login.jsp").forward(req, res);
        }
    }
}
