<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Operation Logs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css" />
</head>
<body class="ad-page">
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
            <button class="chip-button" data-action="switch-role">Switch role</button>
            <button class="chip-button" data-action="reset-demo">Reset demo</button>
            <div class="user-pill">
                <span class="avatar">SA</span>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/accounts.jsp">
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
                <a class="nav-item active" href="${pageContext.request.contextPath}/jsp/ad/logs.jsp">
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
                        <small>LOGS TODAY</small>
                        <strong>6</strong>
                    </span>
                    <em>LG</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>WARNINGS</small>
                        <strong>2</strong>
                    </span>
                    <em>WR</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>EXPORTS</small>
                        <strong>1</strong>
                    </span>
                    <em>EX</em>
                </div>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1>Operation Log</h1>
                    <p>System audit trail</p>
                </div>
                <div class="filter-actions">
                    <button class="chip-button active" data-filter="logs-all">All actions</button>
                    <button class="chip-button" data-filter="logs-risk">Risk events</button>
                    <button class="chip-button" data-export-csv="true" data-export-filename="operation-logs">Export CSV</button>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row">
                    <h2>Log entries</h2>
                    <span>6 item(s)</span>
                </div>

                <div class="list-head log-grid">
                    <span>TIME</span>
                    <span>ACTOR</span>
                    <span>ACTION</span>
                    <span>TARGET</span>
                    <span>RESULT</span>
                    <span>ACTION</span>
                </div>

                <article class="list-row log-grid">
                    <span>2026-04-01 10:22</span>
                    <span>System Admin</span>
                    <span>Approved account</span>
                    <span>yu.ta@campus.edu</span>
                    <span class="status success">● Success</span>
                    <div class="row-actions">
                        <button data-action="log-details">Details</button>
                    </div>
                </article>

                <article class="list-row log-grid">
                    <span>2026-04-01 09:58</span>
                    <span>System Admin</span>
                    <span>Exported vacancies</span>
                    <span>Project list</span>
                    <span class="status success">● Success</span>
                    <div class="row-actions">
                        <button data-action="log-details">Details</button>
                    </div>
                </article>

                <article class="list-row warn log-grid">
                    <span>2026-04-01 09:35</span>
                    <span>System Admin</span>
                    <span>Rejected account</span>
                    <span>morgan.mo@campus.edu</span>
                    <span class="status warning">● Warning</span>
                    <div class="row-actions">
                        <button data-action="log-details">Details</button>
                    </div>
                </article>

                <article class="list-row warn log-grid">
                    <span>2026-04-01 08:47</span>
                    <span>System Admin</span>
                    <span>Forced password reset</span>
                    <span>chen.mo@campus.edu</span>
                    <span class="status warning">● Warning</span>
                    <div class="row-actions">
                        <button data-action="log-details">Details</button>
                    </div>
                </article>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260404-2"></script>
</body>
</html>
