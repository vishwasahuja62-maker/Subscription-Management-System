package com.subscription.servlet;

import com.subscription.dao.PlanDAO;
import com.subscription.model.Plan;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PlanServlet")
public class PlanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PlanDAO planDAO = new PlanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Plan> plans = planDAO.getAllPlans();
            if (ServletSupport.wantsJson(request)) {
                ServletSupport.writeJson(response, HttpServletResponse.SC_OK, plans);
                return;
            }
            request.setAttribute("plans", plans);
            request.getRequestDispatcher("/WEB-INF/views/plans.jsp").forward(request, response);
        } catch (SQLException e) {
            log("Unable to load plans.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load plans.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Plan plan = buildPlan(request);
                plan.setId(planDAO.addPlan(plan));
                respondSuccess(request, response, "Plan added.", plan.getId());
            } else if ("update".equals(action)) {
                Plan plan = buildPlan(request);
                plan.setId(ServletSupport.requiredId(request));
                if (!planDAO.updatePlan(plan)) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Plan was not found.");
                    return;
                }
                respondSuccess(request, response, "Plan updated.", plan.getId());
            } else if ("delete".equals(action)) {
                if (!planDAO.deletePlan(ServletSupport.requiredId(request))) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Plan was not found.");
                    return;
                }
                respondSuccess(request, response, "Plan deleted.", null);
            } else {
                ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported plan action.");
            }
        } catch (IllegalArgumentException e) {
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_CONFLICT,
                    "This plan is used by subscriptions and cannot be deleted.");
        } catch (SQLException e) {
            log("Unable to save plan.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not save plan.");
        }
    }

    private Plan buildPlan(HttpServletRequest request) {
        Plan plan = new Plan();
        plan.setPlanName(ServletSupport.required(request, "planName"));
        if (plan.getPlanName().length() > 100) {
            throw new IllegalArgumentException("Plan name must be 100 characters or fewer.");
        }
        plan.setDescription(request.getParameter("description"));
        plan.setDuration(parsePositiveInt(ServletSupport.required(request, "duration"), "duration"));
        plan.setDurationUnit(ServletSupport.required(request, "durationUnit"));
        plan.setPrice(parsePositiveDouble(ServletSupport.required(request, "price"), "price"));
        if (plan.getPrice() > 99999999.99) {
            throw new IllegalArgumentException("Price exceeds the supported maximum.");
        }
        plan.setStatus(ServletSupport.required(request, "status"));
        String[] features = request.getParameterValues("features");
        plan.setFeatures(features == null ? new ArrayList<>() : Arrays.asList(features));
        List<String> allowedFeatures = Arrays.asList(
                "HD Streaming", "Multiple Devices", "Offline Download", "Family Sharing");
        if (!allowedFeatures.containsAll(plan.getFeatures())) {
            throw new IllegalArgumentException("One or more selected plan features are invalid.");
        }

        if (!Arrays.asList("Days", "Months", "Years").contains(plan.getDurationUnit())) {
            throw new IllegalArgumentException("Invalid plan duration unit.");
        }
        if (!Arrays.asList("Active", "Inactive").contains(plan.getStatus())) {
            throw new IllegalArgumentException("Invalid plan status.");
        }
        return plan;
    }

    private int parsePositiveInt(String value, String field) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // Return a client-facing validation error below.
        }
        throw new IllegalArgumentException(field + " must be a positive whole number.");
    }

    private double parsePositiveDouble(String value, String field) {
        try {
            double parsed = Double.parseDouble(value);
            if (Double.isFinite(parsed) && parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // Return a client-facing validation error below.
        }
        throw new IllegalArgumentException(field + " must be a positive number.");
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
            response.sendRedirect(request.getContextPath() + "/PlanServlet");
        }
    }
}
