document.addEventListener('DOMContentLoaded', function () {
	// lấy table body để thay đổi
	var tableBody = document.getElementById('my-table-data');
	// xử lý request lên server.
	var xmlHttpRequest = new XMLHttpRequest();
	// sự kiện khi request thay đổi trạng thái
	xmlHttpRequest.onreadystatechange = function () {
		// kiểm tra khi trạng thái request đã hoàn thành (readyState = 1) và thành công (status = 200) (thất bại = 500)
		if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
			var data = JSON.parse(xmlHttpRequest.responseText); // ép kiểu dữ liệu nhận về thành json;
			var newContent = ''; // tạo ra biến content mới để cộng dồn và update vào view.
			for (let i = 0; i < data.length; i++) {
				// sử dụng dấu ` để nhồi biến vào html dễ dàng hơn.
				// khi click vào Delete thì thực hiện function bên dưới
				// khi click vào Edit thì sẽ nhảy sang form.html với query params là id của từng Product
				newContent += `
                <tr>
                    <td>${data[i].name}</td>
                    <td>${data[i].price}</td>
                    <td>${data[i].status}</td>
                    <td>
                        <a href="/form.html?id=${data[i].id}" class="btn-edit">Edit</a> |
                        <a href="#" title="${data[i].id}" class="btn-delete">Delete</a>	|
                        <a href="#" title="${data[i].id}" class="btn-addcart">Add to cart</a>
                    </td>
                </tr>`;
			}
			tableBody.innerHTML = newContent; // thay đổi nội dung table.
		}
	};
	xmlHttpRequest.open('get', 'http://localhost:8080/api/products', false);
	xmlHttpRequest.send();

	// kiểm tra nếu click vào btn delete thì sẽ delete Product đó đi
	document.body.addEventListener('click', function (event) {
		if (event.target.className === 'btn-delete') {
			if (confirm('Are you sure you want to delete?')) {
				let id = event.target.title;
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = function () {
					if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
						alert('Deleted successfully');
						location.reload();
					}
				};
				xmlHttpRequest.open(
					'delete',
					'http://localhost:8080/api/products/' + id,
					false
				);
				xmlHttpRequest.send();
			}
		}
	});
});
