package com.example.ecsite2;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CartPurchaseRequest {

	@Valid
	@NotEmpty(message = "カートが空です")
	private List<CartItemRequest> items;

	@NotBlank(message = "配送先住所を入力してください")
	private String shippingAddress;

	public List<CartItemRequest> getItems() {
		return items;
	}

	public void setItems(List<CartItemRequest> items) {
		this.items = items;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

}
