package com.subscription.servlet;

import com.subscription.model.Subscription;
// import com.subscription.dao.SubscriptionDAO;   // ← uncomment in JDBC phase
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * SubscriptionServlet — CRUD for Subscriptions.
 *
 * GET  /SubscriptionServlet?action=list           → list subscriptions
 * POST /SubscriptionServlet?action=add            → create subscription
 * POST /SubscriptionServlet?action=update         → update subscription
 * POST /SubscriptionServlet?action=delete&id=X   → delete subscription
 */
@WebServlet("/SubscriptionServlet")
public class SubscriptionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // ── TODO: List<Subscription> subs = new SubscriptionDAO().getAllSubscriptions();
        // req.setAttribute("subscriptions", subs);
        req.getRequestDispatcher("/WEB-INF/views/subscriptions.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        req.setCharacterEncoding("UTF-8");

        // ── TODO: connect each action to SubscriptionDAO
        if ("add".equals(action)) {
            // SubscriptionDAO dao = new SubscriptionDAO();
            // dao.addSubscription(buildFromRequest(req));
        } else if ("update".equals(action)) {
            // dao.updateSubscription(buildFromRequest(req));
        } else if ("delete".equals(action)) {
            // int id = Integer.parseInt(req.getParameter("id"));
            // new SubscriptionDAO().deleteSubscription(id);
        }
        res.sendRedirect(req.getContextPath() + "/SubscriptionServlet?action=list");
    }
}
