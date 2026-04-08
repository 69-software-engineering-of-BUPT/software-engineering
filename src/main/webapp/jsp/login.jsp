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
    <title>Sign in – TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        .login-page {
            min-height: 100vh;
            background: #f4f4f2;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            box-sizing: border-box;
        }
        .login-shell {
            width: min(900px, 100%);
            border: 1px solid #d8d3ca;
            border-radius: 24px;
            overflow: hidden;
            background: #f8f6f2;
            display: grid;
            grid-template-columns: 1fr 1fr;
            min-height: 520px;
        }
        .login-left {
            padding: 42px 40px;
            background: linear-gradient(160deg, #f6f7f8 0%, #eeecea 100%);
            border-right: 1px solid #ddd8ce;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .login-left-top .brand-icon {
            width: 56px;
            height: 56px;
            font-size: 18px;
        }
        .login-left-top .tag {
            margin-top: 18px;
            display: inline-block;
            font-size: 11px;
            letter-spacing: 2px;
            color: #8a8f97;
            border: 1px solid #d6d0c7;
            border-radius: 12px;
            padding: 3px 10px;
            text-transform: uppercase;
        }
        .login-left-top h1 {
            margin: 18px 0 12px;
            font-size: 40px;
            line-height: 1.1;
            color: #2f3742;
        }
        .login-left-top p {
            margin: 0;
            color: #626e7d;
            font-size: 14px;
            line-height: 1.6;
        }
        .login-roles {
            display: flex;
            gap: 10px;
        }
        .login-role-chip {
            border: 1px solid #d8d2c9;
            border-radius: 14px;
            background: #f7f4ef;
            padding: 8px 12px;
            font-size: 12px;
            color: #616874;
        }
        .login-role-chip strong {
            display: block;
            font-size: 14px;
            color: #2f3742;
            margin-bottom: 3px;
        }
        .login-right {
            padding: 42px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .login-right h2 {
            margin: 0 0 8px;
            font-size: 24px;
            color: #27303b;
        }
        .login-right .sub {
            margin: 0 0 28px;
            color: #7b838d;
            font-size: 13px;
        }
        .login-form {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }
        .login-form .filter-field input {
            width: 100%;
            box-sizing: border-box;
        }
        .login-submit {
            height: 46px;
            border: 1px solid #c8b49a;
            border-radius: 14px;
            background: #e8ddd0;
            color: #5b4732;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: background .15s ease;
            margin-top: 4px;
        }
        .login-submit:hover {
            background: #dfd0bf;
        }
        .login-error {
            border: 1px solid #dcc3bf;
            border-radius: 14px;
            background: #f8efee;
            padding: 10px 14px;
            color: #8c5c56;
            font-size: 13px;
        }
        .login-hint {
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px dashed #ddd5c9;
            font-size: 12px;
            color: #9197a0;
        }
        .login-hint code {
            background: #eeebe5;
            border-radius: 6px;
            padding: 1px 5px;
            font-size: 11px;
            color: #5b5347;
        }
        @media (max-width: 680px) {
            .login-shell { grid-template-columns: 1fr; }
            .login-left { border-right: none; border-bottom: 1px solid #ddd8ce; }
        }
    </style>
</head>
<body class="login-page">
<div class="login-shell">
    <section class="login-left">
        <div class="login-left-top">
            <div class="brand-icon">TR</div>
            <span class="tag">BUPT International School</span>
            <h1>TA Recruitment System</h1>
            <p>Sign in with your User ID and password to access your personalised dashboard.</p>
        </div>
        <div class="login-roles">
            <div class="login-role-chip"><strong>TA</strong>Browse &amp; apply</div>
            <div class="login-role-chip"><strong>MO</strong>Post &amp; review</div>
            <div class="login-role-chip"><strong>AD</strong>Manage &amp; monitor</div>
        </div>
    </section>

    <section class="login-right">
        <h2>Sign in</h2>
        <p class="sub">Enter your credentials to continue</p>

        <% if (loginError != null) { %>
        <div class="login-error" style="margin-bottom:16px;"><%= loginError %></div>
        <% } %>

        <form class="login-form" method="post" action="${pageContext.request.contextPath}/login">
            <label class="filter-field">
                <small>USER ID</small>
                <input type="text" name="userId" value="<%= inputUserId %>" placeholder="e.g. TA001 / MO001 / ADMIN001" required autocomplete="username" />
            </label>
            <label class="filter-field">
                <small>PASSWORD</small>
                <input type="password" name="password" placeholder="Enter your password" required autocomplete="current-password" />
            </label>
            <button type="submit" class="login-submit">Sign in →</button>
        </form>

        <div class="login-hint">
            <strong>Demo accounts</strong><br/>
            TA: <code>TA001</code> / <code>password123</code>&nbsp;&nbsp;
            MO: <code>MO001</code> / <code>password123</code>&nbsp;&nbsp;
            AD: <code>ADMIN001</code> / <code>password123</code>
        </div>
        <div style="margin-top:14px;text-align:center;font-size:13px;color:#9197a0;">
            New TA? <a href="${pageContext.request.contextPath}/register" style="color:#5870b3;text-decoration:underline;">Create an account →</a>
        </div>
    </section>
</div>
</body>
</html>
