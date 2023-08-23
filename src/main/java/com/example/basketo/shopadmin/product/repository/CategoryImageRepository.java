package com.example.basketo.shopadmin.product.repository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.basketo.shopadmin.product.model.Category;
import com.example.basketo.shopadmin.product.model.CategoryImage;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, UUID> {

	
	@Transactional
	void deleteAllByCategory(Category category);
	
	@Query(value = "SELECT * FROM category_image WHERE category_id = :categoryId", nativeQuery = true)
	List<CategoryImage> findAllByCategoryId(@Param("categoryId") UUID categoryId);


	
}
