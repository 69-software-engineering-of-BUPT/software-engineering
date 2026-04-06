console.log('TA Recruitment prototype assets loaded.');

var OP_LOG_STORAGE_KEY = 'taRecruitment.operationLogs';
var PROJECT_VIEW_STORAGE_KEY = 'taRecruitment.selectedProject';

function ensureToast() {
	var toast = document.getElementById('ad-action-toast');
	if (toast) {
		return toast;
	}

	toast = document.createElement('div');
	toast.id = 'ad-action-toast';
	toast.style.position = 'fixed';
	toast.style.right = '20px';
	toast.style.bottom = '20px';
	toast.style.zIndex = '9999';
	toast.style.background = '#2f3742';
	toast.style.color = '#ffffff';
	toast.style.borderRadius = '10px';
	toast.style.padding = '10px 14px';
	toast.style.boxShadow = '0 8px 24px rgba(0,0,0,0.2)';
	toast.style.fontSize = '13px';
	toast.style.opacity = '0';
	toast.style.transform = 'translateY(8px)';
	toast.style.transition = 'opacity .2s ease, transform .2s ease';
	toast.style.pointerEvents = 'none';
	document.body.appendChild(toast);
	return toast;
}

function showToast(message) {
	var toast = ensureToast();
	toast.textContent = message;
	toast.style.opacity = '1';
	toast.style.transform = 'translateY(0)';
	clearTimeout(showToast._timer);
	showToast._timer = setTimeout(function () {
		toast.style.opacity = '0';
		toast.style.transform = 'translateY(8px)';
	}, 1400);
}

function normalizeText(value) {
	return (value || '')
		.replace(/\s+/g, ' ')
		.replace(/\u00a0/g, ' ')
		.trim();
}

function getCellText(cell) {
	if (!cell) {
		return '';
	}

	var buttons = Array.prototype.slice.call(cell.querySelectorAll('button'));
	if (buttons.length > 0) {
		return buttons
			.map(function (button) {
				return normalizeText(button.textContent);
			})
			.join(' / ');
	}

	return normalizeText(cell.textContent);
}

function escapeCsvCell(value) {
	var safe = String(value == null ? '' : value).replace(/"/g, '""');
	return '"' + safe + '"';
}

function buildCsvFromListCard(listCard) {
	if (!listCard) {
		return '';
	}

	var headCells = Array.prototype.slice.call(listCard.querySelectorAll('.list-head > span'));
	var headers = headCells.map(function (cell) {
		return normalizeText(cell.textContent);
	});

	var rows = Array.prototype.slice.call(listCard.querySelectorAll('.list-row'))
		.filter(function (row) {
			return row.style.display !== 'none';
		})
		.map(function (row) {
			return Array.prototype.slice.call(row.children).map(getCellText);
		});

	var csvLines = [];
	if (headers.length > 0) {
		csvLines.push(headers.map(escapeCsvCell).join(','));
	}

	rows.forEach(function (rowValues) {
		csvLines.push(rowValues.map(escapeCsvCell).join(','));
	});

	return csvLines.join('\r\n');
}

function downloadCsv(content, filenamePrefix) {
	var bom = '\uFEFF';
	var blob = new Blob([bom + content], { type: 'text/csv;charset=utf-8;' });
	var url = URL.createObjectURL(blob);
	var link = document.createElement('a');

	link.href = url;
	link.download = (filenamePrefix || 'export') + '-' + new Date().toISOString().slice(0, 10) + '.csv';
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
	URL.revokeObjectURL(url);
}

function formatDateTime(date) {
	var d = date instanceof Date ? date : new Date(date);
	if (isNaN(d.getTime())) {
		d = new Date();
	}
	var year = d.getFullYear();
	var month = String(d.getMonth() + 1).padStart(2, '0');
	var day = String(d.getDate()).padStart(2, '0');
	var hour = String(d.getHours()).padStart(2, '0');
	var minute = String(d.getMinutes()).padStart(2, '0');
	return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
}

function getStoredOperationLogs() {
	try {
		var raw = localStorage.getItem(OP_LOG_STORAGE_KEY);
		if (!raw) {
			return [];
		}
		var parsed = JSON.parse(raw);
		return Array.isArray(parsed) ? parsed : [];
	} catch (error) {
		return [];
	}
}

function setStoredOperationLogs(logs) {
	try {
		localStorage.setItem(OP_LOG_STORAGE_KEY, JSON.stringify(logs || []));
	} catch (error) {
		// ignore storage exceptions in prototype mode
	}
}

function actionLabelFromKey(actionKey) {
	switch (actionKey) {
		case 'account-deleted':
			return 'Account Deleted';
		case 'account-frozen':
			return 'Account Frozen';
		case 'reminder-sent':
			return 'Reminder Sent';
		case 'application-submitted':
			return 'Application Submitted';
		case 'supplement-requested':
			return 'Supplement Requested';
		case 'application-approved':
			return 'Application Approved';
		case 'csv-export':
			return 'CSV Export';
		default:
			return 'Reminder Sent';
	}
}

function resultStyleFromAction(actionKey) {
	if (actionKey === 'account-deleted' || actionKey === 'account-frozen') {
		return { text: 'Warning', css: 'warning' };
	}
	return { text: 'Success', css: 'success' };
}

function recordOperationLog(actionKey, target, options) {
	var opts = options || {};
	var logs = getStoredOperationLogs();
	var now = new Date();
	logs.push({
		id: 'log-' + now.getTime() + '-' + Math.floor(Math.random() * 10000),
		time: formatDateTime(now),
		actor: opts.actor || 'System Admin',
		role: opts.role || 'AD',
		actionKey: actionKey,
		actionLabel: actionLabelFromKey(actionKey),
		target: target || '-',
		result: (opts.result || resultStyleFromAction(actionKey).text),
		resultClass: (opts.resultClass || resultStyleFromAction(actionKey).css)
	});

	if (logs.length > 200) {
		logs = logs.slice(logs.length - 200);
	}
	setStoredOperationLogs(logs);
}

function readLogRowsFromDom() {
	return Array.prototype.slice.call(document.querySelectorAll('.log-row')).map(function (row, index) {
		var cells = row.querySelectorAll('span');
		var statusNode = row.querySelector('.status');
		var resultClass = statusNode && statusNode.classList.contains('warning') ? 'warning' : 'success';
		var resultText = normalizeText(statusNode ? statusNode.textContent : '').replace(/^●\s*/, '') || 'Success';
		return {
			id: 'seed-' + index,
			time: normalizeText(cells[0] ? cells[0].textContent : ''),
			actor: normalizeText(cells[1] ? cells[1].textContent : ''),
			role: (row.dataset.role || 'AD').toUpperCase(),
			actionKey: row.dataset.actionKey || 'reminder-sent',
			actionLabel: normalizeText(cells[2] ? cells[2].textContent : '') || actionLabelFromKey(row.dataset.actionKey),
			target: normalizeText(cells[3] ? cells[3].textContent : ''),
			result: resultText,
			resultClass: resultClass
		};
	});
}

function ensureOperationLogSeeded() {
	if (document.querySelectorAll('.log-row').length === 0) {
		return;
	}
	var stored = getStoredOperationLogs();
	if (stored.length > 0) {
		return;
	}
	setStoredOperationLogs(readLogRowsFromDom());
}

function createLogRowNode(entry) {
	var row = document.createElement('article');
	var isWarn = entry.resultClass === 'warning';
	row.className = 'list-row log-grid log-row' + (isWarn ? ' warn' : '');
	row.dataset.role = (entry.role || 'AD').toUpperCase();
	row.dataset.actionKey = entry.actionKey || 'reminder-sent';
	row.dataset.time = entry.time || formatDateTime(new Date());

	row.innerHTML =
		'<span>' + (entry.time || '-') + '</span>' +
		'<span>' + (entry.actor || 'System Admin') + '</span>' +
		'<span>' + (entry.actionLabel || actionLabelFromKey(entry.actionKey)) + '</span>' +
		'<span>' + (entry.target || '-') + '</span>' +
		'<span class="status ' + (isWarn ? 'warning' : 'success') + '">● ' + (entry.result || (isWarn ? 'Warning' : 'Success')) + '</span>' +
		'<div class="row-actions"><button data-action="log-details">Details</button></div>';

	return row;
}

function renderOperationLogRows() {
	var logListCard = document.querySelector('.ad-main .list-card');
	if (!logListCard || document.querySelectorAll('.log-row').length === 0) {
		return;
	}

	var container = logListCard;
	Array.prototype.slice.call(container.querySelectorAll('.log-row')).forEach(function (row) {
		row.remove();
	});

	var entries = getStoredOperationLogs().slice().sort(function (a, b) {
		var ta = parseLogDate(a.time);
		var tb = parseLogDate(b.time);
		return (tb ? tb.getTime() : 0) - (ta ? ta.getTime() : 0);
	});

	entries.forEach(function (entry) {
		container.appendChild(createLogRowNode(entry));
	});
}

function getContextPath() {
	var cssLink = document.querySelector('link[href*="/css/app.css"]');
	if (!cssLink) {
		return '';
	}
	return cssLink.getAttribute('href').replace(/\/css\/app\.css(\?.*)?$/, '');
}

function setActiveFilter(button) {
	var group = button && button.closest('.filter-actions');
	if (!group) {
		return;
	}

	Array.prototype.forEach.call(group.querySelectorAll('.chip-button'), function (item) {
		item.classList.remove('active');
	});
	button.classList.add('active');
}

function updateVisibleCount(listCard) {
	if (!listCard) {
		return;
	}
	var countNode = listCard.querySelector('.list-title-row span');
	if (!countNode) {
		return;
	}
	var visibleCount = Array.prototype.filter.call(listCard.querySelectorAll('.list-row'), function (row) {
		return row.style.display !== 'none';
	}).length;
	countNode.textContent = visibleCount + ' item(s)';
}

function filterProjectRows(showUnfilledOnly) {
	var listCard = document.querySelector('.project-list-card');
	if (!listCard) {
		return;
	}

	Array.prototype.forEach.call(listCard.querySelectorAll('.project-row'), function (row) {
		if (!showUnfilledOnly) {
			row.style.display = '';
			return;
		}
		var vacanciesCell = row.children[5];
		var vacancies = parseInt(normalizeText(vacanciesCell ? vacanciesCell.textContent : '0'), 10);
		row.style.display = vacancies > 0 ? '' : 'none';
	});

	updateVisibleCount(listCard);
	ensureVisibleProjectSelection();
}

function parseProjectTaList(raw) {
	if (!raw) {
		return [];
	}

	return raw
		.split(';')
		.map(function (item) {
			var parts = item.split('|');
			return {
				name: normalizeText(parts[0]),
				email: normalizeText(parts[1]),
				note: normalizeText(parts[2])
			};
		})
		.filter(function (item) {
			return item.name;
		});
}

function buildProjectPayloadFromRow(row) {
	if (!row) {
		return null;
	}
	var data = row.dataset || {};
	return {
		module: data.module || '',
		moduleCode: data.moduleCode || '',
		mo: data.mo || '',
		posted: data.posted || '',
		deadline: data.deadline || '',
		seats: data.seats || '',
		filled: data.filled || '',
		vacancies: data.vacancies || '',
		statusText: data.statusText || '',
		statusClass: data.statusClass || 'warning',
		liveDays: data.liveDays || '',
		requirements: data.requirements || '',
		details: data.details || '',
		approvedTas: data.approvedTas || '',
		pendingTas: data.pendingTas || ''
	};
}

function setSelectedProjectForView(payload) {
	if (!payload) {
		return;
	}
	try {
		localStorage.setItem(PROJECT_VIEW_STORAGE_KEY, JSON.stringify(payload));
	} catch (error) {
		// ignore storage exceptions in prototype mode
	}
}

function getSelectedProjectForView() {
	try {
		var raw = localStorage.getItem(PROJECT_VIEW_STORAGE_KEY);
		if (!raw) {
			return null;
		}
		var parsed = JSON.parse(raw);
		return parsed && typeof parsed === 'object' ? parsed : null;
	} catch (error) {
		return null;
	}
}

function renderProjectViewData(payload) {
	if (!payload) {
		return;
	}

	var nameNode = document.getElementById('project-view-name');
	var codeNode = document.getElementById('project-view-code');
	var statusNode = document.getElementById('project-view-status');
	var moNode = document.getElementById('project-view-mo');
	var postedNode = document.getElementById('project-view-posted');
	var deadlineNode = document.getElementById('project-view-deadline');
	var capacityNode = document.getElementById('project-view-capacity');
	var reqNode = document.getElementById('project-view-req');
	var descNode = document.getElementById('project-view-desc');

	if (!nameNode || !codeNode || !statusNode || !moNode || !postedNode || !deadlineNode || !capacityNode || !reqNode || !descNode) {
		return;
	}

	nameNode.textContent = payload.module || '-';
	codeNode.textContent = (payload.moduleCode || '-') + ' · ' + (payload.liveDays || '0') + ' day(s) live';
	statusNode.textContent = '● ' + (payload.statusText || 'Action Needed');
	statusNode.classList.remove('success', 'warning');
	statusNode.classList.add(payload.statusClass === 'success' ? 'success' : 'warning');
	moNode.textContent = payload.mo || '-';
	postedNode.textContent = payload.posted || '-';
	deadlineNode.textContent = payload.deadline || '-';
	capacityNode.textContent = (payload.seats || '0') + ' / ' + (payload.filled || '0') + ' / ' + (payload.vacancies || '0');
	reqNode.textContent = (payload.requirements || '-').split(';').join(' · ');
	descNode.textContent = payload.details || '-';

	renderProjectTaList('project-approved-ta-list', parseProjectTaList(payload.approvedTas));
	renderProjectTaList('project-pending-ta-list', parseProjectTaList(payload.pendingTas));
}

function initProjectViewPage() {
	if (!document.getElementById('project-view-page')) {
		return;
	}
	renderProjectViewData(getSelectedProjectForView());
}

function renderProjectTaList(containerId, list) {
	var listNode = document.getElementById(containerId);
	if (!listNode) {
		return;
	}

	listNode.innerHTML = '';
	if (!list || list.length === 0) {
		listNode.innerHTML = '<li><span>No TA in this section</span></li>';
		return;
	}

	list.forEach(function (item) {
		var li = document.createElement('li');
		var right = [item.email, item.note].filter(Boolean).join(' · ');
		li.innerHTML = '<span>' + item.name + '</span><small>' + right + '</small>';
		listNode.appendChild(li);
	});
}

function renderProjectDetail(row) {
	if (!row) {
		return;
	}

	var data = row.dataset;
	var nameNode = document.getElementById('project-detail-name');
	var codeNode = document.getElementById('project-detail-code');
	var statusNode = document.getElementById('project-detail-status');
	var moNode = document.getElementById('project-detail-mo');
	var postedNode = document.getElementById('project-detail-posted');
	var deadlineNode = document.getElementById('project-detail-deadline');
	var capacityNode = document.getElementById('project-detail-capacity');
	var reqNode = document.getElementById('project-detail-req');
	var descNode = document.getElementById('project-detail-desc');

	if (!nameNode || !codeNode || !statusNode || !moNode || !postedNode || !deadlineNode || !capacityNode || !reqNode || !descNode) {
		return;
	}

	nameNode.textContent = data.module || '-';
	codeNode.textContent = (data.moduleCode || '-') + ' · ' + (data.liveDays || '0') + ' day(s) live';
	statusNode.textContent = '● ' + (data.statusText || 'Action Needed');
	statusNode.classList.remove('success', 'warning');
	statusNode.classList.add(data.statusClass === 'success' ? 'success' : 'warning');
	moNode.textContent = data.mo || '-';
	postedNode.textContent = data.posted || '-';
	deadlineNode.textContent = data.deadline || '-';
	capacityNode.textContent = (data.seats || '0') + ' / ' + (data.filled || '0') + ' / ' + (data.vacancies || '0');
	reqNode.textContent = (data.requirements || '-').split(';').join(' · ');
	descNode.textContent = data.details || '-';

	renderProjectTaList('project-approved-ta-list', parseProjectTaList(data.approvedTas));
	renderProjectTaList('project-pending-ta-list', parseProjectTaList(data.pendingTas));
}

function selectProjectRow(row) {
	var rows = Array.prototype.slice.call(document.querySelectorAll('.project-row'));
	rows.forEach(function (item) {
		item.classList.remove('active');
	});

	if (!row) {
		return;
	}

	row.classList.add('active');
	renderProjectDetail(row);
}

function ensureVisibleProjectSelection() {
	var rows = Array.prototype.slice.call(document.querySelectorAll('.project-row'));
	if (rows.length === 0) {
		return;
	}

	var activeRow = document.querySelector('.project-row.active');
	if (activeRow && activeRow.style.display !== 'none') {
		renderProjectDetail(activeRow);
		return;
	}

	var firstVisible = rows.find(function (row) {
		return row.style.display !== 'none';
	});
	selectProjectRow(firstVisible || null);
}

function initProjectDetailPanel() {
	if (!document.getElementById('project-detail-panel')) {
		return;
	}

	var rows = Array.prototype.slice.call(document.querySelectorAll('.project-row'));
	if (rows.length === 0) {
		return;
	}

	rows.forEach(function (row) {
		row.addEventListener('click', function (event) {
			if (event.target.closest('[data-action="project-view"]') || event.target.closest('[data-action="project-remind"]')) {
				return;
			}
			selectProjectRow(row);
		});
	});

	selectProjectRow(document.querySelector('.project-row.active') || rows[0]);
}

function filterLogRows(showRiskOnly) {
	var listCard = document.querySelector('.ad-main .list-card');
	if (!listCard) {
		return;
	}

	Array.prototype.forEach.call(listCard.querySelectorAll('.list-row'), function (row) {
		if (!showRiskOnly) {
			row.style.display = '';
			return;
		}
		var isRisk = row.classList.contains('warn');
		row.style.display = isRisk ? '' : 'none';
	});

	updateVisibleCount(listCard);
}

function parseAccountAssignments(raw) {
	if (!raw) {
		return [];
	}

	return raw
		.split(';')
		.map(function (item) {
			var parts = item.split('|');
			return {
				course: normalizeText(parts[0]),
				teacher: normalizeText(parts[1]),
				date: normalizeText(parts[2])
			};
		})
		.filter(function (item) {
			return item.course;
		});
}

function renderAccountDetail(row) {
	if (!row) {
		return;
	}

	var nameNode = document.getElementById('detail-name');
	var emailNode = document.getElementById('detail-email');
	var roleNode = document.getElementById('detail-role');
	var deptNode = document.getElementById('detail-department');
	var loadNode = document.getElementById('detail-load');
	var lastLoginNode = document.getElementById('detail-last-login');
	var flagNode = document.getElementById('detail-flag');
	var badgeNode = document.getElementById('detail-flag-badge');
	var assignmentListNode = document.getElementById('detail-assignment-list');

	if (!nameNode || !emailNode || !roleNode || !deptNode || !loadNode || !lastLoginNode || !flagNode || !badgeNode || !assignmentListNode) {
		return;
	}

	var data = row.dataset;
	var statusClass = data.statusClass === 'warning' ? 'warning' : 'success';

	nameNode.textContent = data.name || '-';
	emailNode.textContent = data.email || '-';
	roleNode.textContent = data.role || '-';
	deptNode.textContent = data.department || '-';
	loadNode.textContent = data.load || '-';
	lastLoginNode.textContent = data.lastLogin || '-';
	flagNode.textContent = data.flag || '-';
	badgeNode.textContent = '● ' + (data.statusText || 'Active');
	badgeNode.classList.remove('success', 'warning');
	badgeNode.classList.add(statusClass);

	var assignments = parseAccountAssignments(data.assignments || '');
	assignmentListNode.innerHTML = '';

	if (assignments.length === 0) {
		var emptyItem = document.createElement('li');
		emptyItem.innerHTML = '<span>No active assignments</span>';
		assignmentListNode.appendChild(emptyItem);
		return;
	}

	assignments.forEach(function (item) {
		var li = document.createElement('li');
		var rightMeta = [item.teacher, item.date].filter(Boolean).join(' · ');
		li.innerHTML = '<span>' + item.course + '</span><small>' + rightMeta + '</small>';
		assignmentListNode.appendChild(li);
	});
}

function initAccountDetailInteraction() {
	var rows = Array.prototype.slice.call(document.querySelectorAll('.account-row'));
	if (rows.length === 0 || !document.getElementById('account-detail-panel')) {
		return;
	}

	rows.forEach(function (row) {
		row.addEventListener('click', function () {
			selectAccountRow(row);
		});

		row.addEventListener('keydown', function (event) {
			if (event.key === 'Enter' || event.key === ' ') {
				event.preventDefault();
				selectAccountRow(row);
			}
		});
	});

	var initial = document.querySelector('.account-row.active') || rows[0];
	if (initial) {
		selectAccountRow(initial);
	}
}

function getAccountRows() {
	return Array.prototype.slice.call(document.querySelectorAll('.account-row'));
}

function isUpperLimitReached(row) {
	if (!row) {
		return false;
	}

	var flagText = normalizeText((row.dataset.flag || '').toLowerCase());
	if (flagText.indexOf('upper limit') >= 0) {
		return true;
	}

	var loadText = normalizeText(row.dataset.load || '');
	var match = loadText.match(/(\d+)\s*\/\s*(\d+)/);
	if (!match) {
		return false;
	}

	var current = parseInt(match[1], 10);
	var upper = parseInt(match[2], 10);
	return !isNaN(current) && !isNaN(upper) && upper > 0 && current >= upper;
}

function getAccountStatusKey(row) {
	if (!row) {
		return 'unknown';
	}

	if (isUpperLimitReached(row)) {
		return 'upper-limit';
	}

	var statusText = normalizeText((row.dataset.statusText || '').toLowerCase());
	if (statusText === 'active') {
		return 'active';
	}
	if (statusText === 'pending') {
		return 'pending';
	}
	if (statusText === 'warning') {
		return 'warning';
	}

	return statusText || 'unknown';
}

function renderEmptyAccountDetail() {
	var nameNode = document.getElementById('detail-name');
	var emailNode = document.getElementById('detail-email');
	var roleNode = document.getElementById('detail-role');
	var deptNode = document.getElementById('detail-department');
	var loadNode = document.getElementById('detail-load');
	var lastLoginNode = document.getElementById('detail-last-login');
	var flagNode = document.getElementById('detail-flag');
	var badgeNode = document.getElementById('detail-flag-badge');
	var assignmentListNode = document.getElementById('detail-assignment-list');

	if (!nameNode || !emailNode || !roleNode || !deptNode || !loadNode || !lastLoginNode || !flagNode || !badgeNode || !assignmentListNode) {
		return;
	}

	nameNode.textContent = 'No account selected';
	emailNode.textContent = '-';
	roleNode.textContent = '-';
	deptNode.textContent = '-';
	loadNode.textContent = '-';
	lastLoginNode.textContent = '-';
	flagNode.textContent = '-';
	badgeNode.textContent = '● -';
	badgeNode.classList.remove('success', 'warning');
	assignmentListNode.innerHTML = '<li><span>No matching account</span></li>';
	updateAccountActionButtonsState(null);
}

function selectAccountRow(row) {
	var rows = getAccountRows();
	rows.forEach(function (item) {
		item.classList.remove('active');
	});

	if (!row) {
		renderEmptyAccountDetail();
		return;
	}

	row.classList.add('active');
	renderAccountDetail(row);
	updateAccountActionButtonsState(row);
}

function ensureVisibleAccountSelection() {
	var rows = getAccountRows();
	if (rows.length === 0) {
		return;
	}

	var activeRow = document.querySelector('.account-row.active');
	if (activeRow && activeRow.style.display !== 'none') {
		renderAccountDetail(activeRow);
		return;
	}

	var firstVisible = rows.find(function (row) {
		return row.style.display !== 'none';
	});
	selectAccountRow(firstVisible || null);
}

function updateAccountFilterMeta(visibleRows) {
	var summaryNode = document.getElementById('account-filter-summary');
	if (summaryNode) {
		summaryNode.textContent = visibleRows.length + ' account(s) match the current filters';
	}

	var upperLimitNode = document.getElementById('account-upper-limit-count');
	if (upperLimitNode) {
		var upperCount = visibleRows.filter(isUpperLimitReached).length;
		upperLimitNode.textContent = String(upperCount);
	}
}

function applyAccountFilters(showAppliedToast) {
	var searchInput = document.getElementById('account-filter-search');
	var roleSelect = document.getElementById('account-filter-role');
	var statusSelect = document.getElementById('account-filter-status');
	var listCard = document.querySelector('.account-list-card');
	var rows = getAccountRows();

	if (!searchInput || !roleSelect || !statusSelect || !listCard || rows.length === 0) {
		return;
	}

	var keyword = normalizeText(searchInput.value).toLowerCase();
	var role = roleSelect.value;
	var status = statusSelect.value;

	rows.forEach(function (row) {
		var dataset = row.dataset;
		var searchHaystack = [dataset.name, dataset.email, dataset.department, dataset.flag].join(' ').toLowerCase();
		var roleMatched = role === 'all' || (dataset.role || '').toUpperCase() === role;
		var statusMatched = status === 'all' || getAccountStatusKey(row) === status;
		var searchMatched = !keyword || searchHaystack.indexOf(keyword) >= 0;
		var shouldShow = roleMatched && statusMatched && searchMatched;

		row.style.display = shouldShow ? '' : 'none';
	});

	var visibleRows = rows.filter(function (row) {
		return row.style.display !== 'none';
	});

	updateVisibleCount(listCard);
	updateAccountFilterMeta(visibleRows);
	ensureVisibleAccountSelection();

	if (showAppliedToast) {
		showToast('Filters applied: ' + visibleRows.length + ' account(s)');
	}
}

function initAccountFilterPanel() {
	var applyButton = document.getElementById('account-filter-apply');
	var clearButton = document.getElementById('account-filter-clear');
	var searchInput = document.getElementById('account-filter-search');
	var roleSelect = document.getElementById('account-filter-role');
	var statusSelect = document.getElementById('account-filter-status');

	if (!applyButton || !clearButton || !searchInput || !roleSelect || !statusSelect) {
		return;
	}

	applyButton.addEventListener('click', function () {
		applyAccountFilters(true);
	});

	clearButton.addEventListener('click', function () {
		searchInput.value = '';
		roleSelect.value = 'all';
		statusSelect.value = 'all';
		applyAccountFilters(false);
		showToast('Filters cleared');
	});

	searchInput.addEventListener('keydown', function (event) {
		if (event.key === 'Enter') {
			event.preventDefault();
			applyAccountFilters(true);
		}
	});

	roleSelect.addEventListener('change', function () {
		applyAccountFilters(false);
	});

	statusSelect.addEventListener('change', function () {
		applyAccountFilters(false);
	});

	applyAccountFilters(false);
}

function updateAccountActionButtonsState(row) {
	var freezeBtn = document.getElementById('account-freeze-btn');
	var unfreezeBtn = document.getElementById('account-unfreeze-btn');
	var deleteBtn = document.getElementById('account-delete-btn');
	if (!freezeBtn || !unfreezeBtn || !deleteBtn) {
		return;
	}

	if (!row) {
		freezeBtn.disabled = true;
		unfreezeBtn.disabled = true;
		deleteBtn.disabled = true;
		return;
	}

	var locked = row.dataset.locked === 'true';
	freezeBtn.disabled = locked;
	unfreezeBtn.disabled = !locked;
	deleteBtn.disabled = false;
}

function updateAccountRowStatusView(row) {
	if (!row) {
		return;
	}

	if (row.dataset.statusClass === 'warning') {
		row.classList.add('warn');
	} else {
		row.classList.remove('warn');
	}

	var statusNode = row.querySelector('.status');
	if (!statusNode) {
		return;
	}

	statusNode.classList.remove('success', 'warning');
	statusNode.classList.add(row.dataset.statusClass === 'warning' ? 'warning' : 'success');
	statusNode.textContent = '● ' + (row.dataset.statusText || 'Active');
}

function freezeSelectedAccount() {
	var row = document.querySelector('.account-row.active');
	if (!row) {
		showToast('No account selected');
		return;
	}

	if (row.dataset.locked === 'true') {
		showToast('Account is already locked');
		return;
	}

	row.dataset.prevStatusText = row.dataset.statusText || 'Active';
	row.dataset.prevStatusClass = row.dataset.statusClass || 'success';
	row.dataset.prevFlag = row.dataset.flag || '-';
	row.dataset.locked = 'true';
	row.dataset.statusText = 'Warning';
	row.dataset.statusClass = 'warning';
	row.dataset.flag = 'Account locked by administrator';
	updateAccountRowStatusView(row);
	renderAccountDetail(row);
	updateAccountActionButtonsState(row);
	recordOperationLog('account-frozen', row.dataset.email || row.dataset.name || '-');
	applyAccountFilters(false);
	showToast('Account locked: ' + (row.dataset.name || '')); 
}

function unfreezeSelectedAccount() {
	var row = document.querySelector('.account-row.active');
	if (!row) {
		showToast('No account selected');
		return;
	}

	if (row.dataset.locked !== 'true') {
		showToast('Account is not locked');
		return;
	}

	row.dataset.locked = 'false';
	row.dataset.statusText = row.dataset.prevStatusText || 'Active';
	row.dataset.statusClass = row.dataset.prevStatusClass || 'success';
	row.dataset.flag = row.dataset.prevFlag || '-';
	delete row.dataset.prevStatusText;
	delete row.dataset.prevStatusClass;
	delete row.dataset.prevFlag;

	updateAccountRowStatusView(row);
	renderAccountDetail(row);
	updateAccountActionButtonsState(row);
	applyAccountFilters(false);
	showToast('Account unlocked: ' + (row.dataset.name || ''));
}

function deleteSelectedAccount() {
	var row = document.querySelector('.account-row.active');
	if (!row) {
		showToast('No account selected');
		return;
	}

	var deletedName = row.dataset.name || 'Selected account';
	var deletedTarget = row.dataset.email || deletedName;
	row.remove();
	recordOperationLog('account-deleted', deletedTarget);
	applyAccountFilters(false);
	showToast('Account deleted: ' + deletedName);
}

function initAccountDetailActions() {
	var freezeBtn = document.getElementById('account-freeze-btn');
	var unfreezeBtn = document.getElementById('account-unfreeze-btn');
	var deleteBtn = document.getElementById('account-delete-btn');
	if (!freezeBtn || !unfreezeBtn || !deleteBtn) {
		return;
	}

	freezeBtn.addEventListener('click', freezeSelectedAccount);
	unfreezeBtn.addEventListener('click', unfreezeSelectedAccount);
	deleteBtn.addEventListener('click', deleteSelectedAccount);

	updateAccountActionButtonsState(document.querySelector('.account-row.active'));
}

function getLogRows() {
	return Array.prototype.slice.call(document.querySelectorAll('.log-row'));
}

function parseLogDate(value) {
	if (!value) {
		return null;
	}
	var normalized = String(value).trim().replace(' ', 'T');
	var parsed = new Date(normalized);
	return isNaN(parsed.getTime()) ? null : parsed;
}

function isLogInRange(row, rangeValue) {
	if (rangeValue === 'all' || rangeValue === 'current-sprint') {
		return true;
	}

	var logDate = parseLogDate(row && row.dataset ? row.dataset.time : '');
	if (!logDate) {
		return false;
	}

	var now = new Date();
	var todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate());

	if (rangeValue === 'today') {
		return logDate >= todayStart;
	}

	if (rangeValue === 'last-7-days') {
		var last7Days = new Date(todayStart);
		last7Days.setDate(last7Days.getDate() - 6);
		return logDate >= last7Days;
	}

	return true;
}

function applyLogFilters(showAppliedToast) {
	var roleSelect = document.getElementById('log-filter-role');
	var actionSelect = document.getElementById('log-filter-action');
	var rangeSelect = document.getElementById('log-filter-range');
	var listCard = document.querySelector('.ad-main .list-card');
	var rows = getLogRows();

	if (!roleSelect || !actionSelect || !rangeSelect || !listCard || rows.length === 0) {
		return;
	}

	var role = roleSelect.value;
	var actionKey = actionSelect.value;
	var rangeValue = rangeSelect.value;

	rows.forEach(function (row) {
		var rowRole = (row.dataset.role || '').toUpperCase();
		var rowAction = row.dataset.actionKey || '';
		var roleMatched = role === 'all' || rowRole === role;
		var actionMatched = actionKey === 'all' || rowAction === actionKey;
		var rangeMatched = isLogInRange(row, rangeValue);
		var shouldShow = roleMatched && actionMatched && rangeMatched;

		row.style.display = shouldShow ? '' : 'none';
	});

	var visibleRows = rows.filter(function (row) {
		return row.style.display !== 'none';
	});

	updateVisibleCount(listCard);

	var summaryNode = document.getElementById('log-filter-summary');
	if (summaryNode) {
		summaryNode.textContent = visibleRows.length + ' log entry or entries match the current filters';
	}

	var countNode = document.getElementById('log-filter-entry-count');
	if (countNode) {
		countNode.textContent = String(visibleRows.length);
	}

	if (showAppliedToast) {
		showToast('Log filters applied: ' + visibleRows.length + ' item(s)');
	}
}

function initLogFilterPanel() {
	var roleSelect = document.getElementById('log-filter-role');
	var actionSelect = document.getElementById('log-filter-action');
	var rangeSelect = document.getElementById('log-filter-range');
	var applyButton = document.getElementById('log-filter-apply');
	var clearButton = document.getElementById('log-filter-clear');

	if (!roleSelect || !actionSelect || !rangeSelect || !applyButton || !clearButton) {
		return;
	}

	applyButton.addEventListener('click', function () {
		applyLogFilters(true);
	});

	clearButton.addEventListener('click', function () {
		roleSelect.value = 'all';
		actionSelect.value = 'all';
		rangeSelect.value = 'current-sprint';
		applyLogFilters(false);
		showToast('Log filters cleared');
	});

	roleSelect.addEventListener('change', function () {
		applyLogFilters(false);
	});
	actionSelect.addEventListener('change', function () {
		applyLogFilters(false);
	});
	rangeSelect.addEventListener('change', function () {
		applyLogFilters(false);
	});

	applyLogFilters(false);
}

function handleCommonAction(action, button) {
	var contextPath = getContextPath();
	var row = button ? button.closest('.list-row') : null;
	var rowTitle = row ? normalizeText((row.querySelector('strong') || row.children[0]).textContent) : '';

	switch (action) {
		case 'switch-role':
			showToast('Switching role...');
			window.location.href = contextPath + '/jsp/role-select.jsp';
			return;
		case 'reset-demo':
			showToast('Returning to login...');
			window.location.href = contextPath + '/jsp/login.jsp';
			return;
		case 'import-csv':
			showToast('Import feature is in progress');
			return;
		case 'account-edit':
			showToast('Edit account: ' + rowTitle);
			return;
		case 'account-disable':
			showToast('Disable account: ' + rowTitle);
			return;
		case 'account-approve':
			showToast('Approved account: ' + rowTitle);
			return;
		case 'account-reject':
			showToast('Rejected account: ' + rowTitle);
			return;
		case 'project-remind':
			recordOperationLog('reminder-sent', rowTitle || 'Project');
			showToast('Reminder sent for: ' + rowTitle);
			return;
		case 'project-view':
			setSelectedProjectForView(buildProjectPayloadFromRow(row));
			window.location.href = contextPath + '/jsp/ad/project-view.jsp';
			return;
		case 'log-details':
			showToast('Showing log details');
			return;
		default:
			return;
	}
}

document.addEventListener('DOMContentLoaded', function () {
	ensureOperationLogSeeded();
	renderOperationLogRows();

	initAccountDetailInteraction();
	initAccountDetailActions();
	initAccountFilterPanel();
	initProjectDetailPanel();
	initProjectViewPage();
	initLogFilterPanel();

	var exportButtons = document.querySelectorAll('[data-export-csv="true"]');

	Array.prototype.forEach.call(exportButtons, function (button) {
		button.addEventListener('click', function () {
			var pageMain = button.closest('.ad-main');
			var listCard = pageMain ? pageMain.querySelector('.list-card') : null;
			var csv = buildCsvFromListCard(listCard);

			if (!csv) {
				showToast('No table data found to export.');
				return;
			}

			downloadCsv(csv, button.getAttribute('data-export-filename'));
			recordOperationLog('csv-export', (button.getAttribute('data-export-filename') || 'list') + ' list');
			renderOperationLogRows();
			showToast('CSV exported successfully');
		});
	});

	document.addEventListener('click', function (event) {
		var filterButton = event.target.closest('[data-filter]');
		if (filterButton) {
			setActiveFilter(filterButton);

			switch (filterButton.getAttribute('data-filter')) {
				case 'projects-all':
					filterProjectRows(false);
						showToast('Showing all positions');
					break;
				case 'projects-unfilled':
					filterProjectRows(true);
						showToast('Showing unfilled positions');
					break;
				case 'logs-all':
					filterLogRows(false);
						showToast('Showing all actions');
					break;
				case 'logs-risk':
					filterLogRows(true);
						showToast('Showing risk events');
					break;
					case 'accounts-all':
						showToast('Showing all accounts');
						break;
				default:
					break;
			}
			return;
		}

		var actionButton = event.target.closest('[data-action]');
		if (actionButton) {
			handleCommonAction(actionButton.getAttribute('data-action'), actionButton);
		}
	});
});
