package com.example.basketo.shopadmin.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponseDto {
    private boolean valid;
    private boolean productSpecific;
    private boolean categorySpecific;
    private int discountPercentage;
    private boolean applicable;
}