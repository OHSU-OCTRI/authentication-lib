/**
 * Uses the Datatables javascript library to sort any tables with the `sorted` class. By default
 * the first column (index 0) will be used for sorting, and the sort direction is ascending. These
 * defaults can be overridden by providing data attributes of `data-column` and `data-orientation`.
 * See the Datatables library documentation for more options: https://datatables.net
 */
$(function() {
	const column = $(".sorted").attr("data-column") || 0;
	const orientation = $(".sorted").attr("data-orientation") || "asc";

	$(".sorted").DataTable({
		order : [ [ column, orientation ] ],
		paging : true,
		searching : true,
		info : true,
	});
});