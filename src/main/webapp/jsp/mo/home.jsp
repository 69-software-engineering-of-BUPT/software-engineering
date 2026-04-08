<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    if (pendingCount == null) pendingCount = 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Module Organiser</div>
                <div class="brand-subtitle">Manage positions &amp; review applicants</div>
            </div>
        </div>
        <div class="top-actions">
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
                    <p>MO features (post jobs, review applications) are coming in the next iteration.</p>
                </div>
            </section>
            <section class="list-card" style="margin-top:14px;">
                <div class="list-title-row">
                    <h2>Coming soon</h2>
                    <span>MO module</span>
                </div>
                <p style="margin:0;color:#69707a;line-height:1.6;">
                    This area will allow you to post job positions, review applicants, and manage your recruitment workflow.
                </p>
            </section>
        </main>
    </div>
</div>
</body>
</html>