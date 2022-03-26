document.addEventListener('DOMContentLoaded', function () {
	var btnSubmit = document.getElementById('btn-submit'); // gọi ra phần tử cần làm việc sử dụng id.
	var txtName = document.forms['product-form']['name']; // ko được lấy value, chỉ lấy text input.
	var txtPrice = document.forms['product-form']['price']; // ko được lấy value, chỉ lấy text input.
	var txtStatus = document.forms['product-form']['status']; // ko được lấy value, chỉ lấy text input.

	// kiểm tra nếu có id từ params thì set trạng thái là đang edit
	// và lấy ra product rồi gán từng trường của product vào form
	var url_string = window.location.href.toLowerCase();
	var url = new URL(url_string);
	var id = url.searchParams.get('id');
	var isEdit = false;
	if (id != undefined && id.length > 0) {
		isEdit = true;
		let xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = function () {
			if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
				var data = JSON.parse(xmlHttpRequest.responseText);
				txtName.value = data.name;
				txtPrice.value = data.price;
				txtStatus.value = data.status;
			}
		};
		xmlHttpRequest.open(
			'get',
			'http://localhost:8080/api/products/' + id,
			false
		);
		xmlHttpRequest.send();
	}

	// ra chỉ thị cho nó
	btnSubmit.onclick = function () {
		var name = txtName.value;
		var price = txtPrice.value;
		var status = txtStatus.value;
		// validate nếu cần.
		// tạo ra đối tượng có định dạng json.
		var dataToSend = {
			name: name,
			price: price,
			status: status,
		};

		// tạo ra các biến để xử lý:
		// + trường hợp tạo mới Product là các giá trị default
		// + trường hợp sửa Product là các giá trị ở trong if
		var method = 'post';
		var url = 'http://localhost:8080/api/products';
		var successStatus = 201;
		if (isEdit) {
			method = 'put';
			url = `${url}/${id}`;
			successStatus = 200;
		}
		// xử lý request lên server.
		var xmlHttpRequest = new XMLHttpRequest();
		// sự kiện khi request thay đổi trạng thái
		xmlHttpRequest.onreadystatechange = function () {
			// kiểm tra khi trạng thái request đã hoàn thành (readyState = 1) và tạo thành công (status = 201) (thất bại = 500)
			if (
				xmlHttpRequest.readyState == 4 &&
				xmlHttpRequest.status == successStatus
			) {
				alert('Create product success!');
				window.location.href = './index.html';
			}
		};
		xmlHttpRequest.open(method, url, false);
		// sửa kiểu dữ liệu gửi lên có định dang json, phải đứng sau hàm open
		xmlHttpRequest.setRequestHeader('Content-Type', 'application/json');
		xmlHttpRequest.send(JSON.stringify(dataToSend)); // gửi dữ liệu ở định dạng json.
	};
});
