package com.example.basketo.shopadmin.product.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopadmin.product.model.Review;
import com.example.basketo.shopadmin.user.model.UserInfo;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

	@Query(value = "SELECT * FROM Review WHERE user_id = :userId and product_id = :productId", nativeQuery = true)
	Review findByUserInfoAndProduct(@Param("userId") UserInfo userId, @Param("productId") Product productId);

	List<Review> findByProduct(Product product);

}
