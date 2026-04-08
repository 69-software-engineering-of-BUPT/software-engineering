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
    String cvFilePath = (String) request.getAttribute("cvFilePath");
    if (cvFilePath == null) cvFilePath = "";
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

            <section class="side-block">
                <p class="side-title">FILTER &amp; SEARCH</p>
                <label class="filter-field" style="margin-bottom:10px;">
                    <small>SEARCH MODULE</small>
                    <input type="text" id="job-search-module" placeholder="e.g. Software Engineering" />
                </label>
                <label class="filter-field" style="margin-bottom:10px;">
                    <small>FILTER BY MO</small>
                    <input type="text" id="job-search-mo" placeholder="e.g. Dr. Smith" />
                </label>
                <label class="filter-field" style="margin-bottom:10px;">
                    <small>SORT BY</small>
                    <select id="job-sort-by">
                        <option value="deadline">Deadline (soonest)</option>
                        <option value="published">Published (newest)</option>
                        <option value="module">Module name (A-Z)</option>
                    </select>
                </label>
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
        <form method="post" action="${pageContext.request.contextPath}/ta/apply" id="ta-apply-form">
            <input type="hidden" name="jobId" id="ta-apply-job-id" value="" />
            <div class="ta-profile-grid">
                <label class="filter-field">
                    <small>APPLICATION TYPE</small>
                    <select name="applicationType">
                        <option value="">Select type</option>
                        <option value="L">Leader TA</option>
                        <option value="NL">Non-leader TA</option>
                    </select>
                </label>
            </div>
            <label class="filter-field" style="margin-top:12px;">
                <small>PERSONAL STATEMENT (optional, max 500 characters)</small>
                <textarea name="statement" rows="6" maxlength="500" placeholder="Explain why you are a good fit for this role..."></textarea>
            </label>

            <div style="border-top:1px solid #f0ece4;margin-top:14px;padding-top:12px;">
                <p style="margin:0 0 8px;font-size:12px;font-weight:600;color:#5e5f60;">Attach CV</p>
                <div id="ta-apply-cv-current" style="margin-bottom:10px;font-size:13px;color:#3d4148;"></div>
                <label style="display:flex;align-items:center;gap:10px;cursor:pointer;" id="ta-apply-cv-label">
                    <input type="checkbox" name="cvAttached" value="true" id="ta-apply-cv-check"
                           style="width:16px;height:16px;accent-color:#5b4732;cursor:pointer;" />
                    <span style="font-size:13px;color:#3d4148;">Include my CV with this application</span>
                </label>
            </div>

            <div class="ta-profile-actions" style="margin-top:14px;">
                <button type="submit" class="chip-button active">Submit application</button>
            </div>
        </form>
    </div>
</div>

<script type="application/json" id="ta-jobs-json"><%= request.getAttribute("jobListJson") != null ? request.getAttribute("jobListJson") : "[]" %></script>
<script>
    window.TA_CONTEXT = "${pageContext.request.contextPath}";
    window.TA_CV_PATH = "<%= cvFilePath %>";
</script>
<script src="${pageContext.request.contextPath}/js/ta-jobs.js"></script>
</body>
</html>
