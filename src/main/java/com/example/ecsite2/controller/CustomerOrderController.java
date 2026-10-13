package com.example.ecsite2.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecsite2.CustomerOrderService;

@RestController
@RequestMapping("/orders")
public class CustomerOrderController {

	private final CustomerOrderService customerOrderService;

	public CustomerOrderController(CustomerOrderService customerOrderService) {

		this.customerOrderService = customerOrderService;
	}

	@PutMapping("/{orderId}/ship")
	public String ship(@PathVariable Integer orderId) {

		customerOrderService.ship(orderId);

		return "発送済みに変更しました";

	}

	@PutMapping("/{orderId}/cancel")
	public String cancel(@PathVariable Integer orderId) {

		customerOrderService.cancel(orderId);

		return "注文をキャンセルしました";
	}

}
