(function () {
    'use strict';

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function escapeHtml(str) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(str || ''));
        return div.innerHTML;
    }

    /* ---------- Render a single job card ---------- */
    function buildJobCard(job) {
        var card = document.createElement('section');
        card.className = 'list-card ta-job-card';
        card.dataset.jobId = job.jobId || '';
        card.dataset.moduleName = (job.moduleName || '').toLowerCase();
        card.dataset.mdName = (job.mdName || '').toLowerCase();
        card.dataset.deadline = job.deadline || '';
        card.dataset.published = job.publishedAt || '';
        card.style.marginBottom = '12px';

        var header = document.createElement('div');
        header.className = 'list-title-row';
        var h2 = document.createElement('h2');
        h2.textContent = job.moduleName || 'Untitled';
        var badge = document.createElement('span');
        badge.className = 'status success';
        badge.textContent = 'OPEN';
        header.appendChild(h2);
        header.appendChild(badge);
        card.appendChild(header);

        var grid = document.createElement('div');
        grid.className = 'detail-kv-grid';

        function kv(label, value, fullWidth) {
            var d = document.createElement('div');
            d.className = 'detail-kv' + (fullWidth ? ' detail-kv-full' : '');
            var s = document.createElement('small');
            s.textContent = label;
            var st = document.createElement('strong');
            st.textContent = value || '—';
            st.style.fontSize = '15px';
            d.appendChild(s);
            d.appendChild(st);
            return d;
        }

        grid.appendChild(kv('JOB ID', job.jobId));
        grid.appendChild(kv('MODULE ORGANISER', job.mdName));
        grid.appendChild(kv('JOB TYPE', job.jobType));
        grid.appendChild(kv('WEEKLY WORKLOAD', job.weeklyWorkload ? job.weeklyWorkload + ' hrs / week' : '—'));
        grid.appendChild(kv('DEADLINE', job.deadline));
        grid.appendChild(kv('PUBLISHED', job.publishedAt ? job.publishedAt.substring(0, 10) : '—'));
        grid.appendChild(kv('REQUIREMENTS', job.requirements, true));
        if (job.introduction) {
            grid.appendChild(kv('INTRODUCTION', job.introduction, true));
        }
        card.appendChild(grid);

        var actions = document.createElement('div');
        actions.className = 'ta-profile-actions';
        actions.style.marginTop = '12px';
        var btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'chip-button active';
        btn.textContent = 'Apply';
        btn.addEventListener('click', function () { openApplyModal(job); });
        actions.appendChild(btn);
        card.appendChild(actions);

        return card;
    }

    /* ---------- Apply modal ---------- */
    function openApplyModal(job) {
        document.getElementById('ta-apply-job-id').value = job.jobId || '';
        document.getElementById('ta-apply-meta').textContent =
            (job.moduleName || '') + ' · ' + (job.jobId || '') + ' · ' + (job.mdName || '');

        // Fill CV status section
        var cvDiv = document.getElementById('ta-apply-cv-current');
        var cvCheck = document.getElementById('ta-apply-cv-check');
        var cvLabel = document.getElementById('ta-apply-cv-label');
        var cvPath = window.TA_CV_PATH || '';
        if (cvDiv) {
            if (cvPath) {
                cvDiv.innerHTML = '<a href="' + contextPath() + '/' + cvPath + '" target="_blank" '
                    + 'class="chip-button" style="text-decoration:none;font-size:12px;">View current CV</a>';
                if (cvCheck) { cvCheck.disabled = false; cvCheck.checked = true; }
                if (cvLabel) cvLabel.style.opacity = '1';
            } else {
                cvDiv.innerHTML = '<span style="color:#c0392b;font-size:13px;">No CV on file.</span>'
                    + ' <a href="' + contextPath() + '/ta/home" style="color:#5870b3;font-size:12px;text-decoration:underline;">Upload on Home page</a>';
                if (cvCheck) { cvCheck.disabled = true; cvCheck.checked = false; }
                if (cvLabel) cvLabel.style.opacity = '0.45';
            }
        }

        var ov = document.getElementById('ta-apply-overlay');
        ov.classList.add('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'false');
    }

    function closeApplyModal() {
        var ov = document.getElementById('ta-apply-overlay');
        ov.classList.remove('ta-feedback-overlay--open');
        ov.setAttribute('aria-hidden', 'true');
    }

    /* ---------- Filter + sort ---------- */
    function applyFilters(jobs) {
        var moduleQ = (document.getElementById('job-search-module').value || '').toLowerCase().trim();
        var moQ = (document.getElementById('job-search-mo').value || '').toLowerCase().trim();
        var sortBy = document.getElementById('job-sort-by').value;

        var filtered = jobs.filter(function (j) {
            if (moduleQ && (j.moduleName || '').toLowerCase().indexOf(moduleQ) < 0) return false;
            if (moQ && (j.mdName || '').toLowerCase().indexOf(moQ) < 0) return false;
            return true;
        });

        filtered.sort(function (a, b) {
            if (sortBy === 'deadline') {
                return (a.deadline || '').localeCompare(b.deadline || '');
            } else if (sortBy === 'published') {
                return (b.publishedAt || '').localeCompare(a.publishedAt || '');
            } else {
                return (a.moduleName || '').localeCompare(b.moduleName || '');
            }
        });

        return filtered;
    }

    function render(jobs) {
        var host = document.getElementById('ta-job-list');
        var empty = document.getElementById('ta-job-empty');
        var countLabel = document.getElementById('job-count-label');
        clearEl(host);

        var filtered = applyFilters(jobs);
        countLabel.textContent = filtered.length + ' position' + (filtered.length === 1 ? '' : 's');

        if (filtered.length === 0) {
            empty.hidden = false;
            return;
        }
        empty.hidden = true;

        filtered.forEach(function (job) {
            host.appendChild(buildJobCard(job));
        });
    }

    /* ---------- Init ---------- */
    function init() {
        var el = document.getElementById('ta-jobs-json');
        if (!el) return;

        var jobs;
        try { jobs = JSON.parse(el.textContent || '[]'); } catch (e) { jobs = []; }

        render(jobs);

        // Wire filter inputs
        document.getElementById('job-search-module').addEventListener('input', function () { render(jobs); });
        document.getElementById('job-search-mo').addEventListener('input', function () { render(jobs); });
        document.getElementById('job-sort-by').addEventListener('change', function () { render(jobs); });

        // Apply modal
        document.getElementById('ta-apply-close').addEventListener('click', closeApplyModal);
        document.getElementById('ta-apply-overlay').addEventListener('click', function (ev) {
            if (ev.target.id === 'ta-apply-overlay') closeApplyModal();
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
