package com.example.ecsite2;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product,Integer>{
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Product p WHERE p.id = :id")
	Optional<Product> findByIdForUpdate(@Param("id") Integer id);
	
	Page<Product> findByNameContainingIgnoreCase(String name,Pageable Pageable);
	
	Page<Product> findByStockGreaterThan(int stock,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndStockGreaterThan(String name,int stock,Pageable pageable);
	
	Page<Product> findByPriceBetween(int minPrice,int maxPrice,Pageable pageable);
	
	Page<Product> findByPriceGreaterThanEqual(int minPrice,Pageable pageable);
	
	Page<Product> findByPriceLessThanEqual(int maxPrice,Pageable pageable);
	
	Page<Product> findByStockGreaterThanAndPriceBetween(int stock,int minPrice,int maxPrice,Pageable pageable);
	
	Page<Product> findByStockGreaterThanAndPriceGreaterThanEqual(int Stock,int minPrice,Pageable pageable);
	
	Page<Product> findByStockGreaterThanAndPriceLessThanEqual(int stock,int maxPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndPriceBetween(String name,int minPrice,int maxPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndPriceGreaterThanEqual(String name,int maxPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndPriceLessThanEqual(String name,int maxPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceBetween(String name,int stock,int minPrice,int maxPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceGreaterThanEqual(String name,int stock,int minPrice,Pageable pageable);
	
	Page<Product> findByNameContainingIgnoreCaseAndStockGreaterThanAndPriceLessThanEqual(String name,int stock,int maxPrice,Pageable pageable);
}


