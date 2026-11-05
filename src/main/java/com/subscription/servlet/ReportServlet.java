package com.subscription.servlet;

import com.subscription.dao.ReportDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("generate".equals(action)) {
            generateReport(request, response);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        generateReport(request, response);
    }

    private void generateReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fromParam = request.getParameter("from");
        String toParam = request.getParameter("to");
        String type = request.getParameter("type");

        if (fromParam == null || toParam == null || type == null) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "From date, To date, and Report type are required.");
            return;
        }

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(fromParam);
            to = LocalDate.parse(toParam);
        } catch (DateTimeParseException e) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid date format. Expected YYYY-MM-DD.");
            return;
        }

        if (from.isAfter(to)) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "From date cannot be after To date.");
            return;
        }

        try {
            Map<String, Object> data;
            switch (type) {
                case "Revenue Report":
                    data = reportDAO.getRevenueReport(from, to);
                    break;
                case "Subscription Report":
                    data = reportDAO.getSubscriptionReport(from, to);
                    break;
                case "Customer Report":
                    data = reportDAO.getCustomerReport(from, to);
                    break;
                case "Payment Report":
                    data = reportDAO.getPaymentReport(from, to);
                    break;
                default:
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                            "Unsupported report type: " + type);
                    return;
            }

            ServletSupport.writeJson(response, HttpServletResponse.SC_OK, data);
        } catch (SQLException e) {
            log("Error generating report", e);
            ServletSupport.writeError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error while generating report: " + e.getMessage());
        }
    }
}
