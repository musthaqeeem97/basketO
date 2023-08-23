package com.example.basketo.shopadmin.order.repository;

import com.example.basketo.shopadmin.order.model.OrderItem;
import com.example.basketo.shopadmin.user.model.UserInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderItem, UUID> {

	@Query(value = "SELECT * FROM order_item WHERE created_at LIKE %:searchTerm%", nativeQuery = true)
	Page<OrderItem> findAll(String searchTerm, Pageable pageable);

	@Query(value = "SELECT * FROM order_item WHERE customer_id=:userInfo", nativeQuery = true)
	Page<OrderItem>  findByCustomer(UserInfo userInfo,Pageable pageable);
	
//	@Query(value = "SELECT * FROM order_item WHERE customer_id = :customerId", nativeQuery = true)
 //   Page<OrderItem> findAllByCustomer(@Param("customerId") UUID customerId, String searchTerm, Pageable pageable);

	 List<OrderItem> findByCreatedAtBetween(Date startDate, Date endDate);
	
	
}
