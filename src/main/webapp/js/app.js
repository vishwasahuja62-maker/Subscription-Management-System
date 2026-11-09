/* ================================================================
   app.js  —  Client-side logic for Subscription Management System
   Handles: navigation, database-backed CRUD operations, modals,
            search/filter, validation, and UI helpers.
   ================================================================ */

'use strict';

const APP_CONTEXT = window.location.pathname.slice(0, window.location.pathname.lastIndexOf('/'));

const DB = {
    customers: [],
    plans: [],
    subscriptions: [],
    payments: []
};

async function requestApi(servlet, action, values) {
    const url = `${APP_CONTEXT}/${servlet}`;
    const options = {
        method: values ? 'POST' : 'GET',
        headers: { 'Accept': 'application/json' },
        credentials: 'same-origin'
    };

    if (values) {
        const body = new URLSearchParams({ action });
        Object.entries(values).forEach(([key, value]) => {
            if (Array.isArray(value)) value.forEach(item => body.append(key, item));
            else if (value !== undefined && value !== null) body.append(key, value);
        });
        options.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        options.body = body;
    } else {
        options.method = 'GET';
    }

    const response = await fetch(values ? url : `${url}?action=${encodeURIComponent(action)}`, options);
    let payload;
    try {
        payload = await response.json();
    } catch (error) {
        throw new Error('The server returned an unreadable response.');
    }
    if (response.status === 401) {
        window.location.assign(`${APP_CONTEXT}/login.jsp`);
        throw new Error('Your session has expired. Please sign in again.');
    }
    if (!response.ok) throw new Error(payload.error || 'The request could not be completed.');
    return payload;
}

async function loadBackendData() {
    const [customers, plans, subscriptions, payments] = await Promise.all([
        requestApi('CustomerServlet', 'list'),
        requestApi('PlanServlet', 'list'),
        requestApi('SubscriptionServlet', 'list'),
        requestApi('PaymentServlet', 'list')
    ]);

    DB.customers = customers.map(c => ({
        id: String(c.id), name: c.name, email: c.email, phone: c.phone,
        dob: c.dateOfBirth, address: c.address, status: c.status
    }));
    DB.plans = plans.map(p => ({
        id: String(p.id), name: p.planName, description: p.description,
        duration: p.duration, durationUnit: p.durationUnit, price: p.price,
        features: p.features || [], status: p.status
    }));
    DB.subscriptions = subscriptions.map(s => ({
        id: String(s.id), customerId: String(s.customer.id),
        customerName: s.customer.name, planId: String(s.plan.id),
        planName: s.plan.planName, startDate: s.startDate, endDate: s.endDate,
        status: s.status, autoRenewal: s.autoRenewal
    }));
    DB.payments = payments.map(p => ({
        id: String(p.id), customerId: String(p.customer.id),
        customerName: p.customer.name, subscriptionId: String(p.subscription.id),
        planName: p.subscription.plan.planName, amount: p.amount,
        paymentDate: p.paymentDate, status: p.paymentStatus
    }));

    if (typeof renderNotificationDropdown === 'function') {
        renderNotificationDropdown();
    }
}

async function initializeBackendPage(render) {
    try {
        await loadBackendData();
        render();
    } catch (error) {
        showToast(`Backend error: ${error.message}`, 'danger');
    }
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

/* ================================================================
   MODAL HELPERS
   ================================================================ */
function openModal(id) {
    const overlay = document.getElementById(id);
    if (overlay) {
        overlay.classList.add('open');
        document.body.style.overflow = 'hidden';
        // Focus first input
        const first = overlay.querySelector('input,select,textarea');
        if (first) setTimeout(() => first.focus(), 100);
    }
}
function closeModal(id) {
    const overlay = document.getElementById(id);
    if (overlay) {
        overlay.classList.remove('open');
        document.body.style.overflow = '';
    }
}
// Close on backdrop click
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('open');
        document.body.style.overflow = '';
    }
});
// Close on Escape
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.open').forEach(m => {
            m.classList.remove('open');
            document.body.style.overflow = '';
        });
    }
});

/* ================================================================
   BADGE HELPERS
   ================================================================ */
function statusBadge(status) {
    const map = {
        'Active':   'badge-active',
        'Inactive': 'badge-inactive',
        'Expiring': 'badge-expiring',
        'Paid':     'badge-paid',
        'Pending':  'badge-pending',
        'Failed':   'badge-failed',
    };
    const cls = map[status] || 'badge-pending';
    return `<span class="badge ${cls}">${status}</span>`;
}

/* ================================================================
   FORM VALIDATION HELPERS
   ================================================================ */
function clearErrors(formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    form.querySelectorAll('.form-error').forEach(el => el.classList.remove('visible'));
    form.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));
}

function showError(fieldId, msg) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('is-invalid');
        const err = field.parentElement.querySelector('.form-error') ||
                    field.closest('.form-group')?.querySelector('.form-error');
        if (err) { err.textContent = msg; err.classList.add('visible'); }
    }
}

function isEmail(v)  { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v); }
function isPhone(v)  { return /^[6-9]\d{9}$/.test(v); }
function isPositive(v) { return !isNaN(v) && Number(v) > 0; }

/* ================================================================
   SEARCH / FILTER UTILITY
   ================================================================ */
function filterData(arr, query, fields) {
    if (!query) return arr;
    const q = query.toLowerCase();
    return arr.filter(row => fields.some(f => String(row[f] || '').toLowerCase().includes(q)));
}

/* ================================================================
   NOTIFICATION TOAST
   ================================================================ */
function showToast(msg, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;gap:8px;';
        document.body.appendChild(container);
    }
    const colors = { success: '#22c55e', danger: '#ef4444', warning: '#f59e0b', info: '#3b82f6' };
    const toast = document.createElement('div');
    toast.style.cssText = `background:#fff;border-left:4px solid ${colors[type]||colors.info};border-radius:8px;
        box-shadow:0 4px 16px rgba(0,0,0,.14);padding:12px 20px;font-size:.85rem;color:#1e293b;
        font-family:Inter,sans-serif;min-width:220px;max-width:360px;
        animation:slideInRight .25s ease;`;
    toast.textContent = msg;
    container.appendChild(toast);
    setTimeout(() => { toast.style.animation = 'fadeOut .25s ease'; setTimeout(() => toast.remove(), 240); }, 3000);
}

// Inject toast animation keyframes once
(function injectKeyframes() {
    const style = document.createElement('style');
    style.textContent = `
    @keyframes slideInRight { from { opacity:0; transform:translateX(40px); } to { opacity:1; transform:translateX(0); } }
    @keyframes fadeOut      { from { opacity:1; } to { opacity:0; transform:translateX(20px); } }
    `;
    document.head.appendChild(style);
})();

/* ================================================================
   ── DASHBOARD ──
   ================================================================ */
function initDashboard() {
    initializeBackendPage(renderDashboard);
}

function renderDashboard() {
    const totalCustomers = DB.customers.length;
    const activeSubs     = DB.subscriptions.filter(s => s.status === 'Active').length;
    const totalRevenue   = DB.payments.filter(p => p.status === 'Paid').reduce((sum, p) => sum + p.amount, 0);

    const now = new Date();
    const sevenDaysLater = new Date();
    sevenDaysLater.setDate(now.getDate() + 7);

    // Calculate subscriptions expiring in the next 7 days or already expired
    const expiringSubscriptions = DB.subscriptions.map(s => {
        const endDate = new Date(s.endDate + 'T00:00:00');
        const diffTime = endDate.getTime() - now.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return { ...s, diffDays };
    }).filter(s => s.status !== 'Inactive' && s.diffDays <= 7)
      .sort((a, b) => a.diffDays - b.diffDays);

    setInner('stat-customers',    totalCustomers);
    setInner('stat-subs',         activeSubs);
    setInner('stat-revenue',      '₹' + totalRevenue.toLocaleString('en-IN'));
    setInner('stat-expiring',     expiringSubscriptions.length);

    // Render Expiring Soon Alert Card
    const alertCard = document.getElementById('expiring-alert-card');
    const alertTbody = document.getElementById('expiring-tbody');
    const alertBadge = document.getElementById('expiring-badge');

    if (alertCard && alertTbody) {
        if (expiringSubscriptions.length > 0) {
            alertCard.style.display = 'block';
            if (alertBadge) alertBadge.textContent = `${expiringSubscriptions.length} Expiring Soon`;
            alertTbody.innerHTML = expiringSubscriptions.map(s => {
                let badgeClass = s.diffDays <= 0 ? 'badge-failed' : (s.diffDays <= 3 ? 'badge-failed' : 'badge-pending');
                let dayText = s.diffDays < 0 ? `${Math.abs(s.diffDays)} day(s) ago` : (s.diffDays === 0 ? 'Today!' : `${s.diffDays} day(s) left`);
                return `
                <tr>
                    <td>
                        <div class="cell-name">${escapeHtml(s.customerName)}</div>
                        <div style="font-size:0.75rem;color:var(--text-muted);">Sub ID: #${escapeHtml(s.id)}</div>
                    </td>
                    <td><div style="font-weight:600;">${escapeHtml(s.planName)}</div></td>
                    <td>${escapeHtml(s.endDate)}</td>
                    <td><span class="badge ${badgeClass}">${dayText}</span></td>
                    <td style="text-align:right; display:flex; justify-content:flex-end; gap:6px;">
                        <button class="btn btn-sm btn-primary" onclick="quickRenewSubscription('${s.id}')" title="Renew for 30 days">
                            ⚡ Renew
                        </button>
                        <button class="btn btn-sm" style="background:#25D366; color:#fff; border-color:#25D366; display:inline-flex; align-items:center; gap:4px; padding:4px 8px; font-size:0.75rem;" onclick="sendRenewalWhatsApp('${s.id}')" title="Send WhatsApp Expiry Reminder">
                            💬 WhatsApp
                        </button>
                    </td>
                </tr>`;
            }).join('');
        } else {
            alertCard.style.display = 'none';
        }
    }

    // Recent activity table
    const tbody = document.getElementById('recent-tbody');
    if (tbody) {
        tbody.innerHTML = DB.subscriptions.slice(0,5).map(s => `
            <tr>
                <td class="col-id">${escapeHtml(s.id)}</td>
                <td><div class="cell-name">${escapeHtml(s.customerName)}</div></td>
                <td>${escapeHtml(s.planName)}</td>
                <td>${escapeHtml(s.startDate)}</td>
                <td>${statusBadge(escapeHtml(s.status))}</td>
            </tr>`).join('');
    }
}

async function quickRenewSubscription(subId) {
    if (!confirm('Are you sure you want to renew this subscription for 30 days?')) return;
    try {
        await requestApi('SubscriptionServlet', 'renew', { id: subId, days: 30 });
        showToast('Subscription renewed successfully by 30 days!');
        await loadBackendData();
        renderDashboard();
    } catch (error) {
        showToast(error.message, 'danger');
    }
}

function sendRenewalReminder(subId) {
    sendRenewalWhatsApp(subId);
}

/* ================================================================
   ── WHATSAPP DISPATCHER ENGINE ──
   ================================================================ */
function formatWhatsAppPhone(phone) {
    if (!phone) return '';
    let cleaned = String(phone).replace(/\D/g, '');
    if (cleaned.length === 10) cleaned = '91' + cleaned; // default to India country code if 10 digits
    return cleaned;
}

function openWhatsAppUrl(phone, text) {
    const formattedPhone = formatWhatsAppPhone(phone);
    if (!formattedPhone) {
        showToast('Valid phone number not found for customer.', 'warning');
        return;
    }
    const url = `https://wa.me/${formattedPhone}?text=${encodeURIComponent(text)}`;
    window.open(url, '_blank');
}

function sendRenewalWhatsApp(subId) {
    const sub = DB.subscriptions.find(s => s.id === subId);
    if (!sub) return;
    const customer = DB.customers.find(c => c.id === sub.customerId);
    const phone = customer?.phone || '';

    const message = 
`🔔 *Subscription Expiry Reminder*
Dear *${sub.customerName}*,

Your subscription for *${sub.planName}* is expiring on *${sub.endDate}*.
To avoid any service interruption, please renew your plan.

👉 Subscription ID: #${sub.id}
👉 Status: ${sub.status}

Need help renewing? Reply to this message.
Thank you,
*Subscription Management System*`;

    openWhatsAppUrl(phone, message);
}

let currentInvoicePaymentId = null;

function sendInvoiceWhatsApp() {
    if (!currentInvoicePaymentId) return;
    const payment = DB.payments.find(p => p.id === currentInvoicePaymentId);
    if (!payment) return;
    const customer = DB.customers.find(c => c.id === payment.customerId);
    const phone = customer?.phone || '';

    const message =
`🧾 *Official Payment Receipt*
Dear *${payment.customerName}*,

Thank you for your payment! Here are your invoice details:

• *Invoice Number:* INV-${payment.id.padStart(5, '0')}
• *Plan:* ${payment.planName}
• *Amount Paid:* ₹${Number(payment.amount).toLocaleString('en-IN')}
• *Payment Date:* ${payment.paymentDate}
• *Status:* ✅ ${payment.status}

For any queries regarding this invoice, please reach out to our support team.
*Subscription Management System*`;

    openWhatsAppUrl(phone, message);
}

let activeCustomerProfileId = null;

function chatCustomerWhatsApp() {
    if (!activeCustomerProfileId) return;
    const customer = DB.customers.find(c => c.id === activeCustomerProfileId);
    if (!customer) return;

    const message = `Hello *${customer.name}*, greeting from Subscription Management System! How can we assist you today?`;
    openWhatsAppUrl(customer.phone, message);
}

function setInner(id, val) { const el = document.getElementById(id); if (el) el.textContent = val; }

/* ================================================================
   ── NOTIFICATION BELL DROPDOWN ──
   ================================================================ */
function toggleNotifDropdown(e) {
    if (e) e.stopPropagation();
    const dropdown = document.getElementById('notif-dropdown');
    if (!dropdown) return;
    const isShowing = dropdown.classList.contains('show');
    if (isShowing) {
        dropdown.classList.remove('show');
    } else {
        renderNotificationDropdown();
        dropdown.classList.add('show');
    }
}

function renderNotificationDropdown() {
    const list = document.getElementById('notif-dropdown-list');
    const badge = document.getElementById('global-notif-badge');
    const countBadge = document.getElementById('notif-count-badge');
    if (!list) return;

    const now = new Date();
    const expiring = DB.subscriptions.map(s => {
        const endDate = new Date(s.endDate + 'T00:00:00');
        const diffDays = Math.ceil((endDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
        return { ...s, diffDays };
    }).filter(s => s.status !== 'Inactive' && s.diffDays <= 7);

    const pendingPayments = DB.payments.filter(p => p.status === 'Pending');

    const totalAlerts = expiring.length + pendingPayments.length;

    if (badge) {
        badge.style.display = totalAlerts > 0 ? 'block' : 'none';
    }
    if (countBadge) {
        countBadge.textContent = `${totalAlerts} Alert${totalAlerts === 1 ? '' : 's'}`;
    }

    if (totalAlerts === 0) {
        list.innerHTML = `<div style="text-align:center;padding:24px;color:var(--text-muted);font-size:0.85rem;">
            🎉 All clear! No pending payments or expiring subscriptions.
        </div>`;
        return;
    }

    let itemsHtml = '';

    expiring.forEach(s => {
        const timeLabel = s.diffDays <= 0 ? 'Expired' : (s.diffDays === 0 ? 'Expires today' : `Expires in ${s.diffDays} day(s)`);
        itemsHtml += `
        <a class="notif-item" href="SubscriptionServlet">
            <div class="notif-item-icon warning">⚠️</div>
            <div style="flex:1;">
                <div class="notif-item-title">${escapeHtml(s.customerName)} (${escapeHtml(s.planName)})</div>
                <div class="notif-item-desc">${timeLabel} on ${escapeHtml(s.endDate)}</div>
            </div>
        </a>`;
    });

    pendingPayments.forEach(p => {
        itemsHtml += `
        <a class="notif-item" href="PaymentServlet">
            <div class="notif-item-icon danger">₹</div>
            <div style="flex:1;">
                <div class="notif-item-title">Pending Payment: ₹${Number(p.amount).toLocaleString('en-IN')}</div>
                <div class="notif-item-desc">${escapeHtml(p.customerName)} • Due since ${escapeHtml(p.paymentDate)}</div>
            </div>
        </a>`;
    });

    list.innerHTML = itemsHtml;
}

// Global click listener to close notification dropdown when clicking outside
document.addEventListener('click', () => {
    const dropdown = document.getElementById('notif-dropdown');
    if (dropdown) dropdown.classList.remove('show');
});

/* ================================================================
   ── PAGINATION ENGINE ──
   ================================================================ */
const paginationState = {
    customers:     { page: 1, pageSize: 5 },
    subscriptions: { page: 1, pageSize: 5 },
    payments:      { page: 1, pageSize: 5 }
};

function paginate(data, type) {
    const state = paginationState[type];
    const totalRecords = data.length;
    const totalPages = Math.max(1, Math.ceil(totalRecords / state.pageSize));
    if (state.page > totalPages) state.page = totalPages;
    if (state.page < 1) state.page = 1;

    const start = (state.page - 1) * state.pageSize;
    const paginatedData = data.slice(start, start + state.pageSize);

    renderPaginationControls(type, totalRecords, totalPages);
    return { paginatedData, start, totalRecords };
}

function renderPaginationControls(type, totalRecords, totalPages) {
    const controls = document.getElementById(`${type === 'subscriptions' ? 'sub' : (type === 'customers' ? 'customer' : 'payment')}-pagination`);
    if (!controls) return;
    const state = paginationState[type];

    if (totalRecords <= state.pageSize) {
        controls.innerHTML = '';
        return;
    }

    let html = `
        <button class="page-btn" onclick="changePage('${type}', ${state.page - 1})" ${state.page <= 1 ? 'disabled' : ''} title="Previous Page">&laquo;</button>
    `;

    for (let i = 1; i <= totalPages; i++) {
        html += `<button class="page-btn ${i === state.page ? 'active' : ''}" onclick="changePage('${type}', ${i})">${i}</button>`;
    }

    html += `
        <button class="page-btn" onclick="changePage('${type}', ${state.page + 1})" ${state.page >= totalPages ? 'disabled' : ''} title="Next Page">&raquo;</button>
    `;
    controls.innerHTML = html;
}

function changePage(type, newPage) {
    paginationState[type].page = newPage;
    if (type === 'customers') searchCustomers();
    else if (type === 'subscriptions') filterSubs();
    else if (type === 'payments') searchPayments();
}

/* ================================================================
   ── CUSTOMERS ──
   ================================================================ */
let editingCustomerId = null;
let customerDeleteId  = null;
let customerSearchQuery = '';

function initCustomers() {
    initializeBackendPage(() => {
        renderCustomerTable(DB.customers);
        populateCustomerSelectsOnOtherPages();
    });
}

function renderCustomerTable(data) {
    const tbody = document.getElementById('customer-tbody');
    if (!tbody) return;
    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-1a3 3 0 00-5.356-1.857M17 20H7m10 0v-1c0-.656-.126-1.284-.356-1.857M7 20H2v-1a3 3 0 015.356-1.857M7 20v-1c0-.656.126-1.284.356-1.857m0 0a5.002 5.002 0 019.288 0"/></svg>
            <h3>No customers found</h3><p>Add your first customer to get started.</p></div></td></tr>`;
        const pg = document.getElementById('customer-pagination'); if (pg) pg.innerHTML = '';
        const countEl = document.getElementById('customer-count'); if (countEl) countEl.textContent = 'Showing 0 record(s)';
        return;
    }
    const { paginatedData, start, totalRecords } = paginate(data, 'customers');
    tbody.innerHTML = paginatedData.map(c => `
        <tr>
            <td class="col-id">${escapeHtml(c.id)}</td>
            <td><div class="cell-name">${escapeHtml(c.name)}</div><div class="cell-sub">${escapeHtml(c.email)}</div></td>
            <td>${escapeHtml(c.email)}</td>
            <td>${escapeHtml(c.phone)}</td>
            <td>${statusBadge(escapeHtml(c.status))}</td>
            <td><div class="actions-cell">
                <button class="btn btn-sm btn-outline" onclick="openCustomerProfile('${c.id}')" title="View Profile">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                    View
                </button>
                <button class="btn btn-sm btn-outline" onclick="openEditCustomer('${c.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    Edit
                </button>
                <button class="btn btn-sm btn-danger" onclick="confirmDeleteCustomer('${c.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    Delete
                </button>
            </div></td>
        </tr>`).join('');
    const countEl = document.getElementById('customer-count');
    if (countEl) countEl.textContent = `Showing ${start + 1}–${Math.min(start + paginatedData.length, totalRecords)} of ${totalRecords} record(s)`;
}

function openCustomerProfile(id) {
    activeCustomerProfileId = id;
    const customer = DB.customers.find(c => c.id === id);
    if (!customer) return;

    // Header
    const avatarEl = document.getElementById('cprofile-avatar');
    if (avatarEl) avatarEl.textContent = customer.name.charAt(0).toUpperCase();
    const nameEl = document.getElementById('cprofile-name');
    if (nameEl) nameEl.textContent = customer.name;
    const subtitleEl = document.getElementById('cprofile-subtitle');
    if (subtitleEl) subtitleEl.textContent = `Customer ID: #${customer.id}`;
    const emailEl = document.getElementById('cprofile-email');
    if (emailEl) emailEl.textContent = customer.email;
    const phoneEl = document.getElementById('cprofile-phone');
    if (phoneEl) phoneEl.textContent = customer.phone;
    const dobEl = document.getElementById('cprofile-dob');
    if (dobEl) dobEl.textContent = customer.dob || 'N/A';
    const addrEl = document.getElementById('cprofile-address');
    if (addrEl) addrEl.textContent = customer.address || 'N/A';
    const badgeEl = document.getElementById('cprofile-status-badge');
    if (badgeEl) badgeEl.innerHTML = statusBadge(escapeHtml(customer.status));

    // Subscriptions
    const customerSubs = DB.subscriptions.filter(s => s.customerId === id);
    const activeSubs = customerSubs.filter(s => s.status === 'Active').length;
    const activePlansEl = document.getElementById('cprofile-active-plans');
    if (activePlansEl) activePlansEl.textContent = activeSubs;

    const subTbody = document.getElementById('cprofile-subs-tbody');
    if (subTbody) {
        subTbody.innerHTML = customerSubs.length === 0
            ? `<tr><td colspan="4" style="text-align:center;padding:16px;color:var(--text-muted);">No subscriptions yet.</td></tr>`
            : customerSubs.map(s => `<tr>
                <td class="col-id">#${escapeHtml(s.id)}</td>
                <td>${escapeHtml(s.planName)}</td>
                <td style="font-size:0.8rem;">${escapeHtml(s.startDate)} → ${escapeHtml(s.endDate)}</td>
                <td>${statusBadge(escapeHtml(s.status))}</td>
            </tr>`).join('');
    }

    // Payments
    const customerPayments = DB.payments.filter(p => p.customerId === id);
    const totalSpend = customerPayments.filter(p => p.status === 'Paid').reduce((sum, p) => sum + Number(p.amount), 0);
    const totalSpendEl = document.getElementById('cprofile-total-spend');
    if (totalSpendEl) totalSpendEl.textContent = `₹${totalSpend.toLocaleString('en-IN')}`;
    const txCountEl = document.getElementById('cprofile-tx-count');
    if (txCountEl) txCountEl.textContent = customerPayments.length;

    const payTbody = document.getElementById('cprofile-payments-tbody');
    if (payTbody) {
        payTbody.innerHTML = customerPayments.length === 0
            ? `<tr><td colspan="4" style="text-align:center;padding:16px;color:var(--text-muted);">No payments yet.</td></tr>`
            : customerPayments.map(p => `<tr>
                <td class="col-id">#${escapeHtml(p.id)}</td>
                <td style="font-weight:600;">₹${Number(p.amount).toLocaleString('en-IN')}</td>
                <td>${escapeHtml(p.paymentDate)}</td>
                <td>${statusBadge(escapeHtml(p.status))}</td>
            </tr>`).join('');
    }

    openModal('customer-profile-modal');
}

function openAddCustomer() {
    editingCustomerId = null;
    clearErrors('customer-form');
    document.getElementById('customer-form').reset();
    document.getElementById('customer-modal-title').textContent = 'Add New Customer';
    setInner('customer-id-display', 'Auto-generated');
    openModal('customer-modal');
}
function openEditCustomer(id) {
    const c = DB.customers.find(x => x.id === id);
    if (!c) return;
    editingCustomerId = id;
    clearErrors('customer-form');
    document.getElementById('cf-name').value    = c.name;
    document.getElementById('cf-email').value   = c.email;
    document.getElementById('cf-phone').value   = c.phone;
    document.getElementById('cf-dob').value     = c.dob;
    document.getElementById('cf-address').value = c.address;
    document.getElementById('cf-status').value  = c.status;
    document.getElementById('customer-modal-title').textContent = 'Edit Customer';
    setInner('customer-id-display', id);
    openModal('customer-modal');
}
async function saveCustomer() {
    const name    = document.getElementById('cf-name').value.trim();
    const email   = document.getElementById('cf-email').value.trim();
    const phone   = document.getElementById('cf-phone').value.trim();
    const dob     = document.getElementById('cf-dob').value;
    const address = document.getElementById('cf-address').value.trim();
    const status  = document.getElementById('cf-status').value;

    clearErrors('customer-form');
    let valid = true;
    if (!name)            { showError('cf-name', 'Name is required'); valid = false; }
    if (!isEmail(email))  { showError('cf-email','Enter a valid email'); valid = false; }
    if (!isPhone(phone))  { showError('cf-phone','Enter a valid 10-digit phone'); valid = false; }
    if (!dob)             { showError('cf-dob', 'Date of birth is required'); valid = false; }
    if (!address)         { showError('cf-address','Address is required'); valid = false; }
    if (!valid) return;

    try {
        const action = editingCustomerId ? 'update' : 'add';
        await requestApi('CustomerServlet', action, {
            id: editingCustomerId, name, email, phone, dob, address, status
        });
        await loadBackendData();
        closeModal('customer-modal');
        searchCustomers();
        populateCustomerSelectsOnOtherPages();
        showToast(editingCustomerId ? 'Customer updated successfully!' : 'Customer added successfully!');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function confirmDeleteCustomer(id) {
    customerDeleteId = id;
    const c = DB.customers.find(x => x.id === id);
    setInner('confirm-delete-name', c ? c.name : id);
    openModal('confirm-delete-modal');
}
async function deleteCustomerConfirmed() {
    if (!customerDeleteId) return;
    try {
        await requestApi('CustomerServlet', 'delete', { id: customerDeleteId });
        await loadBackendData();
        customerDeleteId = null;
        closeModal('confirm-delete-modal');
        searchCustomers();
        populateCustomerSelectsOnOtherPages();
        showToast('Customer deleted.');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function searchCustomers() {
    paginationState['customers'].page = 1;
    customerSearchQuery = (document.getElementById('customer-search') || {}).value || '';
    const statusFilter = (document.getElementById('customer-status-filter') || {}).value || '';
    let results = filterData(DB.customers, customerSearchQuery, ['id','name','email','phone']);
    if (statusFilter) {
        results = results.filter(c => c.status === statusFilter);
    }
    renderCustomerTable(results);
}
async function refreshCustomers() {
    paginationState['customers'].page = 1;
    try {
        await loadBackendData();
    } catch (error) {
        showToast(error.message, 'danger');
        return;
    }
    const el = document.getElementById('customer-search'); if (el) el.value = '';
    const stEl = document.getElementById('customer-status-filter'); if (stEl) stEl.value = '';
    customerSearchQuery = '';
    renderCustomerTable(DB.customers);
}
function populateCustomerSelectsOnOtherPages() {
    // subscription + payment customer dropdowns (if on same page)
    ['sf-customer','pf-customer'].forEach(id => {
        const sel = document.getElementById(id);
        if (!sel) return;
        const cur = sel.value;
        sel.innerHTML = '<option value="">-- Select Customer --</option>' +
            DB.customers.map(c => `<option value="${escapeHtml(c.id)}" ${c.id===cur?'selected':''}>${escapeHtml(c.name)}</option>`).join('');
    });
}

/* ================================================================
   ── PLANS ──
   ================================================================ */
let editingPlanId    = null;
let planDeleteId     = null;

function initPlans() {
    initializeBackendPage(() => {
        renderPlanTable(DB.plans);
        populatePlanSelects();
    });
}

function renderPlanTable(data) {
    const tbody = document.getElementById('plan-tbody');
    if (!tbody) return;
    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state"><h3>No plans found</h3></div></td></tr>`;
        return;
    }
    tbody.innerHTML = data.map(p => `
        <tr>
            <td class="col-id">${escapeHtml(p.id)}</td>
            <td><div class="cell-name">${escapeHtml(p.name)}</div></td>
            <td>${p.duration} ${p.durationUnit}</td>
            <td>₹${p.price.toLocaleString('en-IN')}</td>
            <td>${statusBadge(escapeHtml(p.status))}</td>
            <td><div class="actions-cell">
                <button class="btn btn-sm btn-outline" onclick="openEditPlan('${p.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    Edit
                </button>
                <button class="btn btn-sm btn-danger" onclick="confirmDeletePlan('${p.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    Delete
                </button>
            </div></td>
        </tr>`).join('');
    const countEl = document.getElementById('plan-count'); if(countEl) countEl.textContent = `Showing ${data.length} record(s)`;
}

function openAddPlan() {
    editingPlanId = null;
    clearErrors('plan-form');
    document.getElementById('plan-form').reset();
    document.getElementById('plan-modal-title').textContent = 'Add New Plan';
    setInner('plan-id-display','Auto-generated');
    openModal('plan-modal');
}
function openEditPlan(id) {
    const p = DB.plans.find(x => x.id === id);
    if (!p) return;
    editingPlanId = id;
    clearErrors('plan-form');
    document.getElementById('pf-name').value        = p.name;
    document.getElementById('pf-desc').value        = p.description;
    document.getElementById('pf-duration').value    = p.duration;
    document.getElementById('pf-duration-unit').value = p.durationUnit;
    document.getElementById('pf-price').value       = p.price;
    document.getElementById('pf-status').value      = p.status;
    ['hd-streaming','multiple-devices','offline-download','family-sharing'].forEach(fId => {
        const cb = document.getElementById(fId);
        if (cb) { const label = cb.dataset.label; cb.checked = p.features.includes(label); }
    });
    document.getElementById('plan-modal-title').textContent = 'Edit Plan';
    setInner('plan-id-display', id);
    openModal('plan-modal');
}
async function savePlan() {
    const name     = document.getElementById('pf-name').value.trim();
    const desc     = document.getElementById('pf-desc').value.trim();
    const duration = document.getElementById('pf-duration').value;
    const unit     = document.getElementById('pf-duration-unit').value;
    const price    = document.getElementById('pf-price').value;
    const status   = document.getElementById('pf-status').value;
    const features = [];
    ['hd-streaming','multiple-devices','offline-download','family-sharing'].forEach(fId => {
        const cb = document.getElementById(fId); if (cb && cb.checked) features.push(cb.dataset.label);
    });

    clearErrors('plan-form');
    let valid = true;
    if (!name)              { showError('pf-name', 'Plan name is required'); valid = false; }
    if (!isPositive(duration)) { showError('pf-duration', 'Duration must be a positive number'); valid = false; }
    if (!isPositive(price))    { showError('pf-price',    'Price must be a positive number'); valid = false; }
    if (!valid) return;

    try {
        const action = editingPlanId ? 'update' : 'add';
        await requestApi('PlanServlet', action, {
            id: editingPlanId, planName: name, description: desc, duration,
            durationUnit: unit, price, features, status
        });
        await loadBackendData();
        closeModal('plan-modal');
        searchPlans();
        populatePlanSelects();
        showToast(editingPlanId ? 'Plan updated successfully!' : 'Plan added successfully!');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function confirmDeletePlan(id) {
    planDeleteId = id;
    const p = DB.plans.find(x => x.id === id);
    setInner('confirm-delete-name', p ? p.name : id);
    openModal('confirm-delete-modal');
    window._deleteType = 'plan';
}
async function deletePlanConfirmed() {
    try {
        await requestApi('PlanServlet', 'delete', { id: planDeleteId });
        await loadBackendData();
        planDeleteId = null;
        closeModal('confirm-delete-modal');
        searchPlans();
        populatePlanSelects();
        showToast('Plan deleted.');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function searchPlans() {
    const q = (document.getElementById('plan-search') || {}).value || '';
    renderPlanTable(filterData(DB.plans, q, ['id','name','description']));
}
async function refreshPlans() {
    try {
        await loadBackendData();
    } catch (error) {
        showToast(error.message, 'danger');
        return;
    }
    const el = document.getElementById('plan-search'); if (el) el.value = '';
    renderPlanTable(DB.plans);
}
function populatePlanSelects() {
    ['sf-plan','pf-plan'].forEach(id => {
        const sel = document.getElementById(id);
        if (!sel) return;
        const cur = sel.value;
        sel.innerHTML = '<option value="">-- Select Plan --</option>' +
            DB.plans.map(p => `<option value="${escapeHtml(p.id)}" ${p.id===cur?'selected':''}>${escapeHtml(p.name)} (₹${p.price})</option>`).join('');
    });
}

/* ================================================================
   ── SUBSCRIPTIONS ──
   ================================================================ */
let editingSubId = null;
let subDeleteId  = null;

function initSubscriptions() {
    initializeBackendPage(() => {
        populateCustomerSelectsOnOtherPages();
        populatePlanSelects();
        renderSubTable(DB.subscriptions);
    });
}
function renderSubTable(data) {
    const tbody = document.getElementById('sub-tbody');
    if (!tbody) return;
    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8"><div class="empty-state"><h3>No subscriptions found</h3></div></td></tr>`;
        const pg = document.getElementById('sub-pagination'); if (pg) pg.innerHTML = '';
        const countEl = document.getElementById('sub-count'); if (countEl) countEl.textContent = 'Showing 0 record(s)';
        return;
    }
    const { paginatedData, start, totalRecords } = paginate(data, 'subscriptions');
    tbody.innerHTML = paginatedData.map(s => `
        <tr>
            <td class="col-id">${escapeHtml(s.id)}</td>
            <td><div class="cell-name">${escapeHtml(s.customerName)}</div></td>
            <td>${escapeHtml(s.planName)}</td>
            <td>${escapeHtml(s.startDate)}</td>
            <td>${escapeHtml(s.endDate)}</td>
            <td>${statusBadge(escapeHtml(s.status))}</td>
            <td><span class="badge ${s.autoRenewal?'badge-active':'badge-inactive'}">${s.autoRenewal?'Yes':'No'}</span></td>
            <td><div class="actions-cell">
                <button class="btn btn-sm btn-outline" onclick="openEditSub('${s.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    Edit
                </button>
                <button class="btn btn-sm btn-danger" onclick="confirmDeleteSub('${s.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    Delete
                </button>
            </div></td>
        </tr>`).join('');
    const countEl = document.getElementById('sub-count');
    if (countEl) countEl.textContent = `Showing ${start + 1}–${Math.min(start + paginatedData.length, totalRecords)} of ${totalRecords} record(s)`;
}

function openAddSub() {
    editingSubId = null;
    clearErrors('sub-form');
    document.getElementById('sub-form').reset();
    document.getElementById('sub-modal-title').textContent = 'Add New Subscription';
    setInner('sub-id-display','Auto-generated');
    openModal('sub-modal');
}
function openEditSub(id) {
    const s = DB.subscriptions.find(x => x.id === id);
    if (!s) return;
    editingSubId = id;
    clearErrors('sub-form');
    document.getElementById('sf-customer').value    = s.customerId;
    document.getElementById('sf-plan').value        = s.planId;
    document.getElementById('sf-start').value       = s.startDate;
    document.getElementById('sf-end').value         = s.endDate;
    document.getElementById('sf-status').value      = s.status;
    document.getElementById('sf-auto-renewal').checked = s.autoRenewal;
    document.getElementById('sub-modal-title').textContent = 'Edit Subscription';
    setInner('sub-id-display', id);
    openModal('sub-modal');
}
async function saveSub() {
    const customerId = document.getElementById('sf-customer').value;
    const planId     = document.getElementById('sf-plan').value;
    const startDate  = document.getElementById('sf-start').value;
    const endDate    = document.getElementById('sf-end').value;
    const status     = document.getElementById('sf-status').value;
    const autoRenewal = document.getElementById('sf-auto-renewal').checked;

    clearErrors('sub-form');
    let valid = true;
    if (!customerId) { showError('sf-customer','Please select a customer'); valid = false; }
    if (!planId)     { showError('sf-plan',    'Please select a plan');     valid = false; }
    if (!startDate)  { showError('sf-start',   'Start date is required');   valid = false; }
    if (!endDate)    { showError('sf-end',     'End date is required');     valid = false; }
    if (startDate && endDate && endDate <= startDate) { showError('sf-end','End date must be after start date'); valid = false; }
    if (!valid) return;

    try {
        const action = editingSubId ? 'update' : 'add';
        await requestApi('SubscriptionServlet', action, {
            id: editingSubId, customerId, planId, startDate, endDate, status,
            autoRenewal: autoRenewal ? 'true' : 'false'
        });
        await loadBackendData();
        closeModal('sub-modal');
        filterSubs();
        populateCustomerSelectsOnOtherPages();
        populatePlanSelects();
        showToast(editingSubId ? 'Subscription updated!' : 'Subscription added!');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function confirmDeleteSub(id) {
    subDeleteId = id;
    const s = DB.subscriptions.find(x => x.id === id);
    setInner('confirm-delete-name', s ? s.id : id);
    window._deleteType = 'sub';
    openModal('confirm-delete-modal');
}
async function deleteSubConfirmed() {
    try {
        await requestApi('SubscriptionServlet', 'delete', { id: subDeleteId });
        await loadBackendData();
        subDeleteId = null;
        closeModal('confirm-delete-modal');
        filterSubs();
        populateSubscriptionSelects();
        showToast('Subscription deleted.');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function filterSubs() {
    paginationState['subscriptions'].page = 1;
    const query  = (document.getElementById('sub-search') || {}).value || '';
    const planF  = (document.getElementById('sub-plan-filter') || {}).value || '';
    const statusF= (document.getElementById('sub-status-filter') || {}).value || '';
    let data = DB.subscriptions;
    if (query)  data = filterData(data, query, ['customerName','planName','id']);
    if (planF)  data = data.filter(s => s.planId === planF);
    if (statusF)data = data.filter(s => s.status === statusF);
    renderSubTable(data);
}
async function refreshSubs() {
    paginationState['subscriptions'].page = 1;
    try {
        await loadBackendData();
    } catch (error) {
        showToast(error.message, 'danger');
        return;
    }
    ['sub-search','sub-plan-filter','sub-status-filter'].forEach(id => { const el=document.getElementById(id); if(el) el.value=''; });
    renderSubTable(DB.subscriptions);
}

/* ================================================================
   ── PAYMENTS ──
   ================================================================ */
let editingPayId = null;
let payDeleteId  = null;

function initPayments() {
    initializeBackendPage(() => {
        populateCustomerSelectsOnOtherPages();
        populatePlanSelects();
        populateSubscriptionSelects();
        renderPaymentTable(DB.payments);
    });
}
function populateSubscriptionSelects() {
    const sel = document.getElementById('pf-sub');
    if (!sel) return;
    const cur = sel.value;
    sel.innerHTML = '<option value="">-- Select Subscription --</option>' +
        DB.subscriptions.map(s => `<option value="${escapeHtml(s.id)}" ${s.id===cur?'selected':''}>${escapeHtml(s.id)} – ${escapeHtml(s.customerName)} (${escapeHtml(s.planName)})</option>`).join('');
}
function renderPaymentTable(data) {
    const tbody = document.getElementById('payment-tbody');
    if (!tbody) return;
    if (data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7"><div class="empty-state"><h3>No payments found</h3></div></td></tr>`;
        const pg = document.getElementById('payment-pagination'); if (pg) pg.innerHTML = '';
        const countEl = document.getElementById('payment-count'); if (countEl) countEl.textContent = 'Showing 0 record(s)';
        return;
    }
    const { paginatedData, start, totalRecords } = paginate(data, 'payments');
    tbody.innerHTML = paginatedData.map(p => `
        <tr>
            <td class="col-id">${escapeHtml(p.id)}</td>
            <td><div class="cell-name">${escapeHtml(p.customerName)}</div></td>
            <td>${escapeHtml(p.planName)}</td>
            <td>₹${p.amount.toLocaleString('en-IN')}</td>
            <td>${escapeHtml(p.paymentDate)}</td>
            <td>${statusBadge(escapeHtml(p.status))}</td>
            <td><div class="actions-cell">
                <button class="btn btn-sm btn-outline" onclick="viewInvoice('${p.id}')" title="View & Download Invoice">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                    Invoice
                </button>
                <button class="btn btn-sm btn-outline" onclick="openEditPayment('${p.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                    Edit
                </button>
                <button class="btn btn-sm btn-danger" onclick="confirmDeletePayment('${p.id}')">
                    <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                    Delete
                </button>
            </div></td>
        </tr>`).join('');
    const countEl = document.getElementById('payment-count');
    if (countEl) countEl.textContent = `Showing ${start + 1}–${Math.min(start + paginatedData.length, totalRecords)} of ${totalRecords} record(s)`;
}
function openAddPayment() {
    editingPayId = null;
    clearErrors('payment-form');
    document.getElementById('payment-form').reset();
    document.getElementById('payment-modal-title').textContent = 'Add Payment';
    setInner('payment-id-display','Auto-generated');
    openModal('payment-modal');
}
function openEditPayment(id) {
    const p = DB.payments.find(x => x.id === id);
    if (!p) return;
    editingPayId = id;
    clearErrors('payment-form');
    document.getElementById('pf-customer').value     = p.customerId;
    document.getElementById('pf-sub').value          = p.subscriptionId;
    document.getElementById('pf-amount').value       = p.amount;
    document.getElementById('pf-pay-date').value     = p.paymentDate;
    document.getElementById('pf-pay-status').value   = p.status;
    document.getElementById('payment-modal-title').textContent = 'Edit Payment';
    setInner('payment-id-display', id);
    openModal('payment-modal');
}
async function savePayment() {
    const customerId     = document.getElementById('pf-customer').value;
    const subscriptionId = document.getElementById('pf-sub').value;
    const amount         = document.getElementById('pf-amount').value;
    const paymentDate    = document.getElementById('pf-pay-date').value;
    const status         = document.getElementById('pf-pay-status').value;

    clearErrors('payment-form');
    let valid = true;
    if (!customerId)        { showError('pf-customer',  'Customer is required'); valid = false; }
    if (!subscriptionId)    { showError('pf-sub',       'Subscription is required'); valid = false; }
    if (!isPositive(amount)){ showError('pf-amount',    'Amount must be positive'); valid = false; }
    if (!paymentDate)       { showError('pf-pay-date',  'Payment date is required'); valid = false; }
    if (!status)            { showError('pf-pay-status','Status is required'); valid = false; }
    if (!valid) return;

    try {
        const action = editingPayId ? 'update' : 'add';
        await requestApi('PaymentServlet', action, {
            id: editingPayId, customerId, subscriptionId, amount, paymentDate,
            paymentStatus: status
        });
        await loadBackendData();
        closeModal('payment-modal');
        searchPayments();
        populateCustomerSelectsOnOtherPages();
        populateSubscriptionSelects();
        showToast(editingPayId ? 'Payment updated!' : 'Payment recorded!');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function confirmDeletePayment(id) {
    payDeleteId = id;
    const p = DB.payments.find(x => x.id === id);
    setInner('confirm-delete-name', p ? p.id : id);
    window._deleteType = 'payment';
    openModal('confirm-delete-modal');
}
async function deletePaymentConfirmed() {
    try {
        await requestApi('PaymentServlet', 'delete', { id: payDeleteId });
        await loadBackendData();
        payDeleteId = null;
        closeModal('confirm-delete-modal');
        searchPayments();
        showToast('Payment deleted.');
    } catch (error) {
        showToast(error.message, 'danger');
    }
}
function searchPayments() {
    paginationState['payments'].page = 1;
    const q = (document.getElementById('payment-search') || {}).value || '';
    const statusFilter = (document.getElementById('payment-status-filter') || {}).value || '';
    let results = filterData(DB.payments, q, ['id','customerName','planName']);
    if (statusFilter) {
        results = results.filter(p => p.status === statusFilter);
    }
    renderPaymentTable(results);
}
async function refreshPayments() {
    paginationState['payments'].page = 1;
    try {
        await loadBackendData();
    } catch (error) {
        showToast(error.message, 'danger');
        return;
    }
    const el = document.getElementById('payment-search'); if (el) el.value = '';
    const stEl = document.getElementById('payment-status-filter'); if (stEl) stEl.value = '';
    renderPaymentTable(DB.payments);
}

/* ================================================================
   ── CONFIRM DELETE DISPATCHER ──
   ================================================================ */
function deleteConfirmed() {
    const type = window._deleteType || 'customer';
    if (type === 'customer') deleteCustomerConfirmed();
    else if (type === 'plan') deletePlanConfirmed();
    else if (type === 'sub') deleteSubConfirmed();
    else if (type === 'payment') deletePaymentConfirmed();
}
window._deleteType = 'customer';

/* ================================================================
   ── REPORTS (Database-backed Analytics) ──
   ================================================================ */
async function generateReport() {
    const from   = document.getElementById('rpt-from').value;
    const to     = document.getElementById('rpt-to').value;
    const type   = document.getElementById('rpt-type').value;

    let valid = true;
    if (!from) { showToast('Please select From date.','warning'); valid = false; }
    if (!to)   { showToast('Please select To date.','warning');   valid = false; }
    if (!type) { showToast('Please select report type.','warning'); valid = false; }
    if (from && to && from > to) { showToast('From date must be before To date.','warning'); valid = false; }
    if (!valid) return;

    const container = document.getElementById('report-results');
    if (!container) return;

    container.innerHTML = `<div style="text-align:center;padding:30px;color:var(--text-muted);">
        <div style="margin-bottom:8px;">Fetching real-time analytics from database...</div>
    </div>`;
    container.style.display = 'block';

    try {
        const res = await requestApi('ReportServlet', 'generate', { from, to, type });
        let html = `<div style="padding: 20px 24px 16px 24px; border-bottom: 1px solid var(--border-color); background: var(--bg-card); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
            <div>
                <h3 style="margin: 0; font-size: 1.25rem; font-weight: 700; color: var(--text-primary); display: flex; align-items: center; gap: 10px;">
                    ${escapeHtml(type)}
                    <span class="badge badge-active" style="font-size: 0.75rem; text-transform: none; font-weight: 500;">Live SQL</span>
                </h3>
                <div style="color: var(--text-secondary); font-size: 0.875rem; margin-top: 4px;">
                    Date Filter: <strong>${escapeHtml(from)}</strong> &rarr; <strong>${escapeHtml(to)}</strong>
                </div>
            </div>
            <div style="display: flex; gap: 10px;">
                <button class="btn btn-sm btn-outline" onclick="printReportResults()">
                    <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                    </svg>
                    Print Report
                </button>
                <button class="btn btn-sm btn-outline" onclick="exportReportToCSV()">
                    <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
                    Download CSV
                </button>
            </div>
        </div>`;

        if (type === 'Revenue Report') {
            const rows = res.rows || [];
            const totalPaid = Number(res.totalPaid || 0);
            const totalPending = Number(res.totalPending || 0);
            const totalSubs = Number(res.totalSubscriptions || 0);

            html += `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; padding: 20px 24px; background: rgba(0,0,0,0.02); border-bottom: 1px solid var(--border-color);">
                <div style="background: var(--bg-card); padding: 16px; border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
                    <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600;">Total Paid Revenue</div>
                    <div style="font-size: 1.6rem; font-weight: 800; color: var(--color-success); margin-top: 4px;">₹${totalPaid.toLocaleString('en-IN')}</div>
                </div>
                <div style="background: var(--bg-card); padding: 16px; border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
                    <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600;">Pending Invoices</div>
                    <div style="font-size: 1.6rem; font-weight: 800; color: var(--color-warning); margin-top: 4px;">₹${totalPending.toLocaleString('en-IN')}</div>
                </div>
                <div style="background: var(--bg-card); padding: 16px; border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
                    <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600;">Active Subscriptions</div>
                    <div style="font-size: 1.6rem; font-weight: 800; color: var(--color-primary); margin-top: 4px;">${totalSubs}</div>
                </div>
            </div>
            <div class="table-wrapper" style="border: none; border-radius: 0;">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="padding-left: 24px;">Plan Name</th>
                            <th style="text-align: center;">Subscriptions</th>
                            <th style="text-align: right;">Paid Revenue (₹)</th>
                            <th style="text-align: right;">Pending (₹)</th>
                            <th style="text-align: right; padding-right: 24px;">Total Billed (₹)</th>
                        </tr>
                    </thead>
                    <tbody>
                    ${rows.length === 0 ? `<tr><td colspan="5" style="text-align:center;padding:30px;color:var(--text-muted);">No revenue records found for this period.</td></tr>` : 
                    rows.map(r => {
                        const paid = Number(r.paidRevenue || 0);
                        const pending = Number(r.pendingRevenue || 0);
                        const total = Number(r.totalAmount || (paid + pending));
                        return `<tr>
                            <td style="padding-left: 24px;">
                                <div style="font-weight: 700; color: var(--text-primary);">${escapeHtml(r.planName)}</div>
                            </td>
                            <td style="text-align: center;"><span class="badge badge-active">${Number(r.subscriptions || 0)}</span></td>
                            <td style="text-align: right; font-weight: 600; color: var(--color-success);">₹${paid.toLocaleString('en-IN')}</td>
                            <td style="text-align: right; font-weight: 600; color: ${pending > 0 ? 'var(--color-warning)' : 'var(--text-muted)'};">₹${pending.toLocaleString('en-IN')}</td>
                            <td style="text-align: right; padding-right: 24px; font-weight: 700;">₹${total.toLocaleString('en-IN')}</td>
                        </tr>`;
                    }).join('')}
                    </tbody>
                    <tfoot>
                        <tr style="font-weight: 800; background: rgba(0,0,0,0.03); border-top: 2px solid var(--border-color);">
                            <td style="padding-left: 24px;">Total Summary</td>
                            <td style="text-align: center;">${totalSubs}</td>
                            <td style="text-align: right; color: var(--color-success);">₹${totalPaid.toLocaleString('en-IN')}</td>
                            <td style="text-align: right; color: var(--color-warning);">₹${totalPending.toLocaleString('en-IN')}</td>
                            <td style="text-align: right; padding-right: 24px; color: var(--color-primary); font-size: 1.1rem;">₹${(totalPaid + totalPending).toLocaleString('en-IN')}</td>
                        </tr>
                    </tfoot>
                </table>
            </div>`;
        } else if (type === 'Subscription Report') {
            const rows = res.rows || [];
            const totalCount = Number(res.totalCount || 0);

            html += `
            <div class="table-wrapper" style="border: none; border-radius: 0;">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="padding-left: 24px;">Subscription Status</th>
                            <th style="text-align: right; padding-right: 24px;">Count</th>
                        </tr>
                    </thead>
                    <tbody>
                    ${rows.length === 0 ? `<tr><td colspan="2" style="text-align:center;padding:30px;color:var(--text-muted);">No subscriptions started in this date range.</td></tr>` :
                    rows.map(r => `<tr>
                        <td style="padding-left: 24px;">${statusBadge(escapeHtml(r.status))}</td>
                        <td style="text-align: right; padding-right: 24px; font-weight: 700; font-size: 1.05rem;">${Number(r.count || 0).toLocaleString('en-IN')}</td>
                    </tr>`).join('')}
                    </tbody>
                    <tfoot>
                        <tr style="font-weight: 800; background: rgba(0,0,0,0.03); border-top: 2px solid var(--border-color);">
                            <td style="padding-left: 24px;">Total Active & Tracked Subscriptions</td>
                            <td style="text-align: right; padding-right: 24px; color: var(--color-primary); font-size: 1.1rem;">${totalCount.toLocaleString('en-IN')}</td>
                        </tr>
                    </tfoot>
                </table>
            </div>`;
        } else if (type === 'Customer Report') {
            const rows = res.rows || [];
            html += `
            <div class="table-wrapper" style="border: none; border-radius: 0;">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="padding-left: 24px;">Customer Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th style="text-align: center;">Account Status</th>
                            <th style="text-align: right; padding-right: 24px;">Subscriptions</th>
                        </tr>
                    </thead>
                    <tbody>
                    ${rows.length === 0 ? `<tr><td colspan="5" style="text-align:center;padding:30px;color:var(--text-muted);">No customer records found.</td></tr>` :
                    rows.map(c => `<tr>
                        <td style="padding-left: 24px;"><div class="cell-name">${escapeHtml(c.name)}</div></td>
                        <td>${escapeHtml(c.email)}</td>
                        <td>${escapeHtml(c.phone)}</td>
                        <td style="text-align: center;">${statusBadge(escapeHtml(c.status))}</td>
                        <td style="text-align: right; padding-right: 24px; font-weight: 700;">${Number(c.subscriptions || 0)}</td>
                    </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
        } else {
            // Payment Report
            const rows = res.rows || [];
            const totalAmt = Number(res.totalAmount || 0);
            const paidAmt = Number(res.paidAmount || 0);

            html += `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; padding: 20px 24px; background: rgba(0,0,0,0.02); border-bottom: 1px solid var(--border-color);">
                <div style="background: var(--bg-card); padding: 16px; border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
                    <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600;">Total Payments Recorded</div>
                    <div style="font-size: 1.5rem; font-weight: 800; color: var(--text-primary); margin-top: 4px;">₹${totalAmt.toLocaleString('en-IN')}</div>
                </div>
                <div style="background: var(--bg-card); padding: 16px; border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
                    <div style="font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; font-weight: 600;">Total Settled (Paid)</div>
                    <div style="font-size: 1.5rem; font-weight: 800; color: var(--color-success); margin-top: 4px;">₹${paidAmt.toLocaleString('en-IN')}</div>
                </div>
            </div>
            <div class="table-wrapper" style="border: none; border-radius: 0;">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="padding-left: 24px;">ID</th>
                            <th>Customer</th>
                            <th>Plan</th>
                            <th style="text-align: right;">Amount (₹)</th>
                            <th>Date</th>
                            <th style="text-align: center; padding-right: 24px;">Status</th>
                        </tr>
                    </thead>
                    <tbody>
                    ${rows.length === 0 ? `<tr><td colspan="6" style="text-align:center;padding:30px;color:var(--text-muted);">No payments recorded in this date range.</td></tr>` :
                    rows.map(p => `<tr>
                        <td style="padding-left: 24px;" class="col-id">#${escapeHtml(String(p.id))}</td>
                        <td><div style="font-weight: 600;">${escapeHtml(p.customerName)}</div></td>
                        <td>${escapeHtml(p.planName)}</td>
                        <td style="text-align: right; font-weight: 700;">₹${Number(p.amount || 0).toLocaleString('en-IN')}</td>
                        <td>${escapeHtml(p.paymentDate)}</td>
                        <td style="text-align: center; padding-right: 24px;">${statusBadge(escapeHtml(p.status))}</td>
                    </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
        }

        container.innerHTML = html;
        showToast('Report generated directly from database!');
    } catch (error) {
        container.innerHTML = `<div style="text-align:center;padding:24px;color:var(--color-danger);">
            <strong>Failed to generate report:</strong> ${escapeHtml(error.message)}
        </div>`;
        showToast(error.message, 'danger');
    }
}

function printReportResults() {
    const reportContainer = document.getElementById('report-results');
    if (!reportContainer) return;
    const printWindow = window.open('', '', 'height=750,width=900');
    printWindow.document.write(`
        <html>
            <head>
                <title>Subscription Management System - Analytical Report</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; padding: 32px; color: #1e293b; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 14px; }
                    th, td { border: 1px solid #cbd5e1; padding: 10px 14px; }
                    th { background: #f1f5f9; text-align: left; font-weight: 700; }
                    .badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
                    .badge-active { background: #dcfce7; color: #15803d; }
                    .badge-pending { background: #fef9c3; color: #854d0e; }
                    .badge-failed { background: #fee2e2; color: #b91c1c; }
                    button { display: none !important; }
                    @media print {
                        body { padding: 0; }
                        button { display: none !important; }
                    }
                </style>
            </head>
            <body>
                <div style="text-align: center; margin-bottom: 24px; border-bottom: 2px solid #0f172a; padding-bottom: 12px;">
                    <h2 style="margin: 0; font-size: 20px; text-transform: uppercase;">Subscription Management System</h2>
                    <p style="margin: 4px 0 0 0; color: #64748b; font-size: 13px;">Official Analytical Report — Generated on \${new Date().toLocaleString()}</p>
                </div>
                \${reportContainer.innerHTML}
                <script>
                    window.onload = function() {
                        window.print();
                        window.close();
                    };
                <\/script>
            </body>
        </html>
    `);
    printWindow.document.close();
}

function viewInvoice(id) {
    currentInvoicePaymentId = id;
    const payment = DB.payments.find(p => p.id === id);
    if (!payment) return;
    const sub = DB.subscriptions.find(s => s.id === payment.subscriptionId);
    const customer = DB.customers.find(c => c.id === payment.customerId);

    const invoiceHtml = `
        <!-- Invoice Header -->
        <div style="border-bottom: 2px solid var(--border-color); padding-bottom: 24px; margin-bottom: 28px; display: flex; justify-content: space-between; align-items: flex-start;">
            <div>
                <h1 style="margin: 0 0 6px 0; color: var(--color-primary); font-size: 1.8rem; letter-spacing: 0.5px; font-weight: 800;">INVOICE</h1>
                <div style="color: var(--text-primary); font-weight: 600; font-size: 0.95rem;">Invoice #: INV-${escapeHtml(payment.id.padStart(5, '0'))}</div>
                <div style="color: var(--text-muted); font-size: 0.85rem; margin-top: 2px;">Issue Date: ${escapeHtml(payment.paymentDate)}</div>
            </div>
            <div style="text-align: right;">
                <div style="font-size: 1.25rem; font-weight: 800; color: var(--text-primary); letter-spacing: -0.2px;">Subscription MS</div>
                <div style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 4px;">Billing & Account Management</div>
                <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 2px;">support@subscriptionms.com</div>
            </div>
        </div>

        <!-- Billing & Subscription Details Grid -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 32px; margin-bottom: 32px; padding: 20px; background: rgba(0,0,0,0.02); border-radius: var(--border-radius-md); border: 1px solid var(--border-color);">
            <div>
                <div style="font-size: 0.75rem; text-transform: uppercase; font-weight: 700; color: var(--text-muted); letter-spacing: 0.5px; margin-bottom: 8px;">Billed To</div>
                <div style="font-size: 1.05rem; font-weight: 700; color: var(--text-primary); margin-bottom: 4px;">${escapeHtml(payment.customerName)}</div>
                <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.5;">${escapeHtml(customer?.email || 'N/A')}</div>
                <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.5;">${escapeHtml(customer?.phone || 'N/A')}</div>
                <div style="color: var(--text-muted); font-size: 0.85rem; margin-top: 4px;">${escapeHtml(customer?.address || '')}</div>
            </div>
            <div style="text-align: right;">
                <div style="font-size: 0.75rem; text-transform: uppercase; font-weight: 700; color: var(--text-muted); letter-spacing: 0.5px; margin-bottom: 8px;">Payment Details</div>
                <div style="margin-bottom: 8px;">${statusBadge(escapeHtml(payment.status))}</div>
                <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.5;">Subscription ID: <strong>#${escapeHtml(payment.subscriptionId)}</strong></div>
                <div style="color: var(--text-secondary); font-size: 0.875rem; line-height: 1.5;">Selected Plan: <strong>${escapeHtml(payment.planName)}</strong></div>
            </div>
        </div>

        <!-- Items Table -->
        <table style="width: 100%; border-collapse: collapse; margin-bottom: 28px;">
            <thead>
                <tr style="border-bottom: 2px solid var(--border-color); text-align: left;">
                    <th style="padding: 12px 8px; font-size: 0.85rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Description</th>
                    <th style="padding: 12px 8px; text-align: right; font-size: 0.85rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700;">Amount</th>
                </tr>
            </thead>
            <tbody>
                <tr style="border-bottom: 1px solid var(--border-color);">
                    <td style="padding: 16px 8px;">
                        <div style="font-size: 1rem; font-weight: 700; color: var(--text-primary);">${escapeHtml(payment.planName)} Plan</div>
                        <div style="font-size: 0.825rem; color: var(--text-secondary); margin-top: 4px;">
                            Billing Period: ${sub ? `${escapeHtml(sub.startDate)} to ${escapeHtml(sub.endDate)}` : 'Standard Cycle'}
                        </div>
                    </td>
                    <td style="padding: 16px 8px; text-align: right; font-weight: 700; font-size: 1.05rem; color: var(--text-primary);">
                        ₹${payment.amount.toLocaleString('en-IN')}
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td style="padding: 18px 8px 6px 8px; font-weight: 800; font-size: 1.1rem; color: var(--text-primary);">Total Paid</td>
                    <td style="padding: 18px 8px 6px 8px; text-align: right; font-weight: 800; font-size: 1.35rem; color: var(--color-primary);">
                        ₹${payment.amount.toLocaleString('en-IN')}
                    </td>
                </tr>
            </tfoot>
        </table>

        <!-- Footer note -->
        <div style="font-size: 0.825rem; color: var(--text-muted); text-align: center; border-top: 1px dashed var(--border-color); padding-top: 20px; line-height: 1.5;">
            Thank you for choosing Subscription MS! This is an official computer-generated receipt.
        </div>
    `;

    document.getElementById('invoice-content').innerHTML = invoiceHtml;
    openModal('invoice-modal');
}

function printInvoice() {
    const printContents = document.getElementById('invoice-content').innerHTML;
    const printWindow = window.open('', '', 'height=600,width=800');
    printWindow.document.write(`
        <html>
            <head>
                <title>Invoice</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; padding: 30px; color: #1e293b; }
                    .badge { display: inline-block; padding: 2px 8px; border-radius: 9999px; font-size: 12px; font-weight: 600; text-transform: uppercase; }
                    .badge-active { background: #dcfce7; color: #15803d; }
                    .badge-paid { background: #dcfce7; color: #15803d; }
                    .badge-pending { background: #fef9c3; color: #a16207; }
                    .badge-failed { background: #fee2e2; color: #b91c1c; }
                    @media print { body { -webkit-print-color-adjust: exact; print-color-adjust: exact; } }
                </style>
            </head>
            <body>
                ${printContents}
            </body>
        </html>
    `);
    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
        printWindow.print();
        printWindow.close();
    }, 250);
}

function exportReportToCSV() {
    const table = document.querySelector('#report-results table');
    if (!table) {
        showToast('Please generate a report first before exporting.', 'warning');
        return;
    }

    const type = document.getElementById('rpt-type')?.value || 'report';
    const from = document.getElementById('rpt-from')?.value || '';
    const to = document.getElementById('rpt-to')?.value || '';

    const rows = Array.from(table.querySelectorAll('tr'));
    const csvContent = rows.map(row => {
        const cells = Array.from(row.querySelectorAll('th, td'));
        return cells.map(cell => {
            let text = cell.innerText.trim().replace(/"/g, '""');
            return `"${text}"`;
        }).join(',');
    }).join('\r\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    const filename = `${type.replace(/\s+/g, '_').toLowerCase()}_${from}_${to}.csv`;
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    showToast('Report exported as CSV successfully!');
}

/* ================================================================
   ── LOGIN ──
   ================================================================ */
function doLogin(e) {
    const user = document.getElementById('login-username')?.value.trim();
    const pass = document.getElementById('login-password')?.value.trim();
    const role = document.getElementById('login-role')?.value || 'Admin';

    let valid = true;
    if (!user) { showError('login-username','Username is required'); valid = false; }
    if (!pass) { showError('login-password','Password is required'); valid = false; }
    
    if (!valid) {
        if (e) e.preventDefault();
        return false;
    }
    // If valid, allow the form to submit to LoginServlet natively
    return true;
}

/* ================================================================
   ── SIDEBAR ACTIVE STATE ──
   ================================================================ */
function setActiveSidebarItem(pageName) {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.page === pageName) item.classList.add('active');
    });
}

function toggleDarkMode() { document.body.classList.toggle('dark-mode'); localStorage.setItem('theme', document.body.classList.contains('dark-mode') ? 'dark' : 'light'); }
document.addEventListener('DOMContentLoaded', () => { if (localStorage.getItem('theme') === 'dark') document.body.classList.add('dark-mode'); });

/* -- Sidebar Collapse -- */
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const mainArea = document.querySelector('.main-area');
    const isCollapsed = sidebar.classList.toggle('collapsed');
    if (mainArea) mainArea.classList.toggle('sidebar-collapsed', isCollapsed);
    localStorage.setItem('sidebarCollapsed', isCollapsed ? 'true' : 'false');
}

// Apply saved sidebar state on page load
document.addEventListener('DOMContentLoaded', function () {
    if (localStorage.getItem('sidebarCollapsed') === 'true') {
        const sidebar = document.querySelector('.sidebar');
        const mainArea = document.querySelector('.main-area');
        if (sidebar) sidebar.classList.add('collapsed');
        if (mainArea) mainArea.classList.add('sidebar-collapsed');
    }
});
