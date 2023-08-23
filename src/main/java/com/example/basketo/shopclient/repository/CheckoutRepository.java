package com.example.basketo.shopclient.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.basketo.shopclient.model.Checkout;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, UUID>{

	
}
