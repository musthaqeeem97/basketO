package com.example.basketo.shopadmin.inventory.model;

import java.io.Serializable;

import com.example.basketo.shopadmin.product.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InventoryPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Product product;
    private Warehouse warehouse;

    // Constructors, getters, setters, equals, and hashCode methods
    
    // Implement the required methods
}