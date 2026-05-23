/**
 * ta-conv-widget.js
 * Inline conversation panel for TA pages other than Home.
 * Reads application data from sessionStorage (stored by ta-home.js on Home page).
 * Shows the same split-pane UI as home.jsp, inline inside <main>.
 */
(function () {
    'use strict';

    var PANEL_ID = 'ta-conv-widget-panel';
    var WRAPPER_ID = 'ta-conv-widget-wrapper';
    var STORAGE_KEY = 'taAppSeen_v1';

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function loadSeen() {
        try { return JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}'); } catch (e) { return {}; }
    }

    function saveSeen(obj) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(obj));
    }

    function splitStatement(full) {
        if (!full) return { base: '', replies: [] };
        var idx = full.indexOf('\n[');
        var base = (idx >= 0 ? full.substring(0, idx) : full).trim();
        var tail = idx >= 0 ? full.substring(idx) : '';
        var replies = [];
        tail.split('\n').forEach(function (line) {
            var t = line.trim();
            if (t) replies.push(t);
        });
        return { base: base, replies: replies };
    }

    function hasUpdate(app, seen) {
        var id = app.applicationId;
        if (!id) return false;
        var prev = seen[id];
        var st = app.status || '';
        var fb = app.feedback || '';
        var parts = splitStatement(app.statement || '');
        var lastLine = parts.replies.length > 0 ? parts.replies[parts.replies.length - 1] : '';
        var hasMoLast = / MO\]:/.test(lastLine);
        if (!prev) {
            return fb.trim().length > 0 || (st && st !== 'PENDING') || hasMoLast;
        }
        return prev.status !== st || (prev.feedback || '') !== fb ||
               (hasMoLast && prev.lastLine !== lastLine);
    }

    function markSeen(app) {
        var seen = loadSeen();
        var parts = splitStatement(app.statement || '');
        var lastLine = parts.replies.length > 0 ? parts.replies[parts.replies.length - 1] : '';
        seen[app.applicationId] = {
            status: app.status || '',
            feedback: app.feedback || '',
            lastLine: lastLine
        };
        saveSeen(seen);
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function normalizeApproveType(applicationType) {
        var raw = (applicationType || '').toString().trim().toUpperCase();
        if (!raw) return '';
        if (raw.indexOf('NON') >= 0 && raw.indexOf('LEADER') >= 0) return 'Non-leader approve';
        if (raw.indexOf('LEADER') >= 0) return 'Leader approve';
        if (raw === 'NL') return 'Non-leader approve';
        if (raw === 'L') return 'Leader approve';
        return applicationType;
    }

    function moduleAvatarText(app) {
        var id = (app.jobId || app.applicationId || 'XX').trim();
        return id.length <= 2 ? id.toUpperCase() : id.substring(id.length - 2).toUpperCase();
    }

    function parseReplyLine(line) {
        var m = /^\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}) (TA|MO)\]:\s*(.*)$/.exec(line.trim());
        if (m) return { timestamp: m[1], sender: m[2], text: m[3] };
        return { timestamp: '', sender: 'TA', text: line };
    }

    function latestMsgPreview(app) {
        var parts = splitStatement(app.statement || '');
        var prefix, text;
        if (parts.replies.length > 0) {
            var last = parts.replies[parts.replies.length - 1];
            var parsed = parseReplyLine(last);
            prefix = parsed.sender === 'MO' ? 'MO: ' : 'TA: ';
            text = parsed.text.replace(/\r?\n/g, ' ').trim();
        } else {
            prefix = 'TA: ';
            text = (parts.base || '').replace(/\r?\n/g, ' ').trim();
        }
        return prefix + (text.length > 42 ? text.substring(0, 39) + '...' : text);
    }

    function latestMsgDate(app) {
        var parts = splitStatement(app.statement || '');
        if (parts.replies.length > 0) {
            var last = parts.replies[parts.replies.length - 1];
            var parsed = parseReplyLine(last);
            if (parsed.timestamp) return parsed.timestamp.substring(0, 10);
        }
        return app.applyTime ? String(app.applyTime).substring(0, 10) : '';
    }

    function badgeClass(status) {
        var m = { 'APPROVED': 'badge-approved', 'REJECTED': 'badge-rejected', 'INTERVIEW': 'badge-interview', 'PENDING': 'badge-pending' };
        return 'badge ' + (m[(status || '').toUpperCase()] || 'badge-new');
    }

    function updateUnreadCount() {
        var seen = loadSeen();
        var apps = window.__TA_WIDGET_APPS__ || [];
        var count = 0;
        apps.forEach(function (app) { if (hasUpdate(app, seen)) count++; });
        var badge = document.getElementById('ta-conv-widget-unread');
        if (badge) {
            badge.textContent = String(count);
            badge.style.display = count > 0 ? 'inline-flex' : 'none';
        }
    }

    function addMsgRow(host, isMOSender, abbr, label, text) {
        var row = document.createElement('div');
        row.className = 'message-row' + (isMOSender ? '' : ' message-row--mo');
        var avatar = document.createElement('div');
        avatar.className = 'message-avatar';
        avatar.textContent = abbr;
        var stack = document.createElement('div');
        stack.className = 'message-stack';
        var meta = document.createElement('div');
        meta.className = 'message-meta';
        meta.textContent = label;
        var bubble = document.createElement('div');
        bubble.className = 'message-bubble';
        var content = document.createElement('div');
        content.className = 'message-content';
        content.textContent = text || '(empty)';
        bubble.appendChild(content);
        stack.appendChild(meta);
        stack.appendChild(bubble);
        row.appendChild(avatar);
        row.appendChild(stack);
        host.appendChild(row);
    }

    function renderChatWindow(app) {
        var panel = document.getElementById('ta-conv-widget-chat-panel');
        var empty = document.getElementById('ta-conv-widget-empty');

        var winId = 'ta-wgt-win-' + app.applicationId;
        var existing = document.getElementById(winId);
        if (existing) {
            Array.prototype.forEach.call(panel.querySelectorAll('.chat-window'), function (w) {
                w.classList.remove('active');
            });
            existing.classList.add('active');
            if (empty) empty.style.display = 'none';
            var m = existing.querySelector('.chat-messages');
            if (m) m.scrollTop = m.scrollHeight;
            return;
        }

        if (empty) empty.style.display = 'none';
        Array.prototype.forEach.call(panel.querySelectorAll('.chat-window'), function (w) {
            w.classList.remove('active');
        });

        var parts = splitStatement(app.statement || '');
        var jobAbbr = moduleAvatarText(app);

        var win = document.createElement('section');
        win.className = 'chat-window active';
        win.id = winId;

        /* Header */
        var hdr = document.createElement('div');
        hdr.className = 'chat-header';
        var hdrLeft = document.createElement('div');
        var h2 = document.createElement('h2');
        h2.textContent = app.moduleName || '\u2014';
        var sub = document.createElement('div');
        sub.className = 'chat-subtitle';
        sub.textContent = (app.jobId || '') + ' - Application ' + (app.applicationId || '');
        hdrLeft.appendChild(h2);
        hdrLeft.appendChild(sub);
        var hdrRight = document.createElement('div');
        hdrRight.className = 'chat-actions';
        var statusBadge = document.createElement('span');
        statusBadge.className = badgeClass(app.status);
        statusBadge.textContent = (app.status || 'PENDING').toUpperCase();
        hdrRight.appendChild(statusBadge);
        hdr.appendChild(hdrLeft);
        hdr.appendChild(hdrRight);
        win.appendChild(hdr);

        /* Messages */
        var msgs = document.createElement('div');
        msgs.className = 'chat-messages';
        addMsgRow(msgs, false, jobAbbr, 'TA - (initial statement)', parts.base || '(no statement)');
        parts.replies.forEach(function (line) {
            var parsed = parseReplyLine(line);
            var isMO = parsed.sender === 'MO';
            var abbr = isMO ? 'MO' : jobAbbr;
            var label = (isMO ? 'MO' : 'TA') + (parsed.timestamp ? ' - ' + parsed.timestamp : '');
            addMsgRow(msgs, isMO, abbr, label, parsed.text || line);
        });
        win.appendChild(msgs);

        /* Reply area */
        var chatReply = document.createElement('div');
        chatReply.className = 'chat-reply';
        var composer = document.createElement('div');
        composer.className = 'reply-composer';
        var replyBox = document.createElement('div');
        replyBox.className = 'reply-box';
        var textarea = document.createElement('textarea');
        textarea.name = 'message';
        textarea.placeholder = 'Reply to module organiser\u2026';
        textarea.maxLength = 500;
        var replyActions = document.createElement('div');
        replyActions.className = 'reply-actions';
        var sendBtn = document.createElement('button');
        sendBtn.type = 'button';
        sendBtn.className = 'chip-button active reply-send';
        sendBtn.textContent = 'Send';
        replyActions.appendChild(sendBtn);
        replyBox.appendChild(textarea);
        replyBox.appendChild(replyActions);
        composer.appendChild(replyBox);
        chatReply.appendChild(composer);
        win.appendChild(chatReply);
        panel.appendChild(win);
        msgs.scrollTop = msgs.scrollHeight;

        /* Reply handler */
        var applicationId = app.applicationId;
        textarea.addEventListener('keydown', function (ev) {
            if (ev.key === 'Enter' && !ev.shiftKey && !ev.isComposing) {
                ev.preventDefault();
                if ((textarea.value || '').trim() && !sendBtn.disabled) sendBtn.click();
            }
        });
        sendBtn.addEventListener('click', function () {
            var message = (textarea.value || '').trim();
            if (!message || !applicationId) return;

            var formData = new URLSearchParams();
            formData.append('applicationId', applicationId);
            formData.append('message', message);

            sendBtn.disabled = true;
            sendBtn.textContent = 'Sending\u2026';

            fetch(contextPath() + '/ta/application/reply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            }).then(function (resp) {
                if (!resp.ok) throw new Error('Failed');
                return resp.json();
            }).then(function () {
                var now = new Date();
                var pad = function (n) { return n < 10 ? '0' + n : String(n); };
                var ts = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) +
                         ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes());
                addMsgRow(msgs, false, jobAbbr, 'TA - ' + ts, message);
                msgs.scrollTop = msgs.scrollHeight;
                textarea.value = '';
                var allApps = window.__TA_WIDGET_APPS__ || [];
                for (var i = 0; i < allApps.length; i++) {
                    if (allApps[i].applicationId === applicationId) {
                        allApps[i].statement = (allApps[i].statement || '') + '\n[' + ts + ' TA]: ' + message;
                        try { sessionStorage.setItem('ta_apps', JSON.stringify(allApps)); } catch (e) {}
                        var threadBtn = document.querySelector('#ta-conv-widget-thread-list .mo-thread[data-app-id="' + applicationId + '"]');
                        if (threadBtn) {
                            var prev = threadBtn.querySelector('.thread-preview');
                            if (prev) prev.textContent = 'TA: ' + message.substring(0, 42);
                        }
                        break;
                    }
                }
            }).catch(function () {
                alert('Failed to send reply. Please try again.');
            }).finally(function () {
                sendBtn.disabled = false;
                sendBtn.textContent = 'Send';
            });
        });
    }

    function renderThreadList(apps, activeAppId) {
        var host = document.getElementById('ta-conv-widget-thread-list');
        if (!host) return;
        clearEl(host);

        if (!apps || apps.length === 0) {
            var msg = document.createElement('p');
            msg.style.cssText = 'padding:14px;color:#8a9099;font-size:13px;margin:0;';
            msg.textContent = 'No applications. Visit Home page first.';
            host.appendChild(msg);
            return;
        }

        var seen = loadSeen();
        apps.forEach(function (app) {
            var isActive = app.applicationId === activeAppId;
            var hasUnread = hasUpdate(app, seen);

            var btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'mo-thread' + (isActive ? ' active' : '');
            btn.setAttribute('data-app-id', app.applicationId || '');
            btn.setAttribute('data-needs-reply', hasUnread ? 'true' : 'false');
            var searchText = ((app.moduleName || '') + ' ' + (app.jobId || '') + ' ' + latestMsgPreview(app)).toLowerCase();
            btn.setAttribute('data-search', searchText);

            var avatarSpan = document.createElement('span');
            avatarSpan.className = 'thread-avatar';
            avatarSpan.textContent = moduleAvatarText(app);
            if (hasUnread) {
                var dot = document.createElement('span');
                dot.className = 'thread-dot unread-marker';
                avatarSpan.appendChild(dot);
            }

            var main = document.createElement('span');
            main.className = 'thread-main';
            var titleRow = document.createElement('span');
            titleRow.className = 'thread-title';
            var titleStrong = document.createElement('strong');
            titleStrong.textContent = app.moduleName || '\u2014';
            var titleSmall = document.createElement('small');
            titleSmall.textContent = normalizeApproveType(app.applicationType) || (app.applicationType || '');
            titleRow.appendChild(titleStrong);
            titleRow.appendChild(titleSmall);
            var previewSpan = document.createElement('span');
            previewSpan.className = 'thread-preview';
            previewSpan.textContent = latestMsgPreview(app);
            main.appendChild(titleRow);
            main.appendChild(previewSpan);

            var metaSpan = document.createElement('span');
            metaSpan.className = 'thread-meta';
            var dateSpan = document.createElement('span');
            dateSpan.textContent = latestMsgDate(app);
            metaSpan.appendChild(dateSpan);
            if (hasUnread) {
                var newBadge = document.createElement('span');
                newBadge.className = 'thread-new unread-marker';
                newBadge.textContent = 'new';
                metaSpan.appendChild(newBadge);
            }

            btn.appendChild(avatarSpan);
            btn.appendChild(main);
            btn.appendChild(metaSpan);
            host.appendChild(btn);

            btn.addEventListener('click', function () {
                Array.prototype.forEach.call(host.querySelectorAll('.mo-thread'), function (b) {
                    b.classList.remove('active');
                });
                btn.classList.add('active');
                renderChatWindow(app);
                markSeen(app);
                Array.prototype.forEach.call(btn.querySelectorAll('.unread-marker'), function (el) { el.parentNode.removeChild(el); });
                btn.setAttribute('data-needs-reply', 'false');
                updateUnreadCount();
                try { sessionStorage.setItem('ta_active_app', JSON.stringify(app)); } catch (e) {}
            });
        });
    }

    function injectInlinePanel() {
        if (document.getElementById(PANEL_ID)) return;

        var main = document.querySelector('.ad-main');
        if (!main) return;

        /* Wrap existing main children */
        var wrapper = document.createElement('div');
        wrapper.id = WRAPPER_ID;
        while (main.firstChild) wrapper.appendChild(main.firstChild);
        main.appendChild(wrapper);

        /* Create inline panel */
        var panel = document.createElement('div');
        panel.id = PANEL_ID;
        panel.className = 'conv-inline-panel';

        var topbar = document.createElement('div');
        topbar.className = 'ta-conv-topbar';
        var title = document.createElement('h2');
        title.textContent = 'Conversation';
        var backBtn = document.createElement('button');
        backBtn.type = 'button';
        backBtn.className = 'chip-button';
        backBtn.id = 'ta-conv-widget-back';
        backBtn.textContent = '\u2190 Back';
        topbar.appendChild(title);
        topbar.appendChild(backBtn);

        var shell = document.createElement('div');
        shell.className = 'mo-chat-shell';

        var chatList = document.createElement('div');
        chatList.className = 'mo-chat-list';
        var listHead = document.createElement('div');
        listHead.className = 'mo-chat-list-head';
        var searchInput = document.createElement('input');
        searchInput.className = 'mo-chat-search';
        searchInput.id = 'ta-conv-widget-search';
        searchInput.type = 'search';
        searchInput.placeholder = 'Search';
        searchInput.setAttribute('autocomplete', 'off');
        var unreadBadge = document.createElement('span');
        unreadBadge.className = 'mo-chat-count';
        unreadBadge.id = 'ta-conv-widget-unread';
        unreadBadge.style.display = 'none';
        listHead.appendChild(searchInput);
        listHead.appendChild(unreadBadge);
        var threadList = document.createElement('div');
        threadList.className = 'mo-chat-thread-list';
        threadList.id = 'ta-conv-widget-thread-list';
        chatList.appendChild(listHead);
        chatList.appendChild(threadList);

        var chatPanel = document.createElement('div');
        chatPanel.className = 'mo-chat-panel';
        chatPanel.id = 'ta-conv-widget-chat-panel';
        var emptyMsg = document.createElement('div');
        emptyMsg.className = 'chat-empty';
        emptyMsg.id = 'ta-conv-widget-empty';
        emptyMsg.textContent = 'Select an application to view the conversation.';
        chatPanel.appendChild(emptyMsg);

        shell.appendChild(chatList);
        shell.appendChild(chatPanel);
        panel.appendChild(topbar);
        panel.appendChild(shell);
        main.appendChild(panel);

        backBtn.addEventListener('click', closeWidget);

        searchInput.addEventListener('input', function () {
            var term = searchInput.value.trim().toLowerCase();
            Array.prototype.forEach.call(
                threadList.querySelectorAll('.mo-thread'),
                function (btn) {
                    var match = !term || (btn.getAttribute('data-search') || '').indexOf(term) >= 0;
                    btn.style.display = match ? 'grid' : 'none';
                }
            );
        });
    }

    function openWidget(app) {
        injectInlinePanel();

        /* Reset panel */
        var chatPanel = document.getElementById('ta-conv-widget-chat-panel');
        if (chatPanel) {
            Array.prototype.forEach.call(chatPanel.querySelectorAll('.chat-window'), function (w) {
                w.parentNode.removeChild(w);
            });
            var empty = document.getElementById('ta-conv-widget-empty');
            if (empty) empty.style.display = '';
        }
        var searchInput = document.getElementById('ta-conv-widget-search');
        if (searchInput) searchInput.value = '';

        var apps = window.__TA_WIDGET_APPS__ || [];
        var activeId = app ? app.applicationId : null;

        renderThreadList(apps, activeId);
        if (app) renderChatWindow(app);

        var wrapper = document.getElementById(WRAPPER_ID);
        if (wrapper) wrapper.style.display = 'none';
        var panel = document.getElementById(PANEL_ID);
        if (panel) panel.classList.add('active');
        document.body.classList.add('conv-active');

        if (app) {
            markSeen(app);
            updateUnreadCount();
        }
    }

    function closeWidget() {
        var wrapper = document.getElementById(WRAPPER_ID);
        if (wrapper) wrapper.style.display = '';
        var panel = document.getElementById(PANEL_ID);
        if (panel) panel.classList.remove('active');
        document.body.classList.remove('conv-active');
    }

    function init() {
        var apps = [];
        var activeApp = null;
        try { apps = JSON.parse(sessionStorage.getItem('ta_apps') || '[]'); } catch (e) {}
        try { activeApp = JSON.parse(sessionStorage.getItem('ta_active_app') || 'null'); } catch (e) {}
        if (!activeApp && apps.length > 0) activeApp = apps[0];
        window.__TA_WIDGET_APPS__ = apps;

        updateUnreadCount();

        var btn = document.getElementById('ta-conv-nav-btn');
        if (!btn) return;

        btn.addEventListener('click', function (e) {
            e.preventDefault();
            /* Reload from sessionStorage in case user visited Home in same tab */
            try { apps = JSON.parse(sessionStorage.getItem('ta_apps') || '[]'); } catch (e) {}
            try { activeApp = JSON.parse(sessionStorage.getItem('ta_active_app') || 'null'); } catch (e) {}
            if (!activeApp && apps.length > 0) activeApp = apps[0];
            window.__TA_WIDGET_APPS__ = apps;

            if (activeApp) {
                openWidget(activeApp);
            } else {
                /* No cached data — navigate to Home which will auto-open conversation */
                window.location.href = contextPath() + '/ta/home?conv=1';
            }
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
