const adminProductElement =
    document.getElementById("admin-products");

async function loadAdminProducts() {

    const response =
        await fetch("/products/page?page=0&size=1000");

    const data =
        await response.json();

    displayAdminProducts(data.content);
}

function displayAdminProducts(products) {

    adminProductElement.innerHTML = "";

    products.forEach(product => {

        adminProductElement.innerHTML += `
        <div class="product-card">
        
        <h2>${product.name}</h2>
        
        <p class="product-price">
        ${product.price}円
        </p>
        
        <p class="product-stock">
        在庫:${product.stock}
        </p>
        
        <button
        class="edit-button"
        onclick="editProduct(${product.id})">
        編集
        </button>
        
        <button
        class="danger-button"
        onclick="deleteProduct(${product.id})">
        削除
        </button>

        </div>
        `;
    });
}
async function editProduct(id) {

    const response =
        await fetch(`/products/${id}`);

    const product =
        await response.json();

    document.getElementById("edit-product-id").value =
        product.id;

    document.getElementById("edit-product-name").value =
        product.name;

    document.getElementById("edit-product-price").value =
        product.price;

    document.getElementById("edit-product-stock").value =
        product.stock;
}

async function updateProduct() {
    const csrf = await getCsrfToken();

    const id =
        document.getElementById("edit-product-id").value;

    const name =
        document.getElementById("edit-product-name").value;

    const price =
        document.getElementById("edit-product-price").value;

    const stock =
        document.getElementById("edit-product-stock").value;

    const response =
        await fetch(`/products/${id}`, {
            method: "PUT",

            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },

            body: JSON.stringify({
                name: name,
                price: Number(price),
                stock: Number(stock)
            })
        });

    if (response.ok) {
        alert("商品を更新しました");
        loadAdminProducts();
    }
}
async function deleteProduct(id) {

    const confirmed =
        confirm("本当に削除しますか");

    if (!confirmed) {
        return;
    }

    const csrf =
        await getCsrfToken();

    const response =
        await fetch(`/products/${id}`, {
            method: "DELETE",

            headers: {
                [csrf.headerName]: csrf.token
            }
        });

    if (response.ok) {
        alert("商品を削除しました");
        loadAdminProducts();
    }
}
async function createProduct() {
    const csrf =
        await getCsrfToken();

    const name =
        document.getElementById("product-name").value;

    const price =
        document.getElementById("product-price").value;

    const stock =
        document.getElementById("product-stock").value;

    const response =
        await fetch("/products", {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                [csrf.headerName]: csrf.token
            },

            body: JSON.stringify({
                name: name,
                price: Number(price),
                stock: Number(stock)
            })
        });

    if (response.ok) {
        alert("商品を追加しました");
        loadAdminProducts();
    }
}
async function loadSales() {

    const response =
        await fetch("/sales");

    const sales =
        await response.json();

    const salesElement =
        document.getElementById("sales");

    salesElement.innerHTML = "";

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

        let itemHtml = "";
        let orderTotal = 0;

        orderSales.forEach(sale => {

            orderTotal += sale.sales;

            itemHtml += `
            <p>
            ${sale.productName}
            ${sale.amount}個
            ${sale.sales}円
            </p>
            `;
        });

        const statusText =
            firstSale.status === "ORDERED"
                ? "注文済み"
                : firstSale.status === "SHIPPED"
                    ? "発送済み"
                    : firstSale.status === "CANCELLED"
                        ? "キャンセル済み"
                        : firstSale.status ?? "状態なし";

        const shipButton =
            firstSale.status === "ORDERED"
                && firstSale.orderId != null

                ? `
        
        <button
        onclick="shipOrder(${firstSale.orderId})">
        発送済みにする
        </button>
        `
                : "";

        const cancelButton =
            firstSale.status === "ORDERED"
                && firstSale.orderId != null ? `
                <button
                onclick="cancelOrder(${firstSale.orderId})">
                キャンセルする
                </button>
                `
                : "";

        salesElement.innerHTML += `
        <div class="sale-card">
        
        <h3>
        注文番号:
        ${firstSale.orderId ?? "旧データ"}
        </h3>
        
        <p>購入者:${firstSale.userName ?? "不明"}</p>
        <p>状態:${statusText}</p>
        
        ${itemHtml}
        
        <p>合計金額:${orderTotal}円</p>
        <p>注文日時:${orderedAt}</p>
        
        ${shipButton}
        ${cancelButton}
        
        </div>
        `;
    });
}
async function loadTotalSales() {

    const response =
        await fetch("/sales/total");

    const total =
        await response.json();

    document.getElementById("total-sales").innerHTML =
        total + "円";
}

async function loadSaleCount() {
    const response =
        await fetch("/sales/count");

    const count =
        await response.json();

    document.getElementById("sale-count").innerHTML =
        count + "件";
}

async function shipOrder(orderId) {

    const csrf = await getCsrfToken();

    const response = await fetch(`/orders/${orderId}/ship`,
        {
            method: "PUT",

            headers: {
                [csrf.headerName]: csrf.token
            }
        }
    );

    if (!response.ok) {
        alert("発送済みに変更できませんでした");
        return;
    }

    alert("発送済みに変更しました");

    loadSales();
}

async function cancelOrder(orderId) {

    const confirmed =
        confirm("この注文をキャンセルしますか？");

    if (!confirmed) {
        return;
    }

    const csrf =
        await getCsrfToken();

    const response =
        await fetch(
            `/orders/${orderId}/cancel`,
            {
                method: "PUT",

                headers: {
                    [csrf.headerName]: csrf.token
                }
            }
        );

    if (!response.ok) {
        alert("注文をキャンセルしました");
    }

    loadSales();
    loadAdminProducts();

}

async function getCsrfToken() {

    const response =
        await fetch("/csrf");

    const csrf =
        await response.json();

    return csrf;
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
loadAdminProducts();
loadSales();
loadTotalSales();
loadSaleCount();
