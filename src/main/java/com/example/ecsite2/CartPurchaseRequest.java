package com.example.ecsite2;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class CartPurchaseRequest {
	
    @Valid
    @NotEmpty(message = "カートが空です")
	private List<CartItemRequest> items;

	public List<CartItemRequest> getItems() {
		return items;
	}

	public void setItems(List<CartItemRequest> items) {
		this.items = items;
	}

}
