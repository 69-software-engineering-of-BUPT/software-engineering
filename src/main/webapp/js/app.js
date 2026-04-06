console.log('TA Recruitment prototype assets loaded.');

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
	var listCard = document.querySelector('.ad-main .list-card');
	if (!listCard) {
		return;
	}

	Array.prototype.forEach.call(listCard.querySelectorAll('.list-row'), function (row) {
		if (!showUnfilledOnly) {
			row.style.display = '';
			return;
		}
		var vacanciesCell = row.children[5];
		var vacancies = parseInt(normalizeText(vacanciesCell ? vacanciesCell.textContent : '0'), 10);
		row.style.display = vacancies > 0 ? '' : 'none';
	});

	updateVisibleCount(listCard);
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
	row.remove();
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
			showToast('Reminder sent for: ' + rowTitle);
			return;
		case 'project-view':
			showToast('View project: ' + rowTitle);
			return;
		case 'log-details':
			showToast('Showing log details');
			return;
		default:
			return;
	}
}

document.addEventListener('DOMContentLoaded', function () {
	initAccountDetailInteraction();
	initAccountDetailActions();
	initAccountFilterPanel();
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
