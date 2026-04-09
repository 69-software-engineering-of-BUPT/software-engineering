<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String moId         = (String) request.getAttribute("moId");
    String moName       = (String) request.getAttribute("moName");
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    if (moId == null) moId = "";
    if (moName == null) moName = "Module Organiser";
    if (pendingCount == null) pendingCount = 0;
    String actionSuccess = (String) session.getAttribute("moActionSuccess");
    String actionError   = (String) session.getAttribute("moActionError");
    if (actionSuccess != null) session.removeAttribute("moActionSuccess");
    if (actionError   != null) session.removeAttribute("moActionError");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO · Applications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
<div class="ad-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Applications</div>
                <div class="brand-subtitle">Review &amp; process TA applicants</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/mo/home">Home</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= moName.length() >= 2 ? moName.substring(0,2).toUpperCase() : "MO" %></span>
                <span><strong><%= moName %></strong><small><%= moId %></small></span>
            </div>
        </div>
    </header>

    <div class="ad-layout">
        <aside class="ad-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">MO</span>
                <h3>Module Organiser</h3>
                <p>Signed in as <strong><%= moId %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="${pageContext.request.contextPath}/mo/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Overview</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">AP</span>
                    <span><strong>Applications</strong><small><%= pendingCount %> pending</small></span>
                </span>
            </section>
        </aside>

        <main class="ad-main">
            <% if (actionSuccess != null) { %>
            <section class="list-card ta-flash ta-flash--success" style="margin-bottom:14px;">
                <p style="margin:0;color:#4f6c4d;"><strong>Done:</strong> <%= actionSuccess %></p>
            </section>
            <% } %>
            <% if (actionError != null) { %>
            <section class="list-card ta-flash ta-flash--warn" style="margin-bottom:14px;">
                <p style="margin:0;color:#6b5346;"><strong>Error:</strong> <%= actionError %></p>
            </section>
            <% } %>

            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Applications</h1>
                    <p><%= pendingCount %> pending decision<%= pendingCount == 1 ? "" : "s" %></p>
                </div>
                <div class="filter-actions">
                    <button type="button" class="chip-button active" data-mo-filter="ALL">All</button>
                    <button type="button" class="chip-button" data-mo-filter="PENDING">Pending</button>
                    <button type="button" class="chip-button" data-mo-filter="APPROVED">Approved</button>
                    <button type="button" class="chip-button" data-mo-filter="REJECTED">Rejected</button>
                    <button type="button" class="chip-button" data-mo-filter="INTERVIEW">Interview</button>
                </div>
            </section>

            <div id="mo-app-list"></div>
            <p id="mo-app-empty" class="ta-empty-hint" hidden>No applications for your positions yet.</p>
        </main>
    </div>
</div>

<script type="application/json" id="mo-applications-json"><%= request.getAttribute("applicationsJson") != null ? request.getAttribute("applicationsJson") : "[]" %></script>
<script>
    window.MO_CONTEXT = "${pageContext.request.contextPath}";
    window.MO_ID = "<%= moId %>";
</script>
<script src="${pageContext.request.contextPath}/js/mo-applications.js?v=20260409c"></script>

<!-- Chat overlay -->
<div id="mo-chat-overlay" class="ta-feedback-overlay" aria-hidden="true" style="display:none;">
    <div class="ta-feedback-panel list-card" role="dialog" aria-modal="true" aria-labelledby="mo-chat-title">
        <div class="ta-feedback-head">
            <div>
                <h2 id="mo-chat-title">Application conversation</h2>
                <p class="ta-feedback-meta" id="mo-chat-meta"></p>
            </div>
            <button type="button" class="chip-button" id="mo-chat-close">Close</button>
        </div>
        <div class="ta-chat-thread" id="mo-chat-thread"></div>
        <form class="ta-reply-form" id="mo-chat-form" method="post" action="${pageContext.request.contextPath}/mo/application/reply">
            <input type="hidden" name="applicationId" id="mo-chat-app-id" value="" />
            <label class="filter-field">
                <small>YOUR MESSAGE TO THE APPLICANT (max 500 chars)</small>
                <textarea name="message" id="mo-chat-reply" rows="4" maxlength="500" placeholder="Type your message to the TA…"></textarea>
            </label>
            <div class="ta-profile-actions">
                <button type="submit" class="chip-button active">Send message</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
