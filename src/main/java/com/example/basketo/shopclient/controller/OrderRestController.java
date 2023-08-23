package com.example.basketo.shopclient.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderRestController {
	
//	private final OrderService orderService;
//
//	 @GetMapping("/requestcancel/{id}")
//	    public String requestOrderCancellation(@PathVariable UUID id, Model model){
//	      
//	        OrderItem order = orderService.findById(id);
//	        order.setRequestCancel(true);
//	        orderService.save(order);
//	        model.addAttribute("order", order);
//	        return "success";
//	    }
//	 @GetMapping("/requestcancel/{orderId}")
//	    public ResponseEntity<String> requestCancelOrder(@PathVariable UUID orderId) {
//	        try {
//	        	 OrderItem order = orderService.findById(orderId);
//	 	        order.setRequestCancel(true);
//	 	        orderService.save(order);
//	 	       
//	 	       return ResponseEntity.ok("success");
//	        } catch (Exception e) {
//	            // Handle the exception appropriately (e.g., log the error)
//	        	 return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error occurred during Razorpay integration");
//	             
//	        }
//	    }
}

