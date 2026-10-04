<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Admin Dashboard – Subscription Management System">
    <title>Dashboard – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "dashboard";
%>

<div class="app-shell">

    <!-- ── SIDEBAR ── -->
    <%@ include file="sidebar.jspf" %>

    <!-- ── MAIN AREA ── -->
    <div class="main-area">

        <!-- Top Bar -->
        <header class="topbar" role="banner">
            <div class="topbar-left">
                <div>
                    <div class="topbar-page-title">Admin Dashboard</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Dashboard</span>
                    </div>
                </div>
            </div>
            <div class="topbar-right">
                                <button class="notif-btn btn" id="dark-mode-btn" onclick="toggleDarkMode()" title="Toggle Dark Mode" aria-label="Toggle Dark Mode">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
                    </svg>
                </button>
                <button class="notif-btn btn" id="notif-btn" title="Notifications" aria-label="Notifications">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                    </svg>
                    <span class="notif-badge"></span>
                </button>
                <div class="header-user">
                    <div class="avatar">A</div>
                    <div>
                        <div class="header-user-name">Welcome, Admin</div>
                        <div class="header-user-role">Administrator</div>
                    </div>
                </div>
            </div>
        </header>

        <!-- Page Content -->
        <main class="page-content" id="main-content">

            <!-- Welcome Banner -->
            <div class="welcome-banner" role="region" aria-label="Welcome message">
                <h2>Welcome, Admin 👋</h2>
                <p>Here's what's happening today in your subscription system.</p>
            </div>

            <!-- ── Stats Grid ── -->
            <section class="stats-grid" aria-label="Key statistics">

                <!-- Total Customers -->
                <div class="stat-card">
                    <div class="stat-card-icon blue">
                        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M17 20h5v-1a3 3 0 00-5.356-1.857M17 20H7m10 0v-1c0-.656-.126-1.284-.356-1.857M7 20H2v-1a3 3 0 015.356-1.857M7 20v-1c0-.656.126-1.284.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                        </svg>
                    </div>
                    <div class="stat-card-info">
                        <div class="stat-card-label">Total Customers</div>
                        <div class="stat-card-value" id="stat-customers">0</div>
                        <div class="stat-card-trend">↑ All registered</div>
                    </div>
                </div>

                <!-- Active Subscriptions -->
                <div class="stat-card">
                    <div class="stat-card-icon green">
                        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                    </div>
                    <div class="stat-card-info">
                        <div class="stat-card-label">Active Subscriptions</div>
                        <div class="stat-card-value" id="stat-subs">0</div>
                        <div class="stat-card-trend">Currently active</div>
                    </div>
                </div>

                <!-- Total Revenue -->
                <div class="stat-card">
                    <div class="stat-card-icon purple">
                        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
                        </svg>
                    </div>
                    <div class="stat-card-info">
                        <div class="stat-card-label">Total Revenue</div>
                        <div class="stat-card-value" id="stat-revenue">₹0</div>
                        <div class="stat-card-trend">From paid invoices</div>
                    </div>
                </div>

                <!-- Expiring Soon -->
                <div class="stat-card">
                    <div class="stat-card-icon orange">
                        <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                    </div>
                    <div class="stat-card-info">
                        <div class="stat-card-label">Expiring Soon</div>
                        <div class="stat-card-value" id="stat-expiring">0</div>
                        <div class="stat-card-trend">Need attention</div>
                    </div>
                </div>

            </section>

            <!-- ── Recent Activity ── -->
            <div class="card" role="region" aria-label="Recent activity">
                <div class="card-header">
                    <h2 class="card-title">Recent Activity</h2>
                    <a href="SubscriptionServlet" class="btn btn-secondary btn-sm">View All</a>
                </div>
                <div class="table-wrapper" style="border:none;box-shadow:none;border-radius:0;">
                    <table class="data-table" id="recent-activity-table" aria-label="Recent subscriptions">
                        <thead>
                            <tr>
                                <th scope="col">ID</th>
                                <th scope="col">Customer</th>
                                <th scope="col">Plan</th>
                                <th scope="col">Date</th>
                                <th scope="col">Status</th>
                            </tr>
                        </thead>
                        <tbody id="recent-tbody">
                            <tr><td colspan="5" style="text-align:center;color:#94a3b8;padding:24px;">Loading…</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </main>
    </div><!-- /.main-area -->
</div><!-- /.app-shell -->

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        initDashboard();
    });
</script>
</body>
</html>

