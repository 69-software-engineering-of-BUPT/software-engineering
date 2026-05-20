(function () {
    'use strict';

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function buildNotiCard(n) {
        var card = document.createElement('section');
        card.className = 'list-card ta-noti-card';
        card.dataset.read = String(n.isRead);
        card.style.marginBottom = '10px';
        if (!n.isRead) {
            card.style.borderColor = '#c9a227';
            card.style.boxShadow = '0 0 0 2px rgba(201,162,39,0.15)';
        }

        var header = document.createElement('div');
        header.className = 'list-title-row';

        var left = document.createElement('div');
        var strong = document.createElement('strong');
        strong.textContent = n.type || 'Notification';
        strong.style.fontSize = '14px';
        left.appendChild(strong);

        if (!n.isRead) {
            var pill = document.createElement('span');
            pill.className = 'ta-new-mark';
            pill.textContent = 'Unread';
            pill.style.marginLeft = '8px';
            left.appendChild(pill);
        }
        header.appendChild(left);

        var time = document.createElement('span');
        time.style.color = '#7f868f';
        time.style.fontSize = '12px';
        time.textContent = n.createdAt || '';
        header.appendChild(time);
        card.appendChild(header);

        var content = document.createElement('p');
        content.style.margin = '8px 0 0';
        content.style.color = '#3d4148';
        content.style.lineHeight = '1.5';
        content.textContent = n.content || '';
        card.appendChild(content);

        var actions = document.createElement('div');
        actions.className = 'ta-profile-actions';
        actions.style.marginTop = '10px';
        var form = document.createElement('form');
        form.method = 'post';
        form.action = contextPath() + '/ta/notifications';
        var hiddenAction = document.createElement('input');
        hiddenAction.type = 'hidden';
        hiddenAction.name = 'action';
        hiddenAction.value = n.isRead ? 'markUnread' : 'markRead';
        var hiddenId = document.createElement('input');
        hiddenId.type = 'hidden';
        hiddenId.name = 'notificationId';
        hiddenId.value = n.notificationId || '';
        var btn = document.createElement('button');
        btn.type = 'submit';
        btn.className = 'chip-button';
        btn.textContent = n.isRead ? 'Mark as unread' : 'Mark as read';
        form.appendChild(hiddenAction);
        form.appendChild(hiddenId);
        form.appendChild(btn);
        actions.appendChild(form);
        card.appendChild(actions);

        return card;
    }

    function render(notifications, filter) {
        var host = document.getElementById('ta-noti-list');
        var empty = document.getElementById('ta-noti-empty');
        clearEl(host);
        var count = 0;

        notifications.forEach(function (n) {
            if (filter === 'UNREAD' && n.isRead) return;
            count++;
            host.appendChild(buildNotiCard(n));
        });

        empty.hidden = count !== 0;
    }

    function init() {
        var el = document.getElementById('ta-notifications-json');
        if (!el) return;

        var notifications;
        try { notifications = JSON.parse(el.textContent || '[]'); } catch (e) { notifications = []; }

        var filter = 'ALL';
        render(notifications, filter);

        var filterBtns = document.querySelectorAll('[data-noti-filter]');
        Array.prototype.forEach.call(filterBtns, function (btn) {
            btn.addEventListener('click', function () {
                filter = btn.getAttribute('data-noti-filter');
                Array.prototype.forEach.call(filterBtns, function (b) {
                    b.classList.toggle('active', b.getAttribute('data-noti-filter') === filter);
                });
                render(notifications, filter);
            });
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
