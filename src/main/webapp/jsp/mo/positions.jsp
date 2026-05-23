<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.Job" %>
<%@ page import="com.bupt.tarecruit.model.ApplicationView" %>
<%@ page import="com.bupt.tarecruit.service.ApplicationService" %>
<%@ page import="com.bupt.tarecruit.service.MOConversationService" %>
<%
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";
    int conversationUnreadCount = 0;
    int pendingCount = 0;
    try {
        if (userId != null && !userId.trim().isEmpty()) {
            conversationUnreadCount = new MOConversationService().countUnreadThreads(userId);
            for (ApplicationView a : new ApplicationService().getApplicationsForMO(userId)) {
                if ("PENDING".equalsIgnoreCase(a.getStatus())) pendingCount++;
            }
        }
    } catch (Exception ignored) { }

    String actionSuccess = (String) session.getAttribute("moActionSuccess");
    String actionError   = (String) session.getAttribute("moActionError");
    if (actionSuccess != null) session.removeAttribute("moActionSuccess");
    if (actionError   != null) session.removeAttribute("moActionError");

    List<Job> jobList = (List<Job>) request.getAttribute("jobList");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Positions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        body.mo-positions-page .ad-shell { max-width: 1480px; }
        body.mo-positions-page table th,
        body.mo-positions-page table td { white-space: nowrap; }
        body.mo-positions-page td.positions-actions-cell { white-space: nowrap; }
        body.mo-positions-page td.positions-actions-cell .chip-button { flex-shrink: 0; }
    </style>
</head>
<body class="ad-page mo-positions-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Positions</div>
                <div class="brand-subtitle">Manage your job postings</div>
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
                <span class="nav-item active">
                    <span class="nav-icon">PO</span>
                    <span><strong>Positions</strong><small>Manage jobs</small></span>
                </span>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/publish">
                    <span class="nav-icon">PU</span>
                    <span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                    <span class="nav-icon">AP</span>
                    <span><strong>Applications</strong><small><%= pendingCount > 0 ? pendingCount + " pending" : "Manage applicants" %></small></span>
                </a>
                <a class="nav-item" id="mo-conv-nav-btn" href="${pageContext.request.contextPath}/mo/conversations">
                    <span class="nav-icon">CN</span>
                    <span><strong>Conversation</strong><small><%= conversationUnreadCount > 0 ? conversationUnreadCount + " new" : "TA messages" %></small></span>
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

            <section class="page-head mo-page-head">
                <div>
                    <h1 style="font-size:38px;">My Positions</h1>
                    <p>All published job positions</p>
                </div>
                <a href="${pageContext.request.contextPath}/mo/publish" class="chip-button active mo-publish-button">+ Publish New</a>
            </section>

            <section class="list-card" style="margin-top:14px;">
                <% if (jobList == null || jobList.isEmpty()) { %>
                    <p class="ta-empty-hint">No positions published yet.</p>
                <% } else { %>
                    <div class="mo-table-wrapper">
                        <table class="mo-positions-table">
                            <tr>
                                <th>Job ID</th>
                                <th>Module</th>
                                <th>Type</th>
                                <!-- 新增：表头 -->
                                <th>Leader Need</th>
                                <th>Member Need</th>
                                <th>Total Need</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            <% for (Job job : jobList) { 
                                int total = job.getLeaderCount() + job.getMemberCount();
                            %>
                            <tr>
                                <td><%= job.getJobId() %></td>
                                <td><%= job.getModuleName() %></td>
                                <td><%= job.getJobType() %></td>
                                <!-- 新增：招聘人数数据 -->
                                <td><%= job.getLeaderCount() %></td>
                                <td><%= job.getMemberCount() %></td>
                                <td><%= total %></td>
                                <td style="color:<%= "OPEN".equals(job.getStatus()) ? "green" : "red" %>;">
                                    <%= "OPEN".equals(job.getStatus()) ? "OPEN" : "CLOSED" %>
                                </td>
                                <td class="mo-action-cell">
                                    <a href="<%= request.getContextPath() %>/mo/job/applicants?jobId=<%= job.getJobId() %>" class="chip-button">Applicants</a>
                                    <a href="${pageContext.request.contextPath}/mo/edit?jobId=<%= job.getJobId() %>" class="chip-button">Edit</a>
                                    <a href="${pageContext.request.contextPath}/mo/job/down?jobId=<%= job.getJobId() %>" class="chip-button" onclick="return confirm('Confirm disable?')">Disable</a>
                                    <a href="${pageContext.request.contextPath}/mo/job/delete?jobId=<%= job.getJobId() %>" class="chip-button" onclick="return confirm('Confirm delete?')">Delete</a>
                                </td>
                            </tr>
                            <% } %>
                        </table>
                    </div>
                <% } %>
            </section>
        </main>
    </div>
</div>
<script>
window.MO_CONTEXT = '${pageContext.request.contextPath}';
</script>
</body>
</html>
