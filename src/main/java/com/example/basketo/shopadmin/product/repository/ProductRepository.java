package com.example.basketo.shopadmin.product.repository;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>{
	
	Product findByName(String name);


    boolean existsByName(String productName);


    Page<Product> findByName(Pageable pageable, String keyword);

    Product findByNameLike(String s);
    
    
    Page<Product> findByNameLike(String keyword, Pageable pageable);
	
    @Query(value = "SELECT * FROM product WHERE uuid LIKE %:searchTerm% OR name LIKE %:searchTerm% OR price LIKE %:searchTerm% OR category_id LIKE %:searchTerm%", nativeQuery = true)
	Page<Product> findAll(@Param("searchTerm")String searchTerm, Pageable pageable);


}
