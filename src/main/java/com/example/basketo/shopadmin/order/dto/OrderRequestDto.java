package com.example.basketo.shopadmin.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRequestDto {
    private Float amount;
    private String currency;
    private String receipt;
    private UUID address;
    private String coupon;


}
