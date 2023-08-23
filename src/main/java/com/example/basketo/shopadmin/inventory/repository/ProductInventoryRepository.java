package com.example.basketo.shopadmin.inventory.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.example.basketo.shopadmin.inventory.model.ProductInventory;
import com.example.basketo.shopadmin.product.model.Product;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, UUID> {

	ProductInventory findByProduct(Product product);
	
	 @Query(value = "SELECT * FROM product_inventory WHERE product_id LIKE %:searchTerm%"
	 		+ " OR available_quantity LIKE %:searchTerm%", nativeQuery = true)
		Page<ProductInventory> findAll(@Param("searchTerm")String searchTerm, Pageable pageable);

}
