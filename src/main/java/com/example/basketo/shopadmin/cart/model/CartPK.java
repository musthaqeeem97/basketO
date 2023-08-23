package com.example.basketo.shopadmin.cart.model;

import com.example.basketo.shopadmin.user.model.UserInfo;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CartPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserInfo userInfo;
    private Date addedToCart;



}