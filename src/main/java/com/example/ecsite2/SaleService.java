package com.example.ecsite2;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SaleService {

	private final SaleRepository saleRepository;
	private final CustomerOrderRepository customerOrderRepository;

	public SaleService(SaleRepository saleRepository, CustomerOrderRepository customerOrderRepository) {
		this.saleRepository = saleRepository;
		this.customerOrderRepository = customerOrderRepository;
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

			if (sale.getOrder().getPaymentStatus() == PaymentStatus.PAID && sale.getOrder().getStatus() != OrderStatus.CANCELLED) {

				total += sale.getSales();
			}

		}
		return total;

	}

	public long getSaleCount() {
		return customerOrderRepository.countByPaymentStatusAndStatusNot(PaymentStatus.PAID, OrderStatus.CANCELLED);
	}

}
