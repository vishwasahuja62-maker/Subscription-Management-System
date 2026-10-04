<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Reports – Subscription Management System">
    <title>Reports – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "reports";
%>

<div class="app-shell">
    <%@ include file="sidebar.jspf" %>

    <div class="main-area">
        <header class="topbar" role="banner">
            <div class="topbar-left">
                <div>
                    <div class="topbar-page-title">Reports</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Reports</span>
                    </div>
                </div>
            </div>
                        <div class="topbar-right">
                <button class="notif-btn btn" id="dark-mode-btn" onclick="toggleDarkMode()" title="Toggle Dark Mode" aria-label="Toggle Dark Mode">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
                    </svg>
                </button>
                <div class="header-user">
                    <div class="avatar">A</div>
                    <div>
                        <div class="header-user-name">Admin</div>
                        <div class="header-user-role">Administrator</div>
                    </div>
                </div>
            </div>
        </header>

        <main class="page-content" id="main-content">
            <div class="page-header">
                <div class="page-header-left">
                    <h1>Generate Reports</h1>
                    <p class="page-subtitle">Export and view analytics</p>
                </div>
            </div>

            <div class="card" style="max-width: 800px;">
                <div class="card-header">
                    <h2 class="card-title">Report Criteria</h2>
                </div>
                <div class="card-body">
                    <div class="form-row form-row-2">
                        <!-- Date Range -->
                        <div class="form-group">
                            <label class="form-label" for="rpt-from">From Date</label>
                            <input type="date" id="rpt-from" class="form-control"
                                   value="2025-01-01" aria-label="Report from date">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="rpt-to">To Date</label>
                            <input type="date" id="rpt-to" class="form-control"
                                   value="2025-04-30" aria-label="Report to date">
                        </div>
                    </div>
                    <div class="form-row form-row-2">
                        <!-- Report Type -->
                        <div class="form-group" style="margin-bottom:0;">
                            <label class="form-label" for="rpt-type">Report Type</label>
                            <select id="rpt-type" class="form-control" aria-label="Select report type">
                                <option value="">-- Select Report Type --</option>
                                <option value="Revenue Report">Revenue Report</option>
                                <option value="Subscription Report">Subscription Report</option>
                                <option value="Customer Report">Customer Report</option>
                                <option value="Payment Report">Payment Report</option>
                            </select>
                        </div>
                        <!-- Generate Button -->
                        <div class="form-group" style="display:flex;align-items:flex-end;margin-bottom:0;">
                            <button class="btn btn-primary" onclick="generateReport()" id="generate-report-btn"
                                    style="width:100%;justify-content:center;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                                     viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                          d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                                </svg>
                                Generate Report
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Report Results -->
            <div id="report-results" class="table-wrapper mt-md" style="display:none;"></div>

        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>

