<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";

    String actionSuccess = (String) session.getAttribute("moActionSuccess");
    String actionError   = (String) session.getAttribute("moActionError");
    if (actionSuccess != null) session.removeAttribute("moActionSuccess");
    if (actionError   != null) session.removeAttribute("moActionError");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Publish Position</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Publish Position</div>
                <div class="brand-subtitle">Create a new TA job</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/mo/home">Home</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= avatarText %></span>
                <span><strong><%= userName %></strong><small><%= userId %></small></span>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Overview</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/positions">
                    <span class="nav-icon">PO</span>
                    <span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">PU</span>
                    <span><strong>Publish</strong><small>Post position</small></span>
                </span>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                    <span class="nav-icon">AP</span>
                    <span><strong>Applications</strong><small>Manage applicants</small></span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <% if (actionSuccess != null) { %>
            <section class="list-card ta-flash ta-flash--success">
                <p style="margin:0;color:#4f6c4d;"><%= actionSuccess %></p>
            </section>
            <% } %>
            <% if (actionError != null) { %>
            <section class="list-card ta-flash ta-flash--warn">
                <p style="margin:0;color:#6b5346;"><%= actionError %></p>
            </section>
            <% } %>

            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Publish New Position</h1>
                    <p>Fill in the job details below</p>
                </div>
            </section>

            <section class="list-card" style="margin-top:14px;padding:20px;">
                <form action="${pageContext.request.contextPath}/mo/publish" method="post" style="display:grid;gap:16px;max-width:600px;">
                    <label class="filter-field">
                        <small>MODULE NAME</small>
                        <input type="text" name="moduleName" required style="width:100%;padding:8px;" />
                    </label>
                    <label class="filter-field">
                        <small>JOB TYPE</small>
                        <input type="text" name="jobType" required style="width:100%;padding:8px;" />
                    </label>
                    <label class="filter-field">
                        <small>INTRODUCTION (Job Description)</small>
                        <textarea name="introduction" rows="3" required style="width:100%;padding:8px;"></textarea>
                    </label>
                    <label class="filter-field">
                        <small>REQUIREMENTS (Qualifications)</small>
                        <textarea name="requirements" rows="3" required style="width:100%;padding:8px;"></textarea>
                    </label>
                    <label class="filter-field">
                        <small>WEEKLY WORKLOAD (HOURS)</small>
                        <input type="number" name="weeklyWorkload" min="1" required style="width:100%;padding:8px;" />
                    </label>
                    <label class="filter-field">
                        <small>DEADLINE</small>
                        <input type="date" name="deadline" required style="width:100%;padding:8px;" />
                    </label>

                    <!-- 新增：招聘人数（仅新增这段，其余不动） -->
                    <label class="filter-field">
                        <small>RECRUIT LEADER COUNT</small>
                        <input type="number" name="leaderCount" min="0" value="0" required style="width:100%;padding:8px;" />
                    </label>
                    <label class="filter-field">
                        <small>RECRUIT MEMBER COUNT</small>
                        <input type="number" name="memberCount" min="0" value="0" required style="width:100%;padding:8px;" />
                    </label>

                    <div>
                        <button type="submit" class="chip-button active">Publish Position</button>
                    </div>
                </form>
            </section>
        </main>
    </div>
</div>
</body>
</html>