<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA Recruitment Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell" style="max-width: 860px;">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">TA Recruitment Portal</div>
                <div class="brand-subtitle">Spring 2026 · Role based prototype</div>
            </div>
        </div>
    </header>

    <main class="ad-main">
        <section class="list-card">
            <div class="list-title-row">
                <h2>Welcome</h2>
                <span>Entry</span>
            </div>
            <p style="margin: 0 0 14px; color: #69707a;">系统已启动，请选择入口：</p>
            <div class="row-actions">
                <a class="chip-button" href="${pageContext.request.contextPath}/jsp/login.jsp">Login</a>
            </div>
        </section>
    </main>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
