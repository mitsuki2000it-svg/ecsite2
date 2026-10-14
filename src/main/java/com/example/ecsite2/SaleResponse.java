package com.example.ecsite2;

import java.time.LocalDateTime;

public class SaleResponse {

	private Integer id;
	private Integer amount;
	private Integer price;
	private String productName;
	private Integer sales;
	private LocalDateTime soldAt;
	private String userName;
	private Integer orderId;
	private LocalDateTime orderedAt;
	private String status;
	private String shippingAddress;

	public static SaleResponse from(Sale sale) {
		SaleResponse response = new SaleResponse();

		response.setId(sale.getId());
		response.setAmount(sale.getAmount());
		response.setPrice(sale.getPrice());
		response.setProductName(sale.getProductName());
		response.setSales(sale.getSales());
		response.setSoldAt(sale.getSoldAt());
		if (sale.getUser() != null) {
			response.setUserName(sale.getUser().getUsername());
		}
		if (sale.getOrder() != null) {
			response.setOrderId(sale.getOrder().getId());

			response.setOrderedAt(sale.getOrder().getOrderedAt());

			response.setShippingAddress(sale.getOrder().getShippingAddress());

			if (sale.getOrder().getStatus() != null) {
				response.setStatus(sale.getOrder().getStatus().name());
			}
		}
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getOrderedAt() {
		return orderedAt;
	}

	public void setOrderedAt(LocalDateTime orderedAt) {
		this.orderedAt = orderedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

}
