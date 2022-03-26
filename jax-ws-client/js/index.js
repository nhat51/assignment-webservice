document.addEventListener('DOMContentLoaded', function () {
    // lấy table body để thay đổi
    var tableBody = document.getElementById('my-table-data');
    // xử lý request lên server.
    var xmlHttpRequest = new XMLHttpRequest();
    // sự kiện khi request thay đổi trạng thái
    var data = undefined;

    xmlHttpRequest.onreadystatechange = function () {
        // kiểm tra khi trạng thái request đã hoàn thành (readyState = 1) và thành công (status = 200) (thất bại = 500)
        if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
            data = JSON.parse(xmlHttpRequest.responseText); // ép kiểu dữ liệu nhận về thành json;
            var newContent = ''; // tạo ra biến content mới để cộng dồn và update vào view.
            for (let i = 0; i < data.length; i++) {
                // sử dụng dấu ` để nhồi biến vào html dễ dàng hơn.
                // khi click vào Delete thì thực hiện function bên dưới
                // khi click vào Edit thì sẽ nhảy sang form.html với query params là id của từng Product
                newContent += `
                <tr>
                	<td class="product-id">${data[i].id}</td>
                    <td class="prodct-name">${data[i].name}</td>
                    <td class="price">${data[i].price}</td>
                    <td>${data[i].status}</td>
                    <td>
                        <button class="w3-button w3-blue"><a href="form.html?id=${data[i].id}" class="btn-edit">Edit</a> </button>|
                        <button class="w3-button w3-red"><a href="#" title="${data[i].id}" class="btn-delete">Delete</a></button>	|
                        <button id="add-cart-${data[i].id}"  class="w3-button w3-aqua"><a href="#" title="${data[i].id}" class="btn-cart">Add to cart</a></button>
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
    /*
    Cart*/
    // Modal
    var url = 'http://localhost:8080/api/carts';
    var method = 'get'
    var xmlRequest = new XMLHttpRequest();
    var modal = document.getElementById("myModal");
    var close = document.getElementsByClassName("close")[0];
    var modalbody = document.getElementById('cart-items');
    var totalPrice = document.getElementById('total-price')
    var btnGetCart = document.getElementById("cart");
// tại sao lại có [0] như  thế này bởi vì mỗi close là một html colection nên khi mình muốn lấy giá trị html thì phải thêm [0].
// Nếu mình có 2 cái component cùng class thì khi [0] nó sẽ hiển thị component 1 còn [1] thì nó sẽ hiển thị component 2.
    var close_footer = document.getElementsByClassName("close-footer")[0];
    //Get cart
    var cartData = undefined
    btnGetCart.onclick = function () {
        modal.style.display = "block";
    }

        xmlRequest.onreadystatechange = function () {
            if (xmlRequest.readyState == 4 && xmlRequest.status == 200) {
                cartData = JSON.parse(xmlRequest.responseText);
                var content = '';
                for (let i = 0; i < cartData.cartItems.length; i++) {
                    content += ` <div class="cart-row">
                                        <div class="cart-item cart-column">
                                            <span class="qcart-item-title">${cartData.cartItems[i].productName}</span>
                                        </div>
                                        <span class="cart-price cart-column">${cartData.cartItems[i].unitPrice}</span>
                                        <div class="cart-quantity cart-column">
                                            <input class="cart-quantity-input" type="number" value="${cartData.cartItems[i].quantity}">
                                            <button id="btn-remove-${cartData.cartItems[i].productId}"  class="btn btn-danger" type="button">Xóa</button>
                                        </div>
                                    </div>`;
                }
                modalbody.innerHTML = content;
                totalPrice.innerHTML = cartData.totalPrice
            }
        }
        xmlRequest.open(method, url, false);
        xmlRequest.setRequestHeader('Authorization', '1');
        xmlRequest.send();

    close.onclick = function () {
        modal.style.display = "none";
    }
    close_footer.onclick = function () {
        modal.style.display = "none";
    }
/*    order.onclick = function () {
        alert("Cảm ơn bạn đã thanh toán đơn hàng")
    }*/
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
//add to cart
    for (let i = 0; i < data.length; i++) {
        var addBtn = document.getElementById(`add-cart-${data[i].id}`);
        addBtn.onclick = function (){
            console.log(data[i].id);
            console.log(data)
            xmlRequest.onreadystatechange = function (){
                if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                    alert("Sản phẩm đã được thêm vào giỏ hàng")
                }
            }
            xmlRequest.open(method, url + `/add?productId=${data[i].id}&quantity=1`, false);
            xmlRequest.setRequestHeader('Authorization', '1');
            xmlRequest.send();
        }
    }
//xóa cart
    var btnPayment = document.getElementById('btn-payment');
    btnPayment.onclick = function (){
        xmlRequest.onreadystatechange = function (){
            if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                window.location.reload();
                alert("Thanh Toán Thành công")
            }
        }
        xmlRequest.open(method, url + `/clear`, false);
        xmlRequest.setRequestHeader('Authorization', '1');
        xmlRequest.send();
    }
//  remove item
    for (let i = 0; i < cartData.cartItems.length; i++) {
        var removeBtn = document.getElementById(`btn-remove-${cartData.cartItems[i].productId}`);
        removeBtn.onclick = function (){
            console.log(cartData.cartItems[i].productId);
            xmlRequest.onreadystatechange = function (){
                if (xmlRequest.readyState == 4 && xmlHttpRequest.status == 200){
                    window.location.reload();
                    alert("xóa thành công")
                }
            }
            xmlRequest.open(method, url + `/remove?productId=${cartData.cartItems[i].productId}`, false);
            xmlRequest.setRequestHeader('Authorization', '1');
            xmlRequest.send();
        }
    }
console.log(cartData)

});
