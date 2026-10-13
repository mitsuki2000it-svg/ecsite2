package com.example.ecsite2.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecsite2.CartPurchaseRequest;
import com.example.ecsite2.ProductService;
import com.example.ecsite2.SaleResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

	private final ProductService productService;

	public CartController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/purchase")
	public List<SaleResponse> purchaseCart(@Valid @RequestBody CartPurchaseRequest request, Authentication authentication) {
		
		String username = authentication.getName();

		return productService.purchaseCart(request,username);
	}

}
