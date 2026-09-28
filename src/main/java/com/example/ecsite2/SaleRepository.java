package com.example.ecsite2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale,Integer>{
	
	boolean existsByProduct_Id(Integer productId);

}
