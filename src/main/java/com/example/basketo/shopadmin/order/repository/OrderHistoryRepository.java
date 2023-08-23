package com.example.basketo.shopadmin.order.repository;

import com.example.basketo.shopadmin.order.model.OrderHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {

	List<OrderHistory> findByCreatedAtBetween(Date startDate, Date endDate);
	
	@Query(value = "SELECT * FROM order_history ORDER BY created_at DESC LIMIT 5", nativeQuery = true)
    List<OrderHistory> getLastFiveOrders();

	@Query(value = "SELECT * FROM order_history ORDER BY created_at DESC", nativeQuery = true)
	Page<OrderHistory> findAll(Pageable pageable);

}