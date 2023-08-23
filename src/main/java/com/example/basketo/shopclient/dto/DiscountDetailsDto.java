package com.example.basketo.shopclient.dto;

import com.example.basketo.shopadmin.coupon.enums.CouponType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DiscountDetailsDto {

	private String couponCode;
	private CouponType type;
	private String discountedProduct;
	private String discountedCategory;
	private double discountedAmount;
	private float discountPercentage;
	
}
