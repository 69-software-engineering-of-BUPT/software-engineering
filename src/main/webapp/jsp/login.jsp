<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String loginError = (String) request.getAttribute("loginError");
    String inputUserId = (String) request.getAttribute("inputUserId");
    if (inputUserId == null) inputUserId = "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Sign in - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="auth-page">
<div class="auth-shell">
    <section class="auth-side">
        <div class="auth-brand">
            <div class="brand-icon">TR</div>
            <span class="auth-tag">BUPT International School</span>
            <h1 class="auth-title">TA Recruitment System</h1>
            <p class="auth-copy">Sign in with your User ID and password to access your personalised dashboard.</p>
        </div>
        <div class="auth-role-grid">
            <div class="auth-role-card"><strong>TA</strong>Browse positions and track your application status.</div>
            <div class="auth-role-card"><strong>MO</strong>Manage vacancies, review applicants, and respond.</div>
            <div class="auth-role-card"><strong>AD</strong>Monitor accounts, projects, and system activity.</div>
        </div>
    </section>

    <section class="auth-main">
        <h2 class="auth-panel-title">Sign in</h2>
        <p class="auth-panel-subtitle">Enter your credentials to continue.</p>

        <% if (loginError != null) { %>
        <div class="auth-alert auth-alert--error"><span><%= loginError %></span></div>
        <% } %>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/login">
            <label class="filter-field">
                <small>USER ID</small>
                <input type="text" name="userId" value="<%= inputUserId %>" placeholder="e.g. TA001 / MO001 / ADMIN001" required autocomplete="username" />
            </label>
            <label class="filter-field">
                <small>PASSWORD</small>
                <input type="password" name="password" placeholder="Enter your password" required autocomplete="current-password" />
            </label>
            <button type="submit" class="auth-cta">Sign in -&gt;</button>
        </form>

        <div class="auth-meta">
            <strong>Demo accounts</strong><br />
            TA: <code>TA001</code> / <code>password123</code><br />
            MO: <code>MO001</code> / <code>password123</code><br />
            AD: <code>ADMIN001</code> / <code>password123</code>
        </div>

        <p class="auth-switch">
            New TA? <a class="auth-link" href="${pageContext.request.contextPath}/register">Create an account -&gt;</a>
        </p>
    </section>
</div>
</body>
</html>
