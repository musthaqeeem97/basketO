package com.example.basketo.shopadmin.order.service;

import com.example.basketo.shopadmin.order.model.OnlineOrderRef;
import com.example.basketo.shopadmin.order.repository.OnlineOrderRefRepository;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnlineOrderRefService {

	private final OnlineOrderRefRepository onlineOrderRefRepository;
	
	public OnlineOrderRef save(OnlineOrderRef onlineOrderRef) {
        return onlineOrderRefRepository.save(onlineOrderRef);
    }

  
    public OnlineOrderRef findByRazorpayOrderId(String orderId) {
        return onlineOrderRefRepository.findByRazorPayOrderId(orderId);
    }
}
