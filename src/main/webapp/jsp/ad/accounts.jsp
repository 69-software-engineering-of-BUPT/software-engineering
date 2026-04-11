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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260410-2" />
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
                        <input id="account-filter-search" type="text" placeholder="Name or department" />
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
                            <small id="detail-student-id-label">STUDENT ID</small>
                            <strong id="detail-student-id">TA001</strong>
                        </div>
                        <div class="detail-kv">
                            <small>FULL NAME</small>
                            <strong id="detail-full-name">Dongting Ge</strong>
                        </div>
                        <div class="detail-kv">
                            <small>EMAIL</small>
                            <strong id="detail-email-field">3227875700@qq.com</strong>
                        </div>
                        <div class="detail-kv">
                            <small>PHONE</small>
                            <strong id="detail-phone">18129229280</strong>
                        </div>
                        <div class="detail-kv" id="detail-research-area-card">
                            <small id="detail-research-area-label">RESEARCH AREA</small>
                            <strong id="detail-research-area">computer science</strong>
                        </div>
                        <div class="detail-kv" id="detail-cet6-card">
                            <small>CET6 GRADE</small>
                            <strong id="detail-cet6-grade">655</strong>
                        </div>
                    </div>

                    <div class="detail-actions">
                        <button id="account-freeze-btn" class="detail-action-btn" type="button">Freeze account</button>
                        <button id="account-unfreeze-btn" class="detail-action-btn" type="button">Unfreeze account</button>
                        <button id="account-delete-btn" class="detail-action-btn danger" type="button">Delete account</button>
                    </div>
                </aside>
            </section>

        </main>
    </div>
</div>
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
        var dept       = u.researchArea || u.major || '—';
        var flag       = upperLimit ? 'Reached Upper Limit' : '';
        var studentId  = u.studentId || u.userId || '';
        var fullName   = u.fullName || u.name || u.userId || '';
        var email      = u.email || u.userId || '';
        var phone      = u.phoneNumber || '—';
        var cet6       = u.cet6Grade || '—';
        var displayId  = escHtml(studentId);
        var displayName = escHtml(fullName || u.userId || '');

        var art = document.createElement('article');
        art.className = 'list-row account-grid account-row' + (upperLimit ? ' warn' : '') + (idx === 0 ? ' active' : '');
        art.tabIndex = 0;
        art.dataset.name       = fullName;
        art.dataset.email      = email;
        art.dataset.role       = role;
        art.dataset.department = dept;
        art.dataset.statusText = statusText;
        art.dataset.statusClass = statusCls;
        art.dataset.lastLogin  = '—';
        art.dataset.load       = load;
        art.dataset.flag       = flag;
        art.dataset.assignments = '';
        art.dataset.studentId = studentId;
        art.dataset.fullName = fullName;
        art.dataset.phone = phone;
        art.dataset.researchArea = dept;
        art.dataset.cet6Grade = cet6;

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
<script src="${pageContext.request.contextPath}/js/app.js?v=20260410-4"></script>
</body>
</html>
