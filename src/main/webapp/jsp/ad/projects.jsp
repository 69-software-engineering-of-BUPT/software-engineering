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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=20260406-8" />
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
                    <button class="chip-button active" data-filter="projects-all">All positions</button>
                    <button class="chip-button" data-filter="projects-unfilled">Unfilled positions</button>
                    <button class="chip-button" data-export-csv="true" data-export-filename="projects">Export CSV</button>
                </div>
            </section>

            <section class="project-workbench">
                <section class="list-card project-list-card">
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

                    <article class="list-row project-row active"
                             data-module="Academic Writing Workshop TA"
                             data-module-code="LAN4005"
                             data-mo="Dr. Zhao"
                             data-posted="14 Mar 2026"
                             data-deadline="20 Apr 2026"
                             data-seats="2"
                             data-filled="2"
                             data-vacancies="0"
                             data-status-text="Filled"
                             data-status-class="success"
                             data-live-days="6"
                             data-requirements="Strong writing skills;Academic English background;Clear communication"
                             data-details="Support tutorial preparation, attendance checks and assignment rubric support."
                             data-approved-tas="Mia Wang|mia.wang@campus.edu|3.9 GPA;Daniel Gu|daniel.gu@campus.edu|2.0 yrs TA exp"
                             data-pending-tas="">
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
                            <button data-action="project-remind">Remind MO</button>
                            <button data-action="project-view">View</button>
                        </div>
                    </article>

                    <article class="list-row warn project-row"
                             data-module="Advanced Data Analytics TA"
                             data-module-code="DATA6021"
                             data-mo="Dr. Chen"
                             data-posted="12 Mar 2026"
                             data-deadline="18 Apr 2026"
                             data-seats="2"
                             data-filled="1"
                             data-vacancies="1"
                             data-status-text="Action Needed"
                             data-status-class="warning"
                             data-live-days="8"
                             data-requirements="Machine learning background;Python;Clear written communication"
                             data-details="Support lab preparation, attendance checks and experiment record reviews."
                             data-approved-tas="Mia Wang|mia.wang@campus.edu|3.8 GPA"
                             data-pending-tas="Leo Li|leo.li@campus.edu|Data Mining focus;Sara Xu|sara.xu@campus.edu|5 failed logins">
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
                            <button data-action="project-remind">Remind MO</button>
                            <button data-action="project-view">View</button>
                        </div>
                    </article>

                    <article class="list-row warn project-row"
                             data-module="Human Computer Interaction Studio TA"
                             data-module-code="DES5032"
                             data-mo="Prof. Morgan"
                             data-posted="10 Mar 2026"
                             data-deadline="16 Apr 2026"
                             data-seats="1"
                             data-filled="0"
                             data-vacancies="1"
                             data-status-text="Action Needed"
                             data-status-class="warning"
                             data-live-days="10"
                             data-requirements="UX research experience;Figma prototyping;Workshop facilitation"
                             data-details="Assist with studio workshops, prototype critique sessions and rubric reviews."
                             data-approved-tas=""
                             data-pending-tas="Lin Yu|yu.ta@campus.edu|Portfolio under review;Tina Zhou|tina.zhou@campus.edu|Strong UX writing">
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
                            <button data-action="project-remind">Remind MO</button>
                            <button data-action="project-view">View</button>
                        </div>
                    </article>

                    <article class="list-row warn project-row"
                             data-module="Foundation Economics TA"
                             data-module-code="ECO1010"
                             data-mo="Dr. Chen"
                             data-posted="05 Mar 2026"
                             data-deadline="22 Apr 2026"
                             data-seats="3"
                             data-filled="1"
                             data-vacancies="2"
                             data-status-text="Action Needed"
                             data-status-class="warning"
                             data-live-days="15"
                             data-requirements="Economics major preferred;SPSS basic skills;Reliable attendance"
                             data-details="Support tutorial Q&A, midterm prep sessions and marking assistance."
                             data-approved-tas="Mia Wang|mia.wang@campus.edu|Macroeconomics A"
                             data-pending-tas="Daniel Gu|daniel.gu@campus.edu|2/3 load;Nora Lin|nora.lin@campus.edu|First-time applicant">
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
                            <button data-action="project-remind">Remind MO</button>
                            <button data-action="project-view">View</button>
                        </div>
                    </article>
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
<script src="${pageContext.request.contextPath}/js/app.js?v=20260406-8"></script>
</body>
</html>
