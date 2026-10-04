<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Subscription Management – Subscription Management System">
    <title>Subscriptions – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "subscriptions";
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
                    <div class="topbar-page-title">Subscription Management</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Subscriptions</span>
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

        <!-- Page Content -->
        <main class="page-content" id="main-content">

            <!-- Page Header -->
            <div class="page-header">
                <div class="page-header-left">
                    <h1>Subscription Management</h1>
                    <p class="page-subtitle">Track and manage all customer subscriptions</p>
                </div>
                <button class="btn btn-primary" id="add-sub-btn" onclick="openAddSub()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                    Add Subscription
                </button>
            </div>

            <!-- Search / Filter Toolbar -->
            <div class="toolbar" role="search" aria-label="Subscription filters">
                <div class="toolbar-group">
                    <div class="search-wrap">
                        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                        <input type="text" id="sub-search" class="search-input"
                               placeholder="Search customer or plan…"
                               oninput="filterSubs()" aria-label="Search subscriptions">
                    </div>

                    <select id="sub-plan-filter" class="form-control" style="width:160px;"
                            onchange="filterSubs()" aria-label="Filter by plan">
                        <option value="">All Plans</option>
                    </select>

                    <select id="sub-status-filter" class="form-control" style="width:150px;"
                            onchange="filterSubs()" aria-label="Filter by status">
                        <option value="">All Status</option>
                        <option value="Active">Active</option>
                        <option value="Expiring">Expiring</option>
                        <option value="Inactive">Inactive</option>
                    </select>

                    <button class="btn btn-secondary btn-sm" onclick="refreshSubs()" id="sub-refresh-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                        </svg>
                        Refresh
                    </button>
                </div>
            </div>

            <!-- Subscription Table -->
            <div class="table-wrapper">
                <table class="data-table" id="sub-table" aria-label="Subscription list">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Customer</th>
                            <th scope="col">Plan</th>
                            <th scope="col">Start Date</th>
                            <th scope="col">End Date</th>
                            <th scope="col">Status</th>
                            <th scope="col">Auto Renewal</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="sub-tbody">
                        <tr><td colspan="8" style="text-align:center;padding:24px;color:#94a3b8;">Loading…</td></tr>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span id="sub-count">Loading…</span>
                </div>
            </div>

        </main>
    </div><!-- /.main-area -->
</div><!-- /.app-shell -->


<!-- ================================================================
     SUBSCRIPTION MODAL
     ================================================================ -->
<div class="modal-overlay" id="sub-modal" role="dialog" aria-modal="true"
     aria-labelledby="sub-modal-title">
    <div class="modal modal-md">
        <div class="modal-header">
            <h2 class="modal-title" id="sub-modal-title">Add New Subscription</h2>
            <button class="modal-close" onclick="closeModal('sub-modal')" aria-label="Close">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
        <div class="modal-body">
            <div style="font-size:.75rem;color:#64748b;margin-bottom:12px;">
                Subscription ID: <strong id="sub-id-display">Auto-generated</strong>
            </div>
            <form id="sub-form" onsubmit="return false;" novalidate>

                <!-- Customer -->
                <div class="form-group">
                    <label class="form-label" for="sf-customer">Customer <span class="required">*</span></label>
                    <select id="sf-customer" name="customerId" class="form-control" required>
                        <option value="">-- Select Customer --</option>
                    </select>
                    <div class="form-error" id="err-sf-customer"></div>
                </div>

                <!-- Plan -->
                <div class="form-group">
                    <label class="form-label" for="sf-plan">Plan <span class="required">*</span></label>
                    <select id="sf-plan" name="planId" class="form-control" required>
                        <option value="">-- Select Plan --</option>
                    </select>
                    <div class="form-error" id="err-sf-plan"></div>
                </div>

                <div class="form-row form-row-2">
                    <!-- Start Date -->
                    <div class="form-group">
                        <label class="form-label" for="sf-start">Start Date <span class="required">*</span></label>
                        <input type="date" id="sf-start" name="startDate" class="form-control" required>
                        <div class="form-error" id="err-sf-start"></div>
                    </div>
                    <!-- End Date -->
                    <div class="form-group">
                        <label class="form-label" for="sf-end">End Date <span class="required">*</span></label>
                        <input type="date" id="sf-end" name="endDate" class="form-control" required>
                        <div class="form-error" id="err-sf-end"></div>
                    </div>
                </div>

                <div class="form-row form-row-2">
                    <!-- Status -->
                    <div class="form-group">
                        <label class="form-label" for="sf-status">Status</label>
                        <select id="sf-status" name="status" class="form-control">
                            <option value="Active">Active</option>
                            <option value="Expiring">Expiring</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>
                    <!-- Auto Renewal -->
                    <div class="form-group" style="display:flex;align-items:flex-end;padding-bottom:4px;">
                        <label class="checkbox-item" style="font-size:.9rem;gap:8px;cursor:pointer;">
                            <input type="checkbox" id="sf-auto-renewal" name="autoRenewal"
                                   style="width:16px;height:16px;accent-color:var(--color-primary);">
                            Auto Renewal
                        </label>
                    </div>
                </div>

            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal('sub-modal')" id="cancel-sub-btn">Cancel</button>
            <button class="btn btn-primary" onclick="saveSub()" id="save-sub-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                </svg>
                Save
            </button>
        </div>
    </div>
</div>

<!-- ── Shared Confirm Delete Modal ── -->
<div class="modal-overlay" id="confirm-delete-modal" role="dialog" aria-modal="true">
    <div class="modal modal-sm">
        <div class="modal-body confirm-modal-body">
            <div class="confirm-modal-icon danger">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
            </div>
            <h3>Delete Subscription?</h3>
            <p>Delete subscription <strong id="confirm-delete-name"></strong>?</p>
        </div>
        <div class="modal-footer" style="justify-content:center;gap:12px;">
            <button class="btn btn-secondary" onclick="closeModal('confirm-delete-modal')">Cancel</button>
            <button class="btn btn-danger" onclick="deleteConfirmed()">Delete</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    window._deleteType = 'sub';
    document.addEventListener('DOMContentLoaded', function () {
        initSubscriptions();
        // Populate plan filter dropdown
        const planFilter = document.getElementById('sub-plan-filter');
        DB.plans.forEach(p => {
            const opt = document.createElement('option');
            opt.value = p.id; opt.textContent = p.name;
            planFilter.appendChild(opt);
        });
    });
</script>
</body>
</html>

