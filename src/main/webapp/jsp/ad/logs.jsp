<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="com.bupt.tarecruit.model.OperationLog" %>
<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    private String actionKey(String actionType) {
        if (actionType == null || actionType.trim().isEmpty()) return "reminder-sent";
        return actionType.trim().toLowerCase().replace('_', '-');
    }
%>
<%
    String _adRole = (String) session.getAttribute("userRole");
    if (session.getAttribute("userAccount") == null || !"ADMIN".equalsIgnoreCase(_adRole)) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<OperationLog> operationLogs = (List<OperationLog>) request.getAttribute("operationLogs");
    if (operationLogs == null) {
        operationLogs = Collections.emptyList();
    }
    int warningCount = 0;
    for (OperationLog log : operationLogs) {
        if ("warning".equalsIgnoreCase(log.getResultClass())) {
            warningCount++;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>AD - Operation Logs</title>
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
                <a class="nav-item" href="${pageContext.request.contextPath}/jsp/ad/projects.jsp">
                    <span class="nav-icon">PM</span>
                    <span>
                        <strong>Project Management</strong>
                        <small>Vacancy monitor</small>
                    </span>
                </a>
                <a class="nav-item active" href="${pageContext.request.contextPath}/ad/logs">
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
                        <strong><%= operationLogs.size() %></strong>
                    </span>
                    <em>LG</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>WARNINGS</small>
                        <strong><%= warningCount %></strong>
                    </span>
                    <em>WR</em>
                </div>
                <div class="summary-item">
                    <span>
                        <small>EXPORTS</small>
                        <strong>0</strong>
                    </span>
                    <em>EX</em>
                </div>
            </section>
        </aside>

        <main class="ad-main">
            <section class="page-head">
                <div>
                    <h1>Operation Log</h1>
                    <p id="log-filter-summary"><%= operationLogs.size() %> log entry or entries match the current filters</p>
                </div>
                <div class="filter-actions">
                    <button class="chip-button" data-export-csv="true" data-export-filename="operation-logs">Export CSV</button>
                </div>
            </section>

            <section class="account-filter-panel log-filter-panel">
                <div class="account-filter-grid log-filter-grid">
                    <label class="filter-field">
                        <small>ACCOUNT TYPE</small>
                        <select id="log-filter-role">
                            <option value="all">All</option>
                            <option value="AD">AD</option>
                            <option value="TA">TA</option>
                            <option value="MO">MO</option>
                        </select>
                    </label>

                    <label class="filter-field">
                        <small>ACTION</small>
                        <select id="log-filter-action">
                            <option value="all">All</option>
                            <option value="account-deleted">Account Deleted</option>
                            <option value="account-frozen">Account Frozen</option>
                            <option value="reminder-sent">Reminder Sent</option>
                            <option value="application-submitted">Application Submitted</option>
                            <option value="supplement-requested">Supplement Requested</option>
                            <option value="application-approved">Application Approved</option>
                            <option value="csv-export">CSV Export</option>
                        </select>
                    </label>

                    <label class="filter-field">
                        <small>RANGE</small>
                        <select id="log-filter-range">
                            <option value="all">All</option>
                            <option value="current-sprint" selected>Current sprint</option>
                            <option value="today">Today</option>
                            <option value="last-7-days">Last 7 days</option>
                        </select>
                    </label>

                    <div class="filter-field">
                        <small>ENTRIES</small>
                        <div class="upper-limit-chip"><span id="log-filter-entry-count"><%= operationLogs.size() %></span></div>
                    </div>
                </div>

                <div class="account-filter-actions">
                    <button id="log-filter-clear" class="chip-button">Clear</button>
                    <button id="log-filter-apply" class="chip-button active">Apply filters</button>
                </div>
            </section>

            <section class="list-card">
                <div class="list-title-row">
                    <h2>Log entries</h2>
                    <span><%= operationLogs.size() %> item(s)</span>
                </div>

                <div class="list-head log-grid">
                    <span>TIME</span>
                    <span>ACTOR</span>
                    <span>ACTION</span>
                    <span>TARGET</span>
                    <span>RESULT</span>
                    <span>ACTION</span>
                </div>

                <% if (operationLogs.isEmpty()) { %>
                    <p class="ta-empty-hint">No operation logs yet.</p>
                <% } %>

                <% for (OperationLog log : operationLogs) {
                    String resultClass = "warning".equalsIgnoreCase(log.getResultClass()) ? "warning" : "success";
                %>
                <article class="list-row <%= "warning".equals(resultClass) ? "warn " : "" %>log-grid log-row"
                         data-role="<%= esc(log.getActorRole()) %>"
                         data-action-key="<%= esc(actionKey(log.getActionType())) %>"
                         data-time="<%= esc(log.getCreatedAt()) %>">
                    <span><%= esc(log.getCreatedAt()) %></span>
                    <span><%= esc(log.getActorName()) %></span>
                    <span><%= esc(log.getActionLabel()) %></span>
                    <span><%= esc(log.getTargetName()) %></span>
                    <span class="status <%= resultClass %>">● <%= "warning".equals(resultClass) ? "Warning" : "Success" %></span>
                    <div class="row-actions">
                        <button data-action="log-details" data-log-message="<%= esc(log.getMessage()) %>">Details</button>
                    </div>
                </article>
                <% } %>
            </section>
        </main>
    </div>
</div>
<script type="application/json" id="server-operation-logs"><%= request.getAttribute("operationLogsJson") != null ? request.getAttribute("operationLogsJson") : "[]" %></script>
<script src="${pageContext.request.contextPath}/js/app.js?v=20260518-oplog"></script>
</body>
</html>
