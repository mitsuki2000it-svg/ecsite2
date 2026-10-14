package com.example.ecsite2;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PurchaseRequest {

	@NotNull(message = "購入数を入力してください")
	@Min(value = 1, message = "購入数は1以上にしてください")
	private Integer amount;
	
	@NotBlank(message = "配送先住所を入力してください")
	private String shippingAddress;

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	
	

}
