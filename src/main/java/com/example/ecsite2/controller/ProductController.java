package com.example.ecsite2.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecsite2.ProductCreateRequest;
import com.example.ecsite2.ProductResponse;
import com.example.ecsite2.ProductService;
import com.example.ecsite2.ProductUpdateRequest;
import com.example.ecsite2.PurchaseRequest;
import com.example.ecsite2.SaleResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
		ProductResponse response = productService.create(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	@PostMapping("/{id}/purchase")
	public SaleResponse purchase(@PathVariable Integer id, @Valid @RequestBody PurchaseRequest request,
			Authentication authentication) {

		String username = authentication.getName();

		return productService.purchase(id, request.getAmount(), username);
	}

	@GetMapping("/page")
	public Page<ProductResponse> getProductPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "") String sortOrder,
			@RequestParam(defaultValue = "false") boolean inStockOnly, @RequestParam(defaultValue = "0") int minPrice,
			@RequestParam(defaultValue = "0") int maxPrice) {

		return productService.getProductPage(page, size, sortOrder, inStockOnly, minPrice, maxPrice);
	}

	@GetMapping("/search")
	public Page<ProductResponse> searchProduct(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "") String sortOrder,
			@RequestParam(defaultValue = "false") boolean inStockOnly, @RequestParam(defaultValue = "0") int minPrice,
			@RequestParam(defaultValue = "0") int maxPrice) {

		return productService.searchProducts(keyword, page, size, sortOrder, inStockOnly, minPrice, maxPrice);
	}

	@GetMapping
	public List<ProductResponse> getAll() {
		return productService.getAll();
	}

	@GetMapping("/{id}")
	public ProductResponse getProduct(@PathVariable Integer id) {
		return productService.getProduct(id);
	}

	@PutMapping("/{id}")
	public ProductResponse update(@PathVariable Integer id, @Valid @RequestBody ProductUpdateRequest request) {

		return productService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {

		productService.delete(id);

		return ResponseEntity.noContent().build();
	}

}
