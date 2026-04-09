<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String _adRole = (String) session.getAttribute("userRole");
    if (session.getAttribute("userAccount") == null || !"ADMIN".equalsIgnoreCase(_adRole)) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Project View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260406-8" />
</head>
<body class="ad-page" id="project-view-page">
<div class="ad-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">TA Recruitment Portal</div>
                <div class="brand-subtitle">Spring 2026 | Role-based prototype</div>
            </div>
        </div>
        <div class="top-actions">
            <button class="chip-button" data-action="switch-role">Sign out</button>
            <button class="chip-button" data-action="reset-demo">Go to sign-in</button>
            <div class="user-pill">
                <span class="avatar">SA</span>
                <span>
                    <strong>System Admin</strong>
                    <small>Administrator</small>
                </span>
            </div>
        </div>
    </header>

    <div class="ad-layout">
        <aside class="ad-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">AD</span>
                <h3>Administrator</h3>
                <p>admin@campus.edu</p>
            </section>

            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/accounts.jsp">
                    <span class="nav-icon">AC</span>
                    <span>
                        <strong>Account Management</strong>
                        <small>TA and MO accounts</small>
                    </span>
                </a>
                <a class="nav-item active" href="${pageContext.request.contextPath}/jsp/ad/projects.jsp">
                    <span class="nav-icon">PM</span>
                    <span>
                        <strong>Project Management</strong>
                        <small>Vacancy monitor</small>
                    </span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/logs.jsp">
                    <span class="nav-icon">LG</span>
                    <span>
                        <strong>Operation Log</strong>
                        <small>Audit trail</small>
                    </span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1>Project View</h1>
                    <p>Course details and TA information</p>
                </div>
                <div class="filter-actions">
                    <a class="chip-button" href="${pageContext.request.contextPath}/jsp/ad/projects.jsp">Back to project list</a>
                </div>
            </section>

            <section class="list-card project-view-card">
                <div class="detail-head">
                    <div>
                        <h2 id="project-view-name">No project selected</h2>
                        <p id="project-view-code">-</p>
                    </div>
                    <span class="status warning" id="project-view-status">-</span>
                </div>

                <section class="project-detail-block">
                    <h3>Course details</h3>
                    <div class="project-basic-grid">
                        <div class="detail-kv">
                            <small>MODULE ORGANIZER</small>
                            <strong id="project-view-mo">-</strong>
                        </div>
                        <div class="detail-kv">
                            <small>POSTED</small>
                            <strong id="project-view-posted">-</strong>
                        </div>
                        <div class="detail-kv">
                            <small>APPLICATION DEADLINE</small>
                            <strong id="project-view-deadline">-</strong>
                        </div>
                        <div class="detail-kv">
                            <small>SEATS / FILLED / VACANCIES</small>
                            <strong id="project-view-capacity">-</strong>
                        </div>
                        <div class="detail-kv detail-kv-full">
                            <small>REQUIREMENTS</small>
                            <strong id="project-view-req">-</strong>
                        </div>
                        <div class="detail-kv detail-kv-full">
                            <small>POSITION DETAILS</small>
                            <strong id="project-view-desc">-</strong>
                        </div>
                    </div>
                </section>

                <section class="project-detail-block">
                    <h3>TA information</h3>
                    <div class="project-ta-grid">
                        <div class="project-ta-list-box">
                            <h4>Approved TA</h4>
                            <ul id="project-approved-ta-list"></ul>
                        </div>
                        <div class="project-ta-list-box">
                            <h4>Pending TA</h4>
                            <ul id="project-pending-ta-list"></ul>
                        </div>
                    </div>
                </section>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260406-8"></script>
</body>
</html>
