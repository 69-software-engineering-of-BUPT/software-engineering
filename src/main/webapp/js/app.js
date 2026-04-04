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
	var cssLink = document.querySelector('link[href$="/css/app.css"]');
	if (!cssLink) {
		return '';
	}
	return cssLink.getAttribute('href').replace(/\/css\/app\.css$/, '');
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
