package com.example.basketo.shopadmin.cart.model;

import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.user.model.UserInfo;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CartPK.class)
public class Cart extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @Id
    private UserInfo userInfo;

    @Id
    private Date addedToCart;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
