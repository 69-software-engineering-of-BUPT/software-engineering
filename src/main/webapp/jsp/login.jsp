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
<body class="auth-page auth-page--login">
<video class="login-bg-video" autoplay muted loop playsinline>
    <source src="${pageContext.request.contextPath}/assets/login-bg.mp4" type="video/mp4" />
</video>
<div class="auth-shell auth-shell--login">
    <section class="auth-side">
        <div class="auth-brand">
            <div class="brand-icon">TR</div>
            <div class="auth-brandtext">
                <span class="auth-brandline">TA Recruitment Portal</span>
                <span class="auth-brandline auth-brandline--muted">Secure access for TA, MO, and Admin</span>
            </div>
        </div>
        <div class="auth-hero">
            <p class="auth-eyebrow">Portal access</p>
            <h1 class="auth-title">
                <span>Sign in to the</span>
                <span>recruitment portal</span>
            </h1>
            <p class="auth-copy">Use your account credentials to continue to the correct workspace for your role.</p>
        </div>
        <div class="auth-role-grid">
            <article class="auth-role-card">
                <span class="auth-card-index">01</span>
                <strong>TA</strong>
                <p>Browse open positions and track your application status.</p>
            </article>
            <article class="auth-role-card">
                <span class="auth-card-index">02</span>
                <strong>MO</strong>
                <p>Manage vacancies, review applicants, and respond.</p>
            </article>
            <article class="auth-role-card">
                <span class="auth-card-index">03</span>
                <strong>AD</strong>
                <p>Monitor accounts, projects, and system activity.</p>
            </article>
        </div>
    </section>

    <section class="auth-main">
        <div class="auth-panel-head">
            <p class="auth-panel-kicker">Session based</p>
            <h2 class="auth-panel-title">Sign in</h2>
            <p class="auth-panel-subtitle">Enter your credentials to continue.</p>
        </div>

        <% if (loginError != null) { %>
        <div class="auth-alert auth-alert--error"><span><%= loginError %></span></div>
        <% } %>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/login">
            <label class="filter-field">
                <small>USER ID</small>
                <input type="text" name="userId" value="<%= inputUserId %>" placeholder="TA001" required autocomplete="username" />
            </label>
            <label class="filter-field">
                <small>PASSWORD</small>
                <input type="password" name="password" placeholder="Enter your password" required autocomplete="current-password" />
            </label>
            <button type="submit" class="auth-cta">Sign in</button>
        </form>

        <div class="auth-meta">
            <span class="auth-meta-title">Demo accounts</span>
            <div class="auth-demo-list">
                <div class="auth-demo-row">
                    <span class="auth-demo-label">TA</span>
                    <code>TA001</code>
                    <code>password123</code>
                </div>
                <div class="auth-demo-row">
                    <span class="auth-demo-label">MO</span>
                    <code>MO001</code>
                    <code>password123</code>
                </div>
                <div class="auth-demo-row">
                    <span class="auth-demo-label">AD</span>
                    <code>ADMIN001</code>
                    <code>password123</code>
                </div>
            </div>
        </div>

        <p class="auth-switch">
            New TA? <a class="auth-link" href="${pageContext.request.contextPath}/register">Create an account</a>
        </p>
    </section>
</div>
</body>
</html>
