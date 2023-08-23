package com.example.basketo.shopadmin.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopadmin.order.model.OnlineOrderRef;

@Repository
public interface OnlineOrderRefRepository extends JpaRepository<OnlineOrderRef, UUID> {

	OnlineOrderRef findByRazorPayOrderId(String orderId);

}
