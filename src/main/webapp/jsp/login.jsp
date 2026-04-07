<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String loginError = (String) request.getAttribute("loginError");
    String loginUserId = (String) request.getAttribute("loginUserId");
    if (loginUserId == null) loginUserId = "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Login - TA Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell" style="max-width: 520px;">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">TA Recruitment Portal</div>
                <div class="brand-subtitle">Real login for TA / MO / Admin</div>
            </div>
        </div>
    </header>
    <main class="ad-main">
        <section class="list-card">
            <div class="list-title-row">
                <h2>Sign in</h2>
                <span>Session based</span>
            </div>
            <% if (loginError != null) { %>
            <p style="margin:0 0 16px; color:#9b3d3d;"><strong>Login failed:</strong> <%= loginError %></p>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/login" class="ta-profile-form">
                <div class="ta-profile-grid" style="grid-template-columns: 1fr;">
                    <label class="filter-field">
                        <small>USER ID</small>
                        <input type="text" name="userId" value="<%= loginUserId %>" required />
                    </label>
                    <label class="filter-field">
                        <small>PASSWORD</small>
                        <input type="password" name="password" required />
                    </label>
                </div>
                <div class="ta-profile-actions">
                    <button type="submit" class="chip-button active">Login</button>
                </div>
            </form>
            <p style="margin:16px 0 0; color:#69707a; line-height:1.6;">Test accounts: TA001 / password123, MO001 / password123, ADMIN001 / password123.</p>
        </section>
    </main>
</div>
</body>
</html>
