<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.bupt.tarecruit.model.Job" %>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    Set<String> appliedJobIds = (Set<String>) request.getAttribute("appliedJobIds");
    String success = (String) request.getAttribute("jobSuccessMsg");
    String error = (String) request.getAttribute("jobErrorMsg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA Job Overview</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page ta-page">
<div class="ad-shell" style="max-width: 980px;">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">JB</div>
            <div>
                <div class="brand-title">Job Overview</div>
                <div class="brand-subtitle">Browse open TA positions and apply</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/home">Back to home</a>
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

        <section class="list-card">
            <div class="list-title-row">
                <h2>Open positions</h2>
                <span><%= jobs != null ? jobs.size() : 0 %> item(s)</span>
            </div>
            <% if (jobs == null || jobs.isEmpty()) { %>
            <p style="margin:0;color:#69707a;">No open jobs at the moment.</p>
            <% } else { %>
                <% for (Job job : jobs) { boolean applied = appliedJobIds != null && appliedJobIds.contains(job.getJobId()); %>
                <article class="list-row" style="display:block; margin-bottom:14px;">
                    <div class="list-title-row" style="margin-bottom:8px;">
                        <h2 style="font-size:22px;"><%= job.getModuleName() %></h2>
                        <span><%= job.getJobId() %></span>
                    </div>
                    <p style="margin:0 0 8px;"><strong>MO:</strong> <%= job.getMdName() != null ? job.getMdName() : job.getMoId() %> | <strong>Type:</strong> <%= job.getJobType() %></p>
                    <p style="margin:0 0 8px;"><strong>Requirements:</strong> <%= job.getRequirements() %></p>
                    <p style="margin:0 0 12px;"><strong>Weekly workload:</strong> <%= job.getWeeklyWorkload() %>h | <strong>Deadline:</strong> <%= job.getDeadline() %></p>
                    <% if (applied) { %>
                    <p style="margin:0;color:#69707a;">You have already applied for this job.</p>
                    <% } else { %>
                    <form method="post" action="${pageContext.request.contextPath}/ta/applications">
                        <input type="hidden" name="jobId" value="<%= job.getJobId() %>" />
                        <label class="filter-field">
                            <small>STATEMENT (OPTIONAL, MAX 500 CHARS)</small>
                            <textarea name="statement" rows="3" maxlength="500" placeholder="Why are you suitable for this TA role?"></textarea>
                        </label>
                        <div class="ta-profile-actions">
                            <button type="submit" class="chip-button active">Apply now</button>
                        </div>
                    </form>
                    <% } %>
                </article>
                <% } %>
            <% } %>
        </section>
    </main>
</div>
</body>
</html>
