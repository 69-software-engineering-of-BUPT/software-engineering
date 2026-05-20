<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA · Job overview</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page ta-page">
<%
    String applySuccess = (String) session.getAttribute("applySuccess");
    String applyError = (String) session.getAttribute("applyError");
    if (applySuccess != null) session.removeAttribute("applySuccess");
    if (applyError != null) session.removeAttribute("applyError");
    String studentId = (String) request.getAttribute("studentId");
    if (studentId == null) studentId = "";
%>
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">JB</div>
            <div>
                <div class="brand-title">Job overview</div>
                <div class="brand-subtitle">Browse open TA positions &amp; apply</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/home">Home</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/ta/notifications">Notifications</a>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Profile &amp; applications</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">JB</span>
                    <span><strong>Job overview</strong><small>Open positions</small></span>
                </span>
                <a class="nav-item" href="${pageContext.request.contextPath}/ta/notifications">
                    <span class="nav-icon">NT</span>
                    <span><strong>Notifications</strong><small>Status updates</small></span>
                </a>
            </section>

        </aside>

        <main class="ad-main ta-main">
            <% if (applySuccess != null) { %>
            <section class="list-card ta-flash ta-flash--success" style="margin-bottom:14px;">
                <p style="margin:0;color:#4f6c4d;"><strong>Success:</strong> <%= applySuccess %></p>
            </section>
            <% } %>
            <% if (applyError != null) { %>
            <section class="list-card ta-flash ta-flash--warn" style="margin-bottom:14px;">
                <p style="margin:0;color:#6b5346;"><strong>Error:</strong> <%= applyError %></p>
            </section>
            <% } %>

            <section class="page-head">
                <div>
                    <h1 style="font-size:38px;">Open positions</h1>
                    <p>Click <strong>Apply</strong> to submit your application. You may include an optional statement.</p>
                </div>
                <div class="filter-actions">
                    <span id="job-count-label" style="color:#7f868f;font-size:13px;"></span>
                </div>
            </section>

            <section class="ta-job-filter-bar" style="display:flex;gap:14px;align-items:flex-end;flex-wrap:wrap;margin-bottom:14px;">
                <label class="filter-field" style="flex:1;min-width:160px;">
                    <small>SEARCH MODULE</small>
                    <input type="text" id="job-search-module" placeholder="e.g. Software Engineering" />
                </label>
                <label class="filter-field" style="flex:1;min-width:140px;">
                    <small>FILTER BY MO</small>
                    <input type="text" id="job-search-mo" placeholder="e.g. Dr. Smith" />
                </label>
                <label class="filter-field" style="flex:1;min-width:140px;">
                    <small>LISTED BY</small>
                    <select id="job-sort-by">
                        <option value="deadline">Deadline (soonest)</option>
                        <option value="published">Published (newest)</option>
                        <option value="module">Module name (A-Z)</option>
                    </select>
                </label>
            </section>

            <div id="ta-job-list"></div>
            <p id="ta-job-empty" class="ta-empty-hint" hidden>No open positions at this time.</p>
        </main>
    </div>
</div>

<!-- Apply modal -->
<div id="ta-apply-overlay" class="ta-feedback-overlay" aria-hidden="true">
    <div class="ta-feedback-panel list-card" role="dialog" aria-modal="true" aria-labelledby="ta-apply-title">
        <div class="ta-feedback-head">
            <h2 id="ta-apply-title">Apply for position</h2>
            <button type="button" class="chip-button" id="ta-apply-close">Close</button>
        </div>
        <p class="ta-feedback-meta" id="ta-apply-meta"></p>
        <form method="post" action="${pageContext.request.contextPath}/ta/apply" enctype="multipart/form-data" id="ta-apply-form">
            <input type="hidden" name="jobId" id="ta-apply-job-id" value="" />
            <div class="ta-profile-grid">
                <label class="filter-field">
                    <small>APPLICATION TYPE</small>
                    <select name="applicationType" id="ta-apply-type">
                        <option value="" disabled selected hidden>Select type</option>
                        <option value="L">Leader TA</option>
                        <option value="NL">Non-leader TA</option>
                    </select>
                </label>
            </div>
            <div style="margin-top:12px;" class="filter-field" id="ta-apply-cv-field">
                <small>UPLOAD CV (PDF only, max 5 MB)</small>
                <input type="file" name="cvFile" accept=".pdf" id="ta-apply-cv-input" style="display:none;" />
                <div id="ta-apply-cv-area" style="display:flex;align-items:center;gap:10px;margin-top:4px;">
                    <button type="button" id="ta-apply-cv-browse" class="chip-button active" style="font-size:12px;">Choose file</button>
                    <span id="ta-apply-cv-status" style="font-size:13px;color:#7f868f;">No file chosen</span>
                </div>
                <div id="ta-apply-cv-actions" style="display:none;margin-top:8px;align-items:center;gap:10px;">
                    <button type="button" id="ta-apply-cv-view" class="chip-button" style="font-size:11px;padding:2px 10px;">View CV</button>
                    <button type="button" id="ta-apply-cv-remove" class="chip-button" style="font-size:11px;padding:2px 10px;">Remove</button>
                </div>
            </div>
            <label class="filter-field" style="margin-top:12px;">
                <small>PERSONAL STATEMENT (optional, max 500 characters)</small>
                <textarea name="statement" rows="6" maxlength="500" placeholder="Explain why you are a good fit for this role..."></textarea>
            </label>

            <div class="ta-profile-actions" style="margin-top:14px;">
                <button type="submit" class="chip-button active">Submit application</button>
            </div>
        </form>
    </div>
</div>

<script type="application/json" id="ta-jobs-json"><%= request.getAttribute("jobListJson") != null ? request.getAttribute("jobListJson") : "[]" %></script>
<script>
    window.TA_CONTEXT = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/js/ta-jobs.js"></script>
</body>
</html>
