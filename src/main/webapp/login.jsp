<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Login to Subscription Management System – Admin Portal">
    <title>Login – Subscription Management System</title>

    <link rel="stylesheet" href="css/variables.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/forms.css">
</head>
<body>

<div class="login-page">
    <div class="login-card">

        <!-- Logo -->
        <div class="login-logo" style="background: #fff; box-shadow: none; border-radius: 50%; overflow: hidden;">
            <img src="images/logo.jpeg" alt="Logo" style="width: 100%; height: 100%; object-fit: contain; mix-blend-mode: multiply; filter: contrast(1.5) brightness(1.1);">
        </div>

        <h1 class="login-title">Subscription Management System</h1>
        <p class="login-subtitle">Login to continue</p>

        <!-- Error message from Servlet (future) -->
        <%
            String loginError = (String) request.getAttribute("loginError");
            if (loginError != null && !loginError.isEmpty()) {
        %>
        <div class="login-error-box">
            <%= loginError %>
        </div>
        <% } %>

        <!-- Login Form -->
        <form id="login-form" action="LoginServlet" method="post" onsubmit="doLogin(event)">

            <!-- Username -->
            <div class="form-group">
                <label class="form-label" for="login-username">Username <span class="required">*</span></label>
                <div class="input-icon-wrap">
                    <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                    <input type="text" id="login-username" name="username" class="form-control"
                           placeholder="Enter username" autocomplete="username" required>
                </div>
                <div class="form-error" id="err-username"></div>
            </div>

            <!-- Password -->
            <div class="form-group">
                <label class="form-label" for="login-password">Password <span class="required">*</span></label>
                <div class="input-icon-wrap">
                    <svg class="input-icon" xmlns="http://www.w3.org/2000/svg" fill="none"
                         viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                    </svg>
                    <input type="password" id="login-password" name="password" class="form-control"
                           placeholder="Enter password" autocomplete="current-password" required>
                </div>
                <div class="form-error" id="err-password"></div>
            </div>

            <!-- Login As (Role Selection) -->
            <div class="form-group">
                <label class="form-label" for="login-role">Login as</label>
                <select id="login-role" name="role" class="form-control">
                    <option value="Admin">Admin</option>
                    <option value="Customer">Customer</option>
                </select>
            </div>

            <!-- Remember Me + Forgot Password -->
            <div class="login-footer-links">
                <label class="checkbox-item">
                    <input type="checkbox" id="remember-me" name="rememberMe">
                    Remember me
                </label>
                <a class="login-link" href="#" onclick="alert('Please contact the administrator to reset your password.')">
                    Forgot Password?
                </a>
            </div>

            <!-- Submit -->
            <button type="submit" id="login-btn" class="btn btn-primary login-btn">
                <svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" fill="none"
                     viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/>
                </svg>
                Login
            </button>
        </form>

    </div>
</div>

<script src="js/app.js"></script>
<script>
    // Override doLogin for JSP direct navigation
    document.getElementById('login-form').addEventListener('submit', function(e) {
        // e.preventDefault(); // Removed because we want to submit to LoginServlet
        const user = document.getElementById('login-username').value.trim();
        const pass = document.getElementById('login-password').value.trim();
        let valid = true;
        clearFormErrors();
        if (!user) { markError('login-username','err-username','Username is required'); valid = false; }
        if (!pass) { markError('login-password','err-password','Password is required'); valid = false; }
        
        if (!valid) {
            e.preventDefault(); // Only prevent submission if validation fails
        }
    });

    function markError(fieldId, errId, msg) {
        const field = document.getElementById(fieldId);
        const err   = document.getElementById(errId);
        if (field) field.classList.add('is-invalid');
        if (err)   { err.textContent = msg; err.classList.add('visible'); }
    }
    function clearFormErrors() {
        document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));
        document.querySelectorAll('.form-error').forEach(el => el.classList.remove('visible'));
    }
    // Login page pe dark mode KABHI apply nahi hogi
    document.body.classList.remove('dark-mode');
</script>
</body>
</html>
