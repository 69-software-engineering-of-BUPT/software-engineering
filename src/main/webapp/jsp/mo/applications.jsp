<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.Job" %>
<%@ page import="com.bupt.tarecruit.model.ApplicationView" %>
<%
    Job job = (Job) request.getAttribute("job");
    List<ApplicationView> applications = (List<ApplicationView>) request.getAttribute("applications");
    String error = (String) request.getAttribute("reviewErrorMsg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO Applications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell" style="max-width: 980px;">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">RV</div>
            <div>
                <div class="brand-title">Review applications</div>
                <div class="brand-subtitle"><%= job != null ? job.getModuleName() : "Unknown job" %></div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/mo/home">Back to home</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
        </div>
    </header>
    <main class="ad-main">
        <% if (error != null) { %>
        <section class="list-card" style="margin-bottom:16px;"><p style="margin:0;color:#9b3d3d;"><strong>Error:</strong> <%= error %></p></section>
        <% } %>
        <section class="list-card">
            <div class="list-title-row">
                <h2>Applications</h2>
                <span><%= applications != null ? applications.size() : 0 %> item(s)</span>
            </div>
            <% if (applications == null || applications.isEmpty()) { %>
            <p style="margin:0;color:#69707a;">No applications submitted for this job yet.</p>
            <% } else { %>
                <% for (ApplicationView app : applications) { %>
                <article class="list-row" style="display:block; margin-bottom:14px;">
                    <strong><%= app.getStudentName() %> (<%= app.getStudentId() %>)</strong>
                    <p style="margin:8px 0;">Status: <%= app.getStatus() %> | Applied: <%= app.getApplyTime() %></p>
                    <p style="margin:0 0 8px;"><strong>Statement:</strong> <%= app.getStatement() != null ? app.getStatement() : "-" %></p>
                    <form method="post" action="${pageContext.request.contextPath}/mo/applications/review">
                        <input type="hidden" name="jobId" value="<%= job.getJobId() %>" />
                        <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>" />
                        <label class="filter-field">
                            <small>FEEDBACK</small>
                            <textarea name="feedback" rows="3"><%= app.getFeedback() != null ? app.getFeedback() : "" %></textarea>
                        </label>
                        <div class="row-actions">
                            <button type="submit" class="chip-button active" name="decision" value="APPROVED">Approve</button>
                            <button type="submit" class="chip-button" name="decision" value="REJECTED">Reject</button>
                        </div>
                    </form>
                </article>
                <% } %>
            <% } %>
        </section>
    </main>
</div>
</body>
</html>
