package com.example.ecsite2.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecsite2.Sale;
import com.example.ecsite2.SaleResponse;
import com.example.ecsite2.SaleService;

@RestController
@RequestMapping("/sales")
public class SaleController {
	
	private final SaleService saleService;
	
	public SaleController(SaleService saleService) {
		this.saleService = saleService;
	}
	
	@GetMapping
	public List<SaleResponse> getAllSales(){
		return saleService.getAllSales();
	}
	
	@GetMapping("/total")
	public Integer getTotalSales() {
		return saleService.getTotalSales();
	}
	@GetMapping("/count")
	public long getSaleCount() {
		return saleService.getSaleCount();
	}

}
