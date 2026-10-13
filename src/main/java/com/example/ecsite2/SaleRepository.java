package com.example.ecsite2;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale,Integer>{
	
	boolean existsByProduct_Id(Integer productId);
	
	List<Sale> findByUser_UsernameOrderBySoldAtDesc(String username);
	
	List<Sale> findByOrder_Id(Integer orderId);

}
