<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String prevUserId = (String) request.getAttribute("prevUserId");
    String prevName = (String) request.getAttribute("prevName");
    if (prevUserId == null) prevUserId = "";
    if (prevName == null) prevName = "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Register - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="auth-page">
<div class="auth-shell">
    <section class="auth-side">
        <div class="auth-brand">
            <div class="brand-icon">TR</div>
            <span class="auth-tag">BUPT International School</span>
            <h1 class="auth-title">Create TA Account</h1>
            <p class="auth-copy">Register as a Teaching Assistant to browse open positions and submit applications.</p>
        </div>
        <div class="auth-side-note">
            <strong>TA accounts only.</strong><br />
            This form creates a TA account. Module Organiser and Administrator accounts are issued by the system administrator.
        </div>
    </section>

    <section class="auth-main">
        <h2 class="auth-panel-title">Register</h2>
        <p class="auth-panel-subtitle">Create your TA account to get started.</p>

        <% if (error != null) { %>
        <div class="auth-alert auth-alert--error"><span><%= error %></span></div>
        <% } %>
        <% if (success != null) { %>
        <div class="auth-alert auth-alert--success"><span><%= success %></span></div>
        <% } %>

        <% if (success == null) { %>
        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/register">
            <label class="filter-field">
                <small>USER ID (Student Number)</small>
                <input type="text" name="userId" value="<%= prevUserId %>" placeholder="e.g. TA202412345" required autocomplete="username" />
            </label>
            <label class="filter-field">
                <small>FULL NAME</small>
                <input type="text" name="name" value="<%= prevName %>" placeholder="Your full name" required />
            </label>
            <label class="filter-field">
                <small>PASSWORD</small>
                <input type="password" name="password" placeholder="At least 6 characters" required autocomplete="new-password" />
            </label>
            <label class="filter-field">
                <small>CONFIRM PASSWORD</small>
                <input type="password" name="confirmPassword" placeholder="Repeat your password" required autocomplete="new-password" />
            </label>
            <button type="submit" class="auth-cta">Create account -&gt;</button>
        </form>
        <% } %>

        <div class="auth-meta">
            <strong>Account creation rules</strong><br />
            Use a unique User ID containing only letters, numbers, and underscores. Your new account will be created with the <code>TA</code> role.
        </div>

        <p class="auth-switch">
            Already have an account? <a class="auth-link" href="${pageContext.request.contextPath}/login">Sign in -&gt;</a>
        </p>
    </section>
</div>
</body>
</html>
