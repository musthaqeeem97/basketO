package com.example.basketo.shopadmin.order.model;

import com.example.basketo.shopadmin.coupon.model.Coupon;
import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.order.enums.OrderType;
import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.user.model.Address;
import com.example.basketo.shopadmin.user.model.UserInfo;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderHistory extends BaseEntity { //Order is a reserved keyword
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @Builder.Default
    private double total=0F;

    @Builder.Default
    private Float tax=0F;


    @Builder.Default
    private double totalDiscount=0F;


    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.UNKNOWN;

    @Builder.Default
    private OrderType orderType=OrderType.UNKNOWN;

    //relationship

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserInfo userInfo;

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

//    @OneToOne(mappedBy = "orderHistory")
//    @ToString.Exclude
//    private OnlineOrderRef onlineOrderRef;



  
    


}
