<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.bupt.tarecruit.model.ApplicationView" %>
<%@ page import="com.bupt.tarecruit.model.User" %>
<%!
    public static class Msg {
        public String sender; public String timestamp; public String text;
        public Msg(String s, String ts, String t) { sender = s; timestamp = ts; text = t; }
    }

    private static final Pattern REPLY_PATTERN =
        Pattern.compile("^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}) (TA|MO)\\]:\\s*(.*)$");

    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    private boolean hasText(String v) { return v != null && !v.trim().isEmpty(); }
    private String show(String v) { return hasText(v) ? esc(v) : "-"; }
    private String shortText(String value, int maxLength) {
        if (!hasText(value)) return "-";
        String text = value.replace("\r", " ").replace("\n", " ").trim();
        if (text.length() <= maxLength) return esc(text);
        return esc(text.substring(0, maxLength - 3) + "...");
    }
    private String avatarText(String id) {
        if (!hasText(id)) return "MO";
        String c = id.trim();
        return c.length() <= 2 ? c.toUpperCase() : c.substring(c.length() - 2).toUpperCase();
    }
    private String moduleAvatar(ApplicationView app) {
        String id = app.getJobId();
        if (!hasText(id)) id = app.getApplicationId();
        return avatarText(id == null ? "XX" : id);
    }
    private String badgeClass(String status) {
        String s = status == null ? "" : status.trim().toUpperCase();
        if ("APPROVED".equals(s)) return "badge badge-approved";
        if ("REJECTED".equals(s)) return "badge badge-rejected";
        if ("INTERVIEW".equals(s)) return "badge badge-interview";
        if ("PENDING".equals(s)) return "badge badge-pending";
        return "badge badge-new";
    }
    private String normalizeApproveType(String raw) {
        if (!hasText(raw)) return "";
        String s = raw.trim().toUpperCase();
        if (s.contains("NON") && s.contains("LEADER")) return "Non-leader approve";
        if (s.contains("LEADER")) return "Leader approve";
        if ("NL".equals(s)) return "Non-leader approve";
        if ("L".equals(s)) return "Leader approve";
        return raw;
    }
    private List<Msg> parseMessages(String statement) {
        List<Msg> out = new ArrayList<Msg>();
        if (statement == null) return out;
        int idx = statement.indexOf("\n[");
        String base = (idx >= 0 ? statement.substring(0, idx) : statement).trim();
        if (!base.isEmpty()) out.add(new Msg("TA", "", base));
        if (idx >= 0) {
            String tail = statement.substring(idx);
            for (String line : tail.split("\n")) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                Matcher m = REPLY_PATTERN.matcher(t);
                if (m.matches()) {
                    out.add(new Msg(m.group(2), m.group(1), m.group(3)));
                } else {
                    out.add(new Msg("TA", "", line));
                }
            }
        }
        return out;
    }
    private String latestPreview(List<Msg> msgs) {
        if (msgs.isEmpty()) return "";
        Msg last = msgs.get(msgs.size() - 1);
        String prefix = last.sender + ": ";
        String text = last.text == null ? "" : last.text.replaceAll("[\\r\\n]+", " ").trim();
        if (text.length() > 42) text = text.substring(0, 39) + "...";
        return prefix + text;
    }
    private String latestDate(List<Msg> msgs, String fallback) {
        if (!msgs.isEmpty()) {
            Msg last = msgs.get(msgs.size() - 1);
            if (hasText(last.timestamp)) return last.timestamp.substring(0, Math.min(10, last.timestamp.length()));
        }
        return hasText(fallback) ? fallback.substring(0, Math.min(10, fallback.length())) : "";
    }
    private boolean hasNewMoReply(List<Msg> msgs) {
        if (msgs.isEmpty()) return false;
        Msg last = msgs.get(msgs.size() - 1);
        return "MO".equals(last.sender);
    }
    private String latestMoMessageKey(List<Msg> msgs) {
        for (int i = msgs.size() - 1; i >= 0; i--) {
            Msg m = msgs.get(i);
            if ("MO".equals(m.sender)) {
                String ts = m.timestamp == null ? "" : m.timestamp;
                String tx = m.text == null ? "" : m.text;
                return ts + "|" + tx;
            }
        }
        return null;
    }
    private boolean needsTaAttention(List<Msg> msgs, String appId, Map<String,String> readKeys) {
        if (!hasNewMoReply(msgs)) return false;
        String latest = latestMoMessageKey(msgs);
        if (latest == null) return false;
        String read = readKeys == null ? null : readKeys.get(appId);
        return !latest.equals(read);
    }
%>
<%
    String contextPath = request.getContextPath();
    User currentUser = (User) request.getAttribute("currentUser");
    String studentId = (String) request.getAttribute("studentId");
    if (studentId == null) studentId = "";
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    if (unreadCount == null) unreadCount = 0;
    @SuppressWarnings("unchecked")
    Map<String, String> readKeys = (Map<String, String>) request.getAttribute("conversationReadKeys");
    if (readKeys == null) readKeys = new HashMap<String, String>();
    @SuppressWarnings("unchecked")
    List<ApplicationView> apps = (List<ApplicationView>) request.getAttribute("applicationList");
    if (apps == null) apps = new ArrayList<ApplicationView>();
    String activeAppId = (String) request.getAttribute("activeApplicationId");
    if ((activeAppId == null || activeAppId.isEmpty()) && !apps.isEmpty()) activeAppId = apps.get(0).getApplicationId();

    int convUnread = 0;
    List<List<Msg>> parsedList = new ArrayList<List<Msg>>();
    List<Boolean> unreadFlags = new ArrayList<Boolean>();
    for (ApplicationView a : apps) {
        List<Msg> m = parseMessages(a.getStatement());
        parsedList.add(m);
        boolean u = needsTaAttention(m, a.getApplicationId(), readKeys);
        unreadFlags.add(Boolean.valueOf(u));
        if (u) convUnread++;
    }

    String userDisplayName = (currentUser != null && currentUser.getName() != null && !currentUser.getName().isEmpty())
            ? currentUser.getName() : "Teaching Assistant";
    String avatarInit = studentId.length() >= 2 ? studentId.substring(0, 2).toUpperCase() : "TA";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>TA &middot; Conversation</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/app.css" />
    <style>
        .ta-conversation-page { height: 100vh; overflow: hidden; }
        .ta-conversation-page .ad-shell { width: calc(100vw - 36px); max-width: 1320px; height: calc(100vh - 36px); margin: 18px auto; display: grid; grid-template-rows: 76px minmax(0,1fr); }
        .ta-conversation-page .ad-layout { min-height: 0; height: 100%; }
        .ta-conversation-page .ad-sidebar { min-height: 0; overflow-y: auto; }
        .ta-conversation-page .ad-main { min-height: 0; overflow: hidden; display: flex; flex-direction: column; }
        .ta-conversation-page .mo-chat-shell { flex: 1; min-height: 0; display: grid; grid-template-columns: 320px minmax(0,1fr); border: 1px solid #ded4c7; border-radius: 8px; overflow: hidden; background: #fffdfa; }
        @media (max-width: 900px) {
            .ta-conversation-page { height: auto; overflow: auto; }
            .ta-conversation-page .ad-shell { width: auto; height: auto; display: block; }
            .ta-conversation-page .ad-layout { height: auto; }
            .ta-conversation-page .ad-main { overflow: visible; display: block; }
            .ta-conversation-page .mo-chat-shell { grid-template-columns: 1fr; height: auto; min-height: 0; }
            .ta-conversation-page .mo-chat-list { max-height: 320px; border-right: 0; border-bottom: 1px solid #e2e5ea; }
            .ta-conversation-page .chat-window.active { min-height: 560px; grid-template-rows: auto minmax(280px,1fr) auto; }
        }
    </style>
</head>
<body class="ad-page ta-conversation-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">TA</div>
            <div>
                <div class="brand-title">Conversation</div>
                <div class="brand-subtitle" id="ta-conv-brand-subtitle"><%= convUnread %> new / <%= apps.size() %> total</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="<%= contextPath %>/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= esc(avatarInit) %></span>
                <span><strong><%= esc(userDisplayName) %></strong><small><%= esc(studentId) %></small></span>
            </div>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">TA</span>
                <h3>Account</h3>
                <p>Signed in as <strong><%= studentId.isEmpty() ? "-" : esc(studentId) %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="<%= contextPath %>/ta/home">
                    <span class="nav-icon">HM</span>
                    <span><strong>Home</strong><small>Profile &amp; applications</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/ta/jobs">
                    <span class="nav-icon">JB</span>
                    <span><strong>Job overview</strong><small>Open positions</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/ta/notifications">
                    <span class="nav-icon">NT</span>
                    <span><strong>Notifications</strong><small><%= unreadCount %> unread</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">CO</span>
                    <span><strong>Conversation</strong><small id="ta-nav-conversation-count"><%= convUnread > 0 ? convUnread + " new" : "Messages from MO" %></small></span>
                </span>
            </section>
        </aside>

        <main class="ad-main">
            <% if (apps.isEmpty()) { %>
                <section class="list-card"><p class="ta-empty-hint">No applications yet.</p></section>
            <% } else { %>
                <section class="mo-chat-shell">
                    <div class="mo-chat-list">
                        <div class="mo-chat-list-head">
                            <input id="ta-conv-search" class="mo-chat-search" type="search" placeholder="Search" autocomplete="off" />
                            <span id="ta-conv-list-count" class="mo-chat-count" style="<%= convUnread > 0 ? "" : "display:none;" %>"><%= convUnread %></span>
                        </div>
                        <div class="mo-chat-thread-list">
                            <% for (int i = 0; i < apps.size(); i++) {
                                ApplicationView app = apps.get(i);
                                List<Msg> msgs = parsedList.get(i);
                                boolean isActive = activeAppId != null && activeAppId.equals(app.getApplicationId());
                                boolean unread = unreadFlags.get(i).booleanValue();
                                String preview = latestPreview(msgs);
                                String date = latestDate(msgs, app.getApplyTime());
                                String searchKey = ((app.getModuleName() == null ? "" : app.getModuleName()) + " "
                                        + (app.getJobId() == null ? "" : app.getJobId()) + " "
                                        + preview).toLowerCase();
                            %>
                                <button type="button"
                                        class="mo-thread<%= isActive ? " active" : "" %>"
                                        data-thread-id="ta-thread-<%= i %>"
                                        data-application-id="<%= esc(app.getApplicationId()) %>"
                                        data-needs-reply="<%= unread ? "true" : "false" %>"
                                        data-search="<%= esc(searchKey) %>">
                                    <span class="thread-avatar">
                                        <%= esc(moduleAvatar(app)) %>
                                        <% if (unread) { %><span class="thread-dot unread-marker"></span><% } %>
                                    </span>
                                    <span class="thread-main">
                                        <span class="thread-title">
                                            <strong><%= show(app.getModuleName()) %></strong>
                                            <small><%= esc(normalizeApproveType(app.getApplicationType())) %></small>
                                        </span>
                                        <span class="thread-preview"><%= esc(preview) %></span>
                                    </span>
                                    <span class="thread-meta">
                                        <span><%= esc(date) %></span>
                                        <% if (unread) { %><span class="thread-new unread-marker">new</span><% } %>
                                    </span>
                                </button>
                            <% } %>
                        </div>
                    </div>

                    <div class="mo-chat-panel">
                        <% for (int i = 0; i < apps.size(); i++) {
                            ApplicationView app = apps.get(i);
                            List<Msg> msgs = parsedList.get(i);
                            boolean isActive = activeAppId != null && activeAppId.equals(app.getApplicationId());
                            String moduleAv = moduleAvatar(app);
                        %>
                            <section class="chat-window<%= isActive ? " active" : "" %>" id="ta-thread-<%= i %>">
                                <div class="chat-header">
                                    <div>
                                        <h2><%= show(app.getModuleName()) %></h2>
                                        <div class="chat-subtitle">
                                            <%= show(app.getJobId()) %> - Application <%= show(app.getApplicationId()) %>
                                        </div>
                                    </div>
                                    <div class="chat-actions">
                                        <span class="<%= badgeClass(app.getStatus()) %>"><%= show(app.getStatus()) %></span>
                                    </div>
                                </div>

                                <div class="chat-messages">
                                    <% if (msgs.isEmpty()) { %>
                                        <div class="chat-empty">No conversation content.</div>
                                    <% } else {
                                        for (Msg m : msgs) {
                                            boolean fromMO = "MO".equals(m.sender);
                                            String avatar = fromMO ? "MO" : moduleAv;
                                            String label = (fromMO ? "MO" : "TA")
                                                    + (hasText(m.timestamp) ? " - " + m.timestamp : "");
                                    %>
                                        <div class="message-row<%= fromMO ? "" : " message-row--mo" %>">
                                            <div class="message-avatar"><%= esc(avatar) %></div>
                                            <div class="message-stack">
                                                <div class="message-meta"><%= esc(label) %></div>
                                                <div class="message-bubble">
                                                    <div class="message-content"><%= show(m.text) %></div>
                                                </div>
                                            </div>
                                        </div>
                                    <%  }
                                       } %>
                                </div>

                                <form class="chat-reply" method="post" action="<%= contextPath %>/ta/application/reply">
                                    <input type="hidden" name="applicationId" value="<%= esc(app.getApplicationId()) %>" />
                                    <input type="hidden" name="redirect" value="conversations" />
                                    <div class="reply-composer">
                                        <div class="reply-box">
                                            <textarea name="message" placeholder="Reply to module organiser..." maxlength="500" required></textarea>
                                            <div class="reply-actions">
                                                <button type="submit" class="chip-button active reply-send">Send</button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </section>
                        <% } %>
                    </div>
                </section>
            <% } %>
        </main>
    </div>
</div>
<script>
    (function () {
        'use strict';
        var readUrl = '<%= contextPath %>/ta/conversations/read';
        var navCountEl = document.getElementById('ta-nav-conversation-count');
        var brandSubtitleEl = document.getElementById('ta-conv-brand-subtitle');
        var listCountEl = document.getElementById('ta-conv-list-count');
        var totalApps = <%= apps.size() %>;
        var threads = Array.prototype.slice.call(document.querySelectorAll('.mo-thread'));
        var windows = Array.prototype.slice.call(document.querySelectorAll('.chat-window'));
        var search = document.getElementById('ta-conv-search');

        function clearUnreadMarkers(btn) {
            if (!btn) return;
            btn.setAttribute('data-needs-reply', 'false');
            Array.prototype.slice.call(btn.querySelectorAll('.unread-marker')).forEach(function (el) { el.parentNode.removeChild(el); });
        }
        function setUnreadCount(n) {
            if (typeof n !== 'number' || n < 0) return;
            if (navCountEl) navCountEl.textContent = n > 0 ? (n + ' new') : 'Messages from MO';
            if (brandSubtitleEl) brandSubtitleEl.textContent = n + ' new / ' + totalApps + ' total';
            if (listCountEl) {
                if (n > 0) { listCountEl.textContent = String(n); listCountEl.style.display = ''; }
                else { listCountEl.style.display = 'none'; }
            }
        }
        function markRead(btn) {
            if (!btn || btn.getAttribute('data-needs-reply') !== 'true') return;
            var appId = btn.getAttribute('data-application-id');
            if (!appId) return;
            clearUnreadMarkers(btn);
            var body = new URLSearchParams();
            body.append('applicationId', appId);
            fetch(readUrl, {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: body.toString()
            }).then(function (res) { return res.json(); })
              .then(function (data) {
                  if (data && typeof data.unreadCount === 'number') setUnreadCount(data.unreadCount);
              }).catch(function (err) { if (window.console) console.warn(err); });
        }

        function scrollMessagesToBottom(panel) {
            if (!panel) return;
            var messages = panel.querySelector('.chat-messages');
            if (!messages) return;
            var scroll = function () { messages.scrollTop = messages.scrollHeight; };
            scroll();
            if (window.requestAnimationFrame) window.requestAnimationFrame(scroll);
        }

        Array.prototype.slice.call(document.querySelectorAll('.chat-reply textarea[name="message"]')).forEach(function (textarea) {
            var form = textarea.form;
            textarea.addEventListener('keydown', function (event) {
                if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
                    event.preventDefault();
                    if (!textarea.value.trim() || !form) return;
                    if (form.requestSubmit) form.requestSubmit();
                    else form.submit();
                }
            });
        });

        threads.forEach(function (btn) {
            btn.addEventListener('click', function () {
                var id = btn.getAttribute('data-thread-id');
                var activePanel = null;
                threads.forEach(function (b) { b.classList.remove('active'); });
                btn.classList.add('active');
                windows.forEach(function (w) {
                    var active = w.id === id;
                    w.classList.toggle('active', active);
                    if (active) activePanel = w;
                });
                scrollMessagesToBottom(activePanel);
                markRead(btn);
            });
        });

        // Mark active thread (rendered active server-side) as read on initial load.
        var activeBtn = document.querySelector('.mo-thread.active');
        if (activeBtn) scrollMessagesToBottom(document.getElementById(activeBtn.getAttribute('data-thread-id')));
        if (activeBtn && activeBtn.getAttribute('data-needs-reply') === 'true') markRead(activeBtn);

        if (search) {
            search.addEventListener('input', function () {
                var q = (search.value || '').trim().toLowerCase();
                threads.forEach(function (btn) {
                    var key = btn.getAttribute('data-search') || '';
                    btn.style.display = (!q || key.indexOf(q) >= 0) ? '' : 'none';
                });
            });
        }
    })();
</script>
</body>
</html>
