package com.subscription.servlet;

import com.subscription.model.Customer;
// import com.subscription.dao.CustomerDAO;   // ← uncomment in JDBC phase
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerServlet — CRUD operations for Customers.
 *
 * GET  /CustomerServlet?action=list           → forward to customers.jsp with customer list
 * POST /CustomerServlet?action=add            → add new customer
 * POST /CustomerServlet?action=update         → update existing customer
 * POST /CustomerServlet?action=delete&id=X   → delete customer by ID
 *
 * Currently uses in-memory sample data.
 * In the JDBC phase, replace sample data with CustomerDAO calls.
 */
@WebServlet("/CustomerServlet")
public class CustomerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ── Temporary in-memory store (replace with DAO in JDBC phase) ──
    private static final List<Customer> sampleCustomers = new ArrayList<>();

    static {
        sampleCustomers.add(new Customer(1, "Rahul Sharma",  "rahul@gmail.com",  "9876543210", "1995-03-14", "12 MG Road Delhi",       "Active"));
        sampleCustomers.add(new Customer(2, "Priya Singh",   "priya@gmail.com",  "9804321567", "1998-07-22", "45 Park Street Mumbai",   "Active"));
        sampleCustomers.add(new Customer(3, "Aman Verma",    "aman@gmail.com",   "9712345678", "1993-11-05", "78 Civil Lines Jaipur",   "Active"));
        sampleCustomers.add(new Customer(4, "Neha Gupta",    "neha@gmail.com",   "9698765432", "2000-01-30", "90 Lake View Bangalore",  "Inactive"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("list".equals(action) || action == null) {
            // ── TODO: CustomerDAO dao = new CustomerDAO();
            //          List<Customer> customers = dao.getAllCustomers();
            req.setAttribute("customers", sampleCustomers);
            req.getRequestDispatcher("/WEB-INF/views/customers.jsp").forward(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        req.setCharacterEncoding("UTF-8");

        if ("add".equals(action)) {
            Customer c = buildFromRequest(req, -1);
            // ── TODO: new CustomerDAO().addCustomer(c);
            sampleCustomers.add(c);
            res.sendRedirect(req.getContextPath() + "/CustomerServlet?action=list");

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Customer updated = buildFromRequest(req, id);
            // ── TODO: new CustomerDAO().updateCustomer(updated);
            for (int i = 0; i < sampleCustomers.size(); i++) {
                if (sampleCustomers.get(i).getId() == id) { sampleCustomers.set(i, updated); break; }
            }
            res.sendRedirect(req.getContextPath() + "/CustomerServlet?action=list");

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            // ── TODO: new CustomerDAO().deleteCustomer(id);
            sampleCustomers.removeIf(c -> c.getId() == id);
            res.sendRedirect(req.getContextPath() + "/CustomerServlet?action=list");
        }
    }

    private Customer buildFromRequest(HttpServletRequest req, int id) {
        Customer c = new Customer();
        c.setId(id == -1 ? sampleCustomers.size() + 1 : id);
        c.setName(req.getParameter("name"));
        c.setEmail(req.getParameter("email"));
        c.setPhone(req.getParameter("phone"));
        c.setDateOfBirth(req.getParameter("dob"));
        c.setAddress(req.getParameter("address"));
        c.setStatus(req.getParameter("status"));
        return c;
    }
}
