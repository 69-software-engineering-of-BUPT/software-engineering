<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruit.model.Application" %>
<%
    String userId   = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String avatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";
    
    // 🔥 核心修复：空对象直接跳转回列表
    Application app = (Application) request.getAttribute("application");
    if (app == null) {
        response.sendRedirect(request.getContextPath() + "/mo/applications");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Application Details</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/app.css" />
    <style>
        .app-table { width:100%; border-collapse: collapse; margin-top:12px; }
        .app-table th { padding:10px 12px; text-align:left; font-size:12px; color:#69707a; font-weight:600; }
        .app-table td { padding:12px; border-top:1px solid #e7e9ec; }
        .form-group { margin:16px 0; }
        .form-group label { display:block; margin-bottom:6px; font-size:14px; }
        .form-control { width:100%; padding:10px; border:1px solid #e7e9ec; border-radius:6px; }
        textarea.form-control { min-height:100px; }
        .statement-box { background:#f9fafb; padding:16px; border-radius:8px; white-space:pre-line; margin:12px 0; line-height:1.6; }
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
            <a class="chip-button" href="<%= request.getContextPath() %>/logout">Sign out</a>
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
                <a class="nav-item" href="<%= request.getContextPath() %>/mo/home">
                    <span class="nav-icon">HM</span><span><strong>Home</strong><small>Overview</small></span>
                </a>
                <a class="nav-item" href="<%= request.getContextPath() %>/mo/positions">
                    <span class="nav-icon">PO</span><span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <a class="nav-item" href="<%= request.getContextPath() %>/mo/publish">
                    <span class="nav-icon">PU</span><span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item active" href="<%= request.getContextPath() %>/mo/applications">
                    <span class="nav-icon">AP</span><span><strong>Applications</strong><small>All applications</small></span>
                </a>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Application Details</h1>
                    <p>Manage status, type and conversation</p>
                </div>
                <a href="<%= request.getContextPath() %>/mo/applications" class="chip-button">Back</a>
            </section>

            <!-- 提示信息 -->
            <% String suc = (String) session.getAttribute("moActionSuccess");
               String err = (String) session.getAttribute("moActionError");
               if(suc!=null){ %>
               <div class="list-card" style="background:#d1fae5;padding:12px"><%= suc %></div>
               <% session.removeAttribute("moActionSuccess"); }
               if(err!=null){ %>
               <div class="list-card" style="background:#fee2e2;padding:12px"><%= err %></div>
               <% session.removeAttribute("moActionError"); } %>

            <!-- 基本信息 -->
            <section class="list-card">
                <div class="list-title-row"><h2>Basic Info</h2></div>
                <table class="app-table">
                    <tr><td>Application ID</td><td><%= app.getApplicationId() %></td></tr>
                    <tr><td>Job ID</td><td><%= app.getJobId() %></td></tr>
                    <tr><td>Student ID</td><td><%= app.getStudentId() %></td></tr>
                    <tr><td>Apply Time</td><td><%= app.getAppliedAt() %></td></tr>
                    <tr><td>CV Attached</td><td><%= app.isCvAttached() ? "Yes" : "No" %></td></tr>
                </table>
            </section>

            <!-- 交流记录 -->
            <section class="list-card">
                <div class="list-title-row"><h2>Conversation</h2></div>
                <div class="statement-box"><%= app.getStatement() %></div>
            </section>

            <!-- 编辑表单 -->
            <section class="list-card">
                <div class="list-title-row"><h2>Update Application</h2></div>
                <form method="post" action="<%= request.getContextPath() %>/mo/view/application">
                    <input type="hidden" name="applicationId" value="<%= app.getApplicationId() %>">
                    
                    <div class="form-group">
                        <label>Status</label>
                        <select name="status" class="form-control" required>
                            <option value="PENDING" <%= "PENDING".equals(app.getStatus()) ? "selected" : "" %>>PENDING</option>
                            <option value="APPROVED" <%= "APPROVED".equals(app.getStatus()) ? "selected" : "" %>>APPROVED</option>
                            <option value="REJECTED" <%= "REJECTED".equals(app.getStatus()) ? "selected" : "" %>>REJECTED</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Application Type</label>
                        <select name="applicationType" class="form-control" required>
                            <option value="L" <%= "L".equals(app.getApplicationType()) ? "selected" : "" %>>Leader (L)</option>
                            <option value="NL" <%= "NL".equals(app.getApplicationType()) ? "selected" : "" %>>Non-Leader (NL)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Send Message to TA</label>
                        <textarea name="moMessage" class="form-control" placeholder="Enter message..."></textarea>
                    </div>

                    <button type="submit" class="chip-button active" style="margin-top:12px;">Save Changes</button>
                </form>
            </section>
        </main>
    </div>
</div>
</body>
</html>