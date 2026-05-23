/**
 * ta-conv-widget.js
 * Self-contained conversation overlay for TA pages other than Home.
 * Reads application data from sessionStorage (stored by ta-home.js on Home page).
 */
(function () {
    'use strict';

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function addChatBubble(host, sender, text, kind) {
        var row = document.createElement('div');
        row.className = 'ta-chat-row ta-chat-row--' + kind;

        var meta = document.createElement('div');
        meta.className = 'ta-chat-sender';
        meta.appendChild(document.createTextNode(sender));

        var bubble = document.createElement('div');
        bubble.className = 'ta-chat-bubble ta-chat-bubble--' + kind;
        bubble.appendChild(document.createTextNode(text && text.trim() ? text : '\u2014'));

        row.appendChild(meta);
        row.appendChild(bubble);
        host.appendChild(row);
    }

    function splitStatement(raw) {
        var lines = (raw || '').split('\n');
        var base = '';
        var replies = [];
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i].trim();
            if (!line) continue;
            if (/^\[.*?\]/.test(line)) {
                replies.push(line);
            } else {
                base = base ? base + '\n' + line : line;
            }
        }
        return { base: base, replies: replies };
    }

    var OVERLAY_ID = 'ta-conv-widget-overlay';

    function injectOverlay() {
        if (document.getElementById(OVERLAY_ID)) return;

        var overlay = document.createElement('div');
        overlay.id = OVERLAY_ID;
        overlay.className = 'ta-feedback-overlay';
        overlay.setAttribute('aria-hidden', 'true');

        var panel = document.createElement('div');
        panel.className = 'ta-feedback-panel list-card';
        panel.setAttribute('role', 'dialog');
        panel.setAttribute('aria-modal', 'true');

        panel.innerHTML =
            '<div class="ta-feedback-head">' +
            '  <h2>Conversation</h2>' +
            '  <button type="button" class="chip-button" id="ta-conv-widget-close">Close</button>' +
            '</div>' +
            '<p class="ta-feedback-meta" id="ta-conv-widget-meta"></p>' +
            '<div class="ta-chat-thread" id="ta-conv-widget-thread"></div>' +
            '<form class="ta-reply-form" id="ta-conv-widget-form" method="post">' +
            '  <input type="hidden" name="applicationId" id="ta-conv-widget-app-id" value="" />' +
            '  <label class="filter-field">' +
            '    <small>REPLY TO INSTRUCTOR (optional, max 500 chars)</small>' +
            '    <textarea name="message" id="ta-conv-widget-reply" rows="4" maxlength="500"' +
            '      placeholder="Add a response for the module organiser."></textarea>' +
            '  </label>' +
            '  <div class="ta-profile-actions">' +
            '    <button type="submit" class="chip-button active" id="ta-conv-widget-send">Send reply</button>' +
            '  </div>' +
            '</form>';

        overlay.appendChild(panel);
        document.body.appendChild(overlay);

        document.getElementById('ta-conv-widget-close').addEventListener('click', closeWidget);
        overlay.addEventListener('click', function (ev) {
            if (ev.target === overlay) closeWidget();
        });

        // Reply form submit via AJAX
        var form = document.getElementById('ta-conv-widget-form');
        var replyAction = contextPath() + '/ta/application/reply';
        form.setAttribute('action', replyAction);
        form.addEventListener('submit', function (ev) {
            ev.preventDefault();
            var appIdInput = document.getElementById('ta-conv-widget-app-id');
            var replyInput = document.getElementById('ta-conv-widget-reply');
            var sendBtn = document.getElementById('ta-conv-widget-send');
            var message = (replyInput.value || '').trim();
            var applicationId = (appIdInput.value || '').trim();
            if (!message || !applicationId) return;

            var formData = new URLSearchParams();
            formData.append('applicationId', applicationId);
            formData.append('message', message);

            if (sendBtn) { sendBtn.disabled = true; sendBtn.textContent = 'Sending\u2026'; }

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
                var thread = document.getElementById('ta-conv-widget-thread');
                addChatBubble(thread, 'You (follow-up)', message, 'ta');
                thread.scrollTop = thread.scrollHeight;
                replyInput.value = '';

                // Update sessionStorage so the change persists
                try {
                    var apps = JSON.parse(sessionStorage.getItem('ta_apps') || '[]');
                    var now = new Date();
                    var ts = now.getFullYear() + '-' +
                        String(now.getMonth() + 1).padStart(2, '0') + '-' +
                        String(now.getDate()).padStart(2, '0') + ' ' +
                        String(now.getHours()).padStart(2, '0') + ':' +
                        String(now.getMinutes()).padStart(2, '0');
                    var line = '\n[' + ts + ' TA]: ' + message;
                    for (var i = 0; i < apps.length; i++) {
                        if (apps[i].applicationId === applicationId) {
                            apps[i].statement = (apps[i].statement || '') + line;
                            break;
                        }
                    }
                    sessionStorage.setItem('ta_apps', JSON.stringify(apps));
                } catch (e) {}
            }).catch(function () {
                alert('Failed to send reply. Please try again.');
            }).finally(function () {
                if (sendBtn) { sendBtn.disabled = false; sendBtn.textContent = 'Send reply'; }
            });
        });
    }

    function openWidget(app) {
        var overlay = document.getElementById(OVERLAY_ID);
        if (!overlay) return;

        var thread = document.getElementById('ta-conv-widget-thread');
        var meta = document.getElementById('ta-conv-widget-meta');
        var appIdInput = document.getElementById('ta-conv-widget-app-id');

        clearEl(thread);
        var parts = splitStatement(app.statement || '');
        addChatBubble(thread, 'You (initial statement)', parts.base || 'No statement content.', 'ta');
        if (parts.replies.length > 0) {
            parts.replies.forEach(function (line) {
                var isMO = / MO\]:/.test(line);
                var text = line.replace(/^\[.*?\]\s*/, '').trim();
                addChatBubble(thread, isMO ? 'Module organiser' : 'You (follow-up)', text || line, isMO ? 'mo' : 'ta');
            });
        }
        thread.scrollTop = thread.scrollHeight;

        if (meta) {
            meta.textContent = (app.moduleName || '\u2014') + ' \u00b7 ' + (app.jobId || '') + ' \u00b7 ' + (app.applicationId || '');
        }
        if (appIdInput) appIdInput.value = app.applicationId || '';

        overlay.classList.add('ta-feedback-overlay--open');
        overlay.setAttribute('aria-hidden', 'false');
    }

    function closeWidget() {
        var overlay = document.getElementById(OVERLAY_ID);
        if (!overlay) return;
        overlay.classList.remove('ta-feedback-overlay--open');
        overlay.setAttribute('aria-hidden', 'true');
    }

    function init() {
        injectOverlay();

        var btn = document.getElementById('ta-conv-nav-btn');
        if (!btn) return;

        btn.addEventListener('click', function (e) {
            e.preventDefault();

            var apps = [];
            var activeApp = null;
            try {
                var raw = sessionStorage.getItem('ta_apps');
                if (raw) apps = JSON.parse(raw);
            } catch (ex) {}
            try {
                var activeRaw = sessionStorage.getItem('ta_active_app');
                if (activeRaw) activeApp = JSON.parse(activeRaw);
            } catch (ex) {}

            if (!activeApp && apps.length > 0) activeApp = apps[0];

            if (activeApp) {
                openWidget(activeApp);
            } else {
                // No cached data — navigate to Home which will auto-open conversation
                window.location.href = contextPath() + '/ta/home?conv=1';
            }
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
