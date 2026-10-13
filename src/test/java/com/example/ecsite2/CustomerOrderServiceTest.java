package com.example.ecsite2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class CustomerOrderServiceTest {

	@Mock
	private CustomerOrderRepository customerOrderRepository;

	@Mock
	private SaleRepository saleRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private CustomerOrderService customerOrderService;

	@Test
	void shipTest() {

		CustomerOrder order = new CustomerOrder();

		order.setStatus(OrderStatus.ORDERED);

		when(customerOrderRepository.findById(1)).thenReturn(Optional.of(order));

		CustomerOrder result = customerOrderService.ship(1);

		assertEquals(OrderStatus.SHIPPED, result.getStatus());

		verify(customerOrderRepository).findById(1);

	}

	@Test
	void shipErrorTest() {

		CustomerOrder order = new CustomerOrder();

		order.setStatus(OrderStatus.SHIPPED);

		when(customerOrderRepository.findById(1)).thenReturn(Optional.of(order));

		assertThrows(ResponseStatusException.class, () -> customerOrderService.ship(1));
	}

	@Test
	void cancelTest() {

		CustomerOrder order = new CustomerOrder();

		order.setStatus(OrderStatus.ORDERED);

		Product product = new Product();

		product.setStock(8);

		Sale sale = new Sale();

		sale.setProduct(product);
		sale.setAmount(2);

		when(customerOrderRepository.findById(1)).thenReturn(Optional.of(order));

		when(saleRepository.findByOrder_Id(1)).thenReturn(List.of(sale));

		product.setId(1);

		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product));

		CustomerOrder result = customerOrderService.cancel(1);

		assertEquals(OrderStatus.CANCELLED, result.getStatus());
		assertEquals(10, product.getStock());

		verify(customerOrderRepository).findById(1);

		verify(saleRepository).findByOrder_Id(1);

		verify(productRepository).findByIdForUpdate(1);

	}

	@Test
	void cancelErrorTest() {

		CustomerOrder order = new CustomerOrder();

		order.setStatus(OrderStatus.CANCELLED);

		when(customerOrderRepository.findById(1)).thenReturn(Optional.of(order));

		assertThrows(ResponseStatusException.class, () -> customerOrderService.cancel(1));

		verify(saleRepository, never()).findByOrder_Id(1);
		verify(productRepository, never()).findByIdForUpdate(1);
	}

	@Test
	void cancelMultipleProductsTest() {

		CustomerOrder order = new CustomerOrder();

		order.setStatus(OrderStatus.ORDERED);

		Product product1 = new Product();
		product1.setId(1);
		product1.setStock(8);

		Sale sale1 = new Sale();
		sale1.setProduct(product1);
		sale1.setAmount(2);

		Product product2 = new Product();
		product2.setId(2);
		product2.setStock(5);

		Sale sale2 = new Sale();
		sale2.setProduct(product2);
		sale2.setAmount(1);

		when(customerOrderRepository.findById(1)).thenReturn(Optional.of(order));
		when(saleRepository.findByOrder_Id(1)).thenReturn(List.of(sale1, sale2));

		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product1));
		when(productRepository.findByIdForUpdate(2)).thenReturn(Optional.of(product2));

		CustomerOrder result = customerOrderService.cancel(1);
		
		assertEquals(OrderStatus.CANCELLED,result.getStatus());
		assertEquals(10,product1.getStock());
		assertEquals(6,product2.getStock());
		
		verify(productRepository).findByIdForUpdate(1);
		verify(productRepository).findByIdForUpdate(2);

	}

}
