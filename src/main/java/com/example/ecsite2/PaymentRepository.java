package com.example.ecsite2;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	
	Optional<Payment> findByOrder_Id(Integer orderId);

}
