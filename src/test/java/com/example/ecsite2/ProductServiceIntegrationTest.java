package com.example.ecsite2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private SaleRepository saleRepository;

	@Autowired
	private CustomerOrderRepository customerOrderRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;

	@Test
	void contextLoads() {

	}

	@Test
	void purchaseCartRollbackTest() {

		AppUser user = new AppUser();

		user.setUsername("testuser");
		user.setPassword("testpass");
		user.setRole("USER");

		appUserRepository.save(user);

		Product product1 = new Product();
		product1.setName("マウス");
		product1.setPrice(500);
		product1.setStock(10);

		Product savedProduct1 = productRepository.save(product1);

		Product product2 = new Product();
		product2.setName("キーボード");
		product2.setPrice(1000);
		product2.setStock(0);

		Product savedProduct2 = productRepository.save(product2);

		CartItemRequest item1 = new CartItemRequest();
		item1.setProductId(savedProduct1.getId());
		item1.setAmount(2);

		CartItemRequest item2 = new CartItemRequest();
		item2.setProductId(savedProduct2.getId());
		item2.setAmount(1);

		CartPurchaseRequest request = new CartPurchaseRequest();

		request.setItems(List.of(item1, item2));

		long saleCountBefore = saleRepository.count();
		long orderCountBefore = customerOrderRepository.count();

		assertThrows(ResponseStatusException.class, () -> productService.purchaseCart(request, "testuser"));

		Product reloadedProduct1 = productRepository.findById(savedProduct1.getId()).orElseThrow();

		assertEquals(10, reloadedProduct1.getStock());
		assertEquals(saleCountBefore, saleRepository.count());
		assertEquals(orderCountBefore, customerOrderRepository.count());
	}

	@BeforeEach
	void cleanDatabase() {

		saleRepository.deleteAllInBatch();
		paymentRepository.deleteAllInBatch();
		customerOrderRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		appUserRepository.deleteAllInBatch();

	}

	@Test
	void purchaseCartIntegrationTest() {

		AppUser user = new AppUser();
		user.setUsername("testuser");
		user.setPassword("testpass");
		user.setRole("USER");

		appUserRepository.save(user);

		Product product1 = new Product();
		product1.setName("マウス");
		product1.setPrice(500);
		product1.setStock(10);

		Product savedProduct1 = productRepository.save(product1);

		Product product2 = new Product();
		product2.setName("キーボード");
		product2.setPrice(1000);
		product2.setStock(5);

		Product savedProduct2 = productRepository.save(product2);

		CartItemRequest item1 = new CartItemRequest();
		item1.setProductId(savedProduct1.getId());
		item1.setAmount(2);

		CartItemRequest item2 = new CartItemRequest();
		item2.setProductId(savedProduct2.getId());
		item2.setAmount(1);

		CartPurchaseRequest request = new CartPurchaseRequest();
		
		request.setShippingAddress("千葉県テスト市1-2-3");

		request.setItems(List.of(item1, item2));

		List<SaleResponse> results = productService.purchaseCart(request, "testuser");

		assertEquals(2, results.size());

		Product reloadProduct1 = productRepository.findById(savedProduct1.getId()).orElseThrow();

		Product reloadProduct2 = productRepository.findById(savedProduct2.getId()).orElseThrow();

		assertEquals(8, reloadProduct1.getStock());
		assertEquals(4, reloadProduct2.getStock());

		List<Sale> savedSales = saleRepository.findAll();

		assertEquals(savedSales.get(0).getOrder().getId(), savedSales.get(1).getOrder().getId());
		
		CustomerOrder savedOrder = customerOrderRepository.findAll().get(0);
		
		assertEquals(OrderStatus.ORDERED,savedOrder.getStatus());
		assertEquals("testuser",savedOrder.getUser().getUsername());
		assertEquals("千葉県テスト市1-2-3",savedOrder.getShippingAddress());
		
		assertEquals("マウス",savedSales.get(0).getProductName());
		assertEquals(500,savedSales.get(0).getPrice());
		assertEquals(2,savedSales.get(0).getAmount());
		assertEquals(1000,savedSales.get(0).getSales());
		assertNotNull(savedSales.get(0).getSoldAt());
		assertNotNull(savedOrder.getOrderedAt());
	}

}
