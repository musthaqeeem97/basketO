package com.example.basketo.shopadmin.product.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Category;






@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
	Category findByName(String name);
 

    Page<Category> findByName(Pageable pageable, String keyword);
    
    Optional<Category> findByUuid(UUID uuid);

    @Query(value = "SELECT * FROM category WHERE uuid LIKE %:searchTerm% OR name LIKE %:searchTerm%", nativeQuery = true)
	Page<Category> findAll(@Param("searchTerm")String searchTerm, Pageable pageable);
}