<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Settings – Subscription Management System">
    <title>Settings – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<%
    String activePage = "settings";
%>

<div class="app-shell">
    <%@ include file="sidebar.jspf" %>

    <div class="main-area">
        <header class="topbar" role="banner">
            <div class="topbar-left">
                <div>
                    <div class="topbar-page-title">Settings</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Settings</span>
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
                    <h1>System Settings</h1>
                    <p class="page-subtitle">Configure your application preferences</p>
                </div>
            </div>

            <div class="card" style="max-width: 800px;">
                <div class="card-header">
                    <h2 class="card-title">General Preferences</h2>
                </div>
                <div class="card-body">
                    <form onsubmit="event.preventDefault(); showToast('Settings saved successfully!');">
                        <div class="form-group">
                            <label class="form-label">Application Name</label>
                            <input type="text" class="form-control" value="Subscription Management System" />
                        </div>
                        <div class="form-row form-row-2">
                            <div class="form-group">
                                <label class="form-label">Currency</label>
                                <select class="form-control">
                                    <option value="INR" selected>INR (₹)</option>
                                    <option value="USD">USD ($)</option>
                                    <option value="EUR">EUR (€)</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Timezone</label>
                                <select class="form-control">
                                    <option value="IST" selected>Asia/Kolkata (IST)</option>
                                    <option value="UTC">UTC</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" style="margin-top: 16px;">
                            <button class="btn btn-primary" type="submit">Save Settings</button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>

