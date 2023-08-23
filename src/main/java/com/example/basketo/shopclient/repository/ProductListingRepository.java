package com.example.basketo.shopclient.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Product;

@Repository
public interface ProductListingRepository extends JpaRepository<Product, UUID>{

	@Query(value = "SELECT p.* FROM product p INNER JOIN category c ON p.category_id = c.uuid WHERE c.name LIKE %:searchTerm% OR p.name LIKE %:searchTerm%", nativeQuery = true)
	Page<Product> findAllBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

//	 @Query(value = "SELECT p.* FROM product p INNER JOIN category c ON p.category_id = c.category_id WHERE c.uuid = :categoryId", nativeQuery = true)
//	    Page<Product> findByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    //
    
    @Query("SELECT p FROM Product p WHERE p.category.name LIKE %:categoryName% AND p.name LIKE %:searchTerm% AND p.enabled = true")
    Page<Product> findByCategoryNameAndSearchTermAndEnabled(@Param("categoryName") String categoryName, @Param("searchTerm") String searchTerm, Pageable pageable);

    
}
