<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Account Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260406-2" />
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
                <a class="nav-item active" href="${pageContext.request.contextPath}/jsp/ad/accounts.jsp">
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
                            <option value="pending">Pending</option>
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

                    <article class="list-row account-grid account-row active" tabindex="0"
                             data-name="Dr. Zhao"
                             data-email="zhao.mo@campus.edu"
                             data-role="MO"
                             data-department="Language Center"
                             data-status-text="Active"
                             data-status-class="success"
                             data-last-login="Today"
                             data-load="1/3"
                             data-flag="Current user"
                             data-assignments="Academic Writing|Dr. Zhao|03 Apr 2026;English Training|Dr. Zhao|08 Apr 2026">
                        <div>
                            <strong>Dr. Zhao</strong>
                            <small>zhao.mo@campus.edu</small>
                        </div>
                        <span>MO</span>
                        <span>Language Center</span>
                        <span class="status success">● Active</span>
                        <span>Today</span>
                    </article>

                    <article class="list-row account-grid account-row" tabindex="0"
                             data-name="Dr. Chen"
                             data-email="chen.mo@campus.edu"
                             data-role="MO"
                             data-department="Data Science"
                             data-status-text="Active"
                             data-status-class="success"
                             data-last-login="Today"
                             data-load="0/3"
                             data-flag="Owns 2 published positions"
                             data-assignments="Data Mining|Dr. Chen|05 Apr 2026;Machine Learning|Dr. Chen|11 Apr 2026">
                        <div>
                            <strong>Dr. Chen</strong>
                            <small>chen.mo@campus.edu</small>
                        </div>
                        <span>MO</span>
                        <span>Data Science</span>
                        <span class="status success">● Active</span>
                        <span>Today</span>
                    </article>

                    <article class="list-row warn account-grid account-row" tabindex="0"
                             data-name="Prof. Morgan"
                             data-email="morgan.mo@campus.edu"
                             data-role="MO"
                             data-department="Design School"
                             data-status-text="Pending"
                             data-status-class="warning"
                             data-last-login="2 days ago"
                             data-load="2/3"
                             data-flag="Pending approval"
                             data-assignments="Studio Design|Prof. Morgan|06 Apr 2026;Visual Design|Prof. Morgan|09 Apr 2026">
                        <div>
                            <strong>Prof. Morgan</strong>
                            <small>morgan.mo@campus.edu</small>
                        </div>
                        <span>MO</span>
                        <span>Design School</span>
                        <span class="status warning">● Pending</span>
                        <span>2 days ago</span>
                    </article>

                    <article class="list-row warn account-grid account-row" tabindex="0"
                             data-name="Lin Yu"
                             data-email="yu.ta@campus.edu"
                             data-role="TA"
                             data-department="Economics"
                             data-status-text="Pending"
                             data-status-class="warning"
                             data-last-login="Never"
                             data-load="3/3"
                             data-flag="Reached Upper Limit"
                             data-assignments="Microeconomics|Dr. Chen|05 Apr 2026;Statistics|Prof. Allen|08 Apr 2026;Econometrics|Dr. Stone|10 Apr 2026">
                        <div>
                            <strong>Lin Yu</strong>
                            <small>yu.ta@campus.edu</small>
                        </div>
                        <span>TA</span>
                        <span>Economics</span>
                        <span class="status warning">● Pending</span>
                        <span>Never</span>
                    </article>
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
                </aside>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260406-2"></script>
</body>
</html>
