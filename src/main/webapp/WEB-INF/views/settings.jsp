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

            <!-- General Preferences Card -->
            <div class="card" style="max-width: 800px;">
                <div class="card-header">
                    <h2 class="card-title">General Preferences</h2>
                </div>
                <div class="card-body">
                    <form id="general-prefs-form" onsubmit="saveGeneralPreferences(event)">
                        <div class="form-group">
                            <label class="form-label" for="pref-app-name">Application Name</label>
                            <input type="text" id="pref-app-name" class="form-control" value="Subscription Management System" required />
                        </div>
                        <div class="form-row form-row-2">
                            <div class="form-group">
                                <label class="form-label" for="pref-currency">Currency Display</label>
                                <select id="pref-currency" class="form-control">
                                    <option value="INR" selected>INR (₹) - Indian Rupee</option>
                                    <option value="USD">USD ($) - US Dollar</option>
                                    <option value="EUR">EUR (€) - Euro</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="pref-timezone">System Timezone</label>
                                <select id="pref-timezone" class="form-control">
                                    <option value="Asia/Kolkata" selected>Asia/Kolkata (IST)</option>
                                    <option value="UTC">UTC (Universal Coordinated)</option>
                                </select>
                            </div>
                        </div>
                        <div class="form-group" style="margin-top: 16px;">
                            <button class="btn btn-primary" type="submit" id="save-settings-btn">Save Preferences</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Live Database & System Health Diagnostics Card -->
            <div class="card" style="max-width: 800px; margin-top: 24px;">
                <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <h2 class="card-title" style="margin:0;">System & Database Status</h2>
                        <p class="page-subtitle" style="margin:4px 0 0 0;">Live backend diagnostics and MySQL connection health</p>
                    </div>
                    <button class="btn btn-sm btn-outline" onclick="loadSystemInfo()" title="Refresh Status">
                        <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                        </svg>
                        Check Status
                    </button>
                </div>
                <div class="card-body">
                    <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap:16px; margin-bottom:16px;">
                        <div style="background:rgba(0,0,0,0.02); padding:14px 18px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                            <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">MySQL Database</div>
                            <div id="diag-db-status" style="margin-top:6px; font-weight:700; color:var(--color-success); font-size:1rem; display:flex; align-items:center; gap:6px;">
                                <span style="display:inline-block; width:8px; height:8px; border-radius:50%; background:var(--color-success);"></span>
                                Checking...
                            </div>
                            <div id="diag-db-name" style="font-size:0.8rem; color:var(--text-secondary); margin-top:4px;">Catalog: subscription_management</div>
                        </div>

                        <div style="background:rgba(0,0,0,0.02); padding:14px 18px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                            <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">Servlet Container</div>
                            <div id="diag-server-info" style="margin-top:6px; font-weight:700; color:var(--text-primary); font-size:0.95rem;">
                                Loading...
                            </div>
                            <div id="diag-java-ver" style="font-size:0.8rem; color:var(--text-secondary); margin-top:4px;">Java: OpenJDK</div>
                        </div>

                        <div style="background:rgba(0,0,0,0.02); padding:14px 18px; border-radius:var(--border-radius-md); border:1px solid var(--border-color);">
                            <div style="font-size:0.75rem; text-transform:uppercase; color:var(--text-muted); font-weight:700;">Database Records</div>
                            <div id="diag-records-count" style="margin-top:6px; font-weight:700; color:var(--color-primary); font-size:1.1rem;">
                                ...
                            </div>
                            <div style="font-size:0.8rem; color:var(--text-secondary); margin-top:4px;">Customers • Subs • Payments</div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Security Card -->
            <div class="card" style="max-width: 800px; margin-top: 24px;">
                <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
                    <div>
                        <h2 class="card-title" style="margin:0;">Account Security</h2>
                        <p class="page-subtitle" style="margin:4px 0 0 0;">Manage your password and authentication credentials</p>
                    </div>
                    <span class="badge badge-active" style="display:inline-flex;align-items:center;gap:6px;">
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                        </svg>
                        PBKDF2 Protected
                    </span>
                </div>
                <div class="card-body" style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:16px;">
                    <div>
                        <strong>Password</strong>
                        <div style="color:var(--text-secondary); font-size:0.875rem; margin-top:4px;">
                            Strong salted PBKDF2 encryption is enabled. Keep your credentials safe.
                        </div>
                    </div>
                    <button class="btn btn-secondary" onclick="openModal('password-modal')">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                        </svg>
                        Change Password
                    </button>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- ── Change Password Modal ── -->
<div class="modal-overlay" id="password-modal" role="dialog" aria-modal="true">
    <div class="modal modal-md">
        <div class="modal-header">
            <h2 class="modal-title">Change Account Password</h2>
            <button class="modal-close" onclick="closePasswordModal()" aria-label="Close modal">&times;</button>
        </div>
        <div class="modal-body">
            <form id="password-change-form" onsubmit="handlePasswordChange(event)">
                <div class="form-group">
                    <label class="form-label" for="current-password">Current Password <span class="required">*</span></label>
                    <div style="position:relative;">
                        <input type="password" id="current-password" class="form-control" required autocomplete="current-password" style="padding-right:40px;" />
                        <button type="button" onclick="togglePassVisibility('current-password', this)" style="position:absolute; right:10px; top:50%; transform:translateY(-50%); background:none; border:none; cursor:pointer; color:var(--text-muted);" title="Toggle visibility">
                            👁️
                        </button>
                    </div>
                    <div class="form-error" id="err-current-password"></div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="new-password">New Password (Min 12 chars) <span class="required">*</span></label>
                    <div style="position:relative;">
                        <input type="password" id="new-password" class="form-control" minlength="12" required autocomplete="new-password" style="padding-right:40px;" />
                        <button type="button" onclick="togglePassVisibility('new-password', this)" style="position:absolute; right:10px; top:50%; transform:translateY(-50%); background:none; border:none; cursor:pointer; color:var(--text-muted);" title="Toggle visibility">
                            👁️
                        </button>
                    </div>
                    <div class="form-error" id="err-new-password"></div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirm-password">Confirm New Password <span class="required">*</span></label>
                    <div style="position:relative;">
                        <input type="password" id="confirm-password" class="form-control" minlength="12" required autocomplete="new-password" style="padding-right:40px;" />
                        <button type="button" onclick="togglePassVisibility('confirm-password', this)" style="position:absolute; right:10px; top:50%; transform:translateY(-50%); background:none; border:none; cursor:pointer; color:var(--text-muted);" title="Toggle visibility">
                            👁️
                        </button>
                    </div>
                    <div class="form-error" id="err-confirm-password"></div>
                </div>

                <div class="modal-footer" style="padding:16px 0 0 0; margin-top:20px;">
                    <button type="button" class="btn btn-secondary" onclick="closePasswordModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary" id="update-pass-btn">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                        </svg>
                        Save New Password
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>
    function togglePassVisibility(inputId, btn) {
        const input = document.getElementById(inputId);
        if (!input) return;
        if (input.type === 'password') {
            input.type = 'text';
            btn.textContent = '🔒';
        } else {
            input.type = 'password';
            btn.textContent = '👁️';
        }
    }

    function closePasswordModal() {
        closeModal('password-modal');
        const form = document.getElementById('password-change-form');
        if (form) form.reset();
        clearErrors('password-change-form');
        ['current-password', 'new-password', 'confirm-password'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.type = 'password';
        });
    }

    async function handlePasswordChange(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const confirmPassword = document.getElementById('confirm-password').value;

        clearErrors('password-change-form');

        if (!currentPassword) {
            showError('current-password', 'Current password is required');
            return;
        }
        if (newPassword.length < 12) {
            showError('new-password', 'New password must be at least 12 characters');
            return;
        }
        if (newPassword !== confirmPassword) {
            showError('confirm-password', 'Passwords do not match');
            return;
        }

        const btn = document.getElementById('update-pass-btn');
        btn.disabled = true;
        try {
            await requestApi('SettingsServlet', 'changePassword', {
                currentPassword,
                newPassword,
                confirmPassword
            });
            showToast('Password updated successfully! Please remember it for next login.');
            closePasswordModal();
        } catch (error) {
            showToast(error.message, 'danger');
        } finally {
            btn.disabled = false;
        }
    }
</script>
</body>
</html>

