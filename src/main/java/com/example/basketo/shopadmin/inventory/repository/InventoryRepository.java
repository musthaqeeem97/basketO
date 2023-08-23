package com.example.basketo.shopadmin.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.inventory.model.Inventory;
import com.example.basketo.shopadmin.inventory.model.InventoryPK;
import com.example.basketo.shopadmin.product.model.Product;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, InventoryPK> {

	
	List<Inventory> findByProduct(Product product);
	
	 @Query(value = "SELECT * FROM inventory WHERE product_id LIKE %:searchTerm% OR warehouse_code LIKE %:searchTerm% "
	 		+ "OR modified_at LIKE %:searchTerm% available_quantity LIKE %:searchTerm%", nativeQuery = true)
		Page<Inventory> findAll(@Param("searchTerm")String searchTerm, Pageable pageable);

}
