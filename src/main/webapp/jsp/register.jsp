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
<body class="auth-page auth-page--register">
<div class="auth-shell auth-shell--register">
    <section class="auth-side">
        <div class="auth-brand">
            <div class="brand-icon">TR</div>
            <div class="auth-brandtext">
                <span class="auth-brandline">BUPT International School</span>
                <span class="auth-brandline auth-brandline--muted">TA account onboarding</span>
            </div>
        </div>
        <div class="auth-hero">
            <p class="auth-eyebrow">TA account setup</p>
            <h1 class="auth-title">
                <span>Create your</span>
                <span>TA profile</span>
            </h1>
            <p class="auth-copy">Use one account to browse openings, submit applications, and return to the portal throughout the recruitment cycle.</p>
        </div>
        <div class="auth-checklist">
            <div class="auth-check-item">
                <span class="auth-check-index">01</span>
                <div class="auth-check-copy">
                    <strong>Choose a clear User ID</strong>
                    Use letters, numbers, and underscores only so the account can be stored safely.
                </div>
            </div>
            <div class="auth-check-item">
                <span class="auth-check-index">02</span>
                <div class="auth-check-copy">
                    <strong>TA accounts only</strong>
                    Module Organiser and Administrator accounts are still issued by the system administrator.
                </div>
            </div>
            <div class="auth-check-item">
                <span class="auth-check-index">03</span>
                <div class="auth-check-copy">
                    <strong>Sign in right after registration</strong>
                    Once your account is created successfully, you can use the same credentials on the sign-in page.
                </div>
            </div>
        </div>
    </section>

    <section class="auth-main">
        <div class="auth-panel-head">
            <p class="auth-panel-kicker">Start with a TA account</p>
            <h2 class="auth-panel-title">Register</h2>
            <p class="auth-panel-subtitle">Create your account now, then sign in with the same credentials when you are ready.</p>
        </div>

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
                <input type="text" name="userId" value="<%= prevUserId %>" placeholder="TA202412345" required autocomplete="username" />
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
            <button type="submit" class="auth-cta">Create account</button>
        </form>
        <% } %>
        <% if (success != null) { %>
        <div class="auth-post-action">
            <a class="auth-secondary-cta" href="${pageContext.request.contextPath}/login">Proceed to sign in</a>
        </div>
        <% } %>

        <div class="auth-meta">
            <span class="auth-meta-title">Account rules</span>
            <div class="auth-rule-list">
                <div class="auth-rule-item">
                    <span class="auth-rule-bullet">1</span>
                    <span>Pick a unique User ID and keep it consistent with your student record.</span>
                </div>
                <div class="auth-rule-item">
                    <span class="auth-rule-bullet">2</span>
                    <span>Your new account will be created with the <code>TA</code> role.</span>
                </div>
                <div class="auth-rule-item">
                    <span class="auth-rule-bullet">3</span>
                    <span>Use the login page after registration to enter the portal.</span>
                </div>
            </div>
        </div>

        <p class="auth-switch">
            Already have an account? <a class="auth-link" href="${pageContext.request.contextPath}/login">Sign in</a>
        </p>
    </section>
</div>
</body>
</html>
