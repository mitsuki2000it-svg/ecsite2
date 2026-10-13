package com.example.ecsite2.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecsite2.SaleResponse;
import com.example.ecsite2.SaleService;
@RestController
public class MySaleController {

	private final SaleService saleService;

	public MySaleController(SaleService saleService) {
		this.saleService = saleService;
	}

	@GetMapping("/my-sales")
	public List<SaleResponse> getMySales(Authentication authentication) {

		String username = authentication.getName();

		return saleService.getMySales(username).stream().map(SaleResponse::from).toList();

	}

}
