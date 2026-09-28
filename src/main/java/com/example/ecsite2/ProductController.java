package com.example.ecsite2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public SaleResponse purchase(@PathVariable Integer id,@Valid@RequestBody PurchaseRequest request) {
		
		return productService.purchase(id,request.getAmount());
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
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		
		productService.delete(id);
		
		return ResponseEntity.noContent().build();	
		}
	

}
