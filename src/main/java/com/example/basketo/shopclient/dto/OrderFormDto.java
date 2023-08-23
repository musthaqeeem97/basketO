package com.example.basketo.shopclient.dto;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderFormDto {

	  	private int totalItems;
	    private double totalPrice;
	    private UUID selectedAddress;
	    private String paymentMethod;
	    private String couponCode;
	    private String discountedAmount;
}
