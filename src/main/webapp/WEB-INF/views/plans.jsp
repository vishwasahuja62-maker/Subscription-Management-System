<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Plan Management – Subscription Management System">
    <title>Plans – Subscription Management System</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/variables.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<%
    String activePage = "plans";
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
                    <div class="topbar-page-title">Plan Management</div>
                    <div class="breadcrumb">
                        <span>Home</span> › <span>Plans</span>
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
                    <h1>Subscription Plans</h1>
                    <p class="page-subtitle">Define and manage subscription plans</p>
                </div>
                <button class="btn btn-primary" id="add-plan-btn" onclick="openAddPlan()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                    </svg>
                    Add Plan
                </button>
            </div>

            <!-- Search Toolbar -->
            <div class="toolbar" role="search" aria-label="Plan search">
                <div class="toolbar-group">
                    <div class="search-wrap">
                        <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                        <input type="text" id="plan-search" class="search-input"
                               placeholder="Search by plan name or description…"
                               oninput="searchPlans()" aria-label="Search plans">
                    </div>
                    <button class="btn btn-primary btn-sm" onclick="searchPlans()" id="plan-search-btn">Search</button>
                    <button class="btn btn-secondary btn-sm" onclick="refreshPlans()" id="plan-refresh-btn" title="Refresh">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="none"
                             viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                        </svg>
                        Refresh
                    </button>
                </div>
            </div>

            <!-- Plan Table -->
            <div class="table-wrapper">
                <table class="data-table" id="plan-table" aria-label="Subscription plans list">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Plan Name</th>
                            <th scope="col">Duration</th>
                            <th scope="col">Price</th>
                            <th scope="col">Status</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody id="plan-tbody">
                        <tr><td colspan="6" style="text-align:center;padding:24px;color:#94a3b8;">Loading…</td></tr>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span id="plan-count">Loading…</span>
                </div>
            </div>

        </main>
    </div><!-- /.main-area -->
</div><!-- /.app-shell -->


<!-- ================================================================
     PLAN MODAL
     ================================================================ -->
<div class="modal-overlay" id="plan-modal" role="dialog" aria-modal="true"
     aria-labelledby="plan-modal-title">
    <div class="modal modal-md">
        <div class="modal-header">
            <h2 class="modal-title" id="plan-modal-title">Add New Plan</h2>
            <button class="modal-close" onclick="closeModal('plan-modal')" aria-label="Close modal">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
        <div class="modal-body">
            <div style="font-size:.75rem;color:#64748b;margin-bottom:12px;">
                Plan ID: <strong id="plan-id-display">Auto-generated</strong>
            </div>
            <form id="plan-form" onsubmit="return false;" novalidate>

                <div class="form-row form-row-2">
                    <!-- Plan Name -->
                    <div class="form-group">
                        <label class="form-label" for="pf-name">Plan Name <span class="required">*</span></label>
                        <input type="text" id="pf-name" name="planName" class="form-control"
                               placeholder="e.g. Premium" required>
                        <div class="form-error" id="err-pf-name"></div>
                    </div>
                    <!-- Price -->
                    <div class="form-group">
                        <label class="form-label" for="pf-price">Price (₹) <span class="required">*</span></label>
                        <input type="number" id="pf-price" name="price" class="form-control"
                               placeholder="e.g. 999" min="1" required>
                        <div class="form-error" id="err-pf-price"></div>
                    </div>
                </div>

                <!-- Description -->
                <div class="form-group">
                    <label class="form-label" for="pf-desc">Description</label>
                    <textarea id="pf-desc" name="description" class="form-control" rows="2"
                              placeholder="Brief description of this plan"></textarea>
                </div>

                <div class="form-row form-row-2">
                    <!-- Duration -->
                    <div class="form-group">
                        <label class="form-label" for="pf-duration">Duration <span class="required">*</span></label>
                        <input type="number" id="pf-duration" name="duration" class="form-control"
                               placeholder="e.g. 30" min="1" required>
                        <div class="form-error" id="err-pf-duration"></div>
                    </div>
                    <!-- Duration Unit -->
                    <div class="form-group">
                        <label class="form-label" for="pf-duration-unit">Duration Unit</label>
                        <select id="pf-duration-unit" name="durationUnit" class="form-control">
                            <option value="Days">Days</option>
                            <option value="Months">Months</option>
                            <option value="Years">Years</option>
                        </select>
                    </div>
                </div>

                <!-- Features -->
                <div class="form-group">
                    <label class="form-label">Features</label>
                    <div class="checkbox-group">
                        <label class="checkbox-item">
                            <input type="checkbox" id="hd-streaming" data-label="HD Streaming">
                            HD Streaming
                        </label>
                        <label class="checkbox-item">
                            <input type="checkbox" id="multiple-devices" data-label="Multiple Devices">
                            Multiple Devices
                        </label>
                        <label class="checkbox-item">
                            <input type="checkbox" id="offline-download" data-label="Offline Download">
                            Offline Download
                        </label>
                        <label class="checkbox-item">
                            <input type="checkbox" id="family-sharing" data-label="Family Sharing">
                            Family Sharing
                        </label>
                    </div>
                </div>

                <!-- Status -->
                <div class="form-group">
                    <label class="form-label" for="pf-status">Status</label>
                    <select id="pf-status" name="status" class="form-control">
                        <option value="Active">Active</option>
                        <option value="Inactive">Inactive</option>
                    </select>
                </div>

            </form>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" onclick="closeModal('plan-modal')" id="cancel-plan-btn">Cancel</button>
            <button class="btn btn-primary" onclick="savePlan()" id="save-plan-btn">
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
            <h3 id="confirm-delete-title">Delete Plan?</h3>
            <p>Are you sure you want to delete <strong id="confirm-delete-name"></strong>?</p>
        </div>
        <div class="modal-footer" style="justify-content:center;gap:12px;">
            <button class="btn btn-secondary" onclick="closeModal('confirm-delete-modal')">Cancel</button>
            <button class="btn btn-danger" onclick="deleteConfirmed()" id="confirm-delete-btn">Delete</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    window._deleteType = 'plan';
    document.addEventListener('DOMContentLoaded', function () {
        initPlans();
    });
    document.getElementById('plan-search').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') searchPlans();
    });
</script>
</body>
</html>

