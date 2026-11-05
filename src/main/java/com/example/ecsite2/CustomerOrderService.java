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
	private final PaymentRepository paymentRepository;

	public CustomerOrderService(CustomerOrderRepository customerOrderRepository, SaleRepository saleRepository,
			ProductRepository productRepository, PaymentRepository paymentRepository) {

		this.customerOrderRepository = customerOrderRepository;
		this.saleRepository = saleRepository;
		this.productRepository = productRepository;
		this.paymentRepository = paymentRepository;

	}

	@Transactional
	public CustomerOrder ship(Integer orderId) {

		CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow();

		if (OrderStatus.ORDERED != order.getStatus()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "注文済みの注文だけ発送済みにできます");
		}
		
		if (order.getPaymentStatus() != PaymentStatus.PAID) {
		    throw new ResponseStatusException(
		        HttpStatus.CONFLICT,
		        "支払いが完了していない注文は発送できません"
		    );
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

		Payment payment = order.getPayment();

		if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
			payment.setStatus(PaymentStatus.REFUNDED);
		}

		if (order.getPaymentStatus() == PaymentStatus.PAID) {
			order.setPaymentStatus(PaymentStatus.REFUNDED);
		}

		order.setStatus(OrderStatus.CANCELLED);

		return order;
	}

	@Transactional
	public void pay(Integer orderId, String username) {

		CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow();

		if (order.getUser() == null || !order.getUser().getUsername().equals(username)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この注文の支払いはできません");
		}
		if (order.getStatus() != OrderStatus.ORDERED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "この注文状態では支払いできません");
		}

		Payment payment = paymentRepository.findByOrder_Id(orderId).orElse(null);

		if (payment == null) {

			if (order.getPaymentStatus() != PaymentStatus.UNPAID) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "支払い情報がありません");
			}

			List<Sale> sales = saleRepository.findByOrder_Id(orderId);

			int totalAmount = 0;

			for (Sale sale : sales) {
				totalAmount += sale.getSales();
			}

			payment = new Payment();
			payment.setOrder(order);
			payment.setAmount(totalAmount);
			payment.setStatus(PaymentStatus.UNPAID);

			paymentRepository.save(payment);
		}
		if (payment.getStatus() != PaymentStatus.UNPAID) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "この注文は支払いできません");
		}

		payment.setStatus(PaymentStatus.PAID);

		order.setPaymentStatus(PaymentStatus.PAID);

	}

}
