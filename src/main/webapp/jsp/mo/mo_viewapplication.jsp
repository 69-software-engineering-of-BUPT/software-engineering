<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruit.model.Application" %>
<%@ page import="com.bupt.tarecruit.model.Job" %>
<%@ page import="com.bupt.tarecruit.model.User" %>
<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String show(String value) {
        return hasText(value) ? esc(value) : "-";
    }

    private String selected(String current, String value) {
        return value.equalsIgnoreCase(current == null ? "" : current) ? "selected" : "";
    }

    private String statusClass(String status) {
        if ("APPROVED".equalsIgnoreCase(status)) return "badge-approved";
        if ("REJECTED".equalsIgnoreCase(status)) return "badge-rejected";
        if ("INTERVIEW".equalsIgnoreCase(status)) return "badge-interview";
        return "badge-pending";
    }
%>
<%
    String contextPath = request.getContextPath();
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";

    Application app = (Application) request.getAttribute("application");
    if (app == null) {
        response.sendRedirect(contextPath + "/mo/applications");
        return;
    }

    Job job = (Job) request.getAttribute("job");
    User ta = (User) request.getAttribute("taUser");
    String moduleName = job == null ? "" : job.getModuleName();
    String cvPath = ta == null ? null : ta.getCvFilePath();
    boolean hasCvLink = hasText(cvPath);
    String cvHref = "";
    if (hasCvLink) {
        String cleanPath = cvPath.trim();
        cvHref = contextPath + (cleanPath.startsWith("/") ? cleanPath : "/" + cleanPath);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO - Application Details</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/app.css" />
    <style>
        .app-table { width:100%; border-collapse: collapse; margin-top:12px; }
        .app-table th { padding:10px 12px; text-align:left; font-size:12px; color:#69707a; font-weight:600; }
        .app-table td { padding:12px; border-top:1px solid #e7e9ec; vertical-align:top; }
        .detail-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:12px; margin-top:12px; }
        .detail-item { border:1px solid #e7e9ec; border-radius:8px; padding:12px; background:#fff; }
        .detail-item small { display:block; color:#69707a; font-size:11px; font-weight:700; letter-spacing:.04em; margin-bottom:6px; }
        .detail-item strong { color:#111827; font-size:15px; word-break:break-word; }
        .form-group { margin:16px 0; }
        .form-group label { display:block; margin-bottom:6px; font-size:14px; font-weight:600; color:#374151; }
        .form-control { width:100%; box-sizing:border-box; padding:10px; border:1px solid #d8dde4; border-radius:6px; background:#fff; }
        textarea.form-control { min-height:100px; resize:vertical; }
        .statement-box { background:#f9fafb; padding:16px; border-radius:8px; white-space:pre-wrap; margin:12px 0; line-height:1.6; border:1px solid #e7e9ec; }
        .badge { display:inline-flex; align-items:center; padding:4px 8px; border-radius:12px; font-size:12px; font-weight:700; }
        .badge-pending { background:#fef3c7; color:#92400e; }
        .badge-approved { background:#d1fae5; color:#065f46; }
        .badge-rejected { background:#fee2e2; color:#991b1b; }
        .badge-interview { background:#dbeafe; color:#1d4ed8; }
        .alert-success { background:#d1fae5; color:#065f46; padding:12px; border-radius:8px; }
        .alert-error { background:#fee2e2; color:#991b1b; padding:12px; border-radius:8px; }
        .button-row { display:flex; flex-wrap:wrap; gap:10px; align-items:center; margin-top:12px; }
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
            <a class="chip-button" href="<%= contextPath %>/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= esc(avatarText) %></span>
                <span>
                    <strong><%= esc(userName) %></strong>
                    <small><%= esc(userId) %></small>
                </span>
            </div>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">MO</span>
                <h3>Module Organiser</h3>
                <p>Signed in as <strong><%= esc(userId) %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="<%= contextPath %>/mo/home">
                    <span class="nav-icon">HM</span><span><strong>Home</strong><small>Overview</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/mo/positions">
                    <span class="nav-icon">PO</span><span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/mo/publish">
                    <span class="nav-icon">PU</span><span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item active" href="<%= contextPath %>/mo/applications">
                    <span class="nav-icon">AP</span><span><strong>Applications</strong><small>All applications</small></span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Application Details</h1>
                    <p><%= show(moduleName) %> | <%= show(app.getStudentId()) %></p>
                </div>
                <a href="<%= contextPath %>/mo/applications" class="chip-button">Back</a>
            </section>

            <%
                String suc = (String) session.getAttribute("moActionSuccess");
                String err = (String) session.getAttribute("moActionError");
                if (suc != null) {
            %>
                <div class="alert-success"><%= esc(suc) %></div>
            <%
                    session.removeAttribute("moActionSuccess");
                }
                if (err != null) {
            %>
                <div class="alert-error"><%= esc(err) %></div>
            <%
                    session.removeAttribute("moActionError");
                }
            %>

            <section class="list-card">
                <div class="list-title-row"><h2>Application Summary</h2></div>
                <table class="app-table">
                    <tr><td>Application ID</td><td><%= show(app.getApplicationId()) %></td></tr>
                    <tr><td>Job ID</td><td><%= show(app.getJobId()) %></td></tr>
                    <tr><td>Module</td><td><%= show(moduleName) %></td></tr>
                    <tr><td>TA ID</td><td><%= show(app.getStudentId()) %></td></tr>
                    <tr><td>Applied Time</td><td><%= show(app.getAppliedAt()) %></td></tr>
                    <tr><td>Application Type</td><td><%= show(app.getApplicationType()) %></td></tr>
                    <tr><td>Status</td><td><span class="badge <%= statusClass(app.getStatus()) %>"><%= show(app.getStatus()) %></span></td></tr>
                    <tr><td>Marked By</td><td><%= show(app.getMarkedBy()) %></td></tr>
                    <tr><td>Marked Time</td><td><%= show(app.getMarkTime()) %></td></tr>
                    <tr>
                        <td>CV</td>
                        <td>
                            <%= app.isCvAttached() ? "Attached" : "Not attached to this application" %>
                            <% if (hasCvLink) { %>
                                <a class="chip-button" style="margin-left:8px;" target="_blank" href="<%= esc(cvHref) %>">Open CV</a>
                            <% } else { %>
                                <span style="margin-left:8px;color:#69707a;">No CV file available</span>
                            <% } %>
                        </td>
                    </tr>
                </table>
            </section>

            <section class="list-card">
                <div class="list-title-row"><h2>TA Profile</h2></div>
                <div class="detail-grid">
                    <div class="detail-item"><small>Name</small><strong><%= ta == null ? "-" : show(ta.getName()) %></strong></div>
                    <div class="detail-item"><small>Email</small><strong><%= ta == null ? "-" : show(ta.getEmail()) %></strong></div>
                    <div class="detail-item"><small>Phone</small><strong><%= ta == null ? "-" : show(ta.getPhoneNumber()) %></strong></div>
                    <div class="detail-item"><small>Research Area</small><strong><%= ta == null ? "-" : show(ta.getResearchArea()) %></strong></div>
                    <div class="detail-item"><small>CET-6 Grade</small><strong><%= ta == null ? "-" : show(ta.getCet6Grade()) %></strong></div>
                    <div class="detail-item"><small>Active Jobs Count</small><strong><%= ta == null ? "-" : String.valueOf(ta.getActiveJobsCount()) %></strong></div>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row"><h2>Statement & Conversation</h2></div>
                <div class="statement-box"><%= hasText(app.getStatement()) ? esc(app.getStatement()) : "No statement submitted." %></div>
                <div class="detail-item">
                    <small>Current Feedback</small>
                    <strong><%= show(app.getFeedback()) %></strong>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row"><h2>Review Application</h2></div>
                <form method="post" action="<%= contextPath %>/mo/view/application">
                    <input type="hidden" name="applicationId" value="<%= esc(app.getApplicationId()) %>">

                    <div class="form-group">
                        <label>Status</label>
                        <select name="status" class="form-control" required>
                            <option value="PENDING" <%= selected(app.getStatus(), "PENDING") %>>PENDING</option>
                            <option value="INTERVIEW" <%= selected(app.getStatus(), "INTERVIEW") %>>INTERVIEW</option>
                            <option value="APPROVED" <%= selected(app.getStatus(), "APPROVED") %>>APPROVED</option>
                            <option value="REJECTED" <%= selected(app.getStatus(), "REJECTED") %>>REJECTED</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Application Type</label>
                        <select name="applicationType" class="form-control">
                            <option value="">Keep current</option>
                            <option value="L" <%= selected(app.getApplicationType(), "L") %>>Leader (L)</option>
                            <option value="NL" <%= selected(app.getApplicationType(), "NL") %>>Non-Leader (NL)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Feedback</label>
                        <textarea name="feedback" class="form-control" placeholder="Decision feedback shown to the TA"><%= app.getFeedback() == null ? "" : esc(app.getFeedback()) %></textarea>
                    </div>

                    <div class="form-group">
                        <label>Message to TA</label>
                        <textarea name="moMessage" class="form-control" placeholder="Optional conversation message"></textarea>
                    </div>

                    <div class="button-row">
                        <button type="submit" class="chip-button active">Save Review</button>
                        <a href="<%= contextPath %>/mo/applications" class="chip-button">Cancel</a>
                    </div>
                </form>
            </section>
        </main>
    </div>
</div>
</body>
</html>
