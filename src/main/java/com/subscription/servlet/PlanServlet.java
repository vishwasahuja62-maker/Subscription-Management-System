package com.subscription.servlet;

import com.subscription.model.Plan;
// import com.subscription.dao.PlanDAO;   // ← uncomment in JDBC phase
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * PlanServlet — CRUD operations for Subscription Plans.
 *
 * GET  /PlanServlet?action=list            → list all plans
 * POST /PlanServlet?action=add             → add new plan
 * POST /PlanServlet?action=update          → update plan
 * POST /PlanServlet?action=delete&id=X    → delete plan
 */
@WebServlet("/PlanServlet")
public class PlanServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final List<Plan> samplePlans = new ArrayList<>();

    static {
        samplePlans.add(new Plan(1, "Basic",    "Basic streaming",        30,  "Days", 199,  Arrays.asList("HD Streaming"),                                          "Active"));
        samplePlans.add(new Plan(2, "Standard", "Standard plan",          30,  "Days", 399,  Arrays.asList("HD Streaming","Multiple Devices"),                       "Active"));
        samplePlans.add(new Plan(3, "Premium",  "Premium family plan",    90,  "Days", 999,  Arrays.asList("HD Streaming","Multiple Devices","Offline Download"),    "Active"));
        samplePlans.add(new Plan(4, "Family",   "Full family sharing",    365, "Days", 1499, Arrays.asList("HD Streaming","Multiple Devices","Offline Download","Family Sharing"), "Inactive"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // ── TODO: List<Plan> plans = new PlanDAO().getAllPlans();
        req.setAttribute("plans", samplePlans);
        req.getRequestDispatcher("/WEB-INF/views/plans.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        req.setCharacterEncoding("UTF-8");

        if ("add".equals(action)) {
            Plan p = buildFromRequest(req, -1);
            // ── TODO: new PlanDAO().addPlan(p);
            samplePlans.add(p);

        } else if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Plan updated = buildFromRequest(req, id);
            // ── TODO: new PlanDAO().updatePlan(updated);
            for (int i = 0; i < samplePlans.size(); i++) {
                if (samplePlans.get(i).getId() == id) { samplePlans.set(i, updated); break; }
            }

        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            // ── TODO: new PlanDAO().deletePlan(id);
            samplePlans.removeIf(p -> p.getId() == id);
        }
        res.sendRedirect(req.getContextPath() + "/PlanServlet");
    }

    private Plan buildFromRequest(HttpServletRequest req, int id) {
        Plan p = new Plan();
        p.setId(id == -1 ? samplePlans.size() + 1 : id);
        p.setPlanName(req.getParameter("planName"));
        p.setDescription(req.getParameter("description"));
        p.setDuration(Integer.parseInt(req.getParameter("duration")));
        p.setDurationUnit(req.getParameter("durationUnit"));
        p.setPrice(Double.parseDouble(req.getParameter("price")));
        p.setStatus(req.getParameter("status"));
        // Features from checkboxes
        String[] features = req.getParameterValues("features");
        p.setFeatures(features != null ? Arrays.asList(features) : new ArrayList<>());
        return p;
    }
}
