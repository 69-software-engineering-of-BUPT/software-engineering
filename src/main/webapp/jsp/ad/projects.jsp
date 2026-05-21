<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String _adRole = (String) session.getAttribute("userRole");
    if (session.getAttribute("userAccount") == null || !"ADMIN".equalsIgnoreCase(_adRole)) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Project Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260518-ad-bg-2" />
</head>
<body class="ad-page ad-page--admin">
<div class="ad-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TR</div>
            <div>
                <div class="brand-title">TA Recruitment Portal</div>
                <div class="brand-subtitle">Spring 2026 · Role based prototype</div>
            </div>
        </div>
        <div class="top-actions">
            <button class="chip-button" data-action="reset-demo">Reset demo</button>
            <a class="chip-button" href="${pageContext.request.contextPath}/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar">AD</span>
                <span>
                    <strong>System Admin</strong>
                    <small>Administrator</small>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/ad/accounts">
                    <span class="nav-icon">AC</span>
                    <span>
                        <strong>Account Management</strong>
                        <small>TA and MO accounts</small>
                    </span>
                </a>
                <a class="nav-item active" href="${pageContext.request.contextPath}/ad/projects">
                    <span class="nav-icon">PM</span>
                    <span>
                        <strong>Project Management</strong>
                        <small>Vacancy monitor</small>
                    </span>
                </a>
                <a class="nav-item" href="${pageContext.request.contextPath}/ad/logs">
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
                        <small>VACANCIES</small>
                        <strong>3</strong>
                    </span>
                    <em>VC</em>
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
                    <h1>Project Management</h1>
                    <p>All published positions</p>
                </div>
                <div class="filter-actions">
                    <button class="chip-button active" data-filter="projects-all">All positions</button>
                    <button class="chip-button" data-filter="projects-unfilled">Unfilled positions</button>
                    <button class="chip-button" data-export-csv="true" data-export-filename="projects">Export CSV</button>
                </div>
            </section>

            <section class="project-workbench">
                <section class="list-card project-list-card">
                    <div class="list-title-row">
                        <h2>Project list</h2>
                        <span>0 item(s)</span>
                    </div>

                    <div class="list-head">
                        <span>MODULE</span>
                        <span>MO</span>
                        <span>POSTED</span>
                        <span>LEADER</span>
                        <span>MEMBER</span>
                        <span>VACANCIES</span>
                        <span>STATUS</span>
                        <span>ACTION</span>
                    </div>

                    <div id="project-list-body"></div>
                </section>

                <aside class="list-card project-detail-card" id="project-detail-panel">
                    <div class="detail-head">
                        <div>
                            <h2 id="project-detail-name">Academic Writing Workshop TA</h2>
                            <p id="project-detail-code">LAN4005 · 6 day(s) live</p>
                        </div>
                        <span class="status success" id="project-detail-status">● Filled</span>
                    </div>

                    <section class="project-detail-block">
                        <h3>Course basic information</h3>
                        <div class="project-basic-grid">
                            <div class="detail-kv">
                                <small>MODULE ORGANISER</small>
                                <strong id="project-detail-mo">Dr. Zhao</strong>
                            </div>
                            <div class="detail-kv">
                                <small>POSTED</small>
                                <strong id="project-detail-posted">14 Mar 2026</strong>
                            </div>
                            <div class="detail-kv">
                                <small>APPLICATION DEADLINE</small>
                                <strong id="project-detail-deadline">20 Apr 2026</strong>
                            </div>
                            <div class="detail-kv">
                                <small>SEATS / FILLED / VACANCIES</small>
                                <strong id="project-detail-capacity">2 / 2 / 0</strong>
                            </div>
                            <div class="detail-kv">
                                <small>LEADER FILLED / SEATS</small>
                                <strong id="project-detail-leader-capacity">0 / 0</strong>
                            </div>
                            <div class="detail-kv">
                                <small>MEMBER FILLED / SEATS</small>
                                <strong id="project-detail-member-capacity">0 / 0</strong>
                            </div>
                            <div class="detail-kv detail-kv-full">
                                <small>REQUIREMENTS</small>
                                <strong id="project-detail-req">Strong writing skills · Academic English background · Clear communication</strong>
                            </div>
                            <div class="detail-kv detail-kv-full">
                                <small>POSITION DETAILS</small>
                                <strong id="project-detail-desc">Support tutorial preparation, attendance checks and assignment rubric support.</strong>
                            </div>
                        </div>
                    </section>

                    <section class="project-detail-block">
                        <h3>TA information</h3>
                        <div class="project-ta-grid">
                            <div class="project-ta-list-box">
                                <h4>Approved TA</h4>
                                <ul id="project-approved-ta-list"></ul>
                            </div>
                            <div class="project-ta-list-box">
                                <h4>Pending TA</h4>
                                <ul id="project-pending-ta-list"></ul>
                            </div>
                        </div>
                    </section>
                </aside>
            </section>
        </main>
    </div>
</div>
<script type="application/json" id="project-views-json"><%= request.getAttribute("projectViewsJson") != null ? request.getAttribute("projectViewsJson") : "[]" %></script>
<script>
(function () {
    var raw = document.getElementById('project-views-json');
    var container = document.getElementById('project-list-body');
    var countNode = document.querySelector('.project-list-card .list-title-row span');
    if (!raw || !container) return;

    function escHtml(s) {
        return String(s == null ? '' : s)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    function value(v, fallback) {
        return v == null || v === '' ? fallback : v;
    }

    var projects;
    try {
        projects = JSON.parse(raw.textContent || '[]');
    } catch (e) {
        projects = [];
    }

    projects.forEach(function (project, index) {
        var statusClass = project.statusClass === 'warning' ? 'warning' : 'success';
        var row = document.createElement('article');
        row.className = 'list-row project-row' + (statusClass === 'warning' ? ' warn' : '') + (index === 0 ? ' active' : '');
        row.dataset.jobId = value(project.jobId, '');
        row.dataset.module = value(project.module, '-');
        row.dataset.moduleCode = value(project.moduleCode, '-');
        row.dataset.mo = value(project.mo, '-');
        row.dataset.moId = value(project.moId, '-');
        row.dataset.posted = value(project.posted, '-');
        row.dataset.deadline = value(project.deadline, '-');
        row.dataset.seats = value(project.seats, 0);
        row.dataset.filled = value(project.filled, 0);
        row.dataset.leaderSeats = value(project.leaderSeats, 0);
        row.dataset.leaderFilled = value(project.leaderFilled, 0);
        row.dataset.memberSeats = value(project.memberSeats, 0);
        row.dataset.memberFilled = value(project.memberFilled, 0);
        row.dataset.vacancies = value(project.vacancies, 0);
        row.dataset.statusText = value(project.statusText, '-');
        row.dataset.statusClass = statusClass;
        row.dataset.liveDays = value(project.liveDays, '-');
        row.dataset.requirements = value(project.requirements, '-');
        row.dataset.details = value(project.details, '-');
        row.dataset.approvedTas = value(project.approvedTas, '');
        row.dataset.pendingTas = value(project.pendingTas, '');

        row.innerHTML =
            '<div><strong>' + escHtml(project.module || '-') + '</strong><small>' +
            escHtml(value(project.moduleCode, '-')) + ' · ' + escHtml(value(project.applicationCount, 0)) + ' application(s)</small></div>' +
            '<span>' + escHtml(value(project.mo, '-')) + '</span>' +
            '<span>' + escHtml(value(project.posted, '-')) + '</span>' +
            '<span>' + escHtml(value(project.leaderFilled, 0)) + ' / ' + escHtml(value(project.leaderSeats, 0)) + '</span>' +
            '<span>' + escHtml(value(project.memberFilled, 0)) + ' / ' + escHtml(value(project.memberSeats, 0)) + '</span>' +
            '<span>' + escHtml(value(project.vacancies, 0)) + '</span>' +
            '<span class="status ' + statusClass + '">● ' + escHtml(value(project.statusText, '-')) + '</span>' +
            '<div class="row-actions"><button data-action="project-remind">Remind MO</button><button data-action="project-view">View</button></div>';

        container.appendChild(row);
    });

    if (countNode) {
        countNode.textContent = projects.length + ' item(s)';
    }

    if (projects.length === 0) {
        var empty = document.createElement('p');
        empty.style.cssText = 'padding:14px;color:#aaa;font-size:13px;';
        empty.textContent = 'No published positions found.';
        container.appendChild(empty);
    }
}());
</script>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260518-oplog"></script>
</body>
</html>
