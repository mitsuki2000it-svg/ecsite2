package com.example.ecsite2;

import java.time.LocalDateTime;

public class SaleResponse {

	private Integer id;
	private Integer amount;
	private Integer price;
	private String productName;
	private Integer sales;
	private LocalDateTime soldAt;

	public static SaleResponse from(Sale sale) {
		SaleResponse response = new SaleResponse();

		response.setId(sale.getId());
		response.setId(sale.getId());
	    response.setAmount(sale.getAmount());
	    response.setPrice(sale.getPrice());
	    response.setProductName(sale.getProductName());
	    response.setSales(sale.getSales());
	    response.setSoldAt(sale.getSoldAt());

	    return response;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public LocalDateTime getSoldAt() {
		return soldAt;
	}

	public void setSoldAt(LocalDateTime soldAt) {
		this.soldAt = soldAt;
	}

}
