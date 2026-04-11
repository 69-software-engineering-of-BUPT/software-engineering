<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.ApplicationView" %>
<%
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";
    
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    List<ApplicationView> latestApps = (List<ApplicationView>) request.getAttribute("latestApps");
    
    if (pendingCount == null) pendingCount = 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        /* 仅新增通知样式，不破坏原有样式 */
        .notification-card {
            border-left: 4px solid #e53e3e;
            background: #fef2f2;
            padding: 16px;
            border-radius: 6px;
            margin: 14px 0;
        }
        .notification-normal {
            border-left: 4px solid #38a169;
            background: #f0fdf4;
            padding: 16px;
            border-radius: 6px;
            margin: 14px 0;
        }
    </style>
</head>
<body class="ad-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Module Organiser</div>
                <div class="brand-subtitle">Manage positions & review applicants</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/mo/home">Refresh</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= avatarText %></span>
                <span>
                    <strong><%= userName %></strong>
                    <small><%= userId %></small>
                </span>
            </div>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">MO</span>
                <h3>Module Organiser</h3>
                <p>Signed in as <strong><%= userId %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <span class="nav-item active">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Overview</small></span>
                </span>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/positions">
                    <span class="nav-icon">PO</span>
                    <span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/publish">
                    <span class="nav-icon">PU</span>
                    <span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                    <span class="nav-icon">AP</span>
                    <span><strong>Applications</strong><small><%= pendingCount %> pending</small></span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Welcome, <%= userName %></h1>
                    <p>Manage recruitment positions and review TA applications</p>
                </div>
                <a href="${pageContext.request.contextPath}/mo/publish" class="chip-button active">+ New Position</a>
            </section>

            <!-- ===================== 新增：新申请通知提醒（核心功能） ===================== -->
            <% if (pendingCount > 0) { %>
                <div class="notification-card">
                    <h3 style="margin:0 0 8px 0;color:#dc2626;">🔔 New Application Alert!</h3>
                    <p style="margin:0 0 12px 0;color:#7f1d1d;">
                        You have <strong style="font-size:18px;"><%= pendingCount %></strong> pending TA applications waiting for review!
                    </p>
                    <a href="${pageContext.request.contextPath}/mo/applications" class="chip-button active">Review Now</a>
                </div>
            <% } else { %>
                <div class="notification-normal">
                    <h3 style="margin:0 0 8px 0;color:#166534;">✅ No New Notifications</h3>
                    <p style="margin:0;color:#1f2937;">All applications are processed. No pending reviews required.</p>
                </div>
            <% } %>

            <!-- ===================== 完善：快捷功能卡片（补全按钮） ===================== -->
            <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:14px;margin-top:14px;">
                <section class="list-card">
                    <div class="list-title-row">
                        <h2>My Positions</h2>
                    </div>
                    <p style="margin:0 0 12px;color:#69707a;">Manage all your published job positions.</p>
                    <a href="${pageContext.request.contextPath}/mo/positions" class="chip-button active">View Positions</a>
                </section>

                <section class="list-card">
                    <div class="list-title-row">
                        <h2>Pending Applications</h2>
                    </div>
                    <p style="margin:0 0 12px;color:#69707a;"><%= pendingCount %> applications need your review.</p>
                    <a href="${pageContext.request.contextPath}/mo/applications" class="chip-button active">Check Now</a>
                </section>

                <section class="list-card">
                    <div class="list-title-row">
                        <h2>Publish Job</h2>
                    </div>
                    <p style="margin:0 0 12px;color:#69707a;">Post a new TA recruitment position.</p>
                    <a href="${pageContext.request.contextPath}/mo/publish" class="chip-button active">Create Now</a>
                </section>
            </div>
        </main>
    </div>
</div>
</body>
</html>