package com.example.ecsite2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SaleService {

	private final SaleRepository saleRepository;

	public SaleService(SaleRepository saleRepository) {
		this.saleRepository = saleRepository;
	}

	public List<SaleResponse> getAllSales() {

		List<Sale> sales = saleRepository.findAll();

		return sales.stream().map(SaleResponse::from).toList();

	}
	
	public List<Sale> getMySales(String username) {
		
		return saleRepository.findByUser_UsernameOrderBySoldAtDesc(username);
	}

	public Integer getTotalSales() {

		List<Sale> sales = saleRepository.findAll();

		int total = 0;

		for (Sale sale : sales) {
			total += sale.getSales();

		}
		return total;

	}

	public long getsaleCount() {
		return saleRepository.count();
	}

}
