<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bupt.tarecruit.model.User" %>
<%
    User currentUser = (User) request.getAttribute("currentUser");
    String studentId = (String) request.getAttribute("studentId");
    String profileErr = (String) request.getAttribute("profileErrorMsg");
    if (studentId == null) studentId = "";
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    if (unreadCount == null) unreadCount = 0;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA 路 Personal home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
    <style>
        /* TA Conversation overlay – mirrors MO conversations.jsp inline styles */
        .ta-feedback-overlay { position:fixed; inset:0; background:rgba(47,55,66,.35); display:none; align-items:center; justify-content:center; z-index:2000; padding:18px; box-sizing:border-box; }
        .ta-feedback-overlay--open { display:flex; }
        .ta-conv-panel { width:min(1100px,calc(100vw - 36px)); height:min(740px,calc(100vh - 72px)); overflow:hidden; display:flex; flex-direction:column; padding:0; border-radius:10px; background:#f8f6f2; }
        .ta-conv-topbar { display:flex; align-items:center; justify-content:space-between; padding:14px 18px; border-bottom:1px solid #ded4c7; background:#fffdfa; flex-shrink:0; border-radius:10px 10px 0 0; }
        .ta-conv-topbar h2 { margin:0; font-size:18px; color:#2d3846; }
        .mo-chat-shell { flex:1; min-height:0; display:grid; grid-template-columns:320px minmax(0,1fr); border-radius:0 0 10px 10px; overflow:hidden; background:#fffdfa; }
        .mo-chat-list { display:flex; flex-direction:column; min-width:0; min-height:0; background:#f1ece4; border-right:1px solid #ded4c7; }
        .mo-chat-list-head { display:flex; align-items:center; gap:10px; padding:14px; border-bottom:1px solid #ded4c7; flex-shrink:0; }
        .mo-chat-search { flex:1; min-width:0; border:1px solid #d8cdbc; border-radius:8px; padding:10px 12px; background:#fffdfa; font-size:14px; color:#2f3742; }
        .mo-chat-search:focus { outline:none; border-color:#b98956; box-shadow:0 0 0 3px rgba(185,137,86,.16); }
        .mo-chat-count { display:inline-flex; align-items:center; justify-content:center; min-width:28px; height:28px; border-radius:999px; background:#9d7048; color:#fffdfa; font-size:12px; font-weight:700; }
        .mo-chat-thread-list { flex:1; min-height:0; overflow-y:auto; overflow-x:hidden; padding:8px 0; }
        .mo-thread { width:100%; border:0; background:transparent; display:grid; grid-template-columns:46px minmax(0,1fr) auto; gap:10px; padding:12px 14px; text-align:left; cursor:pointer; border-bottom:1px solid rgba(222,212,199,.76); color:#2f3742; }
        .mo-thread:hover { background:#f6f0e2; }
        .mo-thread.active { background:#e8ddd0; color:#2f3742; box-shadow:inset 4px 0 0 #9d7048; }
        .thread-avatar { width:44px; height:44px; border-radius:8px; display:flex; align-items:center; justify-content:center; background:#f6f0e2; color:#71543a; font-weight:800; flex:0 0 auto; position:relative; border:1px solid #ded4c7; font-size:13px; }
        .mo-thread.active .thread-avatar { background:#fffdfa; color:#7a542e; border-color:#cbb9a3; }
        .thread-dot { position:absolute; top:-4px; right:-4px; width:12px; height:12px; border-radius:50%; background:#c45a44; border:2px solid #f1ece4; }
        .mo-thread.active .thread-dot { border-color:#e8ddd0; }
        .thread-main { min-width:0; display:grid; gap:4px; }
        .thread-title { display:flex; gap:6px; align-items:center; min-width:0; }
        .thread-title strong { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; font-size:15px; }
        .thread-title small { flex:0 0 auto; font-size:11px; opacity:.72; }
        .thread-preview { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; font-size:13px; color:#7c7469; }
        .thread-meta { display:grid; justify-items:end; gap:6px; font-size:12px; color:#8a8175; white-space:nowrap; }
        .mo-thread.active .thread-preview, .mo-thread.active .thread-meta { color:#6f6559; }
        .thread-new { display:inline-flex; align-items:center; justify-content:center; min-width:18px; height:18px; padding:0 6px; border-radius:999px; background:#c45a44; color:#fffdfa; font-size:11px; font-weight:700; }
        .mo-chat-panel { min-width:0; min-height:0; display:grid; background:#f8f6f2; overflow:hidden; }
        .chat-empty { display:flex; align-items:center; justify-content:center; color:#8a9099; height:100%; }
        .chat-window { display:none; min-height:0; height:100%; overflow:hidden; grid-template-rows:auto minmax(0,1fr) auto; }
        .chat-window.active { display:grid; }
        .chat-header { min-height:0; display:flex; justify-content:space-between; align-items:center; gap:12px; padding:16px 22px; border-bottom:1px solid #ded4c7; background:#fffdfa; }
        .chat-header h2 { margin:0; font-size:20px; color:#111827; }
        .chat-subtitle { margin-top:5px; color:#69707a; font-size:13px; }
        .chat-actions { display:flex; gap:8px; align-items:center; flex-wrap:wrap; justify-content:flex-end; }
        .badge { display:inline-flex; align-items:center; padding:4px 8px; border-radius:12px; font-size:12px; font-weight:700; }
        .badge-pending { background:#fef3c7; color:#92400e; }
        .badge-approved { background:#e6ead9; color:#4f6c4d; }
        .badge-rejected { background:#fee2e2; color:#991b1b; }
        .badge-interview { background:#eadcb6; color:#6b4e20; }
        .badge-new { background:#fff1d6; color:#8a4b0f; }
        .chat-messages { min-height:0; overflow:auto; padding:22px; display:flex; flex-direction:column; gap:14px; background:#f8f6f2; }
        .message-row { display:flex; align-items:flex-start; gap:10px; }
        .message-row--mo { justify-content:flex-end; }
        .message-avatar { width:36px; height:36px; border-radius:8px; display:flex; align-items:center; justify-content:center; background:#eee7dc; color:#6b5346; font-size:12px; font-weight:800; flex:0 0 auto; }
        .message-row--mo .message-avatar { order:2; background:#eadcb6; color:#6b4e20; }
        .message-stack { max-width:min(620px,72%); display:grid; gap:5px; }
        .message-row--mo .message-stack { justify-items:end; }
        .message-bubble { border-radius:8px; padding:10px 12px; background:#fffdfa; border:1px solid #ded4c7; box-shadow:0 1px 2px rgba(83,68,51,.05); }
        .message-row--mo .message-bubble { background:#eadcb6; border-color:#d8c48d; }
        .message-meta { color:#858b94; font-size:12px; }
        .message-content { white-space:pre-wrap; color:#1f2937; line-height:1.5; word-break:break-word; }
        .chat-reply { border-top:1px solid #ded4c7; background:#fffdfa; padding:10px 12px 12px; }
        .reply-composer { border:1px solid #d8cdbc; border-radius:10px; background:#fffdfa; box-shadow:0 1px 3px rgba(83,68,51,.06); overflow:hidden; }
        .reply-box { display:grid; grid-template-rows:auto auto; gap:8px; padding:0 10px 10px; }
        .reply-box textarea { min-height:74px; max-height:128px; resize:none; box-sizing:border-box; width:100%; border:0; border-radius:0; padding:12px 2px 8px; font:inherit; background:transparent; color:#2f3742; line-height:1.45; }
        .reply-box textarea:focus { outline:none; }
        .reply-actions { display:flex; justify-content:flex-end; align-items:center; gap:10px; min-height:34px; }
        .reply-send { min-width:76px; border-radius:8px; padding:8px 16px; }
        .reply-send:disabled { opacity:.55; cursor:not-allowed; }
        @media (max-width:700px) {
            .ta-conv-panel { height:calc(100vh - 36px); }
            .mo-chat-shell { grid-template-columns:1fr; }
            .mo-chat-list { max-height:260px; border-right:0; border-bottom:1px solid #ded4c7; }
            .chat-window.active { grid-template-rows:auto minmax(200px,1fr) auto; }
        }
    </style>
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
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/jobs">Job overview</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/notifications">Notifications<% if (unreadCount > 0) { %> (<%= unreadCount %>)<% } %></a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= studentId.length() >= 2 ? studentId.substring(0, 2).toUpperCase() : "TA" %></span>
                <span>
                    <strong><%= currentUser != null && currentUser.getName() != null && !currentUser.getName().isEmpty() ? currentUser.getName() : "Teaching Assistant" %></strong>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/jobs">
                    <span class="nav-icon">JB</span>
                    <span>
                        <strong>Job overview</strong>
                        <small>Open positions</small>
                    </span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/notifications">
                    <span class="nav-icon">NT</span>
                    <span>
                        <strong>Notifications</strong>
                        <small><%= unreadCount %> unread</small>
                    </span>
                </a>
                <a class="nav-item" id="ta-conv-nav-btn" href="${pageContext.request.contextPath}/ta/home?conv=1">
                    <span class="nav-icon">CO</span>
                    <span>
                        <strong>Conversation</strong>
                        <small>Messages from MO</small>
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
                <form class="ta-profile-form" method="post" action="${pageContext.request.contextPath}/ta/profile" novalidate>
                    <div class="ta-profile-grid">
                        <label class="filter-field">
                            <small>STUDENT ID</small>
                            <input type="text" name="studentIdDisplay" value="<%= studentId %>" readonly />
                        </label>
                        <label class="filter-field">
                            <small>FULL NAME</small>
                            <%
                                String fullNameVal = (currentUser != null && currentUser.getName() != null)
                                    ? currentUser.getName()
                                    : (String) session.getAttribute("userName");
                                if (fullNameVal == null) fullNameVal = "";
                            %>
                            <input type="text" name="fullName" value="<%= fullNameVal %>" />
                        </label>
                        <label class="filter-field">
                            <small>EMAIL</small>
                            <input type="email" name="email" value="<%= currentUser != null && currentUser.getEmail() != null ? currentUser.getEmail() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>PHONE</small>
                            <input type="text" name="phoneNumber" value="<%= currentUser != null && currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>RESEARCH AREA</small>
                            <input type="text" name="researchArea" value="<%= currentUser != null && currentUser.getResearchArea() != null ? currentUser.getResearchArea() : "" %>" />
                        </label>
                        <label class="filter-field">
                            <small>CET6 GRADE</small>
                            <input type="text" name="cet6Grade" value="<%= currentUser != null && currentUser.getCet6Grade() != null ? currentUser.getCet6Grade() : "" %>" />
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
                    <button type="button" class="chip-button" data-ta-filter="INTERVIEW">Interview</button>
                </div>
            </section>

            <section class="list-card ta-apps-card">
                <div class="list-head ta-app-head">
                    <span>MODULE</span>
                    <span>MO</span>
                    <span>STATUS</span>
                    <span>APPLIED</span>
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
<script src="${pageContext.request.contextPath}/js/ta-home.js?v=20260523b"></script>

<div id="ta-feedback-overlay" class="ta-feedback-overlay" aria-hidden="true">
    <div class="ta-feedback-panel ta-conv-panel" role="dialog" aria-modal="true" aria-labelledby="ta-feedback-title">
        <div class="ta-conv-topbar">
            <h2 id="ta-feedback-title">Conversation</h2>
            <button type="button" class="chip-button" id="ta-feedback-close">Close</button>
        </div>
        <div class="mo-chat-shell">
            <div class="mo-chat-list">
                <div class="mo-chat-list-head">
                    <input class="mo-chat-search" id="ta-conv-search" type="search" placeholder="Search" autocomplete="off" />
                    <span class="mo-chat-count" id="ta-conv-unread-count" style="display:none;"></span>
                </div>
                <div class="mo-chat-thread-list" id="ta-dialog-thread-list"></div>
            </div>
            <div class="mo-chat-panel" id="ta-dialog-chat-panel">
                <div class="chat-empty" id="ta-dialog-chat-empty">Select an application to view the conversation.</div>
            </div>
        </div>
    </div>
</div>

<div id="ta-feedback-modal" class="ta-feedback-overlay" aria-hidden="true">
    <div class="ta-feedback-panel list-card" role="dialog" aria-modal="true" style="max-width:480px;">
        <div class="ta-feedback-head">
            <h2>Feedback</h2>
            <button type="button" class="chip-button" id="ta-feedback-modal-close">Close</button>
        </div>
        <div id="ta-feedback-modal-body" style="padding:12px 0 4px; white-space:pre-wrap; line-height:1.6; color:#3d4148; font-size:14px;"></div>
    </div>
</div>
</body>
</html>
