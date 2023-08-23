package com.example.basketo.shopclient.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.basketo.shopadmin.order.model.OrderHistory;



@Controller
@RequestMapping("/order")
public class CustomerOrderController {

	@GetMapping("/confirmation")
	public String getOrderConfirmation(HttpSession session,Model model) {
		
		OrderHistory orderHistory =  (OrderHistory) session.getAttribute("orderHistory");
		System.out.println("order history:" + orderHistory);
		
		return "front-end/orderconfirmation";
	}
}
