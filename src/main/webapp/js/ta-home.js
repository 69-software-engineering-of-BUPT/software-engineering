(function () {
    'use strict';

    var STORAGE_KEY = 'taAppSeen_v1';

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function loadSeen() {
        try {
            return JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}');
        } catch (e) {
            return {};
        }
    }

    function saveSeen(obj) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(obj));
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

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function setText(el, text) {
        clearEl(el);
        el.appendChild(document.createTextNode(text || ''));
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
        var apps = window.__TA_APPS__ || [];
        var count = 0;
        apps.forEach(function (app) { if (hasUpdate(app, seen)) count++; });
        var badge = document.getElementById('ta-conv-unread-count');
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
        var panel = document.getElementById('ta-dialog-chat-panel');
        var empty = document.getElementById('ta-dialog-chat-empty');

        var winId = 'ta-chat-win-' + app.applicationId;
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

        /* --- Header --- */
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

        /* --- Messages --- */
        var msgs = document.createElement('div');
        msgs.className = 'chat-messages';
        /* Initial statement: TA is sender → right-aligned (message-row--mo) */
        addMsgRow(msgs, false, jobAbbr, 'TA - (initial statement)', parts.base || '(no statement)');
        parts.replies.forEach(function (line) {
            var parsed = parseReplyLine(line);
            var isMO = parsed.sender === 'MO';
            var abbr = isMO ? 'MO' : jobAbbr;
            var label = (isMO ? 'MO' : 'TA') + (parsed.timestamp ? ' - ' + parsed.timestamp : '');
            addMsgRow(msgs, isMO, abbr, label, parsed.text || line);
        });
        win.appendChild(msgs);

        /* --- Reply area --- */
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

        /* --- Reply handler --- */
        var applicationId = app.applicationId;
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
                var allApps = window.__TA_APPS__ || [];
                for (var i = 0; i < allApps.length; i++) {
                    if (allApps[i].applicationId === applicationId) {
                        allApps[i].statement = (allApps[i].statement || '') + '\n[' + ts + ' TA]: ' + message;
                        var threadBtn = document.querySelector('#ta-dialog-thread-list .mo-thread[data-app-id="' + applicationId + '"]');
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
        var host = document.getElementById('ta-dialog-thread-list');
        clearEl(host);

        if (!apps || apps.length === 0) {
            var msg = document.createElement('p');
            msg.style.cssText = 'padding:14px;color:#8a9099;font-size:13px;margin:0;';
            msg.textContent = 'No applications yet.';
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

            /* Avatar */
            var avatarSpan = document.createElement('span');
            avatarSpan.className = 'thread-avatar';
            avatarSpan.textContent = moduleAvatarText(app);
            if (hasUnread) {
                var dot = document.createElement('span');
                dot.className = 'thread-dot unread-marker';
                avatarSpan.appendChild(dot);
            }

            /* Main */
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

            /* Meta */
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
                refreshRowBadge(app.applicationId, loadSeen());
                try { sessionStorage.setItem('ta_active_app', JSON.stringify(app)); } catch (e) {}
            });
        });
    }

    function openOverlay(app) {
        try { sessionStorage.setItem('ta_active_app', JSON.stringify(app)); } catch (e) {}
        var apps = window.__TA_APPS__ || [];
        /* Clear previously rendered windows so switching apps always shows fresh state */
        var panel = document.getElementById('ta-dialog-chat-panel');
        if (panel) {
            Array.prototype.forEach.call(panel.querySelectorAll('.chat-window'), function (w) {
                w.parentNode.removeChild(w);
            });
            var empty = document.getElementById('ta-dialog-chat-empty');
            if (empty) empty.style.display = '';
        }
        /* Reset search */
        var searchInput = document.getElementById('ta-conv-search');
        if (searchInput) searchInput.value = '';
        renderThreadList(apps, app.applicationId);
        renderChatWindow(app);
        var ov = document.getElementById('ta-feedback-overlay');
        ov.classList.add('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'false');
        markSeen(app);
        updateUnreadCount();
        refreshRowBadge(app.applicationId, loadSeen());
    }

    function closeOverlay() {
        var ov = document.getElementById('ta-feedback-overlay');
        ov.classList.remove('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'true');
    }

    function openFeedbackModal(app) {
        var modal = document.getElementById('ta-feedback-modal');
        var body = document.getElementById('ta-feedback-modal-body');
        if (!modal || !body) return;
        body.textContent = (app.feedback || '').trim() || 'No feedback provided yet.';
        modal.classList.add('ta-feedback-overlay--open');
        modal.setAttribute('aria-hidden', 'false');
        markSeen(app);
        refreshRowBadge(app.applicationId, loadSeen());
    }

    function closeFeedbackModal() {
        var modal = document.getElementById('ta-feedback-modal');
        if (!modal) return;
        modal.classList.remove('ta-feedback-overlay--open');
        modal.setAttribute('aria-hidden', 'true');
    }

    function refreshRowBadge(appId, seen) {
        var row = document.querySelector('.ta-app-row[data-application-id="' + appId + '"]');
        if (!row) return;
        var apps = window.__TA_APPS__ || [];
        var app = apps.filter(function (a) { return a.applicationId === appId; })[0];
        if (!app) return;
        var show = hasUpdate(app, seen);
        row.classList.toggle('ta-app-row--alert', show);
        var pill = row.querySelector('.ta-new-mark');
        if (pill) pill.style.display = show ? 'inline-flex' : 'none';
    }

    function statusPill(status) {
        var s = (status || '').toUpperCase();
        var cls = 'status';
        if (s === 'APPROVED') cls += ' success';
        else if (s === 'REJECTED') cls += ' danger';
        else cls += ' neutral';
        return cls;
    }

    function renderRows(apps, filter, seen) {
        var host = document.getElementById('ta-app-rows');
        var empty = document.getElementById('ta-app-empty');
        clearEl(host);
        var count = 0;
        apps.forEach(function (app) {
            var st = (app.status || '').toUpperCase();
            if (filter !== 'ALL' && st !== filter) return;
            count++;

            var row = document.createElement('article');
            row.className = 'list-row ta-app-row';
            row.dataset.applicationId = app.applicationId || '';
            row.dataset.status = app.status || '';
            if (hasUpdate(app, seen)) row.classList.add('ta-app-row--alert');

            function cell(text, isHtml) {
                var span = document.createElement('span');
                if (isHtml) span.innerHTML = text;
                else span.appendChild(document.createTextNode(text == null || text === '' ? '—' : String(text)));
                return span;
            }

            row.appendChild(cell(app.moduleName));
            row.appendChild(cell(app.mdName));

            var stSpan = document.createElement('span');
            var badge = document.createElement('span');
            badge.className = statusPill(app.status);
            badge.textContent = app.status || '—';
            stSpan.appendChild(badge);
            if (st === 'APPROVED') {
                var approveType = normalizeApproveType(app.applicationType);
                if (approveType) {
                    var detail = document.createElement('small');
                    detail.className = 'ta-approve-type';
                    detail.textContent = approveType;
                    stSpan.appendChild(detail);
                }
            }
            if (hasUpdate(app, seen)) {
                var pill = document.createElement('span');
                pill.className = 'ta-new-mark';
                pill.textContent = 'New';
                stSpan.appendChild(pill);
            }
            row.appendChild(stSpan);

            row.appendChild(cell(app.applyTime));

            var fbCell = document.createElement('span');
            var btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'chip-button ta-feedback-btn';
            btn.textContent = 'Feedback';
            btn.addEventListener('click', function () {
                openFeedbackModal(app);
            });
            fbCell.appendChild(btn);
            row.appendChild(fbCell);

            host.appendChild(row);
        });
        empty.hidden = count !== 0;
    }

    function init() {
        var el = document.getElementById('ta-applications-json');
        if (!el) return;

        var apps;
        try {
            apps = JSON.parse(el.textContent || '[]');
        } catch (e) {
            apps = [];
        }
        window.__TA_APPS__ = apps;
        try { sessionStorage.setItem('ta_apps', JSON.stringify(apps)); } catch (e) {}

        var seen = loadSeen();
        var filter = 'ALL';

        renderRows(apps, filter, seen);

        document.getElementById('ta-app-filters').addEventListener('click', function (ev) {
            var t = ev.target;
            if (!t.getAttribute || !t.getAttribute('data-ta-filter')) return;
            filter = t.getAttribute('data-ta-filter');
            Array.prototype.forEach.call(document.querySelectorAll('[data-ta-filter]'), function (b) {
                b.classList.toggle('active', b.getAttribute('data-ta-filter') === filter);
            });
            renderRows(apps, filter, loadSeen());
        });

        document.getElementById('ta-feedback-close').addEventListener('click', closeOverlay);
        document.getElementById('ta-feedback-overlay').addEventListener('click', function (ev) {
            if (ev.target.id === 'ta-feedback-overlay') closeOverlay();
        });

        var convSearch = document.getElementById('ta-conv-search');
        if (convSearch) {
            convSearch.addEventListener('input', function () {
                var term = convSearch.value.trim().toLowerCase();
                Array.prototype.forEach.call(
                    document.querySelectorAll('#ta-dialog-thread-list .mo-thread'),
                    function (btn) {
                        var match = !term || (btn.getAttribute('data-search') || '').indexOf(term) >= 0;
                        btn.style.display = match ? 'grid' : 'none';
                    }
                );
            });
        }

        updateUnreadCount();

        var feedbackModalClose = document.getElementById('ta-feedback-modal-close');
        if (feedbackModalClose) {
            feedbackModalClose.addEventListener('click', closeFeedbackModal);
        }
        var feedbackModal = document.getElementById('ta-feedback-modal');
        if (feedbackModal) {
            feedbackModal.addEventListener('click', function (ev) {
                if (ev.target.id === 'ta-feedback-modal') closeFeedbackModal();
            });
        }

        var convNavBtn = document.getElementById('ta-conv-nav-btn');
        if (convNavBtn) {
            convNavBtn.addEventListener('click', function (e) {
                e.preventDefault();
                var activeApp = window.__TA_ACTIVE_APP__;
                if (!activeApp && apps.length > 0) {
                    activeApp = apps[0];
                }
                if (activeApp) {
                    openOverlay(activeApp);
                }
            });
        }

        // Auto-open conversation when redirected from another page with ?conv=1
        if (window.location.search.indexOf('conv=1') >= 0) {
            var autoApp = window.__TA_ACTIVE_APP__ || (apps.length > 0 ? apps[0] : null);
            if (autoApp) {
                openOverlay(autoApp);
            }
            if (window.history && window.history.replaceState) {
                window.history.replaceState(null, '', window.location.pathname);
            }
        }

        // Profile form validation
        var profileForm = document.querySelector('.ta-profile-form');
        if (profileForm) {
            var profileFields = [
                { name: 'email', label: 'Email' },
                { name: 'phoneNumber', label: 'Phone' },
                { name: 'researchArea', label: 'Research Area' },
                { name: 'cet6Grade', label: 'CET6 Grade' }
            ];

            // Real-time: clear mandatory on input
            profileFields.forEach(function (f) {
                var input = profileForm.querySelector('[name="' + f.name + '"]');
                if (!input) return;
                input.addEventListener('input', function () {
                    if (input.value.trim()) {
                        var wrap = input.closest('.filter-field');
                        if (wrap) {
                            wrap.classList.remove('mandatory-error');
                            var h = wrap.querySelector('.mandatory-hint');
                            if (h) h.remove();
                        }
                    }
                });
            });

            profileForm.addEventListener('submit', function (ev) {
                var valid = true;
                for (var i = 0; i < profileFields.length; i++) {
                    var input = profileForm.querySelector('[name="' + profileFields[i].name + '"]');
                    if (!input) continue;
                    var fieldWrap = input.closest('.filter-field');
                    // Clear previous
                    if (fieldWrap) {
                        fieldWrap.classList.remove('mandatory-error');
                        var oldHint = fieldWrap.querySelector('.mandatory-hint');
                        if (oldHint) oldHint.remove();
                    }
                    if (!input.value.trim()) {
                        valid = false;
                        if (fieldWrap) {
                            fieldWrap.classList.add('mandatory-error');
                            var hint = document.createElement('span');
                            hint.className = 'mandatory-hint';
                            hint.textContent = 'Mandatory';
                            fieldWrap.appendChild(hint);
                        }
                    }
                }
                if (!valid) {
                    ev.preventDefault();
                }
            });
        }
    }

    document.addEventListener('DOMContentLoaded', init);
})();
