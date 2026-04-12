<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.Application" %>
<%
    // ===================== 修复：强制从 request 获取 jobId =====================
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    String jobId    = (String) request.getAttribute("jobId");
    
    // 空值保护
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    if (jobId == null) jobId = "Unknown Job"; // 修复点

    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";
    
    List<Application> applicantList = (List<Application>) request.getAttribute("applicantList");
    int totalCount = applicantList == null ? 0 : applicantList.size();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Applicants | <%= jobId %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        .app-table { width:100%; border-collapse: collapse; margin-top:12px; }
        .app-table th { padding:10px 12px; text-align:left; font-size:12px; color:#69707a; font-weight:600; }
        .app-table td { padding:12px; border-top:1px solid #e7e9ec; }
        .badge { padding:4px 8px; border-radius:12px; font-size:12px; }
        .badge-pending { background:#fef3c7; color:#92400e; }
        .badge-approved { background:#d1fae5; color:#065f46; }
        .badge-rejected { background:#fee2e2; color:#991b1b; }
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
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Overview</small></span>
                </a>
                <a class="nav-item active" href="${pageContext.request.contextPath}/mo/positions">
                    <span class="nav-icon">PO</span>
                    <span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/publish">
                    <span class="nav-icon">PU</span>
                    <span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                    <span class="nav-icon">AP</span>
                    <span><strong>Applications</strong><small>All applications</small></span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <!-- 修复：正常显示 jobId -->
                    <h1 style="font-size:38px;">Applicants | Job: <%= jobId %></h1>
                    <p>View all students who applied for this position</p>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row">
                    <h2>Total Applicants: <%= totalCount %></h2>
                </div>
                <p style="margin:0 0 12px;color:#69707a;">Only applications for this job are shown</p>

                <table class="app-table">
                    <thead>
                        <tr>
                            <th>Student ID</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Applied Time</th>
                            <th>CV Attached</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (applicantList != null && !applicantList.isEmpty()) {
                                for (Application app : applicantList) {
                                    String type = "L".equals(app.getApplicationType()) ? "Leader" : "Member";
                                    String status = app.getStatus();
                                    String badgeClass = "";
                                    if ("PENDING".equals(status)) badgeClass = "badge-pending";
                                    else if ("APPROVED".equals(status)) badgeClass = "badge-approved";
                                    else badgeClass = "badge-rejected";
                                    String cv = app.isCvAttached() ? "Yes" : "No";
                        %>
                        <tr>
    <td><%= app.getStudentId() %></td>
    <td><%= type %></td>
    <td><span class="badge <%= badgeClass %>"><%= status %></span></td>
    <td><%= app.getAppliedAt() %></td>
    <td><%= cv %></td>
    <!-- ✅ 独立 Details 按钮 -->
    <td>
        <a href="<%= request.getContextPath() %>/mo/view/application?applicationId=<%= app.getApplicationId() %>" class="chip-button">Details</a>
    </td>
</tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="5" style="text-align:center;padding:24px;color:#69707a;">No applicants yet</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</div>
</body>
</html>