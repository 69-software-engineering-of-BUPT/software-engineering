<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Account Management</title>
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
                    <p>TA and MO accounts</p>
                </div>
                <div class="filter-actions">
                    <button class="chip-button active">All accounts</button>
                    <button class="chip-button">Pending review</button>
                    <button class="chip-button">Create account</button>
                </div>
            </section>

            <section class="list-card">
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
                    <span>ACTION</span>
                </div>

                <article class="list-row account-grid">
                    <div>
                        <strong>Dr. Zhao</strong>
                        <small>zhao.mo@campus.edu</small>
                    </div>
                    <span>MO</span>
                    <span>Language Center</span>
                    <span class="status success">● Active</span>
                    <span>Today</span>
                    <div class="row-actions">
                        <button>Edit</button>
                        <button>Disable</button>
                    </div>
                </article>

                <article class="list-row account-grid">
                    <div>
                        <strong>Dr. Chen</strong>
                        <small>chen.mo@campus.edu</small>
                    </div>
                    <span>MO</span>
                    <span>Data Science</span>
                    <span class="status success">● Active</span>
                    <span>Today</span>
                    <div class="row-actions">
                        <button>Edit</button>
                        <button>Disable</button>
                    </div>
                </article>

                <article class="list-row warn account-grid">
                    <div>
                        <strong>Prof. Morgan</strong>
                        <small>morgan.mo@campus.edu</small>
                    </div>
                    <span>MO</span>
                    <span>Design School</span>
                    <span class="status warning">● Pending</span>
                    <span>2 days ago</span>
                    <div class="row-actions">
                        <button>Approve</button>
                        <button>Reject</button>
                    </div>
                </article>

                <article class="list-row warn account-grid">
                    <div>
                        <strong>Lin Yu</strong>
                        <small>yu.ta@campus.edu</small>
                    </div>
                    <span>TA</span>
                    <span>Economics</span>
                    <span class="status warning">● Pending</span>
                    <span>Never</span>
                    <div class="row-actions">
                        <button>Approve</button>
                        <button>Reject</button>
                    </div>
                </article>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
