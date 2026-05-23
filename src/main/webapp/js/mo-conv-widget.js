/**
 * mo-conv-widget.js
 * Self-contained inline conversation panel for MO pages.
 * Fetches conversation data from /mo/conversations?format=json.
 * Uses the same split-pane CSS classes as conversations.jsp.
 */
(function () {
    'use strict';

    var PANEL_ID = 'mo-conv-widget-panel';
    var WRAPPER_ID = 'mo-conv-widget-wrapper';

    function contextPath() {
        return window.MO_CONTEXT || '';
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function badgeClass(status) {
        var m = { 'APPROVED': 'badge-approved', 'REJECTED': 'badge-rejected', 'INTERVIEW': 'badge-interview', 'PENDING': 'badge-pending' };
        return 'badge ' + (m[(status || '').toUpperCase()] || 'badge-new');
    }

    function avatarText(taId) {
        var id = (taId || 'XX').trim();
        return id.length <= 2 ? id.toUpperCase() : id.substring(id.length - 2).toUpperCase();
    }

    function shortText(text, max) {
        var t = (text || '').replace(/\r?\n/g, ' ').trim();
        return t.length > max ? t.substring(0, max - 3) + '...' : t;
    }

    function latestMsg(thread) {
        var msgs = thread.messages || [];
        return msgs.length > 0 ? msgs[msgs.length - 1] : null;
    }

    function latestPreview(thread) {
        var m = latestMsg(thread);
        if (!m) return shortText(thread.applyTime || '', 42);
        return (m.sender === 'MO' ? 'MO: ' : 'TA: ') + shortText(m.content || '', 42);
    }

    function latestTime(thread) {
        var m = latestMsg(thread);
        var t = m ? (m.sentAt || '') : (thread.applyTime || '');
        return t.length > 12 ? t.substring(0, 12) : t;
    }

    function addMsgRow(host, fromMO, abbr, label, text) {
        var row = document.createElement('div');
        row.className = 'message-row' + (fromMO ? ' message-row--mo' : '');
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

    function renderChatWindow(thread) {
        var panel = document.getElementById('mo-conv-widget-chat-panel');
        if (!panel) return;
        var empty = document.getElementById('mo-conv-widget-empty');

        var winId = 'mo-conv-win-' + thread.applicationId;
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

        var abbr = avatarText(thread.taId);
        var win = document.createElement('section');
        win.className = 'chat-window active';
        win.id = winId;

        /* Header */
        var hdr = document.createElement('div');
        hdr.className = 'chat-header';
        var hdrLeft = document.createElement('div');
        var h2 = document.createElement('h2');
        h2.textContent = thread.taId || '\u2014';
        var sub = document.createElement('div');
        sub.className = 'chat-subtitle';
        sub.textContent = (thread.moduleName || '') + ' - Application ' + (thread.applicationId || '');
        hdrLeft.appendChild(h2);
        hdrLeft.appendChild(sub);
        var hdrRight = document.createElement('div');
        hdrRight.className = 'chat-actions';
        if (thread.needsMoReply) {
            var newBadge = document.createElement('span');
            newBadge.className = 'badge badge-new unread-marker';
            newBadge.textContent = 'New TA message';
            hdrRight.appendChild(newBadge);
        }
        var statusBadge = document.createElement('span');
        statusBadge.className = badgeClass(thread.status);
        statusBadge.textContent = (thread.status || 'PENDING').toUpperCase();
        hdrRight.appendChild(statusBadge);
        hdr.appendChild(hdrLeft);
        hdr.appendChild(hdrRight);
        win.appendChild(hdr);

        /* Messages */
        var msgs = document.createElement('div');
        msgs.className = 'chat-messages';
        var messages = thread.messages || [];
        if (messages.length === 0) {
            var emptyDiv = document.createElement('div');
            emptyDiv.className = 'chat-empty';
            emptyDiv.textContent = 'No conversation content.';
            msgs.appendChild(emptyDiv);
        } else {
            messages.forEach(function (msg) {
                var fromMO = (msg.sender || '').toUpperCase() === 'MO';
                var rowAbbr = fromMO ? 'MO' : abbr;
                var label = (fromMO ? 'MO' : 'TA') + (msg.sentAt ? ' - ' + msg.sentAt : '');
                addMsgRow(msgs, fromMO, rowAbbr, label, msg.content || '');
            });
        }
        win.appendChild(msgs);

        /* Reply */
        var chatReply = document.createElement('div');
        chatReply.className = 'chat-reply';
        var composer = document.createElement('div');
        composer.className = 'reply-composer';
        var replyBox = document.createElement('div');
        replyBox.className = 'reply-box';
        var textarea = document.createElement('textarea');
        textarea.name = 'message';
        textarea.placeholder = 'Reply to this TA\u2026';
        textarea.maxLength = 500;
        var replyActions = document.createElement('div');
        replyActions.className = 'reply-actions';
        var sendBtn = document.createElement('button');
        sendBtn.type = 'button';
        sendBtn.className = 'chip-button active reply-send';
        sendBtn.textContent = 'Send';
        sendBtn.disabled = true;
        replyActions.appendChild(sendBtn);
        replyBox.appendChild(textarea);
        replyBox.appendChild(replyActions);
        composer.appendChild(replyBox);
        chatReply.appendChild(composer);
        win.appendChild(chatReply);

        panel.appendChild(win);
        msgs.scrollTop = msgs.scrollHeight;

        textarea.addEventListener('input', function () {
            sendBtn.disabled = textarea.value.trim().length === 0;
        });
        textarea.addEventListener('keydown', function (ev) {
            if (ev.key === 'Enter' && !ev.shiftKey && !ev.isComposing) {
                ev.preventDefault();
                if (textarea.value.trim()) sendBtn.click();
            }
        });

        var applicationId = thread.applicationId;
        sendBtn.addEventListener('click', function () {
            var message = (textarea.value || '').trim();
            if (!message) return;

            var body = new URLSearchParams();
            body.append('applicationId', applicationId);
            body.append('message', message);

            sendBtn.disabled = true;
            fetch(contextPath() + '/mo/application/reply', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: body.toString()
            }).then(function (res) {
                if (!res.ok) throw new Error('Request failed');
                return res.json();
            }).then(function (data) {
                var sentAt = data.sentAt || '';
                addMsgRow(msgs, true, 'MO', 'MO' + (sentAt ? ' - ' + sentAt : ''), message);
                msgs.scrollTop = msgs.scrollHeight;
                textarea.value = '';
                sendBtn.disabled = true;
                /* Update thread preview in sidebar */
                var threadBtn = document.querySelector('#mo-conv-widget-thread-list .mo-thread[data-app-id="' + applicationId + '"]');
                if (threadBtn) {
                    var prev = threadBtn.querySelector('.thread-preview');
                    if (prev) prev.textContent = 'MO: ' + shortText(message, 42);
                    var time = threadBtn.querySelector('.thread-meta span:first-child');
                    if (time && sentAt) time.textContent = sentAt.length > 12 ? sentAt.substring(0, 12) : sentAt;
                }
            }).catch(function () {
                alert('Failed to send message. Please try again.');
                sendBtn.disabled = false;
            });
        });
    }

    function clearUnreadInWidget(btn, appId, unreadCountRef) {
        if (!btn || btn.getAttribute('data-needs-reply') !== 'true') return;
        btn.setAttribute('data-needs-reply', 'false');
        Array.prototype.forEach.call(btn.querySelectorAll('.unread-marker'), function (el) {
            el.parentNode.removeChild(el);
        });
        var winId = 'mo-conv-win-' + appId;
        var win = document.getElementById(winId);
        if (win) {
            Array.prototype.forEach.call(win.querySelectorAll('.unread-marker'), function (el) {
                el.parentNode.removeChild(el);
            });
        }
        unreadCountRef.count = Math.max(0, unreadCountRef.count - 1);
        updateBadge(unreadCountRef.count);
    }

    function updateBadge(count) {
        var badge = document.getElementById('mo-conv-widget-unread');
        if (badge) {
            badge.textContent = String(count);
            badge.style.display = count > 0 ? 'inline-flex' : 'none';
        }
    }

    function markReadOnServer(applicationId, unreadCountRef) {
        var body = new URLSearchParams();
        body.append('applicationId', applicationId);
        fetch(contextPath() + '/mo/conversations/read', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: body.toString()
        }).then(function (res) {
            if (!res.ok) return;
            return res.json();
        }).then(function (data) {
            if (data && typeof data.unreadCount === 'number') {
                unreadCountRef.count = data.unreadCount;
                updateBadge(data.unreadCount);
            }
        }).catch(function () {});
    }

    function renderThreadList(threads, host) {
        clearEl(host);

        if (!threads || threads.length === 0) {
            var msg = document.createElement('p');
            msg.style.cssText = 'padding:14px;color:#8a9099;font-size:13px;margin:0;';
            msg.textContent = 'No TA messages yet.';
            host.appendChild(msg);
            return;
        }

        var initialUnread = 0;
        threads.forEach(function (t) { if (t.needsMoReply) initialUnread++; });
        var unreadCountRef = { count: initialUnread };
        updateBadge(initialUnread);

        threads.forEach(function (thread) {
            var btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'mo-thread';
            btn.setAttribute('data-app-id', thread.applicationId || '');
            btn.setAttribute('data-needs-reply', thread.needsMoReply ? 'true' : 'false');
            var searchText = ((thread.taId || '') + ' ' + (thread.moduleName || '') + ' ' + latestPreview(thread)).toLowerCase();
            btn.setAttribute('data-search', searchText);

            var avatarSpan = document.createElement('span');
            avatarSpan.className = 'thread-avatar';
            avatarSpan.textContent = avatarText(thread.taId);
            if (thread.needsMoReply) {
                var dot = document.createElement('span');
                dot.className = 'thread-dot unread-marker';
                avatarSpan.appendChild(dot);
            }

            var main = document.createElement('span');
            main.className = 'thread-main';
            var titleRow = document.createElement('span');
            titleRow.className = 'thread-title';
            var strong = document.createElement('strong');
            strong.textContent = thread.taId || '\u2014';
            var small = document.createElement('small');
            small.textContent = thread.applicationType || '';
            titleRow.appendChild(strong);
            titleRow.appendChild(small);
            var preview = document.createElement('span');
            preview.className = 'thread-preview';
            preview.textContent = latestPreview(thread);
            main.appendChild(titleRow);
            main.appendChild(preview);

            var metaSpan = document.createElement('span');
            metaSpan.className = 'thread-meta';
            var dateSpan = document.createElement('span');
            dateSpan.textContent = latestTime(thread);
            metaSpan.appendChild(dateSpan);
            if (thread.needsMoReply) {
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
                renderChatWindow(thread);
                clearUnreadInWidget(btn, thread.applicationId, unreadCountRef);
                markReadOnServer(thread.applicationId, unreadCountRef);
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

        /* Top bar */
        var topbar = document.createElement('div');
        topbar.className = 'ta-conv-topbar';
        var topTitle = document.createElement('h2');
        topTitle.textContent = 'Conversations';
        var closeBtn = document.createElement('button');
        closeBtn.type = 'button';
        closeBtn.className = 'chip-button';
        closeBtn.id = 'mo-conv-widget-close';
        closeBtn.textContent = '\u2190 Back';
        topbar.appendChild(topTitle);
        topbar.appendChild(closeBtn);

        /* Shell */
        var shell = document.createElement('div');
        shell.className = 'mo-chat-shell';

        var chatList = document.createElement('div');
        chatList.className = 'mo-chat-list';
        var listHead = document.createElement('div');
        listHead.className = 'mo-chat-list-head';
        var searchInput = document.createElement('input');
        searchInput.className = 'mo-chat-search';
        searchInput.id = 'mo-conv-widget-search';
        searchInput.type = 'search';
        searchInput.placeholder = 'Search';
        searchInput.setAttribute('autocomplete', 'off');
        var unreadBadge = document.createElement('span');
        unreadBadge.className = 'mo-chat-count';
        unreadBadge.id = 'mo-conv-widget-unread';
        unreadBadge.style.display = 'none';
        listHead.appendChild(searchInput);
        listHead.appendChild(unreadBadge);
        var threadList = document.createElement('div');
        threadList.className = 'mo-chat-thread-list';
        threadList.id = 'mo-conv-widget-thread-list';
        chatList.appendChild(listHead);
        chatList.appendChild(threadList);

        var chatPanel = document.createElement('div');
        chatPanel.className = 'mo-chat-panel';
        chatPanel.id = 'mo-conv-widget-chat-panel';
        var emptyMsg = document.createElement('div');
        emptyMsg.className = 'chat-empty';
        emptyMsg.id = 'mo-conv-widget-empty';
        emptyMsg.textContent = 'Select a thread to view the conversation.';
        chatPanel.appendChild(emptyMsg);

        shell.appendChild(chatList);
        shell.appendChild(chatPanel);
        panel.appendChild(topbar);
        panel.appendChild(shell);
        main.appendChild(panel);

        closeBtn.addEventListener('click', closeWidget);

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

    function openWidget() {
        injectInlinePanel();

        /* Reset panel */
        var chatPanel = document.getElementById('mo-conv-widget-chat-panel');
        if (chatPanel) {
            Array.prototype.forEach.call(chatPanel.querySelectorAll('.chat-window'), function (w) {
                w.parentNode.removeChild(w);
            });
            var empty = document.getElementById('mo-conv-widget-empty');
            if (empty) empty.style.display = '';
        }

        var threadList = document.getElementById('mo-conv-widget-thread-list');
        if (threadList) {
            clearEl(threadList);
            var loading = document.createElement('p');
            loading.style.cssText = 'padding:14px;color:#8a9099;font-size:13px;margin:0;';
            loading.textContent = 'Loading\u2026';
            threadList.appendChild(loading);
        }

        var searchInput = document.getElementById('mo-conv-widget-search');
        if (searchInput) searchInput.value = '';

        var wrapper = document.getElementById(WRAPPER_ID);
        if (wrapper) wrapper.style.display = 'none';
        var panel = document.getElementById(PANEL_ID);
        if (panel) panel.classList.add('active');
        document.body.classList.add('conv-active');

        /* Fetch data */
        fetch(contextPath() + '/mo/conversations?format=json', {
            method: 'GET',
            credentials: 'same-origin',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        }).then(function (res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        }).then(function (threads) {
            if (threadList) renderThreadList(threads, threadList);
            if (threads && threads.length > 0) {
                renderChatWindow(threads[0]);
                /* Activate first thread button */
                var firstBtn = threadList ? threadList.querySelector('.mo-thread') : null;
                if (firstBtn) firstBtn.classList.add('active');
            }
        }).catch(function () {
            if (threadList) {
                clearEl(threadList);
                var err = document.createElement('p');
                err.style.cssText = 'padding:14px;color:#c45a44;font-size:13px;margin:0;';
                err.textContent = 'Failed to load conversations.';
                threadList.appendChild(err);
            }
        });
    }

    function closeWidget() {
        var wrapper = document.getElementById(WRAPPER_ID);
        if (wrapper) wrapper.style.display = '';
        var panel = document.getElementById(PANEL_ID);
        if (panel) panel.classList.remove('active');
        document.body.classList.remove('conv-active');
    }

    function init() {
        injectInlinePanel();

        var btn = document.getElementById('mo-conv-nav-btn');
        if (!btn) return;

        btn.addEventListener('click', function (e) {
            e.preventDefault();
            openWidget();
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
