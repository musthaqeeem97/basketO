package com.example.basketo.shopclient.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.product.model.Product;
import com.example.basketo.shopclient.model.WishlistItem;
import com.example.basketo.shopadmin.user.model.UserInfo;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findByUserInfo(UserInfo customer);

	@Query(value = "SELECT * FROM wishlist_item WHERE customer_id = :user AND product_id = :product", nativeQuery = true)
	WishlistItem findByUserInfoAndProduct(UserInfo user, Product product);

	@Query(value = "SELECT * FROM wishlist_item WHERE customer_id = :user", nativeQuery = true)
	Page<WishlistItem> findAllByUserInfo(UserInfo user, Pageable pageable);
}
