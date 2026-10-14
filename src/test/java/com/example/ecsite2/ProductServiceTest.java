package com.example.ecsite2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import exception.ProductNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	@Mock
	private ProductRepository productRepository;

	@Mock
	private SaleRepository saleRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@Mock
	private CustomerOrderRepository customerOrderRepository;

	@InjectMocks
	private ProductService productService;

	@Test
	void purchaseTest() {

		AppUser user = new AppUser();
		user.setUsername("testuser");

		Product product = new Product();
		product.setId(1);
		product.setName("マウス");
		product.setPrice(500);
		product.setStock(10);

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product));

		when(saleRepository.save(any(Sale.class))).thenAnswer(inovation -> inovation.getArgument(0));

		SaleResponse result = productService.purchase(1, 3, "testuser","千葉県テスト市1-2-3");

		assertEquals(7, product.getStock());
		assertEquals(3, result.getAmount());
		assertEquals(500, result.getPrice());
		assertEquals(1500, result.getSales());

		verify(appUserRepository).findByUsername("testuser");
		verify(productRepository).findByIdForUpdate(1);
		verify(saleRepository).save(any(Sale.class));
		verify(customerOrderRepository).save(any(CustomerOrder.class));
	}

	@Test
	void purchaseStockErrorTest() {

		AppUser user = new AppUser();
		user.setUsername("testuser");

		Product product = new Product();
		product.setId(1);
		product.setName("マウス");
		product.setPrice(500);
		product.setStock(2);

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product));

		assertThrows(ResponseStatusException.class, () -> productService.purchase(1, 3, "testuser","千葉県テスト市1-2-3"));

		verify(saleRepository, never()).save(any(Sale.class));

		assertEquals(2, product.getStock());

	}

	@Test
	void purchaseAmountErrorTest() {

		AppUser user = new AppUser();
		user.setUsername("testuser");

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		assertThrows(ResponseStatusException.class, () -> productService.purchase(1, 0, "testuser","千葉県テスト市1-2-3"));

		verify(productRepository, never()).findByIdForUpdate(1);

		verify(saleRepository, never()).save(any(Sale.class));
	}

	@Test
	void purchaseProductNotFoundTest() {

		AppUser user = new AppUser();
		user.setUsername("testuder");

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		when(productRepository.findByIdForUpdate(999)).thenReturn(Optional.empty());

		assertThrows(ProductNotFoundException.class, () -> productService.purchase(999, 1, "testuser","千葉県テスト市1-2-3"));

		verify(saleRepository, never()).save(any(Sale.class));

	}

	@Test
	void PurchaseCart() {

		CartItemRequest item1 = new CartItemRequest();

		item1.setProductId(1);
		item1.setAmount(2);

		CartItemRequest item2 = new CartItemRequest();

		item2.setProductId(2);
		item2.setAmount(1);

		CartPurchaseRequest request = new CartPurchaseRequest();

		request.setItems(List.of(item1, item2));

		AppUser user = new AppUser();
		user.setUsername("testuser");

		Product product1 = new Product();
		product1.setId(1);
		product1.setName("マウス");
		product1.setPrice(500);
		product1.setStock(10);

		Product product2 = new Product();
		product2.setId(2);
		product2.setName("キーボード");
		product2.setPrice(1000);
		product2.setStock(5);

		ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product1));

		when(productRepository.findByIdForUpdate(2)).thenReturn(Optional.of(product2));
		when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

		List<SaleResponse> results = productService.purchaseCart(request, "testuser");

		assertEquals(2, results.size());
		assertEquals(8, product1.getStock());
		assertEquals(4, product2.getStock());

		verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
		verify(saleRepository, times(2)).save(saleCaptor.capture());

		List<Sale> savedSales = saleCaptor.getAllValues();

		assertEquals(savedSales.get(0).getOrder(), savedSales.get(1).getOrder());

		assertEquals(product1, savedSales.get(0).getProduct());
		assertEquals(2, savedSales.get(0).getAmount());
		assertEquals(1000, savedSales.get(0).getSales());

		assertEquals(product2, savedSales.get(1).getProduct());
		assertEquals(1, savedSales.get(1).getAmount());
		assertEquals(1000, savedSales.get(1).getSales());

	}

	@Test
	void purchaseCartStockErrorTest() {

		CartItemRequest item1 = new CartItemRequest();
		item1.setProductId(1);
		item1.setAmount(2);

		CartItemRequest item2 = new CartItemRequest();
		item2.setProductId(2);
		item2.setAmount(1);

		CartPurchaseRequest request = new CartPurchaseRequest();
		request.setItems(List.of(item1, item2));

		AppUser user = new AppUser();
		user.setUsername("testuser");

		Product product1 = new Product();
		product1.setId(1);
		product1.setName("マウス");
		product1.setPrice(500);
		product1.setStock(10);

		Product product2 = new Product();
		product2.setId(2);
		product2.setName("キーボード");
		product2.setPrice(1000);
		product2.setStock(0);

		when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
		when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product1));
		when(productRepository.findByIdForUpdate(2)).thenReturn(Optional.of(product2));
		when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

		assertThrows(ResponseStatusException.class, () -> productService.purchaseCart(request, "testuser"));

		verify(productRepository).findByIdForUpdate(1);
		verify(productRepository).findByIdForUpdate(2);
		verify(saleRepository, times(1)).save(any(Sale.class));

	}

}
