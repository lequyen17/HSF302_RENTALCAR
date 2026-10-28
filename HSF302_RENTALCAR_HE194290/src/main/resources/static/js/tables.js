// Khởi tạo DataTables (phân trang, tìm kiếm, sắp xếp phía trình duyệt) cho mọi <table class="datatable">.
// Tùy chọn: data-order-col / data-order-dir trên <table>; th data-orderable="false" cho cột thao tác.
document.querySelectorAll('table.datatable').forEach(function (table) {
	new DataTable(table, {
		pageLength: 10,
		lengthMenu: [5, 10, 25, 50],
		order: [[parseInt(table.dataset.orderCol || '0', 10), table.dataset.orderDir || 'asc']]
	});
});
