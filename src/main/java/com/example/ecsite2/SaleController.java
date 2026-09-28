package com.example.ecsite2;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		return saleService.getsaleCount();
	}

}
