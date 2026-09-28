package com.example.ecsite2;

public class ProductResponse {

	private Integer id;
	private String name;
	private Integer price;
	private Integer stock;
	
	public static ProductResponse from(Product product) {
		ProductResponse response = new ProductResponse();
		
		response.setId(product.getId());
		response.setName(product.getName());
		response.setPrice(product.getprice());
		response.setStock(product.getStock());
		
		return response;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

}
