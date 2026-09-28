package com.example.ecsite2;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import exception.ProductNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final SaleRepository saleRepository;

	public ProductService(ProductRepository productRepository,SaleRepository saleRepository) {
		this.productRepository = productRepository;
		this.saleRepository = saleRepository;
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

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("商品が漬かりません見つかりません"));

		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());

		Product savedProduct = productRepository.save(product);

		return ProductResponse.from(savedProduct);
	}

	public void delete(Integer id) {

		Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));
		
		if(saleRepository.existsByProduct_Id(id)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"販売履歴があるため商品を削除できません");
		}

		productRepository.delete(product);

	}
	@Transactional
	public SaleResponse purchase(Integer productId,Integer amount) {
		
		Product product = productRepository.findByIdForUpdate(productId).orElseThrow(() -> new ProductNotFoundException("商品が見つかりません"));
		
		if(product.getStock() < amount) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"在庫が不足しています");
			
		}
		
		product.setStock(product.getStock() - amount);
		
		Sale sale = new Sale();
		sale.setProduct(product);
		sale.setAmount(amount);
		sale.setPrice(product.getprice());
		sale.setProductName(product.getName());
		sale.setSales(product.getprice() * amount);
		sale.setSoldAt(LocalDateTime.now());
		
		Sale savedSale = saleRepository.save(sale);
		
		return SaleResponse.from(savedSale);
	}
	

}
