package com.example.basketo.shopadmin.coupon.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidityResponseDto {
    boolean valid = false;
    double priceOff = 0;
    double cartTotal = 0;
    double cartSaved = 0;
    String coupon="";

}
