(function () {
    'use strict';

    var ctx = window.MO_CONTEXT || '';
    var applications = [];
    var activeFilter = 'ALL';

    // ── helpers ──────────────────────────────────────────────────────────────

    function statusClass(s) {
        var map = { PENDING: 'status-pending', APPROVED: 'status-approved', REJECTED: 'status-rejected', INTERVIEW: 'status-interview' };
        return map[(s || '').toUpperCase()] || '';
    }

    function fmt(dt) {
        if (!dt) return '—';
        try { return new Date(dt).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }); } catch (e) { return dt; }
    }

    function escHtml(str) {
        if (!str) return '';
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    // ── chat thread helpers ───────────────────────────────────────────────────

    function splitStatement(full) {
        if (!full) return { base: '', lines: [] };
        var idx = full.indexOf('\n[');
        var base = (idx >= 0 ? full.substring(0, idx) : full).trim();
        var tail = idx >= 0 ? full.substring(idx) : '';
        var lines = [];
        tail.split('\n').forEach(function (l) {
            var t = l.trim();
            if (t) lines.push(t);
        });
        return { base: base, lines: lines };
    }

    function addBubble(host, sender, text, side) {
        // side: 'ta' (TA, left) or 'mo' (MO self, right)
        var row = document.createElement('div');
        row.className = 'ta-chat-row ta-chat-row--' + side;
        var meta = document.createElement('div');
        meta.className = 'ta-chat-sender';
        meta.textContent = sender;
        var bubble = document.createElement('div');
        bubble.className = 'ta-chat-bubble ta-chat-bubble--' + side;
        bubble.textContent = text && text.trim() ? text : '—';
        row.appendChild(meta);
        row.appendChild(bubble);
        host.appendChild(row);
    }

    function renderChatThread(app) {
        var thread = document.getElementById('mo-chat-thread');
        while (thread.firstChild) thread.removeChild(thread.firstChild);

        var parts = splitStatement(app.statement || '');
        // Initial statement is from TA → left side ('mo' CSS = left/beige in our chat CSS)
        addBubble(thread, 'TA ' + escHtml(app.taId) + ' (statement)', parts.base || '(no statement)', 'mo');
        parts.lines.forEach(function (line) {
            var isMO = / MO\]:/.test(line);
            var text  = line.replace(/^\[.*?\]\s*/, '').trim();
            if (isMO) {
                // MO's own message → right side ('ta' CSS = right/lavender)
                addBubble(thread, 'You (MO)', text, 'ta');
            } else {
                // TA reply → left side
                addBubble(thread, 'TA ' + escHtml(app.taId), text, 'mo');
            }
        });
        thread.scrollTop = thread.scrollHeight;
    }

    // ── chat overlay ─────────────────────────────────────────────────────────

    function openChat(app) {
        document.getElementById('mo-chat-app-id').value   = app.applicationId || '';
        document.getElementById('mo-chat-reply').value    = '';
        document.getElementById('mo-chat-meta').textContent =
            (app.moduleName || '—') + ' · ' + (app.taId || '') + ' · ' + (app.applicationId || '');
        renderChatThread(app);
        var ov = document.getElementById('mo-chat-overlay');
        ov.style.display = 'flex';
        ov.setAttribute('aria-hidden', 'false');
    }

    function closeChat() {
        var ov = document.getElementById('mo-chat-overlay');
        ov.style.display = 'none';
        ov.setAttribute('aria-hidden', 'true');
    }

    // ── card builder ─────────────────────────────────────────────────────────

    function kv(label, valueText) {
        return '<div class="kv-pair"><span class="kv-key">' + label
            + '</span><span class="kv-val">' + valueText + '</span></div>';
    }

    function kvRaw(label, innerHtml) {
        return '<div class="kv-pair"><span class="kv-key">' + label
            + '</span><span class="kv-val" style="display:flex;align-items:center;gap:6px;">'
            + innerHtml + '</span></div>';
    }

    function buildCard(app) {
        var status      = (app.status || '').toUpperCase();
        var isPending   = status === 'PENDING';
        var isInterview = status === 'INTERVIEW';

        // CV cell
        var cvHtml;
        if (app.cvAttached && app.cvFilePath) {
            cvHtml = '<a href="' + escHtml(ctx) + '/' + escHtml(app.cvFilePath)
                + '" target="_blank" class="chip-button" '
                + 'style="font-size:11px;color:#2c4080;background:#dde6f7;'
                + 'border-color:#aabce8;text-decoration:none;padding:4px 10px;">View CV</a>';
        } else if (app.cvAttached) {
            cvHtml = '<span style="font-size:12px;color:#4f6c4d;">Attached (no path)</span>';
        } else {
            cvHtml = '<span style="font-size:12px;color:#aaa;">\u2014</span>';
        }

        // Status badge
        var statusBadge = '<strong class="' + statusClass(app.status) + '" '
            + 'style="font-size:13px;">' + escHtml(app.status) + '</strong>';

        // 6-cell key-value grid
        var grid = '<div class="detail-kv-grid" style="margin-bottom:14px;">'
            + kv('APPLICANT (TA ID)', escHtml(app.taId))
            + kv('MODULE',            escHtml(app.moduleName))
            + kv('APPLIED',           fmt(app.applyTime))
            + kv('TYPE',              escHtml(app.applicationType) || '\u2014')
            + kvRaw('STATUS', statusBadge)
            + kvRaw('CV', cvHtml)
            + '</div>';

        // Toolbar row under the grid
        var toolbar = '<div class="mo-card-toolbar">'
            + '<button type="button" class="chip-button mo-chat-btn" '
            + 'data-app-id="' + escHtml(app.applicationId) + '">Chat / Statement</button>'
            + '</div>';

        // Action section
        var actionBlock = '';
        if (isPending) {
            actionBlock = '<div class="mo-card-actions">' + buildDecisionForm(app, false) + '</div>';
        } else if (isInterview) {
            actionBlock = '<div class="mo-card-actions">'
                + '<p style="margin:0 0 10px;font-size:13px;color:#3a6ea8;">'
                + '<strong>Interview in progress.</strong> Make final decision when ready:</p>'
                + buildDecisionForm(app, true)
                + '</div>';
        } else {
            var fb = app.feedback
                ? '<p style="margin:6px 0 0;font-size:13px;color:#555;">'
                  + '<em>' + escHtml(app.feedback) + '</em></p>'
                : '';
            actionBlock = '<div class="mo-card-actions">'
                + '<p style="margin:0;font-size:13px;color:#6b7a8b;">Decision recorded.</p>'
                + fb + '</div>';
        }

        return '<section class="list-card mo-app-card">'
            + grid + toolbar + actionBlock
            + '</section>';
    }

    function buildDecisionForm(app, interviewMode) {
        var approveBtn = '<button type="submit" name="status" value="APPROVED"  class="chip-button" '
            + 'style="background:#d9edda;color:#3a5c39;border-color:#a8cfaa;">Approve</button>';
        var rejectBtn  = '<button type="submit" name="status" value="REJECTED"  class="chip-button" '
            + 'style="background:#f7ddda;color:#7a3030;border-color:#e8b0aa;">Reject</button>';
        var interviewBtn = !interviewMode
            ? '<button type="submit" name="status" value="INTERVIEW" class="chip-button" '
              + 'style="background:#dde6f7;color:#2c4080;border-color:#aabce8;">Interview</button>'
            : '';
        return '<form method="post" action="' + escHtml(ctx) + '/mo/application/action" class="mo-action-form">'
            + '<input type="hidden" name="applicationId" value="' + escHtml(app.applicationId) + '" />'
            + '<input type="hidden" name="taId"          value="' + escHtml(app.taId) + '" />'
            + '<input type="hidden" name="moduleName"    value="' + escHtml(app.moduleName) + '" />'
            + '<div class="form-group" style="margin-bottom:8px;">'
            + '<label class="field-label" style="font-size:12px;">Feedback / decision note (optional)</label>'
            + '<textarea name="feedback" rows="2" class="filter-field" style="width:100%;box-sizing:border-box;" '
            + 'placeholder="Leave a note for the applicant…"></textarea>'
            + '</div>'
            + '<div class="filter-actions" style="gap:8px;">'
            + approveBtn + interviewBtn + rejectBtn
            + '</div></form>';
    }

    // ── render ────────────────────────────────────────────────────────────────

    function render() {
        var list = activeFilter === 'ALL'
            ? applications
            : applications.filter(function (a) { return (a.status || '').toUpperCase() === activeFilter; });

        var container = document.getElementById('mo-app-list');
        var empty     = document.getElementById('mo-app-empty');
        if (!list.length) {
            container.innerHTML = '';
            empty.hidden = false;
        } else {
            empty.hidden = true;
            container.innerHTML = list.map(buildCard).join('');
            // Wire chat buttons
            container.querySelectorAll('.mo-chat-btn').forEach(function (btn) {
                btn.addEventListener('click', function () {
                    var appId = this.getAttribute('data-app-id');
                    var app = applications.filter(function (a) { return a.applicationId === appId; })[0];
                    if (app) openChat(app);
                });
            });
        }
    }

    // ── init ─────────────────────────────────────────────────────────────────

    function init() {
        var raw = document.getElementById('mo-applications-json');
        try { applications = JSON.parse(raw ? raw.textContent : '[]'); } catch (e) { applications = []; }

        document.querySelectorAll('[data-mo-filter]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                activeFilter = this.getAttribute('data-mo-filter');
                document.querySelectorAll('[data-mo-filter]').forEach(function (b) { b.classList.remove('active'); });
                this.classList.add('active');
                render();
            });
        });

        var closeBtn = document.getElementById('mo-chat-close');
        if (closeBtn) closeBtn.addEventListener('click', closeChat);

        var overlay = document.getElementById('mo-chat-overlay');
        if (overlay) {
            overlay.addEventListener('click', function (ev) {
                if (ev.target === overlay) closeChat();
            });
        }

        var replyForm = document.getElementById('mo-chat-form');
        if (replyForm) {
            replyForm.setAttribute('action', ctx + '/mo/application/reply');
        }

        render();
    }

    document.addEventListener('DOMContentLoaded', init);
})();
