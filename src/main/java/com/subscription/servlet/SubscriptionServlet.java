package com.subscription.servlet;

import com.subscription.dao.SubscriptionDAO;
import com.subscription.model.Customer;
import com.subscription.model.Plan;
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

@WebServlet("/SubscriptionServlet")
public class SubscriptionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Subscription> subscriptions = subscriptionDAO.getAllSubscriptions();
            if (ServletSupport.wantsJson(request)) {
                ServletSupport.writeJson(response, HttpServletResponse.SC_OK, subscriptions);
                return;
            }
            request.setAttribute("subscriptions", subscriptions);
            request.getRequestDispatcher("/WEB-INF/views/subscriptions.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            log("Unable to load subscriptions.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load subscriptions.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Subscription subscription = buildSubscription(request);
                subscription.setId(subscriptionDAO.addSubscription(subscription));
                respondSuccess(request, response, "Subscription added.", subscription.getId());
            } else if ("update".equals(action)) {
                Subscription subscription = buildSubscription(request);
                subscription.setId(ServletSupport.requiredId(request));
                if (!subscriptionDAO.updateSubscription(subscription)) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Subscription was not found.");
                    return;
                }
                respondSuccess(request, response, "Subscription updated.", subscription.getId());
            } else if ("delete".equals(action)) {
                if (!subscriptionDAO.deleteSubscription(ServletSupport.requiredId(request))) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Subscription was not found.");
                    return;
                }
                respondSuccess(request, response, "Subscription deleted.", null);
            } else {
                ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported subscription action.");
            }
        } catch (IllegalArgumentException e) {
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_CONFLICT,
                    "The selected customer or plan does not exist.");
        } catch (SQLException e) {
            log("Unable to save subscription.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not save subscription.");
        }
    }

    private Subscription buildSubscription(HttpServletRequest request) {
        Customer customer = new Customer();
        customer.setId(parseId(ServletSupport.required(request, "customerId"), "customer"));
        Plan plan = new Plan();
        plan.setId(parseId(ServletSupport.required(request, "planId"), "plan"));

        String startDate = ServletSupport.required(request, "startDate");
        String endDate = ServletSupport.required(request, "endDate");
        try {
            if (!LocalDate.parse(endDate).isAfter(LocalDate.parse(startDate))) {
                throw new IllegalArgumentException("End date must be after start date.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Subscription dates must be valid ISO dates.");
        }

        String status = ServletSupport.required(request, "status");
        if (!"Active".equals(status) && !"Expiring".equals(status) && !"Inactive".equals(status)) {
            throw new IllegalArgumentException("Invalid subscription status.");
        }

        String renewal = request.getParameter("autoRenewal");
        if (renewal != null && !"on".equals(renewal)
                && !"true".equals(renewal) && !"false".equals(renewal)) {
            throw new IllegalArgumentException("Invalid auto-renewal value.");
        }

        Subscription subscription = new Subscription();
        subscription.setCustomer(customer);
        subscription.setPlan(plan);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus(status);
        subscription.setAutoRenewal("on".equals(renewal) || "true".equals(renewal));
        return subscription;
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
            response.sendRedirect(request.getContextPath() + "/SubscriptionServlet?action=list");
        }
    }
}
