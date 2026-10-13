package com.example.ecsite2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import exception.ProductNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final SaleRepository saleRepository;
	private final AppUserRepository appUserRepository;
	private final CustomerOrderRepository customerOrderRepository;

	public ProductService(ProductRepository productRepository, SaleRepository saleRepository,
			AppUserRepository appUserRepository, CustomerOrderRepository customerOrderRepository) {
		this.productRepository = productRepository;
		this.saleRepository = saleRepository;
		this.appUserRepository = appUserRepository;
		this.customerOrderRepository = customerOrderRepository;
	}

	public Page<ProductResponse> getProductPage(int page, int size, String sortOrder, Boolean inStockOnly, int minPrice,
			int maxPrice) {

		validatePageRequest(page, size, sortOrder, minPrice, maxPrice);

		Pageable pageable = createPageable(page, size, sortOrder);

		Page<Product> products = findProducts(minPrice, maxPrice, inStockOnly, pageable);

		return products.map(ProductResponse::from);
	}

	public Page<ProductResponse> searchProducts(String keyword, int page, int size, String sortOrder,
			boolean inStockOnly, int minPrice, int maxPrice) {

		validatePageRequest(page, size, sortOrder, minPrice, maxPrice);

		Pageable pageable = createPageable(page, size, sortOrder);

		Page<Product> products = findSearchProducts(keyword, minPrice, maxPrice, inStockOnly, pageable);

		return products.map(ProductResponse::from);
	}

	private Page<Product> findProducts(int minPrice, int maxPrice, boolean inStockOnly, Pageable pageable) {

		if (minPrice > 0 && maxPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByStockGreaterThanAndPriceBetween(0, minPrice, maxPrice, pageable);
			}

			return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
		}
		if (minPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByStockGreaterThanAndPriceGreaterThanEqual(0, minPrice, pageable);
			}

			return productRepository.findByPriceGreaterThanEqual(minPrice, pageable);
		}
		if (maxPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByStockGreaterThanAndPriceLessThanEqual(0, maxPrice, pageable);
			}

			return productRepository.findByPriceLessThanEqual(maxPrice, pageable);
		}
		if (inStockOnly) {
			return productRepository.findByStockGreaterThan(0, pageable);
		}

		return productRepository.findAll(pageable);
	}

	private Page<Product> findSearchProducts(String keyword, int minPrice, int maxPrice, boolean inStockOnly,
			Pageable pageable) {

		if (minPrice > 0 && maxPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceBetween(keyword, 0,
						minPrice, maxPrice, pageable);
			}

			return productRepository.findByNameContainingIgnoreCaseAndPriceBetween(keyword, minPrice, maxPrice,
					pageable);
		}

		if (minPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceGreaterThanEqual(
						keyword, 0, minPrice, pageable);
			}

			return productRepository.findByNameContainingIgnoreCaseAndPriceGreaterThanEqual(keyword, minPrice,
					pageable);
		}

		if (maxPrice > 0) {

			if (inStockOnly) {
				return productRepository.findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceLessThanEqual(keyword,
						0, maxPrice, pageable);
			}

			return productRepository.findByNameContainingIgnoreCaseAndPriceLessThanEqual(keyword, maxPrice, pageable);
		}

		if (inStockOnly) {
			return productRepository.findByNameContainingIgnoreCaseAndStockGreaterThan(keyword, 0, pageable);
		}

		return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}

	private Pageable createPageable(int page, int size, String sortOrder) {

		Sort sort = createSort(sortOrder);

		return PageRequest.of(page, size, sort);
	}

	private Sort createSort(String sortOrder) {

		if (sortOrder.equals("price-asc")) {
			return Sort.by("price").ascending();
		}

		if (sortOrder.equals("price-desc")) {
			return Sort.by("price").descending();
		}

		return Sort.unsorted();
	}

	private void validatePageRequest(int page, int size, String sortOrder, int minPrice, int maxPrice) {

		if (!sortOrder.equals("") && !sortOrder.equals("price-asc") && !sortOrder.equals("price-desc")) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortOrderが不正です");
		}

		if (minPrice < 0 || maxPrice < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "価格は0以上にしてください");
		}

		if (page < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pageは0以上にしてください");
		}

		if (size <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sizeは1以上にしてください");
		}

		if (minPrice > 0 && maxPrice > 0 && minPrice > maxPrice) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "最低価格は最高価格以下にしてください");
		}
	}

	public ProductResponse create(ProductCreateRequest request) {

		Product product = new Product();
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());

		Product saveProduct = productRepository.save(product);

		return ProductResponse.from(saveProduct);

	}

	public ProductResponse getProduct(Integer id) {

		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));

		return ProductResponse.from(product);
	}

	public List<ProductResponse> getAll() {

		List<Product> products = productRepository.findAll();

		return products.stream().map(ProductResponse::from).toList();
	}

	public ProductResponse update(Integer id, ProductUpdateRequest request) {

		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));

		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());

		Product savedProduct = productRepository.save(product);

		return ProductResponse.from(savedProduct);
	}

	public void delete(Integer id) {

		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));

		if (saleRepository.existsByProduct_Id(id)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "販売履歴があるため商品を削除できません");
		}

		productRepository.delete(product);

	}

	@Transactional
	public SaleResponse purchase(Integer productId, Integer amount, String username) {

		AppUser user = appUserRepository.findByUsername(username).orElseThrow();

		CustomerOrder order = new CustomerOrder();

		order.setUser(user);
		order.setOrderedAt(LocalDateTime.now());
		order.setStatus(OrderStatus.ORDERED);

		customerOrderRepository.save(order);

		return purchase(productId, amount, user, order);
	}

	@Transactional
	public List<SaleResponse> purchaseCart(CartPurchaseRequest request, String username) {

		List<SaleResponse> results = new ArrayList<>();

		AppUser user = appUserRepository.findByUsername(username).orElseThrow();

		CustomerOrder order = new CustomerOrder();

		order.setUser(user);
		order.setOrderedAt(LocalDateTime.now());
		order.setStatus(OrderStatus.ORDERED);

		customerOrderRepository.save(order);

		for (CartItemRequest item : request.getItems()) {

			SaleResponse sale = purchase(item.getProductId(), item.getAmount(), user, order);

			results.add(sale);
		}

		return results;
	}

	private SaleResponse purchase(Integer id, int amount, AppUser user, CustomerOrder order) {

		if (amount < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "購入数は1以上にしてください");
		}

		Product product = productRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));

		if (product.getStock() < amount) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "在庫が不足しています");

		}

		product.setStock(product.getStock() - amount);

		Sale sale = new Sale();
		sale.setProduct(product);
		sale.setAmount(amount);
		sale.setPrice(product.getPrice());
		sale.setProductName(product.getName());
		sale.setSales(product.getPrice() * amount);
		sale.setSoldAt(LocalDateTime.now());
		sale.setUser(user);
		sale.setOrder(order);

		Sale savedSale = saleRepository.save(sale);

		return SaleResponse.from(savedSale);

	}

}
