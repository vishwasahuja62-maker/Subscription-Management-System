package com.subscription.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * ReportServlet — handles report generation requests.
 *
 * POST /ReportServlet
 *   Parameters: fromDate, toDate, reportType
 *   Response: forwards to payments.jsp with report data in request scope
 *
 * In the JDBC phase this servlet will query aggregated data from the DAO layer.
 */
@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String fromDate   = req.getParameter("fromDate");
        String toDate     = req.getParameter("toDate");
        String reportType = req.getParameter("reportType");

        // ── TODO: fetch report data from DAO based on reportType and date range
        // Example:
        // if ("Revenue Report".equals(reportType)) {
        //     List<RevenueRow> data = new PaymentDAO().getRevenueReport(fromDate, toDate);
        //     req.setAttribute("reportData", data);
        // }

        req.setAttribute("fromDate",   fromDate);
        req.setAttribute("toDate",     toDate);
        req.setAttribute("reportType", reportType);

        req.getRequestDispatcher("/WEB-INF/views/payments.jsp").forward(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, res);
    }
}
