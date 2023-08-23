package com.example.basketo.shopadmin.inventory.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import com.example.basketo.shopadmin.product.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(InventoryPK.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Inventory {
	
	
	@ManyToOne
	@JoinColumn(name="product_id")
	@Id
	private Product product;
	
	@ManyToOne
	@JoinColumn(name="warehouse_code")
	@Id
	Warehouse warehouse;
	
    private int availableQuantity;
    
    public Inventory(Product product,Warehouse warehouse,int availableQuantity) {
    	this.product = product;
    	this.warehouse = warehouse;
    	this.availableQuantity = availableQuantity;
    }
   
}

