<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String adUserId   = (String) request.getAttribute("userId");
    String adUserName = (String) request.getAttribute("userName");
    if (adUserId == null)   adUserId   = (String) session.getAttribute("userAccount");
    if (adUserName == null) adUserName = (String) session.getAttribute("userName");
    if (adUserId == null)   adUserId   = "";
    if (adUserName == null) adUserName = "System Admin";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Account Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260406-5" />
</head>
<body class="ad-page">
<div class="ad-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">TA Recruitment Portal</div>
                <div class="brand-subtitle">Spring 2026 · Admin panel</div>
            </div>
        </div>
        <div class="top-actions">
            <button class="chip-button" data-action="reset-demo">Reset demo</button>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar">AD</span>
                <span>
                    <strong><%= adUserName %></strong>
                    <small><%= adUserId %></small>
                </span>
            </div>
        </div>
    </header>

    <div class="ad-layout">
        <aside class="ad-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">AD</span>
                <h3>Administrator</h3>
                <p>admin@campus.edu</p>
            </section>

            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item active" href="${pageContext.request.contextPath}/ad/accounts">
                    <span class="nav-icon">AC</span>
                    <span>
                        <strong>Account Management</strong>
                        <small>TA and MO accounts</small>
                    </span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/projects.jsp">
                    <span class="nav-icon">PM</span>
                    <span>
                        <strong>Project Management</strong>
                        <small>Vacancy monitor</small>
                    </span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/logs.jsp">
                    <span class="nav-icon">LG</span>
                    <span>
                        <strong>Operation Log</strong>
                        <small>Audit trail</small>
                    </span>
                </a>
            </section>

            <section class="side-block">
                <p class="side-title">SUMMARY</p>
                <div class="summary-item">
                    <span>
                        <small>ACCOUNTS</small>
                        <strong>7</strong>
                    </span>
                    <em>AC</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>PENDING</small>
                        <strong>2</strong>
                    </span>
                    <em>PD</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>LOGS</small>
                        <strong>6</strong>
                    </span>
                    <em>LG</em>
                </div>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1>Account Management</h1>
                    <p id="account-filter-summary">4 account(s) match the current filters</p>
                </div>
                <div class="filter-actions">
                    <button class="chip-button" data-action="import-csv">Import CSV</button>
                    <button class="chip-button" data-export-csv="true" data-export-filename="accounts">Export CSV</button>
                    <a class="chip-button active" href="${pageContext.request.contextPath}/register">+ Create TA account</a>
                </div>
            </section>

            <section class="account-filter-panel">
                <div class="account-filter-grid">
                    <label class="filter-field filter-search">
                        <small>SEARCH</small>
                        <input id="account-filter-search" type="text" placeholder="Name, email or department" />
                    </label>

                    <label class="filter-field">
                        <small>ROLE</small>
                        <select id="account-filter-role">
                            <option value="all">All</option>
                            <option value="TA">TA</option>
                            <option value="MO">MO</option>
                            <option value="AD">AD</option>
                        </select>
                    </label>

                    <label class="filter-field">
                        <small>STATUS</small>
                        <select id="account-filter-status">
                            <option value="all">All</option>
                            <option value="active">Active</option>
                            <option value="warning">Warning</option>
                            <option value="upper-limit">Reached Upper Limit</option>
                        </select>
                    </label>

                    <div class="filter-field">
                        <small>UPPER LIMIT REACHED</small>
                        <div class="upper-limit-chip"><span id="account-upper-limit-count">1</span> TA account(s)</div>
                    </div>
                </div>

                <div class="account-filter-actions">
                    <button id="account-filter-clear" class="chip-button">Clear</button>
                    <button id="account-filter-apply" class="chip-button active">Apply filters</button>
                </div>
            </section>

            <section class="account-workbench">
                <section class="list-card account-list-card">
                    <div class="list-title-row">
                        <h2>Account list</h2>
                        <span>7 item(s)</span>
                    </div>

                    <div class="list-head account-grid">
                        <span>NAME / EMAIL</span>
                        <span>ROLE</span>
                        <span>DEPARTMENT</span>
                        <span>STATUS</span>
                        <span>LAST LOGIN</span>
                    </div>

                    <article class="list-row account-grid account-row" tabindex="0"
                             data-name="Loading…"
                             data-email=""
                             data-role=""
                             data-department=""
                             data-status-text="Active"
                             data-status-class="success"
                             data-last-login="—"
                             data-load="—"
                             data-flag=""
                             data-assignments=""
                             style="display:none;">
                        <div><!-- placeholder --></div>
                    </article>
                    <div id="account-list-body"></div>
                </section>

                <aside class="list-card account-detail-card" id="account-detail-panel">
                    <div class="detail-head">
                        <div>
                            <h2 id="detail-name">Dr. Zhao</h2>
                            <p id="detail-email">zhao.mo@campus.edu</p>
                        </div>
                        <span class="status success" id="detail-flag-badge">● Active</span>
                    </div>

                    <div class="detail-kv-grid">
                        <div class="detail-kv">
                            <small>ACCOUNT TYPE</small>
                            <strong id="detail-role">MO</strong>
                        </div>
                        <div class="detail-kv">
                            <small>DEPARTMENT</small>
                            <strong id="detail-department">Language Center</strong>
                        </div>
                        <div class="detail-kv">
                            <small>COURSE LOAD</small>
                            <strong id="detail-load">1/3</strong>
                        </div>
                        <div class="detail-kv">
                            <small>LAST LOGIN</small>
                            <strong id="detail-last-login">Today</strong>
                        </div>
                        <div class="detail-kv detail-kv-full">
                            <small>FLAG</small>
                            <strong id="detail-flag">Current user</strong>
                        </div>
                    </div>

                    <section class="detail-assignment">
                        <h3>Active assignments</h3>
                        <ul id="detail-assignment-list"></ul>
                    </section>

                    <div class="detail-actions">
                        <button id="account-freeze-btn" class="detail-action-btn" type="button">Freeze account</button>
                        <button id="account-unfreeze-btn" class="detail-action-btn" type="button">Unfreeze account</button>
                        <button id="account-delete-btn" class="detail-action-btn danger" type="button">Delete account</button>
                    </div>
                </aside>
            </section>

            <!-- TA CV Files Section -->
            <section class="list-card" style="margin-top:16px;">
                <div class="list-title-row">
                    <h2>TA CV Files</h2>
                    <span id="ta-cv-badge">—</span>
                </div>
                <div class="list-head account-grid" style="grid-template-columns:120px 160px 120px 1fr;">
                    <span>USER ID</span>
                    <span>NAME</span>
                    <span>ACTIVE JOBS</span>
                    <span>CV FILE</span>
                </div>
                <div id="ta-cv-list"></div>
            </section>
        </main>
    </div>
</div>
<script type="application/json" id="ta-users-json"><%= request.getAttribute("taUsersJson") != null ? request.getAttribute("taUsersJson") : "[]" %></script>
<script type="application/json" id="all-users-json"><%= request.getAttribute("allUsersJson") != null ? request.getAttribute("allUsersJson") : "[]" %></script>
<script>
(function () {
    var raw = document.getElementById('all-users-json');
    if (!raw) return;
    var users;
    try { users = JSON.parse(raw.textContent || '[]'); } catch (e) { return; }
    var container = document.getElementById('account-list-body');
    if (!container) return;

    function escHtml(s) {
        return String(s || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }

    users.forEach(function (u, idx) {
        var role = (u.role || '').toUpperCase();
        var isTA  = role === 'TA';
        var activeJobs = u.activeJobsCount || 0;
        var upperLimit = isTA && activeJobs >= 3;
        var load       = isTA ? (activeJobs + '/3') : '—';
        var statusText = upperLimit ? 'Reached Upper Limit' : 'Active';
        var statusCls  = upperLimit ? 'warning' : 'success';
        var dept       = u.major || '—';
        var flag       = upperLimit ? 'Reached Upper Limit' : '';
        var displayId  = escHtml(u.userId || '');
        var displayName = escHtml(u.name || u.userId || '');

        var art = document.createElement('article');
        art.className = 'list-row account-grid account-row' + (upperLimit ? ' warn' : '') + (idx === 0 ? ' active' : '');
        art.tabIndex = 0;
        art.dataset.name       = u.name || u.userId || '';
        art.dataset.email      = u.userId || '';
        art.dataset.role       = role;
        art.dataset.department = dept;
        art.dataset.statusText = statusText;
        art.dataset.statusClass = statusCls;
        art.dataset.lastLogin  = '—';
        art.dataset.load       = load;
        art.dataset.flag       = flag;
        art.dataset.assignments = '';

        art.innerHTML =
            '<div><strong>' + displayName + '</strong><small>' + displayId + '</small></div>' +
            '<span>' + escHtml(role) + '</span>' +
            '<span>' + escHtml(dept) + '</span>' +
            '<span class="status ' + statusCls + '">● ' + escHtml(statusText) + '</span>' +
            '<span>—</span>';

        container.appendChild(art);
    });

    if (users.length === 0) {
        var p = document.createElement('p');
        p.style.cssText = 'padding:14px;color:#aaa;font-size:13px;';
        p.textContent = 'No user accounts found.';
        container.appendChild(p);
    }
}());
</script>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260406-6"></script>
<script>
(function () {
    var el = document.getElementById('ta-users-json');
    if (!el) return;
    var users;
    try { users = JSON.parse(el.textContent || '[]'); } catch (e) { return; }
    var list = document.getElementById('ta-cv-list');
    var badge = document.getElementById('ta-cv-badge');
    if (badge) badge.textContent = users.length + ' TA(s)';
    var ctx = '${pageContext.request.contextPath}';
    users.forEach(function (u) {
        var row = document.createElement('div');
        row.className = 'list-row';
        row.style.cssText = 'display:grid;grid-template-columns:120px 160px 120px 1fr;gap:8px;padding:10px 14px;border-bottom:1px solid #f0ece4;align-items:center;';
        var cvHtml = u.cvFilePath
            ? '<a href="' + ctx + '/' + u.cvFilePath + '" target="_blank" style="color:#5870b3;text-decoration:underline;font-size:12px;">' + u.cvFilePath + '</a>'
            : '<span style="color:#aaa;font-size:12px;">No CV uploaded</span>';
        row.innerHTML = '<span style="font-size:13px;">' + (u.userId || '') + '</span>'
            + '<span style="font-size:13px;">' + (u.name || '') + '</span>'
            + '<span style="font-size:13px;">' + (u.activeJobsCount || 0) + ' / 3</span>'
            + '<span>' + cvHtml + '</span>';
        list.appendChild(row);
    });
    if (users.length === 0) {
        var empty = document.createElement('p');
        empty.style.cssText = 'padding:14px;color:#aaa;font-size:13px;';
        empty.textContent = 'No TA accounts found.';
        list.appendChild(empty);
    }
}());
</script>
</body>
</html>
