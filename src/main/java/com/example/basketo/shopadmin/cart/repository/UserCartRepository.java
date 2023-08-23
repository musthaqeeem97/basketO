package com.example.basketo.shopadmin.cart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.basketo.shopadmin.cart.model.CartItem;
import com.example.basketo.shopadmin.user.model.UserInfo;

public interface UserCartRepository extends JpaRepository<CartItem, UUID> {
	
	 List<CartItem> findByUserInfo(UserInfo customer);

	void deleteByProduct(CartItem cartItem);
	
	@Query(value = "SELECT * FROM cart_item WHERE customer_id = :#{#cartItem.userInfo} AND product_id = :#{#cartItem.product}", nativeQuery = true)
	CartItem findByUserInfoAndProductId(@Param("cartItem") CartItem cartItem);

	
}
