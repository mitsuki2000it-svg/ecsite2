package com.example.ecsite2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class CustomerOrderService {

	private final CustomerOrderRepository customerOrderRepository;
	private final SaleRepository saleRepository;
	private final ProductRepository productRepository;

	public CustomerOrderService(CustomerOrderRepository customerOrderRepository, SaleRepository saleRepository,
			ProductRepository productRepository) {

		this.customerOrderRepository = customerOrderRepository;
		this.saleRepository = saleRepository;
		this.productRepository = productRepository;

	}

	@Transactional
	public CustomerOrder ship(Integer orderId) {

		CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow();

		if (OrderStatus.ORDERED != order.getStatus()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "注文済みの注文だけ発送済みにできます");
		}

		order.setStatus(OrderStatus.SHIPPED);

		return order;
	}

	@Transactional
	public CustomerOrder cancel(Integer orderId) {

		CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow();

		if (OrderStatus.ORDERED != order.getStatus()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "注文済みの注文だけキャンセルできます");
		}

		List<Sale> sales = saleRepository.findByOrder_Id(orderId);

		for (Sale sale : sales) {

			Product product = productRepository.findByIdForUpdate(sale.getProduct().getId()).orElseThrow();

			product.setStock(product.getStock() + sale.getAmount());
		}

		order.setStatus(OrderStatus.CANCELLED);

		return order;
	}

}
