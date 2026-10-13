package com.example.ecsite2;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {
	@NotNull(message = "商品IDが必要です")
	private Integer productId;

	@NotNull(message = "購入数を入力してください")
	@Min(value = 1, message = "購入数は1以上にしてください")
	private Integer amount;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
