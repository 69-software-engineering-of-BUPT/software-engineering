<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Project Management</title>
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
            <button class="chip-button">Switch role</button>
            <button class="chip-button">Reset demo</button>
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
                <a class="nav-item active" href="${pageContext.request.contextPath}/jsp/ad/projects.jsp">
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
                    <button class="chip-button active">All positions</button>
                    <button class="chip-button">Unfilled positions</button>
                    <button class="chip-button">Export CSV</button>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row">
                    <h2>Project list</h2>
                    <span>4 item(s)</span>
                </div>

                <div class="list-head">
                    <span>MODULE</span>
                    <span>MO</span>
                    <span>POSTED</span>
                    <span>SEATS</span>
                    <span>FILLED</span>
                    <span>VACANCIES</span>
                    <span>STATUS</span>
                    <span>ACTION</span>
                </div>

                <article class="list-row">
                    <div>
                        <strong>Academic Writing Workshop TA</strong>
                        <small>LAN4005 · 6 day(s) live</small>
                    </div>
                    <span>Dr. Zhao</span>
                    <span>14 Mar 2026</span>
                    <span>2</span>
                    <span>2</span>
                    <span>0</span>
                    <span class="status success">● Filled</span>
                    <div class="row-actions">
                        <button>Remind MO</button>
                        <button>View</button>
                    </div>
                </article>

                <article class="list-row warn">
                    <div>
                        <strong>Advanced Data Analytics TA</strong>
                        <small>DATA6021 · 8 day(s) live</small>
                    </div>
                    <span>Dr. Chen</span>
                    <span>12 Mar 2026</span>
                    <span>2</span>
                    <span>1</span>
                    <span>1</span>
                    <span class="status warning">● Action Needed</span>
                    <div class="row-actions">
                        <button>Remind MO</button>
                        <button>View</button>
                    </div>
                </article>

                <article class="list-row warn">
                    <div>
                        <strong>Human Computer Interaction Studio TA</strong>
                        <small>DES5032 · 10 day(s) live</small>
                    </div>
                    <span>Prof. Morgan</span>
                    <span>10 Mar 2026</span>
                    <span>1</span>
                    <span>0</span>
                    <span>1</span>
                    <span class="status warning">● Action Needed</span>
                    <div class="row-actions">
                        <button>Remind MO</button>
                        <button>View</button>
                    </div>
                </article>

                <article class="list-row warn">
                    <div>
                        <strong>Foundation Economics TA</strong>
                        <small>ECO1010 · 15 day(s) live</small>
                    </div>
                    <span>Dr. Chen</span>
                    <span>05 Mar 2026</span>
                    <span>3</span>
                    <span>1</span>
                    <span>2</span>
                    <span class="status warning">● Action Needed</span>
                    <div class="row-actions">
                        <button>Remind MO</button>
                        <button>View</button>
                    </div>
                </article>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
