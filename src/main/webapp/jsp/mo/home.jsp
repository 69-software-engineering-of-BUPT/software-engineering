<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.Job" %>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    String success = (String) request.getAttribute("moSuccessMsg");
    String error = (String) request.getAttribute("moErrorMsg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Module Organizer Workspace</div>
                <div class="brand-subtitle">Publish jobs and review applications</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
        </div>
    </header>
    <main class="ad-main">
        <% if (success != null) { %>
        <section class="list-card" style="margin-bottom:16px;"><p style="margin:0;color:#2d6b46;"><strong>Success:</strong> <%= success %></p></section>
        <% } %>
        <% if (error != null) { %>
        <section class="list-card" style="margin-bottom:16px;"><p style="margin:0;color:#9b3d3d;"><strong>Error:</strong> <%= error %></p></section>
        <% } %>

        <section class="list-card" style="margin-bottom:18px;">
            <div class="list-title-row">
                <h2>Publish a job</h2>
                <span>MO Workspace</span>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/mo/jobs" class="ta-profile-form">
                <div class="ta-profile-grid">
                    <label class="filter-field"><small>MODULE NAME</small><input type="text" name="moduleName" required /></label>
                    <label class="filter-field"><small>JOB TYPE</small><input type="text" name="jobType" required value="Teaching Assistant" /></label>
                    <label class="filter-field"><small>WEEKLY WORKLOAD</small><input type="number" name="weeklyWorkload" min="1" required /></label>
                    <label class="filter-field"><small>DEADLINE</small><input type="date" name="deadline" required /></label>
                    <label class="filter-field" style="grid-column:1 / -1;"><small>REQUIREMENTS</small><textarea name="requirements" rows="3" required></textarea></label>
                    <label class="filter-field" style="grid-column:1 / -1;"><small>POSITION DETAILS</small><textarea name="introduction" rows="3"></textarea></label>
                </div>
                <div class="ta-profile-actions"><button type="submit" class="chip-button active">Publish job</button></div>
            </form>
        </section>

        <section class="list-card">
            <div class="list-title-row">
                <h2>My jobs</h2>
                <span><%= jobs != null ? jobs.size() : 0 %> item(s)</span>
            </div>
            <% if (jobs == null || jobs.isEmpty()) { %>
            <p style="margin:0;color:#69707a;">You have not published any jobs yet.</p>
            <% } else { %>
                <% for (Job job : jobs) { %>
                <article class="list-row" style="display:block; margin-bottom:14px;">
                    <strong><%= job.getModuleName() %></strong>
                    <p style="margin:8px 0;"><%= job.getJobId() %> | <%= job.getStatus() %> | Deadline <%= job.getDeadline() %></p>
                    <p style="margin:0 0 10px;"><%= job.getRequirements() %></p>
                    <div class="row-actions">
                        <a class="chip-button" href="${pageContext.request.contextPath}/mo/applications?jobId=<%= job.getJobId() %>">View applications</a>
                    </div>
                </article>
                <% } %>
            <% } %>
        </section>
    </main>
</div>
</body>
</html>
