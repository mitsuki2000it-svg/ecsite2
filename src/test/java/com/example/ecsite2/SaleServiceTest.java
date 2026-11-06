package com.example.ecsite2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {
	
	@Mock
	private SaleRepository saleRepository;
	
	@Mock
	private CustomerOrderRepository customerOrderRepository;
	
	@InjectMocks
	private SaleService saleService;
	
	@Test
	void getTotalSalesTest() {
		
		CustomerOrder order1 = new CustomerOrder();
		order1.setStatus(OrderStatus.ORDERED);
		order1.setPaymentStatus(PaymentStatus.PAID);
		
		Sale sale1 = new Sale();
		sale1.setSales(1000);
	    sale1.setOrder(order1);
	    
	    CustomerOrder order2 = new CustomerOrder();
	    order2.setStatus(OrderStatus.ORDERED);
	    order2.setPaymentStatus(PaymentStatus.UNPAID);
	    
	    Sale sale2 = new Sale();
	    sale2.setSales(2000);
	    sale2.setOrder(order2);
	    
	    CustomerOrder order3 = new CustomerOrder();
	    order3.setStatus(OrderStatus.CANCELLED);
	    order3.setPaymentStatus(PaymentStatus.REFUNDED);
	    
	    Sale sale3 = new Sale();
	    sale3.setSales(3000);
	    sale3.setOrder(order3);
	    
	    when(saleRepository.findAll()).thenReturn(List.of(sale1, sale2, sale3));
	    
	    assertEquals(1000, saleService.getTotalSales());
	}


}
