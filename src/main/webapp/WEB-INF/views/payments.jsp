<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Payments & Reports – Subscription Management System">
    <title>Payments – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "payments";
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
                    <div class="topbar-page-title">Payments</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Payments</span>
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

            <!-- ============================================================
                 PAYMENTS SECTION
                 ============================================================ -->
            <div class="page-header">
                <div class="page-header-left">
                    <h1>Payment Management</h1>
                    <p class="page-subtitle">Track all payment transactions</p>
                </div>
                <button class="btn btn-primary" id="add-payment-btn" onclick="openAddPayment()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                    Add Payment
                </button>
            </div>

            <!-- Search Toolbar -->
            <div class="toolbar" role="search" aria-label="Payment search">
                <div class="toolbar-group">
                    <div class="search-wrap">
                        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                        <input type="text" id="payment-search" class="search-input"
                               placeholder="Search by customer or plan…"
                               oninput="searchPayments()" aria-label="Search payments">
                    </div>
                    <button class="btn btn-primary btn-sm" onclick="searchPayments()" id="payment-search-btn">Search</button>
                    <button class="btn btn-secondary btn-sm" onclick="refreshPayments()" id="payment-refresh-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                        </svg>
                        Refresh
                    </button>
                </div>
            </div>

            <!-- Payment Table -->
            <div class="table-wrapper">
                <table class="data-table" id="payment-table" aria-label="Payment list">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Customer</th>
                            <th scope="col">Plan</th>
                            <th scope="col">Amount</th>
                            <th scope="col">Date</th>
                            <th scope="col">Status</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="payment-tbody">
                        <tr><td colspan="7" style="text-align:center;padding:24px;color:#94a3b8;">Loading…</td></tr>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span id="payment-count">Loading…</span>
                </div>
            </div>



        </main>
    </div><!-- /.main-area -->
</div><!-- /.app-shell -->


<!-- ================================================================
     PAYMENT MODAL
     ================================================================ -->
<div class="modal-overlay" id="payment-modal" role="dialog" aria-modal="true"
     aria-labelledby="payment-modal-title">
    <div class="modal modal-md">
        <div class="modal-header">
            <h2 class="modal-title" id="payment-modal-title">Add Payment</h2>
            <button class="modal-close" onclick="closeModal('payment-modal')" aria-label="Close">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
        <div class="modal-body">
            <div style="font-size:.75rem;color:#64748b;margin-bottom:12px;">
                Payment ID: <strong id="payment-id-display">Auto-generated</strong>
            </div>
            <form id="payment-form" onsubmit="return false;" novalidate>

                <!-- Customer -->
                <div class="form-group">
                    <label class="form-label" for="pf-customer">Customer <span class="required">*</span></label>
                    <select id="pf-customer" name="customerId" class="form-control" required>
                        <option value="">-- Select Customer --</option>
                    </select>
                    <div class="form-error" id="err-pf-customer"></div>
                </div>

                <!-- Subscription -->
                <div class="form-group">
                    <label class="form-label" for="pf-sub">Subscription <span class="required">*</span></label>
                    <select id="pf-sub" name="subscriptionId" class="form-control" required>
                        <option value="">-- Select Subscription --</option>
                    </select>
                    <div class="form-error" id="err-pf-sub"></div>
                </div>

                <div class="form-row form-row-2">
                    <!-- Amount -->
                    <div class="form-group">
                        <label class="form-label" for="pf-amount">Amount (₹) <span class="required">*</span></label>
                        <input type="number" id="pf-amount" name="amount" class="form-control"
                               placeholder="e.g. 999" min="1" required>
                        <div class="form-error" id="err-pf-amount"></div>
                    </div>
                    <!-- Payment Date -->
                    <div class="form-group">
                        <label class="form-label" for="pf-pay-date">Payment Date <span class="required">*</span></label>
                        <input type="date" id="pf-pay-date" name="paymentDate" class="form-control" required>
                        <div class="form-error" id="err-pf-pay-date"></div>
                    </div>
                </div>

                <!-- Payment Status -->
                <div class="form-group">
                    <label class="form-label" for="pf-pay-status">Payment Status <span class="required">*</span></label>
                    <select id="pf-pay-status" name="paymentStatus" class="form-control" required>
                        <option value="">-- Select Status --</option>
                        <option value="Paid">Paid</option>
                        <option value="Pending">Pending</option>
                        <option value="Failed">Failed</option>
                    </select>
                    <div class="form-error" id="err-pf-pay-status"></div>
                </div>

            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal('payment-modal')" id="cancel-payment-btn">Cancel</button>
            <button class="btn btn-primary" onclick="savePayment()" id="save-payment-btn">
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
            <h3>Delete Payment?</h3>
            <p>Delete payment record <strong id="confirm-delete-name"></strong>?</p>
        </div>
        <div class="modal-footer" style="justify-content:center;gap:12px;">
            <button class="btn btn-secondary" onclick="closeModal('confirm-delete-modal')">Cancel</button>
            <button class="btn btn-danger" onclick="deleteConfirmed()">Delete</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    window._deleteType = 'payment';
    document.addEventListener('DOMContentLoaded', function () {
        initPayments();
    });
    document.getElementById('payment-search').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') searchPayments();
    });
    // Scroll to reports section if hash is #reports
    if (window.location.hash === '#reports') {
        setTimeout(function() {
            document.getElementById('reports')?.scrollIntoView({ behavior: 'smooth' });
        }, 300);
    }
</script>
</body>
</html>

