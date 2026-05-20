<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Choose a Role - TA Recruitment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell" style="max-width: 760px;">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">Choose a role</div>
                <div class="brand-subtitle">Use the real sign-in page to enter the system</div>
            </div>
        </div>
    </header>
    <main class="ad-main">
        <section class="list-card">
            <div class="list-title-row"><h2>Role-based access</h2><span>Protected</span></div>
            <p style="margin:0 0 16px;color:#69707a;line-height:1.6;">This page is informational only. Use the sign-in page with a real user ID and password to enter as TA, MO, or Admin.</p>
            <a class="chip-button active" href="${pageContext.request.contextPath}/jsp/login.jsp">Go to sign-in</a>
        </section>
    </main>
</div>
</body>
</html>
