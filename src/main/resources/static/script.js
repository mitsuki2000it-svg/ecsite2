const productCountElement = document.getElementById("product-count");
const productsElement = document.getElementById("products");
const salesElement = document.getElementById("sales");
const totalSalesElement = document.getElementById("total-sales");
const saleCountElement = document.getElementById("sale-count");
const cartElement = document.getElementById("cart");
let currentPage = 0;
let totalPages = 0;
let pageSize = 5;
let cart =
	JSON.parse(localStorage.getItem("cart")) || [];

function applyFilters() {
	currentPage = 0;
	loadProducts();
}

function changePageSize() {

	const value =
		document.getElementById("page-size").value;

	if (value === "all") {
		pageSize = 1000;
	} else {
		pageSize = Number(value);
	}

	currentPage = 0;

	loadProducts();

}

function nextPage() {

	if (currentPage < totalPages - 1) {
		currentPage++;
		loadProducts();
	}
}

function previousPage() {

	if (currentPage > 0) {
		currentPage--;
		loadProducts();
	}
}

function resetFilters() {

	document.getElementById("search-keyword").value = "";
	document.getElementById("min-price").value = "";
	document.getElementById("max-price").value = "";
	document.getElementById("in-stock-only").checked = false;
	document.getElementById("sort-order").value = "";
	document.getElementById("page-size").value = "5";

	pageSize = 5;
	currentPage = 0;

	loadProducts();
}

function filterAndSortProducts() {

	const minPrice =
		Number(document.getElementById("min-price").value);

	const maxPrice =
		Number(document.getElementById("max-price").value);

	if (minPrice > 0 && maxPrice > 0 && minPrice > maxPrice) {
		alert("最低価格は最高価格以下にしてください");
		return;
	}

	const inStockOnly =
		document.getElementById("in-stock-only").checked;

	const keyword =
		document.getElementById("search-keyword").value.trim();

	const encodedKeyword =
		encodeURIComponent(keyword);

	const sortOrder =
		document.getElementById("sort-order").value;

	let url;

	if (keyword === "") {

		url = `/products/page?page=${currentPage}&size=${pageSize}&sortOrder=${sortOrder}&inStockOnly=${inStockOnly}&minPrice=${minPrice}&maxPrice=${maxPrice}`;
	} else {
		url = `/products/search?keyword=${encodedKeyword}&page=${currentPage}&size=${pageSize}&sortOrder=${sortOrder}&inStockOnly=${inStockOnly}&minPrice=${minPrice}&maxPrice=${maxPrice}`;
	}

	fetch(url)
		.then(response => {

			if (!response.ok) {
				return response.json().then(errorData => {
					throw new Error(
						errorData.error || "商品を取得できませんでした"
					);
				});
			}

			return response.json();
		})
		.then(data => {

			totalPages = data.totalPages;

			productCountElement.innerHTML =
				data.totalElements + "件の商品";

			document.getElementById("page-info").innerHTML =
				totalPages === 0 ? "0/0" : (currentPage + 1) + "/" + totalPages;

			document.getElementById("previous-button").disabled =
				currentPage === 0;

			document.getElementById("next-button").disabled =
				totalPages === 0 || currentPage === totalPages - 1;

			let products = data.content;

			if (products.length === 0) {

				productCountElement.innerHTML =
					"0件の商品";

				productsElement.innerHTML =
					"商品が見つかりません";
				return;
			}

			displayProducts(data.content);
		})
		.catch(error => {
			alert(error.message);
		})

}

function sortProducts() {
	currentPage = 0;
	loadProducts();
}

function resetSearch() {

	document.getElementById("search-keyword").value = "";

	currentPage = 0;

	loadProducts();
}

function handleSearchKey(event) {

	if (event.key === "Enter") {
		searchProducts();
	}

}

function displayProducts(products) {


	productsElement.innerHTML = "";

	products.forEach(product => {

		productsElement.innerHTML += `
	<div class="product-card ${product.stock === 0 ? "sold-out" : ""}">
		
		<h2>${product.name}</h2>

		${product.stock === 0
				? '<span class="sold-out-label">売り切れ</span>'
				: product.stock <= 3
					? '<span class="low-stock-label">残りわずか</span>'
					: ''
			}
		
		<p class="product-price">${product.price}円</p>
		<p class="product-stock ${product.stock <= 3 ? "low-stock" : ""}">
		    在庫:${product.stock}
		</p>
		
        <div class="product-actions">

		<input 
		type="number"
		id="amount-${product.id}"
		value="1"
		min="1"
		oninput="hideOrderConfirm()"
		>

		<button
		class="buy-button"
		onclick="showSingleOrderConfirm(${product.id})"
		${product.stock === 0 ? "disabled" : ""}
		>
		${product.stock === 0 ? "売り切れ" : "購入"}
		</button>
		
		<button 
		class="cart-button"
		onclick="addToCart(${product.id})"
		${product.stock === 0 ? "disabled" : ""}
		>
		カートに追加
		</button>
		
		</div>

	</div>	
		
		`;
	})
}
async function purchaseCart() {
	const csrf =
		await getCsrfToken();

	if (cart.length === 0) {
		alert("カートが空です");
		return;
	}

	const shippingAddress =
		document.getElementById("shipping-address").value.trim();

	if (shippingAddress === "") {
		alert("配送先住所を入力してください");
		return;
	}

	const items = cart.map(item => {
		return {
			productId: item.product.id,
			amount: item.amount
		};
	});

	fetch(`/cart/purchase`, {
		method: "POST",

		headers: {
			"Content-Type": "application/json",
			[csrf.headerName]: csrf.token
		},

		body: JSON.stringify({
			items: items,
			shippingAddress
		})
	})

		.then(response => {

			if (!response.ok) {
				return response.json().then(errorData => {
					throw new Error(
						errorData.error || "購入に失敗しました"
					);
				});
			}

			return response.json();
		})
		.then(data => {

			cart = [];

			document.getElementById("order-confirm-area").hidden = true;

			displayCart();
			loadProducts();
			loadMySales();

			alert("購入が完了しました");
		})
		.catch(error => {
			alert(error.message);
		});

}
function displayCart() {

	const confirmArea =
		document.getElementById("order-confirm-area");

	if (confirmArea) {
		confirmArea.hidden = true;
	}

	const purchaseButton =
		document.getElementById("cart-purchase-button");

	const clearButton =
		document.getElementById("cart-clear-button");

	purchaseButton.disabled = cart.length === 0;
	clearButton.disabled = cart.length === 0;

	let total = 0;

	cartElement.innerHTML = "";

	if (cart.length === 0) {
		cartElement.innerHTML = `
		<p class="empty-message">
		カートは空です
		</p>
		`;

		localStorage.setItem(
			"cart",
			JSON.stringify(cart)
		);

		return;
	}

	cart.forEach(item => {
		total += item.product.price * item.amount;

		cartElement.innerHTML += `
		<div>
		   <p>${item.product.name}</p>
		   <p>${item.product.price}円</p>
		   <input
		       type="number"
			   min="1"
			   value="${item.amount}"
			   onchange="updateCartAmount(${item.product.id}, this.value)"
			   >
			   <button onclick="removeFromCart(${item.product.id})">
                  削除
               </button>
		</div>
		`;
	});
	cartElement.innerHTML += `
	    <p>合計:${total}円</p>
	`;

	localStorage.setItem(
		"cart",
		JSON.stringify(cart)
	);
}
function updateCartAmount(productId, value) {

	const amount = Number(value);

	const item = cart.find(item =>
		item.product.id === productId
	);

	if (!item) {
		return;
	}

	if (amount < 1) {
		alert("購入数は1以上にしてください");
		displayCart();
		return;
	}

	if (amount > item.product.stock) {
		alert("在庫数を超えています");
		displayCart();
		return;
	}

	item.amount = amount;

	displayCart();
}
function removeFromCart(productId) {

	cart = cart.filter(item =>
		item.product.id !== productId
	);

	displayCart();
}
function clearCart() {

	cart = [];

	displayCart();
}
function addToCart(productId) {

	const amountInput =
		document.getElementById(`amount-${productId}`);

	const amount = Number(amountInput.value);

	if (amount < 1) {
		alert("購入数は1以上にしてください");
		return;
	}

	fetch(`/products/${productId}`)
		.then(response => response.json())
		.then(product => {

			if (amount > product.stock) {
				alert("在庫数を超えています");
				return;
			}

			const existingItem =
				cart.find(item =>
					item.product.id === product.id
				);

			if (existingItem) {

				const newAmount =
					existingItem.amount + amount;

				if (newAmount > product.stock) {
					alert("在庫数を超えています");
					return;
				}

				existingItem.amount = newAmount;

			} else {
				cart.push({
					product: product,
					amount: amount
				});
			}

			displayCart();

			alert("カートに追加しました");
		});
}


function searchProducts() {
	currentPage = 0;
	loadProducts();
}

function loadProducts() {
	filterAndSortProducts();
};

async function purchase(productId) {
	const csrf =
		await getCsrfToken();

	const amountInput = document.getElementById(`amount-${productId}`);

	const amount = Number(amountInput.value);

	const shippingAddress = document.getElementById("shipping-address").value.trim();

	if (shippingAddress === "") {
		alert("配送先住所を入力してください");
		return;
	}

	if (amount < 1) {
		alert("購入数は1以上にしてください");
		return;
	}


	fetch(`/products/${productId}/purchase`, {

		method: "POST",


		headers: {
			"Content-Type": "application/json",
			[csrf.headerName]: csrf.token
		},

		body: JSON.stringify({
			amount,
			shippingAddress
		})
	})


		.then(response => {


			if (!response.ok) {
				return response.json().then(errorData => {
					throw new Error(errorData.message || errorData.error || "購入できませんでした");
				});

			}

			return response.json();
		})

		.then(data => {

			document.getElementById("order-confirm-area").hidden = true;

			alert(
				data.productName + "を購入しました！"
			);


			loadProducts();
			loadMySales();

		})

		.catch(error => {


			alert(error.message);
		});
}

function hideOrderConfirm() {

	document.getElementById("order-confirm-area").hidden = true;

}

async function showSingleOrderConfirm(productId) {

	const amountInput =
		document.getElementById(`amount-${productId}`);

	const amount =
		Number(amountInput.value);

	const shippingAddress =
		document.getElementById("shipping-address").value.trim();

	if (amount < 1) {
		alert("購入数は1以上にしてください");
		return;
	}
	if (shippingAddress === "") {
		alert("配送先住所を入力してください");
		return;
	}

	const response =
		await fetch(`/products/${productId}`);

	const product =
		await response.json();

	const confirmArea =
		document.getElementById("order-confirm-area");

	const confirmItems =
		document.getElementById("order-confirm-items");

	const confirmTotal =
		document.getElementById("order-confirm-total");

	const confirmAddress =
		document.getElementById("order-confirm-address");

	const confirmPurchaseButton =
		document.getElementById("confirm-purchase-button");

	const subtotal =
		product.price * amount;

	confirmItems.innerHTML = `
		<p>
		${product.name}
		${amount}個
		${subtotal}円
		</p>
		`;

	confirmTotal.textContent =
		`合計金額:${subtotal}円`;

	confirmAddress.textContent =
		`配送先:${shippingAddress}`;

	confirmPurchaseButton.onclick =
		() => purchase(productId);

	confirmArea.hidden = false;
}
function showOrderConfirm() {

	const confirmArea =
		document.getElementById("order-confirm-area");

	const confirmItems =
		document.getElementById("order-confirm-items");

	const confirmTotal =
		document.getElementById("order-confirm-total");

	const confirmAddress =
		document.getElementById("order-confirm-address");

	const shippingAddress =
		document.getElementById("shipping-address").value.trim();

	if (cart.length === 0) {
		alert("カートが空です");
		return;
	}

	if (shippingAddress === "") {
		alert("配送先住所を入力してください");
		return;
	}

	let total = 0;
	let itemsHtml = "";

	cart.forEach(item => {

		const subtotal =
			item.product.price * item.amount;

		total += subtotal;

		itemsHtml += `
		
		<p>
		${item.product.name}
		${item.amount}個
		${subtotal}円
		</p>
		`;
	});

	confirmItems.innerHTML = itemsHtml;

	confirmTotal.textContent =
		`合計金額:${total}円`;

	confirmAddress.textContent =
		`配送先:${shippingAddress}`;

	const confirmPurchaseButton =
		document.getElementById("confirm-purchase-button");

	confirmPurchaseButton.onclick = purchaseCart;

	confirmArea.hidden = false;

}

async function payOrder(orderId) {

	const csrf =
		await getCsrfToken();

	fetch(`/orders/${orderId}/pay`, {

		method: "PUT",

		headers: {
			[csrf.headerName]: csrf.token
		}

	})
		.then(response => {

			if (!response.ok) {
				throw new Error("支払いに失敗しました");
			}

			alert("支払いが完了しました");

			loadMySales();
		})
		.catch(error => {
			alert(error.message);
		});
}

async function loadMySales() {

	const response = await fetch("/my-sales");
	const sales = await response.json();

	const mySalesElement =
		document.getElementById("my-sales");

	mySalesElement.innerHTML = "";

	if (sales.length === 0) {
		mySalesElement.innerHTML =
			"購入履歴はありません";

		return;
	}

	const groupedSales = {};

	sales.forEach(sale => {

		const key =
			sale.orderId ?? `old-${sale.id}`;

		if (!groupedSales[key]) {
			groupedSales[key] = [];
		}

		groupedSales[key].push(sale);
	});

	Object.values(groupedSales).forEach(orderSales => {

		const firstSale = orderSales[0];

		const orderedAt =
			firstSale.orderedAt
				? new Date(firstSale.orderedAt)
					.toLocaleString("ja-JP")
				: "日時なし";

		let itemsHtml = "";
		let orderTotal = 0;

		const statusText =
			firstSale.status === "ORDERED"
				? "注文済み"
				: firstSale.status === "SHIPPED"
					? "発送済み"
					: firstSale.status === "CANCELLED"
						? "キャンセル済み"
						: "注文情報なし"

		const paymentStatusText =
			firstSale.paymentStatus === "UNPAID"
				? "未払い"
				: firstSale.paymentStatus === "PAID"
					? "支払い済み"
					: firstSale.paymentStatus === "REFUNDED"
						? "返金済み"
						: "支払い情報なし";

		const paymentButtonHtml =
			firstSale.paymentStatus === "UNPAID" && firstSale.orderId != null
				? `<button onclick="payOrder(${firstSale.orderId})">
		               支払う
		            </button>`
				: "";

		orderSales.forEach(sale => {

			orderTotal += sale.sales;

			itemsHtml += `
					<p>
					${sale.productName}
					${sale.amount}個
					${sale.sales}円
					</p>
				`;
		});

		mySalesElement.innerHTML += `
				<div class="sale-card">

				<h3>
				注文番号:
				${firstSale.orderId ?? "旧データ"}
				</h3>

				<p>状態:${statusText}</p>
				<p>支払い:${paymentStatusText}</p>
				<p>配送先:${firstSale.shippingAddress ?? "住所なし"}</p>

				${paymentButtonHtml}

				${itemsHtml}

                <p>合計金額:${orderTotal}円</p>
				<p>購入日時:${orderedAt}</p>

				</div>
				`;
	});
}

async function getCsrfToken() {

	const response =
		await fetch("/csrf");

	const csrf =
		await response.json();

	return csrf;
}

async function checkAdmin() {

	const response =
		await fetch("/is-admin");

	const isAdmin =
		await response.json();

	if (isAdmin) {
		document.getElementById("admin-link").hidden = false;
	}
}

async function loadCurrentUser() {

	const response = await fetch("/current-user");

	const username = await response.text();

	document.getElementById("current-user")
		.textContent = username;
}

async function setLogoutCsrfToken() {

	const csrf = await getCsrfToken();

	const input = document.createElement("input");

	input.type = "hidden";
	input.name = csrf.parameterName;
	input.value = csrf.token;

	document.getElementById("logout-form")
		.appendChild(input);
}

setLogoutCsrfToken();
loadProducts();
displayCart();
checkAdmin();
loadCurrentUser();
loadMySales();