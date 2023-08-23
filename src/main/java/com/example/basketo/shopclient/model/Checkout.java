package com.example.basketo.shopclient.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.order.enums.OrderType;
import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.user.model.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Checkout {
	
	@Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
	private UUID userID;
	

    @OneToMany(mappedBy = "orderHistory", cascade = CascadeType.ALL)
    @ToString.Exclude 
    @Builder.Default
    List<OrderItem> orderItems = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name="coupon_id")
    @ToString.Exclude
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name="address_id")
    @ToString.Exclude
    private Address address;
    
	private Double discountedAmount;
	
    private Double orderTotal;
    
    private OrderType paymentMethod;
    
}
 
 