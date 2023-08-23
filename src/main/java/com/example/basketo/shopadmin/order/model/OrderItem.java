package com.example.basketo.shopadmin.order.model;



import com.example.basketo.shopadmin.order.enums.OrderStatus;
import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.user.model.UserInfo;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderHistory orderHistory;

//    @Builder.Default
//    private Float discountendAmount=0F;

    private int quantity;
    
    private double amount;
    
    private OrderStatus status;
    
    private boolean requestCancel;
    private boolean requestReturn;
    
    
}

