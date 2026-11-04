package com.subscription.servlet;

import com.subscription.dao.PaymentDAO;
import com.subscription.dao.SubscriptionDAO;
import com.subscription.model.Customer;
import com.subscription.model.Payment;
import com.subscription.model.Subscription;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PaymentServlet")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Payment> payments = paymentDAO.getAllPayments();
            if (ServletSupport.wantsJson(request)) {
                ServletSupport.writeJson(response, HttpServletResponse.SC_OK, payments);
                return;
            }
            request.setAttribute("payments", payments);
            request.getRequestDispatcher("/WEB-INF/views/payments.jsp").forward(request, response);
        } catch (SQLException e) {
            log("Unable to load payments.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load payments.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Payment payment = buildPayment(request);
                payment.setId(paymentDAO.addPayment(payment));
                respondSuccess(request, response, "Payment recorded.", payment.getId());
            } else if ("update".equals(action)) {
                Payment payment = buildPayment(request);
                payment.setId(ServletSupport.requiredId(request));
                if (!paymentDAO.updatePayment(payment)) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Payment was not found.");
                    return;
                }
                respondSuccess(request, response, "Payment updated.", payment.getId());
            } else if ("delete".equals(action)) {
                if (!paymentDAO.deletePayment(ServletSupport.requiredId(request))) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Payment was not found.");
                    return;
                }
                respondSuccess(request, response, "Payment deleted.", null);
            } else {
                ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported payment action.");
            }
        } catch (IllegalArgumentException e) {
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_CONFLICT,
                    "The selected customer or subscription does not exist.");
        } catch (SQLException e) {
            log("Unable to save payment.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not save payment.");
        }
    }

    private Payment buildPayment(HttpServletRequest request) throws SQLException {
        int customerId = parseId(ServletSupport.required(request, "customerId"), "customer");
        int subscriptionId =
                parseId(ServletSupport.required(request, "subscriptionId"), "subscription");
        Subscription subscription = subscriptionDAO.getSubscriptionById(subscriptionId);
        if (subscription == null || subscription.getCustomer().getId() != customerId) {
            throw new IllegalArgumentException(
                    "The selected subscription does not belong to the selected customer.");
        }

        double amount;
        try {
            amount = Double.parseDouble(ServletSupport.required(request, "amount"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }
        if (!Double.isFinite(amount) || amount <= 0 || amount > 99999999.99) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }

        String paymentDate = ServletSupport.required(request, "paymentDate");
        try {
            LocalDate.parse(paymentDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Payment date must be a valid ISO date.");
        }

        String status = ServletSupport.required(request, "paymentStatus");
        if (!"Paid".equals(status) && !"Pending".equals(status) && !"Failed".equals(status)) {
            throw new IllegalArgumentException("Invalid payment status.");
        }

        Customer customer = new Customer();
        customer.setId(customerId);
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setSubscription(subscription);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentStatus(status);
        return payment;
    }

    private int parseId(String value, String field) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // Return a client-facing validation error below.
        }
        throw new IllegalArgumentException("Select a valid " + field + ".");
    }

    private void respondSuccess(HttpServletRequest request, HttpServletResponse response,
                                String message, Integer id) throws IOException {
        if (ServletSupport.wantsJson(request)) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", message);
            if (id != null) {
                result.put("id", id);
            }
            ServletSupport.writeJson(response, HttpServletResponse.SC_OK, result);
        } else {
            response.sendRedirect(request.getContextPath() + "/PaymentServlet?action=list");
        }
    }
}
