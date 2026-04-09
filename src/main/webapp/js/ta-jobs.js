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

        grid.appendChild(kv('MODULE ORGANISER', job.mdName));
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

        // Reset form
        var form = document.getElementById('ta-apply-form');
        if (form) form.reset();
        var typeSelect = document.getElementById('ta-apply-type');
        if (typeSelect) typeSelect.selectedIndex = 0;

        // Reset CV state
        var cvStatus = document.getElementById('ta-apply-cv-status');
        var cvActions = document.getElementById('ta-apply-cv-actions');
        if (cvStatus) { cvStatus.textContent = 'No file chosen'; cvStatus.style.color = '#7f868f'; }
        if (cvActions) cvActions.style.display = 'none';
        if (_cvObjectUrl) { URL.revokeObjectURL(_cvObjectUrl); _cvObjectUrl = null; }

        // Clear mandatory marks
        var marks = form ? form.querySelectorAll('.mandatory-error') : [];
        for (var i = 0; i < marks.length; i++) {
            marks[i].classList.remove('mandatory-error');
            var h = marks[i].querySelector('.mandatory-hint');
            if (h) h.remove();
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

    /* ---------- CV preview / remove / view ---------- */
    var _cvObjectUrl = null;

    function initCvPreview() {
        var fileInput = document.getElementById('ta-apply-cv-input');
        var browseBtn = document.getElementById('ta-apply-cv-browse');
        var statusSpan = document.getElementById('ta-apply-cv-status');
        var actionsDiv = document.getElementById('ta-apply-cv-actions');
        var viewBtn = document.getElementById('ta-apply-cv-view');
        var removeBtn = document.getElementById('ta-apply-cv-remove');
        if (!fileInput || !browseBtn) return;

        browseBtn.addEventListener('click', function () {
            fileInput.click();
        });

        fileInput.addEventListener('change', function () {
            if (fileInput.files && fileInput.files.length > 0) {
                var file = fileInput.files[0];
                statusSpan.textContent = file.name;
                statusSpan.style.color = '#3d4148';
                actionsDiv.style.display = 'flex';
                if (_cvObjectUrl) URL.revokeObjectURL(_cvObjectUrl);
                _cvObjectUrl = URL.createObjectURL(file);
            } else {
                resetCvState();
            }
        });

        viewBtn.addEventListener('click', function () {
            if (_cvObjectUrl) {
                window.open(_cvObjectUrl, '_blank');
            }
        });

        removeBtn.addEventListener('click', function () {
            fileInput.value = '';
            resetCvState();
        });

        function resetCvState() {
            statusSpan.textContent = 'No file chosen';
            statusSpan.style.color = '#7f868f';
            actionsDiv.style.display = 'none';
            if (_cvObjectUrl) { URL.revokeObjectURL(_cvObjectUrl); _cvObjectUrl = null; }
        }
    }

    /* ---------- Mandatory field helpers ---------- */
    function clearMandatory(field) {
        field.classList.remove('mandatory-error');
        var hint = field.querySelector('.mandatory-hint');
        if (hint) hint.remove();
    }

    function markMandatory(field) {
        if (field.classList.contains('mandatory-error')) return;
        field.classList.add('mandatory-error');
        var hint = document.createElement('span');
        hint.className = 'mandatory-hint';
        hint.textContent = 'Mandatory';
        field.appendChild(hint);
    }

    /* ---------- Form validation ---------- */
    function initFormValidation() {
        var form = document.getElementById('ta-apply-form');
        if (!form) return;

        // Real-time: clear mandatory when user fills in
        var typeSelect = document.getElementById('ta-apply-type');
        if (typeSelect) {
            typeSelect.addEventListener('change', function () {
                if (typeSelect.value) {
                    var f = typeSelect.closest('.filter-field');
                    if (f) clearMandatory(f);
                }
            });
        }
        var cvInput = document.getElementById('ta-apply-cv-input');
        if (cvInput) {
            cvInput.addEventListener('change', function () {
                if (cvInput.files && cvInput.files.length > 0) {
                    var f = document.getElementById('ta-apply-cv-field');
                    if (f) clearMandatory(f);
                }
            });
        }

        form.addEventListener('submit', function (ev) {
            var valid = true;

            // Application type
            var typeField = document.getElementById('ta-apply-type').closest('.filter-field');
            clearMandatory(typeField);
            if (!typeSelect.value) {
                markMandatory(typeField);
                valid = false;
            }

            // CV file
            var cvField = document.getElementById('ta-apply-cv-field');
            clearMandatory(cvField);
            if (!cvInput.files || cvInput.files.length === 0) {
                markMandatory(cvField);
                valid = false;
            }

            if (!valid) {
                ev.preventDefault();
            }
        });
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

        initCvPreview();
        initFormValidation();
    }

    document.addEventListener('DOMContentLoaded', init);
})();
