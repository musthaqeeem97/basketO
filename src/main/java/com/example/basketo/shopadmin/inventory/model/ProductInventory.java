package com.example.basketo.shopadmin.inventory.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.example.basketo.shopadmin.admin.model.BaseEntity;
import com.example.basketo.shopadmin.product.model.Product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductInventory extends BaseEntity {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
	    @GenericGenerator(name = "uuid2", strategy = "uuid2")
	    @Type(type = "org.hibernate.type.UUIDCharType")
	    private UUID uuid;


	@OneToOne
	@JoinColumn(name="product_id")
	private Product product;
	
    private int availableQuantity;
    
	public ProductInventory(Product product,int availableQuantity ) {
		this.product = product;
		this.availableQuantity=availableQuantity;
	}
	
   


}
