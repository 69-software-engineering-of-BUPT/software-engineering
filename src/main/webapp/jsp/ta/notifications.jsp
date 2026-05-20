<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String studentId = (String) request.getAttribute("studentId");
    if (studentId == null) studentId = "";
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    if (unreadCount == null) unreadCount = 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA · Notifications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page ta-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">NT</div>
            <div>
                <div class="brand-title">Notifications</div>
                <div class="brand-subtitle">Status updates &amp; feedback</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/home">Home</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/jobs">Job overview</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">TA</span>
                <h3>Account</h3>
                <p>Signed in as <strong><%= studentId.isEmpty() ? "-" : studentId %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Profile &amp; applications</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/jobs">
                    <span class="nav-icon">JB</span>
                    <span><strong>Job overview</strong><small>Open positions</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">NT</span>
                    <span><strong>Notifications</strong><small>Status updates</small></span>
                </span>
            </section>
        </aside>

        <main class="ad-main ta-main">
            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Notifications</h1>
                    <p><%= unreadCount %> unread notification<%= unreadCount == 1 ? "" : "s" %></p>
                </div>
                <div class="filter-actions">
                    <form method="post" action="${pageContext.request.contextPath}/ta/notifications" style="display:inline;">
                        <input type="hidden" name="action" value="markAllRead" />
                        <button type="submit" class="chip-button">Mark all read</button>
                    </form>
                    <button type="button" class="chip-button active" data-noti-filter="ALL" id="noti-filter-all">All</button>
                    <button type="button" class="chip-button" data-noti-filter="UNREAD" id="noti-filter-unread">Unread</button>
                </div>
            </section>

            <div id="ta-noti-list"></div>
            <p id="ta-noti-empty" class="ta-empty-hint" hidden>No notifications yet.</p>
        </main>
    </div>
</div>

<script type="application/json" id="ta-notifications-json"><%= request.getAttribute("notificationsJson") != null ? request.getAttribute("notificationsJson") : "[]" %></script>
<script>window.TA_CONTEXT = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/ta-notifications.js"></script>
</body>
</html>