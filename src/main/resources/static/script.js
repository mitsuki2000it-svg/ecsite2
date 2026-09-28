const productsElement = document.getElementById("products");
const salesElement = document.getElementById("sales");
const totalSalesElement = document.getElementById("total-sales");
const saleCountElement = document.getElementById("sale-count");

function filterAndSortProducts() {
	
	const keyword =
	document.getElementById("search-keyword").value.trim();
	
	const sortOrder =
	document.getElementById("sort-order").value;
	
	fetch("/products")
	.then(response => response.json())
	.then(data => {
		
		let products = data;
		
		if(keyword !== "") {
			products = products.filter(product =>
				product.name.toLowerCase()
				.includes(keyword.toLowerCase())
			);
		}
		if(sortOrder === "price-asc") {
			products.sort((a,b) =>
				a.price - b.price
			);
		}
		
		if(sortOrder === "price-desc") {
			products.sort((a,b) =>
				b.price - a.price
			);
		}
		
		if(products.length === 0) {
			productElement.innerHTML =
			"商品が見つかりません";
			return;
		}
		
		displayProducts(products);
		});
	
}

function sortProducts() {
	filterAndSortProducts();
}

function resetSearch() {
	
	document.getElementById("search-keyword").value = "";
	
	filterAndSortProducts();
}

function handleSearchKey(event) {
	
	if(event.key === "Enter") {
		searchProducts();
	}
		
}

function displayProducts(products) {
	
	productsElement.innerHTML = "";
	
	products.forEach(product => {
		
		productsElement.innerHTML += `
		<div class="product-card">
		
		<h2>${product.name}</h2>
		<p>${product.price}円</p>
		<p>在庫:${product.stock}</p>
		
		<input 
		type="number"
		id="amount-${product.id}"
		value="1"
		min="1"
		>
		
		<button
		onclick="purchase(${product.id})"
		${product.stock === 0 ? "disabled" :　""}
		>
		${product.stock === 0 ? "売り切れ" : "購入"}
		</button>	
		
		<button onclick="deleteProduct(${product.id})">
		削除
		</button>
		
		<button onclick="editProduct(${product.id})">
		編集
		</button>
		
		</div>
		
		`;
	})
}

function searchProducts() {
	filterAndSortProducts();	
}

function updateProduct() {
	
	const id =
	document.getElementById("edit-product-id").value;
	
	const name =
	document.getElementById("edit-product-name").value;
	
	const price =
	Number(document.getElementById("edit-product-price").value);
	
	const stock =
	Number(document.getElementById("edit-product-stock").value);
	
	fetch(`/products/${id}`, {
		method: "PUT",
		
		headers: {
			"Content-Type": "application/json"
		},
		
		body: JSON.stringify({
			name,
			price,
			stock
		})
	})
	.then(response => {
	
	if(!response.ok) {
		return response.json().then(errorData => {
			
			const message =
			Object.values(errorData).join("\n");
			
			throw new Error(
				message || "商品を更新できませんでした"
			);
		});
	}
	return response.json();
	
	})
	.then(data => {
		
		alert("商品を更新しました");
		
		document.getElementById("edit-product-id").value = "";
		document.getElementById("edit-product-name").value = "";
		document.getElementById("edit-product-price").value = "";
		document.getElementById("edit-product-stock").value = "";
		
		loadProducts();
	})
	.catch(error => {
		alert(error.message);
	});
}

function editProduct(productId){
	
	fetch(`/products/${productId}`)
	.then(response => response.json())
	.then(product => {
		
		document.getElementById("edit-product-id").value =
		 product.id;
		
		document.getElementById("edit-product-name").value =
		 product.name;
		
		document.getElementById("edit-product-price").value =
		 product.price;
		
		document.getElementById("edit-product-stock").value =
		 product.stock;   
		
	});
}

function deleteProduct(productId){
	
	const confirmed =
	confirm("本当にこの商品を削除しますか");
	
	if(!confirmed){
		return;
	}
	
	fetch(`/products/${productId}`,{
		method: "DELETE"
	})
	.then(response => {
		
		if(!response.ok){
			return response.json().then(errorData => {
				throw new Error(errorData.message || errorData.error || "商品を削除できませんでした");
			});
		}
		
		alert("商品を削除しました");
		
		loadProducts();
	})
	.catch(error => {
		alert(error.message);
	});
}

function createProduct() {
	
	const name = 
	document.getElementById("product-name").value;
	
	const price = 
	Number(document.getElementById("product-price").value);
	
	const stock = 
	Number(document.getElementById("product-stock").value); 
	
	fetch("/products", {
		method: "POST",
		
		headers:{
			"Content-Type": "application/json"
		},
		
		body: JSON.stringify({
			name,
			price,
			stock
		})
	})
	.then(response => {
		
	  if(!response.ok){
		return response.json().then(errorData => {
			
			const message =
			Object.values(errorData).join("\n");
			
			throw new Error(
				message || "商品を追加できませんでした"
			);
		});

	  }
	  
	  return response.json();
	  
	  })
	  
	.then(data => {
		
		alert("商品を追加しました!");
		
		document.getElementById("product-name").value = "";
		document.getElementById("product-price").value = "";
		document.getElementById("product-stock").value = "";
		
		loadProducts();
	})
	.catch(error => {
		alert(error.message);
	});
}

function loadSaleCount(){
	fetch("/sales/count")
	.then(response => response.json())
	.then(data => {
		saleCountElement.innerHTML = data + "件";
	});
}

function loadTotalSales() {
	fetch("/sales/total")
	.then(response => response.json())
	.then(data => {
		totalSalesElement.innerHTML = data + "円";
	});
}

function loadSales(){
	fetch("/sales")
	
	.then(response => response.json())
	
	.then(data => {
		salesElement.innerHTML = "";
		
		data.forEach(sale => {
			const soldAt = sale.soldAt ? new Date(sale.soldAt).toLocaleString("ja-JP") : "日時なし";
			
			salesElement.innerHTML += `
			
			<div class="sale-card">
			<p>商品名:${sale.productName}</p>
			<p>購入数:${sale.amount}個</p>
			<p>売上:${sale.sales}円</p>
			<p>購入日時:${soldAt}</p>
			</div>
			`;
		});
	});
}

function loadProducts(){
	
	
	fetch("/products")
	.then(response => response.json())
	.then(data => {
		
		displayProducts(data);
		
	});
}


function purchase(productId){
	
	const amountInput = document.getElementById(`amount-${productId}`);
	
	const amount = Number(amountInput.value);
	
	if(amount < 1){
		alert("購入数は1以上にしてください");
		return;
	}
	

	
	fetch(`/products/${productId}/purchase`,{
		
		
		method: "POST",
		
		
		headers:{
			"Content-Type":"application/json"
		},
		
		body: JSON.stringify({
			amount
		})
	})
	
	
	.then(response => {
		
		
		if(!response.ok){
			return response.json().then(errorData => {
			throw new Error(errorData.message || errorData.error || "購入できませんでした");
		});
			
		}
		
		return response.json();
	})
	
	.then(data => {
		
		
		alert(
			data.productName + "を購入しました！"
		);
		
		
		loadProducts();
		loadSales();
		loadTotalSales();
		loadSaleCount();
	})
	
	.catch(error => {
		
		
		alert(error.message);
	});
}



loadProducts();
loadSales();
loadTotalSales();
loadSaleCount();