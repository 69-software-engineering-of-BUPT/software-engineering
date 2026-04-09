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
        if (!prev) {
            return fb.trim().length > 0 || (st && st !== 'PENDING');
        }
        return prev.status !== st || (prev.feedback || '') !== fb;
    }

    function markSeen(app) {
        var seen = loadSeen();
        seen[app.applicationId] = {
            status: app.status || '',
            feedback: app.feedback || ''
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
        if (raw.indexOf('NON') >= 0 && raw.indexOf('LEADER') >= 0) return 'Non-leader approval';
        if (raw.indexOf('LEADER') >= 0) return 'Leader approval';
        if (raw === 'NL') return 'Non-leader approval';
        if (raw === 'L') return 'Leader approval';
        return applicationType;
    }

    function addChatBubble(host, sender, text, kind) {
        var row = document.createElement('div');
        row.className = 'ta-chat-row ta-chat-row--' + kind;

        var meta = document.createElement('div');
        meta.className = 'ta-chat-sender';
        meta.appendChild(document.createTextNode(sender));

        var bubble = document.createElement('div');
        bubble.className = 'ta-chat-bubble ta-chat-bubble--' + kind;
        bubble.appendChild(document.createTextNode(text && text.trim() ? text : '-'));

        row.appendChild(meta);
        row.appendChild(bubble);
        host.appendChild(row);
    }

    function renderModal(app) {
        var parts = splitStatement(app.statement || '');
        var chat = document.getElementById('ta-dialog-chat-thread');
        clearEl(chat);

        addChatBubble(chat, 'You (statement)', parts.base || 'No statement content.', 'ta');
        if (parts.replies.length > 0) {
            parts.replies.forEach(function (line) {
                addChatBubble(chat, 'You (follow-up)', line, 'ta');
            });
        }

        var fb = (app.feedback || '').trim();
        addChatBubble(chat, 'Module organizer', fb.length ? fb : 'No instructor feedback yet.', 'mo');

        document.getElementById('ta-dialog-app-id').value = app.applicationId || '';
        document.getElementById('ta-dialog-reply').value = '';

        var ph = 'Add a response for the module organizer.';
        if (fb.length) {
            ph = 'Reply to: "' + fb.substring(0, 120) + (fb.length > 120 ? '...' : '') + '"';
        }
        document.getElementById('ta-dialog-reply').setAttribute('placeholder', ph);

        document.getElementById('ta-feedback-meta').textContent =
            (app.moduleName || '-') + ' | ' + (app.jobId || '') + ' | ' + (app.applicationId || '');
    }

    function openOverlay(app) {
        renderModal(app);
        var ov = document.getElementById('ta-feedback-overlay');
        ov.classList.add('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'false');
        markSeen(app);
        refreshRowBadge(app.applicationId, loadSeen());
    }

    function closeOverlay() {
        var ov = document.getElementById('ta-feedback-overlay');
        ov.classList.remove('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'true');
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
                else span.appendChild(document.createTextNode(text == null || text === '' ? '-' : String(text)));
                return span;
            }

            row.appendChild(cell(app.moduleName));
            row.appendChild(cell(app.mdName));

            var stSpan = document.createElement('span');
            var badge = document.createElement('span');
            badge.className = statusPill(app.status);
            badge.textContent = app.status || '-';
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
                openOverlay(app);
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

        var replyForm = document.querySelector('.ta-reply-form');
        if (replyForm) {
            var replyAction = contextPath() + '/ta/application/reply';
            replyForm.setAttribute('action', replyAction);

            replyForm.addEventListener('submit', function (ev) {
                ev.preventDefault();
                var appIdInput = document.getElementById('ta-dialog-app-id');
                var replyInput = document.getElementById('ta-dialog-reply');
                var message = (replyInput.value || '').trim();
                var applicationId = (appIdInput.value || '').trim();

                if (!message || !applicationId) return;

                var formData = new URLSearchParams();
                formData.append('applicationId', applicationId);
                formData.append('message', message);

                var submitBtn = replyForm.querySelector('button[type="submit"]');
                if (submitBtn) { submitBtn.disabled = true; submitBtn.textContent = 'Sending...'; }

                fetch(replyAction, {
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
                    // Append the message to the chat thread
                    var chat = document.getElementById('ta-dialog-chat-thread');
                    addChatBubble(chat, 'You (follow-up)', message, 'ta');
                    chat.scrollTop = chat.scrollHeight;

                    // Update the in-memory app data so subsequent opens reflect it
                    var allApps = window.__TA_APPS__ || [];
                    for (var i = 0; i < allApps.length; i++) {
                        if (allApps[i].applicationId === applicationId) {
                            var now = new Date();
                            var ts = now.getFullYear() + '-' +
                                String(now.getMonth() + 1).padStart(2, '0') + '-' +
                                String(now.getDate()).padStart(2, '0') + ' ' +
                                String(now.getHours()).padStart(2, '0') + ':' +
                                String(now.getMinutes()).padStart(2, '0');
                            var line = '\n[' + ts + ' TA]: ' + message;
                            allApps[i].statement = (allApps[i].statement || '') + line;
                            break;
                        }
                    }

                    replyInput.value = '';
                }).catch(function () {
                    alert('Failed to send reply. Please try again.');
                }).finally(function () {
                    if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = 'Send reply'; }
                });
            });
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
