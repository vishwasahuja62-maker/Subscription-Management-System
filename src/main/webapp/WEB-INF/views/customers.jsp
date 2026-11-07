<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Customer Management – Subscription Management System">
    <title>Customers – Subscription Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "customers";
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
                    <div class="topbar-page-title">Customer Management</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Customers</span>
                    </div>
                </div>
            </div>
                        <div class="topbar-right">
                <button class="notif-btn btn" id="dark-mode-btn" onclick="toggleDarkMode()" title="Toggle Dark Mode" aria-label="Toggle Dark Mode">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
                    </svg>
                </button>
                <div style="position:relative;">
                    <button class="notif-btn btn" id="notif-btn" onclick="toggleNotifDropdown(event)" title="Notifications" aria-label="Notifications">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                        </svg>
                        <span class="notif-badge" id="global-notif-badge" style="display:none;"></span>
                    </button>
                    <div class="notif-dropdown" id="notif-dropdown" onclick="event.stopPropagation()">
                        <div class="notif-dropdown-header">
                            <h4>Notifications</h4>
                            <span class="badge badge-pending" id="notif-count-badge">0 New</span>
                        </div>
                        <div class="notif-dropdown-list" id="notif-dropdown-list"></div>
                        <div class="notif-dropdown-footer">
                            <a href="SubscriptionServlet">View All Subscriptions &rarr;</a>
                        </div>
                    </div>
                </div>
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
                    <h1>Customer Management</h1>
                    <p class="page-subtitle">Manage all registered customers</p>
                </div>
                <button class="btn btn-primary" id="add-customer-btn" onclick="openAddCustomer()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                    Add Customer
                </button>
            </div>

            <!-- Search Toolbar -->
            <div class="toolbar" role="search" aria-label="Customer search">
                <div class="toolbar-group">
                    <div class="search-wrap">
                        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                        <input type="text" id="customer-search" class="search-input"
                               placeholder="Search by name, email, phone…"
                               oninput="searchCustomers()" aria-label="Search customers">
                    </div>
                    <button class="btn btn-primary btn-sm" onclick="searchCustomers()" id="customer-search-btn">
                        Search
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="refreshCustomers()" id="customer-refresh-btn"
                            title="Refresh">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                        </svg>
                        Refresh
                    </button>
                </div>
            </div>

            <!-- Customer Table -->
            <div class="table-wrapper">
                <table class="data-table" id="customer-table" aria-label="Customer list">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Name / Email</th>
                            <th scope="col">Email</th>
                            <th scope="col">Phone</th>
                            <th scope="col">Status</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="customer-tbody">
                        <tr><td colspan="6" style="text-align:center;padding:24px;color:#94a3b8;">Loading…</td></tr>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span id="customer-count">Loading…</span>
                    <div class="pagination-controls" id="customer-pagination" style="display:flex;align-items:center;gap:6px;"></div>
                </div>
            </div>

        </main>
    </div><!-- /.main-area -->
</div><!-- /.app-shell -->


<!-- ================================================================
     CUSTOMER MODAL
     ================================================================ -->
<div class="modal-overlay" id="customer-modal" role="dialog" aria-modal="true"
     aria-labelledby="customer-modal-title">
    <div class="modal modal-md">
        <div class="modal-header">
            <h2 class="modal-title" id="customer-modal-title">Add New Customer</h2>
            <button class="modal-close" onclick="closeModal('customer-modal')" aria-label="Close modal">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
        <div class="modal-body">
            <div style="font-size:.75rem;color:#64748b;margin-bottom:12px;">
                Customer ID: <strong id="customer-id-display">Auto-generated</strong>
            </div>
            <form id="customer-form" onsubmit="return false;" novalidate>

                <div class="form-row form-row-2">
                    <!-- Name -->
                    <div class="form-group">
                        <label class="form-label" for="cf-name">Name <span class="required">*</span></label>
                        <input type="text" id="cf-name" name="name" class="form-control"
                               placeholder="Enter name" required>
                        <div class="form-error" id="err-cf-name"></div>
                    </div>
                    <!-- Phone -->
                    <div class="form-group">
                        <label class="form-label" for="cf-phone">Phone <span class="required">*</span></label>
                        <input type="tel" id="cf-phone" name="phone" class="form-control"
                               placeholder="10-digit mobile" maxlength="10" required>
                        <div class="form-error" id="err-cf-phone"></div>
                    </div>
                </div>

                <!-- Email -->
                <div class="form-group">
                    <label class="form-label" for="cf-email">Email <span class="required">*</span></label>
                    <input type="email" id="cf-email" name="email" class="form-control"
                           placeholder="Enter email address" required>
                    <div class="form-error" id="err-cf-email"></div>
                </div>

                <div class="form-row form-row-2">
                    <!-- Date of Birth -->
                    <div class="form-group">
                        <label class="form-label" for="cf-dob">Date of Birth <span class="required">*</span></label>
                        <input type="date" id="cf-dob" name="dob" class="form-control" required>
                        <div class="form-error" id="err-cf-dob"></div>
                    </div>
                    <!-- Status -->
                    <div class="form-group">
                        <label class="form-label" for="cf-status">Status</label>
                        <select id="cf-status" name="status" class="form-control">
                            <option value="Active">Active</option>
                            <option value="Inactive">Inactive</option>
                        </select>
                    </div>
                </div>

                <!-- Address -->
                <div class="form-group">
                    <label class="form-label" for="cf-address">Address <span class="required">*</span></label>
                    <textarea id="cf-address" name="address" class="form-control" rows="2"
                              placeholder="Enter full address" required></textarea>
                    <div class="form-error" id="err-cf-address"></div>
                </div>

            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal('customer-modal')" id="cancel-customer-btn">Cancel</button>
            <button class="btn btn-primary" onclick="saveCustomer()" id="save-customer-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M5 13l4 4L19 7"/>
                </svg>
                Save
            </button>
        </div>
    </div>
</div>

<!-- ================================================================
     CONFIRM DELETE MODAL
     ================================================================ -->
<div class="modal-overlay" id="confirm-delete-modal" role="dialog" aria-modal="true"
     aria-labelledby="confirm-delete-title">
    <div class="modal modal-sm">
        <div class="modal-body confirm-modal-body">
            <div class="confirm-modal-icon danger">
                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
            </div>
            <h3 id="confirm-delete-title">Delete Record?</h3>
            <p>Are you sure you want to delete <strong id="confirm-delete-name"></strong>? This action cannot be undone.</p>
        </div>
        <div class="modal-footer" style="justify-content:center;gap:12px;">
            <button class="btn btn-secondary" onclick="closeModal('confirm-delete-modal')" id="cancel-delete-btn">Cancel</button>
            <button class="btn btn-danger" onclick="deleteConfirmed()" id="confirm-delete-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
                Delete
            </button>
        </div>
    </div>
</div>


<!-- ── Customer 360 Profile & History Modal ── -->
<div class="modal-overlay" id="customer-profile-modal" role="dialog" aria-modal="true">
    <div class="modal modal-lg" style="max-width: 760px;">
        <div class="modal-header" style="padding: 18px 28px;">
            <div style="display:flex; align-items:center; gap:12px;">
                <div class="avatar" id="cprofile-avatar" style="width:42px; height:42px; font-size:1.1rem;">C</div>
                <div>
                    <h2 class="modal-title" id="cprofile-name" style="margin:0; font-size:1.25rem;">Customer Profile</h2>
                    <div style="font-size:0.8rem; color:var(--text-muted);" id="cprofile-subtitle">Customer 360° View</div>
                </div>
            </div>
            <button class="modal-close" onclick="closeModal('customer-profile-modal')" aria-label="Close modal">&times;</button>
        </div>
        <div class="modal-body" style="padding: 24px 28px;">
            <!-- Customer Quick Stats -->
            <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap:16px; margin-bottom: 24px;">
                <div style="background:rgba(0,0,0,0.02); padding:14px 16px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                    <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">Account Status</div>
                    <div id="cprofile-status-badge" style="margin-top:6px;"></div>
                </div>
                <div style="background:rgba(0,0,0,0.02); padding:14px 16px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                    <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">Total Paid Spend</div>
                    <div id="cprofile-total-spend" style="font-size:1.3rem; font-weight:800; color:var(--color-success); margin-top:4px;">₹0</div>
                </div>
                <div style="background:rgba(0,0,0,0.02); padding:14px 16px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                    <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">Active Plans</div>
                    <div id="cprofile-active-plans" style="font-size:1.3rem; font-weight:800; color:var(--color-primary); margin-top:4px;">0</div>
                </div>
            </div>

            <!-- Contact & Demographics Details -->
            <div style="margin-bottom: 24px; padding:16px 20px; background:var(--bg-app); border-radius:var(--border-radius-md); font-size:0.875rem;">
                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:12px;">
                    <div><strong>Email:</strong> <span id="cprofile-email" style="color:var(--text-secondary);"></span></div>
                    <div><strong>Phone:</strong> <span id="cprofile-phone" style="color:var(--text-secondary);"></span></div>
                    <div><strong>Date of Birth:</strong> <span id="cprofile-dob" style="color:var(--text-secondary);"></span></div>
                    <div><strong>Address:</strong> <span id="cprofile-address" style="color:var(--text-secondary);"></span></div>
                </div>
            </div>

            <!-- Subscriptions Tab/Header -->
            <h4 style="margin:0 0 12px 0; font-size:0.95rem; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
                <span>📜</span> Subscriptions History
            </h4>
            <div class="table-wrapper" style="margin-bottom: 24px; border:1px solid var(--border-color); border-radius:var(--border-radius-md);">
                <table class="data-table" style="font-size:0.85rem;">
                    <thead>
                        <tr>
                            <th>Sub ID</th>
                            <th>Plan</th>
                            <th>Cycle</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody id="cprofile-subs-tbody"></tbody>
                </table>
            </div>

            <!-- Payments History -->
            <h4 style="margin:0 0 12px 0; font-size:0.95rem; color:var(--text-primary); display:flex; align-items:center; gap:8px;">
                <span>💳</span> Payment Transactions
            </h4>
            <div class="table-wrapper" style="border:1px solid var(--border-color); border-radius:var(--border-radius-md);">
                <table class="data-table" style="font-size:0.85rem;">
                    <thead>
                        <tr>
                            <th>Payment ID</th>
                            <th>Amount</th>
                            <th>Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody id="cprofile-payments-tbody"></tbody>
                </table>
            </div>
        </div>
        <div class="modal-footer" style="padding: 16px 28px; justify-content: space-between;">
            <button class="btn btn-secondary" onclick="closeModal('customer-profile-modal')">Close</button>
            <button class="btn btn-success" id="btn-cprofile-whatsapp" style="background:#25D366; color:#fff; border-color:#25D366; display:inline-flex; align-items:center; gap:8px;" onclick="chatCustomerWhatsApp()" title="Open WhatsApp Chat">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12.031 6.172c-3.181 0-5.767 2.586-5.768 5.766-.001 1.298.38 2.27 1.019 3.287l-.711 2.598 2.669-.699c.969.584 1.771.895 2.791.896h.001c3.181 0 5.767-2.586 5.768-5.766 0-3.18-2.587-5.766-5.769-5.766zm3.374 8.167c-.145.407-.743.771-1.037.818-.28.045-.632.067-2.036-.514-1.666-.69-2.738-2.385-2.82-2.496-.083-.11-.676-.901-.676-1.718 0-.817.426-1.218.577-1.385.152-.166.331-.208.441-.208.111 0 .221.002.318.006.103.004.241-.039.378.29.145.347.498 1.218.541 1.307.044.09.073.194.013.313-.059.119-.089.193-.177.296-.088.104-.185.231-.264.311-.089.09-.182.188-.078.367.103.178.46 1.758 1.986 2.438.196.088.35.14.47.178.197.063.376.054.517.033.158-.024.485-.198.554-.39zM12 2C6.477 2 2 6.477 2 12c0 1.891.524 3.662 1.436 5.176L2 22l4.981-1.393C8.423 21.492 10.155 22 12 22c5.523 0 10-4.477 10-10S17.523 2 12 2z"/>
                </svg>
                Message on WhatsApp
            </button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    // page-level delete type
    window._deleteType = 'customer';

    document.addEventListener('DOMContentLoaded', function () {
        initCustomers();
    });

    // Override showError to map to the form-error elements in this page
    // (app.js showError uses parentElement traversal which works fine)

    // Enter key on search
    document.getElementById('customer-search').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') searchCustomers();
    });
</script>
</body>
</html>
