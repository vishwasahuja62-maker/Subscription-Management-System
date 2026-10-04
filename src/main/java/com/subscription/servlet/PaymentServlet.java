package com.subscription.servlet;

// import com.subscription.dao.PaymentDAO;   // ← uncomment in JDBC phase
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * PaymentServlet — CRUD for Payments.
 *
 * GET  /PaymentServlet?action=list           → list payments
 * POST /PaymentServlet?action=add            → record payment
 * POST /PaymentServlet?action=update         → update payment
 * POST /PaymentServlet?action=delete&id=X   → delete payment
 */
@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // ── TODO: List<Payment> payments = new PaymentDAO().getAllPayments();
        // req.setAttribute("payments", payments);
        req.getRequestDispatcher("/WEB-INF/views/payments.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        req.setCharacterEncoding("UTF-8");

        // ── TODO: replace with PaymentDAO calls
        if ("add".equals(action)) {
            // new PaymentDAO().addPayment(buildFromRequest(req));
        } else if ("update".equals(action)) {
            // new PaymentDAO().updatePayment(buildFromRequest(req));
        } else if ("delete".equals(action)) {
            // new PaymentDAO().deletePayment(Integer.parseInt(req.getParameter("id")));
        }
        res.sendRedirect(req.getContextPath() + "/PaymentServlet?action=list");
    }
}
