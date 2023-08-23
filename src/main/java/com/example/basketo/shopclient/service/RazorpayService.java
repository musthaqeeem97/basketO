package com.example.basketo.shopclient.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class RazorpayService {

    @Value("${razorpay.keyId}")
    private String keyId;

    @Value("${razorpay.keySecret}")
    private String keySecret;

    public String createOrder(double total, String currency) throws RazorpayException {
        try {
        	System.out.println("create razor pay order service");
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", total*10);
            orderRequest.put("currency", currency);
            System.out.println("create razor pay order service2");
            Order order = razorpayClient.orders.create(orderRequest);
            System.out.println("create razor pay order service3");
            return order.get("id");
        } catch (Exception e) {
            throw new RazorpayException("Failed to create order: " + e.getMessage());
        }
    }
}