(function () {
    'use strict';

    var currentFileUrl = null;

    function contextPath() {
        return window.TA_CONTEXT || '';
    }

    function currentCvPath() {
        return window.TA_CURRENT_CV || '';
    }

    function currentCvUrl() {
        var path = currentCvPath();
        if (!path) return '';
        if (/^https?:\/\//i.test(path)) return path;
        return contextPath() + (path.charAt(0) === '/' ? path : '/' + path);
    }

    function clearEl(el) {
        while (el.firstChild) el.removeChild(el.firstChild);
    }

    function buildJobCard(job, appliedJobIds) {
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

        var title = document.createElement('h2');
        title.textContent = job.moduleName || 'Untitled';

        var badge = document.createElement('span');
        badge.className = 'status success';
        badge.textContent = 'OPEN';

        header.appendChild(title);
        header.appendChild(badge);
        card.appendChild(header);

        var grid = document.createElement('div');
        grid.className = 'detail-kv-grid';

        function kv(label, value, fullWidth) {
            var block = document.createElement('div');
            block.className = 'detail-kv' + (fullWidth ? ' detail-kv-full' : '');

            var small = document.createElement('small');
            small.textContent = label;

            var strong = document.createElement('strong');
            strong.textContent = value || '-';
            strong.style.fontSize = '15px';

            block.appendChild(small);
            block.appendChild(strong);
            return block;
        }

        grid.appendChild(kv('MODULE ORGANISER', job.mdName));
        grid.appendChild(kv('WEEKLY WORKLOAD', job.weeklyWorkload ? job.weeklyWorkload + ' hrs / week' : '-'));
        grid.appendChild(kv('DEADLINE', job.deadline));
        grid.appendChild(kv('PUBLISHED', job.publishedAt ? job.publishedAt.substring(0, 10) : '-'));
        grid.appendChild(kv('REQUIREMENTS', job.requirements, true));
        if (job.introduction) {
            grid.appendChild(kv('INTRODUCTION', job.introduction, true));
        }
        card.appendChild(grid);

        var actions = document.createElement('div');
        actions.className = 'ta-profile-actions';
        actions.style.marginTop = '12px';

        var applied = appliedJobIds.indexOf(job.jobId || '') >= 0;
        var button = document.createElement('button');
        button.type = 'button';
        button.className = 'chip-button' + (applied ? '' : ' active');
        button.textContent = applied ? 'Already applied' : 'Apply';
        button.disabled = applied;
        if (!applied) {
            button.addEventListener('click', function () {
                openApplyModal(job);
            });
        }
        actions.appendChild(button);

        if (applied) {
            var note = document.createElement('span');
            note.style.color = '#7f868f';
            note.style.fontSize = '13px';
            note.textContent = 'Your application for this position is already on record.';
            actions.appendChild(note);
        }

        card.appendChild(actions);
        return card;
    }

    function syncCvState() {
        var fileInput = document.getElementById('ta-apply-cv-input');
        var statusSpan = document.getElementById('ta-apply-cv-status');
        var actionsDiv = document.getElementById('ta-apply-cv-actions');
        var removeBtn = document.getElementById('ta-apply-cv-remove');

        if (!fileInput || !statusSpan || !actionsDiv) {
            return;
        }

        if (fileInput.files && fileInput.files.length > 0) {
            statusSpan.textContent = fileInput.files[0].name;
            statusSpan.style.color = '#3d4148';
            actionsDiv.style.display = 'flex';
            if (removeBtn) removeBtn.style.display = '';
            return;
        }

        if (currentCvPath()) {
            statusSpan.textContent = 'Current CV on file';
            statusSpan.style.color = '#3d4148';
            actionsDiv.style.display = 'flex';
            if (removeBtn) removeBtn.style.display = 'none';
            return;
        }

        statusSpan.textContent = 'No file chosen';
        statusSpan.style.color = '#7f868f';
        actionsDiv.style.display = 'none';
        if (removeBtn) removeBtn.style.display = '';
    }

    function openApplyModal(job) {
        document.getElementById('ta-apply-job-id').value = job.jobId || '';
        document.getElementById('ta-apply-meta').textContent =
            (job.moduleName || '') + ' | ' + (job.jobId || '') + ' | ' + (job.mdName || '');

        var form = document.getElementById('ta-apply-form');
        if (form) form.reset();

        var typeSelect = document.getElementById('ta-apply-type');
        if (typeSelect) typeSelect.selectedIndex = 0;

        if (currentFileUrl) {
            URL.revokeObjectURL(currentFileUrl);
            currentFileUrl = null;
        }

        var marks = form ? form.querySelectorAll('.mandatory-error') : [];
        for (var i = 0; i < marks.length; i++) {
            marks[i].classList.remove('mandatory-error');
            var hint = marks[i].querySelector('.mandatory-hint');
            if (hint) hint.remove();
        }

        syncCvState();

        var overlay = document.getElementById('ta-apply-overlay');
        overlay.classList.add('ta-feedback-overlay--open');
        overlay.setAttribute('aria-hidden', 'false');
    }

    function closeApplyModal() {
        var overlay = document.getElementById('ta-apply-overlay');
        overlay.classList.remove('ta-feedback-overlay--open');
        overlay.setAttribute('aria-hidden', 'true');
    }

    function clearMandatory(field) {
        if (!field) return;
        field.classList.remove('mandatory-error');
        var hint = field.querySelector('.mandatory-hint');
        if (hint) hint.remove();
    }

    function markMandatory(field) {
        if (!field || field.classList.contains('mandatory-error')) return;
        field.classList.add('mandatory-error');
        var hint = document.createElement('span');
        hint.className = 'mandatory-hint';
        hint.textContent = 'Mandatory';
        field.appendChild(hint);
    }

    function initCvPreview() {
        var fileInput = document.getElementById('ta-apply-cv-input');
        var browseBtn = document.getElementById('ta-apply-cv-browse');
        var viewBtn = document.getElementById('ta-apply-cv-view');
        var removeBtn = document.getElementById('ta-apply-cv-remove');
        if (!fileInput || !browseBtn || !viewBtn || !removeBtn) return;

        browseBtn.addEventListener('click', function () {
            fileInput.click();
        });

        fileInput.addEventListener('change', function () {
            if (currentFileUrl) {
                URL.revokeObjectURL(currentFileUrl);
                currentFileUrl = null;
            }
            if (fileInput.files && fileInput.files.length > 0) {
                currentFileUrl = URL.createObjectURL(fileInput.files[0]);
            }
            syncCvState();
        });

        viewBtn.addEventListener('click', function () {
            if (currentFileUrl) {
                window.open(currentFileUrl, '_blank');
                return;
            }
            var existingCv = currentCvUrl();
            if (existingCv) {
                window.open(existingCv, '_blank');
            }
        });

        removeBtn.addEventListener('click', function () {
            fileInput.value = '';
            if (currentFileUrl) {
                URL.revokeObjectURL(currentFileUrl);
                currentFileUrl = null;
            }
            syncCvState();
        });

        syncCvState();
    }

    function initFormValidation() {
        var form = document.getElementById('ta-apply-form');
        if (!form) return;

        var typeSelect = document.getElementById('ta-apply-type');
        var cvInput = document.getElementById('ta-apply-cv-input');

        if (typeSelect) {
            typeSelect.addEventListener('change', function () {
                if (typeSelect.value) {
                    clearMandatory(typeSelect.closest('.filter-field'));
                }
            });
        }

        if (cvInput) {
            cvInput.addEventListener('change', function () {
                if ((cvInput.files && cvInput.files.length > 0) || currentCvPath()) {
                    clearMandatory(document.getElementById('ta-apply-cv-field'));
                }
            });
        }

        form.addEventListener('submit', function (ev) {
            var valid = true;

            var typeField = typeSelect ? typeSelect.closest('.filter-field') : null;
            clearMandatory(typeField);
            if (!typeSelect || !typeSelect.value) {
                markMandatory(typeField);
                valid = false;
            }

            var cvField = document.getElementById('ta-apply-cv-field');
            clearMandatory(cvField);
            if ((!cvInput || !cvInput.files || cvInput.files.length === 0) && !currentCvPath()) {
                markMandatory(cvField);
                valid = false;
            }

            if (!valid) {
                ev.preventDefault();
            }
        });
    }

    function applyFilters(jobs) {
        var moduleQuery = (document.getElementById('job-search-module').value || '').toLowerCase().trim();
        var moQuery = (document.getElementById('job-search-mo').value || '').toLowerCase().trim();
        var sortBy = document.getElementById('job-sort-by').value;

        var filtered = jobs.filter(function (job) {
            if (moduleQuery && (job.moduleName || '').toLowerCase().indexOf(moduleQuery) < 0) return false;
            if (moQuery && (job.mdName || '').toLowerCase().indexOf(moQuery) < 0) return false;
            return true;
        });

        filtered.sort(function (a, b) {
            if (sortBy === 'deadline') {
                return (a.deadline || '').localeCompare(b.deadline || '');
            }
            if (sortBy === 'published') {
                return (b.publishedAt || '').localeCompare(a.publishedAt || '');
            }
            return (a.moduleName || '').localeCompare(b.moduleName || '');
        });

        return filtered;
    }

    function render(jobs, appliedJobIds) {
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
            host.appendChild(buildJobCard(job, appliedJobIds));
        });
    }

    function init() {
        var jobsEl = document.getElementById('ta-jobs-json');
        if (!jobsEl) return;

        var appliedEl = document.getElementById('ta-applied-job-ids-json');
        var jobs;
        var appliedJobIds;
        try {
            jobs = JSON.parse(jobsEl.textContent || '[]');
        } catch (e) {
            jobs = [];
        }
        try {
            appliedJobIds = JSON.parse(appliedEl ? appliedEl.textContent : '[]') || [];
        } catch (e) {
            appliedJobIds = [];
        }

        render(jobs, appliedJobIds);

        document.getElementById('job-search-module').addEventListener('input', function () {
            render(jobs, appliedJobIds);
        });
        document.getElementById('job-search-mo').addEventListener('input', function () {
            render(jobs, appliedJobIds);
        });
        document.getElementById('job-sort-by').addEventListener('change', function () {
            render(jobs, appliedJobIds);
        });

        document.getElementById('ta-apply-close').addEventListener('click', closeApplyModal);
        document.getElementById('ta-apply-overlay').addEventListener('click', function (ev) {
            if (ev.target.id === 'ta-apply-overlay') {
                closeApplyModal();
            }
        });

        initCvPreview();
        initFormValidation();
    }

    document.addEventListener('DOMContentLoaded', init);
})();
