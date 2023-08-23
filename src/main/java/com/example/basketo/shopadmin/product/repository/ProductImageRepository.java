package com.example.basketo.shopadmin.product.repository;


import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.ProductImage;



@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    @Transactional
    void deleteAllByProduct(Product product);
  		
//    @Query(value = "SELECT * FROM category_image WHERE category_id = :categoryId", nativeQuery = true)
//	List<CategoryImage> findAllByCategoryId(@Param("categoryId") UUID categoryId);


}