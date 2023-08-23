package com.example.basketo.shopclient.model;



import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.user.model.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistItem  extends BaseEntity  {
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
    
 
}
