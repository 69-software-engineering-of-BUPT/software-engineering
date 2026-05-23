<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bupt.tarecruit.model.ConversationMessage" %>
<%@ page import="com.bupt.tarecruit.model.ConversationThread" %>
<%!
    private String esc(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String show(String value) {
        return hasText(value) ? esc(value) : "-";
    }

    private String shortText(String value, int maxLength) {
        if (!hasText(value)) return "-";
        String text = value.replace("\r", " ").replace("\n", " ").trim();
        if (text.length() <= maxLength) return esc(text);
        return esc(text.substring(0, maxLength - 3) + "...");
    }

    private String badgeClass(String status) {
        if ("APPROVED".equalsIgnoreCase(status)) return "badge-approved";
        if ("REJECTED".equalsIgnoreCase(status)) return "badge-rejected";
        if ("INTERVIEW".equalsIgnoreCase(status)) return "badge-interview";
        return "badge-pending";
    }

    private String avatarText(String taId) {
        if (!hasText(taId)) return "TA";
        String clean = taId.trim();
        return clean.length() <= 2 ? clean.toUpperCase() : clean.substring(clean.length() - 2).toUpperCase();
    }
%>
<%
    String contextPath = request.getContextPath();
    String userId = (String) request.getAttribute("userId");
    String userName = (String) request.getAttribute("userName");
    if (userId == null) userId = "";
    if (userName == null) userName = "Module Organiser";
    String currentAvatarText = userName.length() >= 2 ? userName.substring(0, 2).toUpperCase() : "MO";

    List<ConversationThread> threads = (List<ConversationThread>) request.getAttribute("conversationThreads");
    Integer unread = (Integer) request.getAttribute("conversationUnreadCount");
    int conversationUnreadCount = unread == null ? 0 : unread;
    int totalCount = threads == null ? 0 : threads.size();
    String activeApplicationId = (String) request.getAttribute("activeApplicationId");
    if (!hasText(activeApplicationId) && threads != null && !threads.isEmpty()) {
        activeApplicationId = threads.get(0).getApplicationId();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>MO - Conversations</title>
    <link rel="stylesheet" href="<%= contextPath %>/css/app.css" />
    <style>
        .mo-conversation-page { height:100vh; overflow:hidden; }
        .mo-conversation-page .ad-shell { width:calc(100vw - 36px); max-width:1320px; height:calc(100vh - 36px); margin:18px auto; display:grid; grid-template-rows:76px minmax(0,1fr); }
        .mo-conversation-page .ad-layout { min-height:0; height:100%; }
        .mo-conversation-page .ad-sidebar { min-height:0; overflow-y:auto; }
        .mo-conversation-page .ad-main { min-height:0; overflow:hidden; display:flex; flex-direction:column; }
        .mo-chat-shell { flex:1; min-height:0; display:grid; grid-template-columns:320px minmax(0,1fr); border:1px solid #ded4c7; border-radius:8px; overflow:hidden; background:#fffdfa; }
        .mo-chat-list { display:flex; flex-direction:column; min-width:0; min-height:0; background:#f1ece4; border-right:1px solid #ded4c7; }
        .mo-chat-list-head { display:flex; align-items:center; gap:10px; padding:14px; border-bottom:1px solid #ded4c7; }
        .mo-chat-search { flex:1; min-width:0; border:1px solid #d8cdbc; border-radius:8px; padding:10px 12px; background:#fffdfa; font-size:14px; color:#2f3742; }
        .mo-chat-search:focus { outline:none; border-color:#b98956; box-shadow:0 0 0 3px rgba(185,137,86,.16); }
        .mo-chat-count { display:inline-flex; align-items:center; justify-content:center; min-width:28px; height:28px; border-radius:999px; background:#9d7048; color:#fffdfa; font-size:12px; font-weight:700; }
        .mo-chat-thread-list { flex:1; min-height:0; overflow-y:auto; overflow-x:hidden; padding:8px 0; }
        .mo-thread { width:100%; border:0; background:transparent; display:grid; grid-template-columns:46px minmax(0,1fr) auto; gap:10px; padding:12px 14px; text-align:left; cursor:pointer; border-bottom:1px solid rgba(222,212,199,.76); color:#2f3742; }
        .mo-thread:hover { background:#f6f0e2; }
        .mo-thread.active { background:#e8ddd0; color:#2f3742; box-shadow:inset 4px 0 0 #9d7048; }
        .thread-avatar { width:44px; height:44px; border-radius:8px; display:flex; align-items:center; justify-content:center; background:#f6f0e2; color:#71543a; font-weight:800; flex:0 0 auto; position:relative; border:1px solid #ded4c7; }
        .mo-thread.active .thread-avatar { background:#fffdfa; color:#7a542e; border-color:#cbb9a3; }
        .thread-dot { position:absolute; top:-4px; right:-4px; width:12px; height:12px; border-radius:50%; background:#c45a44; border:2px solid #f1ece4; }
        .mo-thread.active .thread-dot { border-color:#e8ddd0; }
        .thread-main { min-width:0; display:grid; gap:4px; }
        .thread-title { display:flex; gap:6px; align-items:center; min-width:0; }
        .thread-title strong { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; font-size:15px; }
        .thread-title small { flex:0 0 auto; font-size:11px; opacity:.72; }
        .thread-preview { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; font-size:13px; color:#7c7469; }
        .thread-meta { display:grid; justify-items:end; gap:6px; font-size:12px; color:#8a8175; white-space:nowrap; }
        .mo-thread.active .thread-preview, .mo-thread.active .thread-meta { color:#6f6559; }
        .thread-new { display:inline-flex; align-items:center; justify-content:center; min-width:18px; height:18px; padding:0 6px; border-radius:999px; background:#c45a44; color:#fffdfa; font-size:11px; font-weight:700; }
        .mo-chat-panel { min-width:0; min-height:0; display:grid; background:#f8f6f2; overflow:hidden; }
        .chat-empty { flex:1; display:flex; align-items:center; justify-content:center; color:#8a9099; }
        .chat-window { display:none; min-height:0; height:100%; overflow:hidden; grid-template-rows:auto minmax(0,1fr) auto; }
        .chat-window.active { display:grid; }
        .chat-header { min-height:0; display:flex; justify-content:space-between; align-items:center; gap:12px; padding:16px 22px; border-bottom:1px solid #ded4c7; background:#fffdfa; }
        .chat-header h2 { margin:0; font-size:20px; color:#111827; }
        .chat-subtitle { margin-top:5px; color:#69707a; font-size:13px; }
        .chat-actions { display:flex; gap:8px; align-items:center; flex-wrap:wrap; justify-content:flex-end; }
        .badge { display:inline-flex; align-items:center; padding:4px 8px; border-radius:12px; font-size:12px; font-weight:700; }
        .badge-pending { background:#fef3c7; color:#92400e; }
        .badge-approved { background:#e6ead9; color:#4f6c4d; }
        .badge-rejected { background:#fee2e2; color:#991b1b; }
        .badge-interview { background:#eadcb6; color:#6b4e20; }
        .badge-new { background:#fff1d6; color:#8a4b0f; }
        .chat-messages { min-height:0; overflow:auto; padding:22px; display:flex; flex-direction:column; gap:14px; background:#f8f6f2; }
        .message-row { display:flex; align-items:flex-start; gap:10px; }
        .message-row--mo { justify-content:flex-end; }
        .message-avatar { width:36px; height:36px; border-radius:8px; display:flex; align-items:center; justify-content:center; background:#eee7dc; color:#6b5346; font-size:12px; font-weight:800; flex:0 0 auto; }
        .message-row--mo .message-avatar { order:2; background:#eadcb6; color:#6b4e20; }
        .message-stack { max-width:min(620px,72%); display:grid; gap:5px; }
        .message-row--mo .message-stack { justify-items:end; }
        .message-bubble { border-radius:8px; padding:10px 12px; background:#fffdfa; border:1px solid #ded4c7; box-shadow:0 1px 2px rgba(83,68,51,.05); }
        .message-row--mo .message-bubble { background:#eadcb6; border-color:#d8c48d; }
        .message-meta { color:#858b94; font-size:12px; }
        .message-content { white-space:pre-wrap; color:#1f2937; line-height:1.5; word-break:break-word; }
        .chat-reply { border-top:1px solid #ded4c7; background:#fffdfa; padding:10px 12px 12px; position:relative; z-index:2; }
        .reply-composer { border:1px solid #d8cdbc; border-radius:10px; background:#fffdfa; box-shadow:0 1px 3px rgba(83,68,51,.06); overflow:hidden; }
        .reply-box { display:grid; grid-template-rows:auto auto; gap:8px; padding:0 10px 10px; }
        .reply-box textarea { min-height:74px; max-height:128px; resize:none; box-sizing:border-box; width:100%; border:0; border-radius:0; padding:12px 2px 8px; font:inherit; background:transparent; color:#2f3742; line-height:1.45; }
        .reply-box textarea:focus { outline:none; }
        .reply-actions { display:flex; justify-content:flex-end; align-items:center; gap:10px; min-height:34px; }
        .reply-send { min-width:76px; border-radius:8px; padding:8px 16px; }
        .reply-send:disabled { opacity:.55; cursor:not-allowed; }
        @media (max-width: 900px) {
            .mo-conversation-page { height:auto; overflow:auto; }
            .mo-conversation-page .ad-shell { width:auto; height:auto; display:block; }
            .mo-conversation-page .ad-layout { height:auto; }
            .mo-conversation-page .ad-main { overflow:visible; display:block; }
            .mo-chat-shell { grid-template-columns:1fr; height:auto; min-height:0; }
            .mo-chat-list { max-height:320px; border-right:0; border-bottom:1px solid #e2e5ea; }
            .chat-window.active { min-height:560px; grid-template-rows:auto minmax(280px,1fr) auto; }
        }
    </style>
</head>
<body class="ad-page mo-conversation-page">
<div class="ad-shell ta-shell">
    <header class="ad-topbar">
        <div class="brand-group">
            <div class="brand-icon">MO</div>
            <div>
                <div class="brand-title">Conversation</div>
                <div class="brand-subtitle"><span id="mo-unread-count"><%= conversationUnreadCount %></span> unread / <%= totalCount %> total</div>
            </div>
        </div>
        <div class="top-actions">
            <a class="chip-button" href="<%= contextPath %>/logout">Sign out</a>
            <div class="user-pill">
                <span class="avatar"><%= esc(currentAvatarText) %></span>
                <span><strong><%= esc(userName) %></strong><small><%= esc(userId) %></small></span>
            </div>
        </div>
    </header>

    <div class="ad-layout ta-layout">
        <aside class="ad-sidebar ta-sidebar">
            <section class="side-card profile-card">
                <span class="role-tag">MO</span>
                <h3>Module Organiser</h3>
                <p>Signed in as <strong><%= esc(userId) %></strong></p>
            </section>
            <section class="side-block">
                <p class="side-title">NAVIGATION</p>
                <a class="nav-item" href="<%= contextPath %>/mo/home">
                    <span class="nav-icon">HM</span><span><strong>Home</strong><small>Overview</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/mo/positions">
                    <span class="nav-icon">PO</span><span><strong>Positions</strong><small>Manage jobs</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/mo/publish">
                    <span class="nav-icon">PU</span><span><strong>Publish</strong><small>Post position</small></span>
                </a>
                <a class="nav-item" href="<%= contextPath %>/mo/applications">
                    <span class="nav-icon">AP</span><span><strong>Applications</strong><small>All applications</small></span>
                </a>
                <span class="nav-item active">
                    <span class="nav-icon">CN</span>
                    <span><strong>Conversation</strong><small id="mo-nav-conversation-count"><%= conversationUnreadCount > 0 ? conversationUnreadCount + " new" : "TA messages" %></small></span>
                </span>
            </section>
        </aside>

        <main class="ad-main">
            <%
                String suc = (String) session.getAttribute("moActionSuccess");
                String err = (String) session.getAttribute("moActionError");
                if (suc != null) {
            %>
                <section class="list-card ta-flash ta-flash--success"><p style="margin:0;color:#4f6c4d;"><%= esc(suc) %></p></section>
            <%
                    session.removeAttribute("moActionSuccess");
                }
                if (err != null) {
            %>
                <section class="list-card ta-flash ta-flash--warn"><p style="margin:0;color:#6b5346;"><%= esc(err) %></p></section>
            <%
                    session.removeAttribute("moActionError");
                }
            %>

            <% if (threads == null || threads.isEmpty()) { %>
                <section class="list-card"><p class="ta-empty-hint">No TA messages yet.</p></section>
            <% } else { %>
                <section class="mo-chat-shell">
                    <div class="mo-chat-list">
                        <div class="mo-chat-list-head">
                            <input id="conversation-search" class="mo-chat-search" type="search" placeholder="Search" autocomplete="off" />
                            <% if (conversationUnreadCount > 0) { %><span id="mo-chat-count" class="mo-chat-count"><%= conversationUnreadCount %></span><% } %>
                        </div>
                        <div class="mo-chat-thread-list">
                            <% for (int i = 0; i < threads.size(); i++) {
                                ConversationThread thread = threads.get(i);
                                ConversationMessage latest = thread.getLatestMessage();
                                String latestContent = latest == null ? "No conversation content." : latest.getContent();
                                String latestSender = latest == null ? "" : (latest.isFromMO() ? "MO: " : "TA: ");
                                String latestTime = latest == null ? thread.getApplyTime() : latest.getSentAt();
                                boolean isActive = hasText(activeApplicationId) ? activeApplicationId.equals(thread.getApplicationId()) : i == 0;
                                String activeClass = isActive ? " active" : "";
                                String needsReplyValue = thread.isNeedsMoReply() ? "true" : "false";
                            %>
                                <button type="button"
                                        class="mo-thread<%= activeClass %>"
                                        data-thread-id="thread-<%= i %>"
                                        data-application-id="<%= esc(thread.getApplicationId()) %>"
                                        data-needs-reply="<%= needsReplyValue %>"
                                        data-search="<%= esc((thread.getModuleName() + " " + thread.getTaId() + " " + latestContent).toLowerCase()) %>">
                                    <span class="thread-avatar">
                                        <%= esc(avatarText(thread.getTaId())) %>
                                        <% if (thread.isNeedsMoReply()) { %><span class="thread-dot unread-marker"></span><% } %>
                                    </span>
                                    <span class="thread-main">
                                        <span class="thread-title">
                                            <strong><%= show(thread.getTaId()) %></strong>
                                            <small><%= show(thread.getApplicationType()) %></small>
                                        </span>
                                        <span class="thread-preview"><%= esc(latestSender) %><%= shortText(latestContent, 42) %></span>
                                    </span>
                                    <span class="thread-meta">
                                        <span><%= shortText(latestTime, 12) %></span>
                                        <% if (thread.isNeedsMoReply()) { %><span class="thread-new unread-marker">new</span><% } %>
                                    </span>
                                </button>
                            <% } %>
                        </div>
                    </div>

                    <div class="mo-chat-panel">
                        <% for (int i = 0; i < threads.size(); i++) {
                            ConversationThread thread = threads.get(i);
                            boolean isActive = hasText(activeApplicationId) ? activeApplicationId.equals(thread.getApplicationId()) : i == 0;
                            String activeClass = isActive ? " active" : "";
                        %>
                            <section class="chat-window<%= activeClass %>" id="thread-<%= i %>" data-application-id="<%= esc(thread.getApplicationId()) %>">
                                <div class="chat-header">
                                    <div>
                                        <h2><%= show(thread.getTaId()) %></h2>
                                        <div class="chat-subtitle">
                                            <%= show(thread.getModuleName()) %> - Application <%= show(thread.getApplicationId()) %>
                                        </div>
                                    </div>
                                    <div class="chat-actions">
                                        <% if (thread.isNeedsMoReply()) { %><span class="badge badge-new unread-marker">New TA message</span><% } %>
                                        <span class="badge <%= badgeClass(thread.getStatus()) %>"><%= show(thread.getStatus()) %></span>
                                        <a class="chip-button" href="<%= contextPath %>/mo/view/application?applicationId=<%= esc(thread.getApplicationId()) %>">Open Application</a>
                                    </div>
                                </div>

                                <div class="chat-messages">
                                    <% if (thread.getMessages().isEmpty()) { %>
                                        <div class="chat-empty">No conversation content.</div>
                                    <% } else {
                                        for (ConversationMessage message : thread.getMessages()) {
                                            boolean fromMO = message.isFromMO();
                                    %>
                                        <div class="message-row<%= fromMO ? " message-row--mo" : "" %>">
                                            <div class="message-avatar"><%= fromMO ? "MO" : esc(avatarText(thread.getTaId())) %></div>
                                            <div class="message-stack">
                                                <div class="message-meta"><%= fromMO ? "MO" : "TA" %> - <%= show(message.getSentAt()) %></div>
                                                <div class="message-bubble">
                                                    <div class="message-content"><%= show(message.getContent()) %></div>
                                                </div>
                                            </div>
                                        </div>
                                    <%  }
                                       } %>
                                </div>

                                <form class="chat-reply" method="post" action="<%= contextPath %>/mo/application/reply">
                                    <input type="hidden" name="applicationId" value="<%= esc(thread.getApplicationId()) %>" />
                                    <input type="hidden" name="redirect" value="conversations" />
                                    <div class="reply-composer">
                                        <div class="reply-box">
                                            <textarea name="message" placeholder="Reply to this TA..." required></textarea>
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
        var readUrl = '<%= contextPath %>/mo/conversations/read';
        var unreadCount = <%= conversationUnreadCount %>;
        var buttons = Array.prototype.slice.call(document.querySelectorAll('.mo-thread'));
        var windows = Array.prototype.slice.call(document.querySelectorAll('.chat-window'));
        var search = document.getElementById('conversation-search');

        function nowText() {
            var d = new Date();
            var pad = function (n) { return n < 10 ? '0' + n : String(n); };
            return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());
        }

        function parseJsonResponse(res, options) {
            options = options || {};
            var contentType = res.headers.get('content-type') || '';
            if (contentType.indexOf('application/json') >= 0) {
                return res.json().then(function (data) {
                    if (!res.ok) throw new Error(data && data.error ? data.error : 'Request failed');
                    return data;
                });
            }
            return res.text().then(function () {
                var finalUrl = res.url || '';
                if (res.status === 401 || finalUrl.indexOf('/login') >= 0) {
                    throw new Error('Login expired. Please sign in again.');
                }
                if (res.status === 404) {
                    throw new Error('Conversation endpoint is not loaded. Please restart Tomcat and refresh this page.');
                }
                if (res.redirected && options.allowRedirectSuccess) {
                    return { success: true, sentAt: nowText() };
                }
                if (res.redirected) {
                    throw new Error('Server returned a redirect page instead of JSON. Please restart Tomcat and refresh this page.');
                }
                throw new Error('Server returned HTML instead of JSON. Please restart Tomcat and refresh this page.');
            });
        }

        function setUnreadCount(count) {
            unreadCount = Math.max(0, Number(count) || 0);
            var headerCount = document.getElementById('mo-unread-count');
            var navCount = document.getElementById('mo-nav-conversation-count');
            var chatCount = document.getElementById('mo-chat-count');
            if (headerCount) headerCount.textContent = String(unreadCount);
            if (navCount) navCount.textContent = unreadCount > 0 ? unreadCount + ' new' : 'TA messages';
            if (chatCount) {
                if (unreadCount > 0) {
                    chatCount.textContent = String(unreadCount);
                    chatCount.style.display = 'inline-flex';
                } else {
                    chatCount.style.display = 'none';
                }
            }
        }

        function clearUnread(btn, panel, nextCount) {
            if (btn && btn.getAttribute('data-needs-reply') === 'true') {
                btn.setAttribute('data-needs-reply', 'false');
                btn.querySelectorAll('.unread-marker').forEach(function (node) { node.remove(); });
                if (panel) panel.querySelectorAll('.unread-marker').forEach(function (node) { node.remove(); });
                setUnreadCount(typeof nextCount === 'number' ? nextCount : unreadCount - 1);
            }
        }

        function markRead(btn, panel) {
            if (!btn || btn.getAttribute('data-needs-reply') !== 'true') return;
            var appId = btn.getAttribute('data-application-id');
            clearUnread(btn, panel);

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
            }).then(function (res) {
                return parseJsonResponse(res);
            }).then(function (data) {
                if (data && typeof data.unreadCount === 'number') {
                    setUnreadCount(data.unreadCount);
                }
            }).catch(function (err) {
                if (window.console) console.warn(err.message || err);
            });
        }

        function activate(id) {
            var activeButton = null;
            var activePanel = null;
            buttons.forEach(function (btn) {
                var active = btn.getAttribute('data-thread-id') === id;
                btn.classList.toggle('active', active);
                if (active) activeButton = btn;
            });
            windows.forEach(function (panel) {
                var active = panel.id === id;
                panel.classList.toggle('active', active);
                if (active) {
                    activePanel = panel;
                    var messages = panel.querySelector('.chat-messages');
                    if (messages) messages.scrollTop = messages.scrollHeight;
                }
            });
            if (activeButton && activeButton.scrollIntoView) {
                activeButton.scrollIntoView({ block: 'nearest' });
            }
            if (activeButton && window.history && window.URL) {
                var appId = activeButton.getAttribute('data-application-id');
                var url = new URL(window.location.href);
                url.searchParams.set('applicationId', appId);
                window.history.replaceState(null, '', url.toString());
            }
            markRead(activeButton, activePanel);
        }

        buttons.forEach(function (btn) {
            btn.addEventListener('click', function () {
                activate(btn.getAttribute('data-thread-id'));
            });
        });

        if (search) {
            search.addEventListener('input', function () {
                var term = search.value.trim().toLowerCase();
                var firstVisible = null;
                buttons.forEach(function (btn) {
                    var match = !term || (btn.getAttribute('data-search') || '').indexOf(term) >= 0;
                    btn.style.display = match ? 'grid' : 'none';
                    if (match && firstVisible === null) firstVisible = btn;
                });
                if (firstVisible && !firstVisible.classList.contains('active')) {
                    activate(firstVisible.getAttribute('data-thread-id'));
                }
            });
        }

        function appendMoMessage(panel, text, sentAt) {
            var messages = panel.querySelector('.chat-messages');
            if (!messages) return;
            var empty = messages.querySelector('.chat-empty');
            if (empty) empty.remove();

            var row = document.createElement('div');
            row.className = 'message-row message-row--mo';
            var avatar = document.createElement('div');
            avatar.className = 'message-avatar';
            avatar.textContent = 'MO';
            var stack = document.createElement('div');
            stack.className = 'message-stack';
            var meta = document.createElement('div');
            meta.className = 'message-meta';
            meta.textContent = 'MO - ' + (sentAt || '');
            var bubble = document.createElement('div');
            bubble.className = 'message-bubble';
            var content = document.createElement('div');
            content.className = 'message-content';
            content.textContent = text;
            bubble.appendChild(content);
            stack.appendChild(meta);
            stack.appendChild(bubble);
            row.appendChild(avatar);
            row.appendChild(stack);
            messages.appendChild(row);
            messages.scrollTop = messages.scrollHeight;
        }

        function updateThreadPreview(btn, text, sentAt) {
            if (!btn) return;
            var preview = btn.querySelector('.thread-preview');
            var time = btn.querySelector('.thread-meta span:first-child');
            if (preview) preview.textContent = 'MO: ' + text;
            if (time && sentAt) time.textContent = sentAt.length > 12 ? sentAt.substring(0, 12) : sentAt;
        }

        document.querySelectorAll('.chat-reply').forEach(function (form) {
            var draft = form.querySelector('textarea[name="message"]');
            var sendButton = form.querySelector('button[type="submit"]');
            function syncSendState() {
                if (sendButton && draft) sendButton.disabled = draft.value.trim().length === 0;
            }
            if (draft) {
                draft.addEventListener('input', syncSendState);
                draft.addEventListener('keydown', function (event) {
                    if (event.key === 'Enter' && (event.ctrlKey || event.metaKey)) {
                        event.preventDefault();
                        if (draft.value.trim()) form.requestSubmit();
                    }
                });
                syncSendState();
            }

            form.addEventListener('submit', function (event) {
                event.preventDefault();
                var panel = form.closest('.chat-window');
                var btn = panel ? document.querySelector('.mo-thread[data-thread-id="' + panel.id + '"]') : null;
                var textarea = form.querySelector('textarea[name="message"]');
                var submit = form.querySelector('button[type="submit"]');
                var message = textarea ? textarea.value.trim() : '';
                if (!message) return;

                var body = new URLSearchParams();
                Array.prototype.slice.call(form.elements).forEach(function (field) {
                    if (field.name) body.append(field.name, field.value);
                });

                if (submit) submit.disabled = true;
                fetch(form.action, {
                    method: 'POST',
                    credentials: 'same-origin',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: body.toString()
                }).then(function (res) {
                    return parseJsonResponse(res, { allowRedirectSuccess: true });
                }).then(function (data) {
                    var sentAt = data.sentAt || '';
                    appendMoMessage(panel, message, sentAt);
                    updateThreadPreview(btn, message, sentAt);
                    clearUnread(btn, panel);
                    if (textarea) textarea.value = '';
                    syncSendState();
                }).catch(function (err) {
                    alert(err.message || 'Failed to send message');
                }).finally(function () {
                    syncSendState();
                });
            });
        });

        if (buttons.length) activate(buttons[0].getAttribute('data-thread-id'));
    })();
</script>
</body>
</html>
