<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruit.model.TAProfile" %>
<%
    TAProfile profile = (TAProfile) request.getAttribute("profile");
    String studentId = (String) request.getAttribute("studentId");
    String profileErr = (String) request.getAttribute("profileErrorMsg");
    if (studentId == null) studentId = "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA · Personal home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page ta-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TA</div>
            <div>
                <div class="brand-title">Personal home</div>
                <div class="brand-subtitle">Profile &amp; application history</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/jsp/ta/jobs.jsp">Job overview</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/jsp/role-select.jsp">Switch role</a>
            <div class="user-pill">
                <span class="avatar"><%= studentId.length() >= 2 ? studentId.substring(0, 2).toUpperCase() : "TA" %></span>
                <span>
                    <strong><%= profile != null && profile.getFullName() != null && !profile.getFullName().isEmpty() ? profile.getFullName() : "Teaching Assistant" %></strong>
                    <small><%= studentId.isEmpty() ? "Not signed in" : studentId %></small>
                </span>
            </div>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">TA</span>
                <h3>Account</h3>
                <p>Signed in as <strong><%= studentId.isEmpty() ? "-" : studentId %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <span class="nav-item active">
                    <span class="nav-icon">HM</span>
                    <span>
                        <strong>Home</strong>
                        <small>Profile &amp; applications</small>
                    </span>
                </span>
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ta/jobs.jsp">
                    <span class="nav-icon">JB</span>
                    <span>
                        <strong>Job overview</strong>
                        <small>Open positions (placeholder)</small>
                    </span>
                </a>
            </section>
        </aside>

        <main class="ad-main ta-main">
            <% if (profileErr != null) { %>
            <section class="list-card ta-flash ta-flash--warn" style="margin-bottom: 14px;">
                <p style="margin:0; color:#6b5346;"><strong>Profile not saved:</strong> <%= profileErr %></p>
            </section>
            <% } %>

            <section class="page-head">
                <div>
                    <h1 style="font-size: 38px;">Personal information</h1>
                    <p>Edit your profile fields and save. All fields are required by the server validation rules.</p>
                </div>
            </section>

            <section class="list-card ta-profile-card">
                <div class="list-title-row">
                    <h2>Basic profile</h2>
                    <span><%= studentId %></span>
                </div>
                <form class="ta-profile-form" method="post" action="${pageContext.request.contextPath}/ta/profile">
                    <div class="ta-profile-grid">
                        <label class="filter-field">
                            <small>STUDENT ID</small>
                            <input type="text" name="studentIdDisplay" value="<%= studentId %>" readonly />
                        </label>
                        <label class="filter-field">
                            <small>FULL NAME</small>
                            <input type="text" name="fullName" required value="<%= profile != null && profile.getFullName() != null ? profile.getFullName() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>EMAIL</small>
                            <input type="email" name="email" required value="<%= profile != null && profile.getEmail() != null ? profile.getEmail() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>PHONE</small>
                            <input type="text" name="phoneNumber" required value="<%= profile != null && profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>RESEARCH AREA</small>
                            <input type="text" name="researchArea" required value="<%= profile != null && profile.getResearchArea() != null ? profile.getResearchArea() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>CET6 GRADE</small>
                            <input type="text" name="cet6Grade" required value="<%= profile != null && profile.getCet6Grade() != null ? profile.getCet6Grade() : "" %>" />
                        </label>
                    </div>
                    <div class="ta-profile-actions">
                        <button type="submit" class="chip-button active">Save profile</button>
                    </div>
                </form>
            </section>

            <section class="page-head" style="margin-top: 22px;">
                <div>
                    <h1 style="font-size: 38px;">Application history</h1>
                    <p>Filter by status. Use Feedback to view your statement, instructor comments, and send a follow-up.</p>
                </div>
                <div class="filter-actions" id="ta-app-filters">
                    <button type="button" class="chip-button active" data-ta-filter="ALL">All</button>
                    <button type="button" class="chip-button" data-ta-filter="PENDING">Pending</button>
                    <button type="button" class="chip-button" data-ta-filter="APPROVED">Approved</button>
                    <button type="button" class="chip-button" data-ta-filter="REJECTED">Rejected</button>
                </div>
            </section>

            <section class="list-card ta-apps-card">
                <div class="list-head ta-app-head">
                    <span>JOB ID</span>
                    <span>MODULE</span>
                    <span>MO</span>
                    <span>STATUS</span>
                    <span>APPLIED</span>
                    <span>TYPE</span>
                    <span>APP ID</span>
                    <span>FEEDBACK</span>
                </div>
                <div id="ta-app-rows"></div>
                <p id="ta-app-empty" class="ta-empty-hint" hidden>No applications yet.</p>
            </section>
        </main>
    </div>
</div>

<script type="application/json" id="ta-applications-json"><%= request.getAttribute("applicationListJson") != null ? request.getAttribute("applicationListJson") : "[]" %></script>
<script>window.TA_CONTEXT = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/ta-home.js"></script>

<div id="ta-feedback-overlay" class="ta-feedback-overlay" aria-hidden="true">
    <div class="ta-feedback-panel list-card" role="dialog" aria-modal="true" aria-labelledby="ta-feedback-title">
        <div class="ta-feedback-head">
            <h2 id="ta-feedback-title">Application conversation</h2>
            <button type="button" class="chip-button" id="ta-feedback-close">Close</button>
        </div>
        <p class="ta-feedback-meta" id="ta-feedback-meta"></p>
        <div class="ta-chat-thread" id="ta-dialog-chat-thread"></div>

        <form class="ta-reply-form" method="post" action="${pageContext.request.contextPath}/ta/application/reply">
            <input type="hidden" name="applicationId" id="ta-dialog-app-id" value="" />
            <label class="filter-field">
                <small>REPLY TO INSTRUCTOR (optional, max 500 chars)</small>
                <textarea name="message" id="ta-dialog-reply" rows="4" maxlength="500" placeholder="Type your message to the module organiser..."></textarea>
            </label>
            <div class="ta-profile-actions">
                <button type="submit" class="chip-button active">Send reply</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
